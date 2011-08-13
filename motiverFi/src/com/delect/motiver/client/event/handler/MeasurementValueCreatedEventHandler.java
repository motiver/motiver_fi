/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event.handler;

import com.google.gwt.event.shared.EventHandler;

import com.delect.motiver.client.event.MeasurementValueCreatedEvent;

public interface MeasurementValueCreatedEventHandler extends EventHandler {
  void onMeasurementValueCreated(MeasurementValueCreatedEvent event);
}
