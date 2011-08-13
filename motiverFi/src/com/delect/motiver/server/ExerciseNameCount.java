
/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * Use count value for each exercise. How many times given name (id) is used. 
 * <br>Is fetched regulary as a cron job.
 * @author Antti
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ExerciseNameCount {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent(defaultFetchGroup="false")
	private Long nameId;
	@Persistent
	private Integer count;
  @Persistent(defaultFetchGroup="false")
  public Long uid;
  
  @Persistent(defaultFetchGroup="false")
  public String openId;

	public ExerciseNameCount(Long nameId, Integer count, String openId) {
		this.nameId = nameId;
		this.count = count;
    this.openId = openId;
	}

	public int getCount() {
		if(count != null) {
			return count;
    }
		else {
			return 0;
    }
	}
}
