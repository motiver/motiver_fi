/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RunRemovedEventHandler;
import com.delect.motiver.shared.RunModel;

public class RunRemovedEvent extends GwtEvent<RunRemovedEventHandler> {
	
  public static Type<RunRemovedEventHandler> TYPE = new Type<RunRemovedEventHandler>();
  private RunModel model;


  public RunRemovedEvent(RunModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RunRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RunModel getRun() {
		return model;  
  }

  @Override
  protected void dispatch(RunRemovedEventHandler handler) {
    handler.onRunRemoved(this);
  }
}




