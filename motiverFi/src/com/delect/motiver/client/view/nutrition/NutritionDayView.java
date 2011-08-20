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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter;
import com.delect.motiver.client.presenter.nutrition.NutritionDayPresenter.NutritionDayHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Document;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * Foods for single day
 * <br> Totals always visible
 * <br> When clicked totals -> shows foods & comments 
 * @author Antti
 *
 */
public class NutritionDayView extends NutritionDayPresenter.NutritionDayDisplay {

	private MessageBox box = null;
	private MyButton btnShowFoods = new MyButton();
	private int floatingPosition;
	private boolean foodsPermission = true;
	private NutritionDayHandler handler;
	private boolean isFloating = false;
	private Listener<ComponentEvent> listenerShortcuts = new Listener<ComponentEvent>() {
		@Override
    public void handleEvent(ComponentEvent ce) {

			//if valid key comco
			if(Functions.isValidKeyCombo(ce)) {
				//if enough time elapsed
				if(System.currentTimeMillis() - timeLastKeyEvent < Constants.DELAY_KEY_EVENTS) {
					return;
        }
				
				switch(ce.getKeyCode()) {
        //shift + T
	        		case 84:
	    				timeLastKeyEvent = System.currentTimeMillis();
              handler.newTime();
              ce.cancelBubble();
              break;
        }
      }
		}
	};
	private HorizontalPanel panelButtonsBottom = new HorizontalPanel();
	private LayoutContainer panelComments = new LayoutContainer();

	//panels
	private LayoutContainer panelContent = new LayoutContainer();
	private ContentPanel panelData = new ContentPanel();
	private ContentPanel panelDetailed = new ContentPanel();
	
	private LayoutContainer panelFoods = new LayoutContainer();
	private LayoutContainer panelGuide = new LayoutContainer();
	private LayoutContainer panelTotals = new LayoutContainer();

	private LayoutContainer panelTotalsDuplicate = new LayoutContainer();
	//widgets
	private Text textTitle = new Text();
	private long timeLastKeyEvent = 0;

