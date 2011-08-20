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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.training.WorkoutLinkPresenter;
import com.delect.motiver.client.presenter.training.WorkoutLinkPresenter.WorkoutLinkHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.WorkoutModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class WorkoutLinkView extends WorkoutLinkPresenter.WorkoutLinkDisplay {

	private MessageBox box = null;
	
	private WorkoutLinkHandler handler;
	//panels
	private LayoutContainer panelButtons = new LayoutContainer();
	
	private boolean quickSelectOn = false;
	//widgets
	private Timer timerOut;

	private WorkoutModel workout;	
	
	public WorkoutLinkView() {
		this.setStyleName("panel-workoutlink");
		this.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
		this.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
		this.sinkEvents(Event.MOUSEEVENTS);		
		//show hide header buttons based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//cancel timer
				if(timerOut != null) {
					timerOut.cancel();
					timerOut = null;
				}
				
				panelButtons.setVisible(true);
				layout(true);
			}
		});
		this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//cancel timer
				if(timerOut == null) {
					timerOut = new Timer() {
						@Override
						public void run() {
							panelButtons.setVisible(false);
							layout(true);
						}
					};
					timerOut.schedule(Constants.DELAY_HIDE_ICONS);
				}
			}
		});
		
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(getData("btnClick") != null) {
					setData("btnClick", null);
					return;
				}
				handler.selected();
			}
		});

		//layout
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
	}
	
	@Override
	public Widget asWidget() {
		
		try {

			//icon (if has date)
			if(workout.getDate() != null) {
				Image icon = new Image(MyResources.INSTANCE.iconWorkout());
				this.add(icon, new HBoxLayoutData(new Margins(0, 20, 0, 0)));
			}
			
			//if quick selection
			if(quickSelectOn) {
				CheckBox cbSelect = new CheckBox();
				cbSelect.addListener(Events.Change, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						Boolean isSelected = ((CheckBox)be.getSource()).getValue();
						
						cancelSelection();
						
						handler.quickSelect(isSelected);
					}
				});
				this.add(cbSelect, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			}
	
			//name
			Text textName = new Text(workout.getName());
			this.add(textName, new HBoxLayoutData(new Margins(0)));
			
			//spacer
			HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
      flex.setFlex(1);  
      this.add(new Text(), flex);  
	        
      //username
      if(!workout.getUid().equals(AppController.User.getUid())) {
        LayoutContainer panelUsername = new LayoutContainer();
        panelUsername.setWidth(100);
        panelUsername.setHeight(15);
        panelUsername.setStyleName("label-title-username");
        panelUsername.addText("<fb:name uid=\"" + workout.getUid() + "\" useyou=false linked=false></fb:name>");
        this.add(panelUsername, new HBoxLayoutData(new Margins(0)));
      }
      //show remove button
      else if(workout.getDate() != null) {
				//buttons layout
				HBoxLayout layoutButtons = new HBoxLayout();
				layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
				layoutButtons.setPack(BoxLayoutPack.END);
        panelButtons.setLayout(layoutButtons);
        panelButtons.setHeight(16);
        panelButtons.setWidth(50);
		        
        //remove exercise link
				ImageButton btnRemove = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.ThisWorkout().toLowerCase()), MyResources.INSTANCE.iconRemove());
				btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						setData("btnClick", true);
						//ask for confirm
						box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisWorkout().toLowerCase()), new MessageBoxHandler() {
							@Override
							public void okPressed(String text) {
								handler.workoutRemoved();
							}
						});
						box.show();
					}
				});
				panelButtons.add(btnRemove);
				
				this.add(panelButtons, new HBoxLayoutData(new Margins(0, 0, 0, 10)));

				panelButtons.setVisible(false);
      }
			//date (if set)
      else if(workout.getDate() != null) {
				Text textDate = new Text(Functions.getDateString(workout.getDate(), true, true));
				textDate.setStyleName("label-date");
				this.add(textDate, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			}
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setHandler(WorkoutLinkHandler handler) {
		this.handler = handler;
	}
	@Override
	public void setModel(WorkoutModel workout) {
		this.workout = workout;
	}

	@Override
	public void setQuickSelect(boolean quickSelectOn) {
		this.quickSelectOn = quickSelectOn;
	}

	/**
	 * Cancels selection (for example when checkbox is clicked
	 */
	protected void cancelSelection() {
		this.setData("btnClick", true);
	}
	
}
