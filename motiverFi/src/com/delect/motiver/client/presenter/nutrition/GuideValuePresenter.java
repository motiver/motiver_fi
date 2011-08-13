/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.nutrition;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.GuideValueRemovedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.GuideValueModel;

/**
 * Shows single guide value
 * @author Antti
 *
 */
public class GuideValuePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class GuideValueDisplay extends Display {

		public abstract void setHandler(GuideValueHandler guideValueHandler);
		public abstract void setModel(GuideValueModel value);
	}

	public interface GuideValueHandler {
		void valueRemoved();
	}
	private GuideValueDisplay display;

	protected GuideValueModel value;

	public GuideValuePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, GuideValueDisplay display, GuideValueModel value) {
		super(rpcService, eventBus);
		this.display = display;
	    
    this.value = value;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		display.setModel(value);
		if(value.getId() != 0) {
			
			//event handler (fire event)
			display.setHandler(new GuideValueHandler() {
				@Override
				public void valueRemoved() {
					display.setContentEnabled(false);
					
					List<GuideValueModel> list = new ArrayList<GuideValueModel>();
					list.add(value);
					final Request req = rpcService.removeGuideValues(list, new MyAsyncCallback<Boolean>() {
						@Override
						public void onSuccess(Boolean result) {
							
							stop();
							
							//fire event
							eventBus.fireEvent(new GuideValueRemovedEvent(value));
						}
					});
					addRequest(req);
				}
			});
		}
		
	}

}
