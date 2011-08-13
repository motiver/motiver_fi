/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

/**
 * 
 * Measurement page
 *  - measurements (targets & graph)
 */
public class InBodyPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class InBodyDisplay extends Display {

		public abstract void setHandler(InBodyHandler inBodyHandler);
	}

	public interface InBodyHandler {
	}
	private InBodyDisplay display;


	/**
	 * Shows inbodyform
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public InBodyPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, InBodyDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}

	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		display.setHandler(new InBodyHandler() {
			
		});
	}

}
