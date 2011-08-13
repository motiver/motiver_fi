/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.blog;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.blog.SearchPagePresenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class SearchPageView extends SearchPagePresenter.SearchPageDisplay {

	private LayoutContainer panelData = new LayoutContainer();

	public SearchPageView() {
		
		//TEMP
		panelData.addText("Search goes here");
		
		this.add(panelData);
		
	}
	
	@Override
	public Widget asWidget() {
		this.layout();
		return this;
	}

	@Override
	public LayoutContainer getDataContainer() {
		return panelData;
	}

}
