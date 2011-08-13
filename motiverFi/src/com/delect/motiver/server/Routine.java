/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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

import com.delect.motiver.shared.RoutineModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Routine implements Comparable<Routine> {
		
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static RoutineModel getClientModel(Routine model) {
		if(model == null) {
			return null;
    }
		
		RoutineModel modelClient = new RoutineModel(model.getName());
		modelClient.setId(model.getId());
		modelClient.setDate(model.getDate());
		modelClient.setDays(model.getDays());
		modelClient.setInfo(model.getInfo());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Routine getServerModel(RoutineModel model) {
		
		if(model == null) {
			return null;
    }
		
		Routine modelServer = new Routine(model.getName());
		modelServer.setId(model.getId());
		modelServer.setDate(model.getDate());
		modelServer.setDays(model.getDays());
		modelServer.setInfo(model.getInfo());
		
		return modelServer;
	}
	
	@Persistent
	public Date date;

	/**
	 * How many times this have been copied
	 */
  @Persistent
	private Integer copyCount = 0;

	@Persistent
	private Integer days = 7;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String info;

	@Persistent
	private String name;

	@Persistent
	private Long uid;
  
  @Persistent
  public String openId;
	
	public Routine(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Routine compare) {
		return getName().toLowerCase().compareTo(compare.getName().toLowerCase());
	}

	public Date getDate() {
		if(date != null) {
			return date;
    }
		else {
			return null;
    }
  }

	public Integer getDays() {
		if(days != null) {
			return days;
    }
		else {
			return 7;
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
		if(info != null) {
			return info;
    }
		else {
			return "";
    }
	}

	public String getName() {
		if(name != null) {
			return name;
    }
		else {
			return "";
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

	/**
	 * Adds one to copy count
	 */
	public void incrementCopyCount() {
		copyCount++;
	}

	public void setDate(Date date) {
    this.date = date;
  }

	public void setDays(Integer days) {
		this.days = days;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Routine.class.getSimpleName(), id);
    }
		this.id = k;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
