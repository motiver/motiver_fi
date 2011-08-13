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

public class CommentModel extends BaseModelData implements IsSerializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 5035449671650774103L;
	

	public CardioModel getCardio() {
		return get("c");
  }
	public Date getDate() {
		return get("d");
  }
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0;
    }
  }
	public MealModel getMeal() {
		return get("m");
  }
	public MeasurementModel getMeasurement() {
		return get("me");
  }
	public Date getNutritionDate() {
		return get("nd");
  }
	public RoutineModel getRoutine() {
		return get("r");
  }
	public RunModel getRun() {
		return get("ru");
  }
	public String getTarget() {
		return get("ta");
  }
	public String getText() {
		if(get("t") != null) {
			return get("t");
    }
		else {
			return "";
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
	public String getUidTarget() {
		if(get("uidT") != null) {
			return get("uidT");
    }
		else {
			return "";
    }
  }
	public WorkoutModel getWorkout() {
		return get("w");
  }
	/**
	 * Is new comment
	 * @return
	 */
	public boolean isUnread() {
		if(get("un") != null) {
			return get("un");
    }
		else {
			return false;
    }
	}


	public void setCardio(CardioModel c) {
		set("c", c);
	}
	public void setDate(Date d) {
		set("d", d);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setMeal(MealModel m) {
		set("m", m);
	}
	public void setMeasurement(MeasurementModel me) {
		set("me", me);
	}
	public void setNutritionDate(Date d) {
		set("nd", d);
	}
	public void setRoutine(RoutineModel r) {
		set("r", r);
	}
	public void setRun(RunModel ru) {
		set("ru", ru);
	}
	public void setTarget(String ta) {
		set("ta", ta);
	}
	public void setText(String t) {
		set("t", t);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}
	public void setUidTarget(String uidT) {
		set("uidT", uidT);
	}
	public void setUnread(boolean isUnread) {
		set("un", isUnread);
	}
	public void setWorkout(WorkoutModel w) {
		set("w", w);
	}
}
