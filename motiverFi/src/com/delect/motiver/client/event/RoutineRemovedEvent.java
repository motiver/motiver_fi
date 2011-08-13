/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RoutineRemovedEventHandler;
import com.delect.motiver.shared.RoutineModel;

public class RoutineRemovedEvent extends GwtEvent<RoutineRemovedEventHandler> {
	
  public static Type<RoutineRemovedEventHandler> TYPE = new Type<RoutineRemovedEventHandler>();
  private RoutineModel model;


  public RoutineRemovedEvent(RoutineModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RoutineRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RoutineModel getRoutine() {
		return model;  
  }

  @Override
  protected void dispatch(RoutineRemovedEventHandler handler) {
    handler.onRoutineRemoved(this);
  }
}




