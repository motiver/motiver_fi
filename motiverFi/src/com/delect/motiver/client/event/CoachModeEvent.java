/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CoachModeEventHandler;
import com.delect.motiver.shared.UserModel;

/**
 * Event for showing Run
 * @author Antti
 *
 */
public class CoachModeEvent extends GwtEvent<CoachModeEventHandler> {
	
  public static Type<CoachModeEventHandler> TYPE = new Type<CoachModeEventHandler>();
  private final UserModel user;
	  

  /**
  * Event for coach mode
  * @param user
  */
  public CoachModeEvent(UserModel user) {
    this.user = user;
  }
	  
  @Override
  public Type<CoachModeEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  /**
  * User object which is coached
  * @return null if coach mode is ended
  */
  public UserModel getUser() {
    return user;
  }

  @Override
  protected void dispatch(CoachModeEventHandler handler) {
    handler.onCoachModeOn(this);
  }
}




