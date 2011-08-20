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

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

public class EmptyWorkoutPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class EmptyWorkoutDisplay extends Display {
	
	}
	private EmptyWorkoutDisplay display;

	/**
	 * Shows empty presenter for single workout
	 */
	public EmptyWorkoutPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyWorkoutDisplay display ) { 
		super(rpcService, eventBus);
		this.display = display;
	}


	@Override
	public Display getView() {
		return display;
	}
}
