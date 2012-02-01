package com.delect.motiver.client.view.widget;

import com.google.gwt.dom.client.Element;

public class DNDTabletEvent {

  Object data;
  Element element;
  
  public void setData(Object data) {
    this.data = data;
  }
  
  public Object getData() {
    return data;
  }
  
  public Element getElement() {
    return element;
  }
  
  public void setElement(Element element) {
    this.element = element;
  }
}
