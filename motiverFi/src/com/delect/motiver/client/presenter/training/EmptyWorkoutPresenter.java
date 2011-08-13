/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
