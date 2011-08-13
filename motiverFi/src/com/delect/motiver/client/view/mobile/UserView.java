/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.mobile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserHandler;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * Shows single user
 */
public class UserView extends UserPresenter.UserDisplay {


	private UserHandler handler;
	private boolean smallPicture;
	private UserModel user;

	public UserView() {

		this.setStyleName("panel-user");
		this.setStyleAttribute("background-color", "ff0000");

    this.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
    this.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.onClick();
			}
		});
	}
	
	@Override
	public Widget asWidget() {

    //small picture: horizontal layout
    if(smallPicture) {
      this.setHeight(50);
      HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
      this.setLayout(layout);
    }
    //big picture : vertical layout
    else {
      this.setWidth(100);
      this.setLayout(new RowLayout());
    }
    
    //profile pic
//    Html html = new Html("<fb:profile-pic uid=\"" + uid + "\" facebook-logo=true size=" + ((smallPicture)? "square" : "small") + " linked=false></fb:profile-pic>");
//    if(smallPicture) {
//      html.setHeight(50);
//      html.setWidth(50);
//      this.add(html, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
//    }
//    else {
//      html.setWidth(100);
//      this.add(html, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
//    }
        
    //name
    Html htmlName = new Html(user.getNickName());
    htmlName.setStyleName("label-user-name");
    htmlName.setHeight(20);
    this.add(htmlName);
    
    return this;
  }

	@Override
	public void setHandler(UserHandler h) {
		handler = h;
	}

	@Override
	public void setModel(UserModel user) {
		this.user = user;
        
	}

	@Override
	public void setSmallPicture(boolean smallPicture) {
		this.smallPicture = smallPicture;
	}
	
}
