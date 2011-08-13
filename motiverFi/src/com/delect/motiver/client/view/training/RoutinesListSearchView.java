/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.training.RoutinesListSearchPresenter;

import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class RoutinesListSearchView extends RoutinesListSearchPresenter.RoutinesListSearchDisplay {
	
	public RoutinesListSearchView() {
		setLayout(new RowLayout());
	}

	@Override
	public Widget asWidget() {
		
		return this;
	}
}
