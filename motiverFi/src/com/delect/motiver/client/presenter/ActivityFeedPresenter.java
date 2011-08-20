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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.WorkoutSelectedEvent;
import com.delect.motiver.client.event.handler.WorkoutSelectedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.blog.BlogDayPresenter;
import com.delect.motiver.client.presenter.blog.BlogDayPresenter.BlogDayDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.blog.BlogDayView;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;

/**
 * Shows activity feed from single user
 */
public class ActivityFeedPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class ActivityFeedDisplay extends Display {
		public abstract void setHandler(ActivityFeedHandler handler);
	}
  /** Handler for this presenter
   */
  public interface ActivityFeedHandler {
	}
	private ActivityFeedDisplay display = null;

	private EmptyPresenter emptyPresenter;
	private List<Presenter> presenters = new ArrayList<Presenter>();
	private ShowMorePresenter showMorePresenter;
	
	private String uid;

	/**
	 * Constructor for ActivityFeedPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display ActivityFeedDisplay
	 * @param uid long
	 */
	public ActivityFeedPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, ActivityFeedDisplay display, String uid) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.uid = uid;

	}
	
	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {

		display.setHandler(new ActivityFeedHandler() {

		});
		
		//EVENT: workout selected
		addEventHandler(WorkoutSelectedEvent.TYPE, new WorkoutSelectedEventHandler() {
			@Override
			public void workoutSelected(WorkoutSelectedEvent event) {
				Date d = event.getWorkout().getDate();
				d = Functions.getDateGmt(d);
				
				//open correct day
				History.newItem("user/training/" + (d.getTime() / 1000));
			}
		});
	}
	
	@Override
	public void onRun() {

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBaseContainer());
		
		loadData(0, null);
	}


	@Override
	public void onStop() {
		
		unbindPresenters();
	}


	/**
	 * Unbinds all the meal/time presenters
	 */
	private void unbindPresenters() {

		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		
		if(presenters != null) {
			for(int i=0; i < presenters.size(); i++) {
				final Presenter presenter = presenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			presenters.clear();
		}
	}


	/**
	 * Adds new presenter to view
	 * @param presenter
	 */
	protected void addNewPresenter(Presenter presenter) {
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		presenters.add(presenter);
		presenter.run(display.getBaseContainer());
	}

	/**
	 * Shows all data
	 * @param result
	 * @param index int
	 */
	protected void showData(final int index, List<BlogData> result) {

		try {
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			if(showMorePresenter != null) {
				showMorePresenter.stop();
				showMorePresenter = null;
			}
			if(index == 0) {
			  unbindPresenters();
			}
			
			//if null -> no permission
			if(result == null && index == 0) {
				
				//add empty presenter
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoPermission());
				emptyPresenter.run(display.getBaseContainer());
			}
			//if no items -> no data
			else if(result.size() == 0 && index == 0) {
				
				//add empty presenter
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoActivities());
				emptyPresenter.run(display.getBaseContainer());
			}
			else {
		    	
				//show data
				for(int day = 0; day < result.size(); day++) {
					
					BlogData data = result.get(day);
					
					//if null value -> list was limited -> add showMorePresenter
					if(data == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadData(index + Constants.LIMIT_BLOG_DAYS, null);
							}
						});
						showMorePresenter.run(display.getBaseContainer());
					}
					else {
						final BlogDayPresenter fp = new BlogDayPresenter(rpcService, eventBus, (BlogDayDisplay)GWT.create(BlogDayView.class), data, true);
						addNewPresenter(fp);
					}
				}
			}

		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}

	/**
	 * Loads data from server
	 * @param index int
	 * @param dateEnd Date
	 */
	void loadData(final int index, Date dateEnd) {

		if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
      showMorePresenter.stop();
    }

		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBaseContainer());
    	
		//check dates
		final Date now = new Date();
		if(dateEnd == null) {
      dateEnd = new Date((now.getTime() / 1000 +  3600 * 24 * Constants.BLOG_DEFAULT_DAYS_AFTER_TODAY) * 1000);
		}
		
		//if first data -> can be load from cache
		if(index == 0) {
		  Motiver.setNextCallCacheable(true);
		}
		final Request req = rpcService.getBlogData(index, Constants.LIMIT_BLOG_DAYS, 0, null, Functions.trimDateToDatabase(dateEnd, true), String.valueOf(uid), false, new MyAsyncCallback<List<BlogData>>() {
			@Override
			public void onSuccess(List<BlogData> result) {
				//show data
				showData(index, result);
			}
		});
		addRequest(req);
	}

}
