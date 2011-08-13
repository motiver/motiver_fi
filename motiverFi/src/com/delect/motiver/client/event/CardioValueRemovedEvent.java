/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CardioValueRemovedEventHandler;
import com.delect.motiver.shared.CardioValueModel;

public class CardioValueRemovedEvent extends GwtEvent<CardioValueRemovedEventHandler> {
	
  public static Type<CardioValueRemovedEventHandler> TYPE = new Type<CardioValueRemovedEventHandler>();
  private final CardioValueModel model;


  public CardioValueRemovedEvent(CardioValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CardioValueRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CardioValueModel getCardioValue() {
		return model;  
  }

  @Override
  protected void dispatch(CardioValueRemovedEventHandler handler) {
    handler.onCardioValueRemoved(this);
  }
}




