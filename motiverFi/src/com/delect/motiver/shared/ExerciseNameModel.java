/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
}
