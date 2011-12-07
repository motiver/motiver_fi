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
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.MealCreatedEvent;
import com.delect.motiver.client.event.MealRemovedEvent;
import com.delect.motiver.client.event.MealSelectedEvent;
import com.delect.motiver.client.event.MealShowEvent;
import com.delect.motiver.client.event.MealsSelectCancelledEvent;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.event.handler.MealCreatedEventHandler;
import com.delect.motiver.client.event.handler.MealRemovedEventHandler;
import com.delect.motiver.client.event.handler.MealSelectedEventHandler;
import com.delect.motiver.client.event.handler.MealShowEventHandler;
import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.nutrition.MealPresenter.MealDisplay;
import com.delect.motiver.client.presenter.nutrition.MealsListSearchPresenter.MealsListSearchDisplay;
import com.delect.motiver.client.presenter.nutrition.MealsListSubPresenter.MealsListSubDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.nutrition.MealView;
import com.delect.motiver.client.view.nutrition.MealsListSearchView;
import com.delect.motiver.client.view.nutrition.MealsListSubView;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.TimeModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 3 "pages": (with search box on top)
 *  <br>- main page (shows user's meals, most popular)
 *  <br>- search results
 *  <br>- single meal (when individual meal is selected)
 *  <br>- Fires meal created event when meal is selected and stops itself 
 * @author Antti
 *
 */
public class MealsListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MealsListDisplay extends Display {

		public abstract LayoutContainer getDataContainer();
		public abstract void setBackButtonVisible(boolean b);
		public abstract void setCancelButtonVisible(boolean b);
		public abstract void setCopyButtonVisible(boolean b);
		public abstract void setHandler(MealsListHandler mealsListHandler);
		public abstract void setMoveToTimeButtonVisible(boolean visible, TimeModel time);
		public abstract void setQuickSelectionButtonVisible(boolean visible, TimeModel time);
	}

	public interface MealsListHandler {
		void onBackButtonClicked();
		void onCancelButtonClicked();
		void onCopyButtonClicked();
		void onMoveToTimeButtonClicked();
		void onQuickSelectionButtonClicked();
		void search(String query);
	}
	private MealsListDisplay display;

	private String lastQuery = "";
	private int lastView = 0;
	private long mealId = 0;
	private MealsListSearchPresenter mealsListSearchPresenter;
	//child presenters
	private MealsListSubPresenter mealsListSubPresenter;
	
	private List<MealModel> quickSelectionMeals = new ArrayList<MealModel>();	//meals which are selected

	private MealPresenter singleMealPresenter;
	private TimeModel time;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param date
	 * @param mealId : if some meal is open as default
	 */
	public MealsListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MealsListDisplay display, TimeModel time, long mealId) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.time = time;
    this.mealId  = mealId;

    boolean quickSelectionEnabled = (time != null);
		mealsListSubPresenter = new MealsListSubPresenter(rpcService, eventBus, (MealsListSubDisplay)GWT.create(MealsListSubView.class), quickSelectionEnabled);
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setHandler(new MealsListHandler() {
			@Override
			public void onBackButtonClicked() {
				//coming back from search results
				if(singleMealPresenter == null) {
					showMainView();
				}
				//if coming back from single exercise
				else {
					//unbind meal
					singleMealPresenter.stop();
					singleMealPresenter = null;
					
					//last view was search results
					if(lastView == 1) {
            loadSearch(lastQuery);
			    }
					//last view was main menu
					else {
            showMainView();
			    }
				}
			}
			@Override
			public void onCancelButtonClicked() {
				//unbind this
				stop();
				//fire cancel event
				eventBus.fireEvent(new MealsSelectCancelledEvent(time));
			}
			@Override
			public void onCopyButtonClicked() {
				copyMeals();
			}
			@Override
			public void onMoveToTimeButtonClicked() {
				//get meal from single presenter
				List<MealModel> meals = new ArrayList<MealModel>();
				meals.add(singleMealPresenter.meal);
				
				moveMealToTime(meals);
			}
			@Override
			public void onQuickSelectionButtonClicked() {
				//get meals from quick selection
				moveMealToTime(quickSelectionMeals);
				quickSelectionMeals.clear();
			}
			@Override
			public void search(String query) {
				loadSearch(query);
			}
		});

		//EVENT: meal created -> open meal
		addEventHandler(MealCreatedEvent.TYPE, new MealCreatedEventHandler() {
			@Override
			public void onMealCreated(MealCreatedEvent event) {
				//if no date
				if(event.getMeal() != null) {
					if(event.getMeal().getTimeId() == 0) {
						showSingleMeal(event.getMeal());
					}
				}
			}
		});
		
		//EVENT: meal removed -> show main view
		addEventHandler(MealRemovedEvent.TYPE, new MealRemovedEventHandler() {
			@Override
			public void onMealRemoved(MealRemovedEvent event) {
				//if meal in list
				if(event.getMeal() != null && event.getMeal().getTimeId() == 0) {
          showMainView();
		    }
			}
		});
		
		//EVENT: show meal
		addEventHandler(MealShowEvent.TYPE, new MealShowEventHandler() {
			@Override
			public void selectMeal(MealShowEvent event) {
				showSingleMeal(event.getMeal());
			}
		});
		
		//EVENT: meal selected (quick select)
		addEventHandler(MealSelectedEvent.TYPE, new MealSelectedEventHandler() {
			@Override
			public void mealSelected(MealSelectedEvent event) {
				setQuickSelection(event.getMeal(), event.isSelected());
			}
		});
      
    //EVENT: user selected -> show blog
    addEventHandler(UserSelectedEvent.TYPE, new UserSelectedEventHandler() {
      @Override
      public void userSelected(UserSelectedEvent event) {
        //open blog in new window
        Window.open(event.getUser().getBlogUrl(), "_blank", "status=1,toolbar=1,location=1,menubar=1,directories=1,resizable=1,scrollbars=1");
      }
    });
		
    //if date -> show inner title
    if(time != null) {
      //info text
      display.setTitle(AppController.Lang.SelectMeal());
      display.setCancelButtonVisible(true);
    }
	}
  
  @Override
  public void onRefresh() {
    super.onRefresh();
    
    //highlight and scroll
    highlight();
  }

	@Override
	public void onRun() {
	    
    //show single meal
    if(mealId != 0) {
			final Request req = rpcService.getMeal(mealId, new MyAsyncCallback<MealModel>() {
				@Override
				public void onSuccess(MealModel result) {
          showSingleMeal(result);
        }
			});
			addRequest(req);
    }	    	
    else {
      showMainView();
    }
	    
    //highlight and scroll
    highlight();
  }
	
	@Override
	public void onStop() {

		if(mealsListSubPresenter != null) {
			mealsListSubPresenter.stop();
    }
		if(singleMealPresenter != null) {
			singleMealPresenter.stop();
    }
		if(mealsListSearchPresenter != null) {
			mealsListSearchPresenter.stop();
    }
	}


	/**
	 * Copies meal to our meals
	 */
	protected void copyMeals() {
		
		try {
			display.setContentEnabled(false);
			
			//get meal from single presenter
			MealModel model = singleMealPresenter.meal;
			
			//reset dates
			model.setDate(null);
			
			//add meal
			final Request req = rpcService.addMeal(model, 0L, new MyAsyncCallback<MealModel>() {
				@Override
				public void onSuccess(MealModel result) {
					display.setContentEnabled(true);
					
					//fire events
					eventBus.fireEvent(new MealCreatedEvent(result));
				}
			});
			addRequest(req);
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Moves meal to date
	 */
	protected void moveMealToTime(List<MealModel> meals) {
		try {
			display.setContentEnabled(false);
			
			for(MealModel model : meals)
			  model.setTimeId(time.getId());
			
			//add meal
			final Request req = rpcService.addMeals(meals, time.getId(), new MyAsyncCallback<List<MealModel>>() {
				@Override
				public void onSuccess(List<MealModel> result) {
					display.setContentEnabled(true);
					
					List<MealModel> meals = result;
					
					//fire event
					for(MealModel meal : meals) {
						meal.setTimeId(time.getId());
					
						eventBus.fireEvent(new MealCreatedEvent(meal));
					}
				}
			});
			addRequest(req);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}

	/**
	 * Called when meals is selected (or selection is set off)
	 * <br>Shows button to quick select meals
	 * @param meal
	 * @param selected
	 */
	protected void setQuickSelection(MealModel meal, boolean selected) {
		//if new meal
		if(selected) {
			quickSelectionMeals.add(meal);
    }
		else {
			quickSelectionMeals.remove(meal);
    }

		//only if models selected and time not null
		display.setQuickSelectionButtonVisible(quickSelectionMeals.size() > 0 && time != null, time);
	}


	/**
	 * Shows main view
	 *  - my meals
	 *  - most popular
	 */
	protected void showMainView() {

		lastView = 0;
		
		//show/hide buttons
		display.setBackButtonVisible(false);
		display.setCopyButtonVisible(false);
		display.setMoveToTimeButtonVisible(false, null);
		display.setQuickSelectionButtonVisible(false, null);
		
		if(singleMealPresenter != null) {
			singleMealPresenter.stop();
    }
		singleMealPresenter = null;
		if(mealsListSearchPresenter != null) {
			mealsListSearchPresenter.hide();
    }
		
		//run main view
		mealsListSubPresenter.run(display.getDataContainer());
			
	}

	/*
	 * VIEW 3 (single meal
	 */
	protected void showSingleMeal(MealModel m) {
		
		try {
			if(singleMealPresenter != null) {
				singleMealPresenter.stop();
	    }
			
			//hide main page
			if(mealsListSubPresenter != null) {
				mealsListSubPresenter.hide();
	    }
			if(mealsListSearchPresenter != null) {
				mealsListSearchPresenter.hide();
	    }
			
			//show single meal
			singleMealPresenter = new MealPresenter(rpcService, eventBus, (MealDisplay)GWT.create(MealView.class), m);
			singleMealPresenter.run(display.getDataContainer());
			
			//buttons
			display.setBackButtonVisible(true);
			display.setCopyButtonVisible(false);
			display.setMoveToTimeButtonVisible(false, null);
			display.setQuickSelectionButtonVisible(false, null);
			
			//show buttons only if meal's id set
			if(m.getId() > 0) {
				//if date set
				if(time != null) {
					display.setMoveToTimeButtonVisible(true, time);
				}
				else {
					//copy to our meals
					if(!m.getUser().getUid().equals(AppController.User.getUid())) {
						display.setCopyButtonVisible(true);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads meals
	 */
	void loadSearch(final String query) {

		lastView = 1;
		
		//show/hide buttons
		display.setBackButtonVisible(true);
		display.setCopyButtonVisible(false);
		display.setMoveToTimeButtonVisible(false, null);
		display.setQuickSelectionButtonVisible(false, null);
		
		if(singleMealPresenter != null) {
			singleMealPresenter.stop();
    }
		singleMealPresenter = null;
		if(mealsListSubPresenter != null) {
			mealsListSubPresenter.hide();
    }
		
		//if not already loaded
		if(mealsListSearchPresenter == null || !query.equals(lastQuery)) {
			lastQuery  = query;
			if(mealsListSearchPresenter != null) {
				mealsListSearchPresenter.stop();
	    }

      boolean quickSelectionEnabled = (time != null);
			mealsListSearchPresenter = new MealsListSearchPresenter(rpcService, eventBus, (MealsListSearchDisplay)GWT.create(MealsListSearchView.class), query, quickSelectionEnabled);
			mealsListSearchPresenter.run(display.getDataContainer());
		}
		else {
			mealsListSearchPresenter.run(display.getDataContainer());
    }
	}
}
