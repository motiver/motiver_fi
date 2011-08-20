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
