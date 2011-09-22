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
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * Saves search index for workouts for faster search results
 * @author Antti
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class WorkoutSearchIndex {
	
	@SuppressWarnings("unused")
	@Persistent
	private Date date;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
	private Key id = null;

	@Persistent
	private List<Key> keys;

	@Persistent
	private String query;
	
	public WorkoutSearchIndex(String query, List<Key> keys) {
		this.query = query;
		this.keys = keys;
		
		date = new Date();
	}

	public Long getId() {
		if(id != null) {
			return id.getId();
    }
		else {
			return 0L;
    }
  }

	public List<Key> getIds() {
		return keys;
  }

	public Key getKey() {
		return id;
	}

	public String getQuery() {
		return query;
	}

}
