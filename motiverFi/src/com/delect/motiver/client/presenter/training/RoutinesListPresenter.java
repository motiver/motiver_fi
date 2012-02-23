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
package com.delect.motiver.client.presenter.training;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.RoutineCreatedEvent;
import com.delect.motiver.client.event.RoutineRemovedEvent;
import com.delect.motiver.client.event.RoutineSelectCancelledEvent;
import com.delect.motiver.client.event.RoutineSelectedEvent;
import com.delect.motiver.client.event.RoutineShowEvent;
import com.delect.motiver.client.event.UserSelectedEvent;
import com.delect.motiver.client.event.handler.RoutineCreatedEventHandler;
import com.delect.motiver.client.event.handler.RoutineRemovedEventHandler;
import com.delect.motiver.client.event.handler.RoutineSelectedEventHandler;
import com.delect.motiver.client.event.handler.RoutineShowEventHandler;
import com.delect.motiver.client.event.handler.UserSelectedEventHandler;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.training.RoutinePresenter.RoutineDisplay;
import com.delect.motiver.client.presenter.training.RoutinesListSearchPresenter.RoutinesListSearchDisplay;
import com.delect.motiver.client.presenter.training.RoutinesListSubPresenter.RoutinesListSubDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.training.RoutineView;
import com.delect.motiver.client.view.training.RoutinesListSearchView;
import com.delect.motiver.client.view.training.RoutinesListSubView;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * 3 "pages": (with search box on top)
 *  <br>- main page (shows user's routines, most popular)
 *  <br>- search results
 *  <br>- single routine (when individual routine is selected)
 *  <br>- Fires routine created event when routine is selected and stops itself 
 * @author Antti
 *
 */
public class RoutinesListPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RoutinesListDisplay extends Display {

		public abstract LayoutContainer getDataContainer();
		public abstract void setBackButtonVisible(boolean b);
		public abstract void setCancelButtonVisible(boolean b);
		public abstract void setCopyButtonVisible(boolean b);
		public abstract void setHandler(RoutinesListHandler routinesListHandler);
		public abstract void setMoveToDateButtonVisible(boolean b, Date date);
		public abstract void setQuickSelectionButtonVisible(boolean b, Date date);
	}

	public interface RoutinesListHandler {
		void onBackButtonClicked();
		void onCancelButtonClicked();
		void onCopyButtonClicked();
		void onMoveToDateButtonClicked();
		void onQuickSelectionButtonClicked();
		void search(String query);
	}
	private Date date;

	private RoutinesListDisplay display;
	private String lastQuery = "";
	private int lastView = 0;
	private List<RoutineModel> quickSelectionRoutines = new ArrayList<RoutineModel>();	//meals which are selected
	
	private long routineId = 0;

	private RoutinesListSearchPresenter routinesListSearchPresenter;
	//child presenters
	private RoutinesListSubPresenter routinesListSubPresenter;
	private RoutinePresenter singleRoutinePresenter;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param date
	 * @param routineId : if some routine is open as default
	 */
	public RoutinesListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RoutinesListDisplay display, Date date, long routineId) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.date = date;
    this.routineId  = routineId;

		boolean quickSelectionEnabled = (date != null);
    routinesListSubPresenter = new RoutinesListSubPresenter(rpcService, eventBus, (RoutinesListSubDisplay)GWT.create(RoutinesListSubView.class), quickSelectionEnabled);
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		display.setHandler(new RoutinesListHandler() {
			@Override
			public void onBackButtonClicked() {
				//coming back from search results
				if(singleRoutinePresenter == null) {
					showMainView();
				}
				//if coming back from single exercise
				else {
					//unbind routine
					singleRoutinePresenter.stop();
					singleRoutinePresenter = null;
					
					//last view was search results
					if(lastView == 1) {
						loadSearch(lastQuery);
		      }
					//last view was main menu
					else {
						showMainView();
		      }
				}
			}
			@Override
			public void onCancelButtonClicked() {
				//unbind this
				stop();
				//fire cancel event
				eventBus.fireEvent(new RoutineSelectCancelledEvent(date));
			}
			@Override
			public void onCopyButtonClicked() {
				copyRoutine();
			}
			@Override
			public void onMoveToDateButtonClicked() {
				//get meal from single presenter
				List<RoutineModel> routines = new ArrayList<RoutineModel>();
				routines.add(singleRoutinePresenter.routine);
				
				moveRoutineToDate(routines);
			}
			@Override
			public void onQuickSelectionButtonClicked() {
				//get meals from quick selection
				moveRoutineToDate(quickSelectionRoutines);
				quickSelectionRoutines.clear();
			}
			@Override
			public void search(String query) {
				loadSearch(query);
			}
		});

		//EVENT: routine created -> open routine
		addEventHandler(RoutineCreatedEvent.TYPE, new RoutineCreatedEventHandler() {
			@Override
			public void onRoutineCreated(RoutineCreatedEvent event) {
				//if no date
				if(event.getRoutine() != null) {
					if(event.getRoutine().getDate() == null) {
						showSingleRoutine(event.getRoutine());
					}
				}
			}
		});
		
		//EVENT: routine removed -> show main view
		addEventHandler(RoutineRemovedEvent.TYPE, new RoutineRemovedEventHandler() {
			@Override
			public void onRoutineRemoved(RoutineRemovedEvent event) {
				//if routine in list
				if(event.getRoutine() != null) {
          showMainView();
	      }
			}
		});
		
		//EVENT: show routine
		addEventHandler(RoutineShowEvent.TYPE, new RoutineShowEventHandler() {
			@Override
			public void selectRoutine(RoutineShowEvent event) {
				showSingleRoutine(event.getRoutine());
			}
		});
		
		//EVENT: routine selected (quick select)
		addEventHandler(RoutineSelectedEvent.TYPE, new RoutineSelectedEventHandler() {
			@Override
			public void routineSelected(RoutineSelectedEvent event) {
				setQuickSelection(event.getRoutine(), event.isSelected());
			}
		});
      
    //EVENT: user selected -> show blog
    addEventHandler(UserSelectedEvent.TYPE, new UserSelectedEventHandler() {
      @Override
      public void userSelected(UserSelectedEvent event) {
        //open blog in new window
        Window.open(event.getUser().getBlogUrl(), "_blank", "status=1,toolbar=1,location=1,menubar=1,directories=1,resizable=1,scrollbars=1");
      }
    });
		
    //if date -> show inner title
    if(date != null) {
      //info text
      display.setTitle(AppController.Lang.SelectRoutine());
      display.setCancelButtonVisible(true);
    }
	}
	
	@Override
	public void onRefresh() {
	  super.onRefresh();
    
    //highlight and scroll
    highlight();
	}

	@Override
	public void onRun() {
	    
    //show single routine
    if(routineId != 0) {
			rpcService.getRoutine(routineId, new MyAsyncCallback<RoutineModel>() {
				@Override
				public void onSuccess(RoutineModel result) {
          showSingleRoutine(result);
        }
			});
    }	    	
    else {
      showMainView();
    }
    
    //highlight and scroll
    highlight();
  }
	
	@Override
	public void onStop() {
		if(routinesListSubPresenter != null) {
			routinesListSubPresenter.stop();
    }
		if(singleRoutinePresenter != null) {
			singleRoutinePresenter.stop();
    }
		if(routinesListSearchPresenter != null) {
			routinesListSearchPresenter.stop();
    }
	}


	/**
	 * Copies routine ro routine or to our routines
	 */
	protected void copyRoutine() {
		
		try {
			display.setContentEnabled(false);
			
			//get routine from single presenter
			RoutineModel model = singleRoutinePresenter.routine;
			
			//reset date
			model.setDate(null);
			
			//add routine
			rpcService.addRoutine(model, new MyAsyncCallback<RoutineModel>() {
				@Override
				public void onSuccess(RoutineModel result) {
					display.setContentEnabled(true);
					
					//fire event
					eventBus.fireEvent(new RoutineCreatedEvent(result));
				}
			});
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Moves routine to date
	 */
	protected void moveRoutineToDate(List<RoutineModel> routines) {
		try {
			display.setContentEnabled(false);
			
			//set date
			for(RoutineModel model : routines)
      model.setDate(CommonUtils.trimDateToDatabase(date, true));
			
			//add routine
			rpcService.addRoutines(routines, new MyAsyncCallback<List<RoutineModel>>() {
				@Override
				public void onSuccess(List<RoutineModel> result) {
					display.setContentEnabled(true);
					
					stop();
					
					List<RoutineModel> routines = result;

					//only single event for now
					//TODO kaipaa korjausta
					if(routines.size() > 0) {
						RoutineModel routine = routines.get(0);
						routine.setDate(CommonUtils.trimDateToDatabase(date, true));
					
						//fire event
						eventBus.fireEvent(new RoutineCreatedEvent(routine));
					}
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}


	/**
	 * Called when routines is selected (or selection is set off)
	 * <br>Shows button to quick select routines
	 * @param routine
	 * @param selected
	 */
	protected void setQuickSelection(RoutineModel routine, boolean selected) {
		//if new routine
		if(selected) {
			quickSelectionRoutines.add(routine);
    }
		else {
			quickSelectionRoutines.remove(routine);
    }

		//only if models selected and date not null
		display.setQuickSelectionButtonVisible(quickSelectionRoutines.size() > 0 && date != null, date);
	}


	/**
	 * Shows main view
	 *  - my routines
	 *  - most popular
	 */
	protected void showMainView() {

		lastView = 0;
		
		//show/hide buttons
		display.setBackButtonVisible(false);
		display.setCopyButtonVisible(false);
		display.setMoveToDateButtonVisible(false, null);
		display.setQuickSelectionButtonVisible(false, null);
		
		if(singleRoutinePresenter != null) {
			singleRoutinePresenter.stop();
    }
		singleRoutinePresenter = null;
		if(routinesListSearchPresenter != null) {
			routinesListSearchPresenter.hide();
    }

		//run main view
		routinesListSubPresenter.run(display.getDataContainer());
			
	}

	/*
	 * VIEW 3 (single routine
	 */
	protected void showSingleRoutine(RoutineModel m) {
		
		try {
			if(singleRoutinePresenter != null) {
				singleRoutinePresenter.stop();
      }
			
			//hide main page
			if(routinesListSubPresenter != null) {
				routinesListSubPresenter.hide();
      }
			if(routinesListSearchPresenter != null) {
				routinesListSearchPresenter.hide();
      }
			
			//show single routine
			singleRoutinePresenter = new RoutinePresenter(rpcService, eventBus, (RoutineDisplay)GWT.create(RoutineView.class), m);
			singleRoutinePresenter.run(display.getDataContainer());
			
			//buttons
			display.setBackButtonVisible(true);
			display.setCopyButtonVisible(false);
			display.setMoveToDateButtonVisible(false, null);
			display.setQuickSelectionButtonVisible(false, null);

			//show buttons only if routine's id set
			if(m.getId() > 0) {
				//if date set
				if(date != null) {
					display.setMoveToDateButtonVisible(true, date);
				}
				else {
					//copy to our routines
					if(!m.getUser().equals(AppController.User)) {
						display.setCopyButtonVisible(true);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads routines
	 */
	void loadSearch(final String query) {

		lastView = 1;
		
		//show/hide buttons
		display.setBackButtonVisible(true);
		display.setCopyButtonVisible(false);
		display.setMoveToDateButtonVisible(false, null);
		display.setQuickSelectionButtonVisible(false, null);
		
		if(singleRoutinePresenter != null) {
			singleRoutinePresenter.stop();
    }
		singleRoutinePresenter = null;
		if(routinesListSubPresenter != null) {
			routinesListSubPresenter.hide();
    }
		
		//if not already loaded
		if(routinesListSearchPresenter == null || !query.equals(lastQuery)) {
			lastQuery  = query;
			if(routinesListSearchPresenter != null) {
				routinesListSearchPresenter.stop();
      }
			
			boolean quickSelectionEnabled = (date != null);
			routinesListSearchPresenter = new RoutinesListSearchPresenter(rpcService, eventBus, (RoutinesListSearchDisplay)GWT.create(RoutinesListSearchView.class), query, quickSelectionEnabled);
			routinesListSearchPresenter.run(display.getDataContainer());
		}
		else {
			routinesListSearchPresenter.run(display.getDataContainer());
    }
	}
}
