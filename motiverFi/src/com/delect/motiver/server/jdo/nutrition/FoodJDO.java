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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.FoodInMeal;
import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.server.FoodInTime;
import com.delect.motiver.shared.FoodModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FoodJDO implements Serializable, Cloneable {
	
  /**
   * 
   */
  private static final long serialVersionUID = 2160638022282889720L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static FoodModel getClientModel(FoodJDO model) {
		if(model == null) {
			return null;
    }
		
		FoodModel modelClient = new FoodModel();
		modelClient.setId(model.getId().longValue());
		modelClient.setAmount(model.getAmount());
		modelClient.setUid(model.getUid());
		modelClient.setName(FoodName.getClientModel(model.getName()));
		
		return modelClient;
	}
  
	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static FoodJDO getServerModel(FoodModel model) {
		if(model == null) {
			return null;
    }

		FoodJDO modelServer = new FoodJDO();
		if(model.getName() != null) {
			modelServer.setNameId(model.getName().getId());
			modelServer.setName(FoodName.getServerModel(model.getName()));
    }
		else {
			modelServer.setNameId(0L);
    }
		if(model.getId() > 0) {
		  modelServer.setId(model.getId());
		}
		modelServer.setAmount(model.getAmount());
		
		return modelServer;
	}
  
	@Override
  public Object clone() throws CloneNotSupportedException {
    
    FoodJDO clone = new FoodJDO();
    clone.setAmount(getAmount());
    clone.setNameId(getNameId());
    
    return clone;
  }

  public static FoodInMealTime getFoodInMealTimeModel(FoodJDO model) {

    FoodInMealTime modelServer = new FoodInMealTime();
    modelServer.setId(model.getId());
    modelServer.setAmount(model.getAmount());
    modelServer.setNameId(model.getNameId());
    
    return modelServer;
  }

  public static FoodInMeal getFoodInMealModel(FoodJDO model) {

    FoodInMeal modelServer = new FoodInMeal();
    modelServer.setId(model.getId());
    modelServer.setAmount(model.getAmount());
    modelServer.setNameId(model.getNameId());
    
    return modelServer;
  }

  public static FoodInTime getFoodInTimeModel(FoodJDO model) {

    FoodInTime modelServer = new FoodInTime();
    modelServer.setId(model.getId());
    modelServer.setAmount(model.getAmount());
    modelServer.setNameId(model.getNameId());
    
    return modelServer;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof FoodJDO) {
      return ((FoodJDO)obj).getId() == getId();
    }
    else {
      return false;
    }
  }

  @Persistent
  private Long uid;
  
  @Persistent
  private String openId;

	@Persistent
	private Double amount;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
	private Key id = null;

//	@Persistent
//	private Meal meal;

	@Persistent
	private Long name = 0L;

//	@Persistent
//	private Time time;

	@NotPersistent
	private FoodName n;
	
	public FoodJDO() {
    
  }
	
	public Double getAmount() {
		if(amount != null) {
			return amount;
    }
		else {
			return 0D;
    }
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

	public Long getNameId() {
		if(name != null) {
			return name;
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

	public void setAmount(Double amount) {
		this.amount = amount;
  }

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(FoodJDO.class.getSimpleName(), id);
    }
		
		if(k != null) {
			this.id = k;
    }
		else {
			this.id = null;
    }
	}

	public void setNameId(Long name) {
		this.name = name;
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
  
  public void setName(FoodName n) {
    this.n = n;
  }

  public FoodName getName() {
    return n;
  }

  /**
   * Updates food from given model
   * @param model
   */
  public void update(FoodJDO model, boolean includeId) {
    if(includeId) {
      setId(model.getId());
    }
    setAmount(model.getAmount());
    setNameId(model.getNameId());
    setUid(model.getUid());
  }
  
  @Override
  public String toString() {
    return "Food: [id: "+getId()+", name: '"+((getName() != null)? getName().getName() : "")+"', "+getAmount()+"]";
  }
}
