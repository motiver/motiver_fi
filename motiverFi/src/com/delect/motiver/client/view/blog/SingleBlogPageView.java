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
package com.delect.motiver.client.view.blog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.blog.SingleBlogPagePresenter;
import com.delect.motiver.client.presenter.blog.SingleBlogPagePresenter.BlogPageHandler;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class SingleBlogPageView extends SingleBlogPagePresenter.SingleBlogPageDisplay {

	private BlogPageHandler handler;
	//panels
	private LayoutContainer panelBody = new LayoutContainer();
	private LayoutContainer panelFriends = new LayoutContainer();
	private LayoutContainer panelLinkToBlog = new LayoutContainer();

	private LayoutContainer panelUser = new LayoutContainer();
	private long uid = 0;

	public SingleBlogPageView() {

		//right panel
		LayoutContainer panelRight = new LayoutContainer();
		panelRight.setLayout(new RowLayout());
		panelRight.setStyleAttribute("float", "right");
		panelRight.setStyleAttribute("margin-left", "40px");
		panelRight.setWidth(200);
		this.add(panelRight);
		
		//user whose blog this is
		panelUser.setLayout(new RowLayout());
		Text textUser = new Text(AppController.Lang.BlogsOwner());
		textUser.setStyleName("label-title-medium");
		panelUser.add(textUser, new RowData(-1, -1, new Margins(0, 0, 20, 0)));
		panelRight.add(panelUser, new RowData(-1, -1, new Margins(40, 0, 100, 50)));

		//friends
		panelRight.add(panelFriends);
		
		panelBody.setStyleAttribute("overflow", "auto");
		panelBody.add(panelLinkToBlog, new RowData(-1, -1, new Margins(0, 0, 20, 0)));
		this.add(panelBody);	
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelBody;
	}

	@Override
	public LayoutContainer getFriendsContainer() {
		return panelFriends;
	}

	@Override
	public LayoutContainer getUserContainer() {
		return panelUser;
	}
	
	@Override
	public void setHandler(BlogPageHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setShowSingleDay(boolean singleDay) {
		panelLinkToBlog.setVisible( singleDay );
		
		if(uid > 0) {
			
			//link to blog
			Anchor link = new Anchor();
			link.setHTML("Go to <fb:name uid=\"" + uid + "\" useyou=false linked=false possessive=true></fb:name> blog");
			link.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					handler.showFullBlog();
				}
			});
			panelLinkToBlog.removeAll();
			panelLinkToBlog.add(link);
			panelLinkToBlog.layout();
		}
	}

	@Override
	public void setUid(long uid) {
		this.uid  = uid;
	}

}
