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

import com.delect.motiver.client.presenter.PopupPresenter.PopupHandler;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.widget.PopupSize;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Shows error message
 * @author Antti
 *
 */
public class PopupPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class PopupDisplay extends Display {
		/**
		 * Sets handler for view to call.
		 * @param handler PopupHandler
		 */
		public abstract void setHandler(PopupHandler handler);
		public abstract void setSize(PopupSize size);
	}
	
	/** Handler for view to call.
	 */
	public interface PopupHandler {
		/**
		 * Called when popup is closed
		 */
		void onClose();
		void onResize();
	}
	
	private PopupDisplay display;
	private Presenter presenter;
  private PopupHandler handler;
  private PopupSize size;
	
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public PopupPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, PopupDisplay display, Presenter presenter, PopupSize size) {
		super(rpcService, eventBus);
		this.display = display;

    this.presenter = presenter;
    this.size = size;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

	  display.setSize(size);
		display.setHandler(new PopupHandler() {
			@Override
			public void onClose() {
				stop();
			}

      @Override
      public void onResize() {
        presenter.refresh();
      }
		});
	}


  @Override
  public void onRun() {
    if(presenter != null)
      presenter.run(display.getBaseContainer());
  }

}
