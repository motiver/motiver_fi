/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CheckLoginEventHandler;

public class CheckLoginEvent extends GwtEvent<CheckLoginEventHandler> {
  public static Type<CheckLoginEventHandler> TYPE = new Type<CheckLoginEventHandler>();

  private final String password;
  private final boolean rememberMe;
  private final String username;

  /**
  * Check login event
  * @param username
  * @param password
  */
  public CheckLoginEvent(String username, String password, boolean rememberMe) {
    this.username = username;
    this.password = password;
    this.rememberMe = rememberMe;
  }
	  
  @Override
  public Type<CheckLoginEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  /**
  * Get password associated with this event
  * @return password
  */
  public String getPassword() {
		return password;  
  }
	  
  /**
  * Get rememberMe associated with this event
  * @return rememberMe
  */
  public boolean getRememberMe() {
		return rememberMe;  
  }
	  
  /**
  * Get username associated with this event
  * @return username
  */
  public String getUsername() {
		return username;  
  }

  @Override
  protected void dispatch(CheckLoginEventHandler handler) {
    handler.onCheckFinished(this);
  }
}


