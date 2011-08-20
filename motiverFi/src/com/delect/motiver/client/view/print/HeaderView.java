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
