/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.widget;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;

public class MyToggleButton extends MyButton {

	private boolean toggled = false;
	
	public MyToggleButton() {
		
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				toggled = !toggled;
				
				if(toggled) {
					addStyleName("toggled");
        }
				else {
					removeStyleName("toggled");
        }
					
			}
		});
	}

	public boolean isPressed() {
		return toggled;
	}
	
}
