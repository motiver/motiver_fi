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

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MicroNutrientModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -230029L;
	public MicroNutrientModel()  {
	}
	public MicroNutrientModel(int nameId) {
		set("n", nameId);
	}

	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public int getNameId() {
		if(get("n") != null) {
			return get("n");
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
	public double getValue() {
		if(get("v") != null) {
			return get("v");
    }
		else {
			return 0D;
    }
  }
	

	public void setId(long id) {
		set("id", id);
	}
	public void setNameId(int n) {
		set("n", n);
	}
	public void setUid(String uid) {
    set("uid", uid);
  }
	public void setValue(double value) {
    set("v", value);
	}
}
