/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.profile.MeasurementPresenter;
import com.delect.motiver.client.presenter.profile.MeasurementPresenter.MeasurementHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.EnterNamePanel;
import com.delect.motiver.client.view.EnterNamePanel.EnterNamePanelHandler;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.client.view.SmallNotePanel;
import com.delect.motiver.client.view.SmallNotePanelDisplay;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.MeasurementValueModel;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.LineDataProvider;
import com.extjs.gxt.charts.client.model.Scale;
import com.extjs.gxt.charts.client.model.ScaleProvider;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.charts.ChartConfig;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.charts.client.model.charts.dots.Dot;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;


public class MeasurementView extends MeasurementPresenter.MeasurementDisplay {

	private DateField dfMeas;

	private Grid<MeasurementValueModel> grid;
	private MeasurementHandler handler;
	private MeasurementModel measurement;
	private LayoutContainer panelComments = new LayoutContainer();
	private LayoutContainer panelDataOld = new LayoutContainer();
	private LayoutContainer panelDates = new LayoutContainer();
    	
	private boolean showGraph = true;
	private MySpinnerField textValue;
	private List<MeasurementValueModel> values;
	MessageBox box = null;
	
	MyButton btnSwitchView = new MyButton();
	LayoutContainer lcTop = new LayoutContainer();
	
	SmallNotePanelDisplay panelBase = (SmallNotePanelDisplay)GWT.create(SmallNotePanel.class);
	ListStore<MeasurementValueModel> store = new ListStore<MeasurementValueModel>();
	TextField<String> tfUnit = new TextField<String>();
	
