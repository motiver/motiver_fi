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
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.delect.motiver.shared.MonthlySummaryExerciseModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MonthlySummaryExercise {
	
  public static MonthlySummaryExerciseModel getClientModel(MonthlySummaryExercise model) {
    MonthlySummaryExerciseModel modelClient = new MonthlySummaryExerciseModel(model.getType(), model.getValue());
    modelClient.setSets(model.getSets());
    modelClient.setReps(model.getReps());
    modelClient.setWeights(model.getWeights());
    modelClient.setPersonalBest(model.isPersonalBest());
    
    return modelClient;
    
  }
  
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private Long nameId;
	
	/**
	 * Length of set
	 * <br>0: 2-5 reps, 1: 6-10, 2: 11-... 
	 */
	@Persistent
	private Integer length;
	
	@Persistent
	private Integer sets;
	
	@Persistent
	private String reps;
	
	@Persistent
	private String weights;
	
	@Persistent
	private Boolean isPersonalBest;
	
	/**
	 * Type: 0=max, 1=best
	 */
	@Persistent
	private Integer type;

  /**
   * Value: if type=0: max. if type=1: work
   */
  @Persistent
  private Double value;
  
  @Persistent
  private Long uid;
  
  @Persistent
  public String openId;
  
  @Persistent
  private Date workoutDate;
  
	public MonthlySummaryExercise(Integer type, Double value) {
		setType(type, value);
	}

	public Long getNameId() {
    return nameId;
  }
  
  public Integer getLength() {
    return length;
  }
	
  public Integer getSets() {
    return sets;
  }
  
  public String getReps() {
    return reps;
  }
  
  public String getWeights() {
    return weights;
  }
  
  public Integer getType() {
    return type;
  }
  
  public Double getValue() {
    return value;
  }
  
  public Boolean isPersonalBest() {
    return isPersonalBest;
  }


  public void setType(Integer type, Double value) {
    this.type = type;
    this.value = value;
  }
  
	public void setNameId(Long nameId) {
		this.nameId = nameId;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public void setSets(Integer sets) {
    this.sets = sets;
  }

  public void setReps(String reps) {
    this.reps = reps;
  }

  public void setWeights(String weights) {
    this.weights = weights;
  }
  
  public void setWorkoutDate(Date workoutDate) {
    this.workoutDate = workoutDate;
  }
	
	public void setPersonalBest(Boolean isPersonalBest) {
	  this.isPersonalBest = isPersonalBest;
	}
	
	public void setUid(String openId) {
	  this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
