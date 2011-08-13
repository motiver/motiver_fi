/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import java.util.Date;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.NutritionDayDetailsPresenter;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class NutritionDayDetailsView extends NutritionDayDetailsPresenter.NutritionDayDetailsDisplay {
	
	private LayoutContainer panelBody = new LayoutContainer();
	private Text title = new Text();
	
	public NutritionDayDetailsView() {
		
		this.setStyleName("panel-nutrition-day-details");	
		this.setLayout(new RowLayout());
		
		title.setText(AppController.Lang.Micronutrients() + ": ");
		title.setStyleName("label-title-medium");
		this.add(title, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
		
		this.add(panelBody);
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}
	
	@Override
	public LayoutContainer getBaseContainer() {
		return panelBody;
	}

	@Override
	public void setDate(Date date) {
		title.setText(AppController.Lang.Micronutrients() + " " + Functions.getDateString(date, false, false).toLowerCase() + ":");
	}


}
