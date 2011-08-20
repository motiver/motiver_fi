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
