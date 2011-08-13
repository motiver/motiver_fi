/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.training;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.ExerciseNameModel;

/**
 * Edit (or add if model's ID is null) exercise name
 *
 */
public class ExerciseNameEditorPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ExerciseNameEditorDisplay extends Display {

		public abstract void setHandler(ExerciseNameEditorHandler exerciseNameEditorHandler);
		public abstract void setModel(ExerciseNameModel model);
	}
	public interface ExerciseNameEditorHandler {
		void editCancelled();
		void nameSaved(ExerciseNameModel model);
	}

	private ExerciseNameEditorDisplay display;

	private ExerciseNameEditorHandler handler;
	private ExerciseNameModel model;
	
	/**
	 * Edit (or add if model's ID is null) exercise name
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param model : name model
	 * @param exerciseId : exercise id if belongs to some exercise (0 if not)
	 */
	public ExerciseNameEditorPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ExerciseNameEditorDisplay display, ExerciseNameModel model) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.model = model;
	
    if(model == null) {
      return;
    }
	}
	  
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {	
		display.setModel(model);
		
		display.setHandler(new ExerciseNameEditorHandler() {

			@Override
			public void editCancelled() {
				stop();
				
				if(handler != null) {
					handler.editCancelled();
	      }
			}
			@Override
			public void nameSaved(ExerciseNameModel model) {
				rpcService.addExercisename(model, new MyAsyncCallback<ExerciseNameModel>() {
					@Override
					public void onSuccess(ExerciseNameModel result) {
						stop();
						
						if(result != null && handler != null) {
              handler.nameSaved(result);							
						}
					}
				});
			}
		});
	}

	/**
	 * Sets handler which is called when exercise name is created
	 * @param handler
	 */
	public void setHandler(ExerciseNameEditorHandler handler) {
		this.handler = handler;
	}

}
