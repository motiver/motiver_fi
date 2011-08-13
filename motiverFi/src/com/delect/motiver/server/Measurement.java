/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.MeasurementModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Measurement {
	
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static MeasurementModel getClientModel(Measurement model) {
		if(model == null) {
			return null;
    }

		MeasurementModel modelClient = new MeasurementModel(model.getName(), model.getUnit());
		modelClient.setId(model.getId());
		modelClient.setTarget(model.getTarget());
		modelClient.setDate(model.getDate());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Measurement getServerModel(MeasurementModel model) {
		if(model == null) {
			return null;
    }
		
		Measurement modelServer = new Measurement(model.getNameServer(), model.getUnit());
		modelServer.setId(model.getId());
		modelServer.setTarget(model.getTarget());
		modelServer.setDate(model.getDate());
		
		return modelServer;
	}
	
	@Persistent
	private Date date;

	@Persistent
	private String name;

	@Persistent
	private Double target;

	@Persistent
	private Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private String unit;

	@Persistent(mappedBy = "measurement")
  private List<MeasurementValue> values = new ArrayList<MeasurementValue>();

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key id;
	
	public Measurement(String name, String unit) {
		setName(name);
		setUnit(unit);
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

	public String getName() {
    return name;
  }

	public Double getTarget() {
		if(target != null) {
			return target;
    }
		else {
			return 0D;
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

	public String getUnit() {
    return unit;
  }

	public List<MeasurementValue> getValues() {
		return values;
	}

	public void setDate(Date date) {
		this.date = date;
  }

	public void setId(Long id) {
		
		Key k = null;
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Measurement.class.getSimpleName(), id);
    }
		
		this.id = k;
	}

	public void setName(String name) {
		this.name = name;
  }

	public void setTarget(Double target) {
		this.target = target;
  }

	public void setUid(String openId) {
		this.openId = openId;
	}

	public void setUnit(String unit) {
		this.unit = unit;
  }
	
	public void setValues(List<MeasurementValue> values) {
		this.values = values;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
