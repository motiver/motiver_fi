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

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class UserModel extends BaseModelData implements IsSerializable, Comparable<UserModel>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1579824833L;

	
	public UserModel() {
	  
	}
	
	public UserModel(String uid) {
	  setUid(uid);
	}

	@Override
	public int compareTo(UserModel compare) {
		return getEmail().compareTo(compare.getEmail());
	}
	
	@Override
	public boolean equals(Object object) {
	  if(object instanceof UserModel) {
	    return ((UserModel)object).getUid().equals(getUid());
	  }
	  return false;
	}
	
	public String getAlias() {
	  return get("a");
	}
	
	/**
	 * Returns url for this users blog
	 * @return
	 */
	public String getBlogUrl() {
	  String url = "http://";
	  if(getAlias() != null) {
	    url += getAlias();
	  }
	  else {
	    url += String.valueOf(getUid());
	  }
	  url += ".motiver.fi/app/?";
	  
	  return url;
	}
	
	/**
	 * 0 = dd.mm.yyyy, 1 = mm/dd/yyyy
	 */
	public int getDateFormat() {
		if(get("df") != null) {
			return get("df");
    }
		else {
			return 0;
    }
	}
	public String getNickName() {
		if(get("fn") != null) {
			return get("fn");
    }
		else {
			return "";
    }
	}
	public String getGender() {
		if(get("ge") != null) {
			return get("ge");
    }
		else {
			return "";
    }
	}
	public String getEmail() {
		if(get("ln") != null) {
			return get("ln");
    }
		else {
			return "";
    }
	}
	public String getLocale() {
		if(get("lo") != null) {
			return get("lo");
    }
		else {
			return "";
    }
	}
	/**
	 * 0 = metric, 1 = US
	 */
	public int getMeasurementSystem() {
		if(get("ms") != null) {
			return get("ms");
    }
		else {
			return 0;
    }
	}
	
//	/**
//	 * Target to share cardio with
//	 * <br>-2 : share with everybody
//	 * <br>-1 : share with nobody
//	 * <br>0 : share with all facebook friends (DEFAULT)
//	 * <br>>0 : facebook group id
//	 */
//	public String getShareCardio() {
//		if(get("shc") != null && ((String)get("shc")).length() > 0) {
//      return get("shc");
//    }
//		
//		return "0";
//  }
//	/**
//	 * Target to coach
//	 * <br>-1 : share with nobody
//	 * <br>>0 : facebook user id
//	 */
//	public String getShareCoach() {
//		if(get("shco") != null) {
//			return get("shco");
//    }
//		
//		return "";
//  }
//	/**
//	 * Target to share measurements with
//	 * <br>-2 : share with everybody
//	 * <br>-1 : share with nobody
//	 * <br>0 : share with all facebook friends (DEFAULT)
//	 * <br>>0 : facebook group id
//	 */
//	public String getShareMeasurement() {
//		if(get("shm") != null && ((String)get("shm")).length() > 0) {
//      return get("shm");
//    }
//		
//		return "0";
//  }
//	/**
//	 * Target to share nutrition with (only calories)
//	 * <br>-2 : share with everybody
//	 * <br>-1 : share with nobody
//	 * <br>0 : share with all facebook friends (DEFAULT)
//	 * <br>>0 : facebook group id
//	 */
//	public String getShareNutrition() {
//		if(get("shn") != null && ((String)get("shn")).length() > 0) {
//      return get("shn");
//    }
//		
//		return "0";
//  }
//	/**
//	 * Target to share foods with
//	 * <br>-2 : share with everybody
//	 * <br>-1 : share with nobody
//	 * <br>0 : share with all facebook friends (DEFAULT)
//	 * <br>>0 : facebook group id
//	 */
//	public String getShareNutritionFoods() {
//		if(get("shnf") != null && ((String)get("shnf")).length() > 0) {
//      return get("shnf");
//    }
//		
//		return "0";
//  }
//	/**
//	 * Target to share training with
//	 * <br>-2 : share with everybody
//	 * <br>-1 : share with nobody
//	 * <br>0 : share with all facebook friends (DEFAULT)
//	 * <br>>0 : facebook group id
//	 */
//	public String getShareTraining() {
//		if(get("sht") != null && ((String)get("sht")).length() > 0) {
//      return get("sht");
//    }
//		
//		return "0";
//  }
	/**
	 * 0 = HH:mm, 1 = KK:mm aa
	 */
	public int getTimeFormat() {
		if(get("tf") != null) {
			return get("tf");
    }
		else {
			return 0;
    }
	}
	public Integer getTimezone() {
		if(get("tz") != null) {
			return get("tz");
    }
		else {
			return 0;
    }
	}
