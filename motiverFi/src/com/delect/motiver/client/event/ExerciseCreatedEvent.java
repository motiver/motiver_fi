/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.ExerciseCreatedEventHandler;
import com.delect.motiver.shared.ExerciseModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class ExerciseCreatedEvent extends GwtEvent<ExerciseCreatedEventHandler> {
	
  public static Type<ExerciseCreatedEventHandler> TYPE = new Type<ExerciseCreatedEventHandler>();
  private ExerciseModel model;


  public ExerciseCreatedEvent(ExerciseModel model) {
    this.model = model;
  }

  @Override
  public Type<ExerciseCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public ExerciseModel getExercise() {
		return model;  
  }

  @Override
  protected void dispatch(ExerciseCreatedEventHandler handler) {
    handler.onExerciseCreated(this);
  }
}




