/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.FoodUpdatedEventHandler;
import com.delect.motiver.shared.FoodModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class FoodUpdatedEvent extends GwtEvent<FoodUpdatedEventHandler> {
	
  public static Type<FoodUpdatedEventHandler> TYPE = new Type<FoodUpdatedEventHandler>();
  private FoodModel model;


  public FoodUpdatedEvent(FoodModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<FoodUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public FoodModel getFood() {
		return model;  
  }

  @Override
  protected void dispatch(FoodUpdatedEventHandler handler) {
    handler.onFoodUpdated(this);
  }
}




