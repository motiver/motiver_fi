/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.delect.motiver.client.AppController;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RunModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2657348L;
	
	public RunModel() {
		
	}
	public RunModel(Long id, String name) {
		setId(id);
		setName(name);
	}

	public double getDistance() {
		if(get("d") != null) {
			return get("d");
    }
		else {
			return 0L;
    }
  }
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	/**
	 * Return name for client side
	 * @return
	 */
	public String getNameClient() {
		String str = "-" + AppController.Lang.NoName() + "-";
		if(get("n") != null && get("n") instanceof String && ((String)get("n")).length() > 0) {
      str = get("n");
    }
		return str;
	}
	/**
	 * Return name for server side
	 * @return
	 */
	public String getNameServer() {
		if(get("n") != null) {
			return get("n");
    }
		else {
			return "";
    }
  }
	public long getTargetTime() {
		if(get("t") != null) {
			return get("t");
    }
		else {
			return 0L;
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
	public List<RunValueModel> getValues() {
		return get("v");
	}

	public void setDistance(double distance) {
		set("d", distance);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setName(String name) {
		set("n", name);
	}
	public void setTargetTime(long targetTime) {
		set("t", targetTime);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}
	public void setValues(List<RunValueModel> values) {
		set("v", values);
	}
}
