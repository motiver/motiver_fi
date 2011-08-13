/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Header;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.TextField;


public class MyHeader extends Header {
	  
	private Text labelInfo = null;
	private TextField<String> tf;
	
	@Override
	public void insertTool(Component tool, int index) {
				
		int indexNew = index;
		
		if(tool instanceof ToolButton) {
			//check if info label set
			if(labelInfo == null) {
				labelInfo = new Text();
				labelInfo.setStyleName("label-tool-info");
				super.insertTool(labelInfo, 0);
			}
			
			ToolButton tb = ((ToolButton)tool);
			tb.setData("t", tb.getTitle());
			tb.setTitle("");
			
			//add listeners			
			//reset title
			tb.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					labelInfo.setText( (String) ((ToolButton)be.getSource()).getData("t") );
				}
			});
			tb.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					labelInfo.setText("");
				}
			});
			
			if(index == 0) {
			  indexNew = 1;
      }
		}
		
		tool.setVisible(false);
		super.insertTool(tool, indexNew);
	}
	
	/**
	 * Replaces heading text with textfield
	 * <br><b>Pre-render only</b>
	 * @param tf
	 */
	public void setHeadingTextField(TextField<String> tf) {
		this.tf = tf;
	}

	@Override
  protected void onRender(Element target, int index) {
		super.onRender(target, index);
		
		if(tf != null) {

			//remove heading
			this.getElement().getChild(this.getElement().getChildCount() - 1).removeFromParent();
			
			//add text field
			tf.addStyleName("x-panel-header-text");
			tf.setStyleAttribute("margin-top", "-4px");
			//fire onclick event on textfield when header is clicked
			this.addListener(Events.OnClick, new Listener<DomEvent>() {
        public void handleEvent(DomEvent be) {
          int x = be.getClientX() - ((MyHeader)be.getSource()).getAbsoluteLeft();
          if(x < 75) {
            tf.fireEvent(Events.OnClick);
          }
			   }
			});
			this.sinkEvents(Event.ONCLICK);
			tf.render(getElement());
		}
		  
	}

}
