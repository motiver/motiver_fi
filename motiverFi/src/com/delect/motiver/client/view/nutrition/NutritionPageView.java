/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.nutrition.NutritionPagePresenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class NutritionPageView extends NutritionPagePresenter.NutritionPageDisplay {

	private LayoutContainer panelCalendar = new LayoutContainer();

	
	
	@Override
	public Widget asWidget() {		
		this.add(panelCalendar);
		
		return this;
	}

	@Override
	public LayoutContainer getCalendarContainer() {
		return panelCalendar;
	}

}
