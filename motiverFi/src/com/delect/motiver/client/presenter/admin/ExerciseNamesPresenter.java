/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.admin;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.shared.ExerciseNameModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Shows exercises names in list where admins can edit/delete those
 */
public class ExerciseNamesPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ExerciseNamesDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setExercises(List<ExerciseNameModel> exercises);
		public abstract void setHandler(ExerciseNamesHandler handler);
	}
	public interface ExerciseNamesHandler {
		/**
		 * Combines names together
		 * @param firstId : where other IDs are combined
		 * @param ids : other IDs
		 */
		void combineNames(Long firstId, Long[] ids);
		void saveName(ExerciseNameModel model);
		void search(String query);
	}
	private ExerciseNamesDisplay display;

	//child presenters
	private EmptyPresenter emptyPresenter;

	String lastQuery = "";

	/**
	 * Shows exercises names in list where admins can edit/delete those 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public ExerciseNamesPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ExerciseNamesDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}

	@Override
	public Display getView() {
		return display;
	}
	@Override
	public void onBind() {
		
		display.setHandler(new ExerciseNamesHandler() {
			@Override
			public void combineNames(Long firstId, Long[] ids) {
				final Request req = rpcService.combineExerciseNames(firstId, ids, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						//refresh
						onRun();
					}
				});
				addRequest(req);
			}
			@SuppressWarnings("unchecked")
      @Override
			public void saveName(ExerciseNameModel model) {
				final Request req = rpcService.updateExerciseName(model, MyAsyncCallback.EmptyCallback);
				addRequest(req);
			}
			@Override
			public void search(String query) {
				searchExercises(query);
			}
		});
	}


	@Override
	public void onRun() {

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());

		//fetch exercises
		final Request req = rpcService.searchExerciseNames("", 200, new MyAsyncCallback<List<ExerciseNameModel>>() {
			@Override
			public void onSuccess(List<ExerciseNameModel> result) {
				if(emptyPresenter != null) {
          emptyPresenter.stop();
        }
				
				display.setExercises(result);
			}
		});
		addRequest(req);
	}

	@Override
	public void onStop() {
		
		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
	}

	protected void searchExercises(String query) {

		if(query == null) {
		  query = "";
		}
		
		if(lastQuery.equals(query)) {
		  return;
		}
		
		lastQuery = query;
		
		//fetch foods
		final Request req = rpcService.searchExerciseNames(query, 200, new MyAsyncCallback<List<ExerciseNameModel>>() {
			@Override
			public void onSuccess(List<ExerciseNameModel> result) {
				if(emptyPresenter != null) {
          emptyPresenter.stop();
        }
				
				display.setExercises(result);
			}
		});
		addRequest(req);
	}

}
