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

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.delect.motiver.shared.MonthlySummaryModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MonthlySummary {
	
  public static MonthlySummaryModel getClientModel(MonthlySummary model) {
    MonthlySummaryModel modelClient = new MonthlySummaryModel(model.getDate());
    modelClient.setId(model.getId());
    modelClient.setUid(model.getUid());
    
    return modelClient;
  }
  
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private Long uid;
  
  @Persistent
  public String openId;
	
	@Persistent
	private Date date;
	
	//exercises
	@Persistent(defaultFetchGroup="false")
	private List<MonthlySummaryExercise> exercises;
  
	public MonthlySummary(Date date) {
		setDate(date);
	}

  public Long getId() {
    if(id != null) {
      return id.getId();
    }
    else {
      return 0L;
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
  
  public Date getDate() {
    return date;
  }
  
  public List<MonthlySummaryExercise> getExercises() {
    return exercises;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  public void setExercises(List<MonthlySummaryExercise> exercises) {
    this.exercises = exercises;
  }

	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
