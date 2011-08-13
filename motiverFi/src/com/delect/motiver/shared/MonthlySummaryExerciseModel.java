/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class MonthlySummaryExerciseModel extends BaseModelData implements IsSerializable, Comparable<MonthlySummaryExerciseModel> {
		
  /**
   * 
   */
  private static final long serialVersionUID = -8903977227142265223L;
  public MonthlySummaryExerciseModel()  {
		
	}
	public MonthlySummaryExerciseModel(int type, double value) {
		setType(type);
		setValue(value);
	}

	public ExerciseNameModel getExerciseName() {
		return get("n");
  }

	public int getSets() {
		if(get("s") != null) {
			return get("s");
    }
		else {
			return 0;
    }
  }
	public String getReps() {
		if(get("r") != null) {
			return get("r");
    }
		else {
			return "";
    }
  }
  public String getWeights() {
    if(get("w") != null) {
      return get("w");
    }
    else {
      return "";
    }
  }

  public int getType() {
    if(get("t") != null) {
      return get("t");
    }
    else {
      return 0;
    }
  }

  public double getValue() {
    if(get("v") != null) {
      return get("v");
    }
    else {
      return 0D;
    }
  }
	public boolean isPersonalBest() {
		return get("pb");
	}

	public void setExerciseName(ExerciseNameModel n) {
		set("n", n);
	}
	public void setSets(int sets) {
		set("s", sets);
	}
	public void setReps(String reps) {
		set("r", reps);
	}
  public void setWeights(String weights) {
    set("w", weights);
  }
  public void setType(int type) {
    set("t", type);
  }
  public void setValue(double value) {
    set("v", value);
  }
  public void setPersonalBest(boolean isPersonalBest) {
    set("pb", isPersonalBest);
  }
  public void setWorkoutDate(Date workoutDate) {
    set("wd", workoutDate);
  }

  @Override
  public int compareTo(MonthlySummaryExerciseModel compare) {
    if(getExerciseName() != null && compare.getExerciseName() != null) {
      return getExerciseName().getName().compareTo(compare.getExerciseName().getName()); 
    }
    
    return 0;
  }
}
