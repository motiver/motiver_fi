/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MeasurementValueModel extends BaseModel implements IsSerializable, Comparable<MeasurementValueModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2086567262L;
	public MeasurementValueModel() {
		
	}

	//normal food model
	public MeasurementValueModel(MeasurementModel name) {
		setName(name);
	}
	
	@Override
	public int compareTo(MeasurementValueModel value) {
		return (value.getDate().getTime() < getDate().getTime())? 1 : 0;
	}
	public Date getDate() {
		if(get("d") != null) {
			return get("d");
		}
		return null;
	}
	public Long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public MeasurementModel getName() {
		return get("n");
  }
	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
    }
  }

	public Double getValue() {
		if(get("v") != null) {
			return get("v");
		}
		else {
			return 0D;
		}
  }
	public void setDate(Date date) {
    set("d", date);
	}
	public void setId(Long id) {
		set("id", id);
	}

	public void setName(MeasurementModel name) {
		set("n", name);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}

	public void setValue(Double value) {
    set("v", value);
	}
}
