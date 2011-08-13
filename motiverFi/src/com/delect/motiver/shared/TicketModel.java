/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class TicketModel extends BaseModelData implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -23357348L;
	
	

	public String getDesc() {
		if(get("d") != null) {
			return get("d");
    }
		else {
			return "";
    }
  }
	public long getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return 0L;
    }
  }
	public int getPriority() {
		if(get("p") != null) {
			return get("p");
    }
		else {
			return 0;
    }
  }
	public String getTitle() {
		if(get("t") != null) {
			return get("t");
    }
		else {
			return "";
    }
  }
	public String getUid() {
		if(get("uid") != null) {
			return get("uid");
    }
		else {
			return "";
    }
  }

	public void setDesc(String d) {
		set("d", d);
	}
	public void setId(long id) {
		set("id", id);
	}
	public void setPriority(int p) {
		set("p", p);
	}
	public void setTitle(String t) {
		set("t", t);
	}
	public void setUid(String uid) {
		set("uid", uid);
	}
}
