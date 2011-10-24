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
package com.delect.motiver.client.presenter.training;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.WorkoutSelectCancelledEvent;
import com.delect.motiver.client.event.WorkoutSelectedEvent;
import com.delect.motiver.client.event.WorkoutShowEvent;
import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutSelectedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutShowEventHandler;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.training.WorkoutPresenter.WorkoutDisplay;
import com.delect.motiver.client.presenter.training.WorkoutsListSearchPresenter.WorkoutsListSearchDisplay;
import com.delect.motiver.client.presenter.training.WorkoutsListSubPresenter.WorkoutsListSubDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.training.WorkoutView;
import com.delect.motiver.client.view.training.WorkoutsListSearchView;
import com.delect.motiver.client.view.training.WorkoutsListSubView;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 3 "pages": (with search box on top)
 *  <br>- main page (shows user's workouts, most popular)
 *  <br>- search results
 *  <br>- single workout (when individual workout is selected)
 *  <br>- Fires {@link com.delect.motiver.client.event.WorkoutCreatedEvent WorkoutCreatedEvent} when workout is selected. 
 *  <br>- Fires {@link com.delect.motiver.client.event.WorkoutSelectCancelledEvent WorkoutSelectCancelledEvent} when selection is cancelled and stops itself
 * @author Antti
 *
 */
public class WorkoutsListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class WorkoutsListDisplay extends Display {

		public abstract LayoutContainer getDataContainer();
		public abstract void setBackButtonVisible(boolean b);
		public abstract void setCancelButtonVisible(boolean b);
		public abstract void setCopyButtonVisible(boolean b);
		public abstract void setHandler(WorkoutsListHandler workoutsListHandler);
		public abstract void setMoveToDateButtonVisible(boolean b, Date date);
		public abstract void setMoveToRoutineButtonVisible(boolean visible, RoutineModel routine);
		public abstract void setQuickSelectionButtonVisible(boolean b, Date date);
	}

	public interface WorkoutsListHandler {
		void onBackButtonClicked();
		void onCancelButtonClicked();
		void onCopyButtonClicked();
		void onMoveToDateButtonClicked();
		void onMoveToRoutineButtonClicked();
		void onQuickSelectionButtonClicked();
		void search(String query);
	}
	private Date date;

	private int day;
	private WorkoutsListDisplay display;
	private String lastQuery = "";
	private int lastView = 0;
	private List<WorkoutModel> quickSelectionWorkouts = new ArrayList<WorkoutModel>();	//meals which are selected
	
	private RoutineModel routine;
	
	private WorkoutPresenter singleWorkoutPresenter;
	private long workoutId = 0;
	private WorkoutsListSearchPresenter workoutsListSearchPresenter;
	//child presenters
	private WorkoutsListSubPresenter workoutsListSubPresenter;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param date
	 * @param workoutId : if some workout is open as default
	 */
	public WorkoutsListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutsListDisplay display, Date date, long workoutId) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.date = date;
    this.workoutId  = workoutId;

		boolean quickSelectionEnabled = (date != null);
    workoutsListSubPresenter = new WorkoutsListSubPresenter(rpcService, eventBus, (WorkoutsListSubDisplay)GWT.create(WorkoutsListSubView.class), quickSelectionEnabled);
	}
	

	/**
	 * Adds workout to routine
	 * @param rpcService
	 * @param eventBus
	 * @param workoutsListView
	 * @param routine
	 * @param day
	 */
	public WorkoutsListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutsListDisplay display, RoutineModel routine, int day) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.routine = routine;
    this.day = day;

		boolean quickSelectionEnabled = (date != null);
    workoutsListSubPresenter = new WorkoutsListSubPresenter(rpcService, eventBus, (WorkoutsListSubDisplay)GWT.create(WorkoutsListSubView.class), quickSelectionEnabled);
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setHandler(new WorkoutsListHandler() {
			@Override
			public void onBackButtonClicked() {
				//coming back from search results
				if(singleWorkoutPresenter == null) {
					showMainView();
				}
				//if coming back from single exercise
				else {
					//unbind workout
					singleWorkoutPresenter.stop();
					singleWorkoutPresenter = null;
					
					//last view was search results
					if(lastView == 1) {
						loadSearch(lastQuery);
		      }
					//last view was main menu
					else {
						showMainView();
		      }
				}
			}
			@Override
			public void onCancelButtonClicked() {
				//unbind this
				stop();
				
				//fire cancel event
				fireEvent(new WorkoutSelectCancelledEvent(date));
			}
			@Override
			public void onCopyButtonClicked() {
				copyWorkout();
			}
			@Override
			public void onMoveToDateButtonClicked() {
				//get workout from single presenter
				List<WorkoutModel> workouts = new ArrayList<WorkoutModel>();
				workouts.add(singleWorkoutPresenter.workout);
				
				moveWorkoutToDate(workouts);
			}
			@Override
			public void onMoveToRoutineButtonClicked() {
				copyWorkout();
			}
			@Override
			public void onQuickSelectionButtonClicked() {
				//get meals from quick selection
				moveWorkoutToDate(quickSelectionWorkouts);
				quickSelectionWorkouts.clear();
			}
			@Override
			public void search(String query) {
				loadSearch(query);
			}
		});

		//EVENT: workout created -> open workout
		addEventHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {
			@Override
			public void onWorkoutCreated(WorkoutCreatedEvent event) {
				//if no date
				if(event.getWorkout() != null && event.getWorkout().getDate() == null) {
					showSingleWorkout(event.getWorkout());
				}
			}
		});
		
		//EVENT: workout removed -> show main view
		addEventHandler(WorkoutRemovedEvent.TYPE, new WorkoutRemovedEventHandler() {
			@Override
			public void onWorkoutRemoved(WorkoutRemovedEvent event) {
				//if workout in list
				if(event.getWorkout() != null && event.getWorkout().getDate() == null) {
          showMainView();
	      }
			}
		});
		
		//EVENT: show workouts
		addEventHandler(WorkoutShowEvent.TYPE, new WorkoutShowEventHandler() {
			@Override
			public void selectWorkout(WorkoutShowEvent event) {
				showSingleWorkout(event.getWorkout());
			}
		});
		
		//EVENT: workout selected (quick select)
		addEventHandler(WorkoutSelectedEvent.TYPE, new WorkoutSelectedEventHandler() {
			@Override
			public void workoutSelected(WorkoutSelectedEvent event) {
				setQuickSelection(event.getWorkout(), event.isSelected());
			}
		});
      
    //EVENT: user selected -> show blog
    addEventHandler(UserSelectedEvent.TYPE, new UserSelectedEventHandler() {
      @Override
      public void userSelected(UserSelectedEvent event) {
        //open blog in new window
        Window.open(event.getUser().getBlogUrl(), "_blank", "status=1,toolbar=1,location=1,menubar=1,directories=1,resizable=1,scrollbars=1");
      }
    });
		
    //if date -> show inner title
    if(date != null) {
      //info text
      display.setTitle(AppController.Lang.SelectWorkout());
      display.setCancelButtonVisible(true);
    }
    //if routine -> show inner title
    else if(routine != null) {
      //info text
      display.setTitle(AppController.Lang.SelectWorkoutToRoutine(routine.getName(), day));
      display.setCancelButtonVisible(true);
    }
	}

	@Override
	public void onRun() {
	    
    //show single workout
    if(workoutId != 0) {
			final Request req = rpcService.getWorkout(workoutId, new MyAsyncCallback<WorkoutModel>() {
				@Override
				public void onSuccess(WorkoutModel result) {
          showSingleWorkout(result);
        }
			});
			addRequest(req);
    }	    	
    else {
      showMainView();
    }
	    
    //highlight and scroll
    display.highlight();
  }
	
	@Override
	public void onStop() {
		if(workoutsListSubPresenter != null) {
			workoutsListSubPresenter.stop();
    }
		if(singleWorkoutPresenter != null) {
			singleWorkoutPresenter.stop();
    }
		if(workoutsListSearchPresenter != null) {
			workoutsListSearchPresenter.stop();
    }
	}


	/**
	 * Copies workout ro routine or to our workouts
	 */
	protected void copyWorkout() {
		
		try {
			display.setContentEnabled(false);
			
			//get workout from single presenter
			WorkoutModel model = singleWorkoutPresenter.workout;
			
			//reset date
			model.setDate(null);
			//set routine id if set
			if(routine != null) {
				model.setRoutineId(routine.getId());
				model.setDayInRoutine(day);
			}
			
			//add workout
			rpcService.addWorkout(model, new MyAsyncCallback<WorkoutModel>() {
				@Override
				public void onSuccess(WorkoutModel result) {
					display.setContentEnabled(true);

					//fire event
					fireEvent(new WorkoutCreatedEvent(result));
				}
			});
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Moves workout to date
	 */
	protected void moveWorkoutToDate(List<WorkoutModel> workouts) {
		try {
			display.setContentEnabled(false);
			
			//set date
			for(WorkoutModel model : workouts) {
			  model.setDate(Functions.trimDateToDatabase(date, true));
			}
			
			//add workout
			rpcService.addWorkouts(workouts, new MyAsyncCallback<List<WorkoutModel>>() {
				@Override
				public void onSuccess(List<WorkoutModel> result) {
					display.setContentEnabled(true);
					
					stop();
					
					List<WorkoutModel> workouts = result;

					//fire events
					for(WorkoutModel workout : workouts) {
						workout.setDate(Functions.trimDateToDatabase(date, true));
						
						//fire event
						fireEvent(new WorkoutCreatedEvent(workout));
					}
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}


	/**
	 * Called when workouts is selected (or selection is set off)
	 * <br>Shows button to quick select workouts
	 * @param workout
	 * @param selected
	 */
	protected void setQuickSelection(WorkoutModel workout, boolean selected) {
		//if new workout
		if(selected) {
			quickSelectionWorkouts.add(workout);
    }
		else {
			quickSelectionWorkouts.remove(workout);
    }
		
		//only if models selected and date not null
		display.setQuickSelectionButtonVisible(quickSelectionWorkouts.size() > 0 && date != null, date);
	}


	/**
	 * Shows main view
	 *  - my workouts
	 *  - most popular
	 */
	protected void showMainView() {

		lastView = 0;
		
		//show/hide buttons
		display.setBackButtonVisible(false);
		display.setCopyButtonVisible(false);
		display.setMoveToDateButtonVisible(false, null);
		display.setMoveToRoutineButtonVisible(false, null);
		display.setQuickSelectionButtonVisible(false, null);
		
		if(singleWorkoutPresenter != null) {
			singleWorkoutPresenter.stop();
    }
		singleWorkoutPresenter = null;
		if(workoutsListSearchPresenter != null) {
			workoutsListSearchPresenter.hide();
    }

		//run main view
		workoutsListSubPresenter.run(display.getDataContainer());
			
	}

	/*
	 * VIEW 3 (single workout
	 */
	protected void showSingleWorkout(WorkoutModel m) {

		try {
			if(singleWorkoutPresenter != null) {
				singleWorkoutPresenter.stop();
      }
			
			//hide main page
			if(workoutsListSubPresenter != null) {
				workoutsListSubPresenter.hide();
      }
			if(workoutsListSearchPresenter != null) {
				workoutsListSearchPresenter.hide();
      }
			
			//show single workout (clear exercises)
			singleWorkoutPresenter = new WorkoutPresenter(rpcService, eventBus, (WorkoutDisplay)GWT.create(WorkoutView.class), m);
			singleWorkoutPresenter.run(display.getDataContainer());
			
			//buttons
			display.setBackButtonVisible(true);
			display.setCopyButtonVisible(false);
			display.setMoveToDateButtonVisible(false, null);
			display.setMoveToRoutineButtonVisible(false, null);
			display.setQuickSelectionButtonVisible(false, null);

			//show buttons only if workout's id set
			if(m.getId() > 0) {
				//to routine
				if(routine != null) {
					display.setMoveToRoutineButtonVisible(true, routine);
				}
				//if date set
				else if(date != null) {
					display.setMoveToDateButtonVisible(true, date);
				}
				else {
					//copy to our workouts
					if(!m.getUid().equals(AppController.User.getUid())) {
						display.setCopyButtonVisible(true);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads workouts
	 */
	void loadSearch(final String query) {

		lastView = 1;
		
		//show/hide buttons
		display.setBackButtonVisible(true);
		display.setCopyButtonVisible(false);
		display.setMoveToDateButtonVisible(false, null);
		display.setMoveToRoutineButtonVisible(false, null);
		display.setQuickSelectionButtonVisible(false, null);
		
		if(singleWorkoutPresenter != null) {
			singleWorkoutPresenter.stop();
    }
		singleWorkoutPresenter = null;
		if(workoutsListSubPresenter != null) {
			workoutsListSubPresenter.hide();
    }
		
		//if not already loaded
		if(workoutsListSearchPresenter == null || !query.equals(lastQuery)) {
			lastQuery  = query;
			if(workoutsListSearchPresenter != null) {
				workoutsListSearchPresenter.stop();
      }

			boolean quickSelectionEnabled = (date != null);
			workoutsListSearchPresenter = new WorkoutsListSearchPresenter(rpcService, eventBus, (WorkoutsListSearchDisplay)GWT.create(WorkoutsListSearchView.class), query, quickSelectionEnabled);
			workoutsListSearchPresenter.run(display.getDataContainer());
		}
		else {
			workoutsListSearchPresenter.run(display.getDataContainer());
    }
	}
}
