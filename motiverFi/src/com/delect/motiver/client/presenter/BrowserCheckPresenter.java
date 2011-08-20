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
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.event.InfoMessageEvent;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Shows error message
 * @author Antti
 *
 */
public class BrowserCheckPresenter extends Presenter {
	
	/**
	* Abstract class for view to extend
	*/
	public abstract static class BrowserCheckDisplay extends Display {
	}
	
	private BrowserCheckDisplay display = null;


	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public BrowserCheckPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, BrowserCheckDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}


	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onRun() {

		//firefox < 4.00
		if(GXT.isGecko && !AppController.IsGecko40 && !AppController.IsGecko50) {

			final Listener<BaseEvent> listener = new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					Window.open("http://www.mozilla.com", "_blank", "");
				}
			};
			final InfoMessageEvent event = new InfoMessageEvent(MessageColor.COLOR_DEFAULT, AppController.Lang.OldBrowserAlert());
			event.setClickListener(listener);
			fireEvent(event);

		}
		else if(GXT.isIE6 || GXT.isIE7) {

			final Listener<BaseEvent> listener = new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					Window.open("http://www.microsoft.com/ie", "_blank", "");
				}
			};
			final InfoMessageEvent event = new InfoMessageEvent(MessageColor.COLOR_DEFAULT, AppController.Lang.OldBrowserAlert());
			event.setClickListener(listener);
			fireEvent(event);
		}
		else if(GXT.isOpera) {
			
			final Listener<BaseEvent> listener = new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					Window.open("http://www.google.com/chrome", "_blank", "");
				}
			};
			final InfoMessageEvent event = new InfoMessageEvent(MessageColor.COLOR_DEFAULT, AppController.Lang.NotSupportedAlert());
			event.setClickListener(listener);
			fireEvent(event);
		}
		//stop presenter
		else {
      stop();
    }
	}

}
