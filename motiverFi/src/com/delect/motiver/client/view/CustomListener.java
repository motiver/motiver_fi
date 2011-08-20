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

import com.google.gwt.user.client.Timer;

import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;

public interface CustomListener {

	public static final Listener<? extends BaseEvent> fieldMouseOut = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			((Component)be.getSource()).removeStyleName("field-mouse-over");
		}
	};
	//field mouse over
	public static final Listener<? extends BaseEvent> fieldMouseOver = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			((Component)be.getSource()).addStyleName("field-mouse-over");
		}
	};
	
	public static final Listener<? extends BaseEvent> fieldOnClicked = new Listener<BaseEvent>() {
    @SuppressWarnings("rawtypes")
    @Override
    public void handleEvent(BaseEvent be) {
      if(be.getSource() instanceof TextField) {
        TextField tf = (TextField)be.getSource();
        //if nothing selected
        if(tf.getSelectionLength() <= 1) {
          tf.selectAll();
        }
      }
    }
  };
	public static Listener<BaseEvent> listenerMouseOut = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(final BaseEvent be) {

			Timer timer = new Timer() {
				@Override
				public void run() {
				  if(be.getSource() != null && be.getSource() instanceof LayoutContainer) {
				    
				    LayoutContainer container = (LayoutContainer)be.getSource();
				    
						//turn off if highlighOff variable is true
						boolean highlightOff = container.getData("highlightOff");
						
						if(highlightOff) {
						  container.addStyleName("panel-out");
						}
				  }
				}
			};
			((Component)be.getSource()).setData("highlightOff", true);
			timer.schedule(Constants.DELAY_HIGHLIGHT_OFF);
		}
	};

	//partial transparency when mouse over
	public static Listener<BaseEvent> listenerMouseOver = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			//set timerOn data to false so timer won't turn highlighting off
			((Component)be.getSource()).setData("highlightOff", false);
			
			((LayoutContainer)be.getSource()).removeStyleName("panel-out");
		}
	};
	public static final Listener<? extends BaseEvent> panelMouseOut = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			((Component)be.getSource()).removeStyleName("panel-mouse-over");
		}
	};
	//panel mouse over
	public static final Listener<? extends BaseEvent> panelMouseOver = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {			
			((Component)be.getSource()).addStyleName("panel-mouse-over");
		}
	};
}
