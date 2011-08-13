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

public class GuideValueModel extends BaseModelData implements IsSerializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3241687515486940695L;
	public GuideValueModel()  {
		
	}
	public GuideValueModel(Long id, String name) {
		set("id", id);
		set("name", name);
	}

	public Double getCarbs(boolean training) {
    if(training) {
      return (get("cT") != null)? (Double)get("cT") : 0D;
    }
    else {
      return (get("cR") != null)? (Double)get("cR") : 0D;
    }
  }
	public Date getDateEnd() {
		if(get("d2") != null) {
			return get("d2");
    }
		else {
			return new Date();
    }
	}
	public Date getDateStart() {
		if(get("d1") != null) {
			return get("d1");
    }
		else {
			return new Date();
    }
	}
	public Double getEnergy(boolean training) {
    if(training) {
      return (get("eT") != null)? (Double)get("eT") : 0D;
    }
    else {
      return (get("eR") != null)? (Double)get("eR") : 0D;
    }
  }
	public Double getFet(boolean training) {
    if(training) {
      return (get("fT") != null)? (Double)get("fT") : 0D;
    }
    else {
      return (get("fR") != null)? (Double)get("fR") : 0D;
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
	public String getName() {
		if(get("n") != null) {
			return get("n");
    }
		else {
			return "";
    }
  }
	public Double getProtein(boolean training) {
    if(training) {
      return (get("pT") != null)? (Double)get("pT") : 0D;
    }
    else {
      return (get("pR") != null)? (Double)get("pR") : 0D;
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
	/**
	 * Only used for fetching guide value for single day
	 * @return
	 */
	public boolean hasTraining() {
		if(get("ht") != null) {
			return get("ht");
    }
		else {
			return false;
    }
  }
	public boolean isPercent() {
		if(get("ip") != null) {
			return get("ip");
    }
		else {
			return false;
    }
  }

	public void setCarb(boolean training, Double carb) {
		if(training) {
			set("cT", carb);
    }
		else {
			set("cR", carb);
    }
  }
	public void setDates(Date dateStart, Date dateEnd) {
		set("d1", dateStart);
		set("d2", dateEnd);
	}
	public void setEnergy(boolean training, Double energy) {
		if(training) {
			set("eT", energy);
    }
		else {
			set("eR", energy);
    }
  }
	public void setFet(boolean training, Double fet) {
		if(training) {
			set("fT", fet);
    }
		else {
			set("fR", fet);
    }
  }
	/**
	 * Only used for fetching guide value for single day
	 * @param hasTraining
	 */
	public void setHasTraining(boolean hasTraining) {
		set("ht", hasTraining);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setName(String name) {
		set("n", name);
	}
	public void setPercent(boolean isPercent) {
		set("ip", isPercent);
	}
	public void setProtein(boolean training, Double protein) {
		if(training) {
			set("pT", protein);
    }
		else {
			set("pR", protein);
    }
  }
	public void setUid(String uid) {
		set("uid", uid);
	}
}
