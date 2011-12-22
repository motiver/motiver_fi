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
package com.delect.motiver.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.delect.motiver.client.event.LoggedInEvent;
import com.delect.motiver.client.event.LoggedOutEvent;
import com.delect.motiver.client.event.handler.LoggedInEventHandler;
import com.delect.motiver.client.event.handler.LoggedOutEventHandler;
import com.delect.motiver.client.lang.Lang;
import com.delect.motiver.client.lang.LangConstants;
import com.delect.motiver.client.presenter.IndexPresenter;
import com.delect.motiver.client.presenter.IndexPresenter.IndexDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.UserIndexPresenter;
import com.delect.motiver.client.presenter.UserIndexPresenter.UserIndexDisplay;
import com.delect.motiver.client.presenter.blog.BlogIndexPresenter;
import com.delect.motiver.client.presenter.blog.BlogIndexPresenter.BlogIndexDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.IndexView;
import com.delect.motiver.client.view.UserIndexView;
import com.delect.motiver.client.view.blog.BlogIndexView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.GXT;

public class AppController extends Presenter {

  /**
  * Abstract class for view to extend
  */
  public abstract static class AppDisplay extends Display {
    public abstract void showLoadingDialog(boolean show);
  }
  
  
  /**
	 * Global variable which is true when coach mode is true
	 * <br>When true: every call to server is checked (with COACH_MODE_UID) whether user has rights to be coach
	 */
	public static boolean COACH_MODE_ON = false;
  /**
	 * Uid which we have currently selected to show coach view
	 */
	public static String COACH_MODE_UID = null;
  
  public static String BLOG_UID = null;
  public static boolean IsGecko40 = false;
  public static boolean IsGecko50 = false;
  public static boolean IsIE9 = false;

	public static boolean IsSupportedBrowser = false;
	//languages
	public static Lang Lang = GWT.create(Lang.class);
	public static LangConstants LangConstants = GWT.create(LangConstants.class);
	
	/**
	 * Current user
	 */
	public static UserModel User = new UserModel();
	/**
	 * Last user (used when we are in coach mode)
	 */
	public static UserModel UserLast;
		
	private AppDisplay display;
	private Presenter presenter;
	
	
	public AppController(MyServiceAsync rpcService, SimpleEventBus eventBus, AppDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
		
		BLOG_UID = null;
		
    try {
      //parse uid from URL
      RegExp m = RegExp.compile("(.*)\\.motiver\\.fi");
      String uid = m.replace(Constants.URL_APP_CURR, "$1");

      if(!uid.equals("www")) {
        BLOG_UID = uid;
      }

    } catch (Exception e1) {
      BLOG_UID = null;
    }


		final String agent = GXT.getUserAgent();
    IsIE9 = !GXT.isOpera && (agent.indexOf("msie 9") != -1);
    IsGecko40 = GXT.isGecko && agent.indexOf("rv:2") != -1;
    IsGecko50 = GXT.isGecko && agent.indexOf("rv:5") != -1;
	    
    //set supported browser variable
    IsSupportedBrowser = !((GXT.isGecko && !AppController.IsGecko40) || (GXT.isIE && !AppController.IsIE9) || (GXT.isOpera));
	}
	
	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
	    
