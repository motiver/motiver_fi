/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.print.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.EmptyNutritionDayPresenter;
import com.delect.motiver.client.presenter.nutrition.EmptyNutritionDayPresenter.EmptyNutritionDayHandler;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Empty view given text
 */
public class EmptyNutritionDayView extends EmptyNutritionDayPresenter.EmptyNutritionDayDisplay {
	
	@Override
	public Widget asWidget() {
		this.setStyleName("panel-empty");
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		this.setLayout(tl);
		
		//text
		TableData td = new TableData();
		td.setColspan(2);
		td.setHeight("100px");
		this.add(new Text(AppController.Lang.EmptyNutritionDayDesc()), td);
		
		return this;
	}

	@Override
	public void setHandler(EmptyNutritionDayHandler handler) {}

  @Override
  public void setDefaultTimes(int[] timesTraining, int[] timesRest) {}

}
