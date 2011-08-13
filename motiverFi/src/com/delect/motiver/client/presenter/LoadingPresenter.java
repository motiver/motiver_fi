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
 * Shows loading message
 * @author Antti
 *
 */
public class LoadingPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class LoadingDisplay extends Display {
		/**
		 * Set loading message.
		 * @param message String
		 */
		public abstract void setMessage(String message);
	}
	private LoadingDisplay display;
	
	private String message;


	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param message String
	 */
	public LoadingPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, LoadingDisplay display, String message) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.message = message;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setMessage(message);
	}

}
