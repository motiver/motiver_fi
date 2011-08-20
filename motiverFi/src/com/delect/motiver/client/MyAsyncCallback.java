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
package com.delect.motiver.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class MyAsyncCallback<T> implements AsyncCallback<T> {

  /**
   * Empty callback if the presenter doesn't need the response from server
   */
  @SuppressWarnings("rawtypes")
  public static final MyAsyncCallback EmptyCallback = new MyAsyncCallback() {
    @Override
    public void onSuccess(Object result) {
    }
  };
  
	@Override
	public void onFailure(Throwable caught) {
	  //useless because we have our own request builder which handles all errors
	}

  @Override
  public abstract void onSuccess(T result);
}