	/**
	 * Measurement view
	 * @param showOnlyTitle : show title (TRUE, open measurement when clicked) or show just measurement (FALSE)
	 */
	public MeasurementView() {
		
		try {
			panelBase.setStylePrefix("panel-measurement");
			
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
			if(measurement.getId() == 0) {
				
				//add panel where user can type name
				EnterNamePanel panelNameInput = new EnterNamePanel(new EnterNamePanelHandler() {
					@Override
					public void newName(String name) {
						//if cancelled
						if(name == null) {
							handler.saveData(null);
	          }
						else {
							measurement.setName(name);
							handler.saveData(measurement);
						}
					}
				});
				panelBase.getPanelData().add(panelNameInput);
				panelBase.getPanelData().setVisible(true);
				
			}
			//model set
			else {
				
				//top panel
				panelBase.getPanelData().add(getTopPanel(), new RowData(-1, -1, new Margins(5, 0, 10, 0)));
				
				//PANEL DATES
				panelBase.getPanelData().add(panelDates, new RowData(-1, -1, new Margins(0, 0, 10, 0)));

				//switch view
        btnSwitchView = new MyButton();
				btnSwitchView.setScale(ButtonScale.MEDIUM);
				btnSwitchView.setText( (showGraph)? AppController.Lang.ShowInList() : AppController.Lang.ShowInGraph());
				btnSwitchView.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						showGraph = !showGraph;
						btnSwitchView.setText( (showGraph)? AppController.Lang.ShowInList() : AppController.Lang.ShowInGraph());
						setValues(values);
					}			
				});
				panelBase.getPanelData().add(btnSwitchView, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
				
				//graph / list
				panelDataOld.setStyleAttribute("min-height", "150px");
				panelDataOld.setLayout(new RowLayout()); 
				panelBase.getPanelData().add(panelDataOld, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
				
				panelBase.getPanelData().add(panelComments, new RowData(-1, -1, new Margins(10, 0, 0, 0)));
			
				//update fields
				dfMeas.setValue(measurement.getDate());
				tfUnit.setValue(measurement.getUnit());
				textValue.setValue(measurement.getTarget());
				textValue.setFormat(NumberFormat.getFormat("0.0 " + measurement.getUnit()));
				
				initTitlePanel();
				
				panelBase.getPanelData().setVisible(false);
				
				//hide top panel if not our measurement
				if(!measurement.getUid().equals(AppController.User.getUid())) {
					lcTop.removeFromParent();
        }
				
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
	public void setHandler(MeasurementHandler handler) {
		this.handler = handler;
	} 
	

	@Override
	public void setModel(MeasurementModel measurement) {
		this.measurement = measurement;
	}
	
	/**
	 * Populates list/graph with given values
	 */
	@Override
	public void setValues(List<MeasurementValueModel> values) {

		try {
			this.values = values;
			
			//if null -> clear panel
			if(values == null) {
				panelDataOld.removeAll();
				btnSwitchView.setVisible(false);
				return;
			}
			
			//if no values -> hide button
			btnSwitchView.setVisible(values.size() > 0);

			panelDataOld.removeAll();
			
			if(values.size() > 0) {
				
				//graph
				if(showGraph ) {
					
					//if only single value -> show as text
					if(values.size() == 1) {
						VerticalPanel panelSingleValue = new VerticalPanel();
						panelSingleValue.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
						panelSingleValue.setSpacing(10);
						panelSingleValue.setWidth("100%");
						
						Text text = new Text(AppController.Lang.OnlyOneValueFound() + ":");
						text.setStyleName("label-title-small");
						panelSingleValue.add(text);
						
						//value
						MeasurementValueModel value = values.get(0);
						Text textValue = new Text();
						textValue.setText(Functions.getDateString(value.getDate(), true, true) + ": " + value.getValue() + measurement.getUnit());
						panelSingleValue.add(textValue);
						
						panelDataOld.add(panelSingleValue, new RowData(-1, -1, new Margins(100, 0, 100, 0)));
					}
					//show in graph
					else {
						store.removeAll();
						store.add(values);						
						
						//show graph
						final Chart chart = new Chart(Constants.URL_APP_STATIC + "resources/chart/open-flash-chart.swf");
						chart.setHeight(500);
            chart.setBorders(true);
            chart.setChartModel(getChartData());
            panelDataOld.add(chart);
					}
				    
				}
				else {	//show list
					initList();
				}
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

	private ChartModel getChartData() {  
		
		ChartModel model = new ChartModel();  
		model.setBackgroundColour("-1");  
		model.setLegend(new Legend(Position.TOP, true));  
		model.setScaleProvider(new ScaleProvider() {
			@Override
			public Scale calcScale(double min, double max) {
				if (min == 0 && max == 0) {
          return new Scale(-1, 1, 1);
        }
        min = (int)(min * ((min > 0) ? 1 : 1.00));
        max = (int)(max * ((max > 0) ? 1.00 : 1)) + 1;
			      
        //round to closest five
        min -= min % 5;
        max += (5 - max % 5);

        double interval = 2;
        double diff = Math.abs(max - min);
        if(diff <= 30) {
          interval = 5;
        }
        else if(diff <= 60) {
          interval = 10;
        }
        else {
          interval = (int)(diff / 5);
        }
			      
        return new Scale(min, max, interval);
			}
			
		});  
	    
		//values
		LineChart line = new LineChart();
    line.setColour("#000000");  
    LineDataProvider lineProvider = new LineDataProvider("v") {
      @Override
      public void populateData(ChartConfig config) {
        LineChart chart = (LineChart) config;
        chart.getValues().clear();

        XAxis xAxis = null;
        if (labelProperty != null || labelProvider != null) {
          xAxis = chart.getModel().getXAxis();
          if (xAxis == null) {
            xAxis = new XAxis();
            chart.getModel().setXAxis(xAxis);
          }
          xAxis.getLabels().getLabels().clear();
        }

        boolean first = true;
        for (ModelData m : store.getModels()) {
          Number value = getValue(m);
          if (value == null) {
            chart.addNullValue();
          } else {
            Dot dot = new Dot();
            dot.setValue(value);
            dot.setTooltip(value.doubleValue() + measurement.getUnit() + " (" + Functions.getDateString(((MeasurementValueModel)m).getDate(), true, true) + ")");
            chart.addDots(dot);
            maxYValue = first ? value.doubleValue() : Math.max(maxYValue, value.doubleValue());
            minYValue = first ? value.doubleValue() : Math.min(minYValue, value.doubleValue());
            first = false;
          }
          if (xAxis != null) {
            xAxis.addLabels(getLabel(m));
          }
        }  		
      }
    };
    lineProvider.setLabelProvider(new ModelStringProvider<ModelData>() {
			@Override
			public String getStringValue(ModelData model, String property) {
				try {					
					if(store.getModels().size() * 43 <= (getWidth() - 25)) {
						MeasurementValueModel measurement = (MeasurementValueModel)model;
						return Functions.getDateString(measurement.getDate(), false, true, true);
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}
				return "";
			}
	    	
    });
	    	
    lineProvider.bind(store);  
    line.setDataProvider(lineProvider);  
    model.addChartConfig(line);
	    
    return model;  
	 }

	/**
	 * Returns top panel
	 */
	private LayoutContainer getTopPanel() {

		//TOP PANEL
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		lcTop.setLayout(layout);
		
		//unit & target (left)
		lcTop.add(new Text(AppController.Lang.Unit() + ":"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
		tfUnit.setStyleAttribute("margin-left", "10px");
		tfUnit.setMaxLength(5);
		tfUnit.setAutoValidate(true);
		tfUnit.addListener(Events.Valid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//if value changed
				if(!measurement.getUnit().equals(tfUnit.getValue())) {
					measurement.setUnit(tfUnit.getValue());
					handler.saveData(measurement);
				}
			}
		});
		lcTop.add(tfUnit, new HBoxLayoutData(new Margins(0, 40, 0, 0)));

		//target
		lcTop.add(new Label(AppController.Lang.Goal() + ":&nbsp;"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
		dfMeas = new DateField();
		DateTimePropertyEditor pr = new DateTimePropertyEditor(Functions.FmtShort);
		dfMeas.setPropertyEditor(pr);
		dfMeas.setMinValue(new Date());
		dfMeas.setFieldLabel(AppController.Lang.Date());
		dfMeas.setStyleAttribute("margin-left", "10px");
		dfMeas.addListener(Events.Valid, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if(measurement != null) {						
					measurement.setDate(dfMeas.getValue());
					handler.saveData(measurement);
					
				}
			}
		});
		lcTop.add(dfMeas, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
		textValue = new MySpinnerField();   
		textValue.setFieldLabel(AppController.Lang.Value());   
		textValue.setAllowBlank(false);   
		textValue.setEditable(true);
		textValue.setMinValue(0);  
		textValue.setMaxValue(10000);
		textValue.setStyleAttribute("margin-left", "10px");
		textValue.setPropertyEditorType(Double.class);
		new KeyNav<ComponentEvent>(textValue) {
			@Override
			public void onKeyPress(ComponentEvent event) {
				if(event.getKeyCode() == KeyCodes.KEY_ENTER && handler != null) {
					//only if target has changed
					if(Double.compare(measurement.getTarget(), textValue.getValue().doubleValue()) != 0) {
						measurement.setTarget(textValue.getValue().doubleValue());
						//refresh view
						setValues(values);
						
						handler.saveData(measurement);
					}
				}
			}
		};
		textValue.addListener(Events.Blur, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if(handler != null) {
					//only if target has changed
					if(Double.compare(measurement.getTarget(), textValue.getValue().doubleValue()) != 0) {
						measurement.setTarget(textValue.getValue().doubleValue());
						//refresh view
						setValues(values);

						handler.saveData(measurement);
					}
				}
			}
		});
		lcTop.add(textValue, new HBoxLayoutData(new Margins(0, 20, 0, 0)));
		
		//spacer
		HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
    flex.setFlex(1);
    lcTop.add(new Text(), flex); 
		
		return lcTop;
		
	}

	/**
	 * Inits list that shows measurements
	 * Parameters: unit for value column
	 */
	private void initList() {
		
		//columns
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig("d", AppController.Lang.Date(), 200);  
    column.setDateTimeFormat(Functions.Fmt); 
		column.setRenderer(new GridCellRenderer<MeasurementValueModel>() {
			@Override
			public Object render(MeasurementValueModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<MeasurementValueModel> store, Grid<MeasurementValueModel> grid) {
				return Functions.getDateString(model.getDate(), false, true);
			}
    });
    column.setMenuDisabled(true); 
    configs.add(column);   
	    
    column = new ColumnConfig("v", AppController.Lang.Value(), 200);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0.0 " + measurement.getUnit()));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column); 

    final PagingToolBar toolBar = new PagingToolBar(Constants.LIMIT_LIST_RECORDS);  
    //set paging if more than 50 rows
    if(values.size() > Constants.LIMIT_LIST_RECORDS) {
      PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(values);  
	    	  
      // loader  
      PagingLoader<PagingLoadResult<MeasurementValueModel>> loader = new BasePagingLoader<PagingLoadResult<MeasurementValueModel>>(proxy);  
      loader.setRemoteSort(true);  
	      
      store = new ListStore<MeasurementValueModel>(loader);  
	      
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
		grid = new Grid<MeasurementValueModel>(store, cm);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<MeasurementValueModel>());
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setBorders(true);   
    grid.getView().setEmptyText(AppController.Lang.NoMeasurements());
		grid.setColumnResize(false);
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
			panelBase.setTitleText(measurement.getNameClient());
			
			//buttons
			if(measurement.getId() != 0) {

				if(measurement.getUid().equals(AppController.User.getUid())) {
					
					//add value
					panelBase.addHeaderButton(AppController.Lang.AddTarget(AppController.Lang.Value().toLowerCase()), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              handler.newValue();
            }
          });
					
					//rename measurement
					panelBase.addHeaderImageButton(AppController.Lang.Rename(), MyResources.INSTANCE.iconBtnRename(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {

              if(box != null && box.isVisible()) {
                box.close();
              }
              //ask for confirm
              box = Functions.getMessageBoxPrompt(measurement.getNameClient(), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  if(!measurement.getNameClient().equals( text )) {
                    measurement.setName(text);
                    panelBase.setTitleText(measurement.getNameClient());
							        		  
                    handler.saveData(measurement);
                  }
                }
              });
              box.setTitle(AppController.Lang.Name());
              box.setMessage(AppController.Lang.EnterName() + ":");
              box.show();
            }
          });
					
					//remove measurement link
					panelBase.addHeaderImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Workout().toLowerCase()), MyResources.INSTANCE.iconRemove(), 
          new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              setData("btnClick", true);
              //ask for confirm
              box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisMeasurement().toLowerCase()), new MessageBoxHandler() {
                @Override
                public void okPressed(String text) {
                  handler.measurementRemoved();
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
						List<MeasurementValueModel> list = new ArrayList<MeasurementValueModel>();
						for(MeasurementValueModel m : grid.getSelectionModel().getSelectedItems()) {

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
