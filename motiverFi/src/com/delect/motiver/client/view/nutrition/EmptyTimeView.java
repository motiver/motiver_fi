/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.EmptyTimePresenter;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Empty view given text
 */
public class EmptyTimeView extends EmptyTimePresenter.EmptyTimeDisplay {

	private int panelHeight = 100;
	
	
	
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
		this.add(new Text(AppController.Lang.EmptyTimeDesc()));

		return this;
	}

}
