/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class FoodModel extends BaseModel implements IsSerializable {

	private static final long serialVersionUID = -2028656762L;
	
	public FoodModel() {
		
	}

	/**
	 * Instantiates a new food model.
	 *
	 * @param name the name
	 */
	public FoodModel(FoodNameModel name) {
		setName(name);
	}
	
	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public double getAmount() {
		if(get("a") != null) {
			return get("a");
    }
		else {
			return 0D;
    }
  }
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	
	/**
	 * Gets the meal id.
	 *
	 * @return the meal id
	 */
	public long getMealId() {
		if(get("mid") != null) {
			return get("mid");
    }
		else {
			return 0L;
    }
  }
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public FoodNameModel getName() {
		return get("m");
	}
	
	/**
	 * Gets the time id.
	 *
	 * @return the time id
	 */
	public long getTimeId() {
		if(get("tid") != null) {
			return get("tid");
    }
		else {
			return 0L;
    }
  }
	
	/**
	 * Gets the uid.
	 *
	 * @return the uid
	 */
	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
    }
  }

	/**
	 * Sets the amount.
	 *
	 * @param amount the new amount
	 */
	public void setAmount(double amount) {
    set("a", amount);
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		set("id", id);
	}
	
	/**
	 * Sets the meal id.
	 *
	 * @param id the new meal id
	 */
	public void setMealId(long id) {
		set("mid", id);
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(FoodNameModel name) {
		set("m", name);
		if(name != null) {
			set("n", name.getName());	//set so grid shows name
    }
	}
	
	/**
	 * Sets the time id.
	 *
	 * @param id the new time id
	 */
	public void setTimeId(long id) {
		set("tid", id);
	}
	
	/**
	 * Sets the uid.
	 *
	 * @param uid the new uid
	 */
	public void setUid(String uid) {
		set("uid", uid);
	}	
}
