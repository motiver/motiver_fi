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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.event.TimeCreatedEvent;
import com.delect.motiver.client.event.TimeRemovedEvent;
import com.delect.motiver.client.event.TimeUpdatedEvent;
import com.delect.motiver.client.event.handler.DateChangedEventHandler;
import com.delect.motiver.client.event.handler.TimeCreatedEventHandler;
import com.delect.motiver.client.event.handler.TimeRemovedEventHandler;
import com.delect.motiver.client.event.handler.TimeUpdatedEventHandler;
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentsBoxDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.nutrition.EmptyNutritionDayPresenter.EmptyNutritionDayDisplay;
import com.delect.motiver.client.presenter.nutrition.GuideValuesListPresenter.GuideValuesListDisplay;
import com.delect.motiver.client.presenter.nutrition.NutritionDayDetailsPresenter.NutritionDayDetailsDisplay;
import com.delect.motiver.client.presenter.nutrition.TimePresenter.TimeDisplay;
import com.delect.motiver.client.presenter.nutrition.TotalsContainerPresenter.TotalsContainerDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentsBoxView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.nutrition.EmptyNutritionDayView;
import com.delect.motiver.client.view.nutrition.GuideValuesListView;
import com.delect.motiver.client.view.nutrition.NutritionDayDetailsView;
import com.delect.motiver.client.view.nutrition.TimeView;
import com.delect.motiver.client.view.nutrition.TotalsContainerView;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.NutritionDayModel;
import com.delect.motiver.shared.TimeModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * <pre>
 * Init one day in calendar (nutrition)
 *  - todays foods/meals
 *  </pre>
 */
