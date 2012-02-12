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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.BlogShowEvent;
import com.delect.motiver.client.event.CardioShowEvent;
import com.delect.motiver.client.event.CoachModeEvent;
import com.delect.motiver.client.event.CommentNewEvent;
import com.delect.motiver.client.event.InfoMessageEvent;
import com.delect.motiver.client.event.LoadingEvent;
import com.delect.motiver.client.event.MeasurementShowEvent;
import com.delect.motiver.client.event.NutritionDayShowEvent;
import com.delect.motiver.client.event.OfflineModeEvent;
import com.delect.motiver.client.event.RunShowEvent;
import com.delect.motiver.client.event.ShortcutKeyEvent;
import com.delect.motiver.client.event.TabEvent;
import com.delect.motiver.client.event.WorkoutShowEvent;
import com.delect.motiver.client.event.handler.BlogShowEventHandler;
import com.delect.motiver.client.event.handler.CardioShowEventHandler;
import com.delect.motiver.client.event.handler.CoachModeEventHandler;
import com.delect.motiver.client.event.handler.InfoMessageEventHandler;
import com.delect.motiver.client.event.handler.LoadingEventHandler;
import com.delect.motiver.client.event.handler.MeasurementShowEventHandler;
import com.delect.motiver.client.event.handler.NutritionDayShowEventHandler;
import com.delect.motiver.client.event.handler.OfflineModeEventHandler;
import com.delect.motiver.client.event.handler.RunShowEventHandler;
import com.delect.motiver.client.event.handler.ShortcutKeyEventHandler;
import com.delect.motiver.client.event.handler.TabEventHandler;
import com.delect.motiver.client.event.handler.WorkoutShowEventHandler;
import com.delect.motiver.client.presenter.ConfirmDialogPresenter.ConfirmDialogDisplay;
import com.delect.motiver.client.presenter.ConfirmDialogPresenter.ConfirmDialogHandler;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderDisplay;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderTarget;
import com.delect.motiver.client.presenter.InfoMessagePresenter.InfoMessageDisplay;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;
import com.delect.motiver.client.presenter.LoadingPresenter.LoadingDisplay;
import com.delect.motiver.client.presenter.MainPagePresenter.MainPageDisplay;
import com.delect.motiver.client.presenter.ShortcutKeysPresenter.ShortcutKeysDisplay;
import com.delect.motiver.client.presenter.admin.AdminPagePresenter;
import com.delect.motiver.client.presenter.admin.AdminPagePresenter.AdminPageDisplay;
import com.delect.motiver.client.presenter.cardio.CardioPagePresenter;
import com.delect.motiver.client.presenter.cardio.CardioPagePresenter.CardioPageDisplay;
import com.delect.motiver.client.presenter.coach.CoachModeIndicatorPresenter;
import com.delect.motiver.client.presenter.coach.CoachModeIndicatorPresenter.CoachModeIndicatorDisplay;
import com.delect.motiver.client.presenter.coach.CoachPagePresenter;
import com.delect.motiver.client.presenter.coach.CoachPagePresenter.CoachPageDisplay;
import com.delect.motiver.client.presenter.guide.BeginnersGuidePresenter;
import com.delect.motiver.client.presenter.guide.BeginnersGuidePresenter.BeginnersGuideDisplay;
import com.delect.motiver.client.presenter.nutrition.NutritionPagePresenter;
import com.delect.motiver.client.presenter.nutrition.NutritionPagePresenter.NutritionPageDisplay;
import com.delect.motiver.client.presenter.profile.ProfilePagePresenter;
import com.delect.motiver.client.presenter.profile.ProfilePagePresenter.ProfilePageDisplay;
import com.delect.motiver.client.presenter.statistics.StatisticsPagePresenter;
import com.delect.motiver.client.presenter.statistics.StatisticsPagePresenter.StatisticsPageDisplay;
import com.delect.motiver.client.presenter.training.TrainingPagePresenter;
import com.delect.motiver.client.presenter.training.TrainingPagePresenter.TrainingPageDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.ConfirmDialogView;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.HeaderView;
import com.delect.motiver.client.view.InfoMessageView;
import com.delect.motiver.client.view.LoadingView;
import com.delect.motiver.client.view.MainPageView;
import com.delect.motiver.client.view.ShortcutKeysView;
import com.delect.motiver.client.view.admin.AdminPageView;
import com.delect.motiver.client.view.cardio.CardioPageView;
import com.delect.motiver.client.view.coach.CoachModeIndicatorView;
import com.delect.motiver.client.view.coach.CoachPageView;
import com.delect.motiver.client.view.guide.BeginnersGuideView;
import com.delect.motiver.client.view.nutrition.NutritionPageView;
import com.delect.motiver.client.view.profile.ProfilePageView;
import com.delect.motiver.client.view.statistics.StatisticsPageView;
import com.delect.motiver.client.view.training.TrainingPageView;
import com.delect.motiver.shared.CommentModel;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.TicketModel;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/** User index view. Is shown when user is logged in.
 */
