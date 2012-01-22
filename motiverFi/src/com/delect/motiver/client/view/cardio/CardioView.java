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
package com.delect.motiver.client.view.cardio;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.cardio.CardioPresenter;
import com.delect.motiver.client.presenter.cardio.CardioPresenter.CardioHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.widget.NameInputWidget;
import com.delect.motiver.client.view.widget.NameInputWidget.EnterNamePanelHandler;
import com.delect.motiver.client.view.SmallNotePanel;
import com.delect.motiver.client.view.SmallNotePanelDisplay;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;


public class CardioView extends CardioPresenter.CardioDisplay {

	private CardioModel cardio;
	private Grid<CardioValueModel> grid;
	private CardioHandler handler;
	private SmallNotePanelDisplay panelBase = (SmallNotePanelDisplay)GWT.create(SmallNotePanel.class);

	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelDataOld = new LayoutContainer();
	
	
	private LayoutContainer panelDates = new LayoutContainer();
	private ListStore<CardioValueModel> store = new ListStore<CardioValueModel>();
	private List<CardioValueModel> values;
	
	MessageBox box = null;
	
	/**
	 * Measurement view
	 */
	public CardioView() {
		
		try {
			panelBase.setStylePrefix("panel-cardio");
			
			panelBase.getPanelData().addListener(Events.Show, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.valuesVisible();
				}
			});
			
