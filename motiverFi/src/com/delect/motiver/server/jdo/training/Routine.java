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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Routine implements Serializable, Comparable<Routine> {
		
	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static RoutineModel getClientModel(Routine model) {
		if(model == null) {
			return null;
    }
		
		RoutineModel modelClient = new RoutineModel(model.getName());
		modelClient.setId(model.getId());
		modelClient.setDate(model.getDate());
		modelClient.setDays(model.getDays());
		modelClient.setInfo(model.getInfo());
    modelClient.setCount(model.getCount());
    
    //workouts
    if(model.getWorkouts() != null) {
      List<WorkoutModel> workouts = new ArrayList<WorkoutModel>();
      for(Workout m : model.getWorkouts()) {
        workouts.add(Workout.getClientModel(m));
      }
      modelClient.setWorkouts(workouts);
    }
    
    //user
    modelClient.setUser(UserOpenid.getClientModel(model.getUser()));
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Routine getServerModel(RoutineModel model) {
		
		if(model == null) {
			return null;
    }
		
		Routine modelServer = new Routine(model.getName());
		modelServer.setId(model.getId());
		modelServer.setDate(model.getDate());
		modelServer.setDays(model.getDays());
		modelServer.setInfo(model.getInfo());
    if(model.getUser() != null)
      modelServer.setUid(model.getUser().getUid());
		
		return modelServer;
	}
  
  public Object clone() throws CloneNotSupportedException {
    
    Routine clone = new Routine();
    clone.setDate(getDate());
    clone.setDays(getDays());
    clone.setInfo(getInfo());
    clone.setName(getName());
    clone.setUid(getUid());
    
//    List<Workout> workouts = new ArrayList<Workout>();
//    for(Workout e : getWorkouts()) {
//      workouts.add((Workout) e.clone());
//    }
//    clone.setWorkouts(workouts);
    
    return clone;
  }
	
	@Persistent
	public Date date;

	/**
	 * How many times this have been copied
	 */
  @Persistent
	private Integer copyCount = 0;

	@Persistent
	private Integer days = 7;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String info;

	@Persistent
	private String name;

	@Persistent
	private Long uid;
  
  @Persistent
  public String openId;
  
  @NotPersistent
  public List<Workout> workouts;

  @NotPersistent
  private UserOpenid user;

  public Routine() {
  }
  
	public Routine(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Routine compare) {
		return getName().toLowerCase().compareTo(compare.getName().toLowerCase());
	}

	public Date getDate() {
		if(date != null) {
			return date;
    }
		else {
			return null;
    }
  }

	public Integer getDays() {
		if(days != null) {
			return days;
    }
		else {
			return 7;
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

	public String getUid() {
		if(openId != null) {
			return openId;
    }
		else {
			return "";
    }
  }

  public List<Workout> getWorkouts() {
    return workouts;
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

	public void setDays(Integer days) {
		this.days = days;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Routine.class.getSimpleName(), id);
    }
		this.id = k;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  }

  public void setWorkouts(List<Workout> workouts) {
    this.workouts = workouts;
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
   * Updates routine from given model
   * @param model
   */
  public void update(Routine model, boolean includeId) {
    if(includeId) {
      setId(model.getId());
    }
    setDate(model.getDate());
    setDays(model.getDays());
    setInfo(model.getInfo());
    setName(model.getName());
    setCount(model.getCount());

//    if(getWorkouts() != null && model.getWorkouts() != null) {
//      //if workouts removed -> check which was removed
//      if(getWorkouts().size() > model.getWorkouts().size()) {
//        for(Workout f : getWorkouts()) {
//          if(!model.getWorkouts().contains(f)) {
//            getWorkouts().remove(f);
//          }
//        }
//      }
//      //new workout added
//      else {
//        for(Workout f : model.getWorkouts()) {
//            int i = getWorkouts().indexOf(f);
//            if(i != -1) {
//              Workout fOld = getWorkouts().get(i);
//              fOld.update(f, includeId);
//            }
//            else {
//              getWorkouts().add(f);
//            }
//          }
//      }
//    }
  }
  
  @Override
  public String toString() {
    return "Routine: [id: "+getId()+", '"+getName()+"', workouts: "+((getWorkouts() != null)? getWorkouts().size() : "")+"" +
        ", '"+getUid()+"']";
  }
}
