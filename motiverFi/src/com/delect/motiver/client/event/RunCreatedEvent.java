/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RunCreatedEventHandler;
import com.delect.motiver.shared.RunModel;

/**
 * Event for adding Run
 * @author Antti
 *
 */
public class RunCreatedEvent extends GwtEvent<RunCreatedEventHandler> {
	
  public static Type<RunCreatedEventHandler> TYPE = new Type<RunCreatedEventHandler>();
  private RunModel model;


  public RunCreatedEvent(RunModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RunCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RunModel getRun() {
		return model;  
  }

  @Override
  protected void dispatch(RunCreatedEventHandler handler) {
    handler.onRunCreated(this);
  }
}




