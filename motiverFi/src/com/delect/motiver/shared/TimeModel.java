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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class TimeModel extends BaseModelData implements IsSerializable, Comparable<TimeModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8353255753L;
	
	public TimeModel() {
		
	}
	public TimeModel(Date date, int time) {
		this.setDate(date);
		this.setTime(time);
	}

	@Override
	public int compareTo(TimeModel compare) {
		return (getTime() > compare.getTime())? 1: 0;
	}
	public Double getCarb() {
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
	public Double getEnergy() {
		if(get("e") != null) {
			return get("e");
    }
		else {
			return 0D;
    }
  }
	public Double getFet() {
		if(get("f") != null) {
			return get("f");
    }
		else {
			return 0D;
    }
  }
	public List<FoodModel> getFoods() {
		if(get("fo") != null) {
			return get("fo");
    }
		else {
			return new ArrayList<FoodModel>();
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
	public List<MealModel> getMeals() {
		if(get("m") != null) {
			return get("m");
    }
		else {
			return new ArrayList<MealModel>();
    }
	}
	public Double getProtein() {
		if(get("p") != null) {
			return get("p");
    }
		else {
			return 0D;
    }
  }
	public int getTime() {
    return get("t");
  }

	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
    }
  }
	public void setCarb(Double carb) {
    set("c", carb);
	}
	public void setDate(Date date) {
    set("d", date);
  }
	public void setEnergy(Double energy) {
    set("e", energy);
	}
	public void setFet(Double fet) {
    set("f", fet);
	}
	public void setFoods(List<FoodModel> foods) {
		set("fo", foods);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setMeals(List<MealModel> meals) {
		set("m", meals);
	}
	public void setProtein(Double protein) {
    set("p", protein);
	}
	public void setTime(int time) {
    set("t", time);
  }
	public void setUid(String uid) {
		set("uid", uid);
	}
  
  @Override
  public String toString() {
    return "Time: ['"+getTime()+"', meals: "+getMeals().size()+", foods: "+getFoods().size()+"]";
  }
}
