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
package com.delect.motiver.server.jdo.nutrition;

import java.io.Serializable;
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

import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.TimeModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Time implements Serializable, Comparable<Time> {
	
	/**
   * 
   */
  private static final long serialVersionUID = 938076651175865622L;

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
		
		//meals
		if(model.getMeals() != null) {
		  List<MealModel> meals = new ArrayList<MealModel>();
		  for(MealInTime m : model.getMeals()) {
		    meals.add(MealInTime.getClientModel(m));
		  }
		  modelClient.setMeals(meals);
		}
		
    //foods
    if(model.getFoods() != null) {
      List<FoodModel> foods = new ArrayList<FoodModel>();
      for(Food m : model.getFoods()) {
        foods.add(Food.getClientModel(m));
      }
      modelClient.setFoods(foods);
    }
		
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

	@Persistent
  private List<Food> foods = new ArrayList<Food>();

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

	public List<Food> getFoods() {
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
	  return meals;
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

	public void setFoods(List<Food> foods) {
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
