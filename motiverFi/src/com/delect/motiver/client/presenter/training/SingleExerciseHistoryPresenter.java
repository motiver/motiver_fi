/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.training;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.shared.ExerciseModel;

/**
 * Shows history (sets, reps, weights) from single exercise (name)
 * @author Antti
 *
 */
public class SingleExerciseHistoryPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class SingleExerciseHistoryDisplay extends Display {
		public abstract void setLastWeights(List<ExerciseModel> result);
	}
	
	private Date dateEnd;
	private Date dateStart;

	private SingleExerciseHistoryDisplay display;
	private EmptyPresenter emptyPresenter;
	private long id = 0;
	private int limit;

	public SingleExerciseHistoryPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, SingleExerciseHistoryDisplay display, long id, Date dateStart, Date dateEnd, int limit) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.id = id;
    this.dateStart = dateStart;
    this.dateEnd = dateEnd;
    this.limit = limit;
	    
	}
	
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onRun() {
		loadExercises();
	}

	@Override
	public void onStop() {
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
	}

	/**
	 * Loads exercises from server
	 */
	@SuppressWarnings("deprecation")
	void loadExercises() {

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
		emptyPresenter.run(display.getBaseContainer());
	    
		//reset times
		if(dateStart != null) {
			dateStart.setHours(0);
			dateStart.setMinutes(0);
			dateStart.setSeconds(0);
		}
		if(dateEnd != null) {
			dateEnd.setHours(0);
			dateEnd.setMinutes(0);
			dateEnd.setSeconds(0);
		}
		
    //get exercises
		Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getExercisesFromName(id, dateStart, dateEnd, limit, new MyAsyncCallback<List<ExerciseModel>>() {
			@Override
			public void onSuccess(List<ExerciseModel> result) {

				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				
				//if no exercises found
				if(result.size() == 0) {
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoExercisesFound(), EmptyPresenter.OPTION_SMALLER);
					emptyPresenter.run(display.getBaseContainer());
				}
				else {
					display.setLastWeights(result);
	      }
			}
		});
		addRequest(req);
	}

}
