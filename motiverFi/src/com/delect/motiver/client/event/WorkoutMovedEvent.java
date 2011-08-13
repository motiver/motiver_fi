/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.WorkoutMovedEventHandler;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class WorkoutMovedEvent extends GwtEvent<WorkoutMovedEventHandler> {
	
  public static Type<WorkoutMovedEventHandler> TYPE = new Type<WorkoutMovedEventHandler>();
  private WorkoutModel model;


  public WorkoutMovedEvent(WorkoutModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<WorkoutMovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public WorkoutModel getWorkout() {
		return model;  
  }

  @Override
  protected void dispatch(WorkoutMovedEventHandler handler) {
    handler.onWorkoutMoved(this);
  }
}




