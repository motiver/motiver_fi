/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.BlogShowEventHandler;
import com.delect.motiver.shared.UserModel;

/**
 * Event for showing blog
 * @author Antti
 *
 */
public class BlogShowEvent extends GwtEvent<BlogShowEventHandler> {
	
  public static Type<BlogShowEventHandler> TYPE = new Type<BlogShowEventHandler>();
  private UserModel user;


  public BlogShowEvent(UserModel user) {
    this.user = user;
  }
	  
  @Override
  public Type<BlogShowEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public UserModel getUser() {
		return user;  
  }

  @Override
  protected void dispatch(BlogShowEventHandler handler) {
    handler.onBlogShow(this);
  }
}




