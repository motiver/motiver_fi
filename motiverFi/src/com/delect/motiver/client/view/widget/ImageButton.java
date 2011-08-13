/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.widget;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class ImageButton extends LayoutContainer {
	
	public ImageButton(String title, ImageResource resource) {
		ToolTipConfig ttc = new ToolTipConfig(title);
		ttc.setShowDelay(0);
		this.setToolTip(ttc);
		this.setStyleName("btn-image");
		this.setHeight(16);
		
		Image image = new Image();
		image.setResource(resource);
		image.setHeight("16px");
		this.add(image);
	}
}
