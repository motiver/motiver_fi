/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.profile.MeasurementsListPresenter.MeasurementsListDisplay;
import com.delect.motiver.client.presenter.profile.OldDataFetchPresenter.OldDataFetchDisplay;
import com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay;
import com.delect.motiver.client.presenter.profile.ProfilePresenter.ProfileDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.profile.MeasurementsListView;
import com.delect.motiver.client.view.profile.OldDataFetchView;
import com.delect.motiver.client.view.profile.PermissionsSelectView;
import com.delect.motiver.client.view.profile.ProfileView;
import com.delect.motiver.shared.Constants;

/**
 * 
 * Profile page
 *  - measurements (targets & graph)
 */
public class ProfilePagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ProfilePageDisplay extends Display {
	  public abstract void setHandler(ProfilePageHandler handler);
	}
	public interface ProfilePageHandler {
    /**
     * Called when menu item is clicked
     * @param index : 0=measurements, 1=profile, 2=permissions
     */
    void onMenuClicked(int index);
	}
	private ProfilePageDisplay display;

  private Presenter presenter;
  private String target = "";

	/**
	 * Measurements page
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public ProfilePagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ProfilePageDisplay display) {
		super(rpcService, eventBus);
		this.display = display;		 

		this.target = History.getToken().replace(Constants.TOKEN_PROFILE+"/", "");
		
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
    display.setHandler(new ProfilePageHandler() {
      @Override
      public void onMenuClicked(int index) {
        switch(index) {
          case 0:
            target = "measurements";
            break;
          case 1:
            target = "profile";
            break;
          case 2:
            target = "permissions";
            break;
          case 3:
            target = "fetch";
            break;
        }
        
        showTarget();
      }
    });
		
	}


	@Override
	public void onRefresh() {
		//refresh childs
		if(presenter != null) {
		  presenter.run(display.getBaseContainer());
    }
	}

	@Override
	public void onRun() {
    showTarget();
	}

  /**
   * Shows single target
   * @param target
   * @param hide : unbinds presenter when TRUE
   */
  void showTarget() {
    
    History.newItem(Constants.TOKEN_PROFILE+"/"+target, false);

    if(presenter != null) {
      presenter.stop();
    }
    
    if(target.length() == 0) {
      target = "measurements";
    }
    
    //profile
    if(target.startsWith("profile")) {
      
      display.setSelectedMenuItem(1);
      
      presenter = new ProfilePresenter(rpcService, eventBus, (ProfileDisplay)GWT.create(ProfileView.class));
    }
    //permissions
    else if(target.startsWith("permissions")) {
      
      display.setSelectedMenuItem(2);
      
      presenter = new PermissionsSelectPresenter(rpcService, eventBus, (PermissionsSelectDisplay)GWT.create(PermissionsSelectView.class));
    }
    //permissions
    else if(target.startsWith("fetch")) {
      
      display.setSelectedMenuItem(3);
      
      presenter = new OldDataFetchPresenter(rpcService, eventBus, (OldDataFetchDisplay)GWT.create(OldDataFetchView.class));
    }
    //measurements
    else {

      //check if measurement id in token
      long mid = 0;
      try {
        String token = History.getToken();
        if(token.matches(Constants.TOKEN_PROFILE+"/measurements/.*")) {
          String[] arr = token.split("/");
          final String str = arr[arr.length - 1];

          if(str.contains("m")) {
            mid = Long.parseLong(str.replace("m", ""));
          }
        }
      } catch (NumberFormatException e) {
        Motiver.showException(e);
      }
      
      display.setSelectedMenuItem(0);
      
      presenter = new MeasurementsListPresenter(rpcService, eventBus, (MeasurementsListDisplay)GWT.create(MeasurementsListView.class), mid);
    }
    
    if(presenter != null) {
      presenter.run(display.getBaseContainer());
    }
    
  }

	@Override
	public void onStop() {
    if(presenter != null) {
      presenter.stop();
    }
	}

}
