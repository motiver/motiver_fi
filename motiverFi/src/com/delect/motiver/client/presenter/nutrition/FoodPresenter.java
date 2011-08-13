/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.nutrition;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.FoodCreatedEvent;
import com.delect.motiver.client.event.FoodRemovedEvent;
import com.delect.motiver.client.event.FoodUpdatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.nutrition.FoodNameEditorPresenter.FoodNameEditorDisplay;
import com.delect.motiver.client.presenter.nutrition.FoodNameEditorPresenter.FoodNameEditorHandler;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.nutrition.FoodNameEditorView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;

/**
 * Shows single food
 * @author Antti
 *
 */
public class FoodPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class FoodDisplay extends Display {

		public abstract void setHandler(FoodHandler foodHandler);
		public abstract void setModel(FoodModel food);
		public abstract void setNameComboEnabled(boolean b);
	}

	public interface FoodHandler {
		void foodEdited();
		void foodRemoved();
		void nameChanged(String newName);	//when new name is typed
		void query(String query, AsyncCallback<List<FoodNameModel>> callback);	//called when user search for foods
		void saveData(FoodModel food);
	}
	private FoodDisplay display;

	//child presenters
	private FoodNameEditorPresenter foodNameEditorPresenter;

	private String lastQuery = "";
	
	protected FoodModel food;

	public FoodPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, FoodDisplay display, FoodModel food) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.food = food;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		//if new food -> set uid to our
		if(food.getId() == 0) {
      food.setUid(AppController.User.getUid());
    }

		display.setModel(food);
		if(food.getId() != 0) {
			
			display.setContentEnabled(true);
			
			//event handler (fire event)
			display.setHandler(new FoodHandler() {

				@Override
				public void foodEdited() {
					
					//if presenter already visible -> cancel
					if(foodNameEditorPresenter != null) {
            return;
			    }

					//disable combo
					display.setNameComboEnabled(false);
					
					foodNameEditorPresenter = new FoodNameEditorPresenter(rpcService, eventBus, (FoodNameEditorDisplay)GWT.create(FoodNameEditorView.class), food.getName());
					foodNameEditorPresenter.run(display.getBaseContainer());
					//refresh view when name set / edit cancelled
					foodNameEditorPresenter.setHandler(new FoodNameEditorHandler() {
						@Override
						public void editCancelled() {
							
							//enable combo
							display.setNameComboEnabled(true);
							
							//reset model
							display.setModel(food);
							
							foodNameEditorPresenter = null;
						}
						@Override
						public void nameSaved(FoodNameModel model) {
							
							//enable combo
							display.setNameComboEnabled(true);
							
							foodNameEditorPresenter = null;							
							food.setName(model);
						}
					});
				}

				@Override
				public void foodRemoved() {
					display.setContentEnabled(false);
					
					List<FoodModel> list = new ArrayList<FoodModel>();
					list.add(food);
					final Request req = rpcService.removeFoods(list, new MyAsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							if(result) {
								stop();
								
								if(result) {
									fireEvent(new FoodRemovedEvent(food));
									
								}
							}
						}
					});
					addRequest(req);
				}

				@Override
				public void nameChanged(String newName) {
					
					//if presenter already visible -> cancel
					if(foodNameEditorPresenter != null) {
            return;
			    }
					
					//disable combo
					display.setNameComboEnabled(false);
					
					//create dummy model
					FoodNameModel model = new FoodNameModel(0L, "");
					model.setLocale(AppController.User.getLocale());
					//new name typed -> launch NewFoodNamePresenter
					foodNameEditorPresenter = new FoodNameEditorPresenter(rpcService, eventBus, (FoodNameEditorDisplay)GWT.create(FoodNameEditorView.class), model);
					foodNameEditorPresenter.run(display.getBaseContainer());

					//refresh view when name set / edit cancelled
					foodNameEditorPresenter.setHandler(new FoodNameEditorHandler() {
						@Override
						public void editCancelled() {
							
							//enable combo
							display.setNameComboEnabled(true);
							
							//reset model
							display.setModel(food);
							
							foodNameEditorPresenter = null;
						}
						@Override
						public void nameSaved(FoodNameModel model) {
							
							//enable combo
							display.setNameComboEnabled(true);
							
							foodNameEditorPresenter = null;
							
							food.setName(model);
							
							//update food
							updateFood();
						}
					});
				}

				@Override
				public void query(final String query, final AsyncCallback<List<FoodNameModel>> callback) {
					//save query
					lastQuery   = query;

					Motiver.setNextCallCacheable(true);
					final Request req = rpcService.searchFoodNames(query, Constants.LIMIT_SEARCH_NAMES, new MyAsyncCallback<List<FoodNameModel>>() {
						@Override
						public void onSuccess(List<FoodNameModel> result) {
							//only if last query
							if(query.equals(lastQuery)) {
                callback.onSuccess(result);
					    }
						}
					});
					addRequest(req);
				}

				@Override
				public void saveData(FoodModel model) {
					food = model;
					updateFood();
				}
			});
		}
		
		
	}

	@Override
	public void onRun() {
	    
    //if no food -> create new one
    if(food.getId() == 0) {
			
      display.setContentEnabled(false);

			final Request req = rpcService.addFood(food, new MyAsyncCallback<FoodModel>() {
				@Override
				public void onSuccess(FoodModel result) {
					
					if(result != null) {
            display.setContentEnabled(true);

						//set data
						food.setId(result.getId());
						
						//fire event
						fireEvent(new FoodCreatedEvent(food));
						
						//refresh
						display.setModel(food);
						run(display.getBaseContainer());
					}
				}
			});
			addRequest(req);
    }
	}

	@Override
	public void onStop() {

		if(foodNameEditorPresenter != null) {
      foodNameEditorPresenter.stop();
    }
	}

	/**
	 * Updates food from server based to model-variable
	 * Fires FoodUpdatedEvent
	 */
	protected void updateFood() {
	  
		final Request req = rpcService.updateFood(food, new MyAsyncCallback<FoodModel>() {
			@Override
			public void onSuccess(FoodModel result) {

				//update model
				if(result != null) {
					food.setName(result.getName());
					food.setAmount(result.getAmount());
					display.setModel(food);
					
				}
			}
		});
		addRequest(req);
		
		fireEvent(new FoodUpdatedEvent(food));
	}

}
