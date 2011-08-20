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
package com.delect.motiver.client.presenter.blog;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Shows "high profile" blogs
 */
public class CelebPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CelebPageDisplay extends Display {
		public abstract LayoutContainer getDataContainer();
	}
	private CelebPageDisplay display;

	//child presenters
	private Presenter emptyPresenter = null;

	/**
	 * Shows "high profile" blogs
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public CelebPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CelebPageDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}

	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onStop() {
		
		unbindPresenters();
	}

	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {

		try {
			if(emptyPresenter != null) {
        emptyPresenter.stop();
      }
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
}
