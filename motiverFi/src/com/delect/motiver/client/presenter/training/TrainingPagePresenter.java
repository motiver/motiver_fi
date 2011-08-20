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
package com.delect.motiver.client.presenter.training;

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
import com.delect.motiver.client.presenter.cardio.TodayCardioPresenter;
import com.delect.motiver.client.presenter.cardio.TodayCardioPresenter.TodayCardioDisplay;
import com.delect.motiver.client.presenter.training.RoutinesListPresenter.RoutinesListDisplay;
import com.delect.motiver.client.presenter.training.TrainingDayPresenter.TrainingDayDisplay;
import com.delect.motiver.client.presenter.training.WorkoutsListPresenter.WorkoutsListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.DateWeekSelectorView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.NotePanelView;
import com.delect.motiver.client.view.cardio.TodayCardioView;
import com.delect.motiver.client.view.training.RoutinesListView;
import com.delect.motiver.client.view.training.TrainingDayView;
import com.delect.motiver.client.view.training.WorkoutsListView;
import com.delect.motiver.shared.Functions;

/**
 * 
 * Training page
 *  - calendar
 *  - current days workouts
 *  - workouts, routines, exercises
 */
public class TrainingPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class TrainingPageDisplay extends Display {
	}
	private Date date = new Date();

	//child presenters
	private DateWeekSelectorPresenter dateSelectorPresenter;
	private TrainingPageDisplay display;
	private NotePanelPresenter notePanelRoutines;
	private NotePanelPresenter notePanelToday;
	private NotePanelPresenter notePanelWorkouts;
	private long rid = 0;

	private TodayCardioPresenter todayCardioPresenter;
	
	private TrainingDayPresenter todayTrainingPresenter;
	//if some workout/routine is open as default (zero if not)
	private long wid = 0;
	
	public TrainingPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, TrainingPageDisplay display, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
		this.date = (date != null)? date : new Date();

    dateSelectorPresenter = new DateWeekSelectorPresenter(rpcService, eventBus, (DateWeekSelectorDisplay)GWT.create(DateWeekSelectorView.class));
    notePanelToday = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
    notePanelWorkouts = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
    notePanelRoutines = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));

    todayTrainingPresenter = new TrainingDayPresenter(rpcService, eventBus, (TrainingDayDisplay)GWT.create(TrainingDayView.class), AppController.User.getUid(), this.date);
    todayCardioPresenter = new TodayCardioPresenter(rpcService, eventBus, (TodayCardioDisplay)GWT.create(TodayCardioView.class), AppController.User.getUid(), this.date);
	    
	}
	  
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		//check if workout/routine id in token
		wid = 0;
		rid = 0;
		try {
			String token = History.getToken();
			if(token.matches("user/training/[0-9]*/.*")) {
				String[] arr = token.split("/");
				final String str = arr[arr.length - 1];
				//if workout
				if(str.contains("w")) {
					wid = Long.parseLong(str.replace("w", ""));
	      }
				//if routine
				else if(str.contains("r")) {
					rid = Long.parseLong(str.replace("r", ""));
	      }
			}
		} catch (NumberFormatException e) {
      Motiver.showException(e);
		}
					
		//set token
		if(wid == 0 && rid ==  0 && date != null) {
			History.newItem("user/training/" + (date.getTime() / 1000), false);
    }
		
		//calendar
		dateSelectorPresenter.setParameters(date, true, false);
		
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
		if(dateSelectorPresenter != null) {
			dateSelectorPresenter.run(display.getBaseContainer());
    }
		if(notePanelToday != null) {
			notePanelToday.run(display.getBaseContainer());
    }
		if(notePanelWorkouts != null) {
			notePanelWorkouts.run(display.getBaseContainer());
    }
		if(notePanelRoutines != null) {
			notePanelRoutines.run(display.getBaseContainer());
    }
	}


	@Override
	public void onRun() {
	    
    //calendar
    dateSelectorPresenter.run(display.getBaseContainer());
	    
    //today's workouts
    notePanelToday.run(display.getBaseContainer());
    notePanelToday.setTitle(Functions.getDateString(date, true, false));
    notePanelToday.addNewPresenter(todayTrainingPresenter);
    notePanelToday.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Workout().toLowerCase()), todayTrainingPresenter.NewWorkoutListener);
    notePanelToday.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Routine().toLowerCase()), todayTrainingPresenter.NewRoutineListener);
    notePanelToday.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Cardio().toLowerCase()), todayTrainingPresenter.NewCardioListener);
    notePanelToday.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Run().toLowerCase()), todayTrainingPresenter.NewRunListener);
    //today's cardio
    notePanelToday.addNewPresenter(todayCardioPresenter);
		if(wid == 0 && rid ==  0) {
			notePanelToday.showContent();
    }
	    
    //workouts
    notePanelWorkouts.run(display.getBaseContainer());
    final WorkoutsListPresenter workoutsListPresenter = new WorkoutsListPresenter(rpcService, eventBus, (WorkoutsListDisplay)GWT.create(WorkoutsListView.class), null, wid);
    notePanelWorkouts.setTitle(AppController.Lang.Workouts());
    notePanelWorkouts.addNewPresenter(workoutsListPresenter);
    //open if workout is open as default
		if(wid != 0) {
			notePanelWorkouts.showContent();
    }
	    
    //routines
    notePanelRoutines.run(display.getBaseContainer());
    final RoutinesListPresenter routinesListPresenter = new RoutinesListPresenter(rpcService, eventBus, (RoutinesListDisplay)GWT.create(RoutinesListView.class), null, rid);
    notePanelRoutines.setTitle(AppController.Lang.Routines());
    notePanelRoutines.addNewPresenter(routinesListPresenter);
    //open if routine is open as default
		if(rid != 0) {
			notePanelRoutines.showContent();
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
		if(notePanelWorkouts != null) {
			notePanelWorkouts.stop();
    }
		if(notePanelRoutines != null) {
			notePanelRoutines.stop();
    }
	}

	/**
	 * Reloads view. Called when date changes
	 */
	protected void reloadView() {

		//check date
		History.newItem("user/training/" + (date.getTime() / 1000), false);
		
		//title
    notePanelToday.setTitle(Functions.getDateString(date, true, false));
	}

}
