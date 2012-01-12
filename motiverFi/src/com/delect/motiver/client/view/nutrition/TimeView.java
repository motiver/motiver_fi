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

import java.util.Date;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.TimePresenter;
import com.delect.motiver.client.presenter.nutrition.TimePresenter.TimeHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.TimeSelectFieldView;
import com.delect.motiver.client.view.TimeSelectFieldView.TimeSelectFieldHandler;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.TimeModel;

import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class TimeView extends TimePresenter.TimeDisplay {

	private MessageBox box = null;
	private TimeHandler handler;
	private LayoutContainer panelButtons = new LayoutContainer();
	private LayoutContainer panelData = new LayoutContainer();

	//panels
	private LayoutContainer panelHeader = new LayoutContainer();
	private LayoutContainer panelTotals = new LayoutContainer();
	private TimeModel time = null;
	private Timer timerOut;
	

	public TimeView() {
		
		try {
			
			this.addStyleName("panel-time");
			this.setLayout(new RowLayout());
			
			//header
			HBoxLayout layoutHeader = new HBoxLayout();
      layoutHeader.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layoutHeader);
      panelHeader.setHeight(40);
      panelHeader.setAutoWidth(true);
      panelHeader.setStyleName("panel-time-header");
	        
      this.add(panelHeader);

      //content

			//totals panel
			HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelTotals.setLayout(layout);
      panelTotals.setHeight(30);
      panelData.add(panelTotals, new RowData(-1, -1, new Margins(10, 0, 0, 10)));
	        
			panelData.setLayout(new RowLayout());
			panelData.setStyleName("panel-time-data");
			panelData.setStyleAttribute("min-height", "150px");
			this.add(panelData);
			
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
					panelHeader.layout(true);
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
								panelHeader.layout(true);
							}
						};
						timerOut.schedule(Constants.DELAY_HIDE_ICONS);
					}
				}
			});
			//listeners for shift+key
			this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					setTabIndex(0);
				}
			});
			new KeyNav<ComponentEvent>(this) { 

				@Override
				public void onKeyPress(ComponentEvent ce) {

					//if valid key comco
					if(Functions.isValidKeyCombo(ce)) {
            switch(ce.getKeyCode()) {
            //shift + M
			        		case 77:
			        			handler.newMeal(null);
			        			ce.cancelBubble();
			        			break;
                    //shift + F
			        		case 70:
			        			handler.newFood(null);
			        			ce.cancelBubble();
			        			break;
            }
          }
				}
			};
			
			//set drop target (MEAL / FOOD)
			DropTarget targetMeal = new DropTarget(this);
			targetMeal.setGroup("mealfood");
			targetMeal.addDNDListener(new DNDListener() {
			  @Override
			  public void dragDrop(DNDEvent e) {
					removeStyleName("panel-highlight");
					
					//get dragged model
					Object dragged = e.getData();
					
					//meal
					if(dragged instanceof MealModel) {
					  MealModel m = (MealModel)dragged;
						handler.newMeal( m );
						e.cancelBubble();
					}
					//food
					else if(dragged instanceof FoodModel) {
            FoodModel f = (FoodModel)dragged;
            f.setId(0);
            handler.newFood( f );
						e.cancelBubble();
					}
					
					super.dragDrop(e);
				}
				//highligh when something is dragged
				@Override
				public void dragEnter(DNDEvent event) {
				  //scroll to view
          scrollToView();
					
					addStyleName("panel-highlight");

					Object dragged = event.getData();
					
					//change drag panel
					String html = "";
					if(dragged instanceof MealModel) {
						html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(((MealModel)dragged).getName(), AppController.Lang.Time().toLowerCase() + " " + Functions.getTimeToString(time.getTime())));
          }
					else if(dragged instanceof FoodModel) {
						String name = (((FoodModel)dragged).getName() != null)? ((FoodModel)dragged).getName().getName() : "";
						html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(name, AppController.Lang.Time().toLowerCase() + " " + Functions.getTimeToString(time.getTime())));
					}
					event.getStatus().update(html);  
					
					super.dragStart(event);
				}
				@Override
				public void dragLeave(DNDEvent event) {
					
					removeStyleName("panel-highlight");

					Object dragged = event.getData();
					
					//change drag panel
					String html = "";
					if(dragged instanceof MealModel) {
						html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(((MealModel)dragged).getName(), ".."));
          }
					else if(dragged instanceof FoodModel) {
						String name = (((FoodModel)dragged).getName() != null)? ((FoodModel)dragged).getName().getName() : "";
						html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(name, "..."));
					}
					event.getStatus().update(html);  
					
					super.dragLeave(event);
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	@Override
	public Widget asWidget() {

		initTitlePanel();
		panelButtons.setVisible(false);
		
		return this;
	}	

	@Override
	public LayoutContainer getBodyContainer() {		
		return panelData;
	}

	@Override
	public void onStop() {
		if(timerOut != null) {
			timerOut.cancel();
    }
		timerOut = null;
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	@Override
	public void setHandler(TimeHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setModel(TimeModel time) {
		this.time = time;
		
		initTotals();
		
		//check if current time
		checkIfCurrentTime();
	}

	/**
	 * Checks if this time is current time
	 * <br>Adds / removes style 'panel-time-now'
	 */
	@SuppressWarnings("deprecation")
	private void checkIfCurrentTime() {
		Date d = new Date();
		//today
		if(Functions.Fmt.format(d).equals(Functions.Fmt.format(time.getDate()))) {
			long curr = d.getHours() * 3600 + d.getMinutes() * 60;
			//-10min ... +10min
			if(Math.abs(curr - time.getTime()) < 20 * 60) {
				this.addStyleName("panel-time-now");
      }
			else {
				this.removeStyleName("panel-time-now");
      }
		}
	}

	/**
	 * Inits panel which contains the title
	 */
	private void initTitlePanel() {

		try {

			//icon
			Image img = new Image(MyResources.INSTANCE.iconClock());
			panelHeader.add(img, new HBoxLayoutData(new Margins(0, 10, 0, 0)));

			//init time select view
			TimeSelectFieldView tf = new TimeSelectFieldView(time.getTime(), new TimeSelectFieldHandler() {
				@Override
				public void timeChanged(int t) {
					time.setTime(t);
					
					checkIfCurrentTime();
					
					handler.timeChanged(t);
				}
			});
      panelHeader.add(tf);
			
			if(time.getUser().equals(AppController.User)) {
		        
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
				
				//add meal
				MyButton btnAddMeal = new MyButton(AppController.Lang.AddTarget(AppController.Lang.Meal().toLowerCase()));
				btnAddMeal.setColor(MyButton.Style.GREEN);
				btnAddMeal.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						handler.newMeal(null);
					}
				});
				panelButtons.add(btnAddMeal, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
	
				//add food
				MyButton btnAddFood = new MyButton(AppController.Lang.AddTarget(AppController.Lang.Food().toLowerCase()));
				btnAddFood.setColor(MyButton.Style.GREEN);
				btnAddFood.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						handler.newFood(null);
					}
				});
				panelButtons.add(btnAddFood, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
	
				//remove time
				ImageButton btnRemoveTime = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Time().toLowerCase()), MyResources.INSTANCE.iconRemove());
				btnRemoveTime.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						//ask for confirm
						box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisTime().toLowerCase()), new MessageBoxHandler() {
							@Override
							public void okPressed(String text) {
								handler.timeRemoved();
							}
						});
						box.show();
					}
				});
				panelButtons.add(btnRemoveTime, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
				
				panelHeader.add(panelButtons);
			}

		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Inits totals panel based on model
	 */
	private void initTotals() {

		try {
			panelTotals.removeAll();
			panelTotals.add(new Text(AppController.Lang.TimesStats() + ":"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			panelTotals.add(Functions.getTotalPanel(time.getEnergy(), time.getProtein(), time.getCarb(), time.getFet()));
			panelTotals.layout();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
}
