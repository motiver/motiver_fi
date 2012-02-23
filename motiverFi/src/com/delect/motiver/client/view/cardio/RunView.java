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
import com.delect.motiver.client.presenter.cardio.RunPresenter;
import com.delect.motiver.client.presenter.cardio.RunPresenter.RunHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.widget.NameInputWidget;
import com.delect.motiver.client.view.widget.NameInputWidget.EnterNamePanelHandler;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.client.view.SmallNotePanel;
import com.delect.motiver.client.view.SmallNotePanelDisplay;
import com.delect.motiver.shared.util.CommonUtils;
import com.delect.motiver.shared.util.CommonUtils.MessageBoxHandler;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
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
import com.extjs.gxt.ui.client.widget.Text;
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
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;


public class RunView extends RunPresenter.RunDisplay {

	private Grid<RunValueModel> grid;
	private RunHandler handler;
	//panels
	private SmallNotePanelDisplay panelBase = (SmallNotePanelDisplay)GWT.create(SmallNotePanel.class);
	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelDataOld = new LayoutContainer();

	private LayoutContainer panelDates = new LayoutContainer();
	private LayoutContainer panelDistance = new LayoutContainer();
	
	private RunModel run;
	private ListStore<RunValueModel> store = new ListStore<RunValueModel>();
	private List<RunValueModel> values;
	
	MessageBox box = null;
	
	public RunView() {
		
		try {
			panelBase.setStylePrefix("panel-run");
			
			panelBase.getPanelData().addListener(Events.Show, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.valuesVisible();
				}
			});
			
			this.add(panelBase);

			//distance
			TableLayout tl = new TableLayout(2);
			tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
      panelDistance.setLayout(tl);
	        
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	@Override
	public Widget asWidget() {

		panelBase.getPanelData().removeAll();
		
		try {
			//if no model -> ask for name
			if(run.getId() == 0) {
				
				//add panel where user can type name
				NameInputWidget panelNameInput = new NameInputWidget(new EnterNamePanelHandler() {
					@Override
					public void newName(String name) {
						//if cancelled
						if(name == null) {
							handler.saveData(null);
            }
						else {
							run.setName(name);
							handler.saveData(run);
						}
					}
				});
				panelBase.getPanelData().add(panelNameInput);
				panelBase.getPanelData().setVisible(true);
				
			}
			//model set
			else {

				panelDataOld.setStyleAttribute("min-height", "150px");
				panelDataOld.setLayout(new RowLayout());

				initTitlePanel();
				
				initInfoPanel();
				
				panelBase.getPanelData().add(panelDistance, new RowData(-1, -1, new Margins(10, 0, 10, 0)));
				panelBase.getPanelData().add(panelDates, new RowData(-1, -1, new Margins(10, 0, 10, 0)));
				panelBase.getPanelData().add(panelDataOld, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
				panelBase.getPanelData().add(panelComments, new RowData(-1, -1, new Margins(10, 0, 0, 0)));

				panelBase.getPanelData().setVisible(false);
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
	public void setHandler(RunHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void setModel(RunModel run) {
		this.run = run;
	}

	/**
	 * Populates list with given values
	 */
	@Override
	public void setValues(List<RunValueModel> values) {
		
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
	 * Initializes content panel
	 *  - datefield
	 *  - list
	 *  - comments
	 */
	private void initInfoPanel() {

		Text text = new Text(AppController.Lang.Distance() + ":");
		text.setStyleAttribute("margin-right", "5px");
    panelDistance.add(text);
        
		final MySpinnerField tfDistance = new MySpinnerField();   
		tfDistance.setAllowBlank(false);   
		tfDistance.setEditable(true);
		tfDistance.setMinValue(0);  
		tfDistance.setMaxValue(1000);
		tfDistance.setValue(run.getDistance());
		tfDistance.setIncrement(1);
		tfDistance.setPropertyEditorType(Double.class);
		tfDistance.setFormat(NumberFormat.getFormat("0.0 " + StringConstants.UNITS_DISTANCE[AppController.User.getMeasurementSystem()]));
		//change when lost focus
		tfDistance.addListener(Events.Valid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(Double.compare(tfDistance.getValue().doubleValue(), run.getDistance()) != 0) {
					run.setDistance(tfDistance.getValue().doubleValue());
					handler.saveData(run);
				}
			}
		});
		panelDistance.add(tfDistance);
		
		panelDistance.layout();
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
		column.setRenderer(new GridCellRenderer<RunValueModel>() {
			@Override
			public Object render(RunValueModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<RunValueModel> store, Grid<RunValueModel> grid) {
				return CommonUtils.getDateTimeString(model.getDate(), false, true);
			}
    });
    column.setMenuDisabled(true); 
    configs.add(column); 
	    
    //duration
    column = new ColumnConfig("du", AppController.Lang.Duration(), 100);
    column.setRenderer(new GridCellRenderer<RunValueModel>() {
			@Override
			public Object render(RunValueModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<RunValueModel> store, Grid<RunValueModel> grid) {
				long value = model.getDuration();
				return CommonUtils.getDurationString(value);
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
      PagingLoader<PagingLoadResult<RunValueModel>> loader = new BasePagingLoader<PagingLoadResult<RunValueModel>>(proxy);  
      loader.setRemoteSort(true);  
	      
      store = new ListStore<RunValueModel>(loader);  
	      
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
		grid = new Grid<RunValueModel>(store, cm);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<RunValueModel>());
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
			panelBase.setTitleText(run.getNameClient());
			
			//buttons
			if(run.getId() != 0) {

				if(run.getUid().equals(AppController.User.getUid())) {
					
					//add value
					panelBase.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Value().toLowerCase()), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              handler.newValue();
            }
          });
					
					//rename run
					panelBase.addHeaderImageButton(AppController.Lang.Rename(), MyResources.INSTANCE.iconBtnRename(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              if(box != null && box.isVisible()) {
                box.close();
              }
								
              //ask for confirm
              box = CommonUtils.getMessageBoxPrompt(run.getNameClient(), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  if(!run.getNameClient().equals( text )) {
                    run.setName(text);
                    panelBase.setTitleText(run.getNameClient());
							        		  
                    handler.saveData(run);
                  }
                }
              });
              box.setTitle(AppController.Lang.Name());
              box.setMessage(AppController.Lang.EnterName() + ":");
              box.show();
            }
          });
					
					//remove run link
					panelBase.addHeaderImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Run().toLowerCase()), MyResources.INSTANCE.iconRemove(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              setData("btnClick", true);
              //ask for confirm
              box = CommonUtils.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisRun().toLowerCase()), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  handler.runRemoved();
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
			box = CommonUtils.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.SelectedValues().toLowerCase()), new MessageBoxHandler() {
				@Override
				public void okPressed(String text) {
					try {
						
						//get ids of the selected items
						List<RunValueModel> list = new ArrayList<RunValueModel>();
						for(RunValueModel m : grid.getSelectionModel().getSelectedItems()) {

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
