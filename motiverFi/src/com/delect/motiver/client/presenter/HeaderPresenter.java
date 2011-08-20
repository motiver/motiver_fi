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
import com.delect.motiver.client.event.BlogShowEvent;
import com.delect.motiver.client.event.LoggedOutEvent;
import com.delect.motiver.client.event.TabEvent;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

/**
 * Wraps tabs menu view
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>onTabChanged</b> : TabEvent()<br>
 * <div>Fires after tab changes</div>
 * <ul>
 * <li>indexNew : new tab index</li>
 * <li>indexOld : old tab index</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 */
public class HeaderPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class HeaderDisplay extends Display {
		/**
		 * Sets handler for view to call.
		 * @param handler HeaderHandler
		 */
		public abstract void setHandler(HeaderHandler handler);
		/**
		 * Sets current tab.
		 * @param tabIndex int
		 */
		public abstract void setTab(int tabIndex);
		/**
		 * Sets target where header is shown.
		 * @param target HeaderTarget
		 */
		public abstract void setTarget(HeaderTarget target);
    /**
     * Shows/hides loading dialog.
     * @param enabled boolean
     */
    public abstract void showLoadingDialog(boolean enabled);
	}
	/** Handler for this presenter.
	 */
	public interface HeaderHandler {
		/**
		 * Called when logo is clicked.
		 */
		public void logoClicked();
		/**
		 * Called when user logs out.
		 */
		public void logOut();
		/**
		 * Called when logo is clicked.
		 */
		public void onLogoClicked();
		/**
		 * Called when tab is selected.
		 * @param index int
		 */
		public void onTabClick(int index);
		/**
		 * Called when user want's to show comments.
		 */
		public void showComments();
		/**
		 * Called when user want's to view own blog.
		 */
		public void viewBlog();
	}	
	public enum HeaderTarget {
		BLOG,
		USER
	}
	private HeaderDisplay display;
	
	private int tabIndex = 0;
	private HeaderTarget target;
	
	/**
	 * Constructor for HeaderPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display HeaderDisplay
	 * @param target HeaderTarget
	 * @param tabIndex int
	 */
	public HeaderPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, HeaderDisplay display, HeaderTarget target, int tabIndex) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.target = target;
		this.tabIndex = tabIndex;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		//refresh view
		display.setTab(tabIndex);
		display.setTarget(target);
		
		display.setHandler(new HeaderHandler() {

			@Override
			public void logoClicked() {
				//if user -> show main page
				if(target == HeaderTarget.USER) {
				  onTabClick(0);
				}
				//blog -> show main page
				else if(target == HeaderTarget.BLOG) {
				  onTabClick(0);
				}
			}

			@Override
			public void onLogoClicked() {
				//force first tab
				tabIndex = -1;
				onTabClick(0);
			}

			@Override
			public void onTabClick(int index) {
				if(index != tabIndex) {
					tabIndex = index;
					
					//refresh view
					display.setTab(tabIndex);
					
					//fire tab changed event
					fireEvent(new TabEvent(index));
				}
			}
			@Override
			public void showComments() {}

			@Override
			public void viewBlog() {
				fireEvent(new BlogShowEvent(AppController.User));
			}

      @Override
      public void logOut() {
        //fire event
        fireEvent(new LoggedOutEvent());
      }
		});
	}
	
	@Override
	public void onRun() {
	}

	/**
	 * Sets current tab.
	 * @param index int
	 */
	public void setTab(int index) {
		if(index != tabIndex) {
			tabIndex = index;
			
			//refresh view
			display.setTab(tabIndex);
		}
	}
  
}
