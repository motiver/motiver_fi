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

public class MealModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 85325225753L;
	
	public MealModel() {
		
	}
	public MealModel(String name) {
		this.setName(name);
	}

	public double getCarb() {
		if(get("c") != null) {
			return get("c");
    }
		else {
			return 0D;
    }
  }
	public Date getDate() {
		return get("d");
	}
	public double getEnergy() {
		if(get("e") != null) {
			return get("e");
    }
		else {
			return 0D;
    }
  }
	public double getFet() {
		if(get("f") != null) {
			return get("f");
    }
		else {
			return 0D;
    }
  }
	public List<FoodModel> getFoods() {
		return get("fo");
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
    return get("n");
  }
	public double getProtein() {
		if(get("p") != null) {
			return get("p");
    }
		else {
			return 0D;
    }
  }
	public long getTimeId() {
		if(get("tid") != null) {
			return get("tid");
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
	

	public void setCarb(double carb) {
    set("c", carb);
	}
	/**
	 * Used only for "delivering" time's date
	 * @param date
	 */
	public void setDate(Date date) {
		set("d", date);
	}
	public void setEnergy(double energy) {
    set("e", energy);
	}
	public void setFet(double fet) {
    set("f", fet);
	}
	public void setFoods(List<FoodModel> foods) {
		set("fo", foods);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setName(String name) {
		set("n", name);
	}
	public void setProtein(double protein) {
    set("p", protein);
	}
	public void setTimeId(long id) {
		set("tid", id);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}	
}
