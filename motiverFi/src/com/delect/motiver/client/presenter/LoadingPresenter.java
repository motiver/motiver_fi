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
