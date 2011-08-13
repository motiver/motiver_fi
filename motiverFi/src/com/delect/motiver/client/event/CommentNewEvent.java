/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CommentNewEventHandler;

/**
 * Event for adding Cardio
 * @author Antti
 *
 */
public class CommentNewEvent extends GwtEvent<CommentNewEventHandler> {
	
  public static Type<CommentNewEventHandler> TYPE = new Type<CommentNewEventHandler>();
  private int count = 0;


  public CommentNewEvent(int count) {
    this.count = count;
  }
	  
  @Override
  public Type<CommentNewEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public int getCount() {
    return count;
  }

  @Override
  protected void dispatch(CommentNewEventHandler handler) {
    handler.onNewComment(this);
  }
}




