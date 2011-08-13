/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.training.TrainingDayPresenter;
import com.delect.motiver.client.presenter.training.TrainingDayPresenter.TodayTrainingHandler;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Document;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class TrainingDayView extends TrainingDayPresenter.TrainingDayDisplay {
	
	private TodayTrainingHandler handler;
	private Listener<ComponentEvent> listenerShortcut = new Listener<ComponentEvent>() {
		@Override
		public void handleEvent(ComponentEvent ce) {
			//if valid key comco
			if(Functions.isValidKeyCombo(ce)) {
				//if enough time elapsed
				if(System.currentTimeMillis() - timeLastKeyEvent < Constants.DELAY_KEY_EVENTS) {
					return;
        }
				
				switch(ce.getKeyCode()) {
        //shift + W
	        		case 87:
	    				timeLastKeyEvent = System.currentTimeMillis();
              handler.newWorkout();
              ce.cancelBubble();
              break;
	        		//shift + R
	        		case 82:
	    				timeLastKeyEvent = System.currentTimeMillis();
              handler.newRoutine();
              ce.cancelBubble();
              break;
	        		//shift + C
	        		case 67:
	    				timeLastKeyEvent = System.currentTimeMillis();
              handler.newCardio();
              ce.cancelBubble();
              break;
	        		//shift + V
	        		case 86:
	    				timeLastKeyEvent = System.currentTimeMillis();
              handler.newRun();
              ce.cancelBubble();
              break;
        }
      }
		}
	};
	
	private long timeLastKeyEvent = 0;

	public TrainingDayView() {
		
		this.setLayout(new RowLayout());
		
		//listener for shift + key
		Document.get().addListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcut);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void onStop() {
		//remove key listeners
		Document.get().removeListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcut);
	}

	@Override
	public void setHandler(TodayTrainingHandler handler) {
		this.handler = handler;
	}
}
