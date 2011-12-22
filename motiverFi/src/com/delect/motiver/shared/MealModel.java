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
  public int getCount() {
    if(get("cc") != null) {
      return get("cc");
    }
    else {
      return 0;
    }
  }
  public UserModel getUser() {
    return get("u");
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
  public void setCount(int cc) {
    set("cc", cc);
  }
  public void setUser(UserModel u) {
    set("u", u);
  }	
  
  @Override
  public String toString() {
    return "Meal: ['"+getName()+"', foods: "+getFoods().size()+"]";
  }
}
