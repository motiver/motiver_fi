/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.statistics;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.training.SingleExerciseHistoryPresenter;
import com.delect.motiver.client.presenter.training.SingleExerciseHistoryPresenter.SingleExerciseHistoryDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.training.SingleExerciseHistoryView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseNameModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class StatsExerciseHistoryPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class StatsExerciseHistoryDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setExercisesData(List<ExerciseNameModel> exercises);
		public abstract void setHandler(StatsExerciseHistoryHandler statsExerciseHistoryHandler);
	}

	public interface StatsExerciseHistoryHandler {
		void query(String query, AsyncCallback<List<ExerciseNameModel>> callback);	//called when user search for exercises (names)
		void selected(ExerciseNameModel model);
	};
	private Date dateEnd;

	private Date dateStart;
	private StatsExerciseHistoryDisplay display;

	//child presenters
	private SingleExerciseHistoryPresenter lastWeightsPresenter;
	private ExerciseNameModel model = null;

	/**
	 * History for single exercise
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public StatsExerciseHistoryPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, StatsExerciseHistoryDisplay display, Date dateStart, Date dateEnd) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.dateStart = dateStart;
    this.dateEnd = dateEnd;	
	}
	

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		display.setHandler(new StatsExerciseHistoryHandler() {
			@Override
			public void query(String query,	final AsyncCallback<List<ExerciseNameModel>> callback) {
				rpcService.searchExerciseNames(query, Constants.LIMIT_SEARCH_NAMES, new MyAsyncCallback<List<ExerciseNameModel>>() {
					@Override
					public void onSuccess(List<ExerciseNameModel> result) {
						callback.onSuccess(result);
          }
				});
			}

			@Override
			public void selected(ExerciseNameModel m) {
				
				//if model changed
				if(model == null || m.getId() != model.getId()) {
					model = m;
					showWeights();
				}
			}
		});
		
		//EVENT: dates changed
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				dateStart = event.getDate();
				dateEnd = event.getDateEnd();

				showWeights();
			}
		});
	}

	@Override
	public void onStop() {
		if(lastWeightsPresenter != null) {
			lastWeightsPresenter.stop();
    }
	}

	/**
	 * Shows weights (launchs lastWeightPresenter)
	 */
	protected void showWeights() {
		
		try {
			if(lastWeightsPresenter != null) {
				lastWeightsPresenter.stop();
      }
			
			if(model != null) {
				lastWeightsPresenter = new SingleExerciseHistoryPresenter(rpcService, eventBus, (SingleExerciseHistoryDisplay)GWT.create(SingleExerciseHistoryView.class), model.getId(), dateStart, dateEnd, -1);
				lastWeightsPresenter.run(display.getBodyContainer());
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
