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
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

/**
 * Shows error message
 * @author Antti
 *
 */
public class ConfirmDialogPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ConfirmDialogDisplay extends Display {
		/**
		 * Sets handler for view to call.
		 * @param handler ConfirmDialogHandler
		 */
		public abstract void setHandler(ConfirmDialogHandler handler);
		/**
		 * Sets info message.
		 * @param message String
		 */
		public abstract void setMessage(String title, String message);
		public abstract void setDialogType(DialogType type);
	}
	
	/** Handler for view to call.
	 */
	public interface ConfirmDialogHandler {
		/**
		 * Called when message is clicked.
		 */
		void onYes();
		/**
		 * Called when message is closed.
		 */
		void onNo();
	}
	
	public enum DialogType {
	  CONFIRM,
	  ALERT
	}
	
	private ConfirmDialogDisplay display;
	private DialogType type = DialogType.CONFIRM;
  private String title;
	private String message;
  private ConfirmDialogHandler handler;
	
	/**
	 * Shows confirmation dialog
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param color MessageColor
	 * @param message String
	 * @param clickListener Listener<BaseEvent>
	 */
	public ConfirmDialogPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ConfirmDialogDisplay display, String message, ConfirmDialogHandler handler) {
		super(rpcService, eventBus);
		this.display = display;

		this.title = AppController.Lang.Confirm();
    this.message = message;
    this.handler = handler;
	}
	
  /**
   * Shows confirmation dialog
   * @param rpcService
   * @param eventBus
   * @param display
   * @param color MessageColor
   * @param message String
   * @param clickListener Listener<BaseEvent>
   */
  public ConfirmDialogPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ConfirmDialogDisplay display, String title, String message) {
    super(rpcService, eventBus);
    this.display = display;

    this.title = title;
    this.message = message;
    this.type = DialogType.ALERT;
  }
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		display.setMessage(title, message);
		display.setDialogType(type);
		display.setHandler(new ConfirmDialogHandler() {
			@Override
			public void onYes() {
				stop();
				
				if(handler != null) {
				  handler.onYes();
		    }
			}
			@Override
			public void onNo() {
				stop();
        
        if(handler != null) {
          handler.onNo();
        }
			}
		});
	}

}
