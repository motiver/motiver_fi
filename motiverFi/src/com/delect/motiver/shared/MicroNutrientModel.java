/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MicroNutrientModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -230029L;
	public MicroNutrientModel()  {
	}
	public MicroNutrientModel(int nameId) {
		set("n", nameId);
	}

	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public int getNameId() {
		if(get("n") != null) {
			return get("n");
    }
		else {
			return 0;
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
	public double getValue() {
		if(get("v") != null) {
			return get("v");
    }
		else {
			return 0D;
    }
  }
	

	public void setId(long id) {
		set("id", id);
	}
	public void setNameId(int n) {
		set("n", n);
	}
	public void setUid(String uid) {
    set("uid", uid);
  }
	public void setValue(double value) {
    set("v", value);
	}
}
