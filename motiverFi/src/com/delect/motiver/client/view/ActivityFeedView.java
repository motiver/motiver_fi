/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.ActivityFeedPresenter;
import com.delect.motiver.client.presenter.ActivityFeedPresenter.ActivityFeedHandler;

import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ActivityFeedView extends ActivityFeedPresenter.ActivityFeedDisplay {

	public ActivityFeedView() {
		this.setLayout(new RowLayout());
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void setHandler(ActivityFeedHandler handler) {}

}
