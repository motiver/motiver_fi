/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.cardio;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.RunShowEvent;
import com.delect.motiver.client.event.RunValueRemovedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.cardio.RunPresenter.RunDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.cardio.RunView;
import com.delect.motiver.shared.RunValueModel;

/**
 * Shows single run value name as "link"
 * @author Antti
 *
 */
public class RunValueLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RunValueLinkDisplay extends Display {
		public abstract void setHandler(RunValueLinkHandler runValueLinkHandler);
		public abstract void setModel(RunValueModel runValue);
	}

	public interface RunValueLinkHandler {
		void selected();
		void valueRemoved();
	}
	public RunValueModel runValue;

	//child presenters
	private RunPresenter runPresenter;
	
	RunValueLinkDisplay display;

	public RunValueLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RunValueLinkDisplay display, RunValueModel runValue) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.runValue = runValue;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new RunValueLinkHandler() {
			@Override
			public void selected() {
				fireEvent(new RunShowEvent(runValue.getName()));
			}

			@Override
			public void valueRemoved() {
				display.setContentEnabled(false);
				
				//remove value and stop itself
				List<RunValueModel> list = new ArrayList<RunValueModel>();
				list.add(runValue);
				final Request req = rpcService.removeRunValues(runValue.getName(), list, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						fireEvent(new RunValueRemovedEvent(runValue));
					}
				});
				addRequest(req);
			}
		});
		display.setModel(runValue);
	}

	/**
	 * Shows run's presenter where this value belongs
	 */
	public void showRun() {
		
		//if visible
		if(runPresenter != null) {
			runPresenter.stop();
			runPresenter = null;
		}
		else {
			runPresenter = new RunPresenter(rpcService, eventBus, (RunDisplay)GWT.create(RunView.class), runValue.getName(), false, true);
			runPresenter.run(display.getBaseContainer());
		}
	}

}
