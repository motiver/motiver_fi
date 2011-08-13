/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.TimeModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Time implements Comparable<Time> {
	
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static TimeModel getClientModel(Time model) {
		if(model == null) {
			return null;
    }
		
		TimeModel modelClient = new TimeModel(model.getDate(), (int) model.getTime());
		modelClient.setId(model.getId());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Time getServerModel(TimeModel model) {
		if(model == null) {
			return null;
    }
		
		Time modelServer = new Time(model.getDate(), (long) model.getTime());
		modelServer.setId(model.getId());
		
		return modelServer;
	}
	
	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@Persistent
  private Date date;

	@Persistent(mappedBy = "parent")
  private List<FoodInTime> foods = new ArrayList<FoodInTime>();

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent(mappedBy = "parentTime")
	private List<MealInTime> meals = new ArrayList<MealInTime>();

	@Persistent
	private Long time = 0L;	//in seconds from midnight

	public Time() {
		
	}

	public Time(Date date, Long time) {
		this.setDate(date);
		this.setTime(time);
	}

	@Override
	public int compareTo(Time compare) {
		return (time.longValue() > compare.time.longValue())? 1: 0;
	}

	public Date getDate() {
		return date;
  }

	public List<FoodInTime> getFoods() {
		return foods;
	}

	public Long getId() {
		if(id != null) {
			return id.getId();
    }
		else {
			return 0L;
    }
  }

	public Key getKey() {
		return id;
	}

	public List<MealInTime> getMeals() {
		if(meals != null) {
			return meals;
    }
		else {
			return new ArrayList<MealInTime>();
    }
	}

	public long getTime() {
		if(time != null) {
			return time;
    }
		else {
			return 0L;
    }
  }

	public String getUid() {
		if(openId != null) {
			return openId;
    }
		else {
			return "";
    }
  }

	public void setDate(Date date) {
    this.date = date;
  }

	public void setFoods(List<FoodInTime> foods) {
		this.foods = foods;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Time.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setMeals(List<MealInTime> meals) {
		this.meals = meals;
	}
	
	public void setTime(Long time) {
    this.time = time;
  }

	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
