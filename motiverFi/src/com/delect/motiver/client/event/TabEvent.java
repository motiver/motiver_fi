/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.TabEventHandler;

public class TabEvent extends GwtEvent<TabEventHandler> {
  public static Type<TabEventHandler> TYPE = new Type<TabEventHandler>();


  private int index;

  /**
  * Check login event
  * @param tab index : new tab index
  */
  public TabEvent(int index) {
    this.index = index;
  }
	  
  @Override
  public Type<TabEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  /**
  * Get tab index associated with this event
  * @return index
  */
  public int getIndex() {
		return index;  
  }

  @Override
  protected void dispatch(TabEventHandler handler) {
    handler.onTabChanged(this);
  }
}


