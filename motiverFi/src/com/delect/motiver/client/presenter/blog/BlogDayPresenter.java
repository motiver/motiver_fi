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
package com.delect.motiver.client.presenter.blog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.event.CardioValueRemovedEvent;
import com.delect.motiver.client.event.MeasurementValueRemovedEvent;
import com.delect.motiver.client.event.RunValueRemovedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.handler.CardioValueRemovedEventHandler;
import com.delect.motiver.client.event.handler.MeasurementValueRemovedEventHandler;
import com.delect.motiver.client.event.handler.RunValueRemovedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.CardioValueLinkPresenter;
import com.delect.motiver.client.presenter.cardio.CardioValueLinkPresenter.CardioValueLinkDisplay;
import com.delect.motiver.client.presenter.cardio.RunValueLinkPresenter;
import com.delect.motiver.client.presenter.cardio.RunValueLinkPresenter.RunValueLinkDisplay;
import com.delect.motiver.client.presenter.nutrition.NutritionDayLinkPresenter;
import com.delect.motiver.client.presenter.nutrition.NutritionDayLinkPresenter.NutritionDayLinkDisplay;
import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter;
import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter.NutritionDayDisplay;
import com.delect.motiver.client.presenter.profile.MeasurementValueLinkPresenter;
import com.delect.motiver.client.presenter.profile.MeasurementValueLinkPresenter.MeasurementValueLinkDisplay;
import com.delect.motiver.client.presenter.training.WorkoutLinkPresenter;
import com.delect.motiver.client.presenter.training.WorkoutLinkPresenter.WorkoutLinkDisplay;
import com.delect.motiver.client.presenter.training.WorkoutPresenter;
import com.delect.motiver.client.presenter.training.WorkoutPresenter.WorkoutDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.cardio.CardioValueLinkView;
import com.delect.motiver.client.view.cardio.RunValueLinkView;
import com.delect.motiver.client.view.nutrition.NutritionDayLinkView;
import com.delect.motiver.client.view.nutrition.NutritionDayView;
import com.delect.motiver.client.view.profile.MeasurementValueLinkView;
import com.delect.motiver.client.view.training.WorkoutLinkView;
import com.delect.motiver.client.view.training.WorkoutView;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.MeasurementValueModel;
import com.delect.motiver.shared.RunValueModel;
import com.delect.motiver.shared.WorkoutModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * Single day in blog
 */
