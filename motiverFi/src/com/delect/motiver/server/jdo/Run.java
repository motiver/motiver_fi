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

import com.delect.motiver.shared.RunModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Run {
	
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static RunModel getClientModel(Run model) {
		if(model == null) {
			return null;
    }

		RunModel modelClient = new RunModel(model.getId(), model.getName());
		modelClient.setId(model.getId());
		modelClient.setDistance(model.getDistance());
		modelClient.setTargetTime(model.getTargetTime());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Run getServerModel(RunModel model) {
		if(model == null) {
			return null;
    }
		
		Run modelServer = new Run(model.getNameServer());
		modelServer.setId(model.getId());
		modelServer.setDistance(model.getDistance());
		modelServer.setTargetTime(model.getTargetTime());
		
		return modelServer;
	}
	
	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private Double distance;		//in kilometers

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String name;

	@Persistent
	private Long targetTime;	//in seconds

	@Persistent(mappedBy = "run")
  private List<RunValue> values = new ArrayList<RunValue>();

	public Run(String name) {
		setName(name);
	}

	public Double getDistance() {
		return distance;
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

	public Long getTargetTime() {
		return targetTime;
	}

	public String getUid() {
		if(openId != null) {
			return openId;
    }
		else {
			return "";
    }
  }

	public List<RunValue> getValues() {
		return values;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Run.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setName(String name) {
		this.name = name;
  }

	public void setTargetTime(Long targetTime) {
		this.targetTime = targetTime;
	}

	public void setUid(String openId) {
		this.openId = openId;
	}
	
	public void setValues(List<RunValue> values) {
		this.values = values;
	} 

  public Long getUidOld() {
    return uid;
  }

  @SuppressWarnings("unchecked")
  public JSONObject getJson() {
    JSONObject obj = new JSONObject();
    obj.put("distance", getDistance());
    obj.put("id", getId());
    obj.put("name", getName());
    obj.put("openId", getUid());
    obj.put("targetTime", getTargetTime());
    JSONArray list = new JSONArray();
    for(RunValue value : getValues()) {
      list.add(value.getJson());
    }
    obj.put("RunValue", list);

    return obj;
  } 
}
