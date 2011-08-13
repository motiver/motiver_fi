/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.nutrition.MealsListSearchPresenter;

import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class MealsListSearchView extends MealsListSearchPresenter.MealsListSearchDisplay {
	
	public MealsListSearchView() {
		setLayout(new RowLayout());
	}

	@Override
	public Widget asWidget() {
		
		return this;
	}
}
