/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Timer;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.ConnectionErrorEvent;
import com.delect.motiver.client.event.LoggedInEvent;
import com.delect.motiver.client.event.handler.ConnectionErrorEventHandler;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.UserModel;

/**
 * Wraps login view
 *
 */
public class LoginPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class LoginDisplay extends Display {
		/**
     * Shows/hides loading dialog.
		 * @param enabled boolean
		 */
		public abstract void showLoadingDialog(boolean enabled);
	}
	private LoginDisplay display;
  private Timer timer;

	/**
	 * Constructor for LoginPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display LoginDisplay
	 */
	public LoginPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, LoginDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	  
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
	    
    //EVENT: connection error
    addEventHandler(ConnectionErrorEvent.TYPE, new ConnectionErrorEventHandler() {
			@Override
			public void onError(ConnectionErrorEvent event) {
				display.showLoadingDialog(false);
			}
    });

    //init facebook api
    exportMethods(this);
	}


	@Override
	public void onRun() {
	  checkLoginStatus();
	}
	
	@Override
	public void onStop() {
    if(timer != null) {
      timer.cancel();
    }
	}
  
  /**
   * Checks if user is logged in.
   */
  private void checkLoginStatus() {
    display.showLoadingDialog(true);
    
    //hide dialog if no response after 30s
    timer = new Timer() {
      @Override
      public void run() {
        fbLoginResponse(null);
      }
    };
    timer.schedule(Constants.DELAY_LOGIN_DIALOG);

    //check current user
    fbGetLoginStatus();
  }

  /**
   * Called when user is logged in and approved all permissions
   */
  void fbLoginResponse(String accessToken) {

    display.showLoadingDialog(true);
    
    //cancel timer
    if(timer != null) {
    timer.cancel();
    }
    
    //is logged in
    if(accessToken != null) {
      //save accesstoken
//      AppController.ACCESSTOKEN = accessToken;
      
      //save token
      final Request req = rpcService.saveToken(new MyAsyncCallback<UserModel>() {
        @Override
        public void onSuccess(UserModel user) {
          display.showLoadingDialog(false);
          
          //if logged in
          if(user != null) {
            //fire logged in event
            final LoggedInEvent event = new LoggedInEvent(user);
            eventBus.fireEvent(event);
          }
        }
      });
      addRequest(req);
    }
    //not logged in
    else {
    display.showLoadingDialog(false);
    }
  }

  
  /**
   * Decrale javascript methods
   * @param instance
   */
  private native void exportMethods(LoginPresenter instance) /*-{
    $wnd.fbLoginResponse = function(s) {
      return instance.@com.delect.motiver.client.presenter.LoginPresenter::fbLoginResponse(Ljava/lang/String;)(s);
    };
    $wnd.FB.Event.subscribe('auth.login', function(response) {
      var access_token = response.session.access_token;
      $wnd.fbLoginResponse(access_token);
    });
  }-*/;
  
  /**
   * FB.getLoginStatus()
   * @return
   */
  private native String fbGetLoginStatus() /*-{
    $wnd.FB.getLoginStatus(function(response) {
      if (response.session) {
        var access_token = response.session.access_token;
        $wnd.fbLoginResponse(access_token);
      } else {
        $wnd.fbLoginResponse(null);
      }
    }, 'true');
  }-*/;
  
}
