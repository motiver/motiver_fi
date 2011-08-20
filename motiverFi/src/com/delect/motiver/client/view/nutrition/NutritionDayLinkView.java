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
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.NutritionDayLinkPresenter;
import com.delect.motiver.client.presenter.nutrition.NutritionDayLinkPresenter.NutritionDayLinkHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.NutritionDayModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class NutritionDayLinkView extends NutritionDayLinkPresenter.NutritionDayLinkDisplay {

	private NutritionDayLinkHandler handler;
	private NutritionDayModel nutritionDay;
	
	public NutritionDayLinkView() {
		this.setStyleName("panel-nutritiondaylink");
		this.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
		this.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
		this.sinkEvents(Event.MOUSEEVENTS);
		
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.selected();
			}
		});

		//layout
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
	}
	
	@Override
	public Widget asWidget() {
		
		try {
			
			//icon
			Image icon = new Image(MyResources.INSTANCE.iconNutritionDay());
			this.add(icon, new HBoxLayoutData(new Margins(0, 20, 0, 0)));	

			LayoutContainer c = Functions.getTotalPanel(false, nutritionDay.getEnergy(), nutritionDay.getProtein(), nutritionDay.getCarb(), nutritionDay.getFet(), null);
			c.setHeight(30);
			this.add(c, new HBoxLayoutData(new Margins(0)));
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setHandler(NutritionDayLinkHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(NutritionDayModel nutritionDay) {
		this.nutritionDay = nutritionDay;
	}
	
}
