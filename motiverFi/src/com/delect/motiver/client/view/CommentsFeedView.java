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
