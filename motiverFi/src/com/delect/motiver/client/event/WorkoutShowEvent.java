/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.WorkoutShowEventHandler;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class WorkoutShowEvent extends GwtEvent<WorkoutShowEventHandler> {
	
  public static Type<WorkoutShowEventHandler> TYPE = new Type<WorkoutShowEventHandler>();
	private WorkoutModel workout;
	  
	  
  public WorkoutShowEvent(WorkoutModel workout) {
    this.workout = workout;
  }
	  
  @Override
  public Type<WorkoutShowEventHandler> getAssociatedType() {
    return TYPE;
  }

  public WorkoutModel getWorkout() {
		return workout;
	}

  @Override
  protected void dispatch(WorkoutShowEventHandler handler) {
    handler.selectWorkout(this);
  }
}




