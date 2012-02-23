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
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.TimeRemovedEvent;
import com.delect.motiver.client.event.TimeUpdatedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.TimeRemovedEventHandler;
import com.delect.motiver.client.event.handler.TimeUpdatedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.nutrition.MicroNutrientLinkPresenter.MicroNutrientLinkDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.nutrition.MicroNutrientLinkView;
import com.delect.motiver.shared.MicroNutrientModel;
import com.delect.motiver.shared.util.CommonUtils;

/**
 * Shows single day's detailed info (micronutrients, etc...)
 * @author Antti
 *
 */
public class NutritionDayDetailsPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class NutritionDayDetailsDisplay extends Display {
		public abstract void setDate(Date date);
	}
	private Date date;

	private NutritionDayDetailsDisplay display;
	//child presenters
	private EmptyPresenter emptyPresenter;
	
	private List<MicroNutrientLinkPresenter> presenters = new ArrayList<MicroNutrientLinkPresenter>();
	private String uid;
	
	public NutritionDayDetailsPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NutritionDayDetailsDisplay display, String uid, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.uid  = uid;
    this.date = date;
		
    if(this.date == null) {
      return;
    }
	}


	@Override
	public Display getView() {
		return display;
	}


	/**
	 * Loads micronutrients
	 */
	public void loadMicroNutrients() {

		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
		}
		
		for(MicroNutrientLinkPresenter p : presenters)
    p.stop();
		presenters.clear();
		
		//show loading text until data is set
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
		emptyPresenter.run(display.getBaseContainer());
		
		//load micronutrients
		final Request req = rpcService.getMicroNutrientsInCalendar(uid, CommonUtils.trimDateToDatabase(date, true), new MyAsyncCallback<List<MicroNutrientModel>>() {
			@Override
			public void onSuccess(List<MicroNutrientModel> list) {
				showMicroNutrients(list);
			}
		});
		addRequest(req);
	}
	
	@Override
	public void onBind() {
		display.setDate(date);
		
		//EVENT: date changed
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				date = event.getDate();
				display.setDate(date);
				
				loadMicroNutrients();
			}
		});
		//EVENT: time updated
		addEventHandler(TimeUpdatedEvent.TYPE, new TimeUpdatedEventHandler() {
			@Override
			public void onTimeUpdated(TimeUpdatedEvent event) {
				loadMicroNutrients();
			}
		});
		//EVENT: time removed
		addEventHandler(TimeRemovedEvent.TYPE, new TimeRemovedEventHandler() {
			@Override
			public void onTimeRemoved(TimeRemovedEvent event) {
				loadMicroNutrients();
			}
		});

	}

	@Override
	public void onRun() {
		
		loadMicroNutrients();
		
	}


	@Override
	public void onStop() {
		for(MicroNutrientLinkPresenter p : presenters) {
			p.stop();
		}
		presenters.clear();
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
	}

	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(MicroNutrientLinkPresenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}

		presenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}

	protected void showMicroNutrients(List<MicroNutrientModel> list) {
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		if(list.size() == 0) {
			emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoMicroNutrients(), EmptyPresenter.OPTION_SMALLER_LEFT_ALIGN);
			emptyPresenter.run(display.getBaseContainer());
		}
		else {
			for(MicroNutrientModel m : list) {
				MicroNutrientLinkPresenter presenter = new MicroNutrientLinkPresenter(rpcService, eventBus, (MicroNutrientLinkDisplay)GWT.create(MicroNutrientLinkView.class), m);
				addNewPresenter(presenter);
			}
		}
	}

}
