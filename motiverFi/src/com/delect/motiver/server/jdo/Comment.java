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
import com.google.appengine.api.datastore.KeyFactory;

import com.delect.motiver.shared.CommentModel;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Comment {
	
  /**
	 * Converts server object to client side object
	 * @param model : server side model
	 * @return Client side model
	 */
	public static CommentModel getClientModel(Comment model) {
		if(model == null) {
			return null;
    }

		CommentModel modelClient = new CommentModel();
		modelClient.setId(model.getId());
		modelClient.setDate(model.getDate());
		modelClient.setText(model.getText());
		modelClient.setUidTarget(model.getUidTarget());
		modelClient.setUid(model.getUid());
		return modelClient;
	}

  /**
	 * Converts client object to server side object
	 * @param model : client side model
	 * @return Server side model
	 */
	public static Comment getServerModel(CommentModel model) {
		if(model == null) {
			return null;
    }

		Comment modelServer = new Comment( );
		modelServer.setId(model.getId());
		modelServer.setDate(model.getDate());
		modelServer.setText(model.getText());
		modelServer.setUidTarget(model.getUidTarget());
		modelServer.setUid(model.getUid());
		modelServer.setTarget(model.getTarget());

		return modelServer;
	}

  @Persistent
  private Date date;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key id;

  @Persistent
  private String target;

  @Persistent
  private String text;

  @Persistent
  private Long uid;
  
  @Persistent
  public String openId;

	@Persistent
  private Long uidTarget;

  @Persistent
  private String idTarget;

	public Comment() {
    
  }

	public Date getDate() {
		return date;
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

	public String getTarget() {
		if(target != null) {
			return target;
    }
		else {
			return "";
    }
  }

	public String getText() {
		if(text != null) {
			return text;
    }
		else {
			return "";
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

	public String getUidTarget() {
		if(idTarget != null) {
			return idTarget;
    }
		else {
			return "";
    }
  }

	public void setDate(Date date) {
		this.date = date;
  }

	public void setId(Long id) {
		Key k = null;
		
		if(id != null && id != 0) {
      k = KeyFactory.createKey(Comment.class.getSimpleName(), id);
    }
		this.id = k;
	}

	public void setTarget(String target) {
		this.target = target;
  }

	public void setText(String text) {
		this.text = text;
  }

	public void setUid(String openId) {
		this.openId = openId;
  }
	
	public void setUidTarget(String idTarget) {
		this.idTarget = idTarget;
  } 

  public Long getUidOld() {
    return uid;
  } 
}
