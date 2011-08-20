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
package com.delect.motiver.client.view;

import com.google.gwt.resources.client.ImageResource;

import com.delect.motiver.client.view.widget.ImageButton;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public abstract class SmallNotePanelDisplay extends LayoutContainer {
  public abstract void addHeaderButton(String text, Listener<BaseEvent> listener);
  public abstract ImageButton addHeaderImageButton(String text, ImageResource image, Listener<BaseEvent> listener);
  public abstract LayoutContainer getPanelData();
  public abstract void setCollapsible(boolean isCollapsible);
  public abstract void setStylePrefix(String style);
  public abstract void setTitleText(String title);
  public abstract void showContent();
}
