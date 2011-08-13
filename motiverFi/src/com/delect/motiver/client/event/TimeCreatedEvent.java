/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.TimeCreatedEventHandler;
import com.delect.motiver.shared.TimeModel;

public class TimeCreatedEvent extends GwtEvent<TimeCreatedEventHandler> {
	
  public static Type<TimeCreatedEventHandler> TYPE = new Type<TimeCreatedEventHandler>();
  private TimeModel model;


  public TimeCreatedEvent(TimeModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<TimeCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public TimeModel getTime() {
		return model;  
  }

  @Override
  protected void dispatch(TimeCreatedEventHandler handler) {
    handler.onTimeCreated(this);
  }
}