public class BlogDayPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class BlogDayDisplay extends Display {
		/**
		 * Returns container for body
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getBodyContainer();
		/**
		 * Sets date for this this day.
		 * @param date Date
		 */
		public abstract void setDate(Date date);
		/**
		 * Sets if only links are shown.
		 * @param showOnlyLinks boolean
		 */
		public abstract void showOnlyLinks(boolean showOnlyLinks);
	}
	private BlogData data;

	private BlogDayDisplay display;
	private EmptyPresenter emptyPresenter;
	private List<Presenter> presenters = new ArrayList<Presenter>();

	private boolean showOnlyLinks = false;

	/**
	 * Single day in blog
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param data
	 * @param showOnlyLinks : if only links are shown (not 'open' workouts, etc...)
	 */
	public BlogDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, BlogDayDisplay display, BlogData data, boolean showOnlyLinks) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.data = data;
    this.showOnlyLinks = showOnlyLinks;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setDate(data.getDate());
		display.showOnlyLinks(showOnlyLinks);

		//EVENT: workout value removed
		addEventHandler(WorkoutRemovedEvent.TYPE, new WorkoutRemovedEventHandler() {
			@Override
			public void onWorkoutRemoved(WorkoutRemovedEvent event) {
				//if this day
				boolean found = false;
				for(WorkoutModel m : data.getWorkouts()) {
					if(event.getWorkout().equals(m)) {
						data.getWorkouts().remove(m);
						found = true;
						break;
					}
				}
				//check if day empty
				if(found) {
          stopIfEmpty();
        }
			}
		});
		//EVENT: cardio value removed
		addEventHandler(CardioValueRemovedEvent.TYPE, new CardioValueRemovedEventHandler() {
			@Override
			public void onCardioValueRemoved(CardioValueRemovedEvent event) {
				//if this day
				boolean found = false;
				for(CardioValueModel m : data.getCardios()) {
					if(event.getCardioValue().equals(m)) {
						data.getCardios().remove(m);
						found = true;
						break;
					}
				}
				//check if day empty
				if(found) {
          stopIfEmpty();
        }
			}
		});
		//EVENT: run value removed
		addEventHandler(RunValueRemovedEvent.TYPE, new RunValueRemovedEventHandler() {
			@Override
			public void onRunValueRemoved(RunValueRemovedEvent event) {
				//if this day
				boolean found = false;
				for(RunValueModel m : data.getRuns()) {
					if(event.getRunValue().equals(m)) {
						data.getRuns().remove(m);
						found = true;
						break;
					}
				}
				//check if day empty
				if(found) {
          stopIfEmpty();
        }
			}
		});
		//EVENT: measurement value removed
		addEventHandler(MeasurementValueRemovedEvent.TYPE, new MeasurementValueRemovedEventHandler() {
			@Override
			public void onMeasurementValueRemoved(MeasurementValueRemovedEvent event) {
				//if this day
				boolean found = false;
				for(MeasurementValueModel m : data.getMeasurements()) {
					if(event.getMeasurementValue().equals(m)) {
						data.getMeasurements().remove(m);
						found = true;
						break;
					}
				}
				//check if day empty
				if(found) {
          stopIfEmpty();
        }
			}
		});
	}

	@Override
	public void onRun() {
	    
    showData();
  }

	@Override
	public void onStop() {
		//stop presenters
		unbindPresenters();
	}

	/**
	 * Unbinds all the presenters
	 */
	private void unbindPresenters() {
		
		try {
			
			if(emptyPresenter != null) {
        emptyPresenter.stop();
      }

			if(presenters != null) {
				
				for(int i=0; i < presenters.size(); i++) {
					try {
						final Presenter presenter = presenters.get(i);
						if(presenter != null) {
							presenter.stop();
						}
					} catch (Exception e) {
			      Motiver.showException(e);
					}
				}
				presenters = new ArrayList<Presenter>();
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(Presenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			presenters.add(presenter);
			presenter.run(display.getBodyContainer());
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Shows all data
	 */
	protected void showData() {

		try {
			unbindPresenters();

			//workouts
			if(data.getWorkouts() != null) {
				for(WorkoutModel w : data.getWorkouts()) {
					//if only link
					if(showOnlyLinks) {
						final WorkoutLinkPresenter wp = new WorkoutLinkPresenter(rpcService, eventBus, (WorkoutLinkDisplay)GWT.create(WorkoutLinkView.class), w, false);
						addNewPresenter(wp);
					}
					else {
						final WorkoutPresenter wp = new WorkoutPresenter(rpcService, eventBus, (WorkoutDisplay)GWT.create(WorkoutView.class), w);
						addNewPresenter(wp);
					}
				}
			}
			
			//nutrition
			if(data.getNutrition() != null) {
				//if only link
				if(showOnlyLinks) {
					//set date
					data.getNutrition().setDate(CommonUtils.getDateGmt(data.getDate()));
					final NutritionDayLinkPresenter wp = new NutritionDayLinkPresenter(rpcService, eventBus, (NutritionDayLinkDisplay)GWT.create(NutritionDayLinkView.class), data.getNutrition());
					addNewPresenter(wp);
				}
				else {
					final NutritionDayPresenter wp = new NutritionDayPresenter(rpcService, eventBus, (NutritionDayDisplay)GWT.create(NutritionDayView.class), data.getUser().getUid(), CommonUtils.getDateGmt(data.getDate()), data.getNutrition());
					addNewPresenter(wp);
				}
			}

			if(data.getMeasurements() != null) {
				//measurement
				for(MeasurementValueModel m : data.getMeasurements()) {
					final MeasurementValueLinkPresenter wp = new MeasurementValueLinkPresenter(rpcService, eventBus, (MeasurementValueLinkDisplay)GWT.create(MeasurementValueLinkView.class), m);
					addNewPresenter(wp);
				}
			}
			
			//cardio
			if(data.getCardios() != null) {
				for(CardioValueModel m : data.getCardios()) {
					final CardioValueLinkPresenter wp = new CardioValueLinkPresenter(rpcService, eventBus, (CardioValueLinkDisplay)GWT.create(CardioValueLinkView.class), m);
					addNewPresenter(wp);
				}
			}
			
			//run
			if(data.getRuns() != null) {
				for(RunValueModel m : data.getRuns()) {
					final RunValueLinkPresenter wp = new RunValueLinkPresenter(rpcService, eventBus, (RunValueLinkDisplay)GWT.create(RunValueLinkView.class), m);
					addNewPresenter(wp);
				}
			}
		    
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Stops presenter if no data left (all removed)
	 */
	protected void stopIfEmpty() {
		if(data.getWorkouts().size() == 0 && data.getNutrition() == null && data.getMeasurements().size() == 0 && data.getCardios().size() == 0 && data.getRuns().size() == 0) {
			stop();
		}
	}
}
