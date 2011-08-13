/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.GuideValueRemovedEventHandler;
import com.delect.motiver.shared.GuideValueModel;

public class GuideValueRemovedEvent extends GwtEvent<GuideValueRemovedEventHandler> {
	
  public static Type<GuideValueRemovedEventHandler> TYPE = new Type<GuideValueRemovedEventHandler>();
  private GuideValueModel model;


  public GuideValueRemovedEvent(GuideValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<GuideValueRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public GuideValueModel getGuideValue() {
		return model;  
  }

  @Override
  protected void dispatch(GuideValueRemovedEventHandler handler) {
    handler.onGuideValueRemoved(this);
  }
}




