/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.training;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.RoutineCreatedEvent;
import com.delect.motiver.client.event.RoutineRemovedEvent;
import com.delect.motiver.client.event.RoutineShowEvent;
import com.delect.motiver.client.event.handler.RoutineCreatedEventHandler;
import com.delect.motiver.client.event.handler.RoutineRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreDisplay;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;
import com.delect.motiver.client.presenter.training.RoutineLinkPresenter.RoutineLinkDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.ShowMoreView;
import com.delect.motiver.client.view.training.RoutineLinkView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RoutineModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Main page for routines' list
 * <br>Launch RoutineSelectEvent when routine is selected
 * <br>Launch RoutineCreatedEvent when routine is created
 * @author Antti
 *
 */
public class RoutinesListSubPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RoutinesListSubDisplay extends Display {

		public abstract LayoutContainer getMostPopularContainer();
		public abstract LayoutContainer getMyRoutinesContainer();
		public abstract void setHandler(RoutinesListSubHandler routinesListSubHandler);
	}

	public interface RoutinesListSubHandler {
		void createRoutine(String name);
	}
	private RoutinesListSubDisplay display;

	private EmptyPresenter emptyPresenter;
	private EmptyPresenter emptyPresenter2;
	private List<RoutineLinkPresenter> mostPopularPresenters = new ArrayList<RoutineLinkPresenter>();
	//child presenters
	private List<RoutineLinkPresenter> myRoutinePresenters = new ArrayList<RoutineLinkPresenter>();
	private boolean quickSelectionEnabled;
	private boolean reloadRoutines;
	
	private ShowMorePresenter showMorePresenter;
	private ShowMorePresenter showMorePresenter2;
	
	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public RoutinesListSubPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RoutinesListSubDisplay display, boolean quickSelectionEnabled) {
		super(rpcService, eventBus);
		this.display = display;

		this.quickSelectionEnabled = quickSelectionEnabled;
	}
	
	@Override
	public Display getView() {
		return display;
	}
	@Override
	public void onBind() {
		display.setHandler(new RoutinesListSubHandler() {

			@Override
			public void createRoutine(String name) {
				//Create new routine
				RoutineModel model = new RoutineModel();

				eventBus.fireEvent(new RoutineShowEvent(model));
			}
			
		});
		
		//EVENT: routine removed
		addEventHandler(RoutineRemovedEvent.TYPE, new RoutineRemovedEventHandler() {
			@Override
			public void onRoutineRemoved(RoutineRemovedEvent event) {
				//if routine in list
				if(event.getRoutine() != null && event.getRoutine().getDate() == null) {
          removeMyRoutinePresenter(event.getRoutine());
          removeMostPopularPresenter(event.getRoutine());
        }
			}
		});
		
		//EVENT: routine created -> reload routines
		addEventHandler(RoutineCreatedEvent.TYPE, new RoutineCreatedEventHandler() {
			@Override
			public void onRoutineCreated(RoutineCreatedEvent event) {
				reloadRoutines  = true;
			}
		});
	}


	@Override
	public void onRefresh() {
		//reload if routines created
		if(reloadRoutines) {
			loadMyRoutines(0);
    }
		
		reloadRoutines = false;
	}


	@Override
	public void onRun() {
	    
		loadMyRoutines(0);
		loadMostPopularRoutines(0);
	}


	@Override
	public void onStop() {

		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		if(emptyPresenter2 != null) {
			emptyPresenter2.stop();
    }
		if(showMorePresenter != null) {
			showMorePresenter.stop();	
    }
		if(showMorePresenter2 != null) {
			showMorePresenter2.stop();
    }
		
		//stop presenters
		unbindPresenters(0);
	}


	/**
	 * Removes presenter from most popular routines
	 * @param routine
	 */
	private void removeMostPopularPresenter(RoutineModel routine) {

		try {
			//remove also from presenters
			for(int i=0; i < mostPopularPresenters.size(); i++) {
				RoutineLinkPresenter presenter = mostPopularPresenters.get(i);
				if(presenter != null && presenter.routine.getId() == routine.getId()) {
          presenter.stop();
          mostPopularPresenters.remove(presenter);
        }
			}

			//if no routines -> show empty presenter
			if(mostPopularPresenters.size() == 0) {
				if(emptyPresenter2 != null) {
					emptyPresenter2.stop();
	      }
				emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoRoutines());
				emptyPresenter2.run(display.getMostPopularContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Removes presenter from user routines
	 * @param routine
	 */
	private void removeMyRoutinePresenter(RoutineModel routine) {

		try {
			//remove also from presenters
			for(int i=0; i < myRoutinePresenters.size(); i++) {
				RoutineLinkPresenter presenter = myRoutinePresenters.get(i);
				if(presenter != null && presenter.routine.getId() == routine.getId()) {
					presenter.stop();
					myRoutinePresenters.remove(presenter);
				}
			}

			//if no routines -> show empty presenter
			if(myRoutinePresenters.size() == 0) {
				if(emptyPresenter != null) {
					emptyPresenter.stop();
	      }
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoRoutines());
				emptyPresenter.run(display.getMyRoutinesContainer());
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Shows most popular routines
	 * @param list : RoutineModels
	 * @param openFirst : open first routine
	 */
	private void showMostPopularRoutines(final int index, List<RoutineModel> list) {

		try {

			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
      }
			//stop show more
			if(showMorePresenter2 != null) {
				showMorePresenter2.stop();
      }
			//stop presenters if first items
			if(index == 0) {
				unbindPresenters(2);
      }
			
			//if no routines
			if(index == 0 && list.size() == 0) {
				emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoRoutines());
				emptyPresenter2.run(display.getMostPopularContainer());
			}
			else {
				
				for(final RoutineModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter2 = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMostPopularRoutines(index + Constants.LIMIT_ROUTINES);								
							}
						});
						showMorePresenter2.run(display.getMostPopularContainer());
					}
					else {		
						//new presenter
						final RoutineLinkPresenter wp = new RoutineLinkPresenter(rpcService, eventBus, (RoutineLinkDisplay)GWT.create(RoutineLinkView.class), m, quickSelectionEnabled);
						addNewMostPopularPresenter(wp);
					}
					
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Shows user's routines
	 * @param list : RoutineModels
	 */
	private void showMyRoutines(final int index, List<RoutineModel> list) {

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
				unbindPresenters(1);
      }
			
			//if no routines
			if(index == 0 && list.size() == 0) {
				emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoRoutines());
				emptyPresenter.run(display.getMyRoutinesContainer());
			}
			else {
				
				for(final RoutineModel m : list) {	
					
					//if null value -> list was limited -> add showMorePresenter
					if(m == null) {
						showMorePresenter = new ShowMorePresenter(rpcService, eventBus, (ShowMoreDisplay)GWT.create(ShowMoreView.class), new ShowMoreHandler() {
							@Override
							public void showMore() {
								loadMyRoutines(index + Constants.LIMIT_ROUTINES);								
							}
						});
						showMorePresenter.run(display.getMyRoutinesContainer());
					}
					else {		
						//new presenter
						final RoutineLinkPresenter wp = new RoutineLinkPresenter(rpcService, eventBus, (RoutineLinkDisplay)GWT.create(RoutineLinkView.class), m, quickSelectionEnabled);
						addNewMyRoutinePresenter(wp);
					}
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Unbinds all the presenters
	 * @param target : which presenters all unbinded, 0=all, 1=myroutines, 2=most popular, 3=search results
	 */
	private void unbindPresenters(int target) {
			
		//my routines
		if(myRoutinePresenters != null && (target == 0 || target == 1)) {

			if(emptyPresenter != null) {
				emptyPresenter.stop();
      }
			if(showMorePresenter != null) {
				showMorePresenter.stop();
      }	
			
			for(int i=0; i < myRoutinePresenters.size(); i++) {
				final Presenter presenter = myRoutinePresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			myRoutinePresenters.clear();
		}				
		//most popular
		if(mostPopularPresenters != null && (target == 0 || target == 2)) {

			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
      }
			if(showMorePresenter2 != null) {
				showMorePresenter2.stop();
      }
			
			for(int i=0; i < mostPopularPresenters.size(); i++) {
				final Presenter presenter = mostPopularPresenters.get(i);
				if(presenter != null) {
					presenter.stop();
				}
			}
			mostPopularPresenters.clear();
		}
	}


	/**
	 * Adds new presenter to view (most popular routines)
	 * @param presenter
	 */
	protected void addNewMostPopularPresenter(RoutineLinkPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter2 != null) {
				emptyPresenter2.stop();
				emptyPresenter2 = null;
			}
			
			mostPopularPresenters.add(presenter);
			presenter.run(display.getMostPopularContainer());
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}


	/**
	 * Adds new presenter to view (my routines)
	 * @param presenter
	 */
	protected void addNewMyRoutinePresenter(RoutineLinkPresenter presenter) {
		
		try {
			//remove emptypresenter if present
			if(emptyPresenter != null) {
				emptyPresenter.stop();
				emptyPresenter = null;
			}
			
			myRoutinePresenters.add(presenter);
			presenter.run(display.getMyRoutinesContainer());
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Loads routines
	 */
	void loadMostPopularRoutines(final int index) {
    		
		if(emptyPresenter2 != null) {
			emptyPresenter2.stop();
    }
		//stop show more
		if(showMorePresenter2 != null) {
			showMorePresenter2.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters(2);
    }

		//add empty presenter
		emptyPresenter2 = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter2.run(display.getMostPopularContainer());
		
		//load most popular
    Motiver.setNextCallCacheable(true);
    final Request req = rpcService.getMostPopularRoutines(index, new MyAsyncCallback<List<RoutineModel>>() {
			@Override
			public void onSuccess(List<RoutineModel> routines) {
				showMostPopularRoutines(index, routines);
      }
		});
    addRequest(req);
	}

	/**
	 * Loads routines
	 */
	void loadMyRoutines(final int index) {
    		
		if(emptyPresenter != null) {
			emptyPresenter.stop();
    }
		//stop show more
		if(showMorePresenter != null) {
			showMorePresenter.stop();
    }
		//stop presenters if first items
		if(index == 0) {
			unbindPresenters(1);
    }

		//add empty presenter
		emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
		emptyPresenter.run(display.getMyRoutinesContainer());

    //get routines
    Motiver.setNextCallCacheable(true);
		final Request req = rpcService.getRoutines(index, new MyAsyncCallback<List<RoutineModel>>() {
			@Override
			public void onSuccess(List<RoutineModel> routines) {
				showMyRoutines(index, routines);
      }
		});
		addRequest(req);
	}

}
