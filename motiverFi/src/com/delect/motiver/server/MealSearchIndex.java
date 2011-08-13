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
 * Saves search index for meals for faster search results
 * @author Antti
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MealSearchIndex {
	
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
	
	public MealSearchIndex(String query, List<Key> keys) {
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
