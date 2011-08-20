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

import com.delect.motiver.client.presenter.MainPagePresenter;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * 
 * Main page
 *  - ...
 */
public class MainPageView extends MainPagePresenter.MainPageDisplay {

	//panels
	private LayoutContainer panelBody = new LayoutContainer();
	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelFriends = new LayoutContainer();

	public MainPageView() {

		//right panel
		LayoutContainer panelRight = new LayoutContainer();
		panelRight.setLayout(new RowLayout());
		panelRight.setStyleAttribute("float", "right");
		panelRight.setStyleAttribute("margin-left", "40px");
		panelRight.setWidth(350);
		this.add(panelRight);
		
		//comments
		panelRight.add(panelComments, new RowData(-1, -1, new Margins(0, 0, 40, 0)));

		//friends
		panelRight.add(panelFriends);
		
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
	public LayoutContainer getCommentsContainer() {
		return panelComments;
	}
	
	@Override
	public LayoutContainer getFriendsContainer() {
		return panelFriends;
	}

}
