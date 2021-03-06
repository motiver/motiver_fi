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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.event.BlogShowEvent;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.client.presenter.ActivityFeedPresenter.ActivityFeedDisplay;
import com.delect.motiver.client.presenter.CommentsFeedPresenter.CommentsFeedDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.ActivityFeedView;
import com.delect.motiver.client.view.CommentsFeedView;
import com.delect.motiver.client.view.Display;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/** Main page
 */
public class MainPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MainPageDisplay extends Display {
		/**
		 * Returns container for body
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getBodyContainer();
		/**
		 * Returns container for last comments.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getCommentsContainer();
		/**
		 * Returns container for friends.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getFriendsContainer();
	}
	private ActivityFeedPresenter activityFeedPresenter;

	private CommentsFeedPresenter commentsFeedPresenter;
	private MainPageDisplay display;

	private long timeLastBlogShow;

	/**
	 * Constructor for MainPagePresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display MainPageDisplay
	 */
	public MainPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MainPageDisplay display) {
		super(rpcService, eventBus);
    this.display = display;

    activityFeedPresenter = new ActivityFeedPresenter(rpcService, eventBus, (ActivityFeedDisplay)GWT.create(ActivityFeedView.class), AppController.User.getUid());
    commentsFeedPresenter = new CommentsFeedPresenter(rpcService, eventBus, (CommentsFeedDisplay)GWT.create(CommentsFeedView.class));
	    
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		History.newItem("user", false);
		
		//EVENT: user selected
		addEventHandler(UserSelectedEvent.TYPE, new UserSelectedEventHandler() {
			@Override
			public void userSelected(UserSelectedEvent event) {
				//check last time so we avoid double clicks
				if(System.currentTimeMillis() - timeLastBlogShow > 1000) {
					timeLastBlogShow = System.currentTimeMillis();
					
					//show user's blog
					fireEvent(new BlogShowEvent(event.getUser()));
				}
			}
		});
	}

	@Override
	public void onRun() {

    activityFeedPresenter.run(display.getBodyContainer());
    commentsFeedPresenter.run(display.getCommentsContainer());
	    
	}

	@Override
	public void onStop() {
		
		if(activityFeedPresenter != null) {
			activityFeedPresenter.stop();
    }
		if(commentsFeedPresenter != null) {
			commentsFeedPresenter.stop();
    }
	}

}
