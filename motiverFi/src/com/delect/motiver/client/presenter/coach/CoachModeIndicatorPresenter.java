/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.coach;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.event.CoachModeEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.UserModel;

/**
 * Shows coach mode indicator with possibility to end coach mode
 * <br>Fires {@link com.delect.motiver.event.CoachModeEvent CoachModeEvent} (with null user object) when coach mode is ended and stop itself
 * @author Antti
 *
 */
public class CoachModeIndicatorPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CoachModeIndicatorDisplay extends Display {
		public abstract void setHandler(CoachModeIndicatorHandler coachModeIndicatorHandler);
		public abstract void setUser(UserModel user);
	}

	public interface CoachModeIndicatorHandler {
		void onEndCoachMode();
	}
	private CoachModeIndicatorDisplay display;

	private UserModel user;

	public CoachModeIndicatorPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CoachModeIndicatorDisplay display, UserModel user) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.user = user;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setUser(user);
		display.setHandler(new CoachModeIndicatorHandler() {
			@Override
			public void onEndCoachMode() {
				stop();
				
				fireEvent(new CoachModeEvent(null));
			}
		});
	}

}
