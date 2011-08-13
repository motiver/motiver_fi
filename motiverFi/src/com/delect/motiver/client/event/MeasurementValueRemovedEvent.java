/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MeasurementValueRemovedEventHandler;
import com.delect.motiver.shared.MeasurementValueModel;

public class MeasurementValueRemovedEvent extends GwtEvent<MeasurementValueRemovedEventHandler> {
	
  public static Type<MeasurementValueRemovedEventHandler> TYPE = new Type<MeasurementValueRemovedEventHandler>();
  private MeasurementValueModel model;


  public MeasurementValueRemovedEvent(MeasurementValueModel model) {
    this.model = model;
  }

  @Override
  public Type<MeasurementValueRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MeasurementValueModel getMeasurementValue() {
    return model;  
  }

  @Override
  protected void dispatch(MeasurementValueRemovedEventHandler handler) {
    handler.onMeasurementValueRemoved(this);
  }
}




