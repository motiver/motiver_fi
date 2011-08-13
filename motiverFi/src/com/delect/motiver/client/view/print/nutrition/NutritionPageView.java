/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.print.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.nutrition.NutritionPagePresenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class NutritionPageView extends NutritionPagePresenter.NutritionPageDisplay {

	
	
	@Override
	public Widget asWidget() {		
		return this;
	}

	@Override
	public LayoutContainer getCalendarContainer() {
		return null;
	}

}
