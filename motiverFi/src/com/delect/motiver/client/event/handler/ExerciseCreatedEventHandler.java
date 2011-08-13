/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event.handler;

import com.google.gwt.event.shared.EventHandler;

import com.delect.motiver.client.event.ExerciseCreatedEvent;

public interface ExerciseCreatedEventHandler extends EventHandler {
  void onExerciseCreated(ExerciseCreatedEvent event);
}
