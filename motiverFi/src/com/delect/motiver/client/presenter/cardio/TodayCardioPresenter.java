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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioValueCreatedEvent;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.RunValueCreatedEvent;
import com.delect.motiver.client.event.handler.CardioValueCreatedEventHandler;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.RunValueCreatedEventHandler;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.CardioValueLinkPresenter.CardioValueLinkDisplay;
import com.delect.motiver.client.presenter.cardio.RunValueLinkPresenter.RunValueLinkDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.cardio.CardioValueLinkView;
import com.delect.motiver.client.view.cardio.RunValueLinkView;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.RunValueModel;

/**
 * <pre>
 * Shows single days cardios
 *  </pre>
 */
public class TodayCardioPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class TodayCardioDisplay extends Display {
	}
	//child presenters
	private List<Presenter> cardioPresenters = new ArrayList<Presenter>();

	private Date date;
	private String uid;
	protected AddNewCardioValuePresenter addNewCardioValuePresenter;
	
	protected AddNewRunValuePresenter addNewRunValuePresenter;
	TodayCardioDisplay display;
	
	public TodayCardioPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, TodayCardioDisplay display, String uid, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.uid = uid;
    this.date = date;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		//EVENT: reload view when date changes
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				date = event.getDate();
        loadCardios();
			}
		});
		//EVENT: cardio value added
		addEventHandler(CardioValueCreatedEvent.TYPE, new CardioValueCreatedEventHandler() {
			@Override
			public void onCardioValueCreated(CardioValueCreatedEvent event) {
				//if this date
				if(event.getCardioValue() != null && event.getCardioValue().getDate() != null) {
					if(Functions.Fmt.format(event.getCardioValue().getDate()).equals( Functions.Fmt.format(date) )) {
						final CardioValueLinkPresenter wp = new CardioValueLinkPresenter(rpcService, eventBus, (CardioValueLinkDisplay)GWT.create(CardioValueLinkView.class), event.getCardioValue());
						addNewPresenter(wp);
					}
				}
			}
		});
		//EVENT: run value added
		addEventHandler(RunValueCreatedEvent.TYPE, new RunValueCreatedEventHandler() {
			@Override
			public void onRunValueCreated(RunValueCreatedEvent event) {
				//if this date
				if(event.getRunValue() != null && event.getRunValue().getDate() != null) {
					if(Functions.Fmt.format(event.getRunValue().getDate()).equals( Functions.Fmt.format(date) )) {
						final RunValueLinkPresenter wp = new RunValueLinkPresenter(rpcService, eventBus, (RunValueLinkDisplay)GWT.create(RunValueLinkView.class), event.getRunValue());
						addNewPresenter(wp);
					}
				}
			}
		});
		
	}


	@Override
	public void onRefresh() {
		if(cardioPresenters != null) {
			for(int i=0; i < cardioPresenters.size(); i++) {
				final Presenter presenter = cardioPresenters.get(i);
				if(presenter != null) {
					presenter.run(display.getBaseContainer());
				}
			}
		}
	}
	

	@Override
	public void onRun() {
	    
    loadCardios();
  }

	@Override
	public void onStop() {

		if(addNewCardioValuePresenter != null) {
      addNewCardioValuePresenter.stop();
    }
		if(addNewRunValuePresenter != null) {
      addNewRunValuePresenter.stop();
    }

		//unbind presenters
		unbindPresenters();
	}

	/**
	 * Loads cardios from server. Is loaded after workouts are completed
	 */
	private void loadCardios() {

		unbindPresenters();
		
		//fetch cardios & runs
		Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getBlogData(0, 1, 3, Functions.trimDateToDatabase(date, true), Functions.trimDateToDatabase(date, true), uid, true, new MyAsyncCallback<List<BlogData>>() {
			@Override
			public void onSuccess(List<BlogData> result) {
				//show data
				showCardios(result);
			}
		});
		addRequest(req);
	}

	/**
	 * Shows cardios in content (view)
	 * @param list : cardios models to show
	 */
	private void showCardios(List<BlogData> list) {
		
		try {

	    unbindPresenters();

			//if no workouts
			if(list.size() > 0) {
				
				BlogData data = list.get(0);
				
				//cardio
				if(data.getCardios() != null) {
					for(CardioValueModel m : data.getCardios()) {
						final CardioValueLinkPresenter wp = new CardioValueLinkPresenter(rpcService, eventBus, (CardioValueLinkDisplay)GWT.create(CardioValueLinkView.class), m);
						addNewPresenter(wp);
					}
				}
				
				//run
				if(data.getRuns() != null) {
					for(RunValueModel m : data.getRuns()) {
						final RunValueLinkPresenter wp = new RunValueLinkPresenter(rpcService, eventBus, (RunValueLinkDisplay)GWT.create(RunValueLinkView.class), m);
						addNewPresenter(wp);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {			
		if(cardioPresenters != null) {
			for(int i=0; i < cardioPresenters.size(); i++) {
				final Presenter presenter = cardioPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			cardioPresenters.clear();
		}
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(Presenter presenter) {
		
		cardioPresenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}
	
}
