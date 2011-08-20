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
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.training.RoutinesListSubPresenter;
import com.delect.motiver.client.presenter.training.RoutinesListSubPresenter.RoutinesListSubHandler;
import com.delect.motiver.client.view.MyContentPanel;
import com.delect.motiver.client.view.widget.MyButton;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class RoutinesListSubView extends RoutinesListSubPresenter.RoutinesListSubDisplay {

	private RoutinesListSubHandler handler;
	private MyContentPanel panelMostPopular = new MyContentPanel();

	//panels
	private MyContentPanel panelMyRoutines = new MyContentPanel();
	
	public RoutinesListSubView() {

		//VIEW 1
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
    this.setLayout(tl);
    TableData td = new TableData();
    td.setVerticalAlign(VerticalAlignment.TOP);
    td.setWidth("50%");
		
		//my routines
    panelMyRoutines.addStyleName("panel-routines-my");
		panelMyRoutines.setHeading(AppController.Lang.MyRoutines());
		panelMyRoutines.setLayout(new RowLayout());
		panelMyRoutines.setStyleAttribute("margin-right", "5px");
		this.add(panelMyRoutines, td);
		//most popular
		panelMostPopular.addStyleName("panel-routines-mostpopular");
		panelMostPopular.setHeading(AppController.Lang.MostPopular());
		this.add(panelMostPopular, td);
		
		//new routine button
		MyButton btnAddRoutine = new MyButton(AppController.Lang.CreateTarget(AppController.Lang.Routine().toLowerCase()));
		btnAddRoutine.setScale(ButtonScale.MEDIUM);
		btnAddRoutine.setColor(MyButton.Style.GREEN);
		btnAddRoutine.setStyleAttribute("margin-top", "10px");
		btnAddRoutine.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.createRoutine("");
			}
		});
		this.add(btnAddRoutine);
		
	}

	@Override
	public Widget asWidget() {
		
    //		showView(0);
		
		return this;
	}

	@Override
	public LayoutContainer getMostPopularContainer() {
		return panelMostPopular;
	}

	@Override
	public LayoutContainer getMyRoutinesContainer() {
		return panelMyRoutines;
	}

	@Override
	public void setContentEnabled(boolean enabled) {
		this.setEnabled(enabled);
	}

	@Override
	public void setHandler(RoutinesListSubHandler handler) {
		this.handler = handler;
	}
	
}
