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
package com.delect.motiver.client.presenter.cardio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.History;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.RunValueCreatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;

/**
 * Shows window where user can add new run value
 * @author Antti
 *
 */
public class AddNewRunValuePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class AddNewRunValueDisplay extends Display {
		public abstract void setDate(Date date);
		public abstract void setHandler(AddNewRunValueHandler handler);
		public abstract void setModels(List<RunModel> models, boolean enabled);
	}

	public interface AddNewRunValueHandler {
		void cancel();
		void newValue(RunModel run, RunValueModel value);
	}
	private Date date;

	private AddNewRunValueDisplay display;
	private RunModel run;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param run : if null -> shows all runs
	 */
	public AddNewRunValuePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, AddNewRunValueDisplay display, RunModel run, Date date) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.run = run;
    this.date = date;
	}

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		
		//if single model
		if(run != null) {
			List<RunModel> ls = new ArrayList<RunModel>();
			ls.add(run);
			display.setModels(ls, false);
		}
		display.setDate(date);
		
		display.setHandler(new AddNewRunValueHandler() {

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
			public void newValue(RunModel run, RunValueModel value) {
				
				display.setContentEnabled(false);

				final Request req = rpcService.addRunValue(run, value, new MyAsyncCallback<RunValueModel>() {
					@Override
					public void onSuccess(RunValueModel result) {
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
							RunValueModel value = result;
							value.setDate(Functions.getDateGmt(value.getDate()));
							
							if(result != null) {
                eventBus.fireEvent(new RunValueCreatedEvent(value));
				      }
							
						} catch (Exception e) {
				      Motiver.showException(e);
						}
					}
				});
				addRequest(req);
			}
		});

		//add "/add"+string to history
		History.newItem(History.getToken() + "/add", false);
	}

	@Override
	public void onRun() {
		
		//if no run set -> fetch all
		if(run == null) {
      loadRuns();
    }
	}

	/**
	 * Loads values
	 */
	void loadRuns() {

    //get runs
		final Request req = rpcService.getRuns(0, new MyAsyncCallback<List<RunModel>>() {
			@Override
			public void onSuccess(List<RunModel> result) {
				display.setModels(result, true);
			}
		});
		addRequest(req);
	}

}
