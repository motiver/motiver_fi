/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RunValueModel extends BaseModelData implements IsSerializable, Comparable<RunValueModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -44342654L;
	

	@Override
	public int compareTo(RunValueModel value) {
		return (value.getDate().getTime() < getDate().getTime())? 1 : 0;
	}
	public int getCalories() {
		if(get("c") != null) {
			return get("c");
    }
		else {
			return 0;
    }
  }
	public Date getDate() {
		if(get("d") != null) {
			return get("d");
		}
		return null;
  }
	public long getDuration() {
		if(get("du") != null) {
			return get("du");
    }
		else {
			return 0L;
    }
  }
	public Long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public String getInfo() {
    return get("i");
  }
	public RunModel getName() {
		return get("n");
	}
	public int getPulse() {
		if(get("pu") != null) {
			return get("pu");
    }
		else {
			return 0;
    }
  }
  public int getPulseMax() {
    if(get("pum") != null) {
      return get("pum");
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
	public void setCalories(int calories) {
    set("c", calories);
  }
	public void setDate(Date date) {
    set("d", date);
  }
	public void setDuration(long duration) {
    set("du", duration);
  }
	public void setId(Long id) {
		set("id", id);
	}
	public void setInfo(String info) {
    set("i", info);
  }
	public void setName(RunModel name) {
		set("n", name);
	}
	public void setPulse(int pulse) {
    set("pu", pulse);
  }
  public void setPulseMax(int pulseMax) {
    set("pum", pulseMax);
  }

	public void setUid(String uid) {
		set("uid", uid);
	}
}
