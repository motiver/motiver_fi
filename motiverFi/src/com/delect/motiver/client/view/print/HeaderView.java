/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.print;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.HeaderPresenter;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderHandler;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderTarget;
import com.delect.motiver.client.res.MyResources;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Init tab menu
 *  - 5 tabs (main, training, nutrition, measurements, statistics
 */
public class HeaderView extends HeaderPresenter.HeaderDisplay {
	
	public HeaderView() {
		
		this.setWidth(975);
		this.setStyleAttribute("margin", "0 auto 0 auto");
		
	}
	
	@Override
	public Widget asWidget() {

		//logo
		LayoutContainer panelLogo = new LayoutContainer();
		panelLogo.add(new Image(MyResources.INSTANCE.logoHeaderPrint()));
		this.add(panelLogo);
		
		return this;
	}

	@Override
	public void setHandler(HeaderHandler handler) {}

	@Override
	public void setTab(int tabIndex) {}

	@Override
	public void setTarget(HeaderTarget target) {}

  @Override
  public void showLoadingDialog(boolean enabled) {}

}
