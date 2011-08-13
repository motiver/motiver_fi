/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.CardioModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Cardio {
	
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static CardioModel getClientModel(Cardio model) {
		if(model == null) {
			return null;
    }

		CardioModel modelClient = new CardioModel(model.getId(), model.getName());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Cardio getServerModel(CardioModel model) {
		if(model == null) {
			return null;
    }
		
		Cardio modelServer = new Cardio(model.getNameServer());
		modelServer.setId(model.getId());
		
		return modelServer;
	}

	@Persistent
	public Long uid;
	
	@Persistent
	public String openId;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private String name;

	@Persistent(mappedBy = "cardio")
  private List<CardioValue> values = new ArrayList<CardioValue>();

	public Cardio(String name) {
		setName(name);
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

	public String getUid() {
		if(openId != null) {
			return openId;
    }
		else {
			return "";
    }
  }

	public List<CardioValue> getValues() {
		return values;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Cardio.class.getSimpleName(), id);
		}
		this.id = k;
	}

	public void setName(String name) {
		this.name = name;
  }

	public void setUid(String uid) {
		this.openId = uid;
	}
	
	public void setValues(List<CardioValue> values) {
		this.values = values;
	}

  public Long getUidOld() {
    return uid;
  }
}
