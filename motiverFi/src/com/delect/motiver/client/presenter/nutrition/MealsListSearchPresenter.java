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

/**
 * List of meals
 * <br>Launch MealSelectEvent when meal is selected
 * <br>Unbinds itself after event
 */
public class MealsListSearchPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MealsListSearchDisplay extends Display {
	}
	private MealsListSearchDisplay display;

	private EmptyPresenter emptyPresenter;
	private String query = "";
	private boolean quickSelectionEnabled;
	
	//child presenters
	private List<Presenter> searchResultsPresenters = new ArrayList<Presenter>();
	private ShowMorePresenter showMorePresenter;

	public MealsListSearchPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MealsListSearchDisplay display, String query, boolean quickSelectionEnabled) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.query  = query;
		this.quickSelectionEnabled = quickSelectionEnabled;
	}


	/*
	 * VIEW 2 (search results) 
	 */
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onRun() {
	    
    loadSearch(0, query);
	}


	@Override
	public void onStop() {

		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
      showMorePresenter.stop();	
    }
		
		//stop presenters
		unbindSearchPresenters();
	}


	/**
	 * Shows meals (multiple SingleMealPresenters)
	 * @param list : MealModels
	 * @param openFirst : open first meal
	 */
	private void showSearch(final int index, final String query, List<MealModel> list) {

		try {

			if(emptyPresenter != null) {
        emptyPresenter.stop();
	    }
			if(showMorePresenter != null) {
        showMorePresenter.stop();
	    }
			//stop presenters if first items
			if(index == 0) {
        unbindSearchPresenters();
	    }
			
			//if no meals
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMealsFound());
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
				
				for(final MealModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadSearch(index + Constants.LIMIT_WORKOUTS, query);								
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {		
						//new presenter
						final MealLinkPresenter wp = new MealLinkPresenter(rpcService, eventBus, (MealLinkDisplay)GWT.create(MealLinkView.class), m, quickSelectionEnabled);
						addNewSearchResultsPresenter(wp);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Unbinds all the presenters
	 */
	private void unbindSearchPresenters() {
			
		//search results
		if(searchResultsPresenters != null) {

			if(emptyPresenter != null) {
        emptyPresenter.stop();
	    }
			//stop show more
			if(showMorePresenter != null) {
        showMorePresenter.stop();	
	    }
			
			for(int i=0; i < searchResultsPresenters.size(); i++) {
				final Presenter presenter = searchResultsPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			searchResultsPresenters.clear();					
		}
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewSearchResultsPresenter(Presenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			searchResultsPresenters.add(presenter);
			presenter.run(display.getBaseContainer());
		} catch (Exception e) {
			Motiver.showException(e);
		}
	}

	/**
	 * Loads meals
	 */
	void loadSearch(final int index, final String query) {
		
		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
		if(showMorePresenter != null) {
      showMorePresenter.stop();	
    }
		//stop presenters if first items
		if(index == 0) {
      unbindSearchPresenters();
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBaseContainer());

    //search meals
		final Request req = rpcService.searchMeals(index, query, new MyAsyncCallback<List<MealModel>>() {
			@Override
			public void onSuccess(List<MealModel> result) {
				showSearch(index, query, result);
      }
		});
		addRequest(req);
	}

}
