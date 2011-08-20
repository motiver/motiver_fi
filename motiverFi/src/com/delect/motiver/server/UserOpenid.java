/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.delect.motiver.shared.UserModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserOpenid implements Serializable {
	
  /**
   * 
   */
  private static final long serialVersionUID = 8039587661556604141L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static UserModel getClientModel(UserOpenid model) {
		UserModel modelClient = new UserModel();
		
		try {
      if(model.getId() != null) {
        modelClient.setId(model.getId());
      }
			if(model.getUid() != null) {
				modelClient.setUid(model.getUid());
      }
			if(model.getDateFormat() != null) {
				modelClient.setDateFormat(model.getDateFormat());
      }
			if(model.getTimeFormat() != null) {
				modelClient.setTimeFormat(model.getTimeFormat());
      }
			if(model.getMeasurementSystem() != null) {
				modelClient.setMeasurementSystem(model.getMeasurementSystem());
      }
			modelClient.setNickName(model.getNickName());
			modelClient.setEmail(model.getEmail());
			modelClient.setTimezone(model.getTimezone());
			modelClient.setGender(model.getGender());
			modelClient.setLocale(model.getLocale());
			
			modelClient.setShareTraining(model.getShareTraining());
			modelClient.setShareNutrition(model.getShareNutrition());
      modelClient.setShareNutritionFoods(model.getShareNutritionFoods());
			modelClient.setShareCardio(model.getShareCardio());
			modelClient.setShareMeasurement(model.getShareMeasurement());
			modelClient.setShareCoach(model.getShareCoach());
			modelClient.setAdmin(model.isAdmin());
			modelClient.setBanned(model.isBanned());
			modelClient.setAlias(model.getAlias());
			
		} catch (Exception e) {
		}
		
		return modelClient;
	}
	
	@Persistent
	private Boolean admin;
  @Persistent
  private String alias;
	@Persistent
	private Boolean banned;
	@SuppressWarnings("unused")
	@Persistent
	private Date createDate;
	@Persistent
	private Integer dateFormat;
	@Persistent
	private String fbAuthToken;
	@Persistent
	private String firstName;
	@Persistent
	private String gender;
	@Persistent
	private String lastName;
	@Persistent
	private String locale;
	@Persistent
	private Integer measurementSystem;
	@Persistent
	private String shareCardio;
	@Persistent
	private String shareCoach;
	@Persistent
	private String shareMeasurement;
	@Persistent
	private String shareNutrition;
	@Persistent
	private String shareNutritionFoods;
	@Persistent
	private String shareTraining;
	@Persistent
	private Long timeComments;
	@Persistent
	private Integer timeFormat;
	@Persistent
	private Integer timezone;
  @Persistent
	@PrimaryKey
  private String id; //open id
	
	public UserOpenid() {
		createDate = new Date();
	}

	public UserOpenid(String id) {
		createDate = new Date();
		this.id = id;
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

	public String getNickName() {
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

	public String getEmail() {
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
  
  public String getId() {
    return id;
  }
	
	public String getUid() {
		if(id != null) {
			return id;
    }
		else {
			return "";
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

	public void setNickName(String firstName) {
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
	
	public void setEmail(String lastName) {
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
	
	public void setId(String id) {
	  this.id = id;
	}
}
