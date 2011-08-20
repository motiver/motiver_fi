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

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Timer;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.GuideValueCreatedEvent;
import com.delect.motiver.client.event.GuideValueRemovedEvent;
import com.delect.motiver.client.event.TimeCreatedEvent;
import com.delect.motiver.client.event.TimeRemovedEvent;
import com.delect.motiver.client.event.TimeUpdatedEvent;
import com.delect.motiver.client.event.handler.GuideValueCreatedEventHandler;
import com.delect.motiver.client.event.handler.GuideValueRemovedEventHandler;
import com.delect.motiver.client.event.handler.TimeCreatedEventHandler;
import com.delect.motiver.client.event.handler.TimeRemovedEventHandler;
import com.delect.motiver.client.event.handler.TimeUpdatedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.GuideValueModel;
import com.delect.motiver.shared.NutritionDayModel;
import com.delect.motiver.shared.TimeModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single day's total calories etc
 * @author Antti
 *
 */
public class TotalsContainerPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class TotalsContainerDisplay extends Display {
		public abstract LayoutContainer getTotalsContainer();
		public abstract void setCurrentGuideValue(GuideValueModel guide);
		public abstract void setData(double energy, double protein, double carb, double fet);
		public abstract void showTotals();
	}
	private Date date;

	private TotalsContainerDisplay display;
	
	//child presenters
	private EmptyPresenter emptyPresenter;
	private GuideValueModel guide;
	private Timer timer;
	
	private List<TimeModel> times;
	private String uid = null;
	
	public TotalsContainerPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, TotalsContainerDisplay display, String uid, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.uid  = uid;
    this.date = date;
	    
    //reset guide
    guide = null;
		
    if(this.date == null) {
      return;
    }
	}
	
	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
		//EVENT: time added
		addEventHandler(TimeCreatedEvent.TYPE, new TimeCreatedEventHandler() {
			@Override
			public void onTimeCreated(TimeCreatedEvent event) {
				if(times == null) {
					return;
	      }
				
				times.add(event.getTime());
				calculateTotals(false);
			}
		});
		//EVENT: time updated
		addEventHandler(TimeUpdatedEvent.TYPE, new TimeUpdatedEventHandler() {
			@Override
			public void onTimeUpdated(TimeUpdatedEvent event) {
				if(times == null) {
					return;
	      }
				
				final TimeModel mUpdated = event.getTime();
				for(TimeModel m : times) {
					if(m.getId() == mUpdated.getId()) {
						m.setEnergy(mUpdated.getEnergy());
						m.setProtein(mUpdated.getProtein());
						m.setCarb(mUpdated.getCarb());
						m.setFet(mUpdated.getFet());
						m.setMeals(mUpdated.getMeals());
						m.setFoods(mUpdated.getFoods());
						break;
					}						
				}
				calculateTotals(false);
			}
		});
		//EVENT: time removed
		addEventHandler(TimeRemovedEvent.TYPE, new TimeRemovedEventHandler() {
			@Override
			public void onTimeRemoved(TimeRemovedEvent event) {
				if(times == null) {
					return;
	      }
				
				final TimeModel mRemoved = event.getTime();
				for(TimeModel m : times) {
					if(m.getId() == mRemoved.getId()) {
						times.remove(m);
						break;
					}
						
				}
				calculateTotals(false);
			}
		});
		
		//EVENT: guide value added
		addEventHandler(GuideValueCreatedEvent.TYPE, new GuideValueCreatedEventHandler() {
			@Override
			public void onGuideValueCreated(GuideValueCreatedEvent event) {
				//reload guide values
				loadGuideValues();
			}
		});
		
		//EVENT: guide value removed
		addEventHandler(GuideValueRemovedEvent.TYPE, new GuideValueRemovedEventHandler() {
			@Override
			public void onGuideValueRemoved(GuideValueRemovedEvent event) {
				//reload guide values if this guide was removed
				if(event.getGuideValue() != null && guide != null && event.getGuideValue().getId() == guide.getId()) {
          loadGuideValues();
	      }
			}
		});
	
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRun() {
		//show loading text until data is set
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
		emptyPresenter.run(display.getTotalsContainer());
	}


	@Override
	public void onStop() {
		
		if(timer != null) {
			timer.cancel();
    }
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
	}


	/**
	 * Sets date and time so presenter can calculate totals from there
	 * @param date
	 * @param times
	 */
	public void setData(Date date, List<TimeModel> times) {
		this.date = date;
		this.times = times;

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		
		calculateTotals(true);
	}


	/**
	 * Sets date and nutritionDayModel so presenter can calculate totals from there
	 * @param date
	 * @param nutritionDayModel
	 */
	public void setData(Date date, NutritionDayModel nutritionDayModel) {
		this.date = date;

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		
		display.setData(nutritionDayModel.getEnergy(), nutritionDayModel.getProtein(), nutritionDayModel.getCarb(), nutritionDayModel.getFet());
	}

	/**
	 * Calculates totals
	 */
	private void calculateTotals(boolean refreshGuideValues) {
		
		double e = 0, p = 0, c = 0, f = 0;
		for(TimeModel t : times) {
			e += t.getEnergy();
			p += t.getProtein();
			c += t.getCarb();
			f += t.getFet();
		}
		display.setData(e, p, c, f);
		
		//reload guide values
		if(refreshGuideValues) {			
			loadGuideValues();
    }
	}

	/**
	 * Loads current days guide values (after 3 sec delay)
	 * <br>Uses timer so when times are updated often -> values are fetched only once
	 */
	private void loadGuideValues() {
		
		//only if our foods and not blog
		if(!uid.equals(AppController.User.getUid()) || AppController.BLOG_UID != null) {
			return;
    }
		
		if(timer != null) {
			timer.cancel();
    }

		//clear old guide values
		display.setCurrentGuideValue(null);
		
		timer = new Timer() {

			@Override
			public void run() {
			  
	      Motiver.setNextCallCacheable(true);
				final Request req = rpcService.getGuideValues(uid, 0, Functions.trimDateToDatabase(date, true), new MyAsyncCallback<List<GuideValueModel>>() {
					@Override
					public void onSuccess(List<GuideValueModel> result) {
						List<GuideValueModel> list = result;
						//get first value
						if(list != null) {
							if(list.size() > 0) {
								guide = list.get(0);
								display.setCurrentGuideValue(guide);
							}
						}
								
					}
				});
				addRequest(req);
			}
			
		};
		timer.schedule(1000);
	}

}
