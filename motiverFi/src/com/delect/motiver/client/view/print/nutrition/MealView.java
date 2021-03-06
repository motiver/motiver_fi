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

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.MealPresenter;
import com.delect.motiver.client.presenter.nutrition.MealPresenter.MealHandler;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class MealView extends MealPresenter.MealDisplay {

	private MealModel meal = null;
	private LayoutContainer panelFoods = new LayoutContainer();
	//panels
	private LayoutContainer panelTotals = new LayoutContainer();
	
	private LayoutContainer panelUser = new LayoutContainer();
		
	
	
	@Override
	public Widget asWidget() {

		if(meal.getTimeId() == 0) {
			this.setStyleName("panel-meal");
    }
		else {
			this.setStyleName("panel-meal-intime");
    }
				
		try {
			
			//title
			String name = "- " + AppController.Lang.NoName() + " -";
			if(meal.getName().length() > 0) {
				name = meal.getName();
      }
			Text textTitle = new Text(name);
			textTitle.setStyleName("label-title-medium");
      this.add(textTitle, new RowData(-1, -1, new Margins(5, 0, 10, 10)));
				
			if(meal.getTimeId() == 0) {

				//totals panel
				HBoxLayout layout = new HBoxLayout();
        layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
        panelTotals.setLayout(layout);
        panelTotals.setHeight(30);
        this.add(panelTotals, new RowData(-1, -1, new Margins(5, 0, 5, 10)));
			}
	        
			//userview
			panelUser.setStyleAttribute("float", "right");
			panelUser.setStyleAttribute("margin", "20px 20px 0 20px");
			panelUser.setVisible(false);
			this.insert(panelUser, 0);
			
			this.add(panelFoods);

			panelFoods.setLayout(new RowLayout()); 

		} catch (Exception e) {
      Motiver.showException(e);
		}
		
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
	public LayoutContainer getUserContainer() {
		panelUser.setVisible(true);
		return panelUser;
	}

	@Override
	public void setHandler(MealHandler handler) {}

	@Override
	public void setModel(MealModel meal) {
		
		this.meal = meal;
		
		initTotals();
	}

	/**
	 * Inits totals panel based on model
	 */
	private void initTotals() {
		
		try {
			//only if not in time
			if(meal.getTimeId() == 0) {
				panelTotals.removeAll();
				panelTotals.add(new Text(AppController.Lang.MealsStats() + ":"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
				panelTotals.add(CommonUtils.getTotalPanel(meal.getEnergy(), meal.getProtein(), meal.getCarb(), meal.getFet()));
				panelTotals.layout();
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
}