public class UserIndexPresenter extends Presenter implements ValueChangeHandler<String> {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class UserIndexDisplay extends Display {
		/**
		 * Returns container for body.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getBodyContainer();
		/**
		 * Returns container for footer.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getFooterContainer();
		/**
		 * Returns container for header.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getHeaderContainer();
		/**
		 * Returns container for messages.
		 * @return LayoutContainer
		 */
		public abstract LayoutContainer getMessageContainer();
		/**
		 * Sets handler for view to call.
		 * @param handler UserIndexHandler
		 */
		public abstract void setHandler(UserIndexHandler handler);
		/**
		 * Shows/hides 'print view' link.
		 * @param visible boolean
		 */
		public abstract void setPrintLinkVisibility(boolean visible);
		/**
		 * Shows/hides loading text.
		 * @param enabled boolean
		 */
		public abstract void showLoading(boolean enabled);
	}
	/** Handler for this presenter.
	 */
	public interface UserIndexHandler {
		/**
		 * Called when new ticked is written.
		 * @param ticket TicketModel
		 */
		void newTicket(TicketModel ticket);
		/**
		 * Called when print view link is clicked.
		 */
		void printPage();
	}
//	private BrowserCheckPresenter browserCheckPresenter;
	
	private CoachModeIndicatorPresenter coachModeIndicatorPresenter;
	private int connection_count = 0;
	private UserIndexDisplay display;
	private HeaderPresenter headerUserPresenter;
	private InfoMessagePresenter infoMessageOfflineMode;
	private List<InfoMessagePresenter> infoMessagePresenters = new ArrayList<InfoMessagePresenter>();
	private LoadingPresenter loadingPresenter;
	private Presenter pagePresenter;
	private ShortcutKeysPresenter shortcutKeysPresenter;
	private BeginnersGuidePresenter beginnersGuidePresenter;

