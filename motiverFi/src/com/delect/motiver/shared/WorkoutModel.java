/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class WorkoutModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8523335L;
	
	public WorkoutModel() {
		
	}
	public WorkoutModel(String name) {
		this.setName(name);
    this.setTimeStart(0);
    this.setTimeEnd(0);
	}

	public Date getDate() {
		return get("d");
  }
	public int getDayInRoutine() {
		if(get("day") != null) {
			return get("day");
    }
		else {
			return 0;
    }
  }
	public boolean getDone() {
		if(get("do") != null) {
			return get("do");
    }
		else {
			return false;
    }
  }
	public List<ExerciseModel> getExercises() {
		return get("e");
	}
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public String getInfo() {
		return get("i");
	}
	public String getName() {
    return get("n");
  }
	public int getRating() {
		if(get("ra") != null) {
			return get("ra");
    }
		else {
			return 0;
    }
  }
	public long getRoutineId() {
		if(get("r") != null) {
			return get("r");
    }
		else {
			return 0L;
    }
  }
	public int getTimeEnd() {
		if(get("t2") != null) {
			return get("t2");
    }
		else {
			return 0;
    }
  }
	public int getTimeStart() {
		if(get("t1") != null) {
			return get("t1");
    }
		else {
			return 0;
    }
  }
	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
		}
  }

	public void setDate(Date date) {
    set("d", date);
  }
	public void setDayInRoutine(int day) {
    set("day", day);
  }
	public void setDone(boolean done) {
    set("do", done);
  }
	public void setExercises(List<ExerciseModel> exercises) {
		set("e", exercises);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setInfo(String info) {
		set("i", info);
	}
	public void setName(String name) {
		set("n", name);
	}
	public void setRating(int rating) {
    set("ra", rating);
  }
	public void setRoutineId(long routineId) {
    set("r", routineId);
  }
	public void setTimeEnd(int timeEnd) {
    set("t2", timeEnd);
  }
	public void setTimeStart(int timeStart) {
    set("t1", timeStart);
  }
	public void setUid(String uid) {
		set("uid", uid);
	}
}
