/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MeasurementRemovedEventHandler;
import com.delect.motiver.shared.MeasurementModel;

public class MeasurementRemovedEvent extends GwtEvent<MeasurementRemovedEventHandler> {
	
  public static Type<MeasurementRemovedEventHandler> TYPE = new Type<MeasurementRemovedEventHandler>();
  private MeasurementModel model;


  public MeasurementRemovedEvent(MeasurementModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<MeasurementRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MeasurementModel getMeasurement() {
		return model;  
  }

  @Override
  protected void dispatch(MeasurementRemovedEventHandler handler) {
    handler.onMeasurementRemoved(this);
  }
}




