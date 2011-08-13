/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
