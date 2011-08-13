/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.shared;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class CountryModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4718008091025515243L;
	public CountryModel(String code, String name) {
		set("code", code);
		set("name", name);
	}

	public String getCode() {
    return get("code");
  }
	public String getName() {
    return get("name");
  }
}
