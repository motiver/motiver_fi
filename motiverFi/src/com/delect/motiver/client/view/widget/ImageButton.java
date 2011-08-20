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
