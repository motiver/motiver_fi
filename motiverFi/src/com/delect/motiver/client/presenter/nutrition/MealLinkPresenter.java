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

import com.delect.motiver.client.event.MealSelectedEvent;
import com.delect.motiver.client.event.MealShowEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.MealModel;

/**
 * Shows single meal name as "link"
 * @author Antti
 *
 */
public class MealLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MealLinkDisplay extends Display {

		public abstract void setHandler(MealLinkHandler mealLinkHandler);
		public abstract void setModel(MealModel meal);
		public abstract void setQuickSelect(boolean quickSelectOn);
	}

	public interface MealLinkHandler {
		void quickSelect(boolean selected);
		void selected();
	}
	private MealLinkDisplay display;

	private boolean quickSelectOn = false;
	
	protected MealModel meal;

	public MealLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MealLinkDisplay display, MealModel meal, boolean quickSelectOn) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.meal = meal;
    this.quickSelectOn  = quickSelectOn;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(meal);
		display.setQuickSelect(quickSelectOn);
		display.setHandler(new MealLinkHandler() {
			@Override
			public void quickSelect(boolean selected) {
				//fire event
				MealSelectedEvent event = new MealSelectedEvent(meal, selected);
				fireEvent(event);
					
			}
			@Override
			public void selected() {
				//fire event
				fireEvent(new MealShowEvent(meal));
			}
		});
	}
}
