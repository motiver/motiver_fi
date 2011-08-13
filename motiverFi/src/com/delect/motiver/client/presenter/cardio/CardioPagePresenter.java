/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.cardio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.NotePanelPresenter;
import com.delect.motiver.client.presenter.NotePanelPresenter.NotePanelDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.CardiosListPresenter.CardiosListDisplay;
import com.delect.motiver.client.presenter.cardio.RunsListPresenter.RunsListDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.NotePanelView;
import com.delect.motiver.client.view.cardio.CardiosListView;
import com.delect.motiver.client.view.cardio.RunsListView;

/**
 * 
 * Cardio page
 *  - cardio
 *  - run
 */
public class CardioPagePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class CardioPageDisplay extends Display {
	}
	//if some cardio/run is open as default (zero if not)
	private long cid = 0;

	private CardioPageDisplay display;
	//child presenters
	private NotePanelPresenter notePanelCardios;
	
	private NotePanelPresenter notePanelRuns;
	private long rid = 0;
	
	/**
	 * Measurements page
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public CardioPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CardioPageDisplay display) {
		super(rpcService, eventBus);
		this.display = display;

    notePanelCardios = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
    notePanelRuns = new NotePanelPresenter(rpcService, eventBus, (NotePanelDisplay)GWT.create(NotePanelView.class));
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		//check if cardio/run id in token
		cid = 0;
		rid = 0;
		try {
			String token = History.getToken();
			if(token.matches("user/cardio/.*")) {
				String[] arr = token.split("/");
				final String str = arr[arr.length - 1];
				//if workout
				if(str.contains("c")) {
          cid = Long.parseLong(str.replace("c", ""));
	      }
				//if routine
				else if(str.contains("r")) {
          rid = Long.parseLong(str.replace("r", ""));
	      }
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		//set token
		if(cid == 0 && rid ==  0) {
      History.newItem("user/cardio", false);
    }
		
	}


	@Override
	public void onRefresh() {
		//refresh childs
		if(notePanelCardios != null) {
      notePanelCardios.run(display.getBaseContainer());
    }
		
		if(notePanelRuns != null) {
      notePanelRuns.run(display.getBaseContainer());
    }
	}

	@Override
	public void onRun() {
	    
    //cardios
    notePanelCardios.run(display.getBaseContainer());
    //add meal list to notepanel
    CardiosListPresenter cardiosListPresenter = new CardiosListPresenter(rpcService, eventBus, (CardiosListDisplay)GWT.create(CardiosListView.class), cid);
    notePanelCardios.setTitle(AppController.Lang.Cardios());
    notePanelCardios.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Cardio().toLowerCase()), cardiosListPresenter.NewCardioListener);
    notePanelCardios.addNewPresenter(cardiosListPresenter);
    if(rid == 0) {
      notePanelCardios.showContent();
    }
	    
    //runs
    notePanelRuns.run(display.getBaseContainer());
    //add meal list to notepanel
    RunsListPresenter runsListPresenter = new RunsListPresenter(rpcService, eventBus, (RunsListDisplay)GWT.create(RunsListView.class), rid);
    notePanelRuns.setTitle(AppController.Lang.Runs());
    notePanelRuns.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Run().toLowerCase()), runsListPresenter.NewRunListener);
    notePanelRuns.addNewPresenter(runsListPresenter);
    if(cid == 0) {
      notePanelRuns.showContent();
    }
	    	
	}

	@Override
	public void onStop() {
		
		if(notePanelCardios != null) {
			notePanelCardios.stop();
    }
		
		if(notePanelRuns != null) {
			notePanelRuns.stop();
    }
	}

}
