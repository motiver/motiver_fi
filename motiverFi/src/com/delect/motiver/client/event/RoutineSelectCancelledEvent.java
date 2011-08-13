/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RoutineSelectCancelledEventHandler;

/**
 * Event for workout selection cancelled (for routine)
 * @author Antti
 *
 */
public class RoutineSelectCancelledEvent extends GwtEvent<RoutineSelectCancelledEventHandler> {
	
  public static Type<RoutineSelectCancelledEventHandler> TYPE = new Type<RoutineSelectCancelledEventHandler>();
  private Date date;
	 
  public RoutineSelectCancelledEvent(Date date) {
    this.date = date;
  }
	  
  @Override
  public Type<RoutineSelectCancelledEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
	  
  public Date getDate() {
    return date;
  }

  @Override
  protected void dispatch(RoutineSelectCancelledEventHandler handler) {
    handler.onCancel(this);
  }
}




