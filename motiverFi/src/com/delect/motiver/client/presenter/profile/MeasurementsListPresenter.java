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
package com.delect.motiver.client.presenter.profile;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.MeasurementRemovedEvent;
import com.delect.motiver.client.event.handler.MeasurementRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.profile.MeasurementPresenter.MeasurementDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.profile.MeasurementView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.MeasurementModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Shows measurements in list
 * @author Antti
 *
 */
public class MeasurementsListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MeasurementsListDisplay extends Display {
	}
	//new measurement handler
	public Listener<BaseEvent> NewMeasurementListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewMeasurement();
		}
	};

	private MeasurementsListDisplay display;
	private EmptyPresenter emptyPresenter;
	private long measurementId = 0;
	
	private List<MeasurementPresenter> measurementPresenters = new ArrayList<MeasurementPresenter>();
	private ShowMorePresenter showMorePresenter;


	/**
	 * Shows measurements in list
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public MeasurementsListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MeasurementsListDisplay display, long measurementId) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.measurementId = measurementId;
	}
	 
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {

		//EVENT: measurement removed -> refresh view
		addEventHandler(MeasurementRemovedEvent.TYPE, new MeasurementRemovedEventHandler() {
			@Override
			public void onMeasurementRemoved(MeasurementRemovedEvent event) {
				//if routine in list
				if(event.getMeasurement() != null) {
					removePresenter(event.getMeasurement());
	      }
			}
		});
	}	


	@Override
	public void onRefresh() {
		//refresh childs
		if(measurementPresenters != null) {
			for(int i=0; i < measurementPresenters.size(); i++) {
				final Presenter presenter = measurementPresenters.get(i);
				if(presenter != null) {
					presenter.run(display.getBaseContainer());
				}
			}			
		}
	}


	@Override
	public void onRun() {
		
    //load meals
    loadMeasurements(0);
	}

	
	@Override
	public void onStop() {

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }		
		//stop presenters
		unbindPresenters();
	}
	
	/**
	 * Removes presenter from view
	 * @param measurement
	 */
	private void removePresenter(MeasurementModel measurement) {

		try {
			//remove also from presenters
			for(int i=0; i < measurementPresenters.size(); i++) {
			  MeasurementPresenter presenter = measurementPresenters.get(i);
				if(presenter != null && presenter.measurement.getId() == measurement.getId()) {
          measurementPresenters.remove(presenter);
	      }
			}

			//if no measurements -> show empty presenter
			if(measurementPresenters.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMeasurements());
				emptyPresenter.run(display.getBaseContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Shows measurements (multiple MeasurementPresenters)
	 * @param list : MeasurementModels
	 * @param openFirst : open first measurement
	 */
	private void showMeasurements(final int index, List<MeasurementModel> list, boolean openFirst) {

		try {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			//stop show more
			if(showMorePresenter != null) {
				showMorePresenter.stop();
      }
			//stop presenters if first items
			if(index == 0) {
				unbindPresenters();
      }
			
			//if no measurements
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMeasurements());
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
				
				for(MeasurementModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMeasurements(index + Constants.LIMIT_MEASUREMENTS);								
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {
						//if id found or only item
						boolean openAsDefault = (m.getId() == measurementId || list.size() == 1);
						final MeasurementPresenter mp = new MeasurementPresenter(rpcService, eventBus, (MeasurementDisplay)GWT.create(MeasurementView.class), m, true, openAsDefault);
						addNewPresenter(mp);
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
		
		if(measurementPresenters != null) {
			for(int i=0; i < measurementPresenters.size(); i++) {
				final Presenter presenter = measurementPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			measurementPresenters.clear();					
		}
	}


	/**
	 * Adds dummy measurement
	 */
	protected void addNewMeasurement() {
		
		//create empty measurePresenter
		MeasurementModel dummy = new MeasurementModel();
		dummy.setName("");
		dummy.setUnit("");
		final MeasurementPresenter mp = new MeasurementPresenter(rpcService, eventBus, (MeasurementDisplay)GWT.create(MeasurementView.class), dummy, true, true);
		addNewPresenter(mp);
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(MeasurementPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			measurementPresenters.add(presenter);
			presenter.run(display.getBaseContainer());
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads values
	 */
	void loadMeasurements(final int index) {
    		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters();
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBaseContainer());

    //get meals
    if(index == 0) {
      Motiver.setNextCallCacheable(true);
    }
    final Request req = rpcService.getMeasurements(index, new MyAsyncCallback<List<MeasurementModel>>() {
			@Override
			public void onSuccess(List<MeasurementModel> result) {
				showMeasurements(index, result, false);
      }
		});
    addRequest(req);
	}

}
