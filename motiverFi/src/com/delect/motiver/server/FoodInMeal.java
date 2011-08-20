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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.FoodModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FoodInMeal {

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static FoodModel getClientModel(FoodInMeal model) {
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
	public static FoodInMeal getServerModel(FoodModel model) {
		if(model == null) {
			return null;
    }

		FoodInMeal modelServer = new FoodInMeal();
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
	@Persistent
	private Meal parent;

	public FoodInMeal() {
    
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
      k = KeyFactory.createKey(FoodInMeal.class.getSimpleName(), id);
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
