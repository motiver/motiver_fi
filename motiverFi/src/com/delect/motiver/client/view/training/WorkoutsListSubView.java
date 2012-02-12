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
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.training.WorkoutsListSubPresenter;
import com.delect.motiver.client.presenter.training.WorkoutsListSubPresenter.WorkoutsListSubHandler;
import com.delect.motiver.client.view.MyContentPanel;
import com.delect.motiver.client.view.widget.MyButton;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class WorkoutsListSubView extends WorkoutsListSubPresenter.WorkoutsListSubDisplay {

	private WorkoutsListSubHandler handler;
	private MyContentPanel panelMostPopular = new MyContentPanel();

	//panels
	private MyContentPanel panelMyWorkouts = new MyContentPanel();

	public WorkoutsListSubView() {

		//VIEW 1
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
    this.setLayout(tl);
    TableData td = new TableData();
    td.setVerticalAlign(VerticalAlignment.TOP);
    td.setWidth("50%");
		
		//my workouts
    panelMyWorkouts.addStyleName("panel-workouts-my");
		panelMyWorkouts.setHeading(AppController.Lang.MyWorkouts());
		panelMyWorkouts.setStyleAttribute("margin-right", "5px");
		this.add(panelMyWorkouts, td);
		//most popular
		panelMostPopular.addStyleName("panel-workouts-mostpopular");
		panelMostPopular.setHeading(AppController.Lang.MostPopular());
		this.add(panelMostPopular, td);
		
		//new workout button
		MyButton btnAddWorkout = new MyButton(AppController.Lang.CreateTarget(AppController.Lang.Workout().toLowerCase()));
		btnAddWorkout.setScale(ButtonScale.MEDIUM);
		btnAddWorkout.setId("btn-add-workout");
		btnAddWorkout.setColor(MyButton.Style.GREEN);
		btnAddWorkout.setStyleAttribute("margin-top", "10px");
		btnAddWorkout.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.createWorkout("");
			}
		});
		this.add(btnAddWorkout);
		
	}

	@Override
	public Widget asWidget() {
		
    //		showView(0);
		
		return this;
	}

	@Override
	public LayoutContainer getMostPopularContainer() {
		return panelMostPopular;
	}

	@Override
	public LayoutContainer getMyWorkoutsContainer() {
		return panelMyWorkouts;
	}

	@Override
	public void setContentEnabled(boolean enabled) {
		this.setEnabled(enabled);
	}

	@Override
	public void setHandler(WorkoutsListSubHandler handler) {
		this.handler = handler;
	}
	
}
