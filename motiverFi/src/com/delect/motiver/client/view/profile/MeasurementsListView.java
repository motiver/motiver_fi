/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.profile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.profile.MeasurementsListPresenter;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class MeasurementsListView extends MeasurementsListPresenter.MeasurementsListDisplay {
	
	public MeasurementsListView() {
	  setLayout(new RowLayout());
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
}