	private Timer timer;
	private Timer timerReload;
  private ConfirmDialogPresenter msgPresenter;

	
	/**
	 * Constructor for UserIndexPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display UserIndexDisplay
	 */
	public UserIndexPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, UserIndexDisplay display) {
		super(rpcService, eventBus);
		this.display = display;

    //containers
    headerUserPresenter = new HeaderPresenter(rpcService, eventBus, (HeaderDisplay)GWT.create(HeaderView.class), HeaderTarget.USER, 0);
    shortcutKeysPresenter = new ShortcutKeysPresenter(rpcService, eventBus, (ShortcutKeysDisplay)GWT.create(ShortcutKeysView.class));
//    browserCheckPresenter = new BrowserCheckPresenter(rpcService, eventBus, (BrowserCheckDisplay)GWT.create(BrowserCheckView.class));
  }

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

    History.addValueChangeHandler(this);
		
    display.setHandler(new UserIndexHandler() {
			@SuppressWarnings("unchecked")
      @Override
			public void newTicket(TicketModel ticket) {
				final Request req = rpcService.addTicket(ticket, MyAsyncCallback.EmptyCallback);
				addRequest(req);
			}
			@Override
			public void printPage() {
				Window.open(Constants.URL_APP + "print/#" + History.getToken(), "_blank", "status=1,toolbar=1,location=1,menubar=1,directories=1,resizable=1,scrollbars=1");
			}
    });
	    
		//tab change handler
    addEventHandler(TabEvent.TYPE, new TabEventHandler() {
			@Override
			public void onTabChanged(TabEvent event) {
				
				//set new history token
				switch(event.getIndex()) {
					case 0:
						History.newItem("user");
						break;
					case 1:
						History.newItem("user/training");
						break;
					case 2:
						History.newItem("user/nutrition");
						break;
					case 3:
						History.newItem("user/cardio");
						break;
					case 4:
						History.newItem("user/statistics");
						break;
					case 5:
						History.newItem("user/profile");
						break;
					case 6:
						History.newItem("user/coach");
						break;
					case 7:
						History.newItem("user/admin");
						break;
					default:
						History.newItem("user");
						break;
				}
				
				//reset reload timer
				setPageReloadTimer();
			}			
    });
	    
    //EVENT: connection error
    addEventHandler(InfoMessageEvent.TYPE, new InfoMessageEventHandler() {
			@Override
			public void onInfoMessage(InfoMessageEvent event) {				
				final InfoMessagePresenter infoMessagePresenter = new InfoMessagePresenter(rpcService, eventBus, (InfoMessageDisplay)GWT.create(InfoMessageView.class), event.getMessageColor(), event.getMessage(), event.getClickListener());
				infoMessagePresenter.run(display.getMessageContainer());
				
				infoMessagePresenters.add(infoMessagePresenter);
			}
    });
	    
    //EVENT: offline mode
    addEventHandler(OfflineModeEvent.TYPE, new OfflineModeEventHandler() {
			@Override
			public void onModeChange(OfflineModeEvent event) {
				if(infoMessageOfflineMode != null) {
          infoMessageOfflineMode.stop();
	      }
				
				//if mode is on
				if(event.isOn()) {
					infoMessageOfflineMode = new InfoMessagePresenter(rpcService, eventBus, (InfoMessageDisplay)GWT.create(InfoMessageView.class), MessageColor.COLOR_BLUE, AppController.Lang.OfflineModeIsOn(), null);
					infoMessageOfflineMode.run(display.getMessageContainer());	
				}
			}
    });
	    
    //EVENT: blog show
    addEventHandler(BlogShowEvent.TYPE, new BlogShowEventHandler() {
			@Override
			public void onBlogShow(BlogShowEvent event) {
				//open blog in new window
				Window.open(event.getUser().getBlogUrl(), "_blank", "status=1,toolbar=1,location=1,menubar=1,directories=1,resizable=1,scrollbars=1");
			}
    });
	    
    //EVENT: show workout
    addEventHandler(WorkoutShowEvent.TYPE, new WorkoutShowEventHandler() {
			@Override
			public void selectWorkout(WorkoutShowEvent event) {
				if(event.getWorkout().getDate() != null) {
					Date d = event.getWorkout().getDate();
					d = Functions.getDateGmt(d);
					
					//open correct day
					History.newItem("user/training/" + (d.getTime() / 1000));
				}
			}
    });
	    
    //EVENT: show day (nutrition)
    addEventHandler(NutritionDayShowEvent.TYPE, new NutritionDayShowEventHandler() {
			@Override
			public void selectNutritionDay(NutritionDayShowEvent event) {
				if(event.getNutritionDay().getDate() != null) {
					final Date d = event.getNutritionDay().getDate();
					
					//open correct day
					History.newItem("user/nutrition/" + (d.getTime() / 1000));
				}
			}
    });
	    
    //EVENT: show cardio
    addEventHandler(CardioShowEvent.TYPE, new CardioShowEventHandler() {
			@Override
			public void onCardioShow(CardioShowEvent event) {
				//open correct day
				History.newItem("user/cardio/c" + event.getCardio().getId());
			}
    });
	    
    //EVENT: show run
    addEventHandler(RunShowEvent.TYPE, new RunShowEventHandler() {
			@Override
			public void onRunShow(RunShowEvent event) {
				//open correct day
				History.newItem("user/cardio/r" + event.getRun().getId());
			}
    });
	    
    //EVENT: show measurement
    addEventHandler(MeasurementShowEvent.TYPE, new MeasurementShowEventHandler() {
			@Override
			public void onMeasurementShow(MeasurementShowEvent event) {
				//open correct day
				History.newItem("user/profile/m" + event.getMeasurement().getId());
			}
    });
	    
    //EVENT: loading text
    addEventHandler(LoadingEvent.TYPE, new LoadingEventHandler() {

			@Override
			public void onLoading(LoadingEvent event) {

				if(event.getMessage() != null) {
					//if first connection -> fire event
					if(connection_count == 0) {				
						
						if(loadingPresenter != null) {
							loadingPresenter.stop();
			      }
						
						//show loading text
            loadingPresenter = new LoadingPresenter(rpcService, eventBus, (LoadingDisplay)GWT.create(LoadingView.class), event.getMessage());
            loadingPresenter.run(display.getBaseContainer());
					}
					
					connection_count++;
				}
				//cancelled loading event
				else {
					//reduce connection count
					if(connection_count > 0) {
						connection_count--;
					}
					
					//if no connections left -> stop
					if(connection_count == 0 && loadingPresenter != null) {
						loadingPresenter.stop();
          }
				}
			}
	    	
    });
	    
    //EVENT: coach mode on
    addEventHandler(CoachModeEvent.TYPE, new CoachModeEventHandler() {
			@Override
			public void onCoachModeOn(CoachModeEvent event) {
				
				//coach mode started
				if(event.getUser() != null) {
					//set variables
					AppController.UserLast = AppController.User;
					AppController.User = event.getUser();
					AppController.COACH_MODE_UID = event.getUser().getUid();
					AppController.COACH_MODE_ON = true;
					
					//show indicator
					if(coachModeIndicatorPresenter != null) {
						coachModeIndicatorPresenter.stop();
          }
					coachModeIndicatorPresenter = new CoachModeIndicatorPresenter(rpcService, eventBus, (CoachModeIndicatorDisplay)GWT.create(CoachModeIndicatorView.class), event.getUser());
					coachModeIndicatorPresenter.run(display.getBaseContainer());
				}
				//coach mode ended
				else {
					//set variables
					AppController.User = AppController.UserLast;
					AppController.COACH_MODE_UID = null;
					AppController.COACH_MODE_ON = false;
				}
				
				//restart header and menu
				headerUserPresenter.stop();
				pagePresenter.stop();
				pagePresenter = null;
				
        headerUserPresenter = new HeaderPresenter(rpcService, eventBus, (HeaderDisplay)GWT.create(HeaderView.class), HeaderTarget.USER, 0);
        headerUserPresenter.run(display.getHeaderContainer());

				//fire main view
				if(event.getUser() != null) {
					History.newItem("user", true);
				}
				else {
					History.fireCurrentHistoryState();
				}
			}
    });
    
    //EVENT: shortcut key
    addEventHandler(ShortcutKeyEvent.TYPE, new ShortcutKeyEventHandler() {
      @Override
      public void onShortcutKey(ShortcutKeyEvent event) {
        if(event.getKey() == 83) {
          showBeginnerTutorial();
        }
        
      }
    });

    if(History.getToken().length() == 0) {
      History.newItem("user", false);
    }
	}

	protected void showBeginnerTutorial() {
    if(beginnersGuidePresenter != null)
      beginnersGuidePresenter.stop();
    
    beginnersGuidePresenter = new BeginnersGuidePresenter(rpcService, eventBus, (BeginnersGuideDisplay)GWT.create(BeginnersGuideView.class));
    beginnersGuidePresenter.run(display.getMessageContainer());
  }

  @Override
	public void onRun() {

    display.showLoading(true);
	    
    //header
    headerUserPresenter.run(display.getHeaderContainer());

		shortcutKeysPresenter.run(display.getMessageContainer());
	    	    	    
    History.fireCurrentHistoryState();

    //reload page each xx hours
		setPageReloadTimer();
		
		//show tutorial if not already shown
		if(!AppController.User.isTutorialShowed()) {
      showBeginnerTutorial();
      
      //save user
      AppController.User.setTutorialShowed(true);
      rpcService.saveUserData(AppController.User, MyAsyncCallback.EmptyCallback);
		}
	}

	@Override
	public void onStop() {
		
		if(timer != null) {
			timer.cancel();
    }
		if(timerReload != null) {
			timerReload.cancel();
		}
		if(loadingPresenter != null) {
			loadingPresenter.stop();
    }
		if(infoMessageOfflineMode != null) {
			infoMessageOfflineMode.stop();
    }
		if(coachModeIndicatorPresenter != null) {
			coachModeIndicatorPresenter.stop();
    }
//		if(browserCheckPresenter != null) {
//			browserCheckPresenter.stop();
//    }
		if(shortcutKeysPresenter != null) {
			shortcutKeysPresenter.stop();
    }
		if(headerUserPresenter != null) {
			headerUserPresenter.stop();
    }
		if(pagePresenter != null) {
			pagePresenter.stop();
    }
		
    if(msgPresenter != null) {
      msgPresenter.stop();
    }

		if(infoMessagePresenters != null) {
			for(InfoMessagePresenter p : infoMessagePresenters) {
			  if(p != null) {
			    p.stop();
			  }
			}
			infoMessagePresenters.clear();
		}
	}

	/**
	 * Called when url token changes.
	 * @param event ValueChangeEvent<String>
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(ValueChangeEvent<String>)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		final String token = event.getValue();
		
		//remove page styles
		RootPanel.get().removeStyleName("page-training");
		RootPanel.get().removeStyleName("page-nutrition");
		RootPanel.get().removeStyleName("page-cardio");
		RootPanel.get().removeStyleName("page-profile");
		
		//hide print page link
		display.setPrintLinkVisibility(false);
		
		if (token != null) {
			
			try {
				
				//training
				if (token.contains("user/training")) {
					
					//page style
					RootPanel.get().addStyleName("page-training");
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Training());
					
					//analytics
					trackHit("training");
				    
          Date d = null;
          try {
						//check date
						if(token.matches("user/training/[0-9]*(/.*)?")) {
							final long dSec = Integer.parseInt(token.replace("user/training/", ""));
							if(dSec < 1000000000) {
								d = new Date();
							}
							else {
								d = new Date(dSec * 1000);
							}
						}
					} catch (NumberFormatException e) {
			      Motiver.showException(e);
					}
					
					//update tab
					headerUserPresenter.setTab(1);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof TrainingPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}

					if(pagePresenter == null) {
						pagePresenter = new TrainingPagePresenter(rpcService, eventBus, (TrainingPageDisplay)GWT.create(TrainingPageView.class), d);
          }
					
					pagePresenter.run(display.getBodyContainer());
				}
				
				//nutrition
				else if (token.contains("user/nutrition")) {
					
					//page style
					RootPanel.get().addStyleName("page-nutrition");
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Nutrition());
					
					//analytics
					trackHit("nutrition");
					
					//hide print page link
//					display.setPrintLinkVisibility(true);
				    
          Date d = null;
          try {
						//check date
						if(token.matches("user/nutrition/[0-9]*(/.*)?")) {
							final long dSec = Integer.parseInt(token.replace("user/nutrition/", ""));
							if(dSec < 1000000000) {
								d = new Date();
							}
							else {
								d = new Date(dSec * 1000);
							}
						}
					} catch (NumberFormatException e) {
			      Motiver.showException(e);
					}

					//update tab
					headerUserPresenter.setTab(2);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof NutritionPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new NutritionPagePresenter(rpcService, eventBus, (NutritionPageDisplay)GWT.create(NutritionPageView.class), d);
          }
					
					pagePresenter.run(display.getBodyContainer());

				}
				
				//cardio
				else if (token.contains("user/cardio")) {
					
					//page style
					RootPanel.get().addStyleName("page-cardio");
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Cardio());
					
					//analytics
					trackHit("cardio");

					//update tab
					headerUserPresenter.setTab(3);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof CardioPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new CardioPagePresenter(rpcService, eventBus, (CardioPageDisplay)GWT.create(CardioPageView.class));
          }
					
					pagePresenter.run(display.getBodyContainer());
				}
				
				//statistics
				else if (token.contains("user/statistics")) {
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Statistics());

          String target = "";
          try {
						//check date
						if(token.matches("user/statistics/.*")) {
							target = token.replace("user/statistics/", "");
						}
					} catch (Exception e) {
			      Motiver.showException(e);
					}
					
					//analytics
					trackHit("stats");

					//update tab
					headerUserPresenter.setTab(4);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof StatisticsPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new StatisticsPagePresenter(rpcService, eventBus, (StatisticsPageDisplay)GWT.create(StatisticsPageView.class), target);
          }
					
					pagePresenter.run(display.getBodyContainer());
				}			
				//profile
				else if (token.contains("user/profile")) {
					
					//page style
					RootPanel.get().addStyleName("page-profile");
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Profile());
					
					//analytics
					trackHit("profile");

					//update tab
					headerUserPresenter.setTab(5);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof ProfilePagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new ProfilePagePresenter(rpcService, eventBus, (ProfilePageDisplay)GWT.create(ProfilePageView.class));
          }
					
					pagePresenter.run(display.getBodyContainer());
				}			
				//coach
				else if (token.contains("user/coach")) {
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Coach());
					
					//analytics
					trackHit("coach");

					//update tab
					headerUserPresenter.setTab(6);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof CoachPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new CoachPagePresenter(rpcService, eventBus, (CoachPageDisplay)GWT.create(CoachPageView.class));
          }
					
					pagePresenter.run(display.getBodyContainer());
				}			
				//admin
				else if (token.contains("user/admin")) {
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Admin());
					
					//analytics
					trackHit("admin");

					//update tab
					headerUserPresenter.setTab(7);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof AdminPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new AdminPagePresenter(rpcService, eventBus, (AdminPageDisplay)GWT.create(AdminPageView.class));
          }
					
					pagePresenter.run(display.getBodyContainer());
				}
				//main
				else {
					
					//page title
					Window.setTitle("Motiver - " + AppController.Lang.Main());
					
					//analytics
					trackHit("main");

					//update tab
					headerUserPresenter.setTab(0);
					
					//if different page
					if(pagePresenter != null && !(pagePresenter instanceof MainPagePresenter)) {
						pagePresenter.stop();
						pagePresenter = null;
					}
					
					if(pagePresenter == null) {
						pagePresenter = new MainPagePresenter(rpcService, eventBus, (MainPageDisplay)GWT.create(MainPageView.class));
          }
					
          pagePresenter.run(display.getBodyContainer());
				}
				
			} catch (Exception e) {
	      Motiver.showException(e);
			}

      display.showLoading(false);
		}
	}

	/**
	 * Launches timer which reloads page
	 */
	private void setPageReloadTimer() {
		if(timerReload != null) {
			timerReload.cancel();
    }
		timerReload = new Timer() {
			@Override
			public void run() {
				pagePresenter.stop();
				pagePresenter = null;
				
				History.fireCurrentHistoryState();
			}
    };
    timerReload.scheduleRepeating(Constants.DELAY_PAGE_RELOAD);
	}

	/**
	 * Changes Analytics settings.
	 * @param pageName String
	 */
	private native void trackHit(String pageName) /*-{
    try {
      // setup tracking object with account
      var pageTracker = $wnd._gat._getTracker("UA-23160347-1");
      pageTracker._setRemoteServerMode();
      // turn on anchor observing
      pageTracker._setAllowAnchor(true)
      // send event to google server
      pageTracker._trackPageview(pageName);
  	            
    } catch(err) {
    }
	}-*/;
	
	/**
	 * Loads comments and shows comments presenter if necessary
	 */
	protected void loadComments() {
		//not in coach mode
		if(AppController.COACH_MODE_ON) {
			return;
    }
		
		//fetch comments
		final Request req = rpcService.getComments(0, 20, null, AppController.User.getUid(), false, new MyAsyncCallback<List<CommentModel>>() {
			@Override
			public void onSuccess(List<CommentModel> list) {
				//check if new comments
				int newCommentsCount = 0;
				for(CommentModel m : list) {
					if(m != null && m.isUnread()) {
						newCommentsCount++;
					}
				}
				fireEvent(new CommentNewEvent(newCommentsCount));
			}
		});
		addRequest(req);
	}
	
}
