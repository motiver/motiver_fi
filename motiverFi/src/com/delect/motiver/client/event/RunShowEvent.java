/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RunShowEventHandler;
import com.delect.motiver.shared.RunModel;

/**
 * Event for showing Run
 * @author Antti
 *
 */
public class RunShowEvent extends GwtEvent<RunShowEventHandler> {
	
  public static Type<RunShowEventHandler> TYPE = new Type<RunShowEventHandler>();
  private RunModel model;


  public RunShowEvent(RunModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RunShowEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RunModel getRun() {
		return model;  
  }

  @Override
  protected void dispatch(RunShowEventHandler handler) {
    handler.onRunShow(this);
  }
}




