/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.RunValueRemovedEventHandler;
import com.delect.motiver.shared.RunValueModel;

public class RunValueRemovedEvent extends GwtEvent<RunValueRemovedEventHandler> {
	
  public static Type<RunValueRemovedEventHandler> TYPE = new Type<RunValueRemovedEventHandler>();
  private RunValueModel model;


  public RunValueRemovedEvent(RunValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<RunValueRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public RunValueModel getRunValue() {
		return model;  
  }

  @Override
  protected void dispatch(RunValueRemovedEventHandler handler) {
    handler.onRunValueRemoved(this);
  }
}




