/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.training.WorkoutsListSearchPresenter;

import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class WorkoutsListSearchView extends WorkoutsListSearchPresenter.WorkoutsListSearchDisplay {
	
	public WorkoutsListSearchView() {
		setLayout(new RowLayout());
	}

	@Override
	public Widget asWidget() {
		
		return this;
	}
}
