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
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.WorkoutShowEvent;
import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
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

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Main page for workouts' list
 * <br>Launch WorkoutSelectEvent when workout is selected
 * <br>Launch WorkoutCreatedEvent when workout is created
 * @author Antti
 *
 */
public class WorkoutsListSubPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class WorkoutsListSubDisplay extends Display {

		public abstract LayoutContainer getMostPopularContainer();
		public abstract LayoutContainer getMyWorkoutsContainer();
		public abstract void setHandler(WorkoutsListSubHandler workoutsListSubHandler);
	}

	public interface WorkoutsListSubHandler {
		void createWorkout(String name);
	}
	private WorkoutsListSubDisplay display;

	private EmptyPresenter emptyPresenter;
	private EmptyPresenter emptyPresenter2;
	private List<WorkoutLinkPresenter> mostPopularPresenters = new ArrayList<WorkoutLinkPresenter>();
	//child presenters
	private List<WorkoutLinkPresenter> myWorkoutPresenters = new ArrayList<WorkoutLinkPresenter>();
	private boolean quickSelectionEnabled;
	private boolean reloadWorkouts = false;
	
	private ShowMorePresenter showMorePresenter;
	private ShowMorePresenter showMorePresenter2;
	
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public WorkoutsListSubPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutsListSubDisplay display, boolean quickSelectionEnabled) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.quickSelectionEnabled = quickSelectionEnabled;
	}
	
	@Override
	public Display getView() {
		return display;
	}
	@Override
	public void onBind() {
		display.setHandler(new WorkoutsListSubHandler() {

			@Override
			public void createWorkout(String name) {
				//Create new workout
				WorkoutModel model = new WorkoutModel();

				eventBus.fireEvent(new WorkoutShowEvent(model));
			}
			
		});
		
		//EVENT: workout removed
		addEventHandler(WorkoutRemovedEvent.TYPE, new WorkoutRemovedEventHandler() {
			@Override
			public void onWorkoutRemoved(WorkoutRemovedEvent event) {
				//if workout in list
				if(event.getWorkout() != null && event.getWorkout().getDate() == null) {
          removeMyWorkoutPresenter(event.getWorkout());
          removeMostPopularPresenter(event.getWorkout());
        }
			}
		});
		
		//EVENT: workout created -> reload workouts
		addEventHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {
			@Override
			public void onWorkoutCreated(WorkoutCreatedEvent event) {
				if(event.getWorkout().getRoutineId() == 0) {
					reloadWorkouts  = true;
	      }
			}
		});
	}


	@Override
	public void onRefresh() {
		//reload if workouts created
		if(reloadWorkouts) {
			loadMyWorkouts(0);
    }
		
		reloadWorkouts = false;
	}


	@Override
	public void onRun() {
	    
		loadMyWorkouts(0);
		loadMostPopularWorkouts(0);
	}


	@Override
	public void onStop() {

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(emptyPresenter2 != null) {
			emptyPresenter2.stop();
    }
		if(showMorePresenter != null) {
			showMorePresenter.stop();	
    }
		if(showMorePresenter2 != null) {
			showMorePresenter2.stop();
    }
		
		//stop presenters
		unbindPresenters(0);
	}


	/**
	 * Removes presenter from most popular workouts
	 * @param workout
	 */
	private void removeMostPopularPresenter(WorkoutModel workout) {

		try {
			//remove also from presenters
			for(int i=0; i < mostPopularPresenters.size(); i++) {
				WorkoutLinkPresenter presenter = mostPopularPresenters.get(i);
				if(presenter != null && presenter.workout.getId() == workout.getId()) {
          presenter.stop();
          mostPopularPresenters.remove(presenter);
        }
			}

			//if no workouts -> show empty presenter
			if(mostPopularPresenters.size() == 0) {
				if(emptyPresenter2 != null) {
					emptyPresenter2.stop();
	      }
				emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
				emptyPresenter2.run(display.getMostPopularContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Removes presenter from user workouts
	 * @param workout
	 */
	private void removeMyWorkoutPresenter(WorkoutModel workout) {

		try {
			//remove also from presenters
			for(int i=0; i < myWorkoutPresenters.size(); i++) {
				WorkoutLinkPresenter presenter = myWorkoutPresenters.get(i);
				if(presenter != null && presenter.workout.getId() == workout.getId()) {
          presenter.stop();
          myWorkoutPresenters.remove(presenter);
        }
			}

			//if no workouts -> show empty presenter
			if(myWorkoutPresenters.size() == 0) {
				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
				emptyPresenter.run(display.getMyWorkoutsContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Shows most popular workouts
	 * @param list : WorkoutModels
	 * @param openFirst : open first workout
	 */
	private void showMostPopularWorkouts(final int index, List<WorkoutModel> list) {

		try {

			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
      }
			//stop show more
			if(showMorePresenter2 != null) {
				showMorePresenter2.stop();
      }
			//stop presenters if first items
			if(index == 0) {
				unbindPresenters(2);
      }
			
			//if no workouts
			if(index == 0 && list.size() == 0) {
				emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
				emptyPresenter2.run(display.getMostPopularContainer());
			}
			else {
				
				for(final WorkoutModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter2 = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMostPopularWorkouts(index + Constants.LIMIT_WORKOUTS);								
							}
						});
						showMorePresenter2.run(display.getMostPopularContainer());
					}
					else {		
						//new presenter
						final WorkoutLinkPresenter wp = new WorkoutLinkPresenter(rpcService, eventBus, (WorkoutLinkDisplay)GWT.create(WorkoutLinkView.class), m, quickSelectionEnabled);
						addNewMostPopularPresenter(wp);
					}
					
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Shows user's workouts
	 * @param list : WorkoutModels
	 */
	private void showMyWorkouts(final int index, List<WorkoutModel> list) {

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
				unbindPresenters(1);
      }
			
			//if no workouts
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
				emptyPresenter.run(display.getMyWorkoutsContainer());
			}
			else {
				
				for(final WorkoutModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMyWorkouts(index + Constants.LIMIT_WORKOUTS);								
							}
						});
						showMorePresenter.run(display.getMyWorkoutsContainer());
					}
					else {		
						//new presenter
						final WorkoutLinkPresenter wp = new WorkoutLinkPresenter(rpcService, eventBus, (WorkoutLinkDisplay)GWT.create(WorkoutLinkView.class), m, quickSelectionEnabled);
						addNewMyWorkoutPresenter(wp);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Unbinds all the presenters
	 * @param target : which presenters all unbinded, 0=all, 1=myworkouts, 2=most popular, 3=search results
	 */
	private void unbindPresenters(int target) {
		
		//my workouts
		if(myWorkoutPresenters != null && (target == 0 || target == 1)) {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			if(showMorePresenter != null) {
				showMorePresenter.stop();	
      }
			
			for(int i=0; i < myWorkoutPresenters.size(); i++) {
				final Presenter presenter = myWorkoutPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			myWorkoutPresenters.clear();			
		}				
		//most popular
		if(mostPopularPresenters != null && (target == 0 || target == 2)) {

			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
      }
			if(showMorePresenter2 != null) {
				showMorePresenter2.stop();
      }
			
			for(int i=0; i < mostPopularPresenters.size(); i++) {
        final Presenter presenter = mostPopularPresenters.get(i);
        if(presenter != null) {
          presenter.stop();
        }
			}
			mostPopularPresenters.clear();	
		}
	}


	/**
	 * Adds new presenter to view (most popular workouts)
	 * @param presenter
	 */
	protected void addNewMostPopularPresenter(WorkoutLinkPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
				emptyPresenter2 = null;
			}
			
			mostPopularPresenters.add(presenter);
			presenter.run(display.getMostPopularContainer());
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Adds new presenter to view (my workouts)
	 * @param presenter
	 */
	protected void addNewMyWorkoutPresenter(WorkoutLinkPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			myWorkoutPresenters.add(presenter);
			presenter.run(display.getMyWorkoutsContainer());
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads workouts
	 */
	void loadMostPopularWorkouts(final int index) {
    		
		if(emptyPresenter2 != null) {
			emptyPresenter2.stop();
    }
		//stop show more
		if(showMorePresenter2 != null) {
			showMorePresenter2.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters(2);
    }

		//add empty presenter
		emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter2.run(display.getMostPopularContainer());
		
		//load most popular
    Motiver.setNextCallCacheable(true);
    final Request req = rpcService.getMostPopularWorkouts(index, new MyAsyncCallback<List<WorkoutModel>>() {
			@Override
			public void onSuccess(List<WorkoutModel> result) {
				showMostPopularWorkouts(index, result);
      }
		});
    addRequest(req);
	}

	/**
	 * Loads workouts
	 */
	void loadMyWorkouts(final int index) {
    		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters(1);
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getMyWorkoutsContainer());

    //get workouts
    Motiver.setNextCallCacheable(true);
    final Request req = rpcService.getWorkouts(index, null, new MyAsyncCallback<List<WorkoutModel>>() {
			@Override
			public void onSuccess(List<WorkoutModel> result) {
				showMyWorkouts(index, result);
      }
		});
    addRequest(req);
	}

}
