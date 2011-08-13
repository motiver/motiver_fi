/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RoutineCreatedEventHandler;
import com.delect.motiver.shared.RoutineModel;

public class RoutineCreatedEvent extends GwtEvent<RoutineCreatedEventHandler> {
	
  public static Type<RoutineCreatedEventHandler> TYPE = new Type<RoutineCreatedEventHandler>();
  private RoutineModel model;


  public RoutineCreatedEvent(RoutineModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RoutineCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RoutineModel getRoutine() {
		return model;  
  }

  @Override
  protected void dispatch(RoutineCreatedEventHandler handler) {
    handler.onRoutineCreated(this);
  }
}




