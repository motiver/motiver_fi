/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RoutineSelectedEventHandler;
import com.delect.motiver.shared.RoutineModel;

/**
 * Event for selecting routine
 * @author Antti
 *
 */
public class RoutineSelectedEvent extends GwtEvent<RoutineSelectedEventHandler> {
	
  public static Type<RoutineSelectedEventHandler> TYPE = new Type<RoutineSelectedEventHandler>();
  private RoutineModel routine;
  private boolean selectionOn = false;
	  
  public RoutineSelectedEvent(RoutineModel routine, boolean selectionOn) {
    this.routine = routine;
    this.selectionOn = selectionOn;
  }
	  
  @Override
  public Type<RoutineSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }
  public RoutineModel getRoutine() {
    return routine;
  }
	  
  public boolean isSelected() {
    return selectionOn;
  }

  @Override
  protected void dispatch(RoutineSelectedEventHandler handler) {
    handler.routineSelected(this);
  }
}




