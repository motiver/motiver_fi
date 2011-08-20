/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
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




