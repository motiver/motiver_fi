/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RunValueCreatedEventHandler;
import com.delect.motiver.shared.RunValueModel;

/**
 * Event for adding RunValue
 * @author Antti
 *
 */
public class RunValueCreatedEvent extends GwtEvent<RunValueCreatedEventHandler> {
	
  public static Type<RunValueCreatedEventHandler> TYPE = new Type<RunValueCreatedEventHandler>();
  private RunValueModel model;
	 

  public RunValueCreatedEvent(RunValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RunValueCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RunValueModel getRunValue() {
		return model;  
  }

  @Override
  protected void dispatch(RunValueCreatedEventHandler handler) {
    handler.onRunValueCreated(this);
  }
}




