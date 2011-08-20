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
package com.delect.motiver.client.view.widget;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.widget.Text;

public class MyButton extends Text {

  public enum Style {
    GREEN,
    RED,
    DEFAULT
  }
	
	public MyButton() {
		this.setStyleName("btn");
		setScale(ButtonScale.SMALL);
	}
	
	public MyButton(String text) {
		this();
		setText(text);
	}
	
	public void setColor(Style style) {
		
		if(style == Style.RED) {
			this.addStyleName("btn_red");
    }
		else if(style == Style.GREEN) {
			this.addStyleName("btn_green");
    }
		
	}
	
	/**
	 * Sets button size
	 * @param scale
	 */
	public void setScale(ButtonScale scale) {
		
		if(scale == ButtonScale.MEDIUM) {
			this.addStyleName("btn_medium");
			this.setHeight(24);
		}
		else {
			this.addStyleName("btn_small");
			this.setHeight(20);
		}
	}
}
