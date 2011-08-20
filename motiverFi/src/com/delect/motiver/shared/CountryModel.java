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
