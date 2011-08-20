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
import com.delect.motiver.client.event.GuideValueCreatedEvent;
import com.delect.motiver.client.event.GuideValueRemovedEvent;
import com.delect.motiver.client.event.handler.GuideValueCreatedEventHandler;
import com.delect.motiver.client.event.handler.GuideValueRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.nutrition.AddNewGuidePresenter.AddNewGuideDisplay;
import com.delect.motiver.client.presenter.nutrition.GuideValuePresenter.GuideValueDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.nutrition.AddNewGuideView;
import com.delect.motiver.client.view.nutrition.GuideValueView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.GuideValueModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single guide value on title and when expanded user can set new values
 */
public class GuideValuesListPresenter extends Presenter {

	public interface GuideValuesHandler {
		void newValue();
		void valuesHidden();
		void valuesVisible();
	}

	/**
	* Abstract class for view to extend
	*/
	public abstract static class GuideValuesListDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setHandler(GuideValuesHandler guideValuesHandler);
	}
	private AddNewGuidePresenter addNewGuidePresenter;

	private GuideValuesListDisplay display;
	private EmptyPresenter emptyPresenter;
	private ShowMorePresenter showMorePresenter;
	//child presenters	
	private List<GuideValuePresenter> valuePresenters = new ArrayList<GuideValuePresenter>();
	
	public GuideValuesListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, GuideValuesListDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}

	@Override
	public Display getView() {
		return display;
	}	

	@Override
	public void onBind() {
		
		display.setHandler(new GuideValuesHandler() {
			@Override
			public void newValue() {
				
				addNewGuidePresenter = new AddNewGuidePresenter(rpcService, eventBus, (AddNewGuideDisplay)GWT.create(AddNewGuideView.class));
				addNewGuidePresenter.run(display.getBodyContainer());
			}
			@Override
			public void valuesHidden() {

				if(emptyPresenter != null) {
          emptyPresenter.stop();
		    }
				if(showMorePresenter != null) {
          showMorePresenter.stop();	
		    }
				unbindPresenters();
			}	
			@Override
			public void valuesVisible() {
				loadValues(0);
			}
		});
		
		//EVENT: guide value added
		addEventHandler(GuideValueCreatedEvent.TYPE, new GuideValueCreatedEventHandler() {
			@Override
			public void onGuideValueCreated(GuideValueCreatedEvent event) {
				//reload guide values
				loadValues(0);
			}
		});
		//EVENT: guide value removed
		addEventHandler(GuideValueRemovedEvent.TYPE, new GuideValueRemovedEventHandler() {
			@Override
			public void onGuideValueRemoved(GuideValueRemovedEvent event) {
				if(event.getGuideValue() != null) {
          removePresenter(event.getGuideValue());
		    }
			}
		});
	}


	@Override
	public void onRefresh() {
		if(emptyPresenter != null) {
      emptyPresenter.run(display.getBodyContainer());
    }
		if(showMorePresenter != null) {
      showMorePresenter.run(display.getBodyContainer());	
    }
		
		if(valuePresenters != null) {
			
			for(int i=0; i < valuePresenters.size(); i++) {
				final Presenter presenter = valuePresenters.get(i);
				if(presenter != null) {
					presenter.run(display.getBodyContainer());
				}
			}				
		}
		
		//close add new window
		if(addNewGuidePresenter != null) {
      addNewGuidePresenter.stop();
    }
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
		if(addNewGuidePresenter != null) {
      addNewGuidePresenter.stop();	
    }
		//stop presenters
		unbindPresenters();
	}

	/**
	 * Removes presenter from view
	 * @param meal
	 */
	private void removePresenter(GuideValueModel value) {

		try {
			//remove also from presenters
			for(int i=0; i < valuePresenters.size(); i++) {
				GuideValuePresenter presenter = valuePresenters.get(i);
				if(presenter != null && presenter.value.getId() == value.getId()) {
          valuePresenters.remove(presenter);
		    }
			}

			//if no meals/foods -> show empty presenter
			if(valuePresenters.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoValues());
				emptyPresenter.run(display.getBodyContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {		
		if(valuePresenters != null) {
			for(int i=0; i < valuePresenters.size(); i++) {
				final Presenter presenter = valuePresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			valuePresenters.clear();					
		}
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(GuideValuePresenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		valuePresenters.add(presenter);
		presenter.run(display.getBodyContainer());
	}
	/**
	 * Shows foods
	 */
	protected void showValues(final int index, List<GuideValueModel> list) {

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
        unbindPresenters();
	    }
			
			//if no values
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoValues());
				emptyPresenter.run(display.getBodyContainer());
			}
			else {
				
				for(GuideValueModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadValues(index + Constants.LIMIT_GUIDE_VALUES);								
							}
						});
						showMorePresenter.run(display.getBodyContainer());
					}
					else {
						final GuideValuePresenter wp = new GuideValuePresenter(rpcService, eventBus, (GuideValueDisplay)GWT.create(GuideValueView.class), m);
						addNewPresenter(wp);
					}
				}
			}

		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}

	/**
	 * Loads values
	 */
	void loadValues(final int index) {
    		
		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
      showMorePresenter.stop();
    }
		//stop presenters if first items
		if(index == 0) {
      unbindPresenters();
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
		
		//fetch foods
		final Request req = rpcService.getGuideValues(AppController.User.getUid(), index, null, new MyAsyncCallback<List<GuideValueModel>>() {
			@Override
			public void onSuccess(List<GuideValueModel> result) {
				showValues(index, result);
      }
		});
		addRequest(req);
	}
}
