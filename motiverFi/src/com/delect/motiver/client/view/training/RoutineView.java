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
import com.delect.motiver.client.presenter.training.RoutinePresenter;
import com.delect.motiver.client.presenter.training.RoutinePresenter.RoutineHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.EnterNamePanel;
import com.delect.motiver.client.view.EnterNamePanel.EnterNamePanelHandler;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.RoutineModel;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class RoutineView extends RoutinePresenter.RoutineDisplay {

	//widgets
	private MessageBox box = null;
	private RoutineHandler handler;
	private LayoutContainer panelButtons = new LayoutContainer();
	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelData = new LayoutContainer();
	//panels
	private LayoutContainer panelHeader = new LayoutContainer();
	private LayoutContainer panelLinks = new LayoutContainer();
	
	private LayoutContainer panelUser = new LayoutContainer();
	private LayoutContainer panelWorkouts = new LayoutContainer();
	
	private RoutineModel routine = null;
	private Text textTitle;
	
	public RoutineView() {
		
		try {
	        
			this.addStyleName("panel-routine");
			this.setLayout(new RowLayout());
			
			//userview
			panelUser.setStyleAttribute("float", "right");
			panelUser.setStyleAttribute("margin", "20px 20px 0 20px");
			panelUser.setVisible(false);
			this.add(panelUser);

			//links panel's layout
			HBoxLayout layout = new HBoxLayout(); 
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelLinks.setHeight(35);
      panelLinks.setLayout(layout);
			
			//header
			HBoxLayout layoutHeader = new HBoxLayout();
      layoutHeader.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layoutHeader);
      panelHeader.setHeight(40);
      panelHeader.setAutoWidth(true);
      panelHeader.setStyleName("panel-routine-header");
	        
      this.add(panelHeader);

			panelData.setLayout(new RowLayout());
			panelData.setStyleName("panel-routine-data");
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
		
		panelData.removeAll();

		try {
			//if no model -> ask for name
			if(routine.getId() == 0) {
				
				//add panel where user can type name
				EnterNamePanel panelNameInput = new EnterNamePanel(new EnterNamePanelHandler() {
					@Override
					public void newName(String name) {
						//if cancelled
						if(name == null) {
							handler.saveData(null);
            }
						else {
							routine.setName(name);
							handler.saveData(routine);
						}
					}
				});
				panelData.add(panelNameInput);
				
			}
			//model set
			else {
				
				//days
				panelWorkouts.setLayout(new RowLayout());
				panelData.add(panelWorkouts, new RowData(-1, -1, new Margins(0, 0, 5, 0)));

				//links (add/remove days)
				initLinksPanel();
				panelData.add(panelLinks, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
				
				//comments
				panelData.add(panelComments, new RowData(-1, -1, new Margins(10, 0, 0, 0)));

				initTitlePanel();
				panelButtons.setVisible(false);
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		panelData.layout();
		
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelWorkouts;
	}

	@Override
	public LayoutContainer getCommentsContainer() {		
		return panelComments;
	}

	@Override
	public LayoutContainer getUserContainer() {
		panelUser.setVisible(true);
		return panelUser;
	}

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	/**
	 * Shows / hides add/remove day buttons
	 * @param visible
	 */
	@Override
	public void setAddButtonsVisible(boolean visible) {
		panelLinks.setVisible(visible);
		panelLinks.layout();
	}

	@Override
	public void setHandler(RoutineHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(RoutineModel routine) {
		this.routine = routine;
	}
	
	/**
	 * Inits panel which contains routine
	 */
	private void initLinksPanel() {
		
		panelLinks.removeAll();		

		if(routine.getUid().equals(AppController.User.getUid())) {
		    
			//add/remove days links
			MyButton btnAdd = new MyButton();
			btnAdd.setColor(MyButton.Style.GREEN);
			btnAdd.setScale(ButtonScale.MEDIUM);
			btnAdd.setText(AppController.Lang.AddTarget(AppController.Lang.Day().toLowerCase()));
			btnAdd.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.saveDays(true);
				}
			});
			panelLinks.add(btnAdd, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
			MyButton btnRemove = new MyButton();
			btnRemove.setColor(MyButton.Style.RED);
			btnRemove.setScale(ButtonScale.MEDIUM);
			btnRemove.setText(AppController.Lang.RemoveTarget(AppController.Lang.LastDay().toLowerCase()));
			btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					//remove last day
					box = MessageBox.confirm(AppController.Lang.Confirm(), AppController.Lang.ConfirmRemoveLastDayInRoutine(), new Listener<MessageBoxEvent>() {   
						public void handleEvent(MessageBoxEvent be) {
							Button btn = be.getButtonClicked();
							if(Dialog.YES.equals(btn.getItemId())) {
								handler.saveDays(false);
							}
						}
					});
				}
			});
			panelLinks.add(btnRemove, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
			
			panelLinks.layout();

		}
	}

	/**
	 * Inits panel which contains the title
	 */
	private void initTitlePanel() {

		try {
			String name = "- " + AppController.Lang.NoName() + " -";
			if(routine.getName().length() > 0) {
				name = routine.getName();
      }
			textTitle = new Text(name);
			textTitle.setStyleName("label-title-medium");
      panelHeader.add(textTitle);
			
			//buttons
			if(routine.getId() != 0) {

				if(routine.getUid().equals(AppController.User.getUid())) {
			        
					//spacer
					HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 10, 0, 10));
          flex.setFlex(1);  
          panelHeader.add(new Text(), flex); 

					//buttons layout
					HBoxLayout layoutButtons = new HBoxLayout();
					layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
					layoutButtons.setPack(BoxLayoutPack.END);
          panelButtons.setLayout(layoutButtons);
          panelButtons.setHeight(30);
          panelButtons.setWidth(300);
					
					//rename routine
					ImageButton btnRenameWorkout = new ImageButton(AppController.Lang.Rename(), MyResources.INSTANCE.iconBtnRename());
					btnRenameWorkout.addListener(Events.OnClick, new Listener<BaseEvent>() {
						@Override
						public void handleEvent(BaseEvent be) {
							if(box != null && box.isVisible()) {
                box.close();
              }
							//ask for confirm
							box = Functions.getMessageBoxPrompt(routine.getName(), new MessageBoxHandler() {
								@Override
								public void okPressed(String text) {
                  if(!routine.getName().equals( text )) {
                    routine.setName(text);
                    textTitle.setText(routine.getName());
						        		  
                    handler.saveData(routine);
                  }
								}
							});
							box.setTitle(AppController.Lang.Name());
							box.setMessage(AppController.Lang.EnterName() + ":");
							box.show();
						}
					});
					panelButtons.add(btnRenameWorkout, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
					
					//remove time
					ImageButton btnRemoveTime = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Routine().toLowerCase()), MyResources.INSTANCE.iconRemove());
					btnRemoveTime.addListener(Events.OnClick, new Listener<BaseEvent>() {
						@Override
						public void handleEvent(BaseEvent be) {
							//ask for confirm
							box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisRoutine().toLowerCase()), new MessageBoxHandler() {
								@Override
								public void okPressed(String text) {
									handler.routineRemoved();
								}
							});
							box.show();
						}
					});
					panelButtons.add(btnRemoveTime, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
					
					panelHeader.add(panelButtons);
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
}
