/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.OfflineModeEventHandler;

/**
 * Event for showing loading text
 * @author Antti
 *
 */
public class OfflineModeEvent extends GwtEvent<OfflineModeEventHandler> {
	
  public static Type<OfflineModeEventHandler> TYPE = new Type<OfflineModeEventHandler>();
  boolean isOn;


  public OfflineModeEvent(boolean isOn) {
    this.isOn = isOn;
  }
	  
	  
  @Override
  public Type<OfflineModeEventHandler> getAssociatedType() {
    return TYPE;
  }

  public boolean isOn() {
    return isOn;
  }


	@Override
  protected void dispatch(OfflineModeEventHandler handler) {
    handler.onModeChange(this);
  }
}




