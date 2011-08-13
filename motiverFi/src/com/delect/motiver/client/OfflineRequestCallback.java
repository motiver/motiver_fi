/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class OfflineRequestCallback implements RequestCallback {

	public interface OfflineRequestCallbackHandler {
		String loadFromStorage();
		void onErrorReceived();
		/**
		 * @param responseData
		 * @return true if data changed. If false, same data that have already been delivered to client
		 */
		boolean onResponseReceived(String responseData);
	}
	
	private static Response getCustomResponse(final String response) {
		return new Response() { 
      @Override 
      public String getHeader(String header) { 
        return ""; 
      } 
      @Override 
      public Header[] getHeaders() { 
        return new Header[0]; 
      } 
      @Override 
      public String getHeadersAsString() { 
        return ""; 
      } 
      @Override 
      public int getStatusCode() { 
        return 200; 
      } 
      @Override 
      public String getStatusText() { 
        return ""; 
      } 
      @Override 
      public String getText() { 
        return response; 
      } 
		}; 
	}
	//serialized response from server
	private RequestCallback callback;
	private OfflineRequestCallbackHandler handler;

	public OfflineRequestCallback(RequestCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onError(Request request, Throwable exception) {
		//get response from local storage
		String res = handler.loadFromStorage();
		if(res != null) {
			callback.onResponseReceived(request, getCustomResponse(res));
		}
		//not found in local storage
		else {
			handler.onErrorReceived();
    }
	}

	@Override
	public void onResponseReceived(Request request, Response response) {

		if (response.getStatusCode() == 200) {
      final String serializedResponse = response.getText();

			//call handler to save it to local storage
			boolean dataChanged = handler.onResponseReceived(serializedResponse);
			
			//if data changed (compared to local storage) -> call callback
			if(dataChanged) {
        callback.onResponseReceived(request, response);
			}
		}
		else {
			//get response from local storage
			String res = handler.loadFromStorage();
			if(res != null) {
				callback.onResponseReceived(request, getCustomResponse(res));
			}
			//not found in local storage
			else {
				handler.onErrorReceived();
      }
		}
	}
	
	public void setHandler(OfflineRequestCallbackHandler handler) {
		this.handler = handler;
	}
}
