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
package com.delect.motiver.client.view.print.nutrition;

import java.util.Date;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter;
import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter.NutritionDayHandler;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * Foods for single day
 * <br> Totals always visible
 * <br> When clicked totals -> shows foods & comments 
 * @author Antti
 *
 */
public class NutritionDayView extends NutritionDayPresenter.NutritionDayDisplay {

	private NutritionDayHandler handler;
	private LayoutContainer panelData = new LayoutContainer();
	private LayoutContainer panelFoods = new LayoutContainer();
	
	//panels
	private LayoutContainer panelTotals = new LayoutContainer();

	public NutritionDayView() {

		this.setStyleName("panel-today-nutrition");
		this.setLayout(new RowLayout());
		panelData.setLayout(new RowLayout());
	}
	
	@Override
	public Widget asWidget() {
		
		this.removeAll();
		
		//totals
		this.add(panelTotals);

		//foods
		panelData.setBorders(false);
		panelData.add(panelFoods, new RowData(-1, -1, new Margins(10, 0, 0, 0)));
		
		this.add(panelData, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
		
		panelFoods.setStyleAttribute("min-height", "250px");
		panelFoods.setLayout(new RowLayout()); 
		
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelFoods;
	}

	@Override
	public LayoutContainer getCommentsContainer() {
		return null;
	}

	@Override
	public LayoutContainer getDetailsContainer() {
		return null;
	}

	@Override
	public LayoutContainer getGuideContainer() {
		return null;
	}

	@Override
	public LayoutContainer getTotalsContainer() {
		return panelTotals;
	}

	@Override
	public void onScroll(int y) {}

	@Override
	public void removeAllFromTotals() {
		panelTotals.removeAll();
	}

	@Override
	public void setAddButtonVisible(boolean visible) {}

	@Override
	public void setDate(Date date) {}

	@Override
	public void setFoodsEnabled(boolean foodsPermission) {}

	@Override
	public void setHandler(final NutritionDayHandler handler) {
		this.handler = handler;
	}

	@Override
	public void showContent() {
		handler.timesVisible();
	}

}
