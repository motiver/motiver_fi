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

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.MeasurementValueModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class MeasurementValue {
	
  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static MeasurementValueModel getClientModel(MeasurementValue model) {
		if(model == null) {
			return null;
    }
		
		MeasurementValueModel modelClient = new MeasurementValueModel();
		modelClient.setId(model.getId().longValue());
		modelClient.setValue(model.getValue());
		modelClient.setDate(model.getDate());
		modelClient.setUid(model.getUid());
		return modelClient;
	}
  
	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static MeasurementValue getServerModel(MeasurementValueModel model) {
		if(model == null) {
			return null;
    }

		MeasurementValue modelServer = new MeasurementValue();
		modelServer.setId(model.getId());
		modelServer.setValue(model.getValue());
		modelServer.setDate(model.getDate());
		
		return modelServer;
	}
	
	@Persistent
	private Date date;

	@Persistent
	private Measurement measurement;

	@Persistent
	private Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private Double value;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
	protected Key id;

	public MeasurementValue() {
    
  }
	
	public Date getDate() {
    return date;
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

	public Measurement getMeasurement() {
		return measurement;
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
    return value;
  }

	public void setDate(Date date) {
		this.date = date;
  }

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(MeasurementValue.class.getSimpleName(), id);
    }
		
		if(k != null) {
			this.id = k;
    }
		else {
			this.id = null;
    }
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
