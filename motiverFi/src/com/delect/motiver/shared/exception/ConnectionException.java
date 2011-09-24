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
package com.delect.motiver.shared.exception;

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
