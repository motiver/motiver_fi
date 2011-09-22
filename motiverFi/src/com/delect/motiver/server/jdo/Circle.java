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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Circle {

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

	public Circle(Integer target, String uid, String friendId) {
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
