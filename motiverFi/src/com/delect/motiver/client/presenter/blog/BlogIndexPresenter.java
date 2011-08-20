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
package com.delect.motiver.client.presenter.blog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.event.BlogShowEvent;
import com.delect.motiver.client.event.ConnectionErrorEvent;
import com.delect.motiver.client.event.InfoMessageEvent;
import com.delect.motiver.client.event.LoadingEvent;
import com.delect.motiver.client.event.TabEvent;
import com.delect.motiver.client.event.handler.BlogShowEventHandler;
import com.delect.motiver.client.event.handler.ConnectionErrorEventHandler;
import com.delect.motiver.client.event.handler.LoadingEventHandler;
import com.delect.motiver.client.event.handler.TabEventHandler;
import com.delect.motiver.client.presenter.BrowserCheckPresenter;
import com.delect.motiver.client.presenter.BrowserCheckPresenter.BrowserCheckDisplay;
import com.delect.motiver.client.presenter.HeaderPresenter;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderDisplay;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderTarget;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;
import com.delect.motiver.client.presenter.LoadingPresenter;
import com.delect.motiver.client.presenter.LoadingPresenter.LoadingDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.blog.SingleBlogPagePresenter.SingleBlogPageDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.BrowserCheckView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.HeaderView;
import com.delect.motiver.client.view.LoadingView;
import com.delect.motiver.client.view.blog.SingleBlogPageView;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single blog
 * @author Antti
 *
 */
public class BlogIndexPresenter extends Presenter implements ValueChangeHandler<String> {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class BlogIndexDisplay extends Display {
		/**
		 * Returns container for body.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getBodyContainer();
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
		 * Returns container for messages.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getMessageContainer();
	}
	private BrowserCheckPresenter browserCheckPresenter;

	private BlogIndexDisplay display;
	private HeaderPresenter headerUserPresenter;
	private LoadingPresenter loadingPresenter;
	private Presenter pagePresenter;
	
	private int tabIndex = 0;
	private String uid = null;

	/**
	 * Constructor for BlogIndexPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display BlogIndexDisplay
	 */
	public BlogIndexPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, BlogIndexDisplay display, String uid) {
		super(rpcService, eventBus);
		this.display = display;
		this.uid = uid;
		
    //containers
    headerUserPresenter = new HeaderPresenter(rpcService, eventBus, (HeaderDisplay)GWT.create(HeaderView.class), HeaderTarget.BLOG, 0);
    browserCheckPresenter = new BrowserCheckPresenter(rpcService, eventBus, (BrowserCheckDisplay)GWT.create(BrowserCheckView.class));
	}


	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {

    History.addValueChangeHandler(this);
		
		//tab change handler
    addEventHandler(TabEvent.TYPE, new TabEventHandler() {
			@Override
			public void onTabChanged(TabEvent event) {
									
				switch(tabIndex) {
					case 1:
						//out blog
						History.newItem(String.valueOf(AppController.User.getUid()));
						break;
            //our friends
					default:
						Window.Location.assign(Constants.URL_APP);
						break;
				}
			}
    });
	    
    //EVENT: blog show
    addEventHandler(BlogShowEvent.TYPE, new BlogShowEventHandler() {
			@Override
			public void onBlogShow(BlogShowEvent event) {
				Window.Location.replace(event.getUser().getBlogUrl());
			}
    });
	    
    //EVENT: connection error
    addEventHandler(ConnectionErrorEvent.TYPE, new ConnectionErrorEventHandler() {
			@Override
			public void onError(ConnectionErrorEvent event) {
			  
        //show error message
        eventBus.fireEvent(new InfoMessageEvent(MessageColor.COLOR_RED, AppController.Lang.NetworkError()));
			    
        //refresh blog
        History.newItem(String.valueOf(AppController.User.getUid()));
			}
    });
	    
    //EVENT: loading text
    addEventHandler(LoadingEvent.TYPE, new LoadingEventHandler() {

			@Override
			public void onLoading(LoadingEvent event) {
				if(loadingPresenter != null) {
					loadingPresenter.stop();
	      }
				
				//show loading text
				if(event.getMessage() != null) {
					loadingPresenter = new LoadingPresenter(rpcService, eventBus, (LoadingDisplay)GWT.create(LoadingView.class), event.getMessage());
					loadingPresenter.run(display.getBaseContainer());
				}
			}
    });
	}

	@Override
	public void onRun() {

		headerUserPresenter.run(display.getHeaderContainer());
    browserCheckPresenter.run(display.getMessageContainer());
		
    History.fireCurrentHistoryState();
	}
	@Override
	public void onStop() {

		if(loadingPresenter != null) {
			loadingPresenter.stop();
    }
		if(headerUserPresenter != null) {
			headerUserPresenter.stop();
    }
		if(pagePresenter != null) {
			pagePresenter.stop();
    }
	}

	/**
	 * Called when url token changes.
	 * @param event ValueChangeEvent<String>
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(ValueChangeEvent<String>)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		
		if (token != null) {
			
			//reset old view
			if(pagePresenter != null) {
				pagePresenter.stop();
      }
			
			//just show single blog
			tabIndex = -1;
      pagePresenter = new SingleBlogPagePresenter(rpcService, eventBus, (SingleBlogPageDisplay)GWT.create(SingleBlogPageView.class), uid);
			pagePresenter.run(display.getBodyContainer());
			
		}
	}
	
	
}
