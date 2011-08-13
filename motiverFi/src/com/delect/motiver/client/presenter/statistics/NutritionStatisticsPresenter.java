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
import com.delect.motiver.client.presenter.statistics.StatsNutritionDayHistoryPresenter.StatsNutritionDayHistoryDisplay;
import com.delect.motiver.client.presenter.statistics.StatsTopMealsPresenter.StatsTopMealsDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.DatesSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.statistics.StatsNutritionDayHistoryView;
import com.delect.motiver.client.view.statistics.StatsTopMealsView;
import com.delect.motiver.shared.Constants;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Measurement page
 *  - measurements (targets & graph)
 */
public class NutritionStatisticsPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class NutritionStatisticsDisplay extends Display {
    public abstract LayoutContainer getDatesContainer();
		public abstract LayoutContainer getBodyContainer();
	}
	
  private NutritionStatisticsDisplay display;
	
	private Date dateStart;
  private Date dateEnd;

  private DatesSelectorPresenter datesSelectorPresenter;
  private StatsTopMealsPresenter statsTopMealsPresenter;
  private StatsNutritionDayHistoryPresenter statsNutritionDayHistoryPresenter;
	

	/**
	 * Statistics page
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public NutritionStatisticsPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NutritionStatisticsDisplay display) {
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
    statsTopMealsPresenter = new StatsTopMealsPresenter(rpcService, eventBus, (StatsTopMealsDisplay)GWT.create(StatsTopMealsView.class), dateStart, dateEnd);
    statsTopMealsPresenter.run(display.getBodyContainer());
    
    //times
    statsNutritionDayHistoryPresenter = new StatsNutritionDayHistoryPresenter(rpcService, eventBus, (StatsNutritionDayHistoryDisplay)GWT.create(StatsNutritionDayHistoryView.class), dateStart, dateEnd);
    statsNutritionDayHistoryPresenter.run(display.getBodyContainer());
  }


	@Override
	public void onStop() {
	  if(datesSelectorPresenter != null) {
	    datesSelectorPresenter.stop();
	  }
    if(statsTopMealsPresenter != null) {
      statsTopMealsPresenter.stop();
    }
    if(statsNutritionDayHistoryPresenter != null) {
      statsNutritionDayHistoryPresenter.stop();
    }
	}

}
