/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CardioShowEventHandler;
import com.delect.motiver.shared.CardioModel;

/**
 * Event for showing cardio
 * @author Antti
 *
 */
public class CardioShowEvent extends GwtEvent<CardioShowEventHandler> {
	
  public static Type<CardioShowEventHandler> TYPE = new Type<CardioShowEventHandler>();
  private final CardioModel model;


  public CardioShowEvent(CardioModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CardioShowEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CardioModel getCardio() {
		return model;  
  }

  @Override
  protected void dispatch(CardioShowEventHandler handler) {
    handler.onCardioShow(this);
  }
}




