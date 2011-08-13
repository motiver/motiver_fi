/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CardioValueCreatedEventHandler;
import com.delect.motiver.shared.CardioValueModel;

/**
 * Event for adding CardioValue
 * @author Antti
 *
 */
public class CardioValueCreatedEvent extends GwtEvent<CardioValueCreatedEventHandler> {
	
  public static Type<CardioValueCreatedEventHandler> TYPE = new Type<CardioValueCreatedEventHandler>();
  private final CardioValueModel model;
	 

  public CardioValueCreatedEvent(CardioValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CardioValueCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CardioValueModel getCardioValue() {
		return model;  
  }

  @Override
  protected void dispatch(CardioValueCreatedEventHandler handler) {
    handler.onCardioValueCreated(this);
  }
}




