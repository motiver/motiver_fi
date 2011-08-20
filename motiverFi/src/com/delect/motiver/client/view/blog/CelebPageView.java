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
package com.delect.motiver.client.view.blog;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.blog.CelebPagePresenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class CelebPageView extends CelebPagePresenter.CelebPageDisplay {

	private LayoutContainer panelData = new LayoutContainer();

	public CelebPageView() {
		
		//TEMP
		panelData.addText("Celeb blogs goes here");
		
		this.add(panelData);
		
	}
	
	@Override
	public Widget asWidget() {
		this.layout();
		return this;
	}

	@Override
	public LayoutContainer getDataContainer() {
		return panelData;
	}

}
