/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.presenter.NotePanelPresenter;
import com.delect.motiver.client.presenter.NotePanelPresenter.NotePanelDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.admin.ExerciseNamesPresenter.ExerciseNamesDisplay;
import com.delect.motiver.client.presenter.admin.FoodNamesPresenter.FoodNamesDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.NotePanelView;
import com.delect.motiver.client.view.admin.ExerciseNamesView;
import com.delect.motiver.client.view.admin.FoodNamesView;

/**
 * 
 * Admin page
 *  - ??
 */
public class AdminPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class AdminPageDisplay extends Display {
		
	}
	private AdminPageDisplay display;

	//child presenters
	private NotePanelPresenter notePanelExercises;
	private NotePanelPresenter notePanelFoods;

	/**
	 * Admin page
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public AdminPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, AdminPageDisplay display) {
		super(rpcService, eventBus);
		this.display = display;

    notePanelExercises = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
    notePanelFoods = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
				
		//set token
		History.newItem("user/admin/", false);
		
	}
	
	@Override
	public void onRun() {
	    
    //today
    notePanelExercises.run(display.getBaseContainer());
    //add meal list to notepanel
    notePanelExercises.setTitle("Exercises");
    ExerciseNamesPresenter exerciseNamesPresenter = new ExerciseNamesPresenter(rpcService, eventBus, (ExerciseNamesDisplay)GWT.create(ExerciseNamesView.class));
    notePanelExercises.addNewPresenter(exerciseNamesPresenter);
	    
    //workouts
    notePanelFoods.run(display.getBaseContainer());
    //add meal list to notepanel
    FoodNamesPresenter foodNamesPresenter = new FoodNamesPresenter(rpcService, eventBus, (FoodNamesDisplay)GWT.create(FoodNamesView.class));
    notePanelFoods.setTitle("Foods");
    notePanelFoods.addNewPresenter(foodNamesPresenter);
  }

	@Override
	public void onStop() {

		if(notePanelExercises != null) {
      notePanelExercises.stop();
    }

		if(notePanelFoods != null) {
      notePanelFoods.stop();
    }
	}

}
