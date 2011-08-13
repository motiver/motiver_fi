/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MealRemovedEventHandler;
import com.delect.motiver.shared.MealModel;

public class MealRemovedEvent extends GwtEvent<MealRemovedEventHandler> {
	
  public static Type<MealRemovedEventHandler> TYPE = new Type<MealRemovedEventHandler>();
  private MealModel model;


  public MealRemovedEvent(MealModel model) {
    this.model = model;
  }

  @Override
  public Type<MealRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MealModel getMeal() {
    return model;  
  }

  @Override
  protected void dispatch(MealRemovedEventHandler handler) {
    handler.onMealRemoved(this);
  }
}




