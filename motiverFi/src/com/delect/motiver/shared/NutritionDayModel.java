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

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class NutritionDayModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4324782725855148417L;
	public NutritionDayModel()  {
	}
	public NutritionDayModel(double e, double p, double c, double f) {
		set("e", e);
		set("p", p);
		set("c", c);
		set("f", f);
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
	public boolean getFoodsPermission() {
		if(get("pf") != null) {
			return get("pf");
    }
		else {
			return false;	
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
	public double getProtein() {
		if(get("p") != null) {
			return get("p");
    }
		else {
			return 0D;
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
	public void setDate(Date d) {
		set("d", d);
	}
	public void setEnergy(double energy) {
    set("e", energy);
	}
	public void setFet(double fet) {
    set("f", fet);
	}
	public void setFoodsPermission(boolean permissionNutritionFoods) {
		set("pf", permissionNutritionFoods);
	}
	public void setProtein(double protein) {
    set("p", protein);
	}
	public void setUid(String uid) {
    set("uid", uid);
	}
}
