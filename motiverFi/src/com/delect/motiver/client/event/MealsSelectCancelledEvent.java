/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MealsSelectCancelledEventHandler;
import com.delect.motiver.shared.TimeModel;

/**
 * Event for workout selection cancelled (for routine)
 * @author Antti
 *
 */
public class MealsSelectCancelledEvent extends GwtEvent<MealsSelectCancelledEventHandler> {
	
  public static Type<MealsSelectCancelledEventHandler> TYPE = new Type<MealsSelectCancelledEventHandler>();
  private TimeModel time;
	  
  public MealsSelectCancelledEvent(TimeModel time) {
    this.time = time;
  }
	  
  @Override
  public Type<MealsSelectCancelledEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public TimeModel getTime() {
    return time;
  }

  @Override
  protected void dispatch(MealsSelectCancelledEventHandler handler) {
    handler.onCancel(this);
  }
}




