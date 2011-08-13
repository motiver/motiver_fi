/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.ExerciseUpdatedEventHandler;
import com.delect.motiver.shared.ExerciseModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class ExerciseUpdatedEvent extends GwtEvent<ExerciseUpdatedEventHandler> {
	
  public static Type<ExerciseUpdatedEventHandler> TYPE = new Type<ExerciseUpdatedEventHandler>();
  private ExerciseModel model;


  public ExerciseUpdatedEvent(ExerciseModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<ExerciseUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public ExerciseModel getExercise() {
		return model;  
  }

  @Override
  protected void dispatch(ExerciseUpdatedEventHandler handler) {
    handler.onExerciseUpdated(this);
  }
}




