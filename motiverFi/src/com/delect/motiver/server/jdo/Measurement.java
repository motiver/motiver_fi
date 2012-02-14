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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

  @SuppressWarnings("unchecked")
  public JSONObject getJson() {
    JSONObject obj=new JSONObject();
    obj.put("date",(getDate() != null)? getDate().toString() : null);
    obj.put("id",getId());
    obj.put("name",getName());
    obj.put("target",getTarget());
    obj.put("openId",getUid());
    obj.put("uid",getUidOld());
    JSONArray list = new JSONArray();
    for(MeasurementValue value : getValues()) {
      list.add(value.getJson());
    }
    obj.put("values", list);

    return obj;
  }
}
