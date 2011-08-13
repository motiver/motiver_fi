/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CardioRemovedEventHandler;
import com.delect.motiver.shared.CardioModel;

public class CardioRemovedEvent extends GwtEvent<CardioRemovedEventHandler> {
	
  public static Type<CardioRemovedEventHandler> TYPE = new Type<CardioRemovedEventHandler>();
  private final CardioModel model;


  public CardioRemovedEvent(CardioModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CardioRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CardioModel getCardio() {
		return model;  
  }

  @Override
  protected void dispatch(CardioRemovedEventHandler handler) {
    handler.onCardioRemoved(this);
  }
}