    //EVENT: logged in
    addEventHandler(LoggedInEvent.TYPE, new LoggedInEventHandler() {
			@Override
			public void onLoggedIn(LoggedInEvent event) {

        final Date now = new Date();
        long nowLong = now.getTime();
        nowLong += (1000 * 60 * 60 * 24 * 30); //14 days
        now.setTime(nowLong);
        
			  //set auth cookie for all domains
			  String cookie = Cookies.getCookie("ACSID");
			  Cookies.removeCookie("ACSID");
			  Cookies.setCookie("ACSID", cookie, now, ".motiver.fi", "/", false);
			  
				//restore url token
//				History.newItem(Cookies.getCookie(Constants.COOKIE_TOKEN), false);
				
				//save user
				User = event.getUser();
				
				if(BLOG_UID != null) {
          showBlog();
				}
				else {
          showUserIndex();
        }
			}
    });
		//EVENT: logged out
    addEventHandler(LoggedOutEvent.TYPE, new LoggedOutEventHandler() {
			@Override
			public void onLoggedOut(LoggedOutEvent event) {
			  //clear cookies
			  
			  //call logout url
			  Window.Location.replace(User.getLogoutUrl());
			}
		});
	}
	
	
	@Override
	public void onRun() {
		
		//save token to cookie
		final Date now = new Date();
		long nowLong = now.getTime();
		nowLong += (1000 * 60 * 60); //60 min
		now.setTime(nowLong);
		Cookies.setCookie(Constants.COOKIE_TOKEN, History.getToken(), now, "", "/", false);
		
		final Request req = rpcService.getUser(new MyAsyncCallback<UserModel>() {
      @Override
      public void onSuccess(UserModel result) {
        //logged int
        if(result.getUid() != null) {
          //fire logged in event
          final LoggedInEvent event = new LoggedInEvent(result);
          eventBus.fireEvent(event);
        }
        else {
          //redirect back to main page
          Window.Location.replace(Constants.URL_APP);
        }
      }
    });
    addRequest(req);
		
		//hide vertical scrollbars if screen width over LIMIT_MAX_WINDOW_WIDTH
		if(Window.getClientWidth() > Constants.LIMIT_MAX_WINDOW_WIDTH) {
      RootPanel.getBodyElement().setAttribute("style", "overflow-x:hidden");
    }
	}
	
	@Override
	public void onStop() {
		if(presenter != null) {
      presenter.stop();
    }
	} 
	

	/**
	 * Reloads page
	 */
	private native void refreshPage() /*-{ 
    $wnd.location.reload(); 
  }-*/;

	/**
	 * Shows blog
	 */
	void showBlog() {

		//unbind last presenter
		if(presenter != null) {
			presenter.stop();
    }

		display.showLoadingDialog(true);
		
    GWT.runAsync(new RunAsyncCallback() {
      public void onFailure(Throwable caught) {
        Window.alert("Failed to load app!");
      }

      public void onSuccess() {

        display.showLoadingDialog(false);
        
        presenter = new BlogIndexPresenter(rpcService, eventBus, (BlogIndexDisplay) GWT.create(BlogIndexView.class), BLOG_UID);
        presenter.run(getView().getBaseContainer());
      }
    });
		
	}
	
	/**
	 * Called when user is not logged in
	 */
	void showIndex() {

		//unbind last presenter
		if(presenter != null) {
			presenter.stop();
    }

		presenter = new IndexPresenter(rpcService, eventBus, (IndexDisplay) GWT.create(IndexView.class));
		presenter.run(getView().getBaseContainer());
		
	}

	/**
	 * Called when user is logged in
	 */
	void showUserIndex() {
		
		//check if correct locale from cookie (otherwise refresh page)
		final String locale = Cookies.getCookie("locale");
		if(locale == null || !User.getLocale().equals(locale)) {
			
			//save new locale
			final Date now = new Date();
			long nowLong = now.getTime();
			nowLong += (1000 * 60 * 60 * 24 * 365);//year
			now.setTime(nowLong);
			Cookies.setCookie("locale", User.getLocale(), now, "", "/", false);
			
			//refresh page
			refreshPage();
			
			return;
		}

		//unbind last presenter
		if(presenter != null) {
			presenter.stop();
    }

    display.showLoadingDialog(true);
    
		GWT.runAsync(new RunAsyncCallback() {
      public void onFailure(Throwable caught) {
        Window.alert("Failed to load app!!");
      }

      public void onSuccess() {

        display.showLoadingDialog(false);
        
        presenter = new UserIndexPresenter(rpcService, eventBus, (UserIndexDisplay) GWT.create(UserIndexView.class));
        presenter.run(getView().getBaseContainer());
      }
    });
		
	}
}
