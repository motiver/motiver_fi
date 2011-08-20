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

import com.delect.motiver.client.event.RoutineSelectedEvent;
import com.delect.motiver.client.event.RoutineShowEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.RoutineModel;

/**
 * Shows single routine name as "link"
 * @author Antti
 *
 */
public class RoutineLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RoutineLinkDisplay extends Display {

		public abstract void setHandler(RoutineLinkHandler routineLinkHandler);
		public abstract void setModel(RoutineModel routine);
		public abstract void setQuickSelect(boolean quickSelectOn);
	}

	public interface RoutineLinkHandler {
		void quickSelect(boolean selected);
		void selected();
	}
	private RoutineLinkDisplay display;

	private boolean quickSelectOn = false;

	protected RoutineModel routine;

	public RoutineLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RoutineLinkDisplay display, RoutineModel routine, boolean quickSelectOn) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.routine = routine;
    this.quickSelectOn  = quickSelectOn;
	}
	
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setModel(routine);
		display.setQuickSelect(quickSelectOn);
		display.setHandler(new RoutineLinkHandler() {
			@Override
			public void quickSelect(boolean selected) {
				//fire event
				RoutineSelectedEvent event = new RoutineSelectedEvent(routine, selected);
				fireEvent(event);
					
			}
			@Override
			public void selected() {
				//fire event
				fireEvent(new RoutineShowEvent(routine));
			}
		});
	}

}
