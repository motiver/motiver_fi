/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.nutrition;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.MicroNutrientModel;

/**
 * Shows single micronutrient name as "link"
 * @author Antti
 *
 */
public class MicroNutrientLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MicroNutrientLinkDisplay extends Display {
		public abstract void setModel(MicroNutrientModel model);
	}
	private MicroNutrientLinkDisplay display;

	private MicroNutrientModel model;

	public MicroNutrientLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MicroNutrientLinkDisplay display, MicroNutrientModel model) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.model = model;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(model);
	}

}
