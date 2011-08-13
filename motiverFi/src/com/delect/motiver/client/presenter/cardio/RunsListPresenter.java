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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.RunRemovedEvent;
import com.delect.motiver.client.event.handler.RunRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.cardio.RunPresenter.RunDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.cardio.RunView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RunModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Shows runs in list
 * @author Antti
 *
 */
public class RunsListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RunsListDisplay extends Display {
	}
	//new workout handler
	public Listener<BaseEvent> NewRunListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewRun();
		}
	};

	private EmptyPresenter emptyPresenter;
	private long runId = 0;
	//child presenters
	private List<RunPresenter> runPresenters = new ArrayList<RunPresenter>();
	
	private ShowMorePresenter showMorePresenter;
	RunsListDisplay display;


	/**
	 * Shows runs in list
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public RunsListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RunsListDisplay display, long runId) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.runId = runId;
	}
	 
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
	
		//EVENT: run removed
		addEventHandler(RunRemovedEvent.TYPE, new RunRemovedEventHandler() {
			@Override
			public void onRunRemoved(RunRemovedEvent event) {
				//if workout in list
				if(event.getRun() != null) {
          removePresenter(event.getRun());
        }
			}
		});
	}


	@Override
	public void onRefresh() {
		//refresh childs
		if(runPresenters != null) {
			for(int i=0; i < runPresenters.size(); i++) {
				final Presenter presenter = runPresenters.get(i);
				if(presenter != null) {
          presenter.run(display.getBaseContainer());
				}
			}
		}
	}


	@Override
	public void onRun() {
		
    //load meals
    loadRuns(0);
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
	 * @param run
	 */
	private void removePresenter(RunModel run) {

		try {
			//remove also from presenters
			for(int i=0; i < runPresenters.size(); i++) {
				Presenter presenter = runPresenters.get(0);
				if(presenter != null && ((RunPresenter)presenter).run.getId() == run.getId()) {
          runPresenters.remove(presenter);
		    }
			}

			//if no runs/foods -> show empty presenter
			if(runPresenters.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoRuns());
				emptyPresenter.run(display.getBaseContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Shows runs (multiple RunPresenters)
	 * @param list : RunModels
	 * @param openFirst : open first run
	 */
	private void showRuns(final int index, List<RunModel> list, boolean openFirst) {

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
			
			//if no runs
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoRuns());
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
				
				for(RunModel m : list) {
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadRuns(index + Constants.LIMIT_RUNS);
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {
						//if id found or only item
						boolean openAsDefault = (m.getId() == runId || list.size() == 1);
						final RunPresenter mp = new RunPresenter(rpcService, eventBus, (RunDisplay)GWT.create(RunView.class), m, true, openAsDefault);
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
					
		if(runPresenters != null) {
			for(int i=0; i < runPresenters.size(); i++) {
        final Presenter presenter = runPresenters.get(i);
        if(presenter != null) {
          presenter.stop();
        }
			}
			runPresenters.clear();
		}
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(RunPresenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		runPresenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}

	/**
	 * Adds dummy workout
	 */
	protected void addNewRun() {
		//create empty measurePresenter
		RunModel dummy = new RunModel();
		dummy.setName("");
		dummy.setDistance(0);
		final RunPresenter mp = new RunPresenter(rpcService, eventBus, (RunDisplay)GWT.create(RunView.class), dummy, true, true);
		addNewPresenter(mp);
	}

	/**
	 * Loads values
	 */
	void loadRuns(final int index) {
    		
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
		final Request req = rpcService.getRuns(index, new MyAsyncCallback<List<RunModel>>() {
			@Override
			public void onSuccess(List<RunModel> result) {
				showRuns(index, result, false);
      }
		});
		addRequest(req);
	}

}
