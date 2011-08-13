/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.GuideValuesListPresenter;
import com.delect.motiver.client.presenter.nutrition.GuideValuesListPresenter.GuideValuesHandler;
import com.delect.motiver.client.view.SmallNotePanel;
import com.delect.motiver.client.view.SmallNotePanelDisplay;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;


public class GuideValuesListView extends GuideValuesListPresenter.GuideValuesListDisplay {

	private GuideValuesHandler handler;
	
	private SmallNotePanelDisplay panelBase = (SmallNotePanelDisplay)GWT.create(SmallNotePanel.class);

	/**
	 * Guide values view
	 */
	public GuideValuesListView() {

		panelBase.setStylePrefix("panel-guides");
		
		panelBase.getPanelData().addListener(Events.Show, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.valuesVisible();
			}
		});
		panelBase.getPanelData().addListener(Events.Hide, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.valuesHidden();
			}
		});
		
		this.add(panelBase);

	}
	
	@Override
	public Widget asWidget() {

		panelBase.getPanelData().setStyleAttribute("min-height", "250px");

		//title
		panelBase.setTitleText(AppController.Lang.GuideValues());
		
		//add new value -link
		panelBase.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.GuideValue().toLowerCase()), 
    new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.newValue();
      }
    });

		panelBase.getPanelData().setVisible(false);
		panelBase.getPanelData().layout();
		
		return this;
	}
		
	@Override
	public LayoutContainer getBodyContainer() {
		return panelBase.getPanelData();
	}

	@Override
	public void setHandler(GuideValuesHandler handler) {
		this.handler = handler;
	}

}
