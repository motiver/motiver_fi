/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MeasurementCreatedEventHandler;
import com.delect.motiver.shared.MeasurementModel;

/**
 * Event for adding Measurement
 * @author Antti
 *
 */
public class MeasurementCreatedEvent extends GwtEvent<MeasurementCreatedEventHandler> {
	
  public static Type<MeasurementCreatedEventHandler> TYPE = new Type<MeasurementCreatedEventHandler>();
  private MeasurementModel model;


  public MeasurementCreatedEvent(MeasurementModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<MeasurementCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MeasurementModel getMeasurement() {
		return model;  
  }

  @Override
  protected void dispatch(MeasurementCreatedEventHandler handler) {
    handler.onMeasurementCreated(this);
  }
}




