/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MeasurementShowEventHandler;
import com.delect.motiver.shared.MeasurementModel;

/**
 * Event for showing cardio
 * @author Antti
 *
 */
public class MeasurementShowEvent extends GwtEvent<MeasurementShowEventHandler> {
	
  public static Type<MeasurementShowEventHandler> TYPE = new Type<MeasurementShowEventHandler>();
  private MeasurementModel model;


  public MeasurementShowEvent(MeasurementModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<MeasurementShowEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MeasurementModel getMeasurement() {
		return model;  
  }

  @Override
  protected void dispatch(MeasurementShowEventHandler handler) {
    handler.onMeasurementShow(this);
  }
}