public class NutritionDayPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class NutritionDayDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract LayoutContainer getCommentsContainer();
		public abstract LayoutContainer getDetailsContainer();
		public abstract LayoutContainer getGuideContainer();
		public abstract LayoutContainer getTotalsContainer();
		public abstract void onScroll(int scrollTop);
		public abstract void removeAllFromTotals();
		public abstract void setAddButtonVisible(boolean b);
		public abstract void setDate(Date date);
		public abstract void setFoodsEnabled(boolean foodsPermission);
		public abstract void setHandler(NutritionDayHandler todayNutritionHandler);
		public abstract void showContent();
	}

	public interface NutritionDayHandler {
		void detailsHidden();
		void detailsVisible();
		void newTime();
		void removeTimes();
		void timesHidden();
		void timesVisible();
	}
	//new time handler
	public Listener<BaseEvent> NewTimeListener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			addNewTime();
		}
	};

	private CommentsBoxPresenter commentsPresenter;
	private Date date;
	private NutritionDayDisplay display;
	private Presenter emptyPresenter;
	private GuideValuesListPresenter guideValuesPresenter;
	
	private NutritionDayModel nutritionDayModel;
	private boolean showGuides;
	
	//child presenters
	private List<TimePresenter> timePresenters = new ArrayList<TimePresenter>();

	private TotalsContainerPresenter totalsContainerPresenter;
	private String uid;
	protected NutritionDayDetailsPresenter nutritionDayDetails;
	protected List<TimeModel> times = new ArrayList<TimeModel>();
	
	/**
	 * Loads times from server and shows them by default. Guides are shown
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param uid : who's foods
	 * @param date : which date
	 * @param showGuides : if guide values are shown
	 */
	public NutritionDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NutritionDayDisplay display, String uid, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.uid = uid;
    this.date = date;
    showGuides = true;
	    
    totalsContainerPresenter = new TotalsContainerPresenter(rpcService, eventBus, (TotalsContainerDisplay)GWT.create(TotalsContainerView.class), uid, date);
    if(uid.equals(AppController.User.getUid()) && showGuides) {
      guideValuesPresenter = new GuideValuesListPresenter(rpcService, eventBus, (GuideValuesListDisplay)GWT.create(GuideValuesListView.class));
    }
	}
	
	/**
	 * Shows total info, but times are not visible. Guide values are not shown
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param uid : who's foods
	 * @param date : which date
	 * @param nutritionDayModel : model containing total calories, protein, ...
	 */
	public NutritionDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, NutritionDayDisplay display, String uid, Date date, NutritionDayModel nutritionDayModel) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.uid = uid;
    this.date = date;
    showGuides = false;
    this.nutritionDayModel = nutritionDayModel;
	    
    totalsContainerPresenter = new TotalsContainerPresenter(rpcService, eventBus, (TotalsContainerDisplay)GWT.create(TotalsContainerView.class), uid, date);
    if(uid.equals(AppController.User.getUid()) && showGuides) {
      guideValuesPresenter = new GuideValuesListPresenter(rpcService, eventBus, (GuideValuesListDisplay)GWT.create(GuideValuesListView.class));
    }
	}
	 
	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
		
		display.setDate(date);
		//hide add button if not our times
		display.setAddButtonVisible(uid.equals(AppController.User.getUid()));
		if(nutritionDayModel != null) {
			display.setFoodsEnabled(nutritionDayModel.getFoodsPermission());
    }
		
		display.setHandler(new NutritionDayHandler() {
			@Override
			public void detailsHidden() {
				if(nutritionDayDetails != null) {
					nutritionDayDetails.stop();
		    }
			}
			@Override
			public void detailsVisible() {
				nutritionDayDetails = new NutritionDayDetailsPresenter(rpcService, eventBus, (NutritionDayDetailsDisplay)GWT.create(NutritionDayDetailsView.class), uid, date);
				nutritionDayDetails.run(display.getDetailsContainer());
				
			}

			@Override
			public void newTime() {
				addNewTime();
			}
			@Override
			public void removeTimes() {
				display.setContentEnabled(false);
				
				//remove times and fire TimeRemovedEvent
				final TimeModel[] arr = new TimeModel[timePresenters.size()];
				for(int i=0; i < timePresenters.size(); i++) {
					arr[i] = timePresenters.get(i).time;
		    }
				final Request req = rpcService.removeTimes(arr, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						
						//stop each presenter
						for(int i=0; i < timePresenters.size(); i++) {
							timePresenters.get(i).stop();
				    }
						
						display.setContentEnabled(true);
						
						//fire event for each time
						for(TimeModel time : arr) {
							fireEvent(new TimeRemovedEvent(time));
				    }
						
						display.scrollToView();
					}
				});
				addRequest(req);
			}
			@Override
			public void timesHidden() {
				unbindPresenters();
			}
			@Override
			public void timesVisible() {
				loadTimes();
			}
		});

		//EVENT: reload view when date changes
		addEventHandler(DateChangedEvent.TYPE, new DateChangedEventHandler() {
			@Override
			public void onDateChanged(DateChangedEvent event) {
				date = event.getDate();

				if(commentsPresenter != null) {
					commentsPresenter.stop();
		    }
				commentsPresenter = null;
				
        loadTimes();
			}
		});
		//EVENT: time created
		addEventHandler(TimeCreatedEvent.TYPE, new TimeCreatedEventHandler() {
			@Override
			public void onTimeCreated(TimeCreatedEvent event) {
				//handle event
				checkTimeEvent(event.getTime(), 0);	
			}
		});
		//EVENT: time updated
		addEventHandler(TimeUpdatedEvent.TYPE, new TimeUpdatedEventHandler() {
			@Override
			public void onTimeUpdated(TimeUpdatedEvent event) {
				//handle event
				checkTimeEvent(event.getTime(), 1);	
			}
		});
		//EVENT: time removed
		addEventHandler(TimeRemovedEvent.TYPE, new TimeRemovedEventHandler() {
			@Override
			public void onTimeRemoved(TimeRemovedEvent event) {
				//handle event
				checkTimeEvent(event.getTime(), 2);	
			}
		});
		
	}
	
	@Override
	public void onRefresh() {

		if(emptyPresenter != null) {
			emptyPresenter.run(display.getBodyContainer());
    }

		if(commentsPresenter != null) {
			commentsPresenter.run(display.getBodyContainer());
    }
			
		if(timePresenters != null) {
			for(int i=0; i < timePresenters.size(); i++) {
				final Presenter presenter = timePresenters.get(i);
				if(presenter != null) {
					presenter.run(display.getBodyContainer());
				}
			}		
		}

		if(guideValuesPresenter != null) {
			guideValuesPresenter.run(display.getGuideContainer());
    }
		
		if(totalsContainerPresenter != null) {
			totalsContainerPresenter.run(display.getTotalsContainer());
    }
	}


	@Override
	public void onRun() {

    //guides
    if(uid.equals(AppController.User.getUid()) && showGuides) {
			guideValuesPresenter.run(display.getGuideContainer());
    }
		
    //totals
		totalsContainerPresenter.run(display.getTotalsContainer());
		
		//if loading times from server
		if(nutritionDayModel == null) {
      display.showContent();
		}
		//just set totals
		else {
			totalsContainerPresenter.setData(date, nutritionDayModel);
			showComments();
		}
	}


	@Override
	public void onStop() {
		
		//stop time presenters
		unbindPresenters();

		if(commentsPresenter != null) {
			commentsPresenter.stop();
    }

		if(nutritionDayDetails != null) {
			nutritionDayDetails.stop();
    }
		
		if(guideValuesPresenter != null) {
			guideValuesPresenter.stop();
    }
		
		if(totalsContainerPresenter != null) {
			totalsContainerPresenter.stop();
    }
	}

	/**
	 * Loads times from server (times contains foods/meals)
	 */
	private void loadTimes() {

		unbindPresenters();
    	
		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
		//hide add button
		display.setAddButtonVisible(false);
		
    //fetch workouts for given day
    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getTimesInCalendar(uid, Functions.trimDateToDatabase(date, true), new MyAsyncCallback<List<TimeModel>>() {
			@Override
			public void onSuccess(List<TimeModel> result) {
				times  = result;
				
				showTimes();
			}
		});
		addRequest(req);
	}
	
	/**
	 * Removes presenter from view
	 * @param workout
	 */
	private void removePresenter(TimeModel time) {

		//remove also from presenters
		for(int i=0; i < timePresenters.size(); i++) {
			TimePresenter presenter = timePresenters.get(i);
			if(presenter != null && presenter.time.getId() == time.getId()) {
        timePresenters.remove(presenter);
	    }
		}
		
		//if no foods -> show empty presenter
		if(timePresenters.size() == 0 && emptyPresenter == null) {
			if(uid.equals(AppController.User.getUid())) {
				emptyPresenter = new EmptyNutritionDayPresenter(rpcService, eventBus, (EmptyNutritionDayDisplay)GWT.create(EmptyNutritionDayView.class), date);
	    }
			else {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoTimes());
	    }
			emptyPresenter.run(display.getBodyContainer());
			
			//hide add button
			display.setAddButtonVisible(false);
		}
	}
	
	/**
	 * Shows comments presenter
	 */
	private void showComments() {

		if(commentsPresenter == null) {
			//show comments
			commentsPresenter = new CommentsBoxPresenter(rpcService, eventBus, (CommentsBoxDisplay)GWT.create(CommentsBoxView.class), date, uid);
			commentsPresenter.run(display.getCommentsContainer());
		}
	}
	
	/**
	 * Unbinds all the time presenters
	 */
	private void unbindPresenters() {
					
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		emptyPresenter = null;
		
		if(timePresenters != null) {
			for(int i=0; i < timePresenters.size(); i++) {
				final Presenter presenter = timePresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			timePresenters.clear();
		}
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(TimePresenter presenter, boolean highlight) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		timePresenters.add(presenter);
		presenter.run(display.getBodyContainer());
		
		if(highlight) {
			presenter.highlight();
    }
		
		//show add button
		if(uid.equals(AppController.User.getUid())) {
			display.setAddButtonVisible(true);
    }
		
		
		
	}


	/**
	 * Adds dummy time
	 */
	@SuppressWarnings("deprecation")
	protected void addNewTime() {
		try {
			display.setContentEnabled(false);
			
			//create dummy time
			//get current time

			Date today = new Date();
			int timeDbl = today.getHours() * 3600 + today.getMinutes() * 60;
			
			//round
			timeDbl -= timeDbl % 900;
			
			for(int i=timeDbl; i < 85500; i+=900) {
				String str = Functions.getTimeToString(i);
				//go through presenters to see if that time exist
				if(timePresenters != null) {
					boolean found = false;
					for(int p=0; p < timePresenters.size(); p++) {
						final TimePresenter presenter = timePresenters.get(p);
						String strP = Functions.getTimeToString(presenter.time.getTime());

						if(str.equals(strP)) {
							found = true;
							break;
						}
						if(found) {
							break;
				    }
					}
					
					//if time not found -> use that
					if(!found) {
						timeDbl = i;
						break;
					}
				}					
				
			}
			display.setContentEnabled(true);
			
			//add new presenter
			TimeModel time = new TimeModel(Functions.trimDateToDatabase(date, true), timeDbl);
			time.setUser(AppController.User);
			final TimePresenter tp = new TimePresenter(rpcService, eventBus, (TimeDisplay)GWT.create(TimeView.class), time);
			addNewPresenter(tp, true);
			
		} catch (Exception e) {
		  Motiver.showException(e);
			display.setContentEnabled(true);
		}
	}


	/**
	 * Handle time updated/removed event
	 * @param time
	 * @param target : 0=created, 1=updated, 2=removed
	 */
	protected void checkTimeEvent(TimeModel timeUpdated, int target) {
		
	  try {
		    
			//load times if necessary
			boolean orderChanged = false;
			long lastTime = -1;
			int timesFound = 0;
			
			//if created
			if(target == 0) {
				//add presenter if not found
				boolean found = false;
				for(TimePresenter tp : timePresenters) {
					if(tp.time.getId() == timeUpdated.getId()) {
						found = true;
						break;
					}
				}
				if(!found) {
					final TimePresenter tp = new TimePresenter(rpcService, eventBus, (TimeDisplay)GWT.create(TimeView.class), timeUpdated);
					addNewPresenter(tp, false);
				}
				
				//check if order changed
				for(int i=0; i < timePresenters.size(); i++) {
					try {
						final TimePresenter presenter = timePresenters.get(i);
						if(presenter != null) {
							long timeStr = presenter.time.getTime();
							//check order
							if(lastTime >= timeStr) {
								orderChanged = true;
								break;
							}
							lastTime = timeStr;
						}
					} catch (Exception e) {
			      Motiver.showException(e);
					}
				}
				//sort times if order changed
				if(orderChanged || timesFound > 1) {
					
					//remove everything
					try {
						for(int i=0; i < timePresenters.size(); i++) {
							timePresenters.get(i).remove();
						}
					
						//sort
						Collections.sort(timePresenters);
					
						//re-add
						for(int i=0; i < timePresenters.size(); i++) {
							timePresenters.get(i).update(display.getBodyContainer());
						}
					} catch (Exception e) {
			      Motiver.showException(e);
					}
					
				}
				
			}
			//updated
			else if(target == 1) {

				//check if time are in correct order
				for(int i=0; i < timePresenters.size(); i++) {
					try {
						final TimePresenter presenter = timePresenters.get(i);
						if(presenter != null) {
							long timeStr = presenter.time.getTime();
							//check order
							if(lastTime >= timeStr) {
								orderChanged = true;
							}
							//check if more than one same time
							if(timeStr == timeUpdated.getTime()) {
								timesFound++;
					    }
							lastTime = timeStr;
						}
					} catch (Exception e) {
			      Motiver.showException(e);
					}
				}
				//sort times if order changed
				if(orderChanged || timesFound > 1) {
					
					//remove everything
					try {
						for(int i=0; i < timePresenters.size(); i++) {
							timePresenters.get(i).remove();
						}
					
						//sort
						Collections.sort(timePresenters);
					
						//re-add
						for(int i=0; i < timePresenters.size(); i++) {
							timePresenters.get(i).update(display.getBodyContainer());
						}
					} catch (Exception e) {
			      Motiver.showException(e);
					}
					
				}					
			}
			//removed
			else if(target == 2) {
				removePresenter(timeUpdated);
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}

	/**
	 * Shows times in content (view)
	 * @param models : workout models to show
	 */
	protected void showTimes() {
		
		if(times == null) {
			return;
    }
		
		try {
			unbindPresenters();
			
			//if no times
			if(times.size() == 0) {
				if(uid.equals(AppController.User.getUid())) {
					emptyPresenter = new EmptyNutritionDayPresenter(rpcService, eventBus, (EmptyNutritionDayDisplay)GWT.create(EmptyNutritionDayView.class), date);
		    }
				else {
					emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoTimes());
		    }
				emptyPresenter.run(display.getBodyContainer());
				
				//hide add button
				display.setAddButtonVisible(false);
				
				//update totals
				totalsContainerPresenter.setData(date, times);
			}
			else {

				//orders
				Collections.sort(times);
				
				for(TimeModel m : times) {
					
					//init new timePresenter
					final TimePresenter tp = new TimePresenter(rpcService, eventBus, (TimeDisplay)GWT.create(TimeView.class), m);
					addNewPresenter(tp, false);
					
				}
				
				//update totals container
				totalsContainerPresenter.setData(date, times);
			}
			
			showComments();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Enable floating when panel is not visible
	 * @param enabled
	 */
	void setFloatingEnabled() {
		//TODO ei poisteta mitenk��n?!!?
		Window.addWindowScrollHandler(new ScrollHandler() { 
		  public void onWindowScroll(ScrollEvent event) {
				display.onScroll(event.getScrollTop());
      } 
		});
	}

}
