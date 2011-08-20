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
