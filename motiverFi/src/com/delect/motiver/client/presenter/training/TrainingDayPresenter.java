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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.RoutineCreatedEvent;
import com.delect.motiver.client.event.RoutineRemovedEvent;
import com.delect.motiver.client.event.RoutineSelectCancelledEvent;
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.WorkoutSelectCancelledEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.RoutineCreatedEventHandler;
import com.delect.motiver.client.event.handler.RoutineRemovedEventHandler;
import com.delect.motiver.client.event.handler.RoutineSelectCancelledEventHandler;
import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutSelectCancelledEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.AddNewCardioValuePresenter;
import com.delect.motiver.client.presenter.cardio.AddNewCardioValuePresenter.AddNewCardioValueDisplay;
import com.delect.motiver.client.presenter.cardio.AddNewRunValuePresenter;
import com.delect.motiver.client.presenter.cardio.AddNewRunValuePresenter.AddNewRunValueDisplay;
import com.delect.motiver.client.presenter.training.EmptyTrainingDayPresenter.EmptyTrainingDayDisplay;
import com.delect.motiver.client.presenter.training.RoutinesListPresenter.RoutinesListDisplay;
import com.delect.motiver.client.presenter.training.WorkoutPresenter.WorkoutDisplay;
import com.delect.motiver.client.presenter.training.WorkoutsListPresenter.WorkoutsListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.cardio.AddNewCardioValueView;
import com.delect.motiver.client.view.cardio.AddNewRunValueView;
import com.delect.motiver.client.view.training.EmptyTrainingDayView;
import com.delect.motiver.client.view.training.RoutinesListView;
import com.delect.motiver.client.view.training.WorkoutView;
import com.delect.motiver.client.view.training.WorkoutsListView;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * <pre>
 * Init one day in calendar (training)
 *  - workouts
 *  - Add workout button
 *  - Add routine button
 *  </pre>
 */
public class TrainingDayPresenter extends Presenter {

	public interface TodayTrainingHandler {
		void newCardio();
		void newRoutine();
		void newRun();
		void newWorkout();
	}
	
	/**
	* Abstract class for view to extend
	*/
	public abstract static class TrainingDayDisplay extends Display {
		public abstract void setHandler(TodayTrainingHandler todayTrainingHandler);
	}
	
