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

import java.io.Serializable;
import java.util.Date;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class ExerciseModel extends BaseModelData implements Serializable, Comparable<ExerciseModel> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4465554L;
	public ExerciseModel() {
		
	}
	
	public ExerciseModel(ExerciseNameModel name) {
		setName(name);
	}

	public ExerciseModel(String name, int equipment, int sets, String reps) {
		setName(new ExerciseNameModel(0, name, equipment));
		setSets(sets);
		setReps(reps);
	}
	public ExerciseModel(String name, int equipment, int sets, String reps, String weights) {
		setName(new ExerciseNameModel(0, name, equipment));
		setSets(sets);
		setReps(reps);
		setWeights(weights);
	}

	@Override
	public int compareTo(ExerciseModel arg0) {
		try {
			//if compared value has no order set
			if(arg0.getOrder() == 1000 && this.getOrder() != 1000) {
				return 0;
      }
			//if this value has no order set
			else if(arg0.getOrder() != 1000 && this.getOrder() == 1000) {
				return 1;
      }
			//both order are null
			else if(arg0.getOrder() == 1000 && this.getOrder() == 1000) {
				return (arg0.getId() < this.getId())? 1 : 0;
      }
			else {
				return (arg0.getOrder() < this.getOrder())? 1 : 0;
      }
		} catch (Exception e) {
		}
		return 0;
	}
	//used only in lastweights-fetch
	public Date getDate() {
		return get("d");
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
	public ExerciseNameModel getName() {
		return get("m");
	}
	public int getOrder() {
		if(get("o") != null) {
			return get("o");
    }
		else {
			return 1000;
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
	public String getRest() {
		if(get("re") != null) {
			return get("re");
    }
		else {
			return "";
    }
  }
	public int getSets() {
		if(get("s") != null) {
			return get("s");
    }
		else {
			return 0;
    }
  }
	public String getTempo() {
		if(get("t") != null) {
			return get("t");
    }
		else {
			return "";
    }
  }
//	public String getUid() {
//		if(get("uid") != null) {
//			return get("uid");
//    }
//		else {
//			return "";
//    }
//  }
	public String getWeights() {
		if(get("w") != null) {
			return get("w");
    }
		else {
			return "";
    }
  }

	public long getWorkoutId() {
		if(get("wid") != null) {
			return get("wid");
    }
		else {
			return 0L;
    }
  }

  public boolean isPersonalBest() {
    if(get("pb") != null) {
      return get("pb");
    }
    else {
      return false;
    }
  }
  
  public WorkoutModel getWorkout() {
    return get("wo");
  }
	
	public void setDate(Date date) {
		set("d", date);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setInfo(String info) {
    set("i", info);
  }
	public void setName(ExerciseNameModel name) {
		set("m", name);
		if(name != null) {
			set("n", name.getName());	//set so grid shows name
    }
	}
	public void setOrder(int o) {
		set("o", o);
	}
	public void setReps(String reps) {
    set("r", reps);
  }
	public void setRest(String rest) {
    set("re", rest);
  }
	public void setSets(int sets) {
    set("s", sets);
  }
	public void setTempo(String tempo) {
    set("t", tempo);
  }
//	public void setUid(String uid) {
//		set("uid", uid);
//	}
	public void setWeights(String weights) {
    set("w", weights);
  }
  public void setPersonalBest(boolean isPersonalBest) {
    set("pb", isPersonalBest);
  }
	public void setWorkoutId(long id) {
		set("wid", id);
	}

  public void setWorkout(WorkoutModel workout) {
    set("wo", workout);
  }
  
  @Override
  public String toString() {
    return "Exercise"+getOrder()+": [name: '"+((getName() != null)? getName().getName() : "")+"', "+getSets()+" x "+getReps()+" x "+getWeights()+"]";
  }
}
