/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.css3;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TextBox;

public class ExtendedTextBox extends TextBox {
	
	public ExtendedTextBox() {
		this.setStyleName("tb-extended");
		DOM.setElementAttribute(getElement(), "type", "text");
	}
	
	public void setEmptyText(String emptyText) {
		DOM.setElementAttribute(getElement(), "placeholder", emptyText);
	}
}
