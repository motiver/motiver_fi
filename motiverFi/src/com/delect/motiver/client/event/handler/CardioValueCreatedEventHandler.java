/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event.handler;

import com.google.gwt.event.shared.EventHandler;

import com.delect.motiver.client.event.CardioValueCreatedEvent;

public interface CardioValueCreatedEventHandler extends EventHandler {
  void onCardioValueCreated(CardioValueCreatedEvent event);
}
