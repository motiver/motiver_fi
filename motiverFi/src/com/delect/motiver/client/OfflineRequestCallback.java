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

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class OfflineRequestCallback implements RequestCallback {

	public interface OfflineRequestCallbackHandler {
		String loadFromStorage();
		void onErrorReceived(Request request, Throwable throwable);
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
			handler.onErrorReceived(request, exception);
			
    }
	}

	@Override
	public void onResponseReceived(Request request, Response response) {

	  int i = response.getStatusCode();
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
//			//get response from local storage
//			String res = handler.loadFromStorage();
//			if(res != null) {
//				callback.onResponseReceived(request, getCustomResponse(res));
//			}
//			//not found in local storage
//			else {
				handler.onErrorReceived(request, null);
//      }
		}
	}
	
	public void setHandler(OfflineRequestCallbackHandler handler) {
		this.handler = handler;
	}
}