	public NutritionDayView() {

		panelContent.setStyleName("panel-today-nutrition");
		panelContent.setLayout(new RowLayout());
		panelData.setLayout(new RowLayout());

		panelDetailed.setHeaderVisible(false);
		panelDetailed.setTitleCollapse(true);
		panelDetailed.setCollapsible(true);
		panelData.setHeaderVisible(false);
		panelData.setTitleCollapse(true);
		panelData.setCollapsible(true);

		//listeners for shift+key
		Document.get().addListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcuts);
	}
	
	@Override
	public Widget asWidget() {
		
		panelContent.removeAll();
		this.removeAll();
		
		//title
		textTitle.setStyleName("label-title-medium");
		panelContent.add(textTitle, new RowData(-1, -1, new Margins(10)));
		
		//totals
		panelContent.add(panelTotals);
		//"duplicate". Used when floating totals panel
		panelTotalsDuplicate.setVisible(false);
		panelContent.add(panelTotalsDuplicate);
		
		//buttons (if permission to show foods)
		if(foodsPermission) {
			HorizontalPanel panelButtons = new HorizontalPanel();
			panelButtons.setSpacing(8);
			//show/hide foods
			btnShowFoods.setText(AppController.Lang.ShowTarget(AppController.Lang.Foods().toLowerCase()));
			btnShowFoods.setScale(ButtonScale.MEDIUM);
			btnShowFoods.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if(panelData.isExpanded()) {
						btnShowFoods.setText(AppController.Lang.ShowTarget(AppController.Lang.Foods().toLowerCase()));
						panelData.collapse();
					}
					else {
						btnShowFoods.setText(AppController.Lang.HideTarget(AppController.Lang.Foods().toLowerCase()));
						panelData.expand();
					}
				}
			});
			panelButtons.add(btnShowFoods);
			
			//show extra info
			final MyButton btnShowExtraInfo = new MyButton();
			btnShowExtraInfo.setStyleAttribute("margin-right", "10px");
			btnShowExtraInfo.setText(AppController.Lang.ShowTarget(AppController.Lang.Details().toLowerCase()));
			btnShowExtraInfo.setScale(ButtonScale.MEDIUM);
			btnShowExtraInfo.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if(panelDetailed.isExpanded()) {
						btnShowExtraInfo.setText(AppController.Lang.ShowTarget(AppController.Lang.Details().toLowerCase()));
						panelDetailed.collapse();
					}
					else {
						btnShowExtraInfo.setText(AppController.Lang.HideTarget(AppController.Lang.Details().toLowerCase()));
						panelDetailed.expand();
					}
				}
			});
			panelButtons.add(btnShowExtraInfo);
			panelContent.add(panelButtons);
		}

		//detailed info
		panelDetailed.addListener(Events.Expand, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.detailsVisible();
			}
		});
		panelDetailed.addListener(Events.Collapse, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.detailsHidden();
			}
		});
		panelContent.add(panelDetailed);
		panelDetailed.collapse();
		
		//foods
		panelData.setBorders(false);
		panelData.add(panelFoods, new RowData(-1, -1, new Margins(10, 0, 0, 0)));
		
		//buttons
		//add time link
		MyButton btnAdd = new MyButton();
		btnAdd.setText(AppController.Lang.AddTarget(AppController.Lang.Time().toLowerCase()));
		btnAdd.setScale(ButtonScale.MEDIUM);
		btnAdd.setColor(MyButton.Style.GREEN);
		btnAdd.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.newTime();
			}
		});
		panelButtonsBottom.add(btnAdd);
		//remove all times link
		MyButton btnRemoveAll = new MyButton();
		btnRemoveAll.setStyleAttribute("margin-left", "5px");
		btnRemoveAll.setText(AppController.Lang.RemoveAllTarget(AppController.Lang.Times().toLowerCase()));
		btnRemoveAll.setScale(ButtonScale.MEDIUM);
		btnRemoveAll.setColor(MyButton.Style.RED);
		btnRemoveAll.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//ask for confirm
				box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.AllTimes().toLowerCase()), new MessageBoxHandler() {
					@Override
					public void okPressed(String text) {
						handler.removeTimes();
					}
				});
				box.show();
			}
		});
		panelButtonsBottom.add(btnRemoveAll);
		panelData.add(panelButtonsBottom, new RowData(-1, -1, new Margins(10, 0, 10, 10)));
		//call handler when panel expands/collapses
		panelData.addListener(Events.Expand, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.timesVisible();
			}
		});
		panelData.addListener(Events.Collapse, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.timesHidden();
			}
		});
		panelContent.add(panelData, new RowData(-1, -1, new Margins(0, 0, 5, 0)));

		//comments
		panelContent.add(panelComments, new RowData(-1, -1, new Margins(10)));
		
		this.add(panelContent);
		
		//guides
		this.add(panelGuide);
		
		panelFoods.setStyleAttribute("min-height", "250px");
		panelFoods.setLayout(new RowLayout()); 
		panelData.collapse();
		
		//transparent when mouse not over
		setTransparencyOnMouseOver(true);
		
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
	public LayoutContainer getDetailsContainer() {
		return panelDetailed;
	}

	@Override
	public LayoutContainer getGuideContainer() {
		return panelGuide;
	}

	@Override
	public LayoutContainer getTotalsContainer() {
		return panelTotals;
	}

	@Override
	public void onScroll(int y) {
		
		try {
			//save position
			if(!isFloating) {
				floatingPosition = panelTotals.getAbsoluteTop();
      }

			//if not floating or scrolled over this panel
			int foodsY = panelFoods.getAbsoluteTop();
			int foodsH = panelFoods.getHeight();
			
			//if panel not visible
			if(floatingPosition < y + 40) {

				//if scrolled over whole panel -> stop float
				if(y + panelTotals.getHeight() + 100 > foodsY + foodsH) {
					
					if(isFloating) {
						//hide "duplicate"
						panelTotalsDuplicate.setVisible(false);
						
						panelTotals.removeStyleName("panel-floating");
						isFloating = false;
					}
				}
				else if(!isFloating) {
					//show "duplicate"
					panelTotalsDuplicate.setHeight(panelTotals.getHeight());
					panelTotalsDuplicate.setVisible(true);

					panelTotals.setWidth(panelTotalsDuplicate.getWidth());
					panelTotals.addStyleName("panel-floating");
					panelTotals.setStyleAttribute("left", (panelTotalsDuplicate.getAbsoluteLeft() - 2) + "px");
					isFloating = true;
				}
			}
			//panel visible
			else {
				if(isFloating) {
					//hide "duplicate"
					panelTotalsDuplicate.setVisible(false);
					
					panelTotals.removeStyleName("panel-floating");
					isFloating = false;
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
			
	}

	@Override
	public void onStop() {
		//remove key listeners
		Document.get().removeListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcuts);
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	@Override
	public void removeAllFromTotals() {
		panelTotals.removeAll();
	}

	@Override
	public void setAddButtonVisible(boolean visible) {
		if(panelButtonsBottom != null) {
			panelButtonsBottom.setVisible(visible);
		}
	}

	@Override
	public void setDate(Date date) {
		textTitle.setText(AppController.Lang.Foods());
	}

	@Override
	public void setFoodsEnabled(boolean foodsPermission) {
		this.foodsPermission  = foodsPermission;
	}

	@Override
	public void setHandler(final NutritionDayHandler handler) {
		this.handler = handler;
	}

	@Override
	public void showContent() {
		//if already expanded -> just call handler
		if(panelData.isExpanded()) {
			handler.timesVisible();
    }
		else {
			panelData.expand();
    }
		
		btnShowFoods.setText(AppController.Lang.HideTarget(AppController.Lang.Foods().toLowerCase()));
	}

}
