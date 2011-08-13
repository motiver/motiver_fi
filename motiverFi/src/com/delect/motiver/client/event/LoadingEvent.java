/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.LoadingEventHandler;

/**
 * Event for showing loading text
 * @author Antti
 *
 */
public class LoadingEvent extends GwtEvent<LoadingEventHandler> {
	
  public static Type<LoadingEventHandler> TYPE = new Type<LoadingEventHandler>();
  private String msg;


  public LoadingEvent(String msg) {
    this.msg = msg;
  }
	  
	  
  @Override
  public Type<LoadingEventHandler> getAssociatedType() {
    return TYPE;
  }

  public String getMessage() {
    return msg;
  }


	@Override
  protected void dispatch(LoadingEventHandler handler) {
    handler.onLoading(this);
  }
}




