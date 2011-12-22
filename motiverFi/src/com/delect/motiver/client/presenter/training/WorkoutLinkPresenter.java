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

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.WorkoutRemovedEvent;
import com.delect.motiver.client.event.WorkoutSelectedEvent;
import com.delect.motiver.client.event.WorkoutShowEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Shows single workout name as "link"
 * @author Antti
 *
 */
public class WorkoutLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class WorkoutLinkDisplay extends Display {
		public abstract void setHandler(WorkoutLinkHandler handler);
		public abstract void setModel(WorkoutModel workout);
		public abstract void setQuickSelect(boolean quickSelectOn);
	}
	public interface WorkoutLinkHandler {
		void quickSelect(boolean selected);
		void selected();
		void workoutRemoved();
	}
	private WorkoutLinkDisplay display;

	private boolean quickSelectOn = false;
	
	protected WorkoutModel workout;

	public WorkoutLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, WorkoutLinkDisplay display, WorkoutModel workout, boolean quickSelectOn) {
		super(rpcService, eventBus);
		this.display = display;
		this.display = display;
	    
    this.workout = workout;
    this.quickSelectOn  = quickSelectOn;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(workout);
		display.setQuickSelect(quickSelectOn);
		display.setHandler(new WorkoutLinkHandler() {
			@Override
			public void quickSelect(boolean selected) {
				//fire event
				WorkoutSelectedEvent event = new WorkoutSelectedEvent(workout, selected);
				fireEvent(event);
					
			}
			@Override
			public void selected() {
			  System.out.println(workout.getName());
				//fire event
				fireEvent(new WorkoutShowEvent(workout));
			}
			@Override
			public void workoutRemoved() {
				display.setContentEnabled(false);
				
				//remove workout and stop itself
				Request req = rpcService.removeWorkout(workout, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						eventBus.fireEvent(new WorkoutRemovedEvent(workout));
					}
				});
				addRequest(req);
			}
		});
	}
}
