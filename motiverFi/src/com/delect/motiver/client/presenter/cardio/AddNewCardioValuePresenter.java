/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.cardio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioValueCreatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.Functions;

/**
 * Shows window where user can add new cardio value
 * @author Antti
 *
 */
public class AddNewCardioValuePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class AddNewCardioValueDisplay extends Display {

		public abstract void setDate(Date date);
		public abstract void setHandler(AddNewCardioValueHandler addNewCardioValueHandler);
		public abstract void setModels(List<CardioModel> models, boolean b);
	}

	public interface AddNewCardioValueHandler {
		void cancel();
		void newValue(CardioModel cardio, CardioValueModel value);
	}
	private CardioModel cardio;

	private Date date;
	private AddNewCardioValueDisplay display;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param cardio : if null -> shows all cardios
	 */
	public AddNewCardioValuePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, AddNewCardioValueDisplay display, CardioModel cardio, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.cardio = cardio;
    this.date = date;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		//if single model
		if(cardio != null) {
			List<CardioModel> models = new ArrayList<CardioModel>();
			models.add(cardio);
			display.setModels(models, false);
		}
		display.setDate(date);
		
		display.setHandler(new AddNewCardioValueHandler() {

			@Override
			public void cancel() {
				
				//remove add-string from token
				String token = History.getToken();
				if(token.length() > 4) {
					if(token.substring(token.length() - 4, token.length()).equals("/add")) {
						token = token.substring(0, token.length() - 4);
						History.newItem(token, false);
					}
				}
				
				stop();
			}
			@Override
			public void newValue(CardioModel cardio, CardioValueModel value) {
				
				display.setContentEnabled(false);
				
				// TODO Create new guide value and fire add event
				final Request req = rpcService.addCardioValue(cardio, value, new MyAsyncCallback<CardioValueModel>() {
					@Override
					public void onSuccess(CardioValueModel m) {
						try {
							display.setContentEnabled(true);

							//remove add-string from token
							String token = History.getToken();
							if(token.length() > 4) {
								if(token.substring(token.length() - 4, token.length()).equals("/add")) {
									token = token.substring(0, token.length() - 4);
									History.newItem(token, false);
								}
							}
							
							stop();

							//set date
							m.setDate(Functions.getDateGmt(m.getDate()));
							
							eventBus.fireEvent(new CardioValueCreatedEvent(m));
							
						} catch (Exception e) {
				      Motiver.showException(e);
						}
					}
				});
				addRequest(req);
			}
		});
		
		//add "/add"+string to history
		History.newItem(History.getToken() + "/add", false);
	}

	@Override
	public void onRun() {
		
		//if no cardio set -> fetch all
		if(cardio == null) {
      loadCardios();
    }
	}

	/**
	 * Loads values
	 */
	void loadCardios() {

    //get cardios
		final Request req = rpcService.getCardios(0, new MyAsyncCallback<List<CardioModel>>() {
			@Override
			public void onSuccess(List<CardioModel> result) {
				display.setModels(result, true);
      }
		});
		addRequest(req);
	}

}
