/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.coach;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.coach.CoachModeIndicatorPresenter;
import com.delect.motiver.client.presenter.coach.CoachModeIndicatorPresenter.CoachModeIndicatorHandler;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

public class CoachModeIndicatorView extends CoachModeIndicatorPresenter.CoachModeIndicatorDisplay {

	private CoachModeIndicatorHandler handler;
	
	public CoachModeIndicatorView() {
		this.setStyleName("panel-coach-indicator");

		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
        
	}

	@Override
	public Widget asWidget() {

		//text
		this.add(new Text(AppController.Lang.CoachModeOn()), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
		
		//spacer
		HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
    flex.setFlex(1);  
    this.add(new Text(), flex);  
		
    //close button
    Button btnClose = new Button();
    btnClose.setText(AppController.Lang.EndCoachMode());
    btnClose.setScale(ButtonScale.MEDIUM);
    btnClose.addStyleName("btn-red");
    btnClose.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.onEndCoachMode();
			}
    });
    this.add(btnClose, new HBoxLayoutData(new Margins(0, 30, 0, 0))); 

		return this;
	}

	@Override
	public void setHandler(CoachModeIndicatorHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setUser(UserModel user) {}

}
