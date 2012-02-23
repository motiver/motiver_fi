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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.CommentPresenter.CommentDisplay;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.CommentView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CommentModel;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.UserModel;
import com.delect.motiver.shared.WorkoutModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows facebook comments "box"
 * @author Antti
 *
 */
public class CommentsBoxPresenter extends Presenter {

	/** Handler for this presenter
	 */
	public interface CommentBoxHandler {
		/**
		 * Called when user writes new comment
		 * @param text String
		 * @param publishOnFacebook boolean
		 */
		void newComment(String text, boolean publishOnFacebook);
	}
	/**
	* Abstract class for view to extend
	*/
	public abstract static class CommentsBoxDisplay extends Display {
		/**
		 * Returns container for comments
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getCommentsContainer();
		/**
		 * Sets title for comments box
		 * @param title String
		 */
		public abstract void setCommentTitle(String title);
		/**
		 * Disables comment box
		 */
		public abstract void setDisabled();
		/**
		 * Sets handler for view to call
		 * @param handler CommentBoxHandler
		 */
		public abstract void setHandler(CommentBoxHandler handler);
	}

	private CardioModel cardio;

	private List<Presenter> commentPresenters = new ArrayList<Presenter>();
	private Date date;
	private final CommentsBoxDisplay display;
	
	private EmptyPresenter emptyPresenter;
	private MealModel meal;
	private MeasurementModel measurement;
	private RoutineModel routine;
	private RunModel run;
	private ShowMorePresenter showMorePresenter;
	private String target = "";

	private String title = "";
	private String uid;

	//models
	private WorkoutModel workout;

  private Timer timer;
	
