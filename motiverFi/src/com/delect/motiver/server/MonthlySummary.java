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
