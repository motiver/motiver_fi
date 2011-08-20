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
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.training.EmptyTrainingDayPresenter;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Empty view given text
 */
public class EmptyTrainingDayView extends EmptyTrainingDayPresenter.EmptyTrainingDayDisplay {

	private int panelHeight = 250;
	Button btnNewRoutine = new Button();
	
	Button btnNewWorkout = new Button();
	
	
	
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
		this.add(new Text(AppController.Lang.EmptyTrainingDayDesc()));
		
		return this;
	}

}
