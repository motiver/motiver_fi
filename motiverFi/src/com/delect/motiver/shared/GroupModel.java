/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GroupModel extends BaseModelData implements IsSerializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1074120587051188635L;
	public GroupModel() {
		
	}
	public GroupModel(String id, String name) {
		set("id", id);
		set("name", name);
	}

	public String getId() {
		if(get("id") != null) {
			return get("id");
    }
		else {
			return "";
    }
  }
	public String getName() {
		if(get("name") != null) {
			return get("name");
    }
		else {
			return "";
    }
  }

	public void setId(String id) {
		set("id", id);
	}
	public void setName(String name) {
		set("name", name);
	}
}
