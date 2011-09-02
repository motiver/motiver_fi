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
import com.delect.motiver.client.event.MealsSelectCancelledEvent;
import com.delect.motiver.client.event.TimeCreatedEvent;
import com.delect.motiver.client.event.TimeRemovedEvent;
import com.delect.motiver.client.event.TimeUpdatedEvent;
import com.delect.motiver.client.event.handler.FoodCreatedEventHandler;
import com.delect.motiver.client.event.handler.FoodRemovedEventHandler;
import com.delect.motiver.client.event.handler.FoodUpdatedEventHandler;
import com.delect.motiver.client.event.handler.MealCreatedEventHandler;
import com.delect.motiver.client.event.handler.MealRemovedEventHandler;
import com.delect.motiver.client.event.handler.MealUpdatedEventHandler;
import com.delect.motiver.client.event.handler.MealsSelectCancelledEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.nutrition.EmptyTimePresenter.EmptyTimeDisplay;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter.FoodDisplay;
import com.delect.motiver.client.presenter.nutrition.MealPresenter.MealDisplay;
import com.delect.motiver.client.presenter.nutrition.MealsListPresenter.MealsListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.nutrition.EmptyTimeView;
import com.delect.motiver.client.view.nutrition.FoodView;
import com.delect.motiver.client.view.nutrition.MealView;
import com.delect.motiver.client.view.nutrition.MealsListView;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.TimeModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single time (containing foods/meals)
 * @author Antti
 *
 */
public class TimePresenter extends Presenter implements Comparable<TimePresenter> {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class TimeDisplay extends Display {
		public abstract LayoutContainer getBodyContainer();
		public abstract void setHandler(TimeHandler timeHandler);
		public abstract void setModel(TimeModel time);
	}

