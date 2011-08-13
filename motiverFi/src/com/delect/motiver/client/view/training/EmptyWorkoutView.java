/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.training.EmptyWorkoutPresenter;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Empty view given text
 */
public class EmptyWorkoutView extends EmptyWorkoutPresenter.EmptyWorkoutDisplay {

	private int panelHeight = 250;
	
  Button btnNewExercise = new Button();
	
	
	
	@Override
	public Widget asWidget() {
		this.setStyleName("panel-empty");
		
		TableLayout tl = new TableLayout(1);
		tl.setWidth("100%");
		tl.setHeight(panelHeight + "px");
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		this.setLayout(tl);
		
		//text
		this.add(new Text(AppController.Lang.EmptyWorkoutDesc()));

		return this;
	}

}
