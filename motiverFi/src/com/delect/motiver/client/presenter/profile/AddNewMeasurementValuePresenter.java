/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.MeasurementValueCreatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.MeasurementValueModel;

/**
 * Shows window where user can add new guide value
 * @author Antti
 *
 */
public class AddNewMeasurementValuePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class AddNewMeasurementValueDisplay extends Display {

		public abstract void setHandler(AddNewMeasurementValueHandler addNewMeasurementValueHandler);
		public abstract void setMeasurement(MeasurementModel measurement);
	}

	public interface AddNewMeasurementValueHandler {
		void cancel();
		void newValue(MeasurementValueModel model);
	}
	private AddNewMeasurementValueDisplay display;

	private MeasurementModel measurement;

	public AddNewMeasurementValuePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, AddNewMeasurementValueDisplay display, MeasurementModel measurement) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.measurement = measurement;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		//add "/add"+string to history
		History.newItem(History.getToken() + "/add", false);
		
		display.setMeasurement(measurement);
		display.setHandler(new AddNewMeasurementValueHandler() {

			@Override
			public void cancel() {
				
				//remove add-string from token
				String token = History.getToken();
				if(token.length() > 4) {
					if(token.substring(token.length() - 4, token.length()).equals("/add")) {
						token = token.substring(0, token.length() - 4);
						History.newItem(token, false);	
					}
				}
				
				stop();
			}
			@Override
			public void newValue(MeasurementValueModel model) {
				
				display.setContentEnabled(false);
				
				// TODO Create new guide value and fire add event
				rpcService.addMeasurementValue(measurement, model, new MyAsyncCallback<MeasurementValueModel>() {
					@Override
					public void onSuccess(MeasurementValueModel result) {
						try {
							display.setContentEnabled(true);

							//remove add-string from token
							String token = History.getToken();
							if(token.length() > 4) {
								if(token.substring(token.length() - 4, token.length()).equals("/add")) {
									token = token.substring(0, token.length() - 4);
									History.newItem(token, false);	
								}
							}
							
							stop();

							//set date
							MeasurementValueModel value = result;
							value.setDate(Functions.getDateGmt(value.getDate()));
							
							if(result != null) {
								eventBus.fireEvent(new MeasurementValueCreatedEvent(value));
				      }
							
						} catch (Exception e) {
				      Motiver.showException(e);
						}
					}
				});
			}
		});
	}

}
