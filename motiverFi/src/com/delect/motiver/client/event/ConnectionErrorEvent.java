/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.ConnectionErrorEventHandler;

/**
 * Event for connection errors
 * @author Antti
 *
 */
public class ConnectionErrorEvent extends GwtEvent<ConnectionErrorEventHandler> {
	  
	public static Type<ConnectionErrorEventHandler> TYPE = new Type<ConnectionErrorEventHandler>();

	private int code = 0;

	public ConnectionErrorEvent(int code) {
		this.code  = code;
	}
	  
	@Override
	public Type<ConnectionErrorEventHandler> getAssociatedType() {
		return TYPE;
	}

	public int getCode() {
		return code;
	}

	@Override
	protected void dispatch(ConnectionErrorEventHandler handler) {
		handler.onError(this);
	}
}




