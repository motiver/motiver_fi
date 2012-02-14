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

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.CardioValueModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CardioValue {
	
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static CardioValueModel getClientModel(CardioValue model) {
		if(model == null) {
			return null;
    }
		
		CardioValueModel modelClient = new CardioValueModel( );
		modelClient.setId(model.getId());
		modelClient.setDate(model.getDate());
		modelClient.setDuration(model.getDuration());
		modelClient.setPulse(model.getPulse());
    modelClient.setPulseMax(model.getPulseMax());
		modelClient.setCalories(model.getCalories());
		modelClient.setInfo(model.getInfo());
		modelClient.setUid(model.getUid());
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static CardioValue getServerModel(CardioValueModel model) {
		if(model == null) {
			return null;
    }

		CardioValue modelServer = new CardioValue( );
		modelServer.setId(model.getId());
		modelServer.setDate(model.getDate());
		modelServer.setDuration(model.getDuration());
		modelServer.setPulse(model.getPulse());
    modelServer.setPulseMax(model.getPulseMax());
		modelServer.setCalories(model.getCalories());
		modelServer.setInfo(model.getInfo());
		
		return modelServer;
	}

	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private Integer calories;

	@Persistent
	private Cardio cardio;

	@Persistent
	private Date date;

	@Persistent
	private Long duration;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String info;

	@Persistent
	private Integer pulse;

	@Persistent
	private Integer pulseMax;
	
	public CardioValue() {
	  
	}
	
	public Integer getCalories() {
    return calories;
  }

	public Cardio getCardio() {
		return cardio;
	}

	public Date getDate() {
		return date;
	}

	public Long getDuration() {
		if(duration != null) {
			return duration;
    }
		else {
			return 0L;
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

	public String getInfo() {
    return info;
  }
  
	public Integer getPulse() {
    if(pulse != null) {
      return pulse;
    }
    else {
      return 0;
    }
  }
  
  public Integer getPulseMax() {
    if(pulseMax != null) {
      return pulseMax;
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

	public void setCalories(Integer calories) {
		this.calories = calories;
  }

	public void setDate(Date date) {
		this.date = date;
  }

	public void setDuration(Long duration) {
		this.duration = duration;
  }

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(CardioValue.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setInfo(String info) {
		this.info = info;
  }

	public void setPulse(Integer pulse) {
		this.pulse = pulse;
  }

  public void setPulseMax(Integer pulseMax) {
    this.pulseMax = pulseMax;
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  }

  @SuppressWarnings("unchecked")
  public JSONObject getJson() {
    JSONObject obj=new JSONObject();
    obj.put("calories", getCalories());
    obj.put("date",(getDate() != null)? getDate().toString() : null);
    obj.put("duration", getDuration());
    obj.put("id", getId());
    obj.put("info", getInfo());
    obj.put("openId", getUid());
    obj.put("pulse", getPulse());
    obj.put("pulseMax", getPulseMax());
    
    return obj;
  } 
}
