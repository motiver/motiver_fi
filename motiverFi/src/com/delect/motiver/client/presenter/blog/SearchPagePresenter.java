/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
public class SearchPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class SearchPageDisplay extends Display {
		public abstract LayoutContainer getDataContainer();
	}
	private SearchPageDisplay display;

	//child presenters
	private Presenter emptyPresenter = null;

	/**
	 * Shows "high profile" blogs
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public SearchPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, SearchPageDisplay display) {
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
