/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.statistics;

import java.util.Date;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.presenter.DatesSelectorPresenter;
import com.delect.motiver.client.presenter.DatesSelectorPresenter.DatesSelectorDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.statistics.StatsExerciseHistoryPresenter.StatsExerciseHistoryDisplay;
import com.delect.motiver.client.presenter.statistics.StatsTopExercisesPresenter.StatsTopExercisesDisplay;
import com.delect.motiver.client.presenter.statistics.StatsTrainingDaysPresenter.StatsTrainingDaysDisplay;
import com.delect.motiver.client.presenter.statistics.StatsTrainingTimesPresenter.StatsTrainingTimesDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.DatesSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.statistics.StatsExerciseHistoryView;
import com.delect.motiver.client.view.statistics.StatsTopExercisesView;
import com.delect.motiver.client.view.statistics.StatsTrainingDaysView;
import com.delect.motiver.client.view.statistics.StatsTrainingTimesView;
import com.delect.motiver.shared.Constants;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Measurement page
 *  - measurements (targets & graph)
 */
public class TrainingStatisticsPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class TrainingStatisticsDisplay extends Display {
    public abstract LayoutContainer getDatesContainer();
		public abstract LayoutContainer getBodyContainer();
	}
	
  private TrainingStatisticsDisplay display;
	
	private Date dateStart;
  private Date dateEnd;

  private DatesSelectorPresenter datesSelectorPresenter;

  private StatsTopExercisesPresenter statsTopExercisesPresenter;

  private StatsTrainingTimesPresenter statsTrainingTimesPresenter;

  private StatsTrainingDaysPresenter statsTrainingDaysPresenter;

  private StatsExerciseHistoryPresenter statsExerciseHistoryPresenter;
	

	/**
	 * Statistics page
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public TrainingStatisticsPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, TrainingStatisticsDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {		
		//EVENT: dates changed
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				dateStart = event.getDate();
				dateEnd = event.getDateEnd();
			}
		});
	}
	
	@Override
	public void onRun() {
    
    //date selector
    datesSelectorPresenter = new DatesSelectorPresenter(rpcService, eventBus, (DatesSelectorDisplay)GWT.create(DatesSelectorView.class), Constants.DAYS_DIFF_MAX_STATS, Constants.DAYS_INDEX_STATS);
    dateStart = datesSelectorPresenter.getDateStart();
    dateEnd = datesSelectorPresenter.getDateEnd();
    datesSelectorPresenter.run(display.getDatesContainer());
    
    //top 10
    statsTopExercisesPresenter = new StatsTopExercisesPresenter(rpcService, eventBus, (StatsTopExercisesDisplay)GWT.create(StatsTopExercisesView.class), dateStart, dateEnd);
    statsTopExercisesPresenter.run(display.getBodyContainer());
  
    //times
    statsTrainingTimesPresenter = new StatsTrainingTimesPresenter(rpcService, eventBus, (StatsTrainingTimesDisplay)GWT.create(StatsTrainingTimesView.class), dateStart, dateEnd);
    statsTrainingTimesPresenter.run(display.getBodyContainer());
    
    //days
    statsTrainingDaysPresenter = new StatsTrainingDaysPresenter(rpcService, eventBus, (StatsTrainingDaysDisplay)GWT.create(StatsTrainingDaysView.class), dateStart, dateEnd);
    statsTrainingDaysPresenter.run(display.getBodyContainer());
    
    //history
    statsExerciseHistoryPresenter = new StatsExerciseHistoryPresenter(rpcService, eventBus, (StatsExerciseHistoryDisplay)GWT.create(StatsExerciseHistoryView.class), dateStart, dateEnd);
    statsExerciseHistoryPresenter.run(display.getBodyContainer());
  }


	@Override
	public void onStop() {
	  if(datesSelectorPresenter != null) {
	    datesSelectorPresenter.stop();
	  }
    if(statsTopExercisesPresenter != null) {
      statsTopExercisesPresenter.stop();
    }
    if(statsTrainingTimesPresenter != null) {
      statsTrainingTimesPresenter.stop();
    }
    if(statsTrainingDaysPresenter != null) {
      statsTrainingDaysPresenter.stop();
    }
    if(statsExerciseHistoryPresenter != null) {
      statsExerciseHistoryPresenter.stop();
    }
	}

}