	//handlers
	public Listener<BaseEvent> NewCardioListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewCardio();
		}
	};
	public Listener<BaseEvent> NewRoutineListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewRoutine();
		}
	};
	//new run handler
	public Listener<BaseEvent> NewRunListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewRun();
		}
	};
	//new time handler
	public Listener<BaseEvent> NewWorkoutListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewWorkout();
		}
	};
	
	private AddNewCardioValuePresenter addNewCardioValuePresenter;
	private AddNewRunValuePresenter addNewRunValuePresenter;
	private Date date;
	private TrainingDayDisplay display;
	private Presenter emptyPresenter;
	private RoutinesListPresenter routinesListPresenter;
	private String uid;
	private List<Presenter> workoutPresenters = new ArrayList<Presenter>();
	private WorkoutsListPresenter workoutsListPresenter;
	
	public TrainingDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, TrainingDayDisplay display, String uid, Date date) {
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

		display.setHandler(new TodayTrainingHandler() {
			@Override
			public void newCardio() {
				addNewCardio();
			}
			@Override
			public void newRoutine() {
				addNewRoutine();
			}
			@Override
			public void newRun() {
				addNewRun();
			}
			@Override
			public void newWorkout() {
				addNewWorkout();
			}
		});
		
		//EVENT: reload view when date changes
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
			  if(event.getDate() != null) {
			    date = event.getDate();
			  }
        loadWorkouts();
			}
		});
		//EVENT: workout created -> refresh
		addEventHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {
			@Override
			public void onWorkoutCreated(WorkoutCreatedEvent event) {
				//if this date
				if(event.getWorkout() != null) {
					if(event.getWorkout().getDate() != null) {
						if(Functions.Fmt.format(event.getWorkout().getDate()).equals( Functions.Fmt.format(date) )) {
							WorkoutPresenter workout = new WorkoutPresenter(rpcService, eventBus, (WorkoutDisplay)GWT.create(WorkoutView.class), event.getWorkout());
							addNewPresenter(workout);
						}
					}
				}
			}
		});
		//EVENT: workout removed
		addEventHandler(WorkoutRemovedEvent.TYPE, new WorkoutRemovedEventHandler() {
			@Override
			public void onWorkoutRemoved(WorkoutRemovedEvent event) {
				//if workout in calendar
				if(event.getWorkout() != null && event.getWorkout().getDate() != null) {
          removePresenter(event.getWorkout());
	      }
			}
		});
		//EVENT: new routine created -> refresh view
		addEventHandler(RoutineCreatedEvent.TYPE, new RoutineCreatedEventHandler() {
			@Override
			public void onRoutineCreated(RoutineCreatedEvent event) {
				final RoutineModel routine = event.getRoutine();

				if(routine.getDate() != null) {
					loadWorkouts();
	      }
			}			
		});
		//EVENT: routine removed
		addEventHandler(RoutineRemovedEvent.TYPE, new RoutineRemovedEventHandler() {
			@Override
			public void onRoutineRemoved(RoutineRemovedEvent event) {
				//if routine in calendar
				if(event.getRoutine() != null && event.getRoutine().getDate() != null) {
          removePresenter(event.getRoutine());
	      }
			}
		});
		//EVENT: workout selection cancelled
		addEventHandler(WorkoutSelectCancelledEvent.TYPE, new WorkoutSelectCancelledEventHandler() {
			@Override
			public void onCancel(WorkoutSelectCancelledEvent event) {
				//unbind list IF this date
				if(event.getDate() != null) {
					if(Functions.Fmt.format(event.getDate()).equals(Functions.Fmt.format(date))) {
						if(workoutsListPresenter != null) {
							workoutsListPresenter.stop();
			      }
						workoutsListPresenter = null;
						checkIfEmptyPresenterNeeded();
					}
				}
			}
		});
		//EVENT: routine selection cancelled
		addEventHandler(RoutineSelectCancelledEvent.TYPE, new RoutineSelectCancelledEventHandler() {
			@Override
			public void onCancel(RoutineSelectCancelledEvent event) {
				//unbind list IF this date
				if(event.getDate() != null) {
					if(Functions.Fmt.format(event.getDate()).equals(Functions.Fmt.format(date))) {
						if(routinesListPresenter != null) {
							routinesListPresenter.stop();
			      }
						routinesListPresenter = null;
						checkIfEmptyPresenterNeeded();
					}
				}
			}
		});
	}
	@Override
	public void onRefresh() {

		if(emptyPresenter != null) {
			emptyPresenter.run(display.getBaseContainer());
    }
		
		if(workoutPresenters != null) {
			for(int i=0; i < workoutPresenters.size(); i++) {
				final Presenter presenter = workoutPresenters.get(i);
				if(presenter != null) {
					presenter.run(display.getBaseContainer());
				}
			}		
		}
		//stop dialogs
		if(addNewCardioValuePresenter != null) {
			addNewCardioValuePresenter.stop();
    }
		if(addNewRunValuePresenter != null) {
			addNewRunValuePresenter.stop();
    }
	}
	@Override
	public void onRun() {
	    
    loadWorkouts();
  }
		
	@Override
	public void onStop() {

		//unbind presenters
		unbindPresenters();

		if(workoutsListPresenter != null) {
			workoutsListPresenter.stop();
    }
		if(routinesListPresenter != null) {
			routinesListPresenter.stop();
    }
		if(addNewCardioValuePresenter != null) {
			addNewCardioValuePresenter.stop();
    }
		if(addNewRunValuePresenter != null) {
			addNewRunValuePresenter.stop();
    }
	}


	/**
	 * Checks how many workouts are and shows empty presenter if needed
	 */
	private void checkIfEmptyPresenterNeeded() {

		//if no workouts/foods -> show empty presenter
		if(workoutPresenters.size() == 0) {
			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			
			if(uid.equals(AppController.User.getUid())) {
				emptyPresenter = new EmptyTrainingDayPresenter(rpcService, eventBus, (EmptyTrainingDayDisplay)GWT.create(EmptyTrainingDayView.class));
      }
			else {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
      }
			emptyPresenter.run(display.getBaseContainer());
		}
	}

	/**
	 * Loads workouts from server
	 */
	private void loadWorkouts() {

		if(workoutsListPresenter != null) {
			workoutsListPresenter.stop();
    }
		workoutsListPresenter = null;
		if(routinesListPresenter != null) {
			routinesListPresenter.stop();
    }
		routinesListPresenter = null;
		unbindPresenters();
		
		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBaseContainer());
		
    //fetch workouts for given day
    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getWorkoutsInCalendar(uid, Functions.trimDateToDatabase(date, true), Functions.trimDateToDatabase(date, true), new MyAsyncCallback<List<WorkoutModel[]>>() {
			@Override
			public void onSuccess(List<WorkoutModel[]> result) {
			  //convert to list
				if(result != null && result.size() > 0) {					
					List<WorkoutModel> list = new ArrayList<WorkoutModel>();
					for(WorkoutModel m : result.get(0)) {
					  list.add(m);
					}
					showWorkouts(list);
				}
			}
		});
		addRequest(req);
	}
	

	/**
	 * Removes presenter from view
	 * @param routine
	 */
	private void removePresenter(RoutineModel routine) {

		try {
			//remove also from presenters
			for(int i=0; i < workoutPresenters.size(); i++) {
				Presenter presenter = workoutPresenters.get(i);
				if(presenter != null && presenter instanceof RoutinePresenter) {
          if( ((RoutinePresenter)presenter).routine.getId() == routine.getId()) {
            workoutPresenters.remove(presenter);
          }
				}
			}

			checkIfEmptyPresenterNeeded();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Removes presenter from view
	 * @param workout
	 */
	private void removePresenter(WorkoutModel workout) {

		try {
			//remove also from presenters
			for(int i=0; i < workoutPresenters.size(); i++) {
				Presenter presenter = workoutPresenters.get(i);
				if(presenter != null) {
					//workout
					if(presenter instanceof WorkoutPresenter) {
						if( ((WorkoutPresenter)presenter).workout.getId() == workout.getId()) {
							workoutPresenters.remove(presenter);
						}
					}
				}
				
			}

			checkIfEmptyPresenterNeeded();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Shows workout in content (view)
	 * @param models : workout models to show
	 */
	private void showWorkouts(List<WorkoutModel> list) {
		
		try {

			unbindPresenters();

			//if no workouts
			if(list.size() == 0) {
				//only if selection not visible
				if(workoutsListPresenter == null && routinesListPresenter == null) {
					if(uid.equals(AppController.User.getUid())) {
						emptyPresenter = new EmptyTrainingDayPresenter(rpcService, eventBus, (EmptyTrainingDayDisplay)GWT.create(EmptyTrainingDayView.class));
		      }
					else {
						emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoWorkouts());
		      }
					emptyPresenter.run(display.getBaseContainer());
				}
			}
			else {
				
				for(WorkoutModel m : list) {
					//init new workoutPresenter
					final WorkoutPresenter wp = new WorkoutPresenter(rpcService, eventBus, (WorkoutDisplay)GWT.create(WorkoutView.class), m);
					addNewPresenter(wp);
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
		
		try {
      if(emptyPresenter != null) {
      	emptyPresenter.stop();
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
    } catch (Exception e) {
      Motiver.showException(e);
    }
	}

	/**
	 * Shows add new cardio presenter
	 */
	protected void addNewCardio() {
		
		if(addNewCardioValuePresenter != null) {
			addNewCardioValuePresenter.stop();
    }
		addNewCardioValuePresenter = new AddNewCardioValuePresenter(rpcService, eventBus, (AddNewCardioValueDisplay)GWT.create(AddNewCardioValueView.class), null, date);
		addNewCardioValuePresenter.run(display.getBaseContainer());
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(Presenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		workoutPresenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}

	/**
	 * Adds dummy routine
	 */
	protected void addNewRoutine() {
		
		//stop empty presenter
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		
		routinesListPresenter = new RoutinesListPresenter(rpcService, eventBus, (RoutinesListDisplay)GWT.create(RoutinesListView.class), date, 0L);
		routinesListPresenter.run(display.getBaseContainer());
	}
	/**
	 * Shows add new run presenter
	 */
	protected void addNewRun() {

		if(addNewRunValuePresenter != null) {
			addNewRunValuePresenter.stop();
    }
		addNewRunValuePresenter = new AddNewRunValuePresenter(rpcService, eventBus, (AddNewRunValueDisplay)GWT.create(AddNewRunValueView.class), null, date);
		addNewRunValuePresenter.run(display.getBaseContainer());
	}

	/**
	 * Fires SelectWorkout event
	 */
	protected void addNewWorkout() {
		
		//stop empty presenter
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }

		workoutsListPresenter = new WorkoutsListPresenter(rpcService, eventBus, (WorkoutsListDisplay)GWT.create(WorkoutsListView.class), date, 0L);
		workoutsListPresenter.run(display.getBaseContainer());
	}
	
}
