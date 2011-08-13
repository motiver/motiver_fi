/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.cardio;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioRemovedEvent;
import com.delect.motiver.client.event.handler.CardioRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.cardio.CardioPresenter.CardioDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.cardio.CardioView;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;

/**
 * Shows cardios in list
 * @author Antti
 *
 */
public class CardiosListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CardiosListDisplay extends Display {
	}
	//new workout handler
	public Listener<BaseEvent> NewCardioListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewCardio();
		}
	};

	private long cardioId = 0;
	//child presenters
	private List<CardioPresenter> cardioPresenters = new ArrayList<CardioPresenter>();
	private EmptyPresenter emptyPresenter;
	
	private ShowMorePresenter showMorePresenter;
	CardiosListDisplay display;


	/**
	 * Shows cardios in list
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public CardiosListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CardiosListDisplay display, long cardioId) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.cardioId = cardioId;
	}
	 
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
	
		//EVENT: cardio removed
		addEventHandler(CardioRemovedEvent.TYPE, new CardioRemovedEventHandler() {
			@Override
			public void onCardioRemoved(CardioRemovedEvent event) {
				//if workout in list
				if(event.getCardio() != null) {
          removePresenter(event.getCardio());
	      }
			}
		});
	}


	@Override
	public void onRefresh() {
	  
		if(cardioPresenters != null) {
			for(int i=0; i < cardioPresenters.size(); i++) {
				final Presenter presenter = cardioPresenters.get(i);
				if(presenter != null) {
					presenter.run(display.getBaseContainer());
				}
			}
		}
	}


	@Override
	public void onRun() {
		
    //load cardios
    loadCardios(0);
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
		unbindPresenters();
	}
	
	/**
	 * Removes presenter from view
	 * @param cardio
	 */
	private void removePresenter(CardioModel cardio) {

		try {
			//remove also from presenters
			for(int i=0; i < cardioPresenters.size(); i++) {
				Presenter presenter = cardioPresenters.get(0);
				if(presenter != null && ((CardioPresenter)presenter).cardio.getId() == cardio.getId()) {
          cardioPresenters.remove(presenter);
        }
			}

			//if no cardios/foods -> show empty presenter
			if(cardioPresenters.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoCardios());
				emptyPresenter.run(display.getBaseContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Shows cardios (multiple CardioPresenters)
	 * @param list : CardioModels
	 * @param openFirst : open first cardio
	 */
	private void showCardios(final int index, List<CardioModel> list, boolean openFirst) {

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
			
			//if no cardios
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoCardios());
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
				
				for(CardioModel m : list) {
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadCardios(index + Constants.LIMIT_CARDIOS);
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {
						//if id found or only item
						boolean openAsDefault = (m.getId() == cardioId || list.size() == 1);
						final CardioPresenter mp = new CardioPresenter(rpcService, eventBus, (CardioDisplay)GWT.create(CardioView.class), m, true, openAsDefault);
						addNewPresenter(mp);
					}
					
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Unbinds all the cardio presenters
	 */
	private void unbindPresenters() {
		
		if(cardioPresenters != null) {
			for(int i=0; i < cardioPresenters.size(); i++) {
				final Presenter presenter = cardioPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			cardioPresenters.clear();
		}
	}


	/**
	 * Adds dummy cardio
	 */
	protected void addNewCardio() {
		//create empty measurePresenter
		CardioModel dummy = new CardioModel();
		dummy.setName("");
		final CardioPresenter mp = new CardioPresenter(rpcService, eventBus, (CardioDisplay)GWT.create(CardioView.class), dummy, true, true);
		addNewPresenter(mp);
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(CardioPresenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		cardioPresenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}

	/**
	 * Loads values
	 */
	void loadCardios(final int index) {
    		
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
		emptyPresenter.run(display.getBaseContainer());

    //get cardios
		if(index == 0) {
		  Motiver.setNextCallCacheable(true);
		}
		final Request req = rpcService.getCardios(index, new MyAsyncCallback<List<CardioModel>>() {
			@Override
			public void onSuccess(List<CardioModel> result) {
				showCardios(index, result, false);
      }
		});
		addRequest(req);
	}

}
