/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.BrowserCheckPresenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;

public class BrowserCheckView extends BrowserCheckPresenter.BrowserCheckDisplay {
	
	private LayoutContainer c = new LayoutContainer();
	
	@Override
	public Widget asWidget() {
		c.setStyleAttribute("white-space", "nowrap");

		//layout
		HBoxLayout layout = new HBoxLayout(); 
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		c.setLayout(layout);
		c.setStyleName("panel-browser");
    c.setHeight(40);
		
		return this;
	}
	
}
