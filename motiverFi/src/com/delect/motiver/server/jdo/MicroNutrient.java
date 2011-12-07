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
package com.delect.motiver.server.jdo;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.shared.MicroNutrientModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MicroNutrient implements Serializable, Cloneable {
	
	/**
   * 
   */
  private static final long serialVersionUID = 1L;

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
    modelServer.setUid(model.getUid());
		
		return modelServer;
	}
  
  @Override
  public Object clone() throws CloneNotSupportedException {
    
    MicroNutrient clone = new MicroNutrient(getNameId());
    clone.setUid(getUid());
    clone.setValue(getValue());
    
    return clone;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof MicroNutrient) {
      return ((MicroNutrient)obj).getId() == getId();
    }
    else {
      return false;
    }
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

  /**
   * Updates micronutrient from given model
   * @param model
   */
  public void update(MicroNutrient model, boolean includeId) {
    if(includeId) {
      setId(model.getId());
    }
    //update name
    setName(model.getNameId());
    setUid(model.getUid());
    setValue(model.getValue());    
  }
  
  @Override
  public String toString() {
    return "MicroNutrient: [id: "+getId()+", '"+getNameId()+"', value: '"+getValue()+"']";
  }
}