			this.add(panelBase);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	@Override
	public Widget asWidget() {

		panelBase.getPanelData().removeAll();
		
		try {
			//if no model -> ask for name
			if(cardio.getId() == 0) {
				
				//add panel where user can type name
				NameInputWidget panelNameInput = new NameInputWidget(new EnterNamePanelHandler() {
					@Override
					public void newName(String name) {
						//if cancelled
						if(name == null) {
							handler.saveData(null);
            }
						else {
							cardio.setName(name);
							handler.saveData(cardio);
						}
					}
				});
				panelBase.getPanelData().add(panelNameInput);
				panelBase.getPanelData().setVisible(true);
				
			}
			//model set
			else {

				panelDataOld.setStyleAttribute("min-height", "150px");
				panelDataOld.setBorders(true);
				panelDataOld.setLayout(new RowLayout()); 
				panelBase.getPanelData().add(panelDates, new RowData(-1, -1, new Margins(10, 0, 10, 0)));
				panelBase.getPanelData().add(panelDataOld, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
				panelBase.getPanelData().add(panelComments, new RowData(-1, -1, new Margins(10, 0, 0, 0)));

				initTitlePanel();
				
				panelBase.getPanelData().setVisible(false);
				
				panelDataOld.layout();
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		panelBase.getPanelData().layout();
		
		return this;
	}

	@Override
	public LayoutContainer getCommentsContainer() {
		return panelComments;
	}
	
	@Override
	public LayoutContainer getDataContainer() {
		return panelDataOld;
	}
	
	@Override
	public LayoutContainer getDatesContainer() {
		return panelDates;
	}

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
	}
	

	@Override
	public void setCollapsible(boolean isCollapsible) {
		panelBase.setCollapsible(isCollapsible);
	}
	
	@Override
	public void setHandler(CardioHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(CardioModel cardio) {
		this.cardio = cardio;
	}

	/**
	 * Populates list with given values
	 */
	@Override
	public void setValues(List<CardioValueModel> values) {
		
		try {
			this.values = values;
			
			//if null -> clear panel
			if(values == null) {
				panelDataOld.removeAll();
				return;
			}

			panelDataOld.removeAll();
			if(values.size() > 0) {				
				initList();
				panelDataOld.layout();
				
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
	}

	@Override
	public void showContent() {
		panelBase.getPanelData().setVisible(true);
	}

	/**
	 * Inits list that shows measurements
	 * Parameters: unit for value column
	 */
	private void initList() {
		
		//columns
		
		//date
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig("d", AppController.Lang.Date(), 150);  
		final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()] + " " + StringConstants.TIMEFORMATS[AppController.User.getTimeFormat()]);
		column.setDateTimeFormat(fmt);
		column.setRenderer(new GridCellRenderer<CardioValueModel>() {
			@Override
			public Object render(CardioValueModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CardioValueModel> store, Grid<CardioValueModel> grid) {
				return Functions.getDateTimeString(model.getDate(), false, true);
			}
    });
		column.setMenuDisabled(true); 
    configs.add(column); 
	    
    //duration
    column = new ColumnConfig("du", AppController.Lang.Duration(), 100);
    column.setRenderer(new GridCellRenderer<CardioValueModel>() {
			@Override
			public Object render(CardioValueModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CardioValueModel> store, Grid<CardioValueModel> grid) {
				long value = model.getDuration();
				return Functions.getDurationString(value);
			}
    });
    column.setMenuDisabled(true);
    configs.add(column); 
	    
    //pulse
    column = new ColumnConfig("pu", AppController.Lang.Pulse(), 100);
    column.setNumberFormat(NumberFormat.getFormat("0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column); 
      
    //pulse max
    column = new ColumnConfig("pum", AppController.Lang.MaxPulse(), 100);
    column.setNumberFormat(NumberFormat.getFormat("0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column); 

    //calories
    column = new ColumnConfig("c", AppController.Lang.Calories(), 100);
    column.setNumberFormat(NumberFormat.getFormat("0 kcal"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column); 

    //info
    column = new ColumnConfig("i", AppController.Lang.Info(), 100);
    column.setNumberFormat(NumberFormat.getFormat("0 kcal"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column); 

    final PagingToolBar toolBar = new PagingToolBar(Constants.LIMIT_LIST_RECORDS);  
    //set paging if more than 50 rows
    if(values.size() > Constants.LIMIT_LIST_RECORDS) {
      PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(values);  
	    	  
      // loader  
      PagingLoader<PagingLoadResult<CardioValueModel>> loader = new BasePagingLoader<PagingLoadResult<CardioValueModel>>(proxy);  
      loader.setRemoteSort(true);  
	      
      store = new ListStore<CardioValueModel>(loader);  
	      
      toolBar.bind(loader);  
	      
      loader.load(0, Constants.LIMIT_LIST_RECORDS);
    }
    else {
			//populate store
			store.removeAll();
			store.add(values);	    	
    }
	    
    //grid
		ColumnModel cm = new ColumnModel(configs);
		grid = new Grid<CardioValueModel>(store, cm);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<CardioValueModel>());
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setBorders(true);   
    grid.getView().setEmptyText(AppController.Lang.NoValues());
		grid.setAutoExpandColumn("i");
		grid.setAutoWidth(true);
		grid.setAutoHeight(true);
		grid.setColumnResize(false);
		grid.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				grid.focus();
			}			
		});

		//remove selected rows from ALL GRIDS when delete button is pressed
		new KeyNav<ComponentEvent>(grid) {
      @Override
      public void onDelete(ComponentEvent ce) {
				deleteSelectedValues();
      }
    };

		panelDataOld.add(grid);
		if(values.size() > Constants.LIMIT_LIST_RECORDS) {
			panelDataOld.add(toolBar);
    }
	}
	
	/**
	 * Inits panel which contains the title
	 */
	private void initTitlePanel() {

		try {
			//title
			panelBase.setTitleText(cardio.getNameClient());
			
			//buttons
			if(cardio.getId() != 0) {
				
				if(cardio.getUser().equals(AppController.User)) {
					
					panelBase.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Value().toLowerCase()), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              handler.newValue();
            }
          });
					
					panelBase.addHeaderImageButton(AppController.Lang.Rename(), MyResources.INSTANCE.iconBtnRename(),
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {

              if(box != null && box.isVisible()) {
                box.close();
              }
									
              //ask for confirm
              box = Functions.getMessageBoxPrompt(cardio.getNameClient(), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  if(!cardio.getNameClient().equals( text )) {
                    cardio.setName(text);
                    panelBase.setTitleText(cardio.getNameClient());
								        		  
                    handler.saveData(cardio);
                  }
                }
              });
              box.setTitle(AppController.Lang.Name());
              box.setMessage(AppController.Lang.EnterName() + ":");
              box.show();
            }
          });
					
					//remove cardio link
					panelBase.addHeaderImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Cardio().toLowerCase()), MyResources.INSTANCE.iconRemove(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              setData("btnClick", true);
              //ask for confirm
              box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisCardio().toLowerCase()), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  handler.cardioRemoved();
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
	 * Deletes selected measurements from grid
	 */
	protected void deleteSelectedValues() {

		if(grid.getSelectionModel().getSelectedItems().size() > 0) {

			//ask for confirm
			box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.SelectedValues().toLowerCase()), new MessageBoxHandler() {
				@Override
				public void okPressed(String text) {
					try {						
						//get ids of the selected items
						List<CardioValueModel> list = new ArrayList<CardioValueModel>();
						for(CardioValueModel m : grid.getSelectionModel().getSelectedItems()) {

							list.add(m);
							
							//remove item from grid
							store.remove(m);
						}
						
						handler.valuesRemoved(list);
						
					} catch (Exception e) {
			      Motiver.showException(e);
					}
				}
			});
			box.show();
		}
		
	}
	
}
