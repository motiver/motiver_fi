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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.ExerciseCreatedEvent;
import com.delect.motiver.client.event.ExerciseRemovedEvent;
import com.delect.motiver.client.event.ExerciseUpdatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.training.ExerciseNameEditorPresenter.ExerciseNameEditorDisplay;
import com.delect.motiver.client.presenter.training.ExerciseNameEditorPresenter.ExerciseNameEditorHandler;
import com.delect.motiver.client.presenter.training.SingleExerciseHistoryPresenter.SingleExerciseHistoryDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.training.ExerciseNameEditorView;
import com.delect.motiver.client.view.training.SingleExerciseHistoryView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Shows single exercise
 * @author Antti
 *
 */
public class ExercisePresenter extends Presenter implements Comparable<ExercisePresenter> {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ExerciseDisplay extends Display {
		public abstract ExerciseModel getExercise();
		public abstract void setHandler(ExerciseHandler exerciseHandler);
		public abstract void setModel(ExerciseModel exercise);
		public abstract void setNameComboEnabled(boolean b);
	}
	public interface ExerciseHandler {
		void exerciseRemoved();
		void nameChanged(String newName);	//when new name is typed
		void query(String query, AsyncCallback<List<ExerciseNameModel>> callback);	//called when user search for exercises (names)
		void saveData(ExerciseModel mode, boolean nameChanged);
		void showLastWeights();
		void showVideo();
	}

	private ExerciseDisplay display;

	//child presenters
	private ExerciseNameEditorPresenter exerciseNameEditorPresenter;
	private String lastQuery = "";

	private SingleExerciseHistoryPresenter lastWeightsPresenter;
	protected ExerciseModel exercise;

	public ExercisePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ExerciseDisplay display, ExerciseModel exercise, WorkoutModel workout) {
		super(rpcService, eventBus);
		this.display = display;
	    
		exercise.setWorkout(workout);
    this.exercise = exercise;

	}
	
	@Override
	public int compareTo(ExercisePresenter compare) {
		return exercise.compareTo(compare.exercise);
	}


	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		
