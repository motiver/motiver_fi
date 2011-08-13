/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.FriendsListPresenter;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * Show users friends in list
 */
public class FriendsListView extends FriendsListPresenter.FriendsListDisplay {

	private LayoutContainer panelFriends = new LayoutContainer();

	public FriendsListView() {
		
		this.setLayout(new RowLayout());

		//title
		Text text = new Text(AppController.Lang.YourFriends());
		text.setStyleName("label-title-medium");
		this.add(text, new RowData(-1, -1, new Margins(0, 0, 15, 0)));
		
		FlowLayout fl = new FlowLayout();
		panelFriends.setLayout(fl);
		this.add(panelFriends);
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public LayoutContainer getFriendsContainer() {
		return panelFriends;
	}
	
}