	public interface TimeHandler {
		/**
		 * New food added
		 * @param food : if not null -> adds copy of this food
		 */
		void newFood(FoodModel food);
		/**
		 * New meal added
		 * @param meal : if not null -> adds copy of this meal
		 */
		void newMeal(MealModel meal);
		void timeChanged(int time);
		void timeRemoved();
	}
	//new food handler
	public Listener<BaseEvent> NewFoodListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewFood();
		}
	};

	//new meal handler
	public Listener<BaseEvent> NewMealListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewMeal();
		}
	};
	private TimeDisplay display;
	
	private Presenter emptyPresenter;
	
	//child presenters
	private List<Presenter> mealfoodPresenters = new ArrayList<Presenter>();
	protected TimeModel time;

	public TimePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, TimeDisplay display, TimeModel time) {
		super(rpcService, eventBus);
		this.display = display;
		
    this.time = time;
	}
	

	@Override
	public int compareTo(TimePresenter compare) {
		return time.compareTo(compare.time);
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setModel(time);
				
		//event handler (fire event)
		display.setHandler(new TimeHandler() {
			@Override
			public void newFood(FoodModel food) {
				//if food -> add copy of that
				if(food != null) {
					display.setContentEnabled(false);
					
					//create copy
					FoodModel foodCopy = new FoodModel();
					foodCopy.setId(food.getId());
					foodCopy.setUid(time.getUid());
					foodCopy.setTimeId(time.getId());
					foodCopy.setName(food.getName());
					foodCopy.setAmount(food.getAmount());
					rpcService.addFood(foodCopy, new MyAsyncCallback<FoodModel>() {
						@Override
						public void onSuccess(FoodModel result) {
							display.setContentEnabled(true);
							
							result.setUid(time.getUid());
							result.setTimeId(time.getId());
              result.setMealId(0L);
							
							//add new presenter
							addNewFoodPresenter(result);
							
							//fire event
							fireEvent(new FoodCreatedEvent(result));
						}
					});
				}
				//show selection
				else {
					addNewFood();
		    }
			}

			@Override
			public void newMeal(MealModel meal) {
				//if meal -> add copy of that
				if(meal != null) {
					display.setContentEnabled(false);
					
					//create copy
					MealModel mealCopy = new MealModel();
					mealCopy.setId(meal.getId());
					mealCopy.setUid(time.getUid());
					mealCopy.setTimeId(time.getId());
					final Request req = rpcService.addMeal(mealCopy, new MyAsyncCallback<MealModel>() {
						@Override
						public void onSuccess(MealModel result) {
							display.setContentEnabled(true);
							
							result.setUid(time.getUid());
							result.setUid(time.getUid());
							result.setTimeId(time.getId());
							
							//fire event
							fireEvent(new MealCreatedEvent(result));
						}
					});
					addRequest(req);
				}
				//show selection
				else {
					addNewMeal();
		    }
			}

			@Override
			public void timeChanged(int newtime) {
				display.setContentEnabled(false);
				time.setTime(newtime);
				final Request req = rpcService.updateTime(time, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						display.setContentEnabled(true);

						//fire event
						fireEvent(new TimeUpdatedEvent(time));
					}
				});
				addRequest(req);
			}

			@Override
			public void timeRemoved() {
				
				display.setContentEnabled(false);
				
				//remove time and fire TimeRemovedEvent
				TimeModel[] times = new TimeModel[] {time};
				final Request req = rpcService.removeTimes(times, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(result) {
							stop();
							
							//fire event
							fireEvent(new TimeRemovedEvent(time));
						}
					}
				});
				addRequest(req);
			}
		});

		//EVENT: meal added
		addEventHandler(MealCreatedEvent.TYPE, new MealCreatedEventHandler() {
			@Override
			public void onMealCreated(MealCreatedEvent event) {
				//handle event
				checkMealEvent(event.getMeal(), 0, event.getSource());	
			}
		});
		//EVENT: meal updated
		addEventHandler(MealUpdatedEvent.TYPE, new MealUpdatedEventHandler() {
			@Override
			public void onMealUpdated(MealUpdatedEvent event) {
				//handle event
				checkMealEvent(event.getMeal(), 1, event.getSource());	
			}
		});
		//EVENT: meal removed
		addEventHandler(MealRemovedEvent.TYPE, new MealRemovedEventHandler() {
			@Override
			public void onMealRemoved(MealRemovedEvent event) {
				//handle event
				checkMealEvent(event.getMeal(), 2, event.getSource());	
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
				checkFoodEvent(event.getFood(), 1, event.getSource());
			}			
		});
		//EVENT: food removed
		addEventHandler(FoodRemovedEvent.TYPE, new FoodRemovedEventHandler() {
			@Override
			public void onFoodRemoved(FoodRemovedEvent event) {
				//handle event
				checkFoodEvent(event.getFood(), 2, event.getSource());				
			}			
		});
		//EVENT: meal selection cancelled
		addEventHandler(MealsSelectCancelledEvent.TYPE, new MealsSelectCancelledEventHandler() {
			@Override
			public void onCancel(MealsSelectCancelledEvent event) {
				//unbind list IF this time
				if(event.getTime() != null) {
					if(event.getTime().getId() == time.getId()) {
						//check list
						for(int i=0; i < mealfoodPresenters.size(); i++) {
							Presenter presenter = mealfoodPresenters.get(i);
							if(presenter != null && presenter instanceof MealsListPresenter) {
								mealfoodPresenters.remove(presenter);
								break;
							}
						}
						checkIfEmptyPresenterNeeded();
					}
				}
			}
		});
		
	}
	
	@Override
	public void onRun() {
		show();
	    
		//new time
		if(time.getId() == 0) {

			display.setContentEnabled(false);
			final Request req = rpcService.addTime(time, new MyAsyncCallback<TimeModel>() {
				@Override
				public void onSuccess(TimeModel result) {
					display.setContentEnabled(true);
					if(result != null) {
						
						time = result;
						display.setModel(time);
						
						//fire event
						eventBus.fireEvent(new TimeCreatedEvent(time));
					}
					//invalid result
					else {
						stop();
			    }
				}
			});
			addRequest(req);
		}
  }


	@Override
	public void onStop() {

		//stop presenters
		unbindPresenters();
	}
	
	/**
	 * Adds new food presenter
	 * @param food : to be updated
	 */
	private void addNewFoodPresenter(FoodModel food) {
    food.setUid(time.getUid());
    food.setTimeId(time.getId());
    food.setMealId(0L);
		final FoodPresenter wp = new FoodPresenter(rpcService, eventBus, (FoodDisplay)GWT.create(FoodView.class), food);
		addNewPresenter(wp);
	}
	
	/**
	 * Adds new meal presenter
	 * @param meal : to be updated
	 */
	private void addNewMealPresenter(MealModel meal) {
		meal.setUid(time.getUid());
		meal.setUid(time.getUid());
		meal.setTimeId(time.getId());
		final MealPresenter wp = new MealPresenter(rpcService, eventBus, (MealDisplay)GWT.create(MealView.class), meal);
		addNewPresenter(wp);
	}


	/**
	 * Checks how many workouts are and shows empty presenter if needed
	 */
	private void checkIfEmptyPresenterNeeded() {

		//if no meals/foods -> show empty presenter
		if(mealfoodPresenters.size() == 0) {
      if(time.getUid().equals(AppController.User.getUid())) {
				emptyPresenter = new EmptyTimePresenter(rpcService, eventBus, (EmptyTimeDisplay)GWT.create(EmptyTimeView.class));
      }
			else {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoFoods());
	    }
			emptyPresenter.run(display.getBodyContainer());
		}
	}
	
	/**
	 * Removes presenter from view
	 * @param food
	 */
	private void removePresenter(FoodModel food) {

		//remove also from presenters
		for(int i=0; i < mealfoodPresenters.size(); i++) {
			Presenter presenter = mealfoodPresenters.get(i);
			if(presenter != null && presenter instanceof FoodPresenter && ((FoodPresenter)presenter).food.getId() == food.getId()) {
        mealfoodPresenters.remove(presenter);
      }
		}

		checkIfEmptyPresenterNeeded();
	}
	
	/**
	 * Removes presenter from view
	 * @param meal
	 */
	private void removePresenter(MealModel meal) {

		//remove also from presenters
		for(int i=0; i < mealfoodPresenters.size(); i++) {
			Presenter presenter = mealfoodPresenters.get(i);
			if(presenter != null && presenter instanceof MealPresenter && ((MealPresenter)presenter).meal.getId() == meal.getId()) {
        mealfoodPresenters.remove(presenter);
      }
		}

		checkIfEmptyPresenterNeeded();
	}
	
	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		
		if(mealfoodPresenters != null) {
			for(int i=0; i < mealfoodPresenters.size(); i++) {
				final Presenter presenter = mealfoodPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			mealfoodPresenters.clear();				
		}
	}

	/**
	 * Adds new food
	 */
	protected void addNewFood() {
		//create dummy food
		final FoodModel foodDummy = new FoodModel(new FoodNameModel(0L, ""));
		foodDummy.setTimeId(time.getId());
		foodDummy.setUid(time.getUid());
		//init new foodpresenter
    final FoodPresenter fp = new FoodPresenter(rpcService, eventBus, (FoodDisplay)GWT.create(FoodView.class), foodDummy);
    addNewPresenter(fp);
	}


	/**
	 * Shows meals' list
	 */
	protected void addNewMeal() {
		MealsListPresenter mealsListPresenter = new MealsListPresenter(rpcService, eventBus, (MealsListDisplay)GWT.create(MealsListView.class), time, 0L);
		addNewPresenter(mealsListPresenter);
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(Presenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}

		mealfoodPresenters.add(presenter);
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
		if(foodUpdated.getTimeId() == time.getId()) {

			//check if belongs to any food
			for(int i=0; i < mealfoodPresenters.size(); i++) {
				Presenter presenter = mealfoodPresenters.get(i);
				if(presenter != null && presenter instanceof FoodPresenter && presenter.equals(source)) {
					FoodModel food = ((FoodPresenter)presenter).food;
					//updated
					if(target == 1) {
						food.setAmount(foodUpdated.getAmount());
						food.setName(foodUpdated.getName());			
					}
					//removed
					else if(target == 2) {							
						removePresenter(food);
			    }

					found = true;
					break;
				}
			}
			//calculate totals
			if(found) {
				calculateTotals(true);
				
				//highlight if new meal created
				if(target == 0) {
					display.highlight();
		    }
			}
		}
	}

	/**
	 * Handle meal created/updated/removed event
	 * @param meal
	 * @param target : 0=created, 1=updated, 2=removed
	 */
	protected void checkMealEvent(MealModel mealUpdated, int target, Object source) {

		boolean found = false;
		
		//if added to this time
		if(mealUpdated.getTimeId() == time.getId()) {

			if(target == 0) {
				//remove list
				for(int i=0; i < mealfoodPresenters.size(); i++) {
					Presenter presenter = mealfoodPresenters.get(i);
					if(presenter != null) {
						if(presenter instanceof MealsListPresenter) {
							presenter.stop();
							mealfoodPresenters.remove(presenter);
							break;
						}
					}
				}
				
				addNewMealPresenter(mealUpdated);
				found = true;
			}
			else {
				//check if belongs to any meal
				for(int i=0; i < mealfoodPresenters.size(); i++) {
					Presenter presenter = mealfoodPresenters.get(i);
					if(presenter != null && presenter instanceof MealPresenter && presenter.equals(source)) {
						MealModel meal = ((MealPresenter)presenter).meal;
						//removed
						if(target == 2) {
							removePresenter(meal);
						}
						found = true;
						break;
					}					
				}
			}
			
			//calculate totals
			if(found) {
				calculateTotals(true);

				//highlight if new meal created
				if(target == 0) {
					display.highlight();
		    }
			}	
		}
		
	}

	/**
	 * Calculate totals from this time
	 * @param: fireEvent: whether TimeUpdatedEvent is fired
	 */
	void calculateTotals(boolean fireEvent) {
		
		if(time == null) {
			return;
    }

		double e = 0;
		double p = 0;
		double c = 0;
		double f = 0;
		
		try {
		
			for(int i=0; i < mealfoodPresenters.size(); i++) {
				Presenter presenter = mealfoodPresenters.get(i);
				if(presenter != null) {
					if(presenter instanceof FoodPresenter) {
						FoodModel food = ((FoodPresenter)presenter).food;
						if(food.getName() != null && food.getName().getEnergy() > 0) {
							final double amount = food.getAmount();
							e += (food.getName().getEnergy() / 100) * amount;
							p += (food.getName().getProtein() / 100) * amount;
							c += (food.getName().getCarb() / 100) * amount;
							f += (food.getName().getFet() / 100) * amount;
						}
					}
					else if(presenter instanceof MealPresenter) {
						MealModel meal = ((MealPresenter)presenter).meal;
						if(meal != null) {
							e += meal.getEnergy();
							p += meal.getProtein();
							c += meal.getCarb();
							f += meal.getFet();
						}
					}
				}
			}
			
		} catch (Exception e1) {
      Motiver.showException(e1);
		}
		
		//if changes
		if(e != time.getEnergy() || p != time.getProtein() || c != time.getCarb() || f != time.getFet()) {
			time.setEnergy(e);
			time.setProtein(p);
			time.setCarb(c);
			time.setFet(f);
			
			//update model
			display.setModel(time);
			
			//fire event
			if(fireEvent) {
				fireEvent(new TimeUpdatedEvent(time));
			}
		}
	}


	/**
	 * shows meals and foods from time model
	 */
	void show() {

		display.setContentEnabled(false);
		
		unbindPresenters();
		
    //show foods/meals
		if(time.getMeals() != null) {
      for(MealModel meal : time.getMeals()) {
        addNewMealPresenter(meal);
				
      }
		}
		if(time.getFoods() != null) {
      for(FoodModel food : time.getFoods()) {
        addNewFoodPresenter(food);			
      }
		}

		display.setContentEnabled(true);
	
    calculateTotals(false);

    //if nothing found
		boolean found = false;
		if(time.getMeals() != null && time.getMeals().size() > 0) {
      found = true;
    }
		if(time.getFoods() != null && time.getFoods().size() > 0) {
      found = true;
    }
		
    if(!found) {
      if(time.getUid().equals(AppController.User.getUid())) {
				emptyPresenter = new EmptyTimePresenter(rpcService, eventBus, (EmptyTimeDisplay)GWT.create(EmptyTimeView.class));
      }
			else {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoFoods());
	    }
			emptyPresenter.run(display.getBodyContainer());
    }
	}
	
}
