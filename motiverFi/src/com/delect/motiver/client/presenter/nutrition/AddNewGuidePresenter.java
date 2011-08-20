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
package com.delect.motiver.client.presenter.nutrition;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.GuideValueCreatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.GuideValueModel;

/**
 * Shows window where user can add new guide value
 * @author Antti
 *
 */
public class AddNewGuidePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class AddNewGuideDisplay extends Display {
		public abstract void setHandler(AddNewGuideHandler addNewGuideHandler);
	}

	public interface AddNewGuideHandler {
		void cancel();
		void newValue(GuideValueModel model);
	}
	private AddNewGuideDisplay display;

	public AddNewGuidePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, AddNewGuideDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		//add "/add"+string to history
		History.newItem(History.getToken() + "/add", false);
		
		display.setHandler(new AddNewGuideHandler() {

			@Override
			public void cancel() {
				
				//remove add-string from token
				String token = History.getToken();
				if(token.length() > 4) {
					if(token.substring(token.length() - 4, token.length()).equals("/add")) {
						token = token.substring(0, token.length() - 4);
						History.newItem(token, false);	
					}
				}
				
				stop();
			}
			@Override
			public void newValue(GuideValueModel model) {
				
				display.setContentEnabled(false);
				
				// TODO Create new guide value and fire add event
				final Request req = rpcService.addGuideValue(model, new MyAsyncCallback<GuideValueModel>() {
					@Override
					public void onSuccess(GuideValueModel result) {
						try {
							display.setContentEnabled(true);

							//remove add-string from token
							String token = History.getToken();
							if(token.length() > 4) {
								if(token.substring(token.length() - 4, token.length()).equals("/add")) {
									token = token.substring(0, token.length() - 4);
									History.newItem(token, false);	
								}
							}
							
							stop();
							
							if(result != null) {
                eventBus.fireEvent(new GuideValueCreatedEvent(result));
					    }
							
						} catch (Exception e) {
				      Motiver.showException(e);
						}
					}
				});
				addRequest(req);
			}
		});
	}

}
