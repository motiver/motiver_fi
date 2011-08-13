/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CardioCreatedEventHandler;
import com.delect.motiver.shared.CardioModel;

/**
 * Event for adding Cardio
 * @author Antti
 *
 */
public class CardioCreatedEvent extends GwtEvent<CardioCreatedEventHandler> {
	
  public static Type<CardioCreatedEventHandler> TYPE = new Type<CardioCreatedEventHandler>();
  private final CardioModel model;


  public CardioCreatedEvent(CardioModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CardioCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CardioModel getCardio() {
		return model;  
  }

  @Override
  protected void dispatch(CardioCreatedEventHandler handler) {
    handler.onCardioCreated(this);
  }
}




