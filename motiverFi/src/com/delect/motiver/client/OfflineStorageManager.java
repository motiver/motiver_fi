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

import com.google.gwt.storage.client.Storage;

/** Local storage for HTML5 capable browsers.
 */
public class OfflineStorageManager {

	static Storage storage = Storage.getLocalStorageIfSupported();
	
  private static OfflineStorageManager instance;
	
	public static OfflineStorageManager getInstance() {
	  if(instance == null)
	    instance = new OfflineStorageManager();
	  
	  return instance;
	}
	/**
	 * Checks if local storage is too big and removes old values
	 */
	private void checkSize() {
    
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
	public String getItem(String key) {
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
	public void setItem(String key, String value) {
		if(storage == null) {
      return;
    }

		storage.setItem(key, value);
		
		checkSize();
	}
  
  /**
   * Clears all value from storage
   * @param key 
   * @return String
   */
  public void clear() {
    if(storage == null) {
      return;
    }

    storage.clear();
  }
}
