/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

/**
 * Index page when user is not logged in
 */
public class PreviewVideoPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class PreviewVideoDisplay extends Display {
	}
	PreviewVideoDisplay display;
	

	/**
	 * Constructor for PreviewVideoPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display PreviewVideoDisplay
	 */
	public PreviewVideoPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, PreviewVideoDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	    
	}


	@Override
	public Display getView() {
		return display;
	}

}
