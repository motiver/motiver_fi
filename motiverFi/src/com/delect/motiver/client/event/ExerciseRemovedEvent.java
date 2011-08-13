/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.ExerciseRemovedEventHandler;
import com.delect.motiver.shared.ExerciseModel;

public class ExerciseRemovedEvent extends GwtEvent<ExerciseRemovedEventHandler> {
	
  public static Type<ExerciseRemovedEventHandler> TYPE = new Type<ExerciseRemovedEventHandler>();
  private ExerciseModel model;


  public ExerciseRemovedEvent(ExerciseModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<ExerciseRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public ExerciseModel getExercise() {
		return model;  
  }

  @Override
  protected void dispatch(ExerciseRemovedEventHandler handler) {
    handler.onExerciseRemoved(this);
  }
}




