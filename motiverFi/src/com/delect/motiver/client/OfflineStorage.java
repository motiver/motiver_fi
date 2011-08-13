/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;

import com.google.gwt.storage.client.Storage;

/** Local storage for HTML5 capable browsers.
 */
public class OfflineStorage {

	static Storage storage = Storage.getLocalStorageIfSupported();
	
	/**
	 * Checks if local storage is too big and removes old values
	 */
	public static void checkSize() {
    if(storage == null) {
      return;
    }
    
	  try {
      final int length = storage.getLength();
      
      if(length > 500) {
        for(int i = length-1; i > 100; i--) {
          storage.removeItem(storage.key(i));
        }
      }
    } catch (Exception e) {
      Motiver.showException(e);
    }
	  
	}
	
	/**
	 * Loads value from local storage
	 * @param key	
	 * @return String
	 */
	public static String getItem(String key) {
		if(storage == null) {
      return null;
    }

		return storage.getItem(key);
	}

	/**
	 * Saves value to local storage
	 * @param key
	 * @param value
	 */
	public static void setItem(String key, String value) {
		if(storage == null) {
      return;
    }

		storage.setItem(key, value);
	}
}
