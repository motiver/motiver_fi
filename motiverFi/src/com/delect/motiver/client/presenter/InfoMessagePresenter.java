/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Shows error message
 * @author Antti
 *
 */
public class InfoMessagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class InfoMessageDisplay extends Display {
		/**
		 * Sets message's color.
		 * @param color MessageColor
		 */
		public abstract void setColor(MessageColor color);
		/**
		 * Sets handler for view to call.
		 * @param handler InfoMessageHandler
		 */
		public abstract void setHandler(InfoMessageHandler handler);
		/**
		 * Sets info message.
		 * @param message String
		 */
		public abstract void setMessage(String message);
	}
	
	/** Handler for view to call.
	 */
	public interface InfoMessageHandler {
		/**
		 * Called when message is clicked.
		 */
		void onClick();
		/**
		 * Called when message is closed.
		 */
		void onClose();
	}
	public enum MessageColor {
		COLOR_BLUE,
		COLOR_DEFAULT,
		COLOR_RED
	}
	
	private Listener<BaseEvent> clickListener;
	
	private MessageColor color;
	private InfoMessageDisplay display;
	private String message;
	
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param color MessageColor
	 * @param message String
	 * @param clickListener Listener<BaseEvent>
	 */
	public InfoMessagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, InfoMessageDisplay display, MessageColor color, String message, Listener<BaseEvent> clickListener) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.color = color;
    this.message = message;
    this.clickListener = clickListener;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		display.setColor(color);
		display.setMessage(message);
		display.setHandler(new InfoMessageHandler() {
			@Override
			public void onClick() {
				stop();
				
				if(clickListener != null) {
          clickListener.handleEvent(null);
		    }
			}
			@Override
			public void onClose() {
				stop();
			}
		});
	}

}
