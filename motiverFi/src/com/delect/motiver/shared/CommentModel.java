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
	public UserModel getUser() {
		return get("uid");
  }
	public UserModel getUserTarget() {
		return get("uidT");
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
	public void setUser(UserModel uid) {
		set("uid", uid);
	}
	public void setUserTarget(UserModel uidT) {
		set("uidT", uidT);
	}
	public void setUnread(boolean isUnread) {
		set("un", isUnread);
	}
	public void setWorkout(WorkoutModel w) {
		set("w", w);
	}
}
