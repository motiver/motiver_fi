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
package com.delect.motiver.client.presenter;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioValueCreatedEvent;
import com.delect.motiver.client.event.CardioValueRemovedEvent;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.RoutineCreatedEvent;
import com.delect.motiver.client.event.RunValueCreatedEvent;
import com.delect.motiver.client.event.RunValueRemovedEvent;
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.WorkoutMovedEvent;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.handler.CardioValueCreatedEventHandler;
import com.delect.motiver.client.event.handler.CardioValueRemovedEventHandler;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.RoutineCreatedEventHandler;
import com.delect.motiver.client.event.handler.RunValueCreatedEventHandler;
import com.delect.motiver.client.event.handler.RunValueRemovedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutMovedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutRemovedEventHandler;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Shows +1/-2 weeks from given day
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>onDateChanged</b> : DateEvent()<br>
 * <div>Fires after date changes</div>
 * <ul>
 * <li>date : new date</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 *
 */
public class DateWeekSelectorPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class DateWeekSelectorDisplay extends Display {
		/**
		 * Refreshes view (scrolls to correct dates)
		 */
		public abstract void refreshView();
		/**
		 * Sets start date
		 * @param dateStart Date
		 */
		public abstract void setDate(Date dateStart);
		/**
		 * Sets selected date
		 * @param date Date
		 */
		public abstract void setDateSelected(Date date);
		/**
		 * Sets handler
		 * @param handler DateWeekSelectorHandler
		 */
		public abstract void setHandler(DateWeekSelectorHandler handler);
		/**
		 * Sets markers for each day. Single marker is true if day has training/nutrition.
		 * @param markers boolean[]
		 */
		public abstract void setMarkers(boolean[] markers);
    /**
     * How many days are shown at once
     * @return
     */
    public abstract int getTotalDays();
	}
	/** Handler for this presenter.
	 */
	public interface DateWeekSelectorHandler {
		/**
		 * View calls when date is selected
		 * @param startDate Date
		 * @param dateSelected Date
		 */
		void dateSelected(Date startDate, Date dateSelected);
	}
	private Date dateSelected;
	
	private Date dateStart;
	private DateWeekSelectorDisplay display;
	private boolean showNutrition;
	private boolean showTraining;

	private Timer timer;

	/**
	 * Constructor for DateWeekSelectorPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display DateWeekSelectorDisplay
	 */
	public DateWeekSelectorPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, DateWeekSelectorDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		//when date selected -> fire event
		display.setHandler(new DateWeekSelectorHandler() {

			@Override
			public void dateSelected(Date dateSt, Date dateSel) {
								
				//fire event
				final DateChangedEvent event = new DateChangedEvent(dateSel);
				fireEvent(event);
			}
		});

		//EVENT: new workout created -> refresh view
		addEventHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {
			@Override
			public void onWorkoutCreated(WorkoutCreatedEvent event) {
				final WorkoutModel model = event.getWorkout();

				if(model.getDate() != null) {
          load();
		    }
			}
		});
		
		//EVENT: workout moved -> refresh view
		addEventHandler(WorkoutMovedEvent.TYPE, new WorkoutMovedEventHandler() {
			@Override
			public void onWorkoutMoved(WorkoutMovedEvent event) {
				load();
			}
		});
		//EVENT: new workout created -> refresh view
		addEventHandler(WorkoutRemovedEvent.TYPE, new WorkoutRemovedEventHandler() {
			@Override
			public void onWorkoutRemoved(WorkoutRemovedEvent event) {
				final WorkoutModel model = event.getWorkout();

				if(model.getDate() != null) {
          load();
		    }
			}
		});
		//EVENT: new routine created -> refresh view
		addEventHandler(RoutineCreatedEvent.TYPE, new RoutineCreatedEventHandler() {
			@Override
			public void onRoutineCreated(RoutineCreatedEvent event) {
				final RoutineModel model = event.getRoutine();

				if(model.getDate() != null) {
          load();
		    }
			}
		});
		//EVENT: new cardio value created -> refresh view
		addEventHandler(CardioValueCreatedEvent.TYPE, new CardioValueCreatedEventHandler() {
			@Override
			public void onCardioValueCreated(CardioValueCreatedEvent event) {
				load();
			}
		});
		//EVENT: new run value created -> refresh view
		addEventHandler(RunValueCreatedEvent.TYPE, new RunValueCreatedEventHandler() {
			@Override
			public void onRunValueCreated(RunValueCreatedEvent event) {
				load();
			}
		});
		//EVENT: new cardio value created -> refresh view
		addEventHandler(CardioValueRemovedEvent.TYPE, new CardioValueRemovedEventHandler() {
			@Override
			public void onCardioValueRemoved(CardioValueRemovedEvent event) {
				load();
			}
		});
		//EVENT: new run value created -> refresh view
		addEventHandler(RunValueRemovedEvent.TYPE, new RunValueRemovedEventHandler() {
			@Override
			public void onRunValueRemoved(RunValueRemovedEvent event) {
				load();
			}
		});
		
		//EVENT: reload view when date changes
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {

				final Date dateStartOld = dateStart;
				
				//refresh
				setParameters(event.getDate(), showTraining, showNutrition);

				//if changed
				final DateTimeFormat fmt = DateTimeFormat.getFormat("y-M-d");
				if(!fmt.format(dateStartOld).equals(fmt.format(dateStart))) {
          load();
		    }
			}
		});
	}

	@Override
	public void onRun() {
	    
		if(timer != null) {
      timer.cancel();
    }
		
		//small delay so everything else is loaded first
		timer = new Timer() {
			@Override
			public void run() {
        load();
			}
		};
		timer.schedule(1000);
	}


	@Override
	public void onStop() {
		if(timer != null) {
      timer.cancel();
    }
	}

	/**
	 * Method setParameters.
	 * @param date Date
	 * @param showTraining boolean
	 * @param showNutrition boolean
	 */
	public void setParameters(Date date, boolean showTraining, boolean showNutrition) {

		try {
			dateSelected = date;
			display.setDateSelected(dateSelected);
			
			//set date to start one week before selected date
			//only change start date if is it not visible
			if(dateStart == null || dateSelected == null || dateSelected.getTime() < dateStart.getTime() || (dateSelected.getTime() / 1000) > (dateStart.getTime() / 1000 + 3600 * 24 * 21)) {
				final long d = Functions.findPreviousMonday(date.getTime() / 1000 - 3600 * 24 * 7);
				dateStart = new Date(d * 1000);
				display.setDate(dateStart);
			}

			this.showTraining = showTraining;
			this.showNutrition = showNutrition;
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		//reload view
		display.refreshView();
			
		
	}

	/**
	 * Loads data from server
	 */
	private void load() {
		
    final Date dateEnd = new Date(dateStart.getTime() + 1000 * 3600 * 24 * (display.getTotalDays() - 1));
		
		//fetch workouts
    if(showTraining) {
      Motiver.setNextCallCacheable(true);
			final Request req = rpcService.getBlogData(0, 30, 5, Functions.trimDateToDatabase(dateStart, true), Functions.trimDateToDatabase(dateEnd, true), String.valueOf(AppController.User.getUid()), true, new MyAsyncCallback<List<BlogData>>() {
				@Override
				public void onSuccess(List<BlogData> list) {
				  
				  if(list == null) {
            return;
				  }
					
				  //save data to markers array (data needs to be inverted)
					final boolean[] markers = new boolean[21];
					
					int i = list.size() - 1;
					for(BlogData bd : list) {
					  final boolean hasTraining = (bd != null) && (bd.getWorkouts().size() > 0 || bd.getCardios().size() > 0 || bd.getRuns().size() > 0);
					  markers[i] = hasTraining;
					  
					  i--;
					}
					
					display.setMarkers(markers);
				}
			});
			addRequest(req);
    }

		//fetch nutrition
    if(showNutrition) {
      Motiver.setNextCallCacheable(true);
			final Request req = rpcService.getBlogData(0, 30, 2, Functions.trimDateToDatabase(dateStart, true), Functions.trimDateToDatabase(dateEnd, true), String.valueOf(AppController.User.getUid()), true, new MyAsyncCallback<List<BlogData>>() {
				@Override
				public void onSuccess(List<BlogData> list) {
          
          if(list == null) {
            return;
          }
          
          //save data to markers array (data needs to be inverted)
          final boolean[] markers = new boolean[21];
          
          int i = list.size() - 1;
          for(BlogData bd : list) {
            final boolean hasNutrition = (bd != null) && (bd.getNutrition() != null);
            markers[i] = hasNutrition;
            
            i--;
          }
          
          display.setMarkers(markers);
				}
			});
			addRequest(req);
    }
	    
	}

}
