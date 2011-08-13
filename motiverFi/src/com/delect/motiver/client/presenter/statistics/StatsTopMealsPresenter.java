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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.MealModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class StatsTopMealsPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class StatsTopMealsDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setMealsData(List<MealModel> meals);
	}
	private Date dateEnd;

	private Date dateStart;
	
	private StatsTopMealsDisplay display;
	//child presenters
	private EmptyPresenter emptyPresenter;

	/**
	 * Top meals 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public StatsTopMealsPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, StatsTopMealsDisplay display, Date dateStart, Date dateEnd) {
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

		//EVENT: dates changed
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				dateStart = event.getDate();
				dateEnd = event.getDateEnd();
				load();
			}
		});
	}

	@Override
	public void onRun() {
	    
    load();
	}

	@Override
	public void onStop() {

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
	}

	/**
	 * Loads data from server
	 */
	private void load() {

		display.setMealsData(null);

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
		
    //get meals
		rpcService.getStatisticsTopMeals(Functions.trimDateToDatabase(dateStart, true), Functions.trimDateToDatabase(dateEnd, true), new MyAsyncCallback<List<MealModel>>() {
			@Override
			public void onSuccess(List<MealModel> result) {
				
				List<MealModel> meals = result;

				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				
				if(meals.size() > 0) {
					display.setMealsData(meals);
				}
				//nothing found
				else {
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoData());
					emptyPresenter.run(display.getBodyContainer());
				}
			}
		});
	}

}
