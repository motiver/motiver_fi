/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MealCreatedEventHandler;
import com.delect.motiver.shared.MealModel;

public class MealCreatedEvent extends GwtEvent<MealCreatedEventHandler> {
	
  public static Type<MealCreatedEventHandler> TYPE = new Type<MealCreatedEventHandler>();
  private MealModel model;

  public MealCreatedEvent(MealModel model) {
    this.model = model;
  }
  @Override
  public Type<MealCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MealModel getMeal() {
    return model;  
  }

  @Override
  protected void dispatch(MealCreatedEventHandler handler) {
    handler.onMealCreated(this);
  }
}




