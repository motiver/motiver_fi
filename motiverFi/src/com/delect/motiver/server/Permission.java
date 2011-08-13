/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Permission {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key id;
  
	@Persistent
	public String friendId;
  
  @Persistent
  public String openId;
  
  @Persistent
  public Integer target;
	
	@Persistent
	private Date date;

	public Permission(Integer target, String uid, String friendId) {
	  setTarget(target);
		setUid(uid);
    setFriendId(friendId);
    
    date = new Date();
	}

	public String getUid() {
		if(openId != null) {
			return openId;
    }
		else {
			return "";
    }
  }

  public String getFriendId() {
    if(friendId != null) {
      return friendId;
    }
    else {
      return "";
    }
  }

	public void setUid(String uid) {
		this.openId = uid;
	}

  public void setFriendId(String friendId) {
    this.friendId = friendId;
  }

  /**
   * Sets which permission this is
   * <br>0 = training, 1=nutrition, 2=nutrition (foods), 3=cardio, 4=measurements, 5=coach
   * @param target
   */
  public void setTarget(Integer target) {
    this.target = target;
  }
}
