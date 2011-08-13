/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * Saves search index for foods for faster search results
 * @author Antti
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ExerciseSearchIndex {
	
	@SuppressWarnings("unused")
	@Persistent(defaultFetchGroup="false")
	private Date date;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY) 
	private Key id = null;

	@Persistent
	private List<Key> keys;

	@SuppressWarnings("unused")
	@Persistent(defaultFetchGroup="false")
	private String locale;

	@Persistent(defaultFetchGroup="false")
	private String query;

	public ExerciseSearchIndex(String query, String locale, List<Key> keys) {
		this.query = query;
		this.locale = locale;
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
