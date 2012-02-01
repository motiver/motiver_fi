package com.delect.motiver.client.view.widget;

import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.widget.Component;

public class CustomDragSource extends DragSource {

  public CustomDragSource(Component component) {
    super(component);

    draggable = new CustomDraggable(component);
    draggable.setUseProxy(true);
    draggable.setSizeProxyToSource(false);
    draggable.setMoveAfterProxyDrag(false);
    draggable.addDragListener(listener);
    draggable.setProxy(statusProxy.el());
    
  }

}
