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
package com.delect.motiver.client.presenter.nutrition;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.presenter.DateWeekSelectorPresenter;
import com.delect.motiver.client.presenter.DateWeekSelectorPresenter.DateWeekSelectorDisplay;
import com.delect.motiver.client.presenter.NotePanelPresenter;
import com.delect.motiver.client.presenter.NotePanelPresenter.NotePanelDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.nutrition.MealsListPresenter.MealsListDisplay;
import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter.NutritionDayDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.DateWeekSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.NotePanelView;
import com.delect.motiver.client.view.nutrition.MealsListView;
import com.delect.motiver.client.view.nutrition.NutritionDayView;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Nutrition page
 *  - calendar
 *  - current days foods
 *  - meals
 */
public class NutritionPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class NutritionPageDisplay extends Display {
		public abstract LayoutContainer getCalendarContainer();
	}
	private Date date = new Date();

	private DateWeekSelectorPresenter dateSelectorPresenter;
	private NutritionPageDisplay display;
	//if some meal is open as default (zero if not)
	private long mid = 0;
	private NotePanelPresenter notePanelMeals;
	
	//child presenters
	private NotePanelPresenter notePanelToday;

	private NutritionDayPresenter todayNutritionPresenter;
	
	public NutritionPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NutritionPageDisplay display, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
		this.date = (date != null)? date : new Date();

    dateSelectorPresenter = new DateWeekSelectorPresenter(rpcService, eventBus, (DateWeekSelectorDisplay)GWT.create(DateWeekSelectorView.class));
    notePanelToday = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
    notePanelMeals = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
	    
    todayNutritionPresenter = new NutritionDayPresenter(rpcService, eventBus, (NutritionDayDisplay)GWT.create(NutritionDayView.class), AppController.User.getUid(), this.date);
 	}
	  
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		mid = 0;
		
		//check if workout/routine id in token
		try {
			String token = History.getToken();
			if(token.matches("user/nutrition/[0-9]*/.*")) {
				String[] arr = token.split("/");
				final String str = arr[arr.length - 1];
				//if meal
				if(str.contains("m")) {
					mid = Long.parseLong(str.replace("m", ""));
		    }
			}
		} catch (NumberFormatException e) {
      Motiver.showException(e);
		}
		
		//check date
		if(mid == 0) {
			History.newItem("user/nutrition/" + (date.getTime() / 1000), false);
    }
		
		//calendar
		dateSelectorPresenter.setParameters(date, false, true);
		
		//EVENT: reload view when date changes
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				date = event.getDate();
				reloadView();
			}
		});
	}


	@Override
	public void onRefresh() {
		//refresh childs
		if(dateSelectorPresenter != null) {
			dateSelectorPresenter.run(display.getBaseContainer());
    }
		
		if(notePanelToday != null) {
			notePanelToday.run(display.getBaseContainer());
    }
		
		if(notePanelMeals != null) {
			notePanelMeals.run(display.getBaseContainer());
    }
	}


	@Override
	public void onRun() {
		
    //calendar
    dateSelectorPresenter.run(display.getCalendarContainer());
	    
    //today
    notePanelToday.run(display.getBaseContainer());
    //add meal list to notepanel
    notePanelToday.setTitle(CommonUtils.getDateString(date, true, false));
    todayNutritionPresenter.setFloatingEnabled();
    notePanelToday.addNewPresenter(todayNutritionPresenter);
    notePanelToday.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Time().toLowerCase()), todayNutritionPresenter.NewTimeListener);
		if(mid == 0) {
			notePanelToday.showContent();
    }
	    
    //meals
    notePanelMeals.run(display.getBaseContainer());
    //add meal list to notepanel
    final MealsListPresenter mealsListPresenter = new MealsListPresenter(rpcService, eventBus, (MealsListDisplay)GWT.create(MealsListView.class), null, mid);
    notePanelMeals.setTitle(AppController.Lang.MyMeals());
    notePanelMeals.addNewPresenter(mealsListPresenter);
    //open if meal is open as default
		if(mid != 0) {
			notePanelMeals.showContent();
    }

	}

	@Override
	public void onStop() {
		
		if(dateSelectorPresenter != null) {
			dateSelectorPresenter.stop();
    }
		
		if(notePanelToday != null) {
			notePanelToday.stop();
    }
		
		if(notePanelMeals != null) {
			notePanelMeals.stop();
    }
	}

	/**
	 * Reloads view. Called when date changes
	 */
	protected void reloadView() {

		//check date
		History.newItem("user/nutrition/" + (date.getTime() / 1000), false);
		
		//today
		notePanelToday.setTitle(CommonUtils.getDateString(date, true, false));
	    
	}

}
