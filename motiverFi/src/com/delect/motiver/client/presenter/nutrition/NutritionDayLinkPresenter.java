/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
