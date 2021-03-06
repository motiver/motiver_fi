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
