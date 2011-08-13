/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.GuideValueCreatedEventHandler;
import com.delect.motiver.shared.GuideValueModel;

/**
 * Event for adding GuideValue
 * @author Antti
 *
 */
public class GuideValueCreatedEvent extends GwtEvent<GuideValueCreatedEventHandler> {
	
  public static Type<GuideValueCreatedEventHandler> TYPE = new Type<GuideValueCreatedEventHandler>();
  private GuideValueModel model;
	 

  public GuideValueCreatedEvent(GuideValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<GuideValueCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public GuideValueModel getGuideValue() {
		return model;  
  }

  @Override
  protected void dispatch(GuideValueCreatedEventHandler handler) {
    handler.onGuideValueCreated(this);
  }
}




