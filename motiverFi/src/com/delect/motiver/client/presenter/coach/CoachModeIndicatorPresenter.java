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
