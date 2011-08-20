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
import com.delect.motiver.client.event.MealCreatedEvent;
import com.delect.motiver.client.event.MealRemovedEvent;
import com.delect.motiver.client.event.MealShowEvent;
import com.delect.motiver.client.event.handler.MealCreatedEventHandler;
import com.delect.motiver.client.event.handler.MealRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.nutrition.MealLinkPresenter.MealLinkDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.nutrition.MealLinkView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.MealModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Main page for meals' list
 * <br>Launch MealSelectEvent when meal is selected
 * <br>Launch MealCreatedEvent when meal is created
 * @author Antti
 *
 */
public class MealsListSubPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MealsListSubDisplay extends Display {

		public abstract LayoutContainer getMostPopularContainer();
		public abstract LayoutContainer getMyMealsContainer();
		public abstract void setHandler(MealsListSubHandler mealsListSubHandler);
	}

	public interface MealsListSubHandler {
		void createMeal(String name);
	}
	private MealsListSubDisplay display;

	private EmptyPresenter emptyPresenter;
	private EmptyPresenter emptyPresenter2;
	private List<MealLinkPresenter> mostPopularPresenters = new ArrayList<MealLinkPresenter>();
	//child presenters
	private List<MealLinkPresenter> myMealPresenters = new ArrayList<MealLinkPresenter>();
	private boolean quickSelectionEnabled;
	private boolean reloadMeals;
	
	private ShowMorePresenter showMorePresenter;
	private ShowMorePresenter showMorePresenter2;
	
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public MealsListSubPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MealsListSubDisplay display, boolean quickSelectionEnabled) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.quickSelectionEnabled = quickSelectionEnabled;
	}
	
	@Override
	public Display getView() {
		return display;
	}
	@Override
	public void onBind() {
		display.setHandler(new MealsListSubHandler() {

			@Override
			public void createMeal(String name) {
				//Create new meal
				MealModel model = new MealModel();

				eventBus.fireEvent(new MealShowEvent(model));
			}
			
		});
		
		//EVENT: meal removed
		addEventHandler(MealRemovedEvent.TYPE, new MealRemovedEventHandler() {
			@Override
			public void onMealRemoved(MealRemovedEvent event) {
				//if meal in list
				if(event.getMeal() != null) {
					if(event.getMeal().getTimeId() == 0) {
						removeMyMealPresenter(event.getMeal());
						removeMostPopularPresenter(event.getMeal());
					}
				}
			}
		});
		
		//EVENT: meal created -> reload meals
		addEventHandler(MealCreatedEvent.TYPE, new MealCreatedEventHandler() {
			@Override
			public void onMealCreated(MealCreatedEvent event) {
				if(event.getMeal().getTimeId() == 0) {
          reloadMeals = true;
		    }
			}
		});
	}


	@Override
	public void onRefresh() {
		//reload if meals created
		if(reloadMeals) {
      loadMyMeals(0);
    }
		
		reloadMeals = false;
	}


	@Override
	public void onRun() {
	    
		loadMyMeals(0);
		loadMostPopularMeals(0);
	}


	@Override
	public void onStop() {

		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
		if(emptyPresenter2 != null) {
      emptyPresenter2.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
      showMorePresenter.stop();
    }	
		if(showMorePresenter2 != null) {
      showMorePresenter2.stop();
    }
		
		//stop presenters
		unbindPresenters(0);
	}


	/**
	 * Removes presenter from most popular meals
	 * @param meal
	 */
	private void removeMostPopularPresenter(MealModel meal) {

		try {
			//remove also from presenters
			for(int i=0; i < mostPopularPresenters.size(); i++) {
				MealLinkPresenter presenter = mostPopularPresenters.get(i);
				if(presenter != null && presenter.meal.getId() == meal.getId()) {
          presenter.stop();
          mostPopularPresenters.remove(presenter);
        }
			}

			//if no meals -> show empty presenter
			if(mostPopularPresenters.size() == 0) {
				if(emptyPresenter2 != null) {
          emptyPresenter2.stop();
		    }
				emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMeals());
				emptyPresenter2.run(display.getMostPopularContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Removes presenter from user meals
	 * @param meal
	 */
	private void removeMyMealPresenter(MealModel meal) {

		try {
			//remove also from presenters
			for(int i=0; i < myMealPresenters.size(); i++) {
				MealLinkPresenter presenter = myMealPresenters.get(i);
				if(presenter != null && presenter.meal.getId() == meal.getId()) {
          presenter.stop();
          myMealPresenters.remove(presenter);
        }
			}

			//if no meals -> show empty presenter
			if(myMealPresenters.size() == 0) {
				if(emptyPresenter != null) {
          emptyPresenter.stop();
		    }
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMeals());
				emptyPresenter.run(display.getMyMealsContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Shows most popular meals
	 * @param list : MealModels
	 * @param openFirst : open first meal
	 */
	private void showMostPopularMeals(final int index, List<MealModel> list) {

		try {

			if(emptyPresenter2 != null) {
        emptyPresenter2.stop();
	    }
			//stop show more
			if(showMorePresenter2 != null) {
        showMorePresenter2.stop();
	    }
			//stop presenters if first items
			if(index == 0) {
        unbindPresenters(2);
	    }
			
			//if no meals
			if(index == 0 && list.size() == 0) {
				emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMeals());
				emptyPresenter2.run(display.getMostPopularContainer());
			}
			else {
				
				for(final MealModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter2 = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMostPopularMeals(index + Constants.LIMIT_MEALS);								
							}
						});
						showMorePresenter2.run(display.getMostPopularContainer());
					}
					else {		
						//new presenter
						final MealLinkPresenter wp = new MealLinkPresenter(rpcService, eventBus, (MealLinkDisplay)GWT.create(MealLinkView.class), m, quickSelectionEnabled);
						addNewMostPopularPresenter(wp);
					}
					
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Shows user's meals
	 * @param list : MealModels
	 */
	private void showMyMeals(final int index, List<MealModel> list) {

		try {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
	    }
			//stop show more
			if(showMorePresenter != null) {
				showMorePresenter.stop();
	    }
			//stop presenters if first items
			if(index == 0) {
				unbindPresenters(1);
	    }
			
			//if no meals
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMeals());
				emptyPresenter.run(display.getMyMealsContainer());
			}
			else {
				
				for(final MealModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMyMeals(index + Constants.LIMIT_MEALS);								
							}
						});
						showMorePresenter.run(display.getMyMealsContainer());
					}
					else {		
						//new presenter
						final MealLinkPresenter wp = new MealLinkPresenter(rpcService, eventBus, (MealLinkDisplay)GWT.create(MealLinkView.class), m, quickSelectionEnabled);
						addNewMyMealPresenter(wp);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Unbinds all the presenters
	 * @param target : which presenters all unbinded, 0=all, 1=mymeals, 2=most popular, 3=search results
	 */
	private void unbindPresenters(int target) {
			
		//my meals
		if(myMealPresenters != null && (target == 0 || target == 1)) {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
	    }
			if(showMorePresenter != null) {
				showMorePresenter.stop();	
	    }
				
			for(int i=0; i < myMealPresenters.size(); i++) {
				final Presenter presenter = myMealPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			myMealPresenters.clear();
		}				
			
		//most popular
		if(mostPopularPresenters != null && (target == 0 || target == 2)) {

			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
	    }
			if(showMorePresenter2 != null) {
				showMorePresenter2.stop();
	    }
			
			for(int i=0; i < mostPopularPresenters.size(); i++) {
				final Presenter presenter = mostPopularPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			mostPopularPresenters.clear();
		}
	}


	/**
	 * Adds new presenter to view (most popular meals)
	 * @param presenter
	 */
	protected void addNewMostPopularPresenter(MealLinkPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
				emptyPresenter2 = null;
			}
			
			mostPopularPresenters.add(presenter);
			presenter.run(display.getMostPopularContainer());
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Adds new presenter to view (my meals)
	 * @param presenter
	 */
	protected void addNewMyMealPresenter(MealLinkPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			myMealPresenters.add(presenter);
			presenter.run(display.getMyMealsContainer());
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads meals
	 */
	void loadMostPopularMeals(final int index) {
    		
		if(emptyPresenter2 != null) {
			emptyPresenter2.stop();
    }
		//stop show more
		if(showMorePresenter2 != null) {
			showMorePresenter2.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters(2);
    }

		//add empty presenter
		emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter2.run(display.getMostPopularContainer());
		
		//load most popular
    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getMostPopularMeals(index, new MyAsyncCallback<List<MealModel>>() {
			@Override
			public void onSuccess(List<MealModel> result) {
				showMostPopularMeals(index, result);
      }
		});
		addRequest(req);
	}

	/**
	 * Loads meals
	 */
	void loadMyMeals(final int index) {
    		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters(1);
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getMyMealsContainer());

    //get meals
    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getMeals(index, new MyAsyncCallback<List<MealModel>>() {
			@Override
			public void onSuccess(List<MealModel> result) {
				showMyMeals(index, result);
      }
		});
		addRequest(req);
	}

}
