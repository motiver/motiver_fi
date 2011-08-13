/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RoutineShowEventHandler;
import com.delect.motiver.shared.RoutineModel;

/**
 * Event for selecting routine
 * @author Antti
 *
 */
public class RoutineShowEvent extends GwtEvent<RoutineShowEventHandler> {
	
  public static Type<RoutineShowEventHandler> TYPE = new Type<RoutineShowEventHandler>();
  private RoutineModel routine;
	  
  public RoutineShowEvent(RoutineModel routine) {
    this.routine = routine;
  }
	  
  @Override
  public Type<RoutineShowEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RoutineModel getRoutine() {
    return routine;
  }

  @Override
  protected void dispatch(RoutineShowEventHandler handler) {
    handler.selectRoutine(this);
  }
}




