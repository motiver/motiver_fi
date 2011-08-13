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
public class FoodInTime {
	
  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static FoodModel getClientModel(FoodInTime model) {
		if(model == null) {
			return null;
    }
		
		FoodModel modelClient = new FoodModel();
		modelClient.setId(model.getId().longValue());
		modelClient.setAmount(model.getAmount());
		return modelClient;
	}
  
	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static FoodInTime getServerModel(FoodModel model) {
		if(model == null) {
			return null;
    }

		FoodInTime modelServer = new FoodInTime();
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
	private Double amount;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
	private Key id = null;

	@Persistent
	private Long name = 0L;

	@SuppressWarnings("unused")
	@Persistent(defaultFetchGroup="false")
	private Time parent;

	public FoodInTime() {
    
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

	public void setAmount(Double amount) {
		this.amount = amount;
  }

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(FoodInTime.class.getSimpleName(), id);
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
}