//  public String getId() {
//    return get("id");
//  }
	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
    }
  }
	public boolean isAdmin() {
		if(get("admin") != null) {
			return get("admin");
    }
		else {
			return false;
    }
	}
	public boolean isBanned() {
		if(get("ba") != null) {
			return get("ba");
    }
		else {
			return false;
    }
	}
	public boolean isCoach() {
		if(get("coach") != null) {
			return get("coach");
    }
		else {
			return false;
    }
	}
  public String getLogoutUrl() {
    if(get("u") != null) {
      return get("u");
    }
    else {
      return "";
    }
  }
	
	
	
	
	
	public void setAdmin(boolean isAdmin) {
		set("admin", isAdmin);
	}
  public void setAlias(String alias) {
    set("a", alias);
  }
	public void setBanned(boolean isBanned) {
		set("ba", isBanned);
	}
	public void setCoach(boolean isCoach) {
		set("coach", isCoach);
	}
	/**
	 * 0 = dd.mm.yyyy, 1 = mm/dd/yyyy
	 */
	public void setDateFormat(int dateFormat) {
		set("df", dateFormat);
	}	
	public void setNickName(String firstName) {
		set("fn", firstName);
	}
	public void setGender(String gender) {
		set("ge", gender);
	}
	public void setEmail(String lastName) {
		set("ln", lastName);
	}
	public void setLocale(String locale) {
		set("lo", locale);
	}
	
	/**
	 * 0 = metric, 1 = US
	 */
	public void setMeasurementSystem(int measurementSystem) {
		set("ms", measurementSystem);
	}
//	/**
//	 * facebook group id
//	 * "-1" : share with nobody
//	 * "0" : share with all facebook friends (DEFAULT)
//	 */
//	public void setShareCardio(String shareCardio) {
//		set("shc", shareCardio);
//	}
//	/**
//	 * facebook group id
//	 * "-1" : share with nobody (DEFAULT)
//	 */
//	public void setShareCoach(String shareCoach) {
//		set("shco", shareCoach);
//	}
//	/**
//	 * facebook group id
//	 * "-1" : share with nobody
//	 * "0" : share with all facebook friends (DEFAULT)
//	 */
//	public void setShareMeasurement(String shareMeasurement) {
//		set("shm", shareMeasurement);
//	}
//	/**
//	 * facebook group id
//	 * "-1" : share with nobody
//	 * "0" : share with all facebook friends (DEFAULT)
//	 */
//	public void setShareNutrition(String shareNutrition) {
//		set("shn", shareNutrition);
//	}
//	/**
//	 * facebook group id
//	 * "-1" : share with nobody
//	 * "0" : share with all facebook friends (DEFAULT)
//	 */
//	public void setShareNutritionFoods(String shareNutritionFoods) {
//		set("shnf", shareNutritionFoods);
//	}
//	/**
//	 * facebook group id
//	 * "-1" : share with nobody
//	 * "0" : share with all facebook friends (DEFAULT)
//	 */
//	public void setShareTraining(String shareTraining) {
//		set("sht", shareTraining);
//	}
	/**
	 * 0 = HH:mm, 1 = KK:mm aa
	 */
	public void setTimeFormat(int timeFormat) {
		set("tf", timeFormat);
	}
	public void setTimezone(Integer timezone) {
		set("tz", timezone);
	}

//	public void setId(String id) {
//		set("id", id);
//	}

  public void setUid(String uid) {
    set("uid", uid);
  }
  
  public void setLogoutUrl(String url) {
    set("u", url);
  }
  
  public String toString() {
    return "User [uid: '"+getUid()+"']";
  }
}
