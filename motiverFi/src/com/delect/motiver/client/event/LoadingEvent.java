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

import com.delect.motiver.client.event.handler.LoadingEventHandler;

/**
 * Event for showing loading text
 * @author Antti
 *
 */
public class LoadingEvent extends GwtEvent<LoadingEventHandler> {
	
  public static Type<LoadingEventHandler> TYPE = new Type<LoadingEventHandler>();
  private String msg;


  public LoadingEvent(String msg) {
    this.msg = msg;
  }
	  
	  
  @Override
  public Type<LoadingEventHandler> getAssociatedType() {
    return TYPE;
  }

  public String getMessage() {
    return msg;
  }


	@Override
  protected void dispatch(LoadingEventHandler handler) {
    handler.onLoading(this);
  }
}




