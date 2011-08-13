/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

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
import com.delect.motiver.client.event.MeasurementCreatedEvent;
import com.delect.motiver.client.event.MeasurementRemovedEvent;
import com.delect.motiver.client.event.MeasurementValueCreatedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.MeasurementValueCreatedEventHandler;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.DatesSelectorPresenter;
import com.delect.motiver.client.presenter.DatesSelectorPresenter.DatesSelectorDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.profile.AddNewMeasurementValuePresenter.AddNewMeasurementValueDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.DatesSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.profile.AddNewMeasurementValueView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.MeasurementValueModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single measurement
 * @author Antti
 *
 */
public class MeasurementPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MeasurementDisplay extends Display {

		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getDataContainer();
		public abstract LayoutContainer getDatesContainer();
		public abstract void setCollapsible(boolean isCollapsible);
		public abstract void setHandler(MeasurementHandler measurementHandler);
		public abstract void setModel(MeasurementModel measurement);
		public abstract void setValues(List<MeasurementValueModel> values);
		public abstract void showContent();
	}

	public interface MeasurementHandler {
		void measurementRemoved();
		void newValue();
		void saveData(MeasurementModel model);
		void valuesRemoved(List<MeasurementValueModel> values);
		void valuesVisible();	//called when measurement visible (we load foods then)
	}
	private AddNewMeasurementValuePresenter addNewMeasurementValuePresenter;

	private CommentsBoxPresenter commentsPresenter;
	private Date dateEnd;
	private DatesSelectorPresenter datesSelectorPresenter;
	private Date dateStart;
	
	private MeasurementDisplay display;
	//child presenters
	private Presenter emptyPresenter = null;
	private boolean isCollapsible;
	private boolean openAsDefault;

	private List<MeasurementValueModel> values = new ArrayList<MeasurementValueModel>();

	protected MeasurementModel measurement;

	//callback for loading values
	final MyAsyncCallback<List<MeasurementValueModel>> callback = new MyAsyncCallback<List<MeasurementValueModel>>() {
		@Override
		public void onSuccess(List<MeasurementValueModel> result) {
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
	
	/**
	 * Shows all values from single measurement
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param measurement
	 * @param openAsDefault
	 */
	public MeasurementPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MeasurementDisplay display, MeasurementModel measurement, boolean isCollapsible, boolean openAsDefault) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.measurement = measurement;
    this.isCollapsible = isCollapsible;
    this.openAsDefault = openAsDefault;
	    
    //init date selector
    datesSelectorPresenter = new DatesSelectorPresenter(rpcService, eventBus, (DatesSelectorDisplay)GWT.create(DatesSelectorView.class), Constants.DAYS_DIFF_MAX_MEASUREMENT, Constants.DAYS_INDEX_MEASUREMENT);
    dateStart = datesSelectorPresenter.getDateStart();
    dateEnd = datesSelectorPresenter.getDateEnd();
	}	

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(measurement);
		display.setCollapsible(isCollapsible);
					
		//Event handlers
		//Launch MeasurementUpdatedEvent if measurement (content) changes
		display.setHandler(new MeasurementHandler() {
			@Override
			public void measurementRemoved() {
				display.setContentEnabled(false);

				//remove measurement
				rpcService.removeMeasurement(measurement, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
				
						//fire event
						eventBus.fireEvent(new MeasurementRemovedEvent(measurement));
					}
				});
			}
			@Override
			public void newValue() {
				addNewMeasurementValuePresenter = new AddNewMeasurementValuePresenter(rpcService, eventBus, (AddNewMeasurementValueDisplay)GWT.create(AddNewMeasurementValueView.class), measurement);
				addNewMeasurementValuePresenter.run(display.getDataContainer());
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveData(MeasurementModel model) {
				
				//if cancelled adding new measurement
				if(model == null) {
					stop();
					
					//fire event
					eventBus.fireEvent(new MeasurementRemovedEvent(measurement));
				}
				//added new measurement
				else if(model.getId() == 0) {
					measurement = model;
					display.setContentEnabled(false);
					
					//create model
					rpcService.addMeasurement(model, new MyAsyncCallback<MeasurementModel>() {
						@Override
						public void onSuccess(MeasurementModel result) {
							display.setContentEnabled(true);
							measurement = result;

							refresh();
							
							//fire event
							final MeasurementCreatedEvent event = new MeasurementCreatedEvent(measurement);
							fireEvent(event);
						}
					});
				}
				//edited old value
				else {
					measurement = model;
					rpcService.updateMeasurement(measurement, MyAsyncCallback.EmptyCallback);
				}
			}
			@SuppressWarnings("unchecked")
      @Override
			public void valuesRemoved(List<MeasurementValueModel> list) {
				rpcService.removeMeasurementValues(measurement, list, MyAsyncCallback.EmptyCallback);

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
		
		//EVENT: measurement value added
		addEventHandler(MeasurementValueCreatedEvent.TYPE, new MeasurementValueCreatedEventHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onMeasurementValueCreated(MeasurementValueCreatedEvent event) {
				//check dates (to make sure value is visible)
				Date dEvent = event.getMeasurementValue().getDate();
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

				//reload measurement values
				loadValues(dateStart, dateEnd);
			}
		});
		
	}


	@Override
	public void onRefresh() {
		
		if(addNewMeasurementValuePresenter != null) {
			addNewMeasurementValuePresenter.stop();
    }
		
		if(datesSelectorPresenter != null) {
			datesSelectorPresenter.run(display.getDatesContainer());
    }
		
	}


	@Override
	public void onRun() {
    	
    //if all measurements
		if(measurement != null) {
      if(measurement.getId() != 0) {
				
        //if measurement is "open" as default
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
		if(addNewMeasurementValuePresenter != null) {
			addNewMeasurementValuePresenter.stop();
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
		if(measurement.getId() == 0) {
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
      commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), measurement);
      commentsPresenter.run(display.getCommentsContainer());
    }

    Motiver.setNextCallCacheable(true);
    final Request req = rpcService.getMeasurementValues(measurement, Functions.trimDateToDatabase(dateStart, true), Functions.trimDateToDatabase(dateEnd, true), callback);
    addRequest(req);
	}

}
