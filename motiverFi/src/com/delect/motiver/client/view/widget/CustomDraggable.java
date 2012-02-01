package com.delect.motiver.client.view.widget;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

public class CustomDraggable extends Draggable {

  private Component handle;

  public CustomDraggable(Component dragComponent) {
    this(dragComponent, dragComponent);
  }
  
  public CustomDraggable(final Component dragComponent, final Component handle) {
    super(dragComponent, handle);
    this.handle = handle;

    handle.addHandler(new TouchStartHandler() {

      @Override
      public void onTouchStart(TouchStartEvent event) {
        Window.alert("onTouchStart");
        Event evt = DOM.eventGetCurrentEvent();
        ComponentEvent ce = new ComponentEvent(dragComponent, evt);
        onMouseDown(ce);
      }
      
    }, TouchStartEvent.getType()); 
//    
//    dragComponent.addDomHandler(new TouchEndHandler() {
//  
//      @Override
//      public void onTouchEnd(TouchEndEvent event) {
//        Window.alert("onTouchStart");
//        Event evt = DOM.eventGetCurrentEvent();
//        stopDrag(evt);
//      }
//      
//    }, TouchEndEvent.getType()); 
//    
//    dragComponent.addDomHandler(new TouchMoveHandler() {
//  
//      @Override
//      public void onTouchMove(TouchMoveEvent event) {
//        Window.alert("onTouchStart");
//        Event evt = DOM.eventGetCurrentEvent();
//        onMouseMove(evt);
//      }
//      
//    }, TouchMoveEvent.getType()); 
    handle.sinkEvents(Event.TOUCHEVENTS);
  }
}
