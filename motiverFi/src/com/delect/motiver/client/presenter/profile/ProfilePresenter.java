/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.UserModel;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * 
 * Measurement page
 *  - measurements (targets & graph)
 */
public class ProfilePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ProfileDisplay extends Display {
		public abstract void setHandler(ProfileHandler profileHandler);
		public abstract void setUserModel(UserModel user);
		public abstract void showAliasTaken(boolean taken);
	}
	public interface ProfileHandler {
		void saveData(UserModel user);
	}

	private ProfileDisplay display;

	/**
	 * Shows profile (from AppController.User)
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public ProfilePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ProfileDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	
	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
		
		display.setUserModel(AppController.User);
		display.setHandler(new ProfileHandler() {
      @Override
			public void saveData(UserModel user) {
				AppController.User = user;
				//save alias
				final String alias = user.getAlias();
				rpcService.saveUserData(user, new MyAsyncCallback<UserModel>() {
          @Override
          public void onSuccess(UserModel result) {
            if(result != null) {
              System.out.println(alias +" "+result.getAlias() );
              //if alias not changed -> already in use/invalid
              display.showAliasTaken( !alias.equals(result.getAlias()) );
            }
          }
				});
			}
		});
		
	}

	@Override
	public void onRun() {
	}

}
