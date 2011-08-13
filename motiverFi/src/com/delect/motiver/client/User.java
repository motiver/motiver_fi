/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;

import java.util.Date;

import com.google.gwt.user.client.Cookies;

public class User {
	
	/*
	 * Saves username,password
	 * Saves them also to cookies
	 * Parameters: name/password, rememberme (true=infinite cookies)
	 */
	public static void saveUsername(String username, String password, boolean rememberme) {
		try {
			//if null -> remove
			if(username.length() == 0 || password.length() == 0) {
				Cookies.removeCookie("USER_NAME", "/");	
				Cookies.removeCookie("USER_PASSWORD", "/");
				Cookies.removeCookie("USER_REMEMBERME", "/");		
				return;
			}

			Date now = new Date(System.currentTimeMillis() + 5L * 1000L);
			if(rememberme) {
				long msecs = 90L * 24L * 60L * 60L * 1000L; // 90 days in milliseconds 
				now = new Date(System.currentTimeMillis() + msecs); 
			}
			//save to cookies
			Cookies.setCookie("USER_NAME", username, now, null, "/", false); 
			Cookies.setCookie("USER_PASSWORD", password, now, null, "/", false); 
			Cookies.setCookie("USER_REMEMBERME", String.valueOf(rememberme), now, null, "/", false);
		} catch (Exception e) {
      Motiver.showException(e);
		} 
	}
	public String Age = "";
	public String Country = "";
	public String Currency = "€";

	public String Dateformat = "dd.MM.y";
	public String Email = "";
	public String Height = "";
	public String HomeGym = "";
	public String Homeurl = "";
	public long Id = 0;
	public String Language = "";
	public int MeasurementSystem = 2;	//1=metric, 2=us
	public String Name = "";
	public String Password = "";	//md5
	public String Phone = "";
	public String Postcode = "";
	public boolean RememberMe = false;
	public int Sex = 0; //0=female, 1=male
	public String Signupdate = "";
	public String Street = "";
	public String Surname = "";
	public String Timeformat = "HH:mm";
	public String TimeformatServer = "HH:mm:ss";
	public String Timezone = "";
	public String Town = "";
	public String Username = "";
	public String Weight = "";
	
	public String WeightUnit = "g";
	
	public User() {
    //load info from cookie
    Username = Cookies.getCookie("USER_NAME");
    Password = Cookies.getCookie("USER_PASSWORD");
			
    //check values
    if(Username == null) {
      Username = "";
    }
    if(Password == null) {
      Password = "";
    }
	}
	

}
