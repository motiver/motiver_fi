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

public class EmptyTrainingDayPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class EmptyTrainingDayDisplay extends Display {
	}
	private EmptyTrainingDayDisplay display;

	/**
	 * Shows empty presenter for today training day view
	 */
	public EmptyTrainingDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyTrainingDayDisplay display) { 
		super(rpcService, eventBus);
		this.display = display;
		
	}


	@Override
	public Display getView() {
		return display;
	}

}
