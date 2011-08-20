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

import com.delect.motiver.client.AppController;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MeasurementModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -265786912748L;
	
	public MeasurementModel()  {
		
	}
	public MeasurementModel(String name, String unit) {
		setName(name);
		setUnit(unit);
	}

	public Date getDate() {
		return get("d");
  }
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	/**
	 * Return name for client side
	 * @return
	 */
	public String getNameClient() {
		String str = "-" + AppController.Lang.NoName() + "-";
		if(get("n") != null && get("n") instanceof String && ((String)get("n")).length() > 0) {
      str = get("n");
    }
		return str;
	}
	/**
	 * Return name for server side
	 * @return
	 */
	public String getNameServer() {
		if(get("n") != null) {
			return get("n");
    }
		else {
			return "";
    }
  }
	public double getTarget() {
		if(get("t") != null) {
			return get("t");
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
	public String getUnit() {
		if(get("u") != null) {
			return get("u");
    }
		else {
			return "";
    }
  }
	public List<MeasurementValueModel> getValues() {
		return get("v");
	}
	

	public void setDate(Date date) {
		set("d", date);
	}
	/**
	 * For saving highest value from server
	 * @param measurementHighestDate
	 * @param measurementHighestValue
	 */
	public void setHighestValue(Date measurementHighestDate, double measurementHighestValue) {
		set("hd", measurementHighestDate);
		set("hv", measurementHighestValue);
	}
	public void setId(long id) {
		set("id", id);
	}
	/**
	 * For saving lowest value from server
	 * @param measurementHighestDate
	 * @param measurementHighestValue
	 */
	public void setLowestValue(Date measurementLowestDate, double measurementLowestValue) {
		set("ld", measurementLowestDate);
		set("lv", measurementLowestValue);
	}
	public void setName(String name) {
		set("n", name);
	}
	public void setTarget(double target) {
		set("t", target);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}

	public void setUnit(String unit) {
		set("u", unit);
	}
	public void setValues(List<MeasurementValueModel> values) {
		set("v", values);
	}
}
