/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
