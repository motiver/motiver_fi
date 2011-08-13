/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.delect.motiver.shared.WorkoutModel;

public class ServerConnection {

	public interface CheckLoginHandler {
		void checkOk(User user);
	}
	public interface DaysWorkoutsFetchHandler {
		void workoutsFetched(WorkoutModel[][] models);
	}
	
	//HANDLERS
	public interface ResponseHandler {
		public void loadError(Throwable throwable);
		public void loadOk(JSONObject json);
	}
	static List<ServerConnection> Connections = new ArrayList<ServerConnection>();
	
	/*
	 * Parses user info response
	 * Parameters: json containing user info
	 * Return: user object containing info
	 */
	public static User parseUserInfoResponse(JSONObject json) {
		User userClass = new User();
		
		try {
			JSONArray user_arr = json.get("user").isArray();
			JSONObject user = user_arr.get(0).isObject();
			
			userClass.Id = Long.parseLong(user.get("id").isString().stringValue());
			userClass.Username = user.get("user").isString().stringValue();
			userClass.Password = user.get("password").isString().stringValue();
			userClass.Name = user.get("name").isString().stringValue();
			userClass.Surname = user.get("surname").isString().stringValue();
			userClass.Email = user.get("email").isString().stringValue();
			userClass.Age = user.get("age").isString().stringValue();
			userClass.Phone = user.get("phone").isString().stringValue();
			userClass.Street = user.get("street").isString().stringValue();
			userClass.Postcode = user.get("postcode").isString().stringValue();
			userClass.Town = user.get("town").isString().stringValue();
			userClass.Country = user.get("country").isString().stringValue();
			userClass.Signupdate = user.get("signupdate").isString().stringValue();
			userClass.Language = user.get("language").isString().stringValue();
			userClass.MeasurementSystem = (int)(user.get("measurement_system").isNumber().doubleValue());
			userClass.Homeurl = user.get("homeurl").isString().stringValue();
			userClass.Height = user.get("height").isString().stringValue();
			userClass.Weight = user.get("weight").isString().stringValue();
			userClass.Sex = Integer.parseInt(user.get("sex").isString().stringValue());
			userClass.HomeGym = user.get("homegym").isString().stringValue();
			userClass.Timezone = user.get("timezone").isString().stringValue();
			
			if(userClass.Id == 0 || userClass.Username.length() == 0 || userClass.Password.length() == 0) {
        return null;
      }
			
			return userClass;
			
		} catch (Exception e) {
      Motiver.showException(e);
		  return null;
		}
		
	}
	
		 
	/*
	 * Cancels all connections
	 */
	static void cancelAll() {
		for(int i=0; i < Connections.size(); i++) {
		  if(Connections.get(i) != null) {
				Connections.get(i).isCancelled = true;
		  }
		}
	}
	
	public boolean isCancelled = false;
	

	public void connect(String url, final ResponseHandler handler) {		
		//add connection to array
		final ServerConnection thisCon = this;
		Connections.add(thisCon);
		
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.setTimeout(30000);
		jsonp.requestObject(URL.encode(url), new AsyncCallback<JavaScriptObject>() {
			public void onFailure(Throwable throwable) {
				Connections.remove(thisCon);

				if(!isCancelled) {
          handler.loadError(throwable);
        }
			}

		  public void onSuccess(JavaScriptObject feed) {
        JSONObject json = null;
				try {
					json = new JSONObject(feed);
	        if(!isCancelled) {
            handler.loadOk(json);
          }
				} catch (Exception e) {		      
					if(!isCancelled) {
            handler.loadError(e);
          }
				}
		        
				Connections.remove(thisCon);
				    
	    }
		});
	}
}
