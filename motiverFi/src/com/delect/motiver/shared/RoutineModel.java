/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RoutineModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8530227873655753L;
	

	public RoutineModel() {
		
	}
	public RoutineModel(String name) {
		this.setName(name);
	}

	public Date getDate() {
		return get("date");
  }
	public int getDays() {
		if(get("days") != null) {
			return get("days");
    }
		else {
			return 7;
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
	public String getInfo() {
		return get("info");
	}
	public String getName() {
    return get("name");
  }
	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
    }
  }
	public List<WorkoutModel> getWorkouts() {
		return get("w");
	}

	public void setDate(Date date) {
    set("date", date);
  }
	public void setDays(int days) {
		set("days", days);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setInfo(String info) {
		set("info", info);
	}
	public void setName(String name) {
		set("name", name);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}
	public void setWorkouts(List<WorkoutModel> w) {
		set("w", w);
	}
}
