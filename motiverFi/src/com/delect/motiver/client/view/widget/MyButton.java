/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