//		//if new exercise -> set uid to our
//		if(exercise.getId() == 0) {
//			exercise.setUid(AppController.User.getUid());
//    }

		display.setModel(exercise);
		if(exercise.getId() != 0) {
			
			display.setContentEnabled(true);
						
			//event handler (fire event)
			display.setHandler(new ExerciseHandler() {
				@Override
				public void exerciseRemoved() {
					display.setContentEnabled(false);
					
					List<ExerciseModel> list = new ArrayList<ExerciseModel>();
					list.add(exercise);
					final Request req = rpcService.removeExercises(list, new MyAsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean ok) {
							
							stop();

							if(ok) {
								eventBus.fireEvent(new ExerciseRemovedEvent(exercise));
								
							}
						}
					});
					addRequest(req);
				}

				@Override
				public void nameChanged(String newName) {
					
					//if presenter already visible -> cancel
					if(exerciseNameEditorPresenter != null) {
						return;
		      }

					//hide last weights
					if(lastWeightsPresenter != null) {
						lastWeightsPresenter.stop();
						lastWeightsPresenter = null;
					}
					
					//disable combo
					display.setNameComboEnabled(false);
					
					//create dummy model
					ExerciseNameModel model = new ExerciseNameModel(0L, newName, 0);
					//new name typed -> launch NewExerciseNamePresenter
					exerciseNameEditorPresenter = new ExerciseNameEditorPresenter(rpcService, eventBus, (ExerciseNameEditorDisplay)GWT.create(ExerciseNameEditorView.class), model);
					exerciseNameEditorPresenter.run(display.getBaseContainer());

					//refresh view when name set / edit cancelled
					exerciseNameEditorPresenter.setHandler(new ExerciseNameEditorHandler() {
						@Override
						public void editCancelled() {
							
							//enable combo
							display.setNameComboEnabled(true);
							
							//reset model
							display.setModel(exercise);
							
							exerciseNameEditorPresenter = null;
						}
						@Override
						public void nameSaved(ExerciseNameModel model) {
							
							//enable combo
							display.setNameComboEnabled(true);
							
							exerciseNameEditorPresenter = null;
							exercise.setName(model);
							
							//update exercise
							updateExercise(true);
						
						}
					});
				}

				@Override
				public void query(String query, final AsyncCallback<List<ExerciseNameModel>> callback) {
          
          //parse query name (transfer equipment's name to index)
          for(int i=0; i < AppController.LangConstants.Targets().length; i++) {
            String t = AppController.LangConstants.Targets()[i];
            query = query.replaceAll(t, "--" + i + "--");
            query = query.replaceAll(t.toLowerCase(), "--" + i + "--");
          }
              
          //trim
          final String queryTrimmed = query.trim();
      
          //save query
          lastQuery  = queryTrimmed;
      

          Motiver.setNextCallCacheable(true);
					final Request req = rpcService.searchExerciseNames(queryTrimmed, Constants.LIMIT_SEARCH_NAMES, new MyAsyncCallback<List<ExerciseNameModel>>() {
						@Override
						public void onSuccess(List<ExerciseNameModel> result) {
							//only if last query
							if(queryTrimmed.equals(lastQuery)) {
								callback.onSuccess(result);
				      }
						}
					});
					addRequest(req);
				}

				@Override
				public void saveData(ExerciseModel model, boolean nameChanged) {

					exercise = model;
					updateExercise(nameChanged);
					
					//if name changed -> refresh last weights
					if(nameChanged && lastWeightsPresenter != null) {
						lastWeightsPresenter.stop();
						lastWeightsPresenter = null;
						showLastWeightsPresenter();
					}
				}

				@Override
				public void showLastWeights() {
					showLastWeightsPresenter();
				}

				@Override
				public void showVideo() {
					//open video in new window
					if(exercise.getName() != null) {
						String url = exercise.getName().getVideo();
						if(!url.contains("http://")) {
							url = "http://" + url;
			      }
						Window.open(url, "Video for " + exercise.getName().getName(), "");
					}
					
				}
			});
		}
		
	}

	@Override
	public void onRun() {
	    
    //if no exercise -> create new one
    if(exercise.getId() == 0) {
			
      display.setContentEnabled(false);
			final Request req = rpcService.addExercise(exercise, new MyAsyncCallback<ExerciseModel>() {
				@Override
				public void onSuccess(ExerciseModel result) {
					
					if(result != null) {
            display.setContentEnabled(true);
				    	
						//set data
						exercise.setId(result.getId());
						
						//fire event
						eventBus.fireEvent(new ExerciseCreatedEvent(exercise));
						
						//refresh
						display.setModel(exercise);
						run(display.getBaseContainer());
					}
				}
			});
      addRequest(req);
    }
  }

	@Override
	public void onStop() {

		if(exerciseNameEditorPresenter != null) {
			exerciseNameEditorPresenter.stop();
    }
		
		if(lastWeightsPresenter != null) {
			lastWeightsPresenter.stop();
    }
	}

	/**
	 * Shows last weights presenter. Hides presenter if it is already visible
	 */
	protected void showLastWeightsPresenter() {
		if(exercise.getName() != null) {
			if(lastWeightsPresenter == null) {
				//if no date -> show exercise before today
				Date date = (exercise.getDate() != null)? exercise.getDate() : new Date();
				//remove one day
				date = new Date( date.getTime() - 1000 * 3600 * 24 );
				lastWeightsPresenter = new SingleExerciseHistoryPresenter(rpcService, eventBus, (SingleExerciseHistoryDisplay)GWT.create(SingleExerciseHistoryView.class), exercise.getName().getId(), null, date, Constants.LIMIT_LAST_WEIGHTS);
				lastWeightsPresenter.run(display.getBaseContainer());
			}
			else {
				lastWeightsPresenter.stop();
				lastWeightsPresenter = null;
			}
		}	
	}


	/**
	 * Updates exercise from server based to model-variable
	 * Fires ExerciseUpdatedEvent
	 */
	protected void updateExercise(final boolean nameChanged) {
		
		//save date from exercise
		final Date date = exercise.getDate();

		final Request req = rpcService.updateExercise(exercise, new MyAsyncCallback<ExerciseModel>() {
			@Override
			public void onSuccess(ExerciseModel result) {
				
				if(result != null) {
					//set data
					result.setWorkoutId(exercise.getWorkoutId());
					
					//update model
					exercise = result;
					//restore date
					exercise.setDate(date);
					
					if(nameChanged) {
						display.setModel(exercise);
		      }
					
				}
			}
		});
		addRequest(req);
		
		eventBus.fireEvent(new ExerciseUpdatedEvent(exercise));
	}

}
