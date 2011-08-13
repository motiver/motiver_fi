/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.NutritionDayShowEventHandler;
import com.delect.motiver.shared.NutritionDayModel;

/**
 * Event for adding workout
 * @author Antti
 *
 */
public class NutritionDayShowEvent extends GwtEvent<NutritionDayShowEventHandler> {
	
  public static Type<NutritionDayShowEventHandler> TYPE = new Type<NutritionDayShowEventHandler>();
	private NutritionDayModel day;
	  
	  
  public NutritionDayShowEvent(NutritionDayModel day) {
    this.day = day;
  }
	  
  @Override
  public Type<NutritionDayShowEventHandler> getAssociatedType() {
    return TYPE;
  }

  public NutritionDayModel getNutritionDay() {
		return day;
	}

  @Override
  protected void dispatch(NutritionDayShowEventHandler handler) {
    handler.selectNutritionDay(this);
  }
}




