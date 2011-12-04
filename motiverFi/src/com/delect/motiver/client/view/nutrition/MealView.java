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
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.MealPresenter;
import com.delect.motiver.client.presenter.nutrition.MealPresenter.MealHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.SmallNotePanel;
import com.delect.motiver.client.view.SmallNotePanelDisplay;
import com.delect.motiver.client.view.widget.NameInputWidget;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.NameInputWidget.EnterNamePanelHandler;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.MealModel;

import com.extjs.gxt.ui.client.dnd.DragSource;
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
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class MealView extends MealPresenter.MealDisplay {

	
	private MessageBox box = null;
	private MealHandler handler;
	private MealModel meal = null;
	
	private SmallNotePanelDisplay panelBase = (SmallNotePanelDisplay)GWT.create(SmallNotePanel.class);
	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelFoods = new LayoutContainer();
	private LayoutContainer panelTotals = new LayoutContainer();
	private LayoutContainer panelUser = new LayoutContainer();
	
	/**
	 * Meal view
	 */
	public MealView() {
		
		try {
			panelBase.setStylePrefix("panel-meal");
			panelBase.setCollapsible(false);

      //content
			this.add(panelBase);
			panelBase.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					panelBase.setTabIndex(0);
				}
			});
			new KeyNav<ComponentEvent>(panelBase) { 

				@Override
				public void onKeyPress(ComponentEvent ce) {

					//if valid key comco
					if(Functions.isValidKeyCombo(ce)) {
            switch(ce.getKeyCode()) {
            //shift + F
          		case 70:
          			handler.newFood(null);
          			ce.cancelBubble();
          			break;
            }
          }
				}
			};
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	@Override
	public Widget asWidget() {

		if(meal.getTimeId() == 0) {
			panelBase.setStyleName("panel-meal");
    }
		else {
			panelBase.setStyleName("panel-meal-intime");
    }
		
		panelBase.getPanelData().removeAll();
		
		try {
			//if no model -> ask for name
			if(meal.getId() == 0) {
				
				//add panel where user can type name
				NameInputWidget panelNameInput = new NameInputWidget(new EnterNamePanelHandler() {
					@Override
					public void newName(String name) {
						//if cancelled
						if(name == null) {
							handler.saveData(null);
	          }
						else {
							meal.setName(name);
							handler.saveData(meal);
						}
					}
				});
				panelBase.getPanelData().add(panelNameInput);
				
			}
			//model set
			else {
				
				if(meal.getTimeId() == 0) {

					//totals panel
					HBoxLayout layout = new HBoxLayout();
          layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
          panelTotals.setLayout(layout);
          panelTotals.setHeight(30);
          panelBase.getPanelData().add(panelTotals, new RowData(-1, -1, new Margins(5, 0, 5, 10)));
				}
		        
				//userview
				panelUser.setStyleAttribute("float", "right");
				panelUser.setStyleAttribute("margin", "20px 20px 0 20px");
				panelUser.setVisible(false);
				this.insert(panelUser, 0);
				
				panelBase.getPanelData().add(panelFoods);
				panelBase.getPanelData().add(panelComments, new RowData(-1, -1, new Margins(10)));

				if(meal.getTimeId() == 0) {
					panelFoods.setStyleAttribute("min-height", "150px");
        }
				panelFoods.setLayout(new RowLayout()); 
				
				initTitlePanel();
			}
			
			//set drop target (FOOD)
			DropTarget targetMeal = new DropTarget(this);
			targetMeal.setGroup("mealfood");
			targetMeal.addDNDListener(new DNDListener() {
			  @Override
			  public void dragDrop(DNDEvent e) {
					
					//get dragged model
					Object dragged = e.getData();
					if(dragged instanceof FoodModel) {
						handler.newFood( (FoodModel)dragged );
						e.cancelBubble();

						panelBase.removeStyleName("panel-highlight");
					}
					
					super.dragDrop(e);
				}
				//highligh when something is dragged
				@Override
				public void dragEnter(DNDEvent event) {

					//get dragged model
					Object dragged = event.getData();

					//if meal -> cancel drag
					if(dragged instanceof MealModel) {
						event.getStatus().setStatus(false);
						super.dragEnter(event);
						return;
					}
					
					//change drag panel
					if(dragged instanceof FoodModel) {
						
						panelBase.addStyleName("panel-highlight");
						
						String name = (((FoodModel)dragged).getName() != null)? ((FoodModel)dragged).getName().getName() : "";
						String html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(name, meal.getName()));
						event.getStatus().update(html);  
					}
					
					super.dragStart(event);
				}
				@Override
				public void dragLeave(DNDEvent event) {

					//get dragged model
					Object dragged = event.getData();
					if(dragged instanceof FoodModel) {

						panelBase.removeStyleName("panel-highlight");
						
						//change drag panel
						String name = (((FoodModel)dragged).getName() != null)? ((FoodModel)dragged).getName().getName() : "";
						String html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(name, "..."));
						event.getStatus().update(html);  
					} 
					
					super.dragLeave(event);
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		panelBase.getPanelData().layout();
		
		return this;
	}
	

	@Override
	public LayoutContainer getBodyContainer() {
		return panelFoods;
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

	@Override
	public void setHandler(MealHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(MealModel meal) {
		
		this.meal = meal;
		
		initTotals();
	}

	/**
	 * Inits panel which contains the title
	 */
	private void initTitlePanel() {

		try {

			//icon
			//TODO
			
			//title
			String name = "- " + AppController.Lang.NoName() + " -";
			if(meal.getName().length() > 0) {
				name = meal.getName();
      }
			panelBase.setTitleText(name);
			
			//buttons
			if(meal.getId() != 0) {

				if(meal.getUser().getUid().equals(AppController.User.getUid())) {
					
					//add food
					panelBase.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Food().toLowerCase()), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              handler.newFood(null);
            }
          });
					
					//rename meal
					panelBase.addHeaderImageButton(AppController.Lang.Rename(), MyResources.INSTANCE.iconBtnRename(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              if(box != null && box.isVisible()) {
                box.close();
              }
              //ask for confirm
              box = Functions.getMessageBoxPrompt(meal.getName(), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  if(!meal.getName().equals( text )) {
                    meal.setName(text);
                    panelBase.setTitleText(meal.getName());
							        		  
                    handler.saveData(meal);
                  }
                }
              });
              box.setTitle(AppController.Lang.Name());
              box.setMessage(AppController.Lang.EnterName() + ":");
              box.show();
            }
          });
					
					//drag meal
					ImageButton btn = panelBase.addHeaderImageButton(AppController.Lang.DragToCopy(), MyResources.INSTANCE.iconBtnDrag(), null);
					DragSource source = new DragSource(btn) {  
            @Override  
            protected void onDragStart(DNDEvent event) {
              super.onDragStart(event);
    							
              //show this view when dragging
              event.setData(meal); 
    							
              //set drag panel
              String html = Functions.getDragPanel(AppController.Lang.CopyTargetTo(meal.getName(), ".."));
              event.getStatus().update(html);      
            }
					};
					source.setGroup("mealfood");
					
					//remove meal
					panelBase.addHeaderImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Meal().toLowerCase()), MyResources.INSTANCE.iconRemove(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              //ask for confirm
              box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisMeal().toLowerCase()), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  handler.mealRemoved();
                }
              });
              box.show();
            }
          });
				}
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
			//only if not in time
			if(meal.getTimeId() == 0) {
				panelTotals.removeAll();
				panelTotals.add(new Text(AppController.Lang.MealsStats() + ":"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
				panelTotals.add(Functions.getTotalPanel(meal.getEnergy(), meal.getProtein(), meal.getCarb(), meal.getFet()));
				panelTotals.layout();
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
}
