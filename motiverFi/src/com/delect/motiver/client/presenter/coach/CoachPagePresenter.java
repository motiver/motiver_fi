/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.coach;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.event.CoachModeEvent;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.coach.TraineesListPresenter.TraineesListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.coach.TraineesListView;

/**
 * Page shown to coaches. Shows people who have selected this user as coach
 * <br>Fires {@link com.delect.motiver.event.CoachModeEvent CoachModeEvent} when coach mode is selected
 * @author Antti
 *
 */
public class CoachPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CoachPageDisplay extends Display {
	}
	private CoachPageDisplay display;

	//child presenters
	private TraineesListPresenter traineesListPresenter;


	public CoachPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CoachPageDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	    
    traineesListPresenter = new TraineesListPresenter(rpcService, eventBus, (TraineesListDisplay)GWT.create(TraineesListView.class));
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		History.newItem("user/coach", false);
		
		//EVENT: user selected
		addEventHandler(UserSelectedEvent.TYPE, new UserSelectedEventHandler() {
			@Override
			public void userSelected(UserSelectedEvent event) {
				//fire event
				fireEvent(new CoachModeEvent(event.getUser()));
			}
		});
		
	}

	@Override
	public void onRun() {
		
		traineesListPresenter.run(display.getBaseContainer());
	}

	@Override
	public void onStop() {
		if(traineesListPresenter != null) {
      traineesListPresenter.stop();
    }
	}

}
