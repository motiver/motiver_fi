/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.MealSelectedEventHandler;
import com.delect.motiver.shared.MealModel;

/**
 * Event for selecting meal
 * @author Antti
 *
 */
public class MealSelectedEvent extends GwtEvent<MealSelectedEventHandler> {
	
  public static Type<MealSelectedEventHandler> TYPE = new Type<MealSelectedEventHandler>();
  private MealModel meal;
  private boolean selectionOn = false;
	  
  public MealSelectedEvent(MealModel meal, boolean selectionOn) {
    this.meal = meal;
    this.selectionOn = selectionOn;
  }
	  
  @Override
  public Type<MealSelectedEventHandler> getAssociatedType() {
    return TYPE;
  }
  public MealModel getMeal() {
    return meal;
  }
	  
  public boolean isSelected() {
    return selectionOn;
  }

  @Override
  protected void dispatch(MealSelectedEventHandler handler) {
    handler.mealSelected(this);
  }
}




