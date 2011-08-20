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
