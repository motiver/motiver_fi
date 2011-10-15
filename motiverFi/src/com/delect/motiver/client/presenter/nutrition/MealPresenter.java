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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.FoodCreatedEvent;
import com.delect.motiver.client.event.FoodRemovedEvent;
import com.delect.motiver.client.event.FoodUpdatedEvent;
import com.delect.motiver.client.event.MealCreatedEvent;
import com.delect.motiver.client.event.MealRemovedEvent;
import com.delect.motiver.client.event.MealUpdatedEvent;
import com.delect.motiver.client.event.handler.FoodCreatedEventHandler;
import com.delect.motiver.client.event.handler.FoodRemovedEventHandler;
import com.delect.motiver.client.event.handler.FoodUpdatedEventHandler;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter.FoodDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.client.view.nutrition.FoodView;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single meal (name and foods)
 * If new meal (no ID) shows selector panel
 * Events: 	- MealCreatedEvent (after creating new model)
 * 			- MealRemovedEvent (after removing or cancelling selection)
 *
 */
public class MealPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MealDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getUserContainer();
		public abstract void setHandler(MealHandler mealHandler);
		public abstract void setModel(MealModel meal);
	}

	public interface MealHandler {
		void foodsHidden();
		void foodsVisible();	//called when meal visible (we load foods then)
		void mealRemoved();
		/**
		 * New food added
		 * @param food : if not null -> adds copy of this food
		 */
		void newFood(FoodModel food);
		void saveData(MealModel meal);
	}
	public interface MealSelectedHandler {
		void select(MealModel model);
	}
	private CommentsBoxPresenter commentsPresenter;

	private MealDisplay display;
	private EmptyPresenter emptyPresenter;
	//child presenters
	private List<FoodPresenter> foodPresenters = new ArrayList<FoodPresenter>();
	private UserPresenter userPresenter;
	
	protected MealModel meal;

	public MealPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MealDisplay display, MealModel meal) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.meal = meal;
	}
	

	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		display.setModel(meal);
								
		//Event handlers
		//Launch MealUpdatedEvent if meal (content) changes
		display.setHandler(new MealHandler() {
			@Override
			public void foodsHidden() {
				//stop presenters
				unbindPresenters();
			}
			@Override
			public void foodsVisible() {
        showFoods();
			}
			@Override
			public void mealRemoved() {
				display.setContentEnabled(false);

				//remove meal and fire MealRemovedEvent
				final Request req = rpcService.removeMeal(meal, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(result) {

							stop();
							
							fireEvent(new MealRemovedEvent(meal));
						}
					}
				});
				addRequest(req);
			}
			@Override
			public void newFood(FoodModel food) {
				
				//if food -> add copy of that
				if(food != null) {
					display.setContentEnabled(false);
					
					//create copy
					FoodModel foodCopy = new FoodModel();
					foodCopy.setId(food.getId());
					foodCopy.setMealId(meal.getId());
					foodCopy.setTimeId(meal.getTimeId());
					foodCopy.setUid(meal.getUser().getUid());
					foodCopy.setName(food.getName());
					foodCopy.setAmount(food.getAmount());
					Request req = rpcService.addFood(foodCopy, new MyAsyncCallback<FoodModel>() {
						@Override
						public void onSuccess(FoodModel result) {
							display.setContentEnabled(true);

							result.setMealId(meal.getId());
							result.setTimeId(meal.getTimeId());
							result.setUid(meal.getUser().getUid());
							
							//add new presenter
							addNewFoodPresenter(result);
							
							//fire event
							fireEvent(new FoodCreatedEvent(result));
						}
					});
					addRequest(req);
				}
				//create dummy food
				else {
					final FoodModel foodDummy = new FoodModel(new FoodNameModel(0L, ""));
					addNewFoodPresenter(foodDummy);
				}
			}
			@Override
			public void saveData(MealModel model) {
				
				//if cancelled adding new meal
				if(model == null) {
					stop();
					
					//fire event
					fireEvent(new MealRemovedEvent(meal));
				}
				//added new meal
				else if(model.getId() == 0) {
					meal = model;
					display.setContentEnabled(false);
					
					//create model
					final Request req = rpcService.addMeal(model, model.getTimeId(), new MyAsyncCallback<MealModel>() {
						@Override
						public void onSuccess(MealModel result) {
							display.setContentEnabled(true);
							meal = result;

							refresh();
							
							//fire event
							final MealCreatedEvent event = new MealCreatedEvent(meal);
							fireEvent(event);
						}
					});
					addRequest(req);
				}
				//edited old value
				else {
					meal = model;
					final Request req = rpcService.updateMeal(meal, new MyAsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							fireEvent(new MealUpdatedEvent(meal));
						}
					});
					addRequest(req);
				}
			}
		});

		//EVENT: food created
		addEventHandler(FoodCreatedEvent.TYPE, new FoodCreatedEventHandler() {
			@Override
			public void onFoodCreated(FoodCreatedEvent event) {
				//handle event
				checkFoodEvent(event.getFood(), 0, event.getSource());
			}			
		});
		//EVENT: food updated
		addEventHandler(FoodUpdatedEvent.TYPE, new FoodUpdatedEventHandler() {
			@Override
			public void onFoodUpdated(FoodUpdatedEvent event) {
				//handle event
				checkFoodEvent(event.getFood(), 1, event.getSource());
			}			
		});
		//EVENT: food removed
		addEventHandler(FoodRemovedEvent.TYPE, new FoodRemovedEventHandler() {
			@Override
			public void onFoodRemoved(FoodRemovedEvent event) {
				checkFoodEvent(event.getFood(), 2, event.getSource());			
			}			
		});
		
	}	
	
	@Override
	public void onRun() {
    if(meal.getId() != 0) {
      showFoods();
    }
    //no model -> highlight
    else {
      highlight();
    }
	}


	@Override
	public void onStop() {
		
		unbindPresenters();
		
		if(userPresenter != null) {
      userPresenter.stop();
    }
	}
	
	/**
	 * Adds new food presenter
	 * @param food : to be updated
	 */
	private void addNewFoodPresenter(FoodModel food) {
		food.setMealId(meal.getId());
		food.setTimeId(meal.getTimeId());
		food.setUid(meal.getUser().getUid());
		final FoodPresenter wp = new FoodPresenter(rpcService, eventBus, (FoodDisplay)GWT.create(FoodView.class), food);
		addNewPresenter(wp);
	}
	
	/**
	 * Removes presenter from view
	 * @param meal
	 */
	private void removePresenter(FoodModel food) {

		//remove also from presenters
		for(int i=0; i < foodPresenters.size(); i++) {
			FoodPresenter presenter = foodPresenters.get(i);
			if(presenter != null && presenter.food.getId() == food.getId()) {
        foodPresenters.remove(presenter);
	    }
		}

		//if no meals/foods -> show empty presenter
		if(foodPresenters.size() == 0) {
			emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoFoods());
			emptyPresenter.run(display.getBodyContainer());
		}
	}
	
	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {
				
		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }

		if(commentsPresenter != null) {
      commentsPresenter.stop();
    }
		
		if(foodPresenters != null) {
			for(int i=0; i < foodPresenters.size(); i++) {
				final Presenter presenter = foodPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			foodPresenters.clear();					
		}
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(FoodPresenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}

		foodPresenters.add(presenter);
		presenter.run(display.getBodyContainer());
	}


	/**
	 * Handle food created/updated/removed event
	 * @param food
	 * @param target : 0=created, 1=updated, 2=removed
	 */
	protected void checkFoodEvent(FoodModel foodUpdated, int target, Object source) {

		boolean found = false;
		
		//if added to this time
		if(foodUpdated.getMealId() == meal.getId()) {

			//check if belongs to any food
			for(int i=0; i < foodPresenters.size(); i++) {
				FoodPresenter presenter = foodPresenters.get(i);
				if(presenter != null && presenter.equals(source)) {

					FoodModel model = presenter.food;
					//updated
					if(target == 1) {
						model.setAmount(foodUpdated.getAmount());
						model.setName(foodUpdated.getName());			
					}
					//removed
					else if(target == 2) {						
            removePresenter(model);
			    }

					found = true;
					break;
				}
				
			}
			//calculate totals
			if(found) {
				calculateTotals(true);
				
				//highlight
				if(target == 0) {
          display.highlight();
		    }
			}
		}
	}


	/**
	 * Shows foods
	 */
	protected void showFoods() {

		try {

			unbindPresenters();
			
			//userview
			if(meal.getTimeId() == 0) {
        if(userPresenter != null) {
          userPresenter.stop();
        }
				
				//show user if not our workout
				if(!meal.getUser().getUid().equals(AppController.User.getUid())) {
					userPresenter = new UserPresenter(rpcService, eventBus, (UserDisplay) GWT.create(UserView.class), meal.getUser(), false);
					userPresenter.run(display.getUserContainer());
				}
			}

			//if no meals
			if(meal.getFoods().size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoFoods());
				emptyPresenter.run(display.getBodyContainer());
			}
			else {
				for(FoodModel m : meal.getFoods()) {
					m.setUid(meal.getUser().getUid());
					m.setTimeId(meal.getTimeId());
					m.setMealId(meal.getId());
					final FoodPresenter fp = new FoodPresenter(rpcService, eventBus, (FoodDisplay)GWT.create(FoodView.class), m);
					addNewPresenter(fp);
					
				}
			}

			//show comments (only if not in any time)
			if(meal.getTimeId() == 0 && commentsPresenter != null) {
        commentsPresenter.stop();
				commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), meal);
				commentsPresenter.run(display.getCommentsContainer());
			}

      calculateTotals(false);
		    
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}

	/**
	 * Calculate totals from this time
	 * Fires event if time content changes
	 */
	void calculateTotals(boolean fireEvent) {
		
		//if not in any time
		if(meal.getId() == 0) {
      return;
    }
		
		double e = 0D;
		double p = 0;
		double c = 0;
		double f = 0;
		
		try {

			for(int i=0; i < foodPresenters.size(); i++) {
				Presenter presenter = foodPresenters.get(i);
				if(presenter != null) {
					if(presenter instanceof FoodPresenter) {
						FoodModel model = ((FoodPresenter)presenter).food;
						if(model.getName() != null && model.getName().getEnergy() > 0) {
							final double amount = model.getAmount();
							e += (model.getName().getEnergy() / 100) * amount;
							p += (model.getName().getProtein() / 100) * amount;
							c += (model.getName().getCarb() / 100) * amount;
							f += (model.getName().getFet() / 100) * amount;
						}
					}
				}
			}
			
		} catch (Exception e1) {
      Motiver.showException(e1);
		}
		
		//if changes
		if(Double.compare(e, meal.getEnergy()) != 0 || Double.compare(p, meal.getProtein()) != 0 || Double.compare(c, meal.getCarb()) != 0 || Double.compare(f, meal.getFet()) != 0 ) {
			meal.setEnergy(e);
			meal.setProtein(p);
			meal.setCarb(c);
			meal.setFet(f);
			
			//update model
			display.setModel(meal);
			
			//fire event
			if(fireEvent) {
        fireEvent(new MealUpdatedEvent(meal));
	    }
		}
	}

}
