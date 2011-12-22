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
//    addMenuItem(AppController.Lang.FetchData(), false, new Listener<BaseEvent>() {
//      @Override
//      public void handleEvent(BaseEvent be) {
//        handler.onMenuClicked(3);
//      }
//    });
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
