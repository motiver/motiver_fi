/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.ExerciseModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Exercise implements Serializable {
	
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static ExerciseModel getClientModel(Exercise model) {
		if(model == null) {
			return null;
    }

		ExerciseModel modelClient = new ExerciseModel();
		modelClient.setId(model.getId());
		modelClient.setSets(model.getSets());
		modelClient.setReps(model.getReps());
		modelClient.setWeights(model.getWeights());
		modelClient.setOrder(model.getOrder());
		return modelClient;
	}
  
	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Exercise getServerModel(ExerciseModel model) {
		if(model == null) {
			return null;
    }

		Exercise modelServer = new Exercise( );

		if(model.getName() != null) {
			modelServer.setNameId(model.getName().getId());
    }
		else {
			modelServer.setNameId(0L);
    }
		modelServer.setId(model.getId());
		modelServer.setSets(model.getSets());
		modelServer.setReps(model.getReps());
		modelServer.setTempo(model.getTempo());
		modelServer.setRest(model.getRest());
		modelServer.setWeights(model.getWeights());
		modelServer.setInfo(model.getInfo());
		modelServer.setOrder(model.getOrder());
		
		return modelServer;
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent(defaultFetchGroup="false")
	private String info;

	@Persistent
	private Long name = 0L;

	@Persistent
	private Integer order = 0;

	@Persistent
	private String reps;

	@Persistent(defaultFetchGroup="false")
	private String rest;

	@Persistent
	private Integer sets;

	@Persistent(defaultFetchGroup="false")
	private String tempo;

	@Persistent
	private String weights;

	@Persistent
	private Workout workout;

	public Exercise() {
    
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
    return info;
  }

	public Long getNameId() {
		if(name != null) {
			return name;
    }
		else {
			return 0L;
    }
  }

	public Integer getOrder() {
		if(order != null) {
			return order;
    }
		else {
			return 1000;
    }
	}

	public String getReps() {
		if(reps != null) {
			return reps;
    }
		else {
			return "";
    }
  }

	public String getRest() {
		if(rest != null) {
			return rest;
    }
		else {
			return "";
    }
  }

	public Integer getSets() {
		if(sets != null) {
			return sets;
    }
		else {
			return 0;
    }
  }

	public String getTempo() {
		if(tempo != null) {
			return tempo;
    }
		else {
			return "";
    }
  }

	public String getWeights() {
		if(weights != null) {
			return weights;
    }
		else {
			return "";
    }
  }

	public Workout getWorkout() {
		return workout;
	}

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Exercise.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setInfo(String info) {
		this.info = info;
  }

	public void setNameId(Long name) {
		this.name = name;
  }

	public void setOrder(Integer order) {
		this.order = order;
	}

	public void setReps(String reps) {
		this.reps = reps;
  }

	public void setRest(String rest) {
		this.rest = rest;
  }

	public void setSets(Integer sets) {
		this.sets = sets;
  }

	public void setTempo(String tempo) {
		this.tempo = tempo;
  }
	
	public void setWeights(String weights) {
		this.weights = weights;
  }
}
