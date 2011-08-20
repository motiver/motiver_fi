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
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.MealLinkPresenter;
import com.delect.motiver.client.presenter.nutrition.MealLinkPresenter.MealLinkHandler;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.shared.MealModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class MealLinkView extends MealLinkPresenter.MealLinkDisplay {

	private MealLinkHandler handler;
	private MealModel meal;

	private boolean quickSelectOn = false;
	
	public MealLinkView() {
		this.setStyleName("panel-meallink");
		this.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
		this.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
		this.sinkEvents(Event.MOUSEEVENTS);
		
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//if cancel is not on
				if(getData("cancel") == null) {
					handler.selected();
        }
				else {
					setData("cancel", null);
        }
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
			
			//if quick selection
			if(quickSelectOn) {
				CheckBox cbSelect = new CheckBox();
				cbSelect.addListener(Events.Change, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						Boolean isSelected = ((CheckBox)be.getSource()).getValue();
						
						cancelSelection();
						
						handler.quickSelect(isSelected);
					}
				});
				this.add(cbSelect, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			}

			//name
			Text name = new Text(meal.getName());
			name.setStyleName("label-title-medium");
			this.add(name, new HBoxLayoutData(new Margins(0)));
			
			//spacer
			HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, 0, 10));
      flex.setFlex(1);  
      this.add(new Text(), flex);  
	        
      //username
      if(!meal.getUid().equals(AppController.User.getUid())) {
        LayoutContainer panelUsername = new LayoutContainer();
        panelUsername.setWidth(100);
        panelUsername.setHeight(15);
        panelUsername.setStyleName("label-title-username");
        panelUsername.addText("<fb:name uid=\"" + meal.getUid() + "\" useyou=false linked=false></fb:name>");
        this.add(panelUsername, new HBoxLayoutData(new Margins(0)));
      }
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setHandler(MealLinkHandler handler) {
		this.handler = handler;
	}
	@Override
	public void setModel(MealModel meal) {
		this.meal = meal;
	}

	@Override
	public void setQuickSelect(boolean quickSelectOn) {
		this.quickSelectOn = quickSelectOn;
	}

	/**
	 * Cancels selection (for example when checkbox is clicked
	 */
	protected void cancelSelection() {
		this.setData("cancel", true);
	}
	
}
