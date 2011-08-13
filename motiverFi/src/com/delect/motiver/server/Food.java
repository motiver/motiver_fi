/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.FoodModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Food {
	
  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static FoodModel getClientModel(Food model) {
		if(model == null) {
			return null;
    }
		
		FoodModel modelClient = new FoodModel();
		modelClient.setId(model.getId().longValue());
		modelClient.setAmount(model.getAmount());
		modelClient.setUid(model.getUid());
		return modelClient;
	}
  
	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Food getServerModel(FoodModel model) {
		if(model == null) {
			return null;
    }

		Food modelServer = new Food();
		if(model.getName() != null) {
			modelServer.setNameId(model.getName().getId());
    }
		else {
			modelServer.setNameId(0L);
    }
		modelServer.setId(model.getId());
		modelServer.setAmount(model.getAmount());
		
		return modelServer;
	}
	
	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private Double amount;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
	private Key id = null;

	@Persistent
	private Key meal;

	@Persistent
	private Long name = 0L;

	@Persistent
	private Key time;

	public Food() {
    
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

	public Key getMeal() {
		return meal;
	}

	public Long getNameId() {
		if(name != null) {
			return name;
    }
		else {
			return 0L;
    }
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

	public void setAmount(Double amount) {
		this.amount = amount;
  }

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Food.class.getSimpleName(), id);
    }
		
		if(k != null) {
			this.id = k;
    }
		else {
			this.id = null;
    }
	}

	public void setMeal(Key meal) {
    this.meal = meal;
  }

	public void setNameId(Long name) {
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
