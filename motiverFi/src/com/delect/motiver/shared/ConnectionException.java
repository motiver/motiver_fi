/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConnectionException extends Exception implements IsSerializable {

	private static final long serialVersionUID = -9090379642268642628L;
	private String message = "";
	private String source = "";

	public ConnectionException() {
	}

	public ConnectionException(String source, String message) {
		this.source  = source;
    this.message = message;
	}

	public String getMessage() {
    return message;
	}
	public String getSource() {
    return source;
	}
}
