/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.WorkoutSelectCancelledEventHandler;
import com.delect.motiver.shared.RoutineModel;

/**
 * Event for workout selection cancelled (for routine)
 * @author Antti
 *
 */
public class WorkoutSelectCancelledEvent extends GwtEvent<WorkoutSelectCancelledEventHandler> {
	
  public static Type<WorkoutSelectCancelledEventHandler> TYPE = new Type<WorkoutSelectCancelledEventHandler>();
  private Date date;
  private RoutineModel routine;
	  
  public WorkoutSelectCancelledEvent(Date date) {
    this.date = date;
  }
  public WorkoutSelectCancelledEvent(RoutineModel routine) {
    this.routine = routine;
  }
	  
  @Override
  public Type<WorkoutSelectCancelledEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public Date getDate() {
    return date;
  }
	  
  public RoutineModel getRoutine() {
    return routine;
  }

  @Override
  protected void dispatch(WorkoutSelectCancelledEventHandler handler) {
    handler.onCancel(this);
  }
}




