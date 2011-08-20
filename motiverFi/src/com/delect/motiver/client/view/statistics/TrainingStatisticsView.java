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
package com.delect.motiver.client.view.statistics;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.statistics.TrainingStatisticsPresenter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class TrainingStatisticsView extends TrainingStatisticsPresenter.TrainingStatisticsDisplay {
	
  LayoutContainer panelDates = new LayoutContainer();
  LayoutContainer panelBody = new LayoutContainer();
  
	public TrainingStatisticsView() {
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
