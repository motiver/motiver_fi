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
package com.delect.motiver.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.MealInTime;
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
		modelClient.setUser(UserOpenid.getClientModel(model.getUser()));
		
		//meals
		if(model.getMealsNew() != null) {
		  List<MealModel> meals = new ArrayList<MealModel>();
		  for(Meal m : model.getMealsNew()) {
		    meals.add(Meal.getClientModel(m));
		  }
		  modelClient.setMeals(meals);
		}
		
    //foods
    if(model.getFoods() != null) {
      List<FoodModel> foods = new ArrayList<FoodModel>();
      for(FoodJDO m : model.getFoods()) {
        foods.add(FoodJDO.getClientModel(m));
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
    if(model.getUser() != null)
      modelServer.setUid(model.getUser().getUid());
		
		return modelServer;
	}
	
	@Persistent
	private Long uid;
  
  @Persistent
  private String openId;

	@Persistent
  private Date date;

	@NotPersistent
  private List<FoodJDO> foods = new ArrayList<FoodJDO>();

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

  @Persistent
  private List<Key> mealsKeys = new ArrayList<Key>();

  @Persistent
  private List<Key> foodsKeys = new ArrayList<Key>();
  
	@Persistent
	private Long time = 0L;	//in seconds from midnight

  @NotPersistent
  private List<Meal> meals = new ArrayList<Meal>();

  @NotPersistent
  private UserOpenid user;

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

	public List<FoodJDO> getFoods() {
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
    return null;
  }

	public List<Meal> getMealsNew() {
	  return meals;
	}
  
  public List<Key> getFoodsKeys() {
    return foodsKeys;
  }
	
  public List<Key> getMealsKeys() {
    return mealsKeys;
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

	public void setFoods(List<FoodJDO> foods) {
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
	}

  public void setMealsNew(List<Meal> meals) {
    this.meals = meals;
  }

  public void setFoodsKeys(List<Key> foodsKeys) {
    this.foodsKeys = foodsKeys;
  }

  public void setMealsKeys(List<Key> mealsKeys) {
    this.mealsKeys = mealsKeys;
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

  public UserOpenid getUser() {
    return user;
  }

  public void setUser(UserOpenid user) {
    this.user = user;
  }

  /**
   * Updates time from given model
   * @param model
   */
  public void update(Time model, boolean includeId) {
    if(includeId) {
      setId(model.getId());
    }
    setDate(model.getDate());
    setMealsNew(model.getMealsNew());
    setMealsKeys(model.getMealsKeys());
    setTime(model.getTime());
    setUid(model.getUid());

    //if foods removed -> check which was removed
    if(getFoodsKeys().size() > model.getFoodsKeys().size()) {
      for(Key f : getFoodsKeys()) {
        if(!model.getFoodsKeys().contains(f)) {
          getFoodsKeys().remove(f);
        }
      }
    }
    //new food added
    else {
      for(Key f : model.getFoodsKeys()) {
          int i = getFoodsKeys().indexOf(f);
          if(i == -1) {
            getFoodsKeys().add(f);
          }
        }
    }
  }
  
  @Override
  public String toString() {
    return "Time: [id: "+getId()+", '"+getTime()+"', meals: "+getMealsNew().size()+", meals (keys): "+getMealsKeys().size()+", foods: "+getFoods().size()+"" +
        ", '"+getUid()+"']";
  }
  
}
