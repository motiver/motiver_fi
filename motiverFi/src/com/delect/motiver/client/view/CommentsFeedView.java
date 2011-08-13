/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.CommentsFeedPresenter;
import com.delect.motiver.client.presenter.CommentsFeedPresenter.CommentFeedHandler;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class CommentsFeedView extends CommentsFeedPresenter.CommentsFeedDisplay {

	public CommentsFeedView() {
		this.setLayout(new RowLayout());

		//title
		Text text = new Text(AppController.Lang.RecentComments());
		text.setStyleName("label-title-medium");
		this.add(text, new RowData(-1, -1, new Margins(0, 0, 15, 0)));
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public void setHandler(CommentFeedHandler handler) {}
}
