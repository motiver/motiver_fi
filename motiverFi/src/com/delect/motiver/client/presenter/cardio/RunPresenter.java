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
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.RunCreatedEvent;
import com.delect.motiver.client.event.RunRemovedEvent;
import com.delect.motiver.client.event.RunValueCreatedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.RunValueCreatedEventHandler;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.DatesSelectorPresenter;
import com.delect.motiver.client.presenter.DatesSelectorPresenter.DatesSelectorDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.AddNewRunValuePresenter.AddNewRunValueDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.DatesSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.cardio.AddNewRunValueView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single run (name and foods)
 * @author Antti
 *
 */
public class RunPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RunDisplay extends Display {
		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getDataContainer();
		public abstract LayoutContainer getDatesContainer();
		public abstract void setCollapsible(boolean isCollapsible);
		public abstract void setHandler(RunHandler runHandler);
		public abstract void setModel(RunModel run);
		public abstract void setValues(List<RunValueModel> values);
		public abstract void showContent();
	}

	public interface RunHandler {
		void newValue();
		void runRemoved();
		void saveData(RunModel model);
		void valuesRemoved(List<RunValueModel> values);
		void valuesVisible();	//called when run visible (we load foods then)
	}
	public RunModel run;

	private AddNewRunValuePresenter addNewRunValuePresenter;
	private CommentsBoxPresenter commentsPresenter;
	private Date dateEnd;
	
	//child presenters
	private DatesSelectorPresenter datesSelectorPresenter;
	private Date dateStart;
	private Presenter emptyPresenter = null;
	private boolean isCollapsible;
	private boolean openAsDefault;
	private List<RunValueModel> values = new ArrayList<RunValueModel>();

	//callback for loading values
	final MyAsyncCallback<List<RunValueModel>> callback = new MyAsyncCallback<List<RunValueModel>>() {
		@Override
		public void onSuccess(List<RunValueModel> result) {
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

	RunDisplay display;
	
	/**
	 * Shows all values from single run
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param run
	 * @param openAsDefault
	 */
	public RunPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RunDisplay display, RunModel run, boolean isCollapsible, boolean openAsDefault) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.run = run;
    this.isCollapsible = isCollapsible;
    this.openAsDefault = openAsDefault;
	    
    //init date selector
    datesSelectorPresenter = new DatesSelectorPresenter(rpcService, eventBus, (DatesSelectorDisplay)GWT.create(DatesSelectorView.class), Constants.DAYS_DIFF_MAX_RUN, Constants.DAYS_INDEX_RUN);
    dateStart = datesSelectorPresenter.getDateStart();
    dateEnd = datesSelectorPresenter.getDateEnd();
	}
	

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(run);
		display.setCollapsible(isCollapsible);
					
		//Event handlers
		//Launch RunUpdatedEvent if run (content) changes
		display.setHandler(new RunHandler() {
			@Override
			public void newValue() {
				
				addNewRunValuePresenter = new AddNewRunValuePresenter(rpcService, eventBus, (AddNewRunValueDisplay)GWT.create(AddNewRunValueView.class), run, null);
				addNewRunValuePresenter.run(display.getDataContainer());
			}
			@Override
			public void runRemoved() {
				display.setContentEnabled(false);

				//remove run
				final Request req = rpcService.removeRun(run, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						//fire event
						eventBus.fireEvent(new RunRemovedEvent(run));
					}
				});
				addRequest(req);
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveData(RunModel model) {
				
				//if cancelled adding new measurement
				if(model == null) {
					stop();
					
					//fire event
					eventBus.fireEvent(new RunRemovedEvent(run));
				}
				//added new measurement
				else if(model.getId() == 0) {
					run = model;
					display.setContentEnabled(false);
					
					//create model
					final Request req = rpcService.addRun(model, new MyAsyncCallback<RunModel>() {
						@Override
						public void onSuccess(RunModel result) {
							display.setContentEnabled(true);
							run = result;

							refresh();
							
							//fire event
							final RunCreatedEvent event = new RunCreatedEvent(run);
							fireEvent(event);
						}
					});
					addRequest(req);
				}
				//edited old value
				else {
					run = model;
					final Request req = rpcService.updateRun(run, MyAsyncCallback.EmptyCallback);
					addRequest(req);
				}
			}
			@SuppressWarnings("unchecked")
      @Override
			public void valuesRemoved(List<RunValueModel> list) {
				final Request req = rpcService.removeRunValues(run, list, MyAsyncCallback.EmptyCallback);
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
		
		//EVENT: run value added
		addEventHandler(RunValueCreatedEvent.TYPE, new RunValueCreatedEventHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onRunValueCreated(RunValueCreatedEvent event) {
				//check dates (to make sure value is visible)
				Date dEvent = event.getRunValue().getDate();
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

				//reload run values
				loadValues(dateStart, dateEnd);
			}
		});
	}


	@Override
	public void onRefresh() {
		
		if(addNewRunValuePresenter != null) {
      addNewRunValuePresenter.stop();
    }
		
		if(datesSelectorPresenter != null) {
      datesSelectorPresenter.run(display.getDatesContainer());
    }
	}


	@Override
	public void onRun() {

    //if all measurements
		if(run != null) {
      if(run.getId() != 0) {
				
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
		
		if(addNewRunValuePresenter != null) {
			addNewRunValuePresenter.stop();
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
		if(run.getId() == 0) {
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
      commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), run);
      commentsPresenter.run(display.getCommentsContainer());
    }

    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getRunValues(run, CommonUtils.trimDateToDatabase(dateStart, true), CommonUtils.trimDateToDatabase(dateEnd, true), callback);
		addRequest(req);
	}

}
