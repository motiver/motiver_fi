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
package com.delect.motiver.client.view.css3;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * CSS3 Flexbox
 * @author Antti
 *
 */
public class FlexBox extends ComplexPanel implements InsertPanel.ForIsWidget {
	  
	public FlexBox() {
		setElement(DOM.createDiv());
		
		//styles
    DOM.setStyleAttribute(getElement(), "display", "box");
    DOM.setStyleAttribute(getElement(), "display", "-moz-box");
    DOM.setStyleAttribute(getElement(), "display", "-webkit-box");
    DOM.setStyleAttribute(getElement(), "BoxOrient", "horizontal");
    DOM.setStyleAttribute(getElement(), "MozBoxOrient", "horizontal");
    DOM.setStyleAttribute(getElement(), "WebkitBoxOrient", "horizontal");
    //center aligned
    DOM.setStyleAttribute(getElement(), "BoxAlign", "center");
    DOM.setStyleAttribute(getElement(), "MozBoxAlign", "center");
    DOM.setStyleAttribute(getElement(), "WebkitBoxAlign", "center");
	}

	public void add(Widget w, int flex, int rightMargin) {
	    
    Element div = DOM.createDiv();
		if(flex > 0) {
			DOM.setStyleAttribute(div, "BoxFlex", String.valueOf(flex));
			DOM.setStyleAttribute(div, "MozBoxFlex", String.valueOf(flex));
			DOM.setStyleAttribute(div, "WebkitBoxFlex", String.valueOf(flex));
		}
		if(rightMargin > 0) {
			DOM.setStyleAttribute(div, "marginRight", rightMargin + "px");
		}
    DOM.appendChild(getElement(), div);
    add(w, div);
	}

  /**
  * Convenience overload to allow {@link IsWidget} to be used directly.
  */
  public void insert(IsWidget w, int beforeIndex) {
    insert(asWidgetOrNull(w), beforeIndex);
  }
	  
  public void insert(Widget w, int beforeIndex) {
    insert(w, getElement(), beforeIndex, true);
  }
}
