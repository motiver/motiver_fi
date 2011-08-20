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
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class FoodNameModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -23221797L;
	public FoodNameModel() {
		setTrusted(0);
	}
	public FoodNameModel(Long id, String name) {
		setTrusted(0);
		set("id", id);
		set("n", name);
	}

	public double getCarb() {
		if(get("c") != null) {
			return get("c");
    }
		else {
			return 0D;
    }
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
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public String getLocale() {
		if(get("l") != null) {
			return get("l");
    }
		else {
			return "";
    }
  }
	public List<MicroNutrientModel> getMicroNutrients() {
		if(get("mn") != null) {
			return get("mn");
    }
		else {
			return new ArrayList<MicroNutrientModel>();
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
  public double getPortion() {
		if(get("po") != null) {
			return get("po");
    }
		else {
			return 0D;
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
	/**
	 * If food name is trusted
	 * @return 0=not trusted, 1=verified, 100=motiver
	 */
	public int getTrusted() {
		//motiver
		if(getUid().equals("224787470868700")) {
			return 100;
    }
		//valio
		else if(getUid().equals("493464655570")) {
			return 100;
    }
		//return variable
		else {
			if(get("tr") != null) {
				return get("tr");
      }
			else {
				return 0;
      }
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
	public void setEnergy(double energy) {
    set("e", energy);
	}
	public void setFet(double fet) {
    set("f", fet);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setLocale(String locale) {
    set("l", locale);
  }
	public void setMicronutrients(List<MicroNutrientModel> micronutrients) {
		set("mn", micronutrients);
	}
	public void setName(String n) {
		set("n", n);
	}
  public void setPortion(double portion) {
    set("po", portion);
	}
  public void setProtein(double protein) {
    set("p", protein);
	}
	/**
	 * If food name is trusted
	 * @param trusted : 0=not trusted, 1=verified, 100=motiver
	 */
	public void setTrusted(int trusted) {
		set("tr", trusted);
	}
	public void setUid(String uid) {
    set("uid", uid);
  }
}
