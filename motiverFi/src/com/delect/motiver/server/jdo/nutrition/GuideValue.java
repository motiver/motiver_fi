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
package com.delect.motiver.server.jdo.nutrition;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.GuideValueModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GuideValue {

	/**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static GuideValueModel getClientModel(GuideValue model) {
		if(model == null) {
			return null;
    }

		GuideValueModel modelClient = new GuideValueModel(model.getId(), model.getName());
		modelClient.setDates(model.getDateStart(), model.getDateEnd());
		//training
		modelClient.setEnergy(true, model.getEnergy(true));
		modelClient.setProtein(true, model.getProtein(true));
		modelClient.setCarb(true, model.getCarb(true));
		modelClient.setFet(true, model.getFet(true));
		//rest
		modelClient.setEnergy(false, model.getEnergy(false));
		modelClient.setProtein(false, model.getProtein(false));
		modelClient.setCarb(false, model.getCarb(false));
		modelClient.setFet(false, model.getFet(false));
		modelClient.setPercent(model.isPercent());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static GuideValue getServerModel(GuideValueModel model) {
		if(model == null) {
			return null;
    }
		
		GuideValue modelServer = new GuideValue(model.getName());
		modelServer.setDates(model.getDateStart(), model.getDateEnd());
		modelServer.setId(model.getId());
		//training
		modelServer.setEnergy(true, model.getEnergy(true));
		modelServer.setProtein(true, model.getProtein(true));
		modelServer.setCarb(true, model.getCarbs(true));
		modelServer.setFet(true, model.getFet(true));
		//rest
		modelServer.setEnergy(false, model.getEnergy(false));
		modelServer.setProtein(false, model.getProtein(false));
		modelServer.setCarb(false, model.getCarbs(false));
		modelServer.setFet(false, model.getFet(false));
		
		return modelServer;
	}
	
	@Persistent
	public Long uid;
  
  @Persistent
  public String openId;

	@Persistent
	private Double carbRest;

	@Persistent
	private Double carbTraining;

	@Persistent
	private Date dateEnd;

	@Persistent
	private Date dateStart;

	@Persistent
	private Double energyRest;

	@Persistent
	private Double energyTraining;

	@Persistent
	private Double fetRest;

	@Persistent
	private Double fetTraining;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	/**
	 * Are values grams (false) or percent (true)
	 */
	@Persistent
	private boolean isPercent = true;

	@Persistent
	private String name;

	@Persistent
	private Double proteinRest;

	@Persistent
	private Double proteinTraining;

	public GuideValue() {
	  
	}
	
	public GuideValue(String name) {
		setName(name);
	}

	public Double getCarb(boolean training) {
    if(training) {
      return carbTraining;
    }
    else {
      return carbRest;
    }
  }

	public Date getDateEnd() {
		return dateEnd;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public Double getEnergy(boolean training) {
    if(training) {
      return energyTraining;
    }
    else {
      return energyRest;
    }
  }

	public Double getFet(boolean training) {
    if(training) {
      return fetTraining;
    }
    else {
      return fetRest;
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

	public Key getKey() {
		return id;
	}

	public String getName() {
    return name;
  }

	public Double getProtein(boolean training) {
    if(training) {
      return proteinTraining;
    }
    else {
      return proteinRest;
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

	public Boolean isPercent() {
		return isPercent;
	}

	public void setCarb(boolean training, Double carb) {
		if(training) {
			carbTraining = carb;
    }
		else {
			carbRest = carb;
    }
  }

	public void setDates(Date dateStart, Date dateEnd) {
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}

	public void setEnergy(boolean training, Double energy) {
		if(training) {
			energyTraining = energy;
    }
		else {
			energyRest = energy;
    }
  }

	public void setFet(boolean training, Double fet) {
		if(training) {
			fetTraining = fet;
    }
		else {
			fetRest = fet;
    }
  }

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(GuideValue.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setName(String name) {
		this.name = name;
  }

	public void setPercent(boolean isPercent) {
		this.isPercent = isPercent;
	}

	public void setProtein(boolean training, Double protein) {
		if(training) {
			proteinTraining = protein;
    }
		else {
			proteinRest = protein;
    }
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
