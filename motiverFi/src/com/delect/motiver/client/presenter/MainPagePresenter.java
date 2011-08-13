/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
import com.delect.motiver.client.presenter.FriendsListPresenter.FriendsListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.ActivityFeedView;
import com.delect.motiver.client.view.CommentsFeedView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.FriendsListView;

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
	private FriendsListPresenter friendsListPresenter;

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
    friendsListPresenter = new FriendsListPresenter(rpcService, eventBus, (FriendsListDisplay)GWT.create(FriendsListView.class));
	    
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
    friendsListPresenter.run(display.getFriendsContainer());
	    
	}

	@Override
	public void onStop() {
		
		if(activityFeedPresenter != null) {
			activityFeedPresenter.stop();
    }
		if(commentsFeedPresenter != null) {
			commentsFeedPresenter.stop();
    }
		if(friendsListPresenter != null) {
			friendsListPresenter.stop();
    }
	}

}
