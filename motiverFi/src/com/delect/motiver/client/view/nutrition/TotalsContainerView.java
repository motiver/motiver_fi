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

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.nutrition.TotalsContainerPresenter;
import com.delect.motiver.shared.GuideValueModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class TotalsContainerView extends TotalsContainerPresenter.TotalsContainerDisplay {
	
	private double carb = 0;
	private double energy = 0;
	private double fet = 0;
	private GuideValueModel guide;
	private double protein = 0;

	public TotalsContainerView() {
		
		this.setStyleName("panel-nutrition-totals");
		
	}
	
	@Override
	public Widget asWidget() {

		return this;
	}

	@Override
	public LayoutContainer getTotalsContainer() {
		return this;
	}

	@Override
	public void setCurrentGuideValue(GuideValueModel value) {
		guide = value;
		showTotals();
	}
	
	@Override
	public void setData(double energy, double protein, double carb, double fet) {

		//save data
		this.energy = energy;
		this.protein = protein;
		this.carb = carb;
		this.fet = fet;
		
		showTotals();
	}

	/**
	 * Show totals text (incl. guide values)
	 */
	@Override
	public void showTotals() {
		
		this.removeAll();

		//calories
		LayoutContainer c = CommonUtils.getTotalPanel(true, energy, protein, carb, fet, guide);
		c.setHeight(50);
		this.add(c);

		this.layout();
	}


}
