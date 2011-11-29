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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.MealModel;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class MealInTime implements Serializable, Comparable<MealInTime> {
		
  /**
   * 
   */
  private static final long serialVersionUID = 6226963324096990747L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static MealModel getClientModel(MealInTime model) {
		if(model == null) {
			return null;
    }
		
		MealModel modelClient = new MealModel(model.getName());
		modelClient.setId(model.getId());

    //foods
    if(model.getFoods() != null) {
      List<FoodModel> foods = new ArrayList<FoodModel>();
      for(FoodInMealTime m : model.getFoods()) {
        foods.add(FoodInMealTime.getClientModel(m));
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
	public static MealInTime getServerModel(MealModel model) {
		if(model == null) {
			return null;
    }
		
		MealInTime modelServer = new MealInTime(model.getName());
		modelServer.setId(model.getId());
		
		return modelServer;
	}
	
	/**
	 * How many times this have been copied
	 */
  @Persistent
	private Integer copyCount = 0;

	@Persistent(mappedBy = "parent")
  private List<FoodInMealTime> foods = new ArrayList<FoodInMealTime>();

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id = null;

	@Persistent
	private String name = "";

//	@Persistent
//	private Time parentTime;

	public MealInTime() {
		
	}

	public MealInTime(String name) {
		this.setName(name);
	}

	@Override
	public int compareTo(MealInTime compare) {
		return getName().toLowerCase().compareTo(compare.getName().toLowerCase());
	}

	public List<FoodInMealTime> getFoods() {
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

	/**
	 * Adds one to copy count
	 */
	public void incrementCopyCount() {
		copyCount++;
	}
	
	public void setFoods(List<FoodInMealTime> foods) {
		this.foods = foods;
	}

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(MealInTime.class.getSimpleName(), id);
    }
		
		this.id = k;
	}
	
	public void setName(String name) {
    this.name = name;
  }
}
