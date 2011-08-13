/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.FoodRemovedEventHandler;
import com.delect.motiver.shared.FoodModel;

public class FoodRemovedEvent extends GwtEvent<FoodRemovedEventHandler> {
	
  public static Type<FoodRemovedEventHandler> TYPE = new Type<FoodRemovedEventHandler>();
  private FoodModel model;


  public FoodRemovedEvent(FoodModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<FoodRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public FoodModel getFood() {
		return model;  
  }

  @Override
  protected void dispatch(FoodRemovedEventHandler handler) {
    handler.onFoodRemoved(this);
  }
}




