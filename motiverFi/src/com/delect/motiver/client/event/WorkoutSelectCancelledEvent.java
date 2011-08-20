/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
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




