/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.FoodCreatedEventHandler;
import com.delect.motiver.shared.FoodModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class FoodCreatedEvent extends GwtEvent<FoodCreatedEventHandler> {
	
  public static Type<FoodCreatedEventHandler> TYPE = new Type<FoodCreatedEventHandler>();
  private FoodModel model;


  public FoodCreatedEvent(FoodModel model) {
    this.model = model;
  }

  @Override
  public Type<FoodCreatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public FoodModel getFood() {
		return model;  
  }

  @Override
  protected void dispatch(FoodCreatedEventHandler handler) {
    handler.onFoodCreated(this);
  }
}




