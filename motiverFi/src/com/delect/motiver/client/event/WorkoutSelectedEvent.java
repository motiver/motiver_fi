/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.WorkoutSelectedEventHandler;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Event for selecting workout
 * @author Antti
 *
 */
public class WorkoutSelectedEvent extends GwtEvent<WorkoutSelectedEventHandler> {
	
  public static Type<WorkoutSelectedEventHandler> TYPE = new Type<WorkoutSelectedEventHandler>();
  private boolean selectionOn = false;
  private WorkoutModel workout;
	  
  public WorkoutSelectedEvent(WorkoutModel workout, boolean selectionOn) {
    this.workout = workout;
    this.selectionOn = selectionOn;
  }
	  
  @Override
  public Type<WorkoutSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }
  public WorkoutModel getWorkout() {
    return workout;
  }
	  
  public boolean isSelected() {
    return selectionOn;
  }

  @Override
  protected void dispatch(WorkoutSelectedEventHandler handler) {
    handler.workoutSelected(this);
  }
}




