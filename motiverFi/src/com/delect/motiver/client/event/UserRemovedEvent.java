/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.UserRemovedEventHandler;
import com.delect.motiver.shared.UserModel;

public class UserRemovedEvent extends GwtEvent<UserRemovedEventHandler> {
	
  public static Type<UserRemovedEventHandler> TYPE = new Type<UserRemovedEventHandler>();
  private UserModel model;


  public UserRemovedEvent(UserModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<UserRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public UserModel getUser() {
		return model;  
  }

  @Override
  protected void dispatch(UserRemovedEventHandler handler) {
    handler.onUserRemoved(this);
  }
}




