/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class WorkoutCreatedEvent extends GwtEvent<WorkoutCreatedEventHandler> {
	
  public static Type<WorkoutCreatedEventHandler> TYPE = new Type<WorkoutCreatedEventHandler>();
  private WorkoutModel model;


  public WorkoutCreatedEvent(WorkoutModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<WorkoutCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public WorkoutModel getWorkout() {
		return model;  
  }

  @Override
  protected void dispatch(WorkoutCreatedEventHandler handler) {
    handler.onWorkoutCreated(this);
  }
}




