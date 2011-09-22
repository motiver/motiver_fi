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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.delect.motiver.server.jdo.MicroNutrient;
import com.delect.motiver.shared.FoodNameModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FoodName implements Serializable, Comparable<FoodName> {
	
	/**
   * 
   */
  private static final long serialVersionUID = 5611583796576301215L;

  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static FoodNameModel getClientModel(FoodName model) {
		if(model == null) {
			return null;
    }

		FoodNameModel modelClient = new FoodNameModel(model.getId(), model.getName());
		modelClient.setEnergy(model.getEnergy());
		modelClient.setProtein(model.getProtein());
		modelClient.setCarb(model.getCarb());
		modelClient.setFet(model.getFet());
		modelClient.setPortion(model.getPortion());
		modelClient.setLocale(model.getLocale());
		modelClient.setTrusted(model.getTrusted());
		modelClient.setUid(model.getUid());
		
		return modelClient;
	}

	/**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static FoodName getServerModel(FoodNameModel model) {
		if(model == null) {
			return null;
    }
		
		FoodName modelServer = new FoodName(model.getName());
		modelServer.setId(model.getId());
		modelServer.setEnergy(model.getEnergy());
		modelServer.setProtein(model.getProtein());
		modelServer.setCarb(model.getCarb());
		modelServer.setFet(model.getFet());
		modelServer.setPortion(model.getPortion());
		modelServer.setLocale(model.getLocale());
		modelServer.setTrusted(model.getTrusted());
		
		return modelServer;
	}
  
  public Integer countQuery;
  public Integer countUse;
	
	/**
	 * If food name is trusted
	 * @param trusted : 0=not trusted, 1=verified, 100=motiver
	 */
	@Persistent(defaultFetchGroup="false")
	public Integer trusted;

	@Persistent(defaultFetchGroup="false")
	public Long uid;
  
  @Persistent(defaultFetchGroup="false")
  public String openId;

	@Persistent(defaultFetchGroup="false")
	private String barcode;

	@Persistent(defaultFetchGroup="false")
	private Double carb;

	@Persistent(defaultFetchGroup="false")
	private Integer category;

	@Persistent(defaultFetchGroup="false")
	private Double energy = 0D;

	@Persistent(defaultFetchGroup="false")
	private Double fet;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent(defaultFetchGroup="false")
	private String locale;

	@Persistent(mappedBy = "foodname", defaultFetchGroup="false")
  private List<MicroNutrient> micronutrients = new ArrayList<MicroNutrient>();

	@Persistent
	private String name = "";

	@Persistent(defaultFetchGroup="false")
	private Double portion;

	@Persistent(defaultFetchGroup="false")
	private Double price; //in euros

	@Persistent(defaultFetchGroup="false")
	private Double protein;
	
	public FoodName(String name) {
		this.name = name;
	}
  
  @Override
  public int compareTo(FoodName compare) {
    int count = getCountQuery();
    int count2 = compare.getCountQuery();
    
    //if equal count -> compare also use count
    if(count == count2) {
      return compare.getCountUse() - getCountUse();
    }
    else {
      return count2 - count;
    }
  }

  /**
   * Count value based on how much exercise name have been used by user
   * @return
   */
  public int getCountUse() {
    if(countUse != null) {
      return countUse;
    }
    else {
      return 0;
    }
  }

  /**
   * Count value based on how name matches query word
   * @return
   */
  public int getCountQuery() {
    if(countQuery != null) {
      return countQuery;
    }
    else {
      return 0;
    }
  }

	public String getBarcode() {
		if(barcode != null) {
			return barcode;
    }
		else {
			return "";
    }
  }

	public Double getCarb() {
    return carb;
  }

	public Integer getCategory() {
		if(category != null) {
			return category;
    }
		else {
			return 0;
    }
  }

	public Double getEnergy() {
    return energy;
  }

	public Double getFet() {
    return fet;
  }

	public Long getId() {
		if(id != null) {
			return id;
    }
		else {
			return 0L;
    }
  }

	public String getLocale() {
		if(locale != null) {
			return locale;
    }
		else {
			return "";
    }
  }

	public List<MicroNutrient> getMicroNutrients() {
		if(micronutrients != null) {
			return micronutrients;
    }
		else {
			return new ArrayList<MicroNutrient>();
    }
	}

	public String getName() {
    return name;
  }

	public Double getPortion() {
    return portion;
  }

	public Double getPrice() {
    return price;
  }

	public Double getProtein() {
    return protein;
  }

	/**
	 * If food name is trusted
	 * @return 0=not trusted, 1=verified, 100=motiver
	 */
	public Integer getTrusted() {
		//motiver
		if(getUid().equals("224787470868700")) {
			return 100;
    }
		//valio
		else if(getUid().equals("493464655570")) {
			return 100;
    }
		//return variable
		else {
			if(trusted != null) {
				return trusted;
      }
			else {
				return 0;
      }
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
	
  public void setCount(int countQuery, int countUse) {
    this.countUse = countUse;
    this.countQuery = countQuery;
  }

	public void setBarcode(String barcode) {
		this.barcode = barcode;
  }

	public void setCarb(Double carb) {
		this.carb = carb;
  }

	public void setCategory(Integer category) {
		this.category = category;
  }

	public void setEnergy(Double energy) {
		this.energy = energy;
  }

	public void setFet(Double fet) {
		this.fet = fet;
  }

	public void setId(Long id) {
		
		if(id != null && id != 0) {
      this.id = id;
      return;
    }
		
		this.id = null;
	}

	public void setLocale(String locale) {
		this.locale = locale;
  }

	public void setMicronutrients(List<MicroNutrient> micronutrients) {
		this.micronutrients = micronutrients;
	}

	public void setName(String name) {
		this.name = name;
  }

	public void setPortion(Double portion) {
		this.portion = portion;
  }

	public void setPrice(Double price) {
		this.price = price;
  }

	public void setProtein(Double protein) {
		this.protein = protein;
  }

	/**
	 * If food name is trusted
	 * @param trusted : 0=not trusted, 1=verified, 100=motiver
	 */
	public void setTrusted(Integer trusted) {
		this.trusted = trusted;
  }
	
	public void setUid(String openId) {
		this.openId = openId;
	} 

  public Long getUidOld() {
    return uid;
  } 
}
