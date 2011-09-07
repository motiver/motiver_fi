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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * Use count value for each food name. How many times given name (id) is used. 
 * <br>Is fetched regulary as a cron job.
 * @author Antti
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FoodNameCount {
	
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

	public FoodNameCount(Long nameId, Integer count, String openId) {
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

  public void setCount(Integer count) {
    this.count = count;
  }
}
