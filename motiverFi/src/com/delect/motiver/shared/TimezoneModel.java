/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class TimezoneModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 853005417873655753L;
	public TimezoneModel(int id, String name) {
		set("id", id);
		set("name", name);
	}

	public String getId() {
    return get("id");
  }
	public String getName() {
    return get("name");
  }
}
