/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;


import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import com.delect.motiver.client.OfflineRequestCallback.OfflineRequestCallbackHandler;

public class OfflineRequestBuilder extends RequestBuilder {
	
	public interface OfflineRequestBuilderHandler {
		String loadFromStorage(String requestData);
		void onErrorReceived(String requestData);
		void onResponseReceived(String requestData, String responseData);
	}

	private OfflineRequestBuilderHandler handler;
  private boolean isCacheable;
  private OfflineRequestCallback callback;
  private String localResponse = null;
	
	public OfflineRequestBuilder(Method httpMethod, String url) {
		super(httpMethod, url);
	}

	@Override
	public Request send() throws RequestException {
		final String requestData = super.getRequestData();
		
		callback = new OfflineRequestCallback(super.getCallback());
		callback.setHandler(new OfflineRequestCallbackHandler() {
			@Override
			public String loadFromStorage() {
				//call handler
				if(handler != null) {
          return handler.loadFromStorage(requestData);
        }
				return null;
			}
			@Override
			public void onErrorReceived() {
				//call handler
				handler.onErrorReceived(requestData);
			}
			@Override
			public boolean onResponseReceived(String responseData) {
				//call handler
				if(handler != null) {
          handler.onResponseReceived(requestData, responseData);
        }
				
	      //return true if response data differs from local storage data
	      return (localResponse == null || !localResponse.equals(responseData));
				
			}
		});

    try {
      //check if we found response from local storage
      if(isCacheable()) {
        String data = OfflineStorage.getItem(requestData);
        if(data != null) {
          
          //save local storage data to requestbuilder (to compare when fetching new data)
          saveLocalResponse(data);
          
          //call callback with data from old storage
          getCallback().onResponseReceived(null, getOldResponse(data));
        }
      }
    } catch (Exception e) {
      Motiver.showException(e);
    }
    
    return super.sendRequest(requestData, callback); 
	}
	
	public void setHandler(OfflineRequestBuilderHandler handler) {
		this.handler = handler;
	}

  /**
   * @param isCacheable
   */
  public void setCacheable(boolean isCacheable) {
    this.isCacheable = isCacheable;
  }

  /**
   * @return
   */
  public boolean isCacheable() {
    return isCacheable;
  }

  /**
   * Saves response from local storage
   * @param data
   */
  public void saveLocalResponse(String localResponse) {
    this.localResponse    = localResponse;
  }

  
  private Response getOldResponse(final String data) { 
    return new Response() { 
      @Override 
      public String getText() { 
        return data; 
      } 
      @Override 
      public String getStatusText() { 
        return null; 
      } 
      @Override 
      public int getStatusCode() { 
        return 200; 
      } 
      @Override 
      public String getHeadersAsString() { 
        return null; 
      } 
      @Override 
      public Header[] getHeaders() { 
        return null; 
      } 
      @Override 
      public String getHeader(String header) { 
        return null; 
      } 
    }; 
  }
  
}
