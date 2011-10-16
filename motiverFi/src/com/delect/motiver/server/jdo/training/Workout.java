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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.OneToOne;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.WorkoutModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Workout implements Serializable, Comparable<Workout>, Cloneable {
	
	/**
   * 
   */
  private static final long serialVersionUID = -4265455516532989163L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static WorkoutModel getClientModel(Workout model) {
		if(model == null) {
			return null;
    }
		
		WorkoutModel modelClient = new WorkoutModel(model.getName());
		modelClient.setId(model.getId());
		modelClient.setRoutineId( model.getRoutineId() );
		modelClient.setTimeStart((int)model.getTimeStart().longValue());
		modelClient.setTimeEnd((int)model.getTimeEnd().longValue());
		modelClient.setDone(model.getDone());
		modelClient.setRating(model.getRating());
		modelClient.setDate(model.getDate());
		modelClient.setDayInRoutine(model.getDayInRoutine());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Workout getServerModel(WorkoutModel model) {
		if(model == null) {
			return null;
    }
		
		Workout modelServer = new Workout(model.getName());
		modelServer.setId(model.getId());
		modelServer.setRoutineId( model.getRoutineId() );
		modelServer.setTimeStart((long) model.getTimeStart());
		modelServer.setTimeEnd((long) model.getTimeEnd());
		modelServer.setDone(model.getDone());
		modelServer.setRating(model.getRating());
		modelServer.setDate(model.getDate());
		modelServer.setDayInRoutine(model.getDayInRoutine());
		modelServer.setInfo(model.getInfo());
		
		return modelServer;
	}
  
  public Object clone() throws CloneNotSupportedException {
    
    Workout clone = new Workout();
    clone.setDate(getDate());
    clone.setDayInRoutine(getDayInRoutine());
    clone.setDone(getDone());
    clone.setInfo(getInfo());
    clone.setName(getName());
    clone.setRating(getRating());
    clone.setRoutineId(getRoutineId());
    clone.setTimeEnd(getTimeEnd());
    clone.setTimeStart(getTimeStart());
    clone.setUid(getUid());
    
    List<Exercise> exercises = new ArrayList<Exercise>();
    for(Exercise e : getExercises()) {
      exercises.add((Exercise) e.clone());
    }
    clone.setExercises(exercises);
    
    return clone;
  }
  
	
	@Persistent
	public Integer dayInRoutine = 0;
	@Persistent
	public Integer rating = 0;
	/**
	 * How many times this have been copied
	 */
  @Persistent
	private Integer copyCount = 0;
	@Persistent
	private Date date;
	@Persistent
	private Boolean done = false;
	@Persistent(mappedBy = "workout")
  private List<Exercise> exercises = new ArrayList<Exercise>();
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String info;
	@Persistent
	private String name;
	@OneToOne
	private Long routineId = 0L;
	@Persistent
	private Long timeEnd = 0L;
	@Persistent
	private Long timeStart = 0L;
	@Persistent
	private Long uid;
  @Persistent
  public String openId;

  @NotPersistent
  private UserOpenid user;

	public Workout() {
		
	}

	public Workout(String name) {
		this.setName(name);
	}

	@Override
	public int compareTo(Workout compare) {
		return getName().toLowerCase().compareTo(compare.getName().toLowerCase());
	}

	public Date getDate() {
		return date;
  }

	public Integer getDayInRoutine() {
		return dayInRoutine;
  }

	public Boolean getDone() {
		if(done != null) {
			return done;
    }
		else {
			return false;
    }
  }

	public List<Exercise> getExercises() {
		return exercises;
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

	public String getInfo() {
		if(info != null) {
			return info;
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

	public Integer getRating() {
		if(rating != null) {
			return rating;
    }
		else {
			return 0;
    }
  }

	public Long getRoutineId() {
		if(routineId != null) {
			return routineId;
    }
		else {
			return 0L;
    }
  }

	public Long getTimeEnd() {
		if(timeEnd != null) {
			return timeEnd;
    }
		else {
			return 0L;
    }
  }

	public Long getTimeStart() {
		if(timeStart != null) {
			return timeStart;
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

	/**
	 * Adds one to copy count
	 */
	public void incrementCopyCount() {
		copyCount++;
	}

	public void setDate(Date date) {
    this.date = date;
  }

	public void setDayInRoutine(Integer dayInWorkout) {
    dayInRoutine = dayInWorkout;
  }

	public void setDone(Boolean done) {
    this.done = done;
  }

	public void setExercises(List<Exercise> exercises) {
		this.exercises = exercises;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Workout.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setName(String name) {
    this.name = name;
  }

	public void setRating(Integer rating) {
    this.rating = rating;
  }

	public void setRoutineId(Long routineId) {
		this.routineId = routineId;
  }
	
	public void setTimeEnd(Long timeEnd) {
    this.timeEnd = timeEnd;
  }

	public void setTimeStart(Long timeStart) {
    this.timeStart = timeStart;
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 

  public UserOpenid getUser() {
    return user;
  }

  public void setUser(UserOpenid user) {
    this.user = user;
  }

  public Integer getCount() {
    return copyCount;
  }

  public void setCount(Integer copyCount) {
    this.copyCount = copyCount;
  }

  /**
   * Updates time from given model
   * @param model
   */
  public void update(Workout model) {
    setDate(model.getDate());
    setDayInRoutine(model.getDayInRoutine());
    setDone(model.getDone());
    setInfo(model.getInfo());
    setName(model.getName());
    setRating(model.getRating());
    setRoutineId(model.getRoutineId());
    setTimeEnd(model.getTimeEnd());
    setTimeStart(model.getTimeStart());
    setUid(model.getUid());
    
    for(Exercise f : model.getExercises()) {
      int i = getExercises().indexOf(f);
      if(i != -1) {
        Exercise fOld = getExercises().get(i);
        fOld.update(f);
      }
      else {
        getExercises().add(f);
      }
    }
  }
}
