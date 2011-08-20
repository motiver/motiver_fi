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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.FoodNameModel;

/**
 * Edit (or add if model's ID is null) food name
 *
 */
public class FoodNameEditorPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class FoodNameEditorDisplay extends Display {

		public abstract void setHandler(FoodNameEditorHandler foodNameEditorHandler);
		public abstract void setModel(FoodNameModel model);
	}

	public interface FoodNameEditorHandler {
		void editCancelled();
		void nameSaved(FoodNameModel model);
	}
	private FoodNameEditorDisplay display;

	private FoodNameEditorHandler handler;
	private FoodNameModel model;

	/**
	 * Edit (or add if model's ID is null) food name
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param model : name model
	 * @param foodId : food id if belongs to some food (0 if not)
	 */
	public FoodNameEditorPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, FoodNameEditorDisplay display, FoodNameModel model) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.model = model;
	    
    if(model == null) {
      return;
    }
	}
	  
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {	
		display.setModel(model);
		
		display.setHandler(new FoodNameEditorHandler() {

			@Override
			public void editCancelled() {
				stop();
				if(handler != null) {
          handler.editCancelled();
		    }
			}
			@Override
			public void nameSaved(final FoodNameModel model) {
				
				//if admin -> set trusted
				if(AppController.User.isAdmin()) {
          model.setTrusted(100);
		    }
				
				//set locale based on user locale
				model.setLocale(AppController.User.getLocale());
				
				//if new
				if(model.getId() == 0) {
					final Request req = rpcService.addFoodname(model, new MyAsyncCallback<FoodNameModel>() {
						@Override
						public void onSuccess(FoodNameModel result) {
							stop();
							
							if(result != null && handler != null) {
                handler.nameSaved(result);							
							}
						}
					});
					addRequest(req);
				}
				//update old value
				else {
					final Request req = rpcService.updateFoodName(model, new MyAsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							stop();
							
							//call handler
							if(handler != null) {
                handler.nameSaved(model);
					    }
						}
					});
					addRequest(req);
				}
			}
		});
	}

	@Override
	public void onRun() {
		if(model.getId() > 0) {
			display.setContentEnabled(false);
			
			//if old value -> get from server
			final Request req = rpcService.getFoodname(model.getId(), new MyAsyncCallback<FoodNameModel>() {
				@Override
				public void onSuccess(FoodNameModel result) {
					display.setContentEnabled(true);
					model = result;
					
					display.setModel(model);
				}
			});
			addRequest(req);
		}
	}

	/**
	 * Sets handler which is called when food name is created
	 * @param handler
	 */
	public void setHandler(FoodNameEditorHandler handler) {
		this.handler = handler;
	}

}
