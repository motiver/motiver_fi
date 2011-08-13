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
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.WorkoutSelectCancelledEvent;
import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutSelectCancelledEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.training.WorkoutPresenter.WorkoutDisplay;
import com.delect.motiver.client.presenter.training.WorkoutsListPresenter.WorkoutsListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.training.WorkoutView;
import com.delect.motiver.client.view.training.WorkoutsListView;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single day in routine
 * @author Antti
 *
 */
public class RoutineDayPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RoutineDayDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setDay(int day);
		public abstract void setHandler(RoutineDayHandler routineDayHandler);
		public abstract void setModel(RoutineModel routine);
	}

	public interface RoutineDayHandler {
		void newWorkout();
	}
	private int day;

	private RoutineDayDisplay display;
	private EmptyPresenter emptyPresenter;
	private RoutineModel routine;

	private List<WorkoutPresenter> workoutPresenters = new ArrayList<WorkoutPresenter>();
	private List<WorkoutModel> workouts;
	//child presenters
	private WorkoutsListPresenter workoutsListPresenter;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param routine : which routine this belong
	 * @param day : 1-x
	 * @param workouts : workouts in this day
	 */
	public RoutineDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RoutineDayDisplay display, RoutineModel routine, int day, List<WorkoutModel> workouts) {
		super(rpcService, eventBus);
		this.display = display;
	    
		this.routine = routine;
		this.day = day;
    this.workouts = workouts;
	}


	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		display.setModel(routine);
		display.setDay(day);
		display.setHandler(new RoutineDayHandler() {
			@Override
			public void newWorkout() {
				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				if(workoutsListPresenter != null) {
					workoutsListPresenter.stop();
	      }
				
				workoutsListPresenter = new WorkoutsListPresenter(rpcService, eventBus, (WorkoutsListDisplay)GWT.create(WorkoutsListView.class), routine, day);
				workoutsListPresenter.run(display.getBodyContainer());
			}
		});

		//EVENT: new workout created. If added to this routine -> refresh
		addEventHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {
			@Override
			public void onWorkoutCreated(WorkoutCreatedEvent event) {
				//if correct day add new presenter
				if(event.getWorkout().getDayInRoutine() == day) {
					if(workoutsListPresenter != null) {
						workoutsListPresenter.stop();
		      }
					addNewPresenter(event.getWorkout());
				}
			}
		});
		//EVENT: workout removed
		addEventHandler(WorkoutRemovedEvent.TYPE, new WorkoutRemovedEventHandler() {
			@Override
			public void onWorkoutRemoved(WorkoutRemovedEvent event) {
				if(workoutPresenters.size() == 0) {
					return;
	      }
				
				//if correct day add new presenter
				if(event.getWorkout().getDayInRoutine() == day) {
					for(WorkoutPresenter wp : workoutPresenters) {
						if(wp.workout.getId() == event.getWorkout().getId()) {
							wp.stop();
							workoutPresenters.remove(wp);
						}
						break;
					}

					//if no workouts
					if(workoutPresenters.size() == 0) {
						if(emptyPresenter != null) {
							emptyPresenter.stop();
			      }
						emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
						emptyPresenter.run(display.getBodyContainer());						
					}
				}
			}
		});
		
		//EVENT: workout selection cancelled -> show empty presenter if no workouts
		addEventHandler(WorkoutSelectCancelledEvent.TYPE, new WorkoutSelectCancelledEventHandler() {
			@Override
			public void onCancel(WorkoutSelectCancelledEvent event) {
				//if no workouts
				if(workoutPresenters.size() == 0 && event.getSource().equals(workoutsListPresenter)) {
					if(emptyPresenter != null) {
						emptyPresenter.stop();
		      }
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
					emptyPresenter.run(display.getBodyContainer());						
				}
			}
		});
	}


	@Override
	public void onRun() {
		showWorkouts();
	}
	
	@Override
	public void onStop() {
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(workoutsListPresenter != null) {
			workoutsListPresenter.stop();
    }

		if(workoutPresenters != null) {
			for(int i=0; i < workoutPresenters.size(); i++) {
				final Presenter presenter = workoutPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			workoutPresenters.clear();
		}
		
	}

	/**
	 * Shows workouts
	 * @param index
	 */
	private void showWorkouts() {
		
		//no workouts
		if(workouts.size() == 0) {
			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
			emptyPresenter.run(display.getBodyContainer());
		}
		else {
			for(WorkoutModel m : workouts) {
				addNewPresenter(m);
			}
		}
	}

	/**
	 * Adds new presenter to view
	 * @param day in routine
	 * @param presenter
	 */
	protected void addNewPresenter(WorkoutModel workout) {
		
		try {
			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }

			WorkoutPresenter presenter = new WorkoutPresenter(rpcService, eventBus, (WorkoutDisplay)GWT.create(WorkoutView.class), workout);
			workoutPresenters.add(presenter);
			presenter.run(display.getBodyContainer());
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
