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

import com.delect.motiver.client.event.NutritionDayShowEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.NutritionDayModel;

/**
 * Shows single nutrition days as "link" (energy, protein, ...)
 * @author Antti
 *
 */
public class NutritionDayLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class NutritionDayLinkDisplay extends Display {
		public abstract void setHandler(NutritionDayLinkHandler nutritionDayLinkHandler);
		public abstract void setModel(NutritionDayModel nutritionDay);
	}

	public interface NutritionDayLinkHandler {
		void selected();
	}
	private NutritionDayLinkDisplay display;
	
	private NutritionDayModel nutritionDay;

	public NutritionDayLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NutritionDayLinkDisplay display, NutritionDayModel nutritionDay) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.nutritionDay = nutritionDay;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(nutritionDay);
		display.setHandler(new NutritionDayLinkHandler() {
			@Override
			public void selected() {
				fireEvent(new NutritionDayShowEvent(nutritionDay));
			}
		});
	}

}
