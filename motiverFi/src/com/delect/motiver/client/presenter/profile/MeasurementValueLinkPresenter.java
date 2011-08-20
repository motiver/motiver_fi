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
package com.delect.motiver.client.presenter.profile;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.MeasurementShowEvent;
import com.delect.motiver.client.event.MeasurementValueRemovedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.profile.MeasurementPresenter.MeasurementDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.profile.MeasurementView;
import com.delect.motiver.shared.MeasurementValueModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Shows single measurement value name as "link"
 * @author Antti
 *
 */
public class MeasurementValueLinkPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MeasurementValueLinkDisplay extends Display {

		public abstract LayoutContainer getBodyContainer();
		public abstract void setHandler(MeasurementValueLinkHandler measurementValueLinkHandler);
		public abstract void setModel(MeasurementValueModel measurementValue);
	}
	public interface MeasurementValueLinkHandler {
		void selected();
		void valueRemoved();
	}

	private MeasurementValueLinkDisplay display;

	//child presenters
	private MeasurementPresenter measurementPresenter;

	private MeasurementValueModel measurementValue;

	public MeasurementValueLinkPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MeasurementValueLinkDisplay display, MeasurementValueModel measurementValue) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.measurementValue = measurementValue;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new MeasurementValueLinkHandler() {
			@Override
			public void selected() {
				fireEvent(new MeasurementShowEvent(measurementValue.getName()));
			}

			@Override
			public void valueRemoved() {
				display.setContentEnabled(false);
				
				//remove value and stop itself
				List<MeasurementValueModel> list = new ArrayList<MeasurementValueModel>();
				list.add(measurementValue);
				rpcService.removeMeasurementValues(measurementValue.getName(), list, new MyAsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						stop();
						
						fireEvent(new MeasurementValueRemovedEvent(measurementValue));
					}
				});
			}
		});

		display.setModel(measurementValue);
	}

	/**
	 * Shows measurement's presenter where this value belongs
	 */
	public void showMeasurement() {
		
		//if visible
		if(measurementPresenter != null) {
			measurementPresenter.stop();
			measurementPresenter = null;
		}
		else {
			measurementPresenter = new MeasurementPresenter(rpcService, eventBus, (MeasurementDisplay)GWT.create(MeasurementView.class), measurementValue.getName(), false, true);
			measurementPresenter.run(display.getBodyContainer());
		}
	}

}
