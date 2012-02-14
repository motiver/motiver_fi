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
package com.delect.motiver.server.jdo.training;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.delect.motiver.shared.ExerciseNameModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ExerciseName implements Serializable {
	
	/**
   * 
   */
  private static final long serialVersionUID = 2726251490814363630L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static ExerciseNameModel getClientModel(ExerciseName model) {
		if(model == null) {
			return null;
    }

		ExerciseNameModel modelClient = new ExerciseNameModel(model.getId(), model.getName(), model.getTarget());
		modelClient.setVideo(model.getVideo());
		modelClient.setLocale(model.getLocale());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static ExerciseName getServerModel(ExerciseNameModel model) {
		if(model == null) {
			return null;
    }
		
		ExerciseName modelServer = new ExerciseName(model.getName(), model.getTarget());
		modelServer.setId(model.getId());
		modelServer.setVideo(model.getVideo());
		modelServer.setLocale(model.getLocale());
    modelServer.setUid(model.getUid());
		
		return modelServer;
	}
  
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof ExerciseName) {
      ExerciseName name = (ExerciseName)obj;
      
      if(getId().longValue() == name.getId().longValue())
        return true;
      
      return  getName().equals(name.getName())
                && getTarget().equals(name.getTarget());
    }
    else {
      return false;
    }
  }
	
	public Integer countQuery;
  public Integer countUse;
	
	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String locale;

	@Persistent
	private String name;

	@Persistent
	private Integer target = 0;

	@Persistent	
	private String video;

	public ExerciseName() {
	  
	}
	
	public ExerciseName(String name, Integer target) {
		this.name = name;
		this.target = target;
	}

	/**
	 * Count value based on how much exercise name have been used by user
	 * @return
	 */
	public int getCountUse() {
		if(countUse != null) {
			return countUse;
    }
		else {
			return 0;
    }
	}

  /**
   * Count value based on how name matches query word
   * @return
   */
  public int getCountQuery() {
    if(countQuery != null) {
      return countQuery;
    }
    else {
      return 0;
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

	public String getLocale() {
		if(locale != null) {
			return locale;
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

	public Integer getTarget() {
		if(target != null) {
			return target;
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

	public String getVideo() {
		if(video != null) {
			return video;
    }
		else {
			return "";
    }
  }

	public void setCount(int countQuery, int countUse) {
    this.countUse = countUse;
    this.countQuery = countQuery;
  }

	public void setId(Long id) {
		if(id != 0) {
			this.id = KeyFactory.createKey(ExerciseName.class.getSimpleName(), id);
    }
		else {
			this.id = null;
    }
	}

	public void setLocale(String locale) {
		this.locale = locale;
  }

	public void setName(String name) {
		this.name = name;
  }

	public void setTarget(Integer target) {
		this.target = target;
  }

	public void setUid(String openId) {
		this.openId = openId;
	}
	
	public void setVideo(String video) {
		this.video = video;
	} 

  public Long getUidOld() {
    return uid;
  } 

  /**
   * Updates name from given model
   * @param model
   */
  public void update(ExerciseName model, boolean includeId) {
    if(includeId) {
      setId(model.getId());
    }
    setLocale(model.getLocale());
    setName(model.getName());
    setTarget(model.getTarget());
    setUid(model.getUid());
    setVideo(model.getVideo());
  }
  
  @Override
  public String toString() {
    return "ExerciseName: [id: "+getId()+", '"+getName()+"', equipment: '"+getTarget()+"']";
  }

  @SuppressWarnings("unchecked")
  public JSONObject getJson() {
    JSONObject obj=new JSONObject();
    obj.put("id",getId());
    obj.put("locale",getLocale());
    obj.put("name",getName());
    obj.put("openId",getUid());
    obj.put("target",getTarget());
    obj.put("video",getVideo());

    return obj;
  }
}
