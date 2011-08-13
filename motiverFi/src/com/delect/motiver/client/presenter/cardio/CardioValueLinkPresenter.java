/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.cardio;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioShowEvent;
import com.delect.motiver.client.event.CardioValueRemovedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.CardioPresenter.CardioDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.cardio.CardioView;
import com.delect.motiver.shared.CardioValueModel;

/**
 * Shows single cardio value name as "link"
 * @author Antti
 *
 */
public class CardioValueLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CardioValueLinkDisplay extends Display {
		public abstract void setHandler(CardioValueLinkHandler cardioValueLinkHandler);
		public abstract void setModel(CardioValueModel cardioValue);
	}

	public interface CardioValueLinkHandler {
		void selected();
		void valueRemoved();
	}
	public CardioValueModel cardioValue;

	//child presenters
	private CardioPresenter cardioPresenter;
	
	CardioValueLinkDisplay display;

	public CardioValueLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CardioValueLinkDisplay display, CardioValueModel cardioValue) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.cardioValue = cardioValue;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new CardioValueLinkHandler() {
			@Override
			public void selected() {
				fireEvent(new CardioShowEvent(cardioValue.getName()));
			}

			@Override
			public void valueRemoved() {
				display.setContentEnabled(false);
				
				//remove value and stop itself
				List<CardioValueModel> list = new ArrayList<CardioValueModel>();
				list.add(cardioValue);
				final Request req = rpcService.removeCardioValues(cardioValue.getName(), list, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						fireEvent(new CardioValueRemovedEvent(cardioValue));
					}
				});
				addRequest(req);
			}
		});

		display.setModel(cardioValue);
	}

	@Override
	public void onStop() {
		if(cardioPresenter != null) {
      cardioPresenter.stop();
    }
	}

	/**
	 * Shows cardio's presenter where this value belongs
	 */
	public void showCardio() {
		
		//if visible
		if(cardioPresenter != null) {
			cardioPresenter.stop();
			cardioPresenter = null;
		}
		else {
			cardioPresenter = new CardioPresenter(rpcService, eventBus, (CardioDisplay)GWT.create(CardioView.class), cardioValue.getName(), false, true);
			cardioPresenter.run(display.getBaseContainer());
		}
	}

}
