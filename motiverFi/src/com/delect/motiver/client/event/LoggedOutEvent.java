/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.LoggedOutEventHandler;

public class LoggedOutEvent extends GwtEvent<LoggedOutEventHandler> {
  public static Type<LoggedOutEventHandler> TYPE = new Type<LoggedOutEventHandler>();
 
  @Override
  public Type<LoggedOutEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(LoggedOutEventHandler handler) {
    handler.onLoggedOut(this);
  }
}

