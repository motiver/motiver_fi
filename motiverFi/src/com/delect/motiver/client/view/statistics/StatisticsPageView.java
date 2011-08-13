/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.statistics;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.statistics.StatisticsPagePresenter;
import com.delect.motiver.client.presenter.statistics.StatisticsPagePresenter.StatisticsPageHandler;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

public class StatisticsPageView extends StatisticsPagePresenter.StatisticsPageDisplay {

	private StatisticsPageHandler handler;
	
	public StatisticsPageView() {
	  
	}
	
	@Override
	public Widget asWidget() {

	  //menu items
	  addMenuItem(AppController.Lang.Training(), true, new Listener<BaseEvent>() {
	    @Override
	    public void handleEvent(BaseEvent be) {
	      handler.onMenuClicked(0);
	    }
	  });
    addMenuItem(AppController.Lang.Nutrition(), false, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {        
        handler.onMenuClicked(1);
      }
    });
    addMenuItem(AppController.Lang.MonthlyReport(), false, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.onMenuClicked(2);
      }
    });
    
    
		return this;
	}

	@Override
	public void setHandler(StatisticsPageHandler handler) {
		this.handler = handler;
	}

}
