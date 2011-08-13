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

/** Show more panel with single link.
 */
public class ShowMorePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ShowMoreDisplay extends Display {
		/**
		 * Sets handler for view to call.
		 * @param handler ShowMoreHandler
		 */
		public abstract void setHandler(ShowMoreHandler handler);
	}
	/** Handler for this presenter.
	 */
	public interface ShowMoreHandler {
		/**
		 * Called when user want's to show more.
		 */
		void showMore();
	}
	private ShowMoreDisplay display;

	private ShowMoreHandler handler;

	/**
	 * Shows button with text "show more"
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param handler : which is called when button is clicked
	 */
	public ShowMorePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ShowMoreDisplay display, ShowMoreHandler handler) { 
		super(rpcService, eventBus);
		this.display = display;
	    
    this.handler  = handler;
		
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(handler);
	}

}
