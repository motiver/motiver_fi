/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.nutrition.TotalsContainerPresenter;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.GuideValueModel;

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
		LayoutContainer c = Functions.getTotalPanel(true, energy, protein, carb, fet, guide);
		c.setHeight(50);
		this.add(c);

		this.layout();
	}


}
