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
package com.delect.motiver.client.view.profile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.profile.MeasurementsListPresenter;
import com.delect.motiver.client.presenter.profile.MeasurementsListPresenter.MeasurementsListHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class MeasurementsListView extends MeasurementsListPresenter.MeasurementsListDisplay {
	
	private MeasurementsListHandler handler;

  public MeasurementsListView() {
	  setLayout(new RowLayout());
	}
	
	@Override
	public Widget asWidget() {
    
    //add panel where user can type name
    MyButton btnCreate = new MyButton();
    btnCreate.setScale(ButtonScale.MEDIUM);
    btnCreate.setText(AppController.Lang.CreateTarget(AppController.Lang.Measurement().toLowerCase()));
    btnCreate.addListener(Events.OnClick, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.newMeasurement();
      }
    });
    this.add(btnCreate, new RowData(-1, -1, new Margins(0,0,10,0)));
    
		return this;
	}

  @Override
  public void setHandler(MeasurementsListHandler handler) {
    this.handler = handler;
  }
}
