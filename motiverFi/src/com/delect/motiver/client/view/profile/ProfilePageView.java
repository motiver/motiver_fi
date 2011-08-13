/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.profile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.profile.ProfilePagePresenter;
import com.delect.motiver.client.presenter.profile.ProfilePagePresenter.ProfilePageHandler;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

public class ProfilePageView extends ProfilePagePresenter.ProfilePageDisplay {


	private ProfilePageHandler handler;

  public ProfilePageView() {

    //menu items
    addMenuItem(AppController.Lang.Measurements(), true, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.onMenuClicked(0);
      }
    });
    addMenuItem(AppController.Lang.PersonalInformation(), false, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {        
        handler.onMenuClicked(1);
      }
    });
    addMenuItem(AppController.Lang.Permissions(), false, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.onMenuClicked(2);
      }
    });
    addMenuItem(AppController.Lang.FetchData(), false, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.onMenuClicked(3);
      }
    });
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

  @Override
  public void setHandler(ProfilePageHandler handler) {
    this.handler = handler;
  }

}
