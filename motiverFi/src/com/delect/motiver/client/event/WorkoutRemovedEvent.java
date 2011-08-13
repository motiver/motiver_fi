/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
import com.delect.motiver.shared.WorkoutModel;

public class WorkoutRemovedEvent extends GwtEvent<WorkoutRemovedEventHandler> {
	
  public static Type<WorkoutRemovedEventHandler> TYPE = new Type<WorkoutRemovedEventHandler>();
  private WorkoutModel model;


  public WorkoutRemovedEvent(WorkoutModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<WorkoutRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public WorkoutModel getWorkout() {
		return model;  
  }

  @Override
  protected void dispatch(WorkoutRemovedEventHandler handler) {
    handler.onWorkoutRemoved(this);
  }
}




