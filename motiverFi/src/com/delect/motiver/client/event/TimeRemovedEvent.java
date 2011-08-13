/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.TimeRemovedEventHandler;
import com.delect.motiver.shared.TimeModel;

public class TimeRemovedEvent extends GwtEvent<TimeRemovedEventHandler> {
	
  public static Type<TimeRemovedEventHandler> TYPE = new Type<TimeRemovedEventHandler>();
  private TimeModel model;


  public TimeRemovedEvent(TimeModel model) {
    this.model = model;
  }

  @Override
  public Type<TimeRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public TimeModel getTime() {
    return model;  
  }

  @Override
  protected void dispatch(TimeRemovedEventHandler handler) {
    handler.onTimeRemoved(this);
  }
}




