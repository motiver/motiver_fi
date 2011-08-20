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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.event.LoadingEvent;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/** Base class for presenters.
 */
public abstract class Presenter {
	
	private List<HandlerRegistration> listHandlers = new ArrayList<HandlerRegistration>();
	private List<Request> rpcRequests = new ArrayList<Request>();
	protected LayoutContainer container = null;
	
	protected SimpleEventBus eventBus;
	protected MyServiceAsync rpcService;
	
	/**
	 * Constructor for Presenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 */
	public Presenter(MyServiceAsync rpcService, SimpleEventBus eventBus) {
//		System.out.println("Presenter: " + this.getClass().getName());
		
		this.rpcService = rpcService;
		this.eventBus = eventBus;
	}
	/**
	 * Adds handler to event bus. Handlers are removed when presenter is stopped
	 * @param type
	 * @param handler
	 * @return HandlerRegistration
	 */
	public <H extends EventHandler> HandlerRegistration addEventHandler(Type<H> type, H handler) {
		final HandlerRegistration ret = eventBus.addHandler(type, handler);
		listHandlers.add(ret);
		return ret;
	}
	/**
	 * Fires event to event bus
	 * @param event
	 */
	public final void fireEvent(GwtEvent<?> event) {
		eventBus.fireEventFromSource(event, this);
	}
	/**
	 * Returns view for this presenter	
	 * @return view */
	public abstract Display getView();
	/**
	 * Hides presenter. For example when switching through tabs
	 * <br>Calls display's onStop() method
	 */
	public final void hide() {
		//stop display (closes popups etc...)
		if(getView() != null) {
      getView().onStop();
    }
		
		setVisible(false);

//		System.out.println("Presenter: " + this.getClass().toString() + " onHide()");
		onHide();
	}
	

	/**
	 * Calls displays highlight function
	 */
	public final void highlight() {
		if(getView() != null) {
      getView().highlight();
    }
	}

	/**
	 * Called before presenter is runned. Can be used for attaching handler etc.
	 */
	public void onBind() {
	  
	}
	
	/**
	 * Called after presenter is hidden.
	 */
	public void onHide() {
		
	}
	
	/**
	 * Called when presenter is run for the second time. Sub-class can just refresh the presenter
	 */
	public void onRefresh() {
	  
	}
	
	/**
	 * Called when presenter is run. This is called ONCE after presenter is added to container.
	 */
	public void onRun() {
	  
	}
	
	/**
	 * Called when presenter is stopped. Presenter's container is removed after this
	 */
	public void onStop() {
	  
	}
	
	/**
	 * refresh the presenter (and display) and calls run
	 */
	public final void refresh() {
		
		onStop();
		onBind();

		if(container != null) {
			container.removeAll();
			
			if(getView() != null) {
        container.add(getView().asWidget());
      }
			container.layout();
		}
		setVisible(true);

//		System.out.println("Presenter: " + this.getClass().getName() + " onBind() & onRun()");
    onRun();
	}
	
	/**
	 * Removes presenter from parent container. Does NOT clear display!
	 */
	public final void remove() {
		container.removeFromParent();
	}
	
	public final void run(LayoutContainer containerParent) {
		run(containerParent, true);
	}

	/**
	 * Called when presenter is run. It adds presenter to given container and calls onRun
	 * @param containerParent LayoutContainer
	 * @param doLayout boolean
	 */
	public final void run(LayoutContainer containerParent, boolean doLayout) {
		
		//if parent is null -> cancel
		if(containerParent == null) {
      return;
    }
		
		onBind();
		
		//if container not set -> run for the first time
		if(container == null) {			
			container = new LayoutContainer();
			if(getView() != null) {
			  
			  final Widget p = getView().asWidget();
			  
			  //if left menu enabled
			  if(getView().panelMenuLeft != null) {
			    container.add(getView().panelMenuLeft);
			    
			    ((LayoutContainer)p).setStyleAttribute("marginLeft", "160px");
			  }
			  
        container.add(p);
      }

			containerParent.add(container);
			if(doLayout) {
        containerParent.layout();
      }

			setVisible(true);
			if(getView() != null && !getView().isEnabled()) {
        getView().setContentEnabled(true);
      }

//			System.out.println("Presenter: " + this.getClass().getName() + " onBind() & onRun()");
      onRun();
		    
		}
		//container already set -> just refresh
		else {
			
			setVisible(true);
			if(getView() != null) {
        getView().setContentEnabled(true);
      }
			
//			System.out.println("Presenter: " + this.getClass().getName() + " onBind() & onRefresh()");
			onRefresh();
		}
	}
	/**
	 * Called when presenter is stopped
	 * <br>We can remove presenter from parent
	 */
	public final void stop() {
		
		//cancel RPC calls
		for(Request request : rpcRequests) {
			if(request != null && request.isPending()) {
				request.cancel();
				
				//cancel loading event
				eventBus.fireEvent(new LoadingEvent(null));
			}
		}
		
		if(getView() != null) {
			getView().onStop();
		}
		
		//remove event handlers
		for(int i=0; i < listHandlers.size(); i++) {
			if(listHandlers.get(i) != null) {
        listHandlers.get(i).removeHandler();
      }
		}
		listHandlers.clear();
		
//		System.out.println("Presenter: " + this.getClass().getName() + " onStop()");
		onStop();
		
		if(container != null) {
			container.removeFromParent();
		}
		container = null;
	}
	
	/**
	 * Updates current container to given parent.
	 * <br>Calls run if container is null
	 * @param containerParent
	 */
	public final void update(LayoutContainer containerParent) {
		if(container != null) {
			containerParent.add(container);
			containerParent.layout();
		}
		else {
      run(containerParent);
    }
	}
		
	/**
	 * Shows / hides presenter
	 * @param visible boolean
	 */
	private void setVisible(boolean visible) {
		if(container != null && container.isVisible() != visible) {
			container.setVisible(visible);
      if(container.isVisible()) {
        container.layout(true);
      }
		}
	}
	
	/**
	 * Adds rpc request to array. These requests are cancelled when presenters stops
	 * @param request
	 */
	public final void addRequest(Request request) {
		rpcRequests.add(request);
	}
}
