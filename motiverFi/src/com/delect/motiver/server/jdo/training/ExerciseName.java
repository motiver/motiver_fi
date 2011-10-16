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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.ExerciseNameModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ExerciseName implements Serializable, Comparable<ExerciseName> {
	
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
		
		return modelServer;
	}
	
	public Integer countQuery;
  public Integer countUse;
	
	@Persistent(defaultFetchGroup="false")
	public Long uid;
  
  @Persistent(defaultFetchGroup="false")
  public String openId;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent(defaultFetchGroup="false")
	private String locale;

	@Persistent
	private String name;

	@Persistent
	private Integer target = 0;

	@Persistent(defaultFetchGroup="false")
	
	private String video;

	public ExerciseName(String name, Integer target) {
		this.name = name;
		this.target = target;
	}
	
	@Override
	public int compareTo(ExerciseName compare) {
		int count = getCountQuery();
		int count2 = compare.getCountQuery();
		
		//if equal count -> compare also use count
		if(count == count2) {
			return compare.getCountUse() - getCountUse();
		}
		else {
			return count2 - count;
    }
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
}
