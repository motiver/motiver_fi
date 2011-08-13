/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.blog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.CardioShowEvent;
import com.delect.motiver.client.event.MeasurementShowEvent;
import com.delect.motiver.client.event.RunShowEvent;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.event.handler.CardioShowEventHandler;
import com.delect.motiver.client.event.handler.MeasurementShowEventHandler;
import com.delect.motiver.client.event.handler.RunShowEventHandler;
import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.blog.BlogDayPresenter.BlogDayDisplay;
import com.delect.motiver.client.presenter.cardio.CardioValueLinkPresenter;
import com.delect.motiver.client.presenter.cardio.RunValueLinkPresenter;
import com.delect.motiver.client.presenter.profile.MeasurementValueLinkPresenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.client.view.blog.BlogDayView;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.UserModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 
 * User's public profile
 * <br> - all data in "blog type view"
 * <br>Redirects to www.motiver.fi if blog not found
 */
public class SingleBlogPagePresenter extends Presenter {

	public interface BlogPageHandler {
		void showFullBlog();
	}
	/**
	* Abstract class for view to extend
	*/
	public abstract static class SingleBlogPageDisplay extends Display {
		public  abstract LayoutContainer getBodyContainer();
		public  abstract LayoutContainer getFriendsContainer();
		public  abstract LayoutContainer getUserContainer();
		public  abstract void setHandler(BlogPageHandler handler);
		public  abstract void setShowSingleDay(boolean singleDay);
		public  abstract void setUid(long uid);
	}
	private Date dateEnd;

	private List<Presenter> dayPresenters = new ArrayList<Presenter>();
	private SingleBlogPageDisplay display;
	private Presenter emptyPresenter = null;
	private ShowMorePresenter showMorePresenter;

	private String uid = null;
	private UserPresenter userPresenter;

	/**
	 * Blog
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public SingleBlogPagePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, SingleBlogPageDisplay display, String uid) {
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
		display.setHandler(new BlogPageHandler() {
			@Override
			public void showFullBlog() {
				loadData(0, null);
			}
		});

    //EVENT: blog show
		addEventHandler(UserSelectedEvent.TYPE, new UserSelectedEventHandler() {
			@Override
			public void userSelected(UserSelectedEvent event) {
        Window.Location.replace(event.getUser().getBlogUrl());
			}
    });
    //EVENT: show cardio -> open value presenters body
    addEventHandler(CardioShowEvent.TYPE, new CardioShowEventHandler() {
			@Override
			public void onCardioShow(CardioShowEvent event) {
				if(event.getSource() instanceof CardioValueLinkPresenter) {
					CardioValueLinkPresenter presenter = (CardioValueLinkPresenter)event.getSource();
					presenter.showCardio();
				}
			}
    });
	    
    //EVENT: show run -> open value presenters body
    addEventHandler(RunShowEvent.TYPE, new RunShowEventHandler() {
			@Override
			public void onRunShow(RunShowEvent event) {
				if(event.getSource() instanceof RunValueLinkPresenter) {
					RunValueLinkPresenter presenter = (RunValueLinkPresenter)event.getSource();
					presenter.showRun();
				}
			}
    });
	    
    //EVENT: show measurement -> open value presenters body
    addEventHandler(MeasurementShowEvent.TYPE, new MeasurementShowEventHandler() {
			@Override
			public void onMeasurementShow(MeasurementShowEvent event) {
				if(event.getSource() instanceof MeasurementValueLinkPresenter) {
					MeasurementValueLinkPresenter presenter = (MeasurementValueLinkPresenter)event.getSource();
					presenter.showMeasurement();
				}
			}
    });
		
	}

	@Override
	public void onRun() {

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());

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

		try {
			
			if(userPresenter != null) {
				userPresenter.stop();
      }
			
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			if(showMorePresenter != null) {
				showMorePresenter.stop();
				showMorePresenter = null;
			}
			
			if(dayPresenters != null) {
				for(int i=0; i < dayPresenters.size(); i++) {
					final Presenter presenter = dayPresenters.get(i);
					if(presenter != null) {
						presenter.stop();
					}
				}
				dayPresenters.clear();
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
		
		//remove emptypresenter if present
		if(emptyPresenter != null) {
			emptyPresenter.stop();
			emptyPresenter = null;
		}
		dayPresenters.add(presenter);
		presenter.run(display.getBodyContainer());
	}


	/**
	 * Shows all data
	 * @param result
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
				Window.Location.replace(Constants.URL_APP);
			}
			//if no items -> no data
			else if(result.size() == 0 && index == 0) {
				
				//add empty presenter
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoActivities());
				emptyPresenter.run(display.getBodyContainer());
			}
			else {

	      //show user presenter
	      if(index == 0) {
	        UserModel user = new UserModel();
	        user.setUid(result.get(0).getUser().getUid());
	        userPresenter = new UserPresenter(rpcService, eventBus, (UserDisplay) GWT.create(UserView.class), user, false);
	        userPresenter.run(display.getUserContainer());
	      }
		    	
				//show data
				for(int day = 0; day < result.size(); day++) {
					
					BlogData data = result.get(day);
					
					//if null value -> list was limited -> add showMorePresenter
					if(data == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadData(index + Constants.LIMIT_BLOG_DAYS, dateEnd);
							}
						});
						showMorePresenter.run(display.getBodyContainer());
					}
					else {
						final BlogDayPresenter fp = new BlogDayPresenter(rpcService, eventBus, (BlogDayDisplay)GWT.create(BlogDayView.class), data, false);
						addNewPresenter(fp);
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
	void loadData(final int index, Date dateEnd) {

		this.dateEnd = dateEnd;
		display.setShowSingleDay(false);
		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		if(index == 0) {
			unbindPresenters();
    }
		
		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getBodyContainer());
    		
		//check dates
		Date now = new Date();
		Date dateStart = null;
		if(dateEnd == null) {
      dateEnd = new Date((now.getTime() / 1000 +  3600 * 24 * Constants.BLOG_DEFAULT_DAYS_AFTER_TODAY) * 1000);
		}
		//show single date
		else {
			display.setShowSingleDay(true);
			dateStart = Functions.trimDateToDatabase(dateEnd, true);
		}

    //if first data -> can be load from cache
    if(index == 0) {
      Motiver.setNextCallCacheable(true);
    }
		final Request req = rpcService.getBlogData(index, Constants.LIMIT_BLOG_DAYS, 0, dateStart, Functions.trimDateToDatabase(dateEnd, true), uid, false, new MyAsyncCallback<List<BlogData>>() {
			@Override
			public void onSuccess(List<BlogData> result) {			  
				//show data
				showData(index, result);
			}
		});
		addRequest(req);
	}

}
