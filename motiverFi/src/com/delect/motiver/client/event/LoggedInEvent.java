/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.LoggedInEventHandler;
import com.delect.motiver.shared.UserModel;

public class LoggedInEvent extends GwtEvent<LoggedInEventHandler> {
  public static Type<LoggedInEventHandler> TYPE = new Type<LoggedInEventHandler>();
	  

  private UserModel user;

  /**
  * Logged in event
  * @param user object. Null if not logged in
  */
  public LoggedInEvent(UserModel user) {
    this.user = user;
  }
  
	  
  @Override
  public Type<LoggedInEventHandler> getAssociatedType() {
    return TYPE;
  }

  public UserModel getUser() {
    return user;
  }

	@Override
  protected void dispatch(LoggedInEventHandler handler) {
    handler.onLoggedIn(this);
  }
}

