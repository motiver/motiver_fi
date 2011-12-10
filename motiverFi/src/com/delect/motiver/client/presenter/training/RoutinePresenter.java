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
import com.delect.motiver.client.event.RoutineCreatedEvent;
import com.delect.motiver.client.event.RoutineRemovedEvent;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.training.RoutineDayPresenter.RoutineDayDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.client.view.training.RoutineDayView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single routine & all the workouts it contains
 *
 */
public class RoutinePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RoutineDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getUserContainer();
		public abstract void setAddButtonsVisible(boolean b);
		public abstract void setHandler(RoutineHandler routineHandler);
		public abstract void setModel(RoutineModel routine);
	}

	public interface RoutineHandler {
		void routineRemoved();
		void saveData(RoutineModel model);
		void saveDays(boolean dayAdded);
	}
	public interface RoutineSelectedHandler {
		void select(RoutineModel model);
	}
	private CommentsBoxPresenter commentsPresenter;

	private RoutineDisplay display;
	private Presenter[] emptyDayPresenters = null;
	private EmptyPresenter emptyPresenter;
	//child presenters
	private List<RoutineDayPresenter> routineDayPresenters = new ArrayList<RoutineDayPresenter>();
	private ShowMorePresenter showMorePresenter;
	private UserPresenter userPresenter;
	
	protected RoutineModel routine;


	public RoutinePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RoutineDisplay display, RoutineModel routine) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.routine = routine;
		
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(routine);
		
		emptyDayPresenters = new Presenter[routine.getDays()];		    
		
		display.setHandler(new RoutineHandler() {
			@Override
			public void routineRemoved() {
				
				display.setContentEnabled(false);
				
				//remove routine and fire RoutineRemovedEvent
				rpcService.removeRoutine(routine, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						display.setContentEnabled(true);
						
						stop();
						
						//fire event
						eventBus.fireEvent(new RoutineRemovedEvent(routine));
					}
				});
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveData(RoutineModel model) {
				
				//if cancelled adding new measurement
				if(model == null) {
					stop();
					
					//fire event
					eventBus.fireEvent(new RoutineRemovedEvent(routine));
				}
				//added new measurement
				else if(model.getId() == 0) {
					routine = model;
					display.setContentEnabled(false);
					
					//create model
					rpcService.addRoutine(model, new MyAsyncCallback<RoutineModel>() {
						@Override
						public void onSuccess(RoutineModel result) {
							display.setContentEnabled(true);
							routine = result;

							refresh();
							
							//fire event
							final RoutineCreatedEvent event = new RoutineCreatedEvent(routine);
							eventBus.fireEvent(event);
						}
					});
				}
				//edited old value
				else {
					routine = model;
					rpcService.updateRoutine(routine, MyAsyncCallback.EmptyCallback);
				}
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveDays(boolean dayAdded) {
				
				if(dayAdded) {
					routine.setDays( routine.getDays() + 1 );
					
					//add new presenter
					final RoutineDayPresenter presenter = new RoutineDayPresenter(rpcService, eventBus, (RoutineDayDisplay)GWT.create(RoutineDayView.class), routine, routine.getDays(), new ArrayList<WorkoutModel>());
					addNewPresenter(presenter);
				}
				else {
					routine.setDays( routine.getDays() - 1 );
					
					//remove last day
					RoutineDayPresenter rdp = routineDayPresenters.get(routineDayPresenters.size() - 1);
					rdp.stop();
					routineDayPresenters.remove(rdp);
				}
				
				rpcService.updateRoutine(routine, MyAsyncCallback.EmptyCallback);
			}
		});
	}
	
	@Override
	public void onRun() {
    if(routine.getId() != 0) {
      loadWorkouts();
    }
    //no model -> hightlight
    else {
      highlight();
    }
	}


	@Override
	public void onStop() {
		
		//stop workout presenters
		unbindPresenters();

		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		if(userPresenter != null) {
			userPresenter.stop();
    }
	}


	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(commentsPresenter != null) {
			commentsPresenter.stop();
    }
		
		if(routineDayPresenters != null) {
			for(int i=0; i < routineDayPresenters.size(); i++) {
				final Presenter presenter = routineDayPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			routineDayPresenters.clear();
		}
		if(emptyDayPresenters != null) {
			for(int i=0; i < emptyDayPresenters.length; i++) {
				final Presenter presenter = emptyDayPresenters[i];
				if(presenter != null) {
					presenter.stop();
					emptyDayPresenters[i] = null;
				}
			}				
		}
	}


	/**
	 * Adds new presenter to view
	 * @param day in routine
	 * @param presenter
	 */
	protected void addNewPresenter(RoutineDayPresenter presenter) {
		
		try {			
			routineDayPresenters.add(presenter);
			presenter.run(display.getBodyContainer());
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads workouts to this routine
	 */
	protected void loadWorkouts() {

	  //if workouts already found
	  if(routine.getWorkouts().size() > 0) {
	    showWorkouts(0, routine.getWorkouts());
	    return;
	  }
	  
		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
		emptyPresenter.run(display.getBodyContainer());
		
		//fetch workouts
		Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getWorkouts(0, routine, new MyAsyncCallback<List<WorkoutModel>>() {
			@Override
			public void onSuccess(List<WorkoutModel> workouts) {
				showWorkouts(0, workouts);
      }
		});
		addRequest(req);
		
		//hide add buttons
		display.setAddButtonsVisible(false);
	}

	/**
	 * Shows workouts
	 */
	protected void showWorkouts(final int index, final List<WorkoutModel> workouts) {

		long dur = (new Date()).getTime();

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		if(index == 0) {
			unbindPresenters();
    }

		//userview
		if(userPresenter != null) {
			userPresenter.stop();
    }
		
		//show user if not our workout
		if(!routine.getUser().equals(AppController.User) && index == 0) {
			userPresenter = new UserPresenter(rpcService, eventBus, (UserDisplay) GWT.create(UserView.class), routine.getUser(), false);
			userPresenter.run(display.getUserContainer());
		}
				
		//each day
		int day = 0;
		for(day = 1; day <= routine.getDays(); day++) {

			//if null value -> list was limited -> add showMorePresenter
			if(day - index > Constants.LIMIT_ROUTINE_DAYS) {
				showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
					@Override
					public void showMore() {
						showWorkouts(index + Constants.LIMIT_ROUTINE_DAYS, workouts);								
					}
				});
				showMorePresenter.run(display.getBodyContainer());
				break;
			}
			else if(day >= index + 1) {
				
				//find this day's workouts
				List<WorkoutModel> daysWorkouts = new ArrayList<WorkoutModel>();
				for(int i = 0; i < workouts.size(); i++) {
					final WorkoutModel workout = workouts.get(i);
					//correct day
					if(workout.getDayInRoutine() == day) {
						daysWorkouts.add(workout);
					}
				}
				
				//add presenter
				final RoutineDayPresenter routineDay = new RoutineDayPresenter(rpcService, eventBus, (RoutineDayDisplay)GWT.create(RoutineDayView.class), routine, day, daysWorkouts);
				addNewPresenter(routineDay);
			}
		}
		
		//if all days visible -> show buttons
		if(day > routine.getDays()) {
			display.setAddButtonsVisible(true);
    }
		
		if(commentsPresenter != null) {
			commentsPresenter.stop();
    }
		commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), routine);
		commentsPresenter.run(display.getCommentsContainer());
	
    dur = (new Date()).getTime() - dur;
	}

}
