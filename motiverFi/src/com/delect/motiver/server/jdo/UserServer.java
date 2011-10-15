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
package com.delect.motiver.server.jdo;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.delect.motiver.shared.UserModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserServer {
	
  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static UserModel getClientModel(UserServer model) {
		UserModel modelClient = new UserModel();
		
		try {
//			if(model.getUid() != null) {
//				modelClient.setUid(model.getUid());
//      }
			if(model.getDateFormat() != null) {
				modelClient.setDateFormat(model.getDateFormat());
      }
			if(model.getTimeFormat() != null) {
				modelClient.setTimeFormat(model.getTimeFormat());
      }
			if(model.getMeasurementSystem() != null) {
				modelClient.setMeasurementSystem(model.getMeasurementSystem());
      }
			modelClient.setNickName(model.getFirstName());
			modelClient.setEmail(model.getLastName());
			modelClient.setTimezone(model.getTimezone());
			modelClient.setGender(model.getGender());
			modelClient.setLocale(model.getLocale());
			
			modelClient.setAdmin(model.isAdmin());
			modelClient.setBanned(model.isBanned());
			modelClient.setAlias(model.getAlias());
			
		} catch (Exception e) {
		}
		
		return modelClient;
	}
	
	@Persistent(defaultFetchGroup="false")
	private Boolean admin;
  @Persistent(defaultFetchGroup="false")
  private String alias;
	@Persistent(defaultFetchGroup="false")
	private Boolean banned;
	@SuppressWarnings("unused")
	@Persistent(defaultFetchGroup="false")
	private Date createDate;
	@Persistent(defaultFetchGroup="false")
	private Integer dateFormat;
	@Persistent(defaultFetchGroup="false")
	private String fbAuthToken;
	@Persistent(defaultFetchGroup="false")
	private String firstName;
	@Persistent(defaultFetchGroup="false")
	private String gender;
	@Persistent(defaultFetchGroup="false")
	private String lastName;
	@Persistent(defaultFetchGroup="false")
	private String locale;
	@Persistent(defaultFetchGroup="false")
	private Integer measurementSystem;
	@Persistent(defaultFetchGroup="false")
	private String shareCardio;
	@Persistent(defaultFetchGroup="false")
	private String shareCoach;
	@Persistent(defaultFetchGroup="false")
	private String shareMeasurement;
	@Persistent(defaultFetchGroup="false")
	private String shareNutrition;
	@Persistent(defaultFetchGroup="false")
	private String shareNutritionFoods;
	@Persistent(defaultFetchGroup="false")
	private String shareTraining;
	@Persistent(defaultFetchGroup="false")
	private Long timeComments;
	@Persistent(defaultFetchGroup="false")
	private Integer timeFormat;
	@Persistent(defaultFetchGroup="false")
	private Integer timezone;

  @PrimaryKey
	@Persistent
	private Long uid;	//facebook uid
	
	public UserServer() {
		createDate = new Date();
	}

	public UserServer(Long uid) {
		createDate = new Date();
		this.uid = uid;
	}
	
	public String getAlias() {
	  return alias;
	}

	public Integer getDateFormat() {
		return dateFormat;
	}

	/**
	 * Returns user's current auth token for facebook
	 * @return token : null when user have logged out
	 */
	public String getFbAuthToken() {
		if(fbAuthToken != null) {
			return fbAuthToken;
    }
		else {
			return "";
    }
	}

	public String getFirstName() {
		return firstName;
	}
	
	public String getGender() {
		return gender;
	}

	/**
	 * Returns the time when comments were last
	 * @return
	 */
	public long getLastCommentTime() {
		if(timeComments != null) {
			return timeComments;
    }
		else {
			return 0;
    }
	}

	public String getLastName() {
		return lastName;
	}

	public String getLocale() {
		return locale;
	}

	public Integer getMeasurementSystem() {
		return measurementSystem;
	}

	/**
	 * Target to share cardio with
	 * <br>-2 : share with everybody
	 * <br>-1 : share with nobody
	 * <br>0 : share with all facebook friends (DEFAULT)
	 * <br>>0 : facebook group id
	 */
	public String getShareCardio() {
		if(shareCardio != null && shareCardio.length() > 0) {
      return shareCardio;
    }
		
		return "0";
  }

	/**
	 * Target to coach
	 * <br>-1 : share with nobody
	 * <br>>0 : facebook user id
	 */
	public String getShareCoach() {
		if(shareCoach != null) {
      return shareCoach;
    }
		
		return "";
  }

	/**
	 * Target to share measurements with
	 * <br>-2 : share with everybody
	 * <br>-1 : share with nobody
	 * <br>0 : share with all facebook friends (DEFAULT)
	 * <br>>0 : facebook group id
	 */
	public String getShareMeasurement() {
		if(shareMeasurement != null && shareMeasurement.length() > 0) {
      return shareMeasurement;
    }
		
		return "0";
  }

	/**
	 * Target to share nutrition with (only calories)
	 * <br>-2 : share with everybody
	 * <br>-1 : share with nobody
	 * <br>0 : share with all facebook friends (DEFAULT)
	 * <br>>0 : facebook group id
	 */
	public String getShareNutrition() {
		if(shareNutrition != null && shareNutrition.length() > 0) {
      return shareNutrition;
    }
		
		return "0";
  }

	/**
	 * Target to share foods with
	 * <br>-2 : share with everybody
	 * <br>-1 : share with nobody
	 * <br>0 : share with all facebook friends (DEFAULT)
	 * <br>>0 : facebook group id
	 */
	public String getShareNutritionFoods() {
		if(shareNutritionFoods != null && shareNutritionFoods.length() > 0) {
      return shareNutritionFoods;
    }
		
		return getShareNutrition();
  }

	/**
	 * Target to share training with
	 * <br>-2 : share with everybody
	 * <br>-1 : share with nobody
	 * <br>0 : share with all facebook friends (DEFAULT)
	 * <br>>0 : facebook group id
	 */
	public String getShareTraining() {
		if(shareTraining != null && shareTraining.length() > 0) {
      return shareTraining;
    }
		
		return "0";
  }

	public Integer getTimeFormat() {
		return timeFormat;
	}

	public Integer getTimezone() {
		return timezone;
	}
	
	public Long getUid() {
		if(uid != null) {
			return uid;
    }
		else {
			return 0L;
    }
	}

	/**
	 * If user has administrator priviledges
	 * @return
	 */
	public Boolean isAdmin() {
		if(admin != null) {
			return admin;
    }
		else {
			return false;
    }
	}

  /**
	 * If user is banned
	 * @return
	 */
	public Boolean isBanned() {
		if(banned != null) {
			return banned;
    }
		else {
			return true;
    }
	}

	/**
	 * Sets if user has administrator priviledges
	 * @param admin
	 */
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	/**
	 * Sets alias for user's blog (alias.motiver.fi)
	 * @param alias
	 */
	public void setAlias(String alias) {
	  this.alias = alias;
	}

	/**
	 * Sets if user is banned
	 * @param admin
	 */
	public void setBanned(Boolean banned) {
		this.banned = banned;
	}

	public void setDateFormat(int dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * Sets current auth token for facebook for this user
	 * @param fbAuthToken
	 */
	public void setFbAuthToken(String fbAuthToken) {
		this.fbAuthToken = fbAuthToken;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Sets the date when comments were fetch last
	 * @param date : in seconds
	 */
	public void setLastCommentTime(long time) {
		timeComments = time;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setMeasurementSystem(int measurementSystem) {
		this.measurementSystem = measurementSystem;
	}

	/**
	 * facebook group id
	 * <br>"-1" : share with nobody
	 * <br>"0" : share with all facebook friends (DEFAULT)
	 */
	public void setShareCardio(String shareCardio) {
		this.shareCardio = shareCardio;
	}

	/**
	 * facebook group id
	 * <br>"-1" : share with nobody (DEFAULT)
	 */
	public void setShareCoach(String shareCoach) {
		this.shareCoach = shareCoach;
	}

	/**
	 * facebook group id
	 * <br>"-1" : share with nobody
	 * <br>"0" : share with all facebook friends (DEFAULT)
	 */
	public void setShareMeasurement(String shareMeasurement) {
		this.shareMeasurement = shareMeasurement;
	}

	/**
	 * facebook group id
	 * <br>"-1" : share with nobody
	 * <br>"0" : share with all facebook friends (DEFAULT)
	 */
	public void setShareNutrition(String shareNutrition) {
		this.shareNutrition = shareNutrition;
	}

	/**
	 * facebook group id
	 * <br>"-1" : share with nobody
	 * <br>"0" : share with all facebook friends (DEFAULT)
	 */
	public void setShareNutritionFoods(String shareNutritionFoods) {
		this.shareNutritionFoods = shareNutritionFoods;
	}

	/**
	 * facebook group id
	 * <br>"-1" : share with nobody
	 * <br>"0" : share with all facebook friends (DEFAULT)
	 */
	public void setShareTraining(String shareTraining) {
		this.shareTraining = shareTraining;
	}

	public void setTimeFormat(int timeFormat) {
		this.timeFormat = timeFormat;
	}

	public void setTimezone(Integer timezone) {
		this.timezone = timezone;
	}
	
	public void setId(Long uid) {
	  this.uid = uid;
	}
}
