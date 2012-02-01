package com.delect.motiver.client.view.widget;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DragSourceTablet {

  static String draggedId = null;
  static Map<String, DragSourceTablet> sources = new HashMap<String, DragSourceTablet>();
  static Map<String, DropTargetTablet> target = new HashMap<String, DropTargetTablet>();
  
  public Component component;
  DNDTabletEvent e = new DNDTabletEvent();
  public Widget widgetCopy;
  private String group;
  
  public DragSourceTablet(Component c) {
    component = c;
    final DragSourceTablet _this = this;
    
    component.addListener(Events.Render, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        sources.put(component.getId(), _this);
        setDraggable(component.getId());
      }
    });
  }
  
  public static native void setDraggable(String id) /*-{
    var div = $doc.getElementById(id);
    
    //touch events
    div.addEventListener('touchstart',function (e) {
      var x = e.changedTouches[0].pageX;  
      var y = e.changedTouches[0].pageY;
      @com.delect.motiver.client.view.widget.DragSourceTablet::onTouchStart(Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayInteger;)(this.id, new Array(y, x));
    },true);
  }-*/;

  public static native void attachTouchListener() /*-{
    //disable scrolling
    $doc.ontouchmove = function(e){ e.preventDefault(); }
    
    //touch move
    $doc.addEventListener('touchmove',function (e) {
      var x = e.changedTouches[0].pageX;  
      var y = e.changedTouches[0].pageY;
      @com.delect.motiver.client.view.widget.DragSourceTablet::onTouchMove(Lcom/google/gwt/core/client/JsArrayInteger;)(new Array(y, x));
    },true);
    
    $doc.addEventListener('touchend',function (e) {
      //enable scrolling
      $doc.ontouchmove = function(e){ }
      @com.delect.motiver.client.view.widget.DragSourceTablet::onTouchEnd()();
    },true);
  }-*/;
  
  static void onTouchStart(final String id, JsArrayInteger array) {
    System.out.println("onTouchStart");
    if(draggedId == null) {
      draggedId = id;
      DragSourceTablet source = sources.get(id);

      //clone component
      Element elCopy = (Element) source.component.getElement().cloneNode(true);
      source.e.setElement(elCopy);
      
      //call event handler
      source.onTouchStart(source.e);

      final Element el = (Element) source.e.getElement();
      int y = array.get(0)-el.getClientHeight();
      int x = array.get(1)-el.getClientWidth();
      el.addClassName("x-component");
      el.addClassName("x-dd-drag-proxy");
      el.addClassName("x-dd-drop-nodrop");
      el.setAttribute("style", "visibility:visible;position:absolute;top:"+y+"px;left:"+x+"px;");
      
      source.widgetCopy = new Widget() {{
        setElement(el);
      }};
      
      RootPanel.get().add(source.widgetCopy);
      
      attachTouchListener();
      
    }
  }
  
  static void onTouchMove(JsArrayInteger array) {
    System.out.println("onTouchMove");
    if(draggedId != null) {      
      DragSourceTablet source = sources.get(draggedId);
      source.onTouchMove(source.e);

      Element el = source.widgetCopy.getElement();
      int y = array.get(0)-el.getClientHeight()/2;
      int x = array.get(1)-el.getClientWidth()/2;
      
      el.setAttribute("style", "visibility:visible;position:absolute;top:"+y+"px;left:"+x+"px;");
    }
  }
  
  static void onTouchEnd() {
    System.out.println("onTouchMove");
    if(draggedId != null) {      
      DragSourceTablet source = sources.get(draggedId);
      source.onTouchMove(source.e);
      
      //remove element
      RootPanel.get().remove(source.widgetCopy);
      
      //check if dropped to something
    }
  }
  
  protected void onTouchStart(DNDTabletEvent event) { 
    
  }
  
  protected void onTouchMove(DNDTabletEvent event) { 
    
  }

  public void setGroup(String group) {
    this.group = group;
  }
}
