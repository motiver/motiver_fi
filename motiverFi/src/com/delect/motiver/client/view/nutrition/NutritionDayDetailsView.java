/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import java.util.Date;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.NutritionDayDetailsPresenter;
import com.delect.motiver.shared.util.CommonUtils;

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
		title.setText(AppController.Lang.Micronutrients() + " " + CommonUtils.getDateString(date, false, false).toLowerCase() + ":");
	}


}
