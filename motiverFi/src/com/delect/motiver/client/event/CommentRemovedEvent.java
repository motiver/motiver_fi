/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CommentRemovedEventHandler;
import com.delect.motiver.shared.CommentModel;

public class CommentRemovedEvent extends GwtEvent<CommentRemovedEventHandler> {
	
  public static Type<CommentRemovedEventHandler> TYPE = new Type<CommentRemovedEventHandler>();
  private CommentModel model;


  public CommentRemovedEvent(CommentModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CommentRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CommentModel getComment() {
		return model;  
  }

  @Override
  protected void dispatch(CommentRemovedEventHandler handler) {
    handler.onCommentRemoved(this);
  }
}




