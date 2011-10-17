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
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.MealModel;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Meal implements Serializable, Comparable<Meal>, Cloneable {
		
  /**
   * 
   */
  private static final long serialVersionUID = -1889245717390374201L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static MealModel getClientModel(Meal model) {
		if(model == null) {
			return null;
    }
		
		MealModel modelClient = new MealModel(model.getName());
		modelClient.setId(model.getId());
//		modelClient.setUid(model.getUid());

    //foods
    List<FoodModel> foods = new ArrayList<FoodModel>();
    if(model.getFoods() != null) {
      for(Food m : model.getFoods()) {
        foods.add(Food.getClientModel(m));
      }
    }
    modelClient.setFoods(foods);
    
    //user
    modelClient.setUser(UserOpenid.getClientModel(model.getUser()));
    
		return modelClient;
	}

  /**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Meal getServerModel(MealModel model) {
		if(model == null) {
			return null;
    }
		
		Meal modelServer = new Meal(model.getName());
		modelServer.setId(model.getId());
		
		return modelServer;
	}
	
	public Object clone() throws CloneNotSupportedException {
	  
	  Meal clone = new Meal();
	  clone.setName(getName());
	  clone.setTime(getTime());
	  clone.setUid(getUid());
	  clone.setUser(getUser());
	  
	  List<Food> foods = new ArrayList<Food>();
	  for(Food f : getFoods()) {
	    foods.add((Food) f.clone());
	  }
	  clone.setFoods(foods);
	  
	  return clone;
	}
	
	/**
	 * How many times this have been copied
	 */
  @Persistent
	private Integer copyCount = 0;

	@Persistent
  @Element(dependent = "true")
  private List<Food> foods = new ArrayList<Food>();

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id = null;

	@Persistent
	private String name = "";

	@Persistent
	private Long timeId;

	@Persistent
	private Long uid;
  
  @Persistent
  private String openId;

  @NotPersistent
  private UserOpenid user;

	public Meal() {
		
	}

	public Meal(String name) {
		this.setName(name);
	}

	@Override
	public int compareTo(Meal compare) {
		return getName().toLowerCase().compareTo(compare.getName().toLowerCase());
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

	public String getName() {
    return name;
  }

	public Long getTime() {
		return timeId;
	}

	public String getUid() {
		if(openId != null) {
			return openId;
    }
		else {
			return "";
    }
  }

	/**
	 * Adds one to copy count
	 */
	public void incrementCopyCount() {
		copyCount++;
	}

	public void setFoods(List<Food> foods) {
		this.foods = foods;
	}

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Meal.class.getSimpleName(), id);
    }
		
		this.id = k;
	}
	
	public void setName(String name) {
    this.name = name;
  }

	public void setTime(Long timeId) {
    this.timeId = timeId;
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return 0L;
  } 

  /**
   * Updates time from given model
   * @param model
   */
  public void update(Meal model, boolean includeId) {
    if(includeId) {
      setId(model.getId());
    }
    setName(model.getName());
    setTime(model.getTime());
    setUid(model.getUid());
    
    for(Food f : model.getFoods()) {
      int i = getFoods().indexOf(f);
      if(i != -1) {
        Food fOld = getFoods().get(i);
        fOld.update(f, includeId);
      }
      else {
        getFoods().add(f);
      }
    }
  }

  public UserOpenid getUser() {
    return user;
  }

  public void setUser(UserOpenid user) {
    this.user = user;
  }

  public Integer getCount() {
    return copyCount;
  }

  public void setCount(Integer copyCount) {
    this.copyCount = copyCount;
  }
  
  @Override
  public String toString() {
    return "Meal: ['"+getName()+"', foods: "+getFoods().size()+"]";
  }
}
