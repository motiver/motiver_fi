/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.event.BlogShowEvent;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

/**
 * Shows error message
 * @author Antti
 *
 */
public class ShortcutKeysPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ShortcutKeysDisplay extends Display {
		/**
		 * Sets handler for view to call.
		 * @param handler ShortcutKeysHandler
		 */
		public abstract void setHandler(ShortcutKeysHandler handler);
		/**
		 * Shows help window.
		 */
		public abstract void showHelpWindow();
	}
	/** Handler for this presenter.
	 */
	public interface ShortcutKeysHandler {
		/**
		 * Called when Shift+key is pressed.
		 * @param keyCode int
		 */
		void onShortcutKey(int keyCode);
		/**
		 * Called when window is closed.
		 */
		void windowHidden();
	}
	
	private ShortcutKeysDisplay display;	
	
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public ShortcutKeysPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ShortcutKeysDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new ShortcutKeysHandler() {
			@Override
			public void onShortcutKey(int key) {
        switch(key) {
          case 66: //shift + B (user's blog)
            fireEvent(new BlogShowEvent(AppController.User));
            break;
          case 72: //shift + H (help window)
            display.showHelpWindow();
            break;
        }
			}
			@Override
			public void windowHidden() {
				stop();
			}
		});
	}

}
