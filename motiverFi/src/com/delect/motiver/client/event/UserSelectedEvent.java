/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.shared.UserModel;

/**
 * Event for selecting user
 * @author Antti
 *
 */
public class UserSelectedEvent extends GwtEvent<UserSelectedEventHandler> {
	
  public static Type<UserSelectedEventHandler> TYPE = new Type<UserSelectedEventHandler>();
  private UserModel user;
	  
  public UserSelectedEvent(UserModel user) {
    this.user = user;
  }
	  
  @Override
  public Type<UserSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public UserModel getUser() {
    return user;
  }

  @Override
  protected void dispatch(UserSelectedEventHandler handler) {
    handler.userSelected(this);
  }
}




