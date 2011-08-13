/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MealShowEventHandler;
import com.delect.motiver.shared.MealModel;

/**
 * Event for selecting meal
 * @author Antti
 *
 */
public class MealShowEvent extends GwtEvent<MealShowEventHandler> {
	
  public static Type<MealShowEventHandler> TYPE = new Type<MealShowEventHandler>();
  private MealModel meal;
	  
  public MealShowEvent(MealModel meal) {
    this.meal = meal;
  }
	  
  @Override
  public Type<MealShowEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public MealModel getMeal() {
    return meal;
  }

  @Override
  protected void dispatch(MealShowEventHandler handler) {
    handler.selectMeal(this);
  }
}




