/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.blog;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.presenter.FriendsListPresenter;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.FriendsListView;

/**
 * 
 * Shows "high profile" blogs
 */
public class BlogPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class BlogPageDisplay extends Display {
		
	}
	private BlogPageDisplay display;

	//child presenters
	private FriendsListPresenter friendsListPresenter;


	/**
	 * Shows users friends blogs
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public BlogPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, BlogPageDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}


	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onRun() {
	    
    //friends
    friendsListPresenter = new FriendsListPresenter(rpcService, eventBus, new FriendsListView());
    friendsListPresenter.run(display.getBaseContainer());
	}

	@Override
	public void onStop() {

		if(friendsListPresenter != null) {
      friendsListPresenter.stop();
    }
	}
}
