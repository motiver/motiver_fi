/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MeasurementValueCreatedEventHandler;
import com.delect.motiver.shared.MeasurementValueModel;

/**
 * Event for adding MeasurementValue
 * @author Antti
 *
 */
public class MeasurementValueCreatedEvent extends GwtEvent<MeasurementValueCreatedEventHandler> {
	
  public static Type<MeasurementValueCreatedEventHandler> TYPE = new Type<MeasurementValueCreatedEventHandler>();
  private MeasurementValueModel model;
	 

  public MeasurementValueCreatedEvent(MeasurementValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<MeasurementValueCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MeasurementValueModel getMeasurementValue() {
		return model;  
  }

  @Override
  protected void dispatch(MeasurementValueCreatedEventHandler handler) {
    handler.onMeasurementValueCreated(this);
  }
}




