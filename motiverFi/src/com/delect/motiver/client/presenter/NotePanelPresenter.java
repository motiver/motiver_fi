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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/** Notepanel with header (text) and body.
 */
public class NotePanelPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class NotePanelDisplay extends Display {
		/**
		 * Adds header button.
		 * @param text String
		 * @param listener Listener<BaseEvent>
		 */
		public abstract void addHeaderButton(String text, Listener<BaseEvent> listener);
		/**
		 * Returns container for body.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getBodyContainer();
		/**
		 * Sets handler for view to call.
		 * @param handler NotePanelHandler
		 */
		public abstract void setHandler(NotePanelHandler handler);
		/**
		 * Shows content (expands note panel).
		 */
		public abstract void showContent();
	}
	/** Handler for this presenter.
	 */
	public interface NotePanelHandler {
		/**
		 * Called when panel is collapsed.
		 */
		void contentHidden();
		/**
		 * Called when panel is expanded.
		 */
		void contentVisible();
	}
	private NotePanelDisplay display;

	//child presenters	
	private List<Presenter> presenters = new ArrayList<Presenter>();

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public NotePanelPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NotePanelDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	
	/**
	 * Adds button to title panel
	 * @param text
	 * @param listener
	 */
	public void addHeaderButton(String text, Listener<BaseEvent> listener) {
		display.addHeaderButton(text, listener);
	}
	  
	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	public void addNewPresenter(Presenter presenter) {
	  presenter.setParent(this);
		presenters.add(presenter);
	}
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		
		display.setHandler(new NotePanelHandler() {
			@Override
			public void contentHidden() {
				//stop presenters
				for(Presenter p : presenters) 
        if(p != null) {
          p.hide();
        }
			}
			@Override
			public void contentVisible() {
			  //show all presenters
		    for(Presenter p : presenters) {
		      if(p != null) {
		        p.run(display.getBodyContainer());
		      }
		    }
			}
		});
	}

	@Override
	public void show() {
	  display.showContent();
  }

  @Override
	public void onRefresh() {
		//refresh all presenters
		for(Presenter p : presenters) {
		  if(p != null) {
		    p.run(display.getBodyContainer());
		  }
		}
	}
	
	@Override
	public void onStop() {
		//stop presenters
		unbindPresenters();
	}
	
	/**
	 * Sets notepanel title
	 * @param title
	 */
	public void setTitle(String title) {
		display.setTitle(title);
	}
	
	/**
	 * Shows note panel's content
	 */
	public void showContent() {
		display.showContent();
	}
	
	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {
		if(presenters != null) {
			for(Presenter p : presenters) {
			  if(p != null) {
			    p.stop();
        }
			}
			presenters = new ArrayList<Presenter>();					
		}
	}

}
