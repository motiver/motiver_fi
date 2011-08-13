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

import com.delect.motiver.shared.MicroNutrientModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MicroNutrient {
	
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static MicroNutrientModel getClientModel(MicroNutrient model) {
		if(model == null) {
			return null;
    }

		MicroNutrientModel modelClient = new MicroNutrientModel(model.getNameId());
		modelClient.setId(model.getId());
		modelClient.setValue(model.getValue());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static MicroNutrient getServerModel(MicroNutrientModel model) {
		if(model == null) {
			return null;
    }
		
		MicroNutrient modelServer = new MicroNutrient(model.getNameId());
		modelServer.setId(model.getId());
		modelServer.setValue(model.getValue());
		
		return modelServer;
	}
	
	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private FoodName foodname;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private Integer nameId = 0;

	@Persistent
	private Double value = 0D;
	
	public MicroNutrient(Integer nameId) {
		this.nameId = nameId;
	}

	public FoodName getFood() {
		return foodname;
	}

	public Long getId() {
		if(id != null) {
			return id.getId();
    }
		else {
			return 0L;
    }
  }

	public Integer getNameId() {
		if(nameId != null) {
			return nameId;
    }
		else {
			return 0;
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

	public Double getValue() {
		if(value != null) {
			return value;
    }
		else {
			return 0D;
    }
  }

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(MicroNutrient.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setName(Integer nameId) {
		this.nameId = nameId;
  }

	public void setUid(String openId) {
		this.openId = openId;
	}
	
	public void setValue(Double value) {
		this.value = value;
  } 

  public Long getUidOld() {
    return uid;
  } 
}
