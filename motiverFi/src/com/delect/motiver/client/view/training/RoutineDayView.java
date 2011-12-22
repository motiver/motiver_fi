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
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.training.RoutineDayPresenter;
import com.delect.motiver.client.presenter.training.RoutineDayPresenter.RoutineDayHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.RoutineModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class RoutineDayView extends RoutineDayPresenter.RoutineDayDisplay {

	private int day = 0;
	private RoutineDayHandler handler;
	private LayoutContainer panelButtons = new LayoutContainer();
	
	private LayoutContainer panelData = new LayoutContainer();
	//panels
	private LayoutContainer panelHeader = new LayoutContainer();
	private RoutineModel routine;
	
	//widgets
	private Text textTitle;
	
	
	public RoutineDayView() {
		this.setStyleAttribute("margin-bottom", "10px");
		
		try {
			
			this.setLayout(new RowLayout());
			
			//header
			HBoxLayout layoutHeader = new HBoxLayout();
      layoutHeader.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layoutHeader);
      panelHeader.setHeight(30);
      panelHeader.setAutoWidth(true);
      panelHeader.setStyleName("panel-routineday-header");
	        
      this.add(panelHeader);

      //content
	        
			panelData.setLayout(new RowLayout());
			panelData.setStyleName("panel-routineday-data");
			this.add(panelData);
			
			//show hide header buttons based on mouse position
			this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					panelButtons.setVisible(true);
					panelHeader.layout(true);
				}
			});
			this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					panelButtons.setVisible(false);
					panelHeader.layout(true);
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	
	}
	
	@Override
	public Widget asWidget() {

		textTitle = new Text(AppController.Lang.Day() + " " + day);
		textTitle.setStyleName("label-title-medium");
    panelHeader.add(textTitle);
        
		//spacer
		HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 10, 0, 10));
    flex.setFlex(1);  
    panelHeader.add(new Text(), flex); 
													
		//add new workout -link
		if(routine.getUser().equals(AppController.User)) {

			//buttons layout
			HBoxLayout layoutButtons = new HBoxLayout();
			layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
			layoutButtons.setPack(BoxLayoutPack.END);
      panelButtons.setLayout(layoutButtons);
      panelButtons.setHeight(30);
      panelButtons.setWidth(300);

      //add workout
			MyButton btnAddWorkout = new MyButton(AppController.Lang.AddTarget(AppController.Lang.Workout().toLowerCase()));
			btnAddWorkout.setColor(MyButton.Style.GREEN);
			btnAddWorkout.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.newWorkout();
				}
			});
			panelButtons.add(btnAddWorkout, new HBoxLayoutData(new Margins(0, 0, 0, 10)));

			panelHeader.add(panelButtons);
			panelButtons.setVisible(false);
		}
		
		return this;
	}
	
	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}
	
	@Override
	public void setDay(int day) {
		this.day = day;
	}
	
	@Override
	public void setHandler(RoutineDayHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setModel(RoutineModel routine) {
		this.routine = routine;
	}
	
}
