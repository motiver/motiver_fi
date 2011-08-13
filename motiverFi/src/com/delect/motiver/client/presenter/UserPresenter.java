/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.event.UserRemovedEvent;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.UserModel;

/**
 * Shows single user
 * @author Antti
 *
 */
public class UserPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class UserDisplay extends Display {
		/**
		 * Sets handler for view to call.
		 * @param handler UserHandler
		 */
		public abstract void setHandler(UserHandler handler);
		/**
		 * Sets user.
		 * @param uid long
		 */
		public abstract void setModel(UserModel user);
		/**
		 * Sets if small picture is shown.
		 * @param smallPicture boolean
		 */
		public abstract void setSmallPicture(boolean smallPicture);
		/**
		 * Shows delete icon. Calls onDelete method from handler
		 */
		public abstract void showDeleteIcon();
	}
	/** Handler for this presenter.
	 */
	public interface UserHandler {
		/**
		 * Called when user is clicked.
		 */
		void onClick();
		/**
		 * Called when delete icon is clicked.
		 */
		void onDelete();
	}
	private UserDisplay display = null;
	
	private boolean smallPicture;
	private UserModel user;
		
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param user UserModel
	 * @param smallPicture boolean
	 */
	public UserPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, UserDisplay display, UserModel user, boolean smallPicture) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.user = user;
    this.smallPicture = smallPicture;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setModel(user);
		display.setSmallPicture(smallPicture);
		display.setHandler(new UserHandler() {
			@Override
			public void onClick() {
				//fire event
				eventBus.fireEvent(new UserSelectedEvent(user));
			}

      @Override
      public void onDelete() {
        //fire event
        fireEvent(new UserRemovedEvent(user));
        
        stop();
      }
			
		});
	}

	@Override
	public void onRun() {
	}
	
	/**
	 * Sets handler for view.
	 * @param handler UserHandler
	 */
	public void setClickHandler(UserHandler handler) {
		display.setHandler(handler);
	}
	
	/**
	 * Shows delete icon
	 */
	public void showDeleteIcon() {
	  display.showDeleteIcon();
	}

}
