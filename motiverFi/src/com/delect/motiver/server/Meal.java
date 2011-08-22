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
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.MealModel;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class Meal implements Serializable, Comparable<Meal> {
		
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
		modelClient.setUid(model.getUid());
		
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
	
	/**
	 * How many times this have been copied
	 */
  @Persistent
	private Integer copyCount = 0;

	@Persistent(mappedBy = "parent")
  private List<FoodInMeal> foods = new ArrayList<FoodInMeal>();

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id = null;

	@Persistent
	private String name = "";

	@Persistent
	private Key time;

	@Persistent
	private Long uid;
  
  @Persistent
  public String openId;

	public Meal() {
		
	}

	public Meal(String name) {
		this.setName(name);
	}

	@Override
	public int compareTo(Meal compare) {
		return getName().toLowerCase().compareTo(compare.getName().toLowerCase());
	}

	public List<FoodInMeal> getFoods() {
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

	public Key getTime() {
		return time;
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

	public void setFoods(List<FoodInMeal> foods) {
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

	public void setTime(Key time) {
    this.time = time;
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