	/**
	 * Comments box for cardio
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param cardio
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, CardioModel cardio) {
		super(rpcService, eventBus);
    this.display = display;
    this.cardio = cardio;
	}
	/**
	 * Comments box for nutrition day
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param date
	 * @param uid : who's nutrition it is
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, Date date, String uid) {
		super(rpcService, eventBus);
    this.display = display;
    this.date = date;
    this.uid  = uid;
	}
	/**
	 * Comments box for meal
	 * @param rpcService
	 * @param eventBus
	 * @param display
	
	 * @param meal MealModel
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, MealModel meal) {
		super(rpcService, eventBus);
    this.display = display;
    this.meal = meal;
	}
	/**
	 * Comments box for measurement value
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param measurement
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, MeasurementModel measurement) {
		super(rpcService, eventBus);
    this.display = display;
    this.measurement = measurement;
	}
	/**
	 * Comments box for routine
	 * @param rpcService
	 * @param eventBus
	 * @param display
	
	 * @param routine RoutineModel
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, RoutineModel routine) {
		super(rpcService, eventBus);
    this.display = display;
		this.routine = routine;
	}
	/**
	 * Comments box for run
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param run
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, RunModel run) {
		super(rpcService, eventBus);
    this.display = display;
    this.run = run;
	}
	/**
	 * Comments box for workout
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param workout
	 */
	public CommentsBoxPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, CommentsBoxDisplay display, WorkoutModel workout) {
		super(rpcService, eventBus);
    this.display = display;
		this.workout = workout;
	}

	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
		
		display.setHandler(new CommentBoxHandler() {
			@Override
			public void newComment(String text, boolean publishOnFacebook) {
				sendNewComment(text, publishOnFacebook);
			}
		});
		
		//workout
		if(workout != null) {
			title = workout.getName();
			target = "w" + workout.getId();
			uid = workout.getUser().getUid();
		}
		//routine
		else if(routine != null) {
			title = routine.getName();
			target = "r" + routine.getId();
			uid = routine.getUser().getUid();
		}
		//meal
		else if(meal != null) {
			title = meal.getName();
			target = "m" + meal.getId();
			uid = meal.getUser().getUid();
		}
		//measurement
		else if(measurement != null) {
			title = measurement.getNameClient();
			target = "me" + measurement.getId();
			uid = measurement.getUid();
		}
		//cardio
		else if(cardio != null) {
			title = cardio.getNameClient();
			target = "c" + cardio.getId();
			uid = cardio.getUid();
		}
		//run
		else if(run != null) {
			title = run.getNameClient();
			target = "ru" + run.getId();
			uid = run.getUid();
		}
		//nutrition day
		else if(date != null) {
			final DateTimeFormat fmt2 = DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()]);
			title = AppController.Lang.Foods().toLowerCase() + " (" + fmt2.format(date) + ")";
			target = "n" + (date.getTime() / 1000);
		}
		
		display.setCommentTitle(title);
	}
	
	@Override
	public void onRun() {

		//if coach mode -> disable comments
		if(AppController.COACH_MODE_ON) {
			display.setDisabled();

			emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.CommentsDisabledInCoachMode(), EmptyPresenter.OPTION_SMALLER);
			emptyPresenter.run(display.getBaseContainer());
		}
		//load comments
		else  {
      loadComments(0, true);
      
      //refresh comments every X seconds
      if(timer != null) {
        timer.cancel();
      }
      timer = new Timer() {
        @Override
        public void run() {
          loadComments(0, false);
        }
      };
      timer.scheduleRepeating(Constants.DELAY_COMMENTS_REFRESH);
    }
	}


	@Override
	public void onStop() {
	  if(timer != null) {
	    timer.cancel();
	  }
		unbindPresenters();
	}

	/**
	 * Stops all comment presenters
	 */
	private void unbindPresenters() {
		if(commentPresenters != null) {
			for(int i=0; i < commentPresenters.size(); i++) {
				final Presenter presenter = commentPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			commentPresenters.clear();
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
		commentPresenters.add(presenter);
		presenter.run(display.getCommentsContainer());
	}
	
	/**
	 * Loads comments from server
	 * @param index int
	 * @param loadFromCache : if data is loaded from cache before server call
	 * @param showLoading : if loading text are shown. If false loads on background and refreshes comments when new data is fetched.
	 */
	protected void loadComments(final int index, boolean showLoading) {
    		
	  if(showLoading) {
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
	    emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
	    emptyPresenter.run(display.getCommentsContainer());
	  }
		
		//fetch all comments
		if(index == 0) {
	    Motiver.setNextCallCacheable(true);
		}
		final Request req = rpcService.getComments(index, Constants.LIMIT_COMMENTS, target, uid, true, new MyAsyncCallback<List<CommentModel>>() {
			@Override
			public void onSuccess(List<CommentModel> result) {
				showComments(index, result);
			}
		});
		addRequest(req);
	}
	
	/**
	 * Sends new comment to server
	 * @param text
	 * @param publishOnFacebook boolean
	 */
	protected void sendNewComment(final String text, final boolean publishOnFacebook) {
		
		final CommentModel model = new CommentModel();
		model.setDate(CommonUtils.trimDateToDatabase(new Date(), false));
		model.setTarget(target);
		model.setText(text);
		model.setUserTarget(new UserModel(uid));
		
		display.setContentEnabled(false);
		
		//send comment
		final Request req = rpcService.addComment(model, new MyAsyncCallback<CommentModel>() {
			@Override
			public void onSuccess(CommentModel result) {
				display.setContentEnabled(true);
				
				loadComments(0, false);
				
				//publish on facebook
//				if(publishOnFacebook) {
//					
//					final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "https://graph.facebook.com/me/feed");
//					rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
//					try {							
//						//send to facebook servers
//				    rb.sendRequest(URL.encode("message=" + text + "&link=http://www.motiver.fi&actions={\"name\": \"" + AppController.Lang.ViewOnMotiver() + "\", \"link\": \"http://"+uid+".motiver.fi\"}&privacy={\"value\": \"EVERYONE\"}&access_token=" + AppController.ACCESSTOKEN + ""), null);
//					}
//					catch (RequestException e) {
//						final ConnectionErrorEvent event = new ConnectionErrorEvent(Constants.ERROR_CANT_CONNECT_FACEBOOK);
//						eventBus.fireEvent(event);
//					}
//					
//				}
			}
		});
		addRequest(req);
		
	}

	/**
	 * Parses facebooks groups
	 * @param index int
	 * @param result List<CommentModel>
	 */
	protected void showComments(final int index, List<CommentModel> result) {

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
			
			//if no comments
			if(result.size() == 0 && index == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoComments(), EmptyPresenter.OPTION_SMALLER);
				emptyPresenter.run(display.getCommentsContainer());
			}
			else {
				//add comments
				for(CommentModel m : result) {
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadComments(index + Constants.LIMIT_COMMENTS, true);
							}
						});
						showMorePresenter.run(display.getCommentsContainer());
					}
					else {
						final CommentPresenter fp = new CommentPresenter(rpcService, eventBus, (CommentDisplay)GWT.create(CommentView.class), m, false);
						addNewPresenter(fp);
					}
				}
				
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
