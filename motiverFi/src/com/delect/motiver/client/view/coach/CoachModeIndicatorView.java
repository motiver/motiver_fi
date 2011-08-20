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
package com.delect.motiver.client.view.coach;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.coach.CoachModeIndicatorPresenter;
import com.delect.motiver.client.presenter.coach.CoachModeIndicatorPresenter.CoachModeIndicatorHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.client.view.widget.MyButton.Style;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Text;
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
    MyButton btnClose = new MyButton();
    btnClose.setText(AppController.Lang.EndCoachMode());
    btnClose.setScale(ButtonScale.MEDIUM);
    btnClose.setColor(Style.RED);
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
