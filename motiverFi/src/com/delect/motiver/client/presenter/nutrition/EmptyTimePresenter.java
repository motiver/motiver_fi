/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.nutrition;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

public class EmptyTimePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class EmptyTimeDisplay extends Display {
	  
	}
	private EmptyTimeDisplay display;

	/**
	 * Shows empty presenter for time view
	 */
	public EmptyTimePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyTimeDisplay display) { 
		super(rpcService, eventBus);
		this.display = display;
	}


	@Override
	public Display getView() {
		return display;
	}

}
