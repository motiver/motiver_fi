/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.TimeUpdatedEventHandler;
import com.delect.motiver.shared.TimeModel;

public class TimeUpdatedEvent extends GwtEvent<TimeUpdatedEventHandler> {
	
  public static Type<TimeUpdatedEventHandler> TYPE = new Type<TimeUpdatedEventHandler>();
  private TimeModel model;


  public TimeUpdatedEvent(TimeModel model) {
    this.model = model;
  }
  @Override
  public Type<TimeUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public TimeModel getTime() {
    return model;  
  }

  @Override
  protected void dispatch(TimeUpdatedEventHandler handler) {
    handler.onTimeUpdated(this);
  }
}




