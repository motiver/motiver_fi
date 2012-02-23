/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
package com.delect.motiver.client.presenter.cardio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioCreatedEvent;
import com.delect.motiver.client.event.CardioRemovedEvent;
import com.delect.motiver.client.event.CardioValueCreatedEvent;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.handler.CardioValueCreatedEventHandler;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.DatesSelectorPresenter;
import com.delect.motiver.client.presenter.DatesSelectorPresenter.DatesSelectorDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.AddNewCardioValuePresenter.AddNewCardioValueDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.DatesSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.cardio.AddNewCardioValueView;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single cardio (name and foods)
 * @author Antti
 *
 */
public class CardioPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CardioDisplay extends Display {
		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getDataContainer();
		public abstract LayoutContainer getDatesContainer();
		public abstract void setCollapsible(boolean isCollapsible);
		public abstract void setHandler(CardioHandler cardioHandler);
		public abstract void setModel(CardioModel cardio);
		public abstract void setValues(List<CardioValueModel> values);
		public abstract void showContent();
	}

	public interface CardioHandler {
		void cardioRemoved();
		void newValue();
		void saveData(CardioModel model);
		void valuesRemoved(List<CardioValueModel> values);
		void valuesVisible();	//called when cardio visible (we load foods then)
	}
	private AddNewCardioValuePresenter addNewCardioValuePresenter;

	//callback for loading values
	private MyAsyncCallback<List<CardioValueModel>> callback = new MyAsyncCallback<List<CardioValueModel>>() {
		@Override
		public void onSuccess(List<CardioValueModel> result) {
			values = result;
			
			//sort list (date DESC)
			Collections.sort(values);
			
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			display.setValues(values);
			//no values -> show empty presenter
			if(values.size() == 0) {
				//add empty presenter
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoValues());
				emptyPresenter.run(display.getDataContainer());
			}
		}
	};
	private CommentsBoxPresenter commentsPresenter;
	private Date dateEnd;
	private DatesSelectorPresenter datesSelectorPresenter;

	private Date dateStart;
	private CardioDisplay display;
	//child presenters
	private Presenter emptyPresenter = null;
	private boolean isCollapsible;
	private boolean openAsDefault;

	private List<CardioValueModel> values = new ArrayList<CardioValueModel>();
	
	protected CardioModel cardio;
	
	/**
	 * Shows all values from single cardio
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param cardio
	 * @param openAsDefault
	 */
	public CardioPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CardioDisplay display, CardioModel cardio, boolean isCollapsible, boolean openAsDefault) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.cardio = cardio;
    this.isCollapsible = isCollapsible;
    this.openAsDefault = openAsDefault;
	    
    //init date selector
    datesSelectorPresenter = new DatesSelectorPresenter(rpcService, eventBus, (DatesSelectorDisplay)GWT.create(DatesSelectorView.class), Constants.DAYS_DIFF_MAX_CARDIO, Constants.DAYS_INDEX_CARDIO);
    dateStart = datesSelectorPresenter.getDateStart();
    dateEnd = datesSelectorPresenter.getDateEnd();
	}
	

	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		display.setModel(cardio);
		display.setCollapsible(isCollapsible);
					
		//Event handlers
		//Launch CardioUpdatedEvent if cardio (content) changes
		display.setHandler(new CardioHandler() {
			@Override
			public void cardioRemoved() {
				display.setContentEnabled(false);

				//remove cardio
				final Request req = rpcService.removeCardio(cardio, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						//fire event
						eventBus.fireEvent(new CardioRemovedEvent(cardio));
					}
				});
				addRequest(req);
			}
			@Override
			public void newValue() {
				
				addNewCardioValuePresenter = new AddNewCardioValuePresenter(rpcService, eventBus, (AddNewCardioValueDisplay)GWT.create(AddNewCardioValueView.class), cardio, null);
				addNewCardioValuePresenter.run(display.getDataContainer());
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveData(CardioModel model) {
				
				//if cancelled adding new measurement
				if(model == null) {
					stop();
					
					//fire event
					eventBus.fireEvent(new CardioRemovedEvent(cardio));
				}
				//added new measurement
				else if(model.getId() == 0) {
					cardio = model;
					display.setContentEnabled(false);
					
					//create model
					final Request req = rpcService.addCardio(model, new MyAsyncCallback<CardioModel>() {
						@Override
						public void onSuccess(CardioModel result) {
							display.setContentEnabled(true);
							cardio = result;

							refresh();
							
							//fire event
							final CardioCreatedEvent event = new CardioCreatedEvent(cardio);
							fireEvent(event);
						}
					});
					addRequest(req);
				}
				//edited old value
				else {
					cardio = model;
					final Request req = rpcService.updateCardio(cardio, MyAsyncCallback.EmptyCallback);
					addRequest(req);
				}
			}
			@SuppressWarnings("unchecked")
      @Override
			public void valuesRemoved(List<CardioValueModel> list) {
				final Request req = rpcService.removeCardioValues(cardio, list, MyAsyncCallback.EmptyCallback);
				addRequest(req);

				//remove values from list
				values.removeAll(list);

				display.setValues(values);
				
				if(values.size() == 0) {
					//add empty presenter
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoValues());
					emptyPresenter.run(display.getDataContainer());
				}
				
			}
			@Override
			public void valuesVisible() {
				loadValues(dateStart, dateEnd);
			}
		});

		//EVENT: dates changed
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				if(event.getSource() != null && event.getSource().equals(datesSelectorPresenter)) {
          loadValues(event.getDate(), event.getDateEnd());
				}
			}
		});
		
		//EVENT: cardio value added
		addEventHandler(CardioValueCreatedEvent.TYPE, new CardioValueCreatedEventHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onCardioValueCreated(CardioValueCreatedEvent event) {
				//check dates (to make sure value is visible)
				Date dEvent = event.getCardioValue().getDate();
				long d1 = Date.UTC(dateStart.getYear(), dateStart.getMonth(), dateStart.getDate(), 0, 0, 0);
				long d2 = Date.UTC(dateEnd.getYear(), dateEnd.getMonth(), dateEnd.getDate(), 23, 59, 59);
				
				//not visible
				if(dEvent.getTime() < d1 || dEvent.getTime() > d2) {
					//show week after cardio values date
					dateStart = dEvent;
					dateEnd = new Date( (dateStart.getTime() + 1000 * 3600 * 24 * 7) );
					
					//update dates selector
					datesSelectorPresenter.setDates(dateStart, dateEnd);
				}

				//reload cardio values
				loadValues(dateStart, dateEnd);
			}
		});
	}


	@Override
	public void onRefresh() {
		
		if(addNewCardioValuePresenter != null) {
      addNewCardioValuePresenter.stop();
    }
		
		if(datesSelectorPresenter != null) {
      datesSelectorPresenter.run(display.getDatesContainer());
    }
	}


	@Override
	public void onRun() {

    //if all measurements
		if(cardio != null) {
      if(cardio.getId() != 0) {
				
        //if cardio is "open" as default
        if(openAsDefault) {
          display.showContent();
        }
	
      }
      //no model -> hightlight
      else {
        highlight();
      }
		}
		else {
      display.showContent();
    }
	}

	@Override
	public void onStop() {

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }

		if(commentsPresenter != null) {
			commentsPresenter.stop();
    }
		
		if(addNewCardioValuePresenter != null) {
			addNewCardioValuePresenter.stop();
    }
		
		if(datesSelectorPresenter != null) {
			datesSelectorPresenter.stop();
    }
	}

	/**
	 * Loads values
	 */
	void loadValues(Date dateStart, Date dateEnd) {
		
		//only if id is not zero
		if(cardio.getId() == 0) {
			return;
    }
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		
		datesSelectorPresenter.run(display.getDatesContainer());
		
		//reset data
		display.setValues(null);

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getDataContainer());

    //show comments
    if(commentsPresenter == null) {
      commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), cardio);
      commentsPresenter.run(display.getCommentsContainer());
    }
    
    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getCardioValues(cardio, CommonUtils.trimDateToDatabase(dateStart, true), CommonUtils.trimDateToDatabase(dateEnd, true), callback);
		addRequest(req);
	}

}
