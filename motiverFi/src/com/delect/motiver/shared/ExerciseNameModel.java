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
package com.delect.motiver.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ExerciseNameModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -26243234748L;
	
	public ExerciseNameModel()  {
		setVideo("");
	}
	public ExerciseNameModel(long id, String name, int target) {
		setVideo("");
		set("id", id);
		set("n", name);
		set("t", target);
	}

	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public String getLocale() {
		if(get("l") != null) {
			return get("l");
    }
		else {
			return "";
    }
  }
	public String getName() {
		if(get("n") != null) {
			return get("n");
    }
		else {
			return "";
    }
  }
	public int getTarget() {
		if(get("t") != null) {
			return get("t");
    }
		else {
			return 0;
    }
  }
	public String getVideo() {
		if(get("v") != null) {
			return get("v");
    }
		else {
			return "";
    }
  }
  public String getUid() {
    if(get("uid") != null) {
      return get("uid");
    }
    else {
      return "";
    }
  }
	
	public void setLocale(String locale) {
    set("l", locale);
  }
	public void setName(String name) {
		set("n", name);
	}
	public void setTarget(int target) {
		set("t", target);
	}
	public void setVideo(String video) {
		set("v", video);
	}
  public void setUid(String uid) {
    set("uid", uid);
  }
  
  @Override
  public String toString() {
    return "ExerciseName: ['"+getName()+"', equipment: '"+getTarget()+"']";
  }
}
