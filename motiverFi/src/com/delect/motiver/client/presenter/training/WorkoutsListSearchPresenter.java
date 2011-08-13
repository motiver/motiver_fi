/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.training;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.training.WorkoutLinkPresenter.WorkoutLinkDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.training.WorkoutLinkView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.WorkoutModel;

/**
 * List of workouts
 * <br>Launch WorkoutSelectEvent when workout is selected
 * <br>Unbinds itself after event
 */
public class WorkoutsListSearchPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class WorkoutsListSearchDisplay extends Display {
	}
	private WorkoutsListSearchDisplay display;

	private EmptyPresenter emptyPresenter;
	private String query = "";
	private boolean quickSelectionEnabled;
	
	//child presenters
	private List<Presenter> searchResultsPresenters = new ArrayList<Presenter>();
	private ShowMorePresenter showMorePresenter;

	
	public WorkoutsListSearchPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutsListSearchDisplay display, String query, boolean quickSelectionEnabled) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.query  = query;
		this.quickSelectionEnabled = quickSelectionEnabled;
	}


	/*
	 * VIEW 2 (search results) 
	 */
	
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onRun() {
	    
    loadSearch(0, query);
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
		unbindSearchPresenters();
	}


	/**
	 * Shows workouts (multiple SingleWorkoutPresenters)
	 * @param list : WorkoutModels
	 * @param openFirst : open first workout
	 */
	private void showSearch(final int index, final String query, List<WorkoutModel> list) {

		try {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			if(showMorePresenter != null) {
				showMorePresenter.stop();
      }
			//stop presenters if first items
			if(index == 0) {
				unbindSearchPresenters();
      }
			
			//if no workouts
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkoutsFound());
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
				
				for(final WorkoutModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadSearch(index + Constants.LIMIT_WORKOUTS, query);								
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {		
						//new presenter
						final WorkoutLinkPresenter wp = new WorkoutLinkPresenter(rpcService, eventBus, (WorkoutLinkDisplay)GWT.create(WorkoutLinkView.class), m, quickSelectionEnabled);
						addNewSearchResultsPresenter(wp);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Unbinds all the presenters
	 */
	private void unbindSearchPresenters() {
		
		//search results
		if(searchResultsPresenters != null) {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			//stop show more
			if(showMorePresenter != null) {
				showMorePresenter.stop();
      }	
			
			for(int i=0; i < searchResultsPresenters.size(); i++) {
				final Presenter presenter = searchResultsPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			searchResultsPresenters.clear();					
		}
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewSearchResultsPresenter(Presenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			searchResultsPresenters.add(presenter);
			presenter.run(display.getBaseContainer());
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads workouts
	 */
	void loadSearch(final int index, final String query) {
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }	
		//stop presenters if first items
		if(index == 0) {
			unbindSearchPresenters();
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBaseContainer());

    //search workouts
		rpcService.searchWorkouts(index, query, new MyAsyncCallback<List<WorkoutModel>>() {
			@Override
			public void onSuccess(List<WorkoutModel> workouts) {
				showSearch(index, query, workouts);
      }
		});
	}

}
