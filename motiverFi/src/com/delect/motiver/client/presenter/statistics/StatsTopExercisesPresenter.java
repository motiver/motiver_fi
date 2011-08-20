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
package com.delect.motiver.client.presenter.statistics;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

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
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class StatsTopExercisesPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class StatsTopExercisesDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setExercisesData(List<ExerciseNameModel> list);
	}
	private Date dateEnd;

	private Date dateStart;
	
	private StatsTopExercisesDisplay display;
	//child presenters
	private EmptyPresenter emptyPresenter;

	/**
	 * Top exercises 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public StatsTopExercisesPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, StatsTopExercisesDisplay display, Date dateStart, Date dateEnd) {
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

		//clear data
		display.setExercisesData(null);
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
		
    //get exercises
		final Request req = rpcService.getStatisticsTopExercises(Functions.trimDateToDatabase(dateStart, true), Functions.trimDateToDatabase(dateEnd, true), new MyAsyncCallback<List<ExerciseNameModel>>() {
			@Override
			public void onSuccess(List<ExerciseNameModel> result) {
				
				List<ExerciseNameModel> list = result;

				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				
				if(list.size() > 0) {
					display.setExercisesData(list);
				}
				//nothing found
				else {
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoData());
					emptyPresenter.run(display.getBodyContainer());
				}
			}
		});
		addRequest(req);
	}

}
