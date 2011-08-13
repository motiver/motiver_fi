/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MealUpdatedEventHandler;
import com.delect.motiver.shared.MealModel;

public class MealUpdatedEvent extends GwtEvent<MealUpdatedEventHandler> {
	
  public static Type<MealUpdatedEventHandler> TYPE = new Type<MealUpdatedEventHandler>();
  private MealModel model;


  public MealUpdatedEvent(MealModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<MealUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MealModel getMeal() {
    return model;  
  }

  @Override
  protected void dispatch(MealUpdatedEventHandler handler) {
    handler.onMealUpdated(this);
  }
}




