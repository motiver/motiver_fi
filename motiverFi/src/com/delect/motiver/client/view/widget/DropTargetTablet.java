package com.delect.motiver.client.view.widget;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DropTargetTablet {
  
  public Component component;
  DNDTabletEvent e = new DNDTabletEvent();
  public Widget widgetCopy;
  
  public DropTargetTablet(Component c) {
  
  }
  
  protected void onTouchEnter(DNDTabletEvent event) { 
    
  }
  
  protected void onTouchLeave(DNDTabletEvent event) { 
    
  }
}
