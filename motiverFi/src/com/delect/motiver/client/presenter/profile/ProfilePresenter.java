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
package com.delect.motiver.client.presenter.profile;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.UserModel;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

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
				final Request req = rpcService.saveUserData(user, new MyAsyncCallback<UserModel>() {
          @Override
          public void onSuccess(UserModel result) {
            if(result != null && alias != null) {
              //if alias not changed -> already in use/invalid
              display.showAliasTaken(false);
            }
          }
          @Override
          public void onFailure(Throwable throwable) {
//            if(throwable instanceof AliasTakenException) {
//              display.showAliasTaken(true);
//            }
//            else {
              super.onFailure(throwable);
//            }
          }
				});
				addRequest(req);
			}
		});
		
	}

	@Override
	public void onRun() {
	}

}
