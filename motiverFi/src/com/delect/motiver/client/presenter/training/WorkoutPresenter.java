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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Timer;

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
import com.delect.motiver.client.presenter.PopupPresenter;
import com.delect.motiver.client.presenter.PopupPresenter.PopupDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.training.EmptyWorkoutPresenter.EmptyWorkoutDisplay;
import com.delect.motiver.client.presenter.training.ExercisePresenter.ExerciseDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.PopupView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.client.view.training.EmptyWorkoutView;
import com.delect.motiver.client.view.training.ExerciseView;
import com.delect.motiver.client.view.training.WorkoutView;
import com.delect.motiver.client.view.widget.PopupSize;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Single workout
 */
public class WorkoutPresenter extends Presenter {

  static PopupSize POPUP_WORKOUT = new PopupSize(800,500);
  
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
    void openNewWindow();
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

  protected Timer timerUpdate;

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
				  
				  //minus one, because orders start at zero
					newPos--;
					
					boolean found = false;
					//update order fields
					List<ExerciseModel> arr = new ArrayList<ExerciseModel>();
					for(int i=0; i < exercisePresenters.size(); i++) {
						Presenter presenter = exercisePresenters.get(i);
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
							
							//update exercise
							((ExercisePresenter)presenter).setModel(ex);
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
				showExercises();
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
					final Request req = rpcService.addWorkout(model, new MyAsyncCallback<WorkoutModel>() {
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
          addRequest(req);
				}
				//edited old value
				else {
					workout = model;
					
			    if(timerUpdate != null)
			      timerUpdate.cancel();
			    
			    timerUpdate = new Timer() {

			      @Override
			      public void run() {
			        
    			    rpcService.updateWorkout(workout, MyAsyncCallback.EmptyCallback);
			      }
			    };
			    timerUpdate.schedule(Constants.DELAY_MODEL_UPDATE);
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
      @Override
      public void openNewWindow() {
        PopupPresenter p = WorkoutPresenter.getWorkoutPopup(rpcService, eventBus, workout);
        p.run(display.getBodyContainer());
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
      showExercises();
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
			if(workout.getUser().equals(AppController.User)) {
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
    final ExercisePresenter fp = new ExercisePresenter(rpcService, eventBus, (ExerciseDisplay)GWT.create(ExerciseView.class), dummy, workout);
    addNewPresenter(fp, true);
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(ExercisePresenter presenter, boolean setOrder) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		//set order (last exercises order plus one)
		if(setOrder) {
		  presenter.exercise.setOrder( (exercisePresenters.size() == 0)? 0 : (exercisePresenters.get(exercisePresenters.size() - 1).exercise.getOrder() + 100) );
		}
		
		exercisePresenters.add(presenter);
		presenter.run(display.getBodyContainer());
		
		//show add button
		display.setAddButtonVisible( workout.getUser().equals(AppController.User) );
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

	protected void showExercises() {

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
				if(!workout.getUser().equals(AppController.User)) {
					userPresenter = new UserPresenter(rpcService, eventBus, (UserDisplay) GWT.create(UserView.class), workout.getUser(), false);
					userPresenter.run(display.getUserContainer());
				}
			}
			
			//if no workouts
			if(workout.getExercises().size() == 0) {
				if(workout.getUser().equals(AppController.User)) {
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
				Collections.sort(workout.getExercises());
				
				for(ExerciseModel m : workout.getExercises()) {
						
					//set date & id & order
					m.setWorkoutId(workout.getId());
					m.setDate(workout.getDate());
					m.setWorkout(workout);
					
					//init new exercisePresenter
					final ExercisePresenter fp = new ExercisePresenter(rpcService, eventBus, (ExerciseDisplay)GWT.create(ExerciseView.class), m, workout);
					addNewPresenter(fp, false);
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

	/**
	 * Returns popup presenter for workout
	 * @param rpcService
	 * @param eventBus
	 * @param workout
	 * @return
	 */
  protected static PopupPresenter getWorkoutPopup(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutModel workout) {
    PopupPresenter p = new PopupPresenter(rpcService, eventBus, (PopupDisplay)GWT.create(PopupView.class), new WorkoutPresenter(rpcService, eventBus, (WorkoutDisplay)GWT.create(WorkoutView.class), workout), POPUP_WORKOUT);
    p.setTitle(Functions.getDateString(workout.getDate(), true, false));
    return p;
  }

}
