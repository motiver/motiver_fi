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


