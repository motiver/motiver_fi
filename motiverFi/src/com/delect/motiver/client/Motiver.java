/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

import com.delect.motiver.client.AppController.AppDisplay;
import com.delect.motiver.client.OfflineRequestBuilder.OfflineRequestBuilderHandler;
import com.delect.motiver.client.event.InfoMessageEvent;
import com.delect.motiver.client.event.LoadingEvent;
import com.delect.motiver.client.event.OfflineModeEvent;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;
import com.delect.motiver.client.service.MyService;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.AppView;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Motiver implements EntryPoint {
	
	public static boolean offlineMode = false;
  SimpleEventBus eventBus = new SimpleEventBus();
  /**
   * If next RPC call is cacheable
   */
  private static boolean isCacheable = false; 
  
  MyServiceAsync rpcService;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		//hide loading text
    RootPanel panelLoading = RootPanel.get("loading");
    panelLoading.setVisible(false);
    
    //rpc service
		rpcService = GWT.create(MyService.class);
		((ServiceDefTarget) rpcService).setRpcRequestBuilder(new RpcRequestBuilder() {
			@Override
			protected RequestBuilder doCreate(String serviceEntryPoint) {
				
				//custom builder
				final OfflineRequestBuilder builder = new OfflineRequestBuilder(RequestBuilder.POST, ((ServiceDefTarget)rpcService).getServiceEntryPoint());
				builder.setCacheable(isCacheable);
				builder.setHandler(new OfflineRequestBuilderHandler() {
					@Override
					public String loadFromStorage(String requestData) {
						
					  try {
              //if we are fetching response from local storage -> we are in offline mode
              if(!offlineMode) {
                eventBus.fireEvent(new OfflineModeEvent(true));
              }
                
              offlineMode = true;
              
              //cancel loading event
              eventBus.fireEvent(new LoadingEvent(null));
                
              //get response from local storage
              final String res = OfflineStorage.getItem(requestData);
              if(res != null && res.startsWith("//OK")) {
                return res;
              }
              
            } catch (Exception e) {
              Motiver.showException(e);
            }
						
						return null;
					}
					@Override
					public void onErrorReceived(String requestData) {
						
						//cancel loading event
						eventBus.fireEvent(new LoadingEvent(null));
						
						//show error message
						eventBus.fireEvent(new InfoMessageEvent(MessageColor.COLOR_RED, AppController.Lang.NetworkError()));
						
					}
					@Override
					public void onResponseReceived(String requestData, String responseData) {

						try {
              //cancel loading event
              eventBus.fireEvent(new LoadingEvent(null));

              //save data to local storage (if OK)
              if(responseData != null && responseData.startsWith("//OK") && builder.isCacheable()) {
                OfflineStorage.setItem(requestData, responseData);
              }
              
              //set offline mode off (use variable so this event won't fire everytime
              if(offlineMode) {
                eventBus.fireEvent(new OfflineModeEvent(false));
              	offlineMode = false;
              }
              
            } catch (Exception e) {
              Motiver.showException(e);
            }
					}
				});

				isCacheable = false;
				
				//if coach mode is on -> add trainee's uid to token
//				String token = AppController.ACCESSTOKEN;
//				if(AppController.COACH_MODE_ON) {
//          token += "____" + AppController.COACH_MODE_UID;
//        }
//				
//				//save token as header
//				if(token.length() > 0) {
//				  builder.setHeader("token", token);
//				}

        //fire loading event
        eventBus.fireEvent(new LoadingEvent(AppController.Lang.Loading() + "..."));
        
				return builder;
			}
		});
		
		//launch appcontroller
    AppController appViewer = new AppController(rpcService, eventBus, (AppDisplay) GWT.create(AppView.class));
    LayoutContainer lc = new LayoutContainer();
    RootPanel.get().add(lc);
    appViewer.run(lc);
	    
    //check if local storage too big
    OfflineStorage.checkSize();
	}
	
	/**
	 * Shows exception
	 * @param exception
	 */
	public static void showException(Exception exception) {
	  exception.printStackTrace();
	}

  
  public static void setNextCallCacheable(boolean cacheable) {
    isCacheable = cacheable;
  }
}
