/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.InfoMessageEventHandler;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Event for showing blog
 * @author Antti
 *
 */
public class InfoMessageEvent extends GwtEvent<InfoMessageEventHandler> {
	
  public static Type<InfoMessageEventHandler> TYPE = new Type<InfoMessageEventHandler>();
	  
  private Listener<BaseEvent> clickListener = null;
  private MessageColor color = MessageColor.COLOR_DEFAULT;
  String message = "";

  public InfoMessageEvent(MessageColor color, String message) {
    this.message = message;
    this.color = color;
  }
	  
  @Override
  public Type<InfoMessageEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public Listener<BaseEvent> getClickListener() {
    return clickListener;
  }
	  
  public String getMessage() {
		return message;  
  }
	  
  public MessageColor getMessageColor() {
    return color;
  }
  public void setClickListener(Listener<BaseEvent> clickListener) {
    this.clickListener = clickListener;
  }

  @Override
  protected void dispatch(InfoMessageEventHandler handler) {
    handler.onInfoMessage(this);
  }
}




