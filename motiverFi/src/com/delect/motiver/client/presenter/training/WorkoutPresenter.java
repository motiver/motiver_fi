/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.training;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.ExerciseRemovedEvent;
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.WorkoutMovedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.handler.ExerciseRemovedEventHandler;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.training.EmptyWorkoutPresenter.EmptyWorkoutDisplay;
import com.delect.motiver.client.presenter.training.ExercisePresenter.ExerciseDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.client.view.training.EmptyWorkoutView;
import com.delect.motiver.client.view.training.ExerciseView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.UserModel;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Single workout
 */
public class WorkoutPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class WorkoutDisplay extends Display {
		public abstract LayoutContainer getBodyContainer();
		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getUserContainer();
		public abstract void setAddButtonVisible(boolean b);
		public abstract void setDropTarget(String string);
		public abstract void setHandler(WorkoutHandler workoutHandler);
		public abstract void setModel(WorkoutModel workout);
	}

	public interface WorkoutHandler {
		/**
		 * Called when exercise is dragged
		 * @param id
		 * @param newPos : position which exercise is dragged (BEFORE this position)
		 */
		void dragged(long id, int newPos);
		void exercisesHidden();
		void exercisesVisible();
		void newExercise();
		void saveData(WorkoutModel model);
		void workoutMoved(Date newDate);
		void workoutRemoved();
	}
	//new exercise listener
	public Listener<BaseEvent> NewExerciseListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewExercise();
		}
	};

	private CommentsBoxPresenter commentsPresenter;
	private Presenter emptyPresenter;
	private List<ExercisePresenter> exercisePresenters = new ArrayList<ExercisePresenter>();
	private UserPresenter userPresenter;
	
	protected WorkoutDisplay display;
	
	protected WorkoutModel workout;

	/**
	 * Shows single workout
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param workout : model, if null -> creates new
	 */
	public WorkoutPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutDisplay display, WorkoutModel workout) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.workout = workout;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		display.setModel(workout);

		display.setDropTarget(Constants.DRAG_GROUP_WORKOUT + workout.getId());
		
		//event handler (fire event)
		display.setHandler(new WorkoutHandler() {
			@SuppressWarnings("unchecked")
      @Override
			public void dragged(long id, int newPos) {
				try {
					
					boolean found = false;
					//update order fields
					List<ExerciseModel> arr = new ArrayList<ExerciseModel>();
					for(int i=1; i <= exercisePresenters.size(); i++) {
						Presenter presenter = exercisePresenters.get(i - 1);
						if(presenter != null) {
							final ExerciseModel ex = ((ExercisePresenter)presenter).exercise;
							//if dragged exercise
							if( ex.getId() == id) {
								ex.setOrder(newPos);
								found = true;
							}
							else if(found) {
								//if before new pos
								if(i <= newPos) {
									ex.setOrder( i - 1 );
					      }
								//if after new pos
								else {
									ex.setOrder( i );
					      }
							}
							else if(!found) {
								//if before new pos
								if(i < newPos) {
									ex.setOrder( i );
					      }
								//if after new pos
								else {
									ex.setOrder( i + 1 );
					      }
							}
							arr.add(ex);
						}
						
					}

					//sort presenters
					Collections.sort(exercisePresenters);
					
					//get id array
					Long[] ids = new Long[exercisePresenters.size()];
					for(int i=0; i < exercisePresenters.size(); i++) {
						final ExerciseModel ex = exercisePresenters.get(i).exercise;
						ids[i] = ex.getId();
					}
					
					//update server
					rpcService.updateExerciseOrder(workout, ids, MyAsyncCallback.EmptyCallback);

					//remove everything
					for(int i=0; i < exercisePresenters.size(); i++) {
						exercisePresenters.get(i).remove();
					}
				
					//re-add
					for(int i=0; i < exercisePresenters.size(); i++) {
						exercisePresenters.get(i).update(display.getBodyContainer());
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
			@Override
			public void exercisesHidden() {
				//stop presenters
				unbindPresenters();
			}
			@Override
			public void exercisesVisible() {
				loadExercises();
			}
			@Override
			public void newExercise() {
				addNewExercise();	
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveData(WorkoutModel model) {
				
				//if cancelled adding new measurement
				if(model == null) {
					stop();
					
					//fire event
					eventBus.fireEvent(new WorkoutRemovedEvent(workout));
				}
				//added new measurement
				else if(model.getId() == 0) {
					workout = model;
					display.setContentEnabled(false);
					
					//create model
					rpcService.addWorkout(model, new MyAsyncCallback<WorkoutModel>() {
						@Override
						public void onSuccess(WorkoutModel result) {
							display.setContentEnabled(true);
							workout = result;

							refresh();
							
							//fire event
							final WorkoutCreatedEvent event = new WorkoutCreatedEvent(workout);
							eventBus.fireEvent(event);
						}
					});
				}
				//edited old value
				else {
					workout = model;
					rpcService.updateWorkout(workout, MyAsyncCallback.EmptyCallback);
				}
			}
			@Override
			public void workoutMoved(final Date newDate) {
				try {
					display.getBaseContainer().setEnabled(false);

					workout.setDate(Functions.trimDateToDatabase(newDate, true));
					rpcService.updateWorkout(workout, new MyAsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							//fire events
							DateChangedEvent event = new DateChangedEvent(newDate);
							fireEvent(event);
							
							fireEvent(new WorkoutMovedEvent(workout));
							
						}
					});
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
			@Override
			public void workoutRemoved() {

				display.setContentEnabled(false);
				
				//remove workout and fire WorkoutRemovedEvent
				rpcService.removeWorkout(workout, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						display.setContentEnabled(true);
						
						stop();
						
						//fire event
						eventBus.fireEvent(new WorkoutRemovedEvent(workout));
					}
				});
			}
		});
		//EVENT: exercise removed
		addEventHandler(ExerciseRemovedEvent.TYPE, new ExerciseRemovedEventHandler() {
			@Override
			public void onExerciseRemoved(ExerciseRemovedEvent event) {
				checkExerciseEvent(event.getExercise(), 2);				
			}			
		});
	}

	@Override
	public void onRun() {

    if(workout.getId() != 0) {
      if(workout.getExercises() == null) {
        loadExercises();
      }
      else {
        showExercises(workout.getExercises());
      }
    }
    //no model -> hightlight
    else {
      highlight();
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
		if(userPresenter != null) {
			userPresenter.stop();
    }
		unbindPresenters();		
	}
	
	/**
	 * Removes presenter from view
	 * @param meal
	 */
	private void removePresenter(ExerciseModel exercise) {

		//remove also from presenters
		for(int i=0; i < exercisePresenters.size(); i++) {
			ExercisePresenter presenter = exercisePresenters.get(i);
			if(presenter != null && presenter.exercise.getId() == exercise.getId()) {
        exercisePresenters.remove(presenter);
      }
		}

		//if no exercises -> show empty presenter
		if(exercisePresenters.size() == 0) {
			if(workout.getUid().equals(AppController.User.getUid())) {
				emptyPresenter = new EmptyWorkoutPresenter(rpcService, eventBus, (EmptyWorkoutDisplay)GWT.create(EmptyWorkoutView.class));
      }
			else {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoExercises());
      }
			emptyPresenter.run(display.getBodyContainer());
			
			//hide add button
			display.setAddButtonVisible(false);
		}
	}
	
	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {

		if(exercisePresenters != null) {
			for(int i=0; i < exercisePresenters.size(); i++) {
				final Presenter presenter = exercisePresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			exercisePresenters.clear();
		}
	}


	/**
	 * Adds new exercise to workout
	 */
	protected void addNewExercise() {

		//create dummy exercise
		final ExerciseModel dummy = new ExerciseModel(new ExerciseNameModel(0L, "", 0));
		dummy.setWorkoutId(workout.getId());
		//init new foodpresenter
    final ExercisePresenter fp = new ExercisePresenter(rpcService, eventBus, (ExerciseDisplay)GWT.create(ExerciseView.class), dummy);
    addNewPresenter(fp);
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(ExercisePresenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		//set order (last exercises order plus one)
		presenter.exercise.setOrder( (exercisePresenters.size() == 0)? 0 : (exercisePresenters.get(exercisePresenters.size() - 1).exercise.getOrder() + 100) );
		
		exercisePresenters.add(presenter);
		presenter.run(display.getBodyContainer());
		
		//show add button
		display.setAddButtonVisible( workout.getUid().equals(AppController.User.getUid()) );
	}


	/**
	 * Handle exercise created/updated/removed event
	 * @param exercise
	 * @param target : 0=created, 1=updated, 2=removed
	 */
	protected void checkExerciseEvent(ExerciseModel exerciseUpdated, int target) {
		
		//if added to this time
		if(exerciseUpdated.getWorkoutId() != 0) {
			if(exerciseUpdated.getWorkoutId() == workout.getId()) {

				//check if belongs to any exercise
				for(int i=0; i < exercisePresenters.size(); i++) {
					ExercisePresenter presenter = exercisePresenters.get(i);
					if(presenter != null) {
						ExerciseModel e = presenter.exercise;
						if( e.getId() == exerciseUpdated.getId()) {

							//removed
							if(target == 2) {					
								removePresenter(e);
				      }
							break;
						}
					}
					
				}
			}			
		}
	}

	/**
	 * Loads exercises and names
	 */
	protected void loadExercises() {
		
		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());

		//hide add button
		display.setAddButtonVisible(false);
		
		//fetch exercises
		rpcService.getExercises(workout, new MyAsyncCallback<List<ExerciseModel>>() {
			@Override
			public void onSuccess(List<ExerciseModel> result) {
				showExercises(result);
      }
		});
		
	}

	protected void showExercises(List<ExerciseModel> result) {

		display.setContentEnabled(false);
		
		try {
			
			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }

			if(commentsPresenter != null) {
				commentsPresenter.stop();
      }
			unbindPresenters();

			//userview
			if(workout.getDate() == null && workout.getRoutineId() == 0) {
				if(userPresenter != null) {
					userPresenter.stop();
	      }
				
				//show user if not our workout
				if(!workout.getUid().equals(AppController.User.getUid())) {
					UserModel user = new UserModel();
					user.setUid(workout.getUid());
					userPresenter = new UserPresenter(rpcService, eventBus, (UserDisplay) GWT.create(UserView.class), user, false);
					userPresenter.run(display.getUserContainer());
				}
			}
			
			//if no workouts
			if(result.size() == 0) {
				if(workout.getUid().equals(AppController.User.getUid())) {
					emptyPresenter = new EmptyWorkoutPresenter(rpcService, eventBus, (EmptyWorkoutDisplay)GWT.create(EmptyWorkoutView.class));
	      }
				else {
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoExercises());
	      }
				emptyPresenter.run(display.getBodyContainer());
				
				//hide add button
				display.setAddButtonVisible(false);
			}
			else {
				//sort
				Collections.sort(result);
				
				for(ExerciseModel m : result) {
						
					//set date & id & order
					m.setWorkoutId(workout.getId());
					m.setDate(workout.getDate());
					
					//init new exercisePresenter
					final ExercisePresenter fp = new ExercisePresenter(rpcService, eventBus, (ExerciseDisplay)GWT.create(ExerciseView.class), m);
					addNewPresenter(fp);
				}
			}

			//show user & comments (if not in any routine)
			if(workout.getDate() != null || workout.getRoutineId() == 0) {
				
				if(commentsPresenter != null) {
					commentsPresenter.stop();
	      }
				commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), workout);
				commentsPresenter.run(display.getCommentsContainer());
			}
		    
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		display.setContentEnabled(true);
	
	}

}
