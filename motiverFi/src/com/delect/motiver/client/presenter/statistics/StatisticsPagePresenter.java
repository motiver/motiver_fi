/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.statistics;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.statistics.MonthlyReportListPresenter.MonthlyReportListDisplay;
import com.delect.motiver.client.presenter.statistics.NutritionStatisticsPresenter.NutritionStatisticsDisplay;
import com.delect.motiver.client.presenter.statistics.TrainingStatisticsPresenter.TrainingStatisticsDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.statistics.MonthlyReportListView;
import com.delect.motiver.client.view.statistics.NutritionStatisticsView;
import com.delect.motiver.client.view.statistics.TrainingStatisticsView;
import com.delect.motiver.shared.Constants;

/**
 * 
 * Measurement page
 *  - measurements (targets & graph)
 */
public class StatisticsPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class StatisticsPageDisplay extends Display {
		public abstract void setHandler(StatisticsPageHandler statisticsPageHandler);
	}
	public interface StatisticsPageHandler {
	  /**
	   * Called when menu item is clicked
	   * @param index : 0=training, 1=nutrition, 2=monthly summary
	   */
	  void onMenuClicked(int index);
	}
	
  private StatisticsPageDisplay display;

  private Presenter presenter;
	private String target = "";


	/**
	 * Statistics page
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public StatisticsPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, StatisticsPageDisplay display, String target) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.target  = target;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new StatisticsPageHandler() {
      @Override
      public void onMenuClicked(int index) {
        switch(index) {
          case 0:
            target = "training";
            break;
          case 1:
            target = "nutrition";
            break;
          case 2:
            target = "summary";
            break;
        }
        
        showTarget();
      }
		});
	}
	
	@Override
	public void onRun() {
	  showTarget();
	}


	@Override
	public void onStop() {
	  if(presenter != null) {
	    presenter.stop();
	  }
	}

	/**
	 * Shows single target
	 * @param target
	 * @param hide : unbinds presenter when TRUE
	 */
	void showTarget() {
    
    History.newItem(Constants.TOKEN_STATISTICS+"/"+target, false);

    if(presenter != null) {
      presenter.stop();
    }
	  
	  if(target.length() == 0) {
	    target = "training";
	  }
	  
	  //training
	  if(target.startsWith("training")) {
	    
	    display.setSelectedMenuItem(0);
      
      presenter = new TrainingStatisticsPresenter(rpcService, eventBus, (TrainingStatisticsDisplay)GWT.create(TrainingStatisticsView.class));
	  }
    //nutrition
	  else if(target.startsWith("nutrition")) {
      
      display.setSelectedMenuItem(1);
      
      presenter = new NutritionStatisticsPresenter(rpcService, eventBus, (NutritionStatisticsDisplay)GWT.create(NutritionStatisticsView.class));
	  }
    //summary
    else if(target.startsWith("summary")) {
      
      display.setSelectedMenuItem(2);
      
      presenter = new MonthlyReportListPresenter(rpcService, eventBus, (MonthlyReportListDisplay)GWT.create(MonthlyReportListView.class));
    }
	  
	  if(presenter != null) {
	    presenter.run(display.getBaseContainer());
	  }
		
	}

}
