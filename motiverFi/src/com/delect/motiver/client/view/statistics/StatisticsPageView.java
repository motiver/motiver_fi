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
