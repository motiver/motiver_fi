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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.event.ConnectionErrorEvent;
import com.delect.motiver.client.event.InfoMessageEvent;
import com.delect.motiver.client.event.TabEvent;
import com.delect.motiver.client.event.handler.ConnectionErrorEventHandler;
import com.delect.motiver.client.event.handler.TabEventHandler;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;
import com.delect.motiver.client.presenter.LoginPresenter.LoginDisplay;
import com.delect.motiver.client.presenter.PreviewVideoPresenter.PreviewVideoDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.LoginView;
import com.delect.motiver.client.view.PreviewVideoView;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Index page when user is not logged in
 */
public class IndexPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class IndexDisplay extends Display {
		/**
		 * Returns container for footer.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getFooterContainer();
		/**
		 * Returns container for header.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getHeaderContainer();
		/**
		 * Returns container for info messages.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getInfoContainer();
		/**
		 * Returns container for preview video.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getPreviewVideoContainer();
		/**
		 * Returns container for sign in panel.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getSignInContainer();
    /**
     * Returns container for messages.
     * @return LayoutContainer
     */
    public abstract LayoutContainer getMessageContainer();
		/**
		 * Shows/hides progress box.
		 * @param show boolean
		 */
		public abstract void showProgressBox(boolean show);
	}
	private IndexDisplay display;

	private LoginPresenter loginPresenter;
	private PreviewVideoPresenter previewVideoPresenter;

	/**
	 * Constructor for IndexPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display IndexDisplay
	 */
	public IndexPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, IndexDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
		
    loginPresenter = new LoginPresenter(rpcService, eventBus, (LoginDisplay)GWT.create(LoginView.class));
    previewVideoPresenter = new PreviewVideoPresenter(rpcService, eventBus, (PreviewVideoDisplay)GWT.create(PreviewVideoView.class));
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		//tab change handler
    addEventHandler(TabEvent.TYPE, new TabEventHandler() {
			@Override
			public void onTabChanged(TabEvent event) {
					
        switch(event.getIndex()) {
						case 1:
//							Window.Location.assign(Constants.URL_BLOG);
							break;
						default:
							Window.Location.assign(Constants.URL_APP);
							break;
        }
			}			
    });
	    
    //EVENT: connection error
    addEventHandler(ConnectionErrorEvent.TYPE, new ConnectionErrorEventHandler() {
			@Override
			public void onError(ConnectionErrorEvent event) {

        display.showProgressBox(false);
				
				//show error message
        eventBus.fireEvent(new InfoMessageEvent(MessageColor.COLOR_RED, AppController.Lang.NetworkError()));
			}
    });
	}

	@Override
	public void onRun() {
		
		loginPresenter.run(display.getSignInContainer());
	  previewVideoPresenter.run(display.getPreviewVideoContainer());
		
		//remove token
		History.newItem("", false);
	}

	@Override
	public void onStop() {

		display.showProgressBox(false);
		
		if(previewVideoPresenter != null) {
			previewVideoPresenter.stop();
    }
		if(loginPresenter != null) {
			loginPresenter.stop();
    }
	}

}
