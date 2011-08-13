/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.statistics;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.statistics.NutritionStatisticsPresenter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class NutritionStatisticsView extends NutritionStatisticsPresenter.NutritionStatisticsDisplay {
	
  LayoutContainer panelDates = new LayoutContainer();
  LayoutContainer panelBody = new LayoutContainer();
  
	public NutritionStatisticsView() {
	  panelDates.setLayout(new FlowLayout()); 
	  panelDates.setWidth(750);
	  this.add(panelDates, new RowData(-1, -1, new Margins(0,0,20,0)));
	  
	  this.add(panelBody);
	}
	
	@Override
	public Widget asWidget() {
	  return this;
	}

  @Override
  public LayoutContainer getDatesContainer() {
    return panelDates;
  }

  @Override
  public LayoutContainer getBodyContainer() {
    return panelBody;
  }
}
