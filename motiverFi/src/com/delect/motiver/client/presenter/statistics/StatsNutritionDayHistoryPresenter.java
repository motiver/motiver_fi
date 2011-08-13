/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.statistics;

import java.util.ArrayList;
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
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.NutritionDayModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class StatsNutritionDayHistoryPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class StatsNutritionDayHistoryDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setDaysData(List<NutritionDayModel> data);
	}
	private Date dateEnd;

	private Date dateStart;
	
	private StatsNutritionDayHistoryDisplay display;
	//child presenters
	private EmptyPresenter emptyPresenter;

	/**
	 * Training days
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public StatsNutritionDayHistoryPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, StatsNutritionDayHistoryDisplay display, Date dateStart, Date dateEnd) {
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
		
		display.setDaysData(null);

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
		
    //get days
		rpcService.getBlogData(0, 100, 2, Functions.trimDateToDatabase(dateStart, true), Functions.trimDateToDatabase(dateEnd, true), String.valueOf(AppController.User.getUid()), false, new MyAsyncCallback<List<BlogData>>() {
			@Override
			public void onSuccess(List<BlogData> list) {
				
				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				
				//check if days has data
				List<NutritionDayModel> values = new ArrayList<NutritionDayModel>();

				//go through backwards
				for(int i=list.size() - 1; i >= 0; i--) {
					BlogData bd = list.get(i);
					if(bd != null) {
						NutritionDayModel model = null;
						if(bd.getNutrition() != null) {
							model = bd.getNutrition();
							model.setDate(bd.getDate());
						}
						values.add(model);
					}
				}
				
				//no data
				if(values.size() == 0) {
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoData());
					emptyPresenter.run(display.getBodyContainer());
				}
				else {
					display.setDaysData(values);
	      }
			}
		});
	}

}
