/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.cardio;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.cardio.TodayCardioPresenter;

import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class TodayCardioView extends TodayCardioPresenter.TodayCardioDisplay {
	
	public TodayCardioView() {
		
		this.setLayout(new RowLayout());
		
		
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

}
