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
package com.delect.motiver.client.view.admin;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.admin.ExerciseNamesPresenter;
import com.delect.motiver.client.presenter.admin.ExerciseNamesPresenter.ExerciseNamesHandler;
import com.delect.motiver.shared.ExerciseNameModel;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class ExerciseNamesView extends ExerciseNamesPresenter.ExerciseNamesDisplay {

	private List<ExerciseNameModel> exercises = new ArrayList<ExerciseNameModel>();

	private ExerciseNamesHandler handler;
	private LayoutContainer panelData = new LayoutContainer();
	private ListStore<ExerciseNameModel> store;
	
	private TextField<String> tfFilter = new TextField<String>();

	public ExerciseNamesView() {
		
		tfFilter.setMinLength(2);
		tfFilter.setAutoValidate(true);
		tfFilter.addListener(Events.Valid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.search(tfFilter.getValue());
			}
		});
		this.add(tfFilter, new RowData(-1, -1, new Margins(0, 0, 10, 0)));

		this.add(panelData);
	}
	
	@Override
	public Widget asWidget() {
		
		this.layout();
		return this;
	}
	
	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}

	@Override
	public void setExercises(List<ExerciseNameModel> exercises) {
		this.exercises  = exercises;
		initGrid();
	}

	@Override
	public void setHandler(ExerciseNamesHandler handler) {
		this.handler = handler;
	}

	private void initGrid() {
		
		panelData.removeAll();
	    
    store = new ListStore<ExerciseNameModel>();
    store.add(exercises);
		store.addFilter(new StoreFilter<ExerciseNameModel>() {
			@Override
			public boolean select(Store<ExerciseNameModel> store, ExerciseNameModel parent, ExerciseNameModel item, String property) {
				return item.getName().toLowerCase().contains(tfFilter.getValue().toLowerCase());
			}
		});
		
		//columns
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		//name
		ColumnConfig column = new ColumnConfig("n", "Name", 200); 
    column.setMenuDisabled(true); 
    column.setEditor(new CellEditor(new TextField<String>()));
    configs.add(column);     
	  
    //targets as combo box
    final SimpleComboBox<String> comboT = new SimpleComboBox<String>();
    comboT.setForceSelection(true);
    comboT.setTriggerAction(TriggerAction.ALL); 
    comboT.setEditable(false);
    comboT.setAllowBlank(false);
    for(int i=0; i < AppController.LangConstants.Targets().length; i++) {
      comboT.add(AppController.LangConstants.Targets()[i]);
    }
    CellEditor editorT = new CellEditor(comboT) {   
      @Override   
      public Object postProcessValue(Object value) { 
        if (value == null) {   
          return value;   
        }   
        try {
          final String val = ((ModelData) value).get("value");
          //get index
          for(int i=0; i < AppController.LangConstants.Targets().length; i++) {
            if(AppController.LangConstants.Targets()[i].equals(val)) {
              return i;
            }
          }
        } catch (Exception e) {
          Motiver.showException(e);
        }   
        return 0;
      }
      @Override   
      public Object preProcessValue(Object value) {   
        if (value == null) {   
          return value;   
        }   
        try {
          return comboT.findModel(AppController.LangConstants.Targets()[Integer.parseInt(value.toString())]);
        } catch (NumberFormatException e) {
          return null;
        }
      }   
    };
    column = new ColumnConfig("t", "Target", 110);
    column.setMenuDisabled(true);   
    column.setSortable(false); 
    column.setEditor(editorT);
    column.setRenderer(new GridCellRenderer<ExerciseNameModel>() {
			@Override
			public Object render(ExerciseNameModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ExerciseNameModel> store, Grid<ExerciseNameModel> grid) {
				return AppController.LangConstants.Targets()[model.getTarget()];
			}
    });
    configs.add(column);
	    
		//url
		column = new ColumnConfig("v", "Url", 200); 
    column.setMenuDisabled(true); 
    column.setEditor(new CellEditor(new TextField<String>()));
    configs.add(column); 
	    
		//locale
		column = new ColumnConfig("l", "Locale", 75); 
    column.setMenuDisabled(true); 
    column.setEditor(new CellEditor(new TextField<String>()));
    configs.add(column); 
	    
    //grid
		ColumnModel cm = new ColumnModel(configs);
		final EditorGrid<ExerciseNameModel> grid = new EditorGrid<ExerciseNameModel>(store, cm);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<ExerciseNameModel>());
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		grid.setBorders(true); 
		grid.setAutoWidth(true);
		grid.setAutoHeight(true);
		//save value
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<ExerciseNameModel>>() {
			@Override
			public void handleEvent(GridEvent<ExerciseNameModel> be) {

				//get model
				final ExerciseNameModel modelEdited = grid.getStore().getAt(be.getRowIndex());
				
				handler.saveName(modelEdited);
			}
		});

		panelData.add(grid);
		
		//combine button
		Button btnCombine = new Button();
		btnCombine.setText("Combine selected values");
		btnCombine.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				final List<ExerciseNameModel> list = grid.getSelectionModel().getSelectedItems();
				if(list.size() >= 2) {	
					
					//ask for confirm
					MessageBox.confirm("Confirm", "Are you sure you want to combine these names?", new Listener<MessageBoxEvent>() {   
						public void handleEvent(MessageBoxEvent ce) {
							Button btn = ce.getButtonClicked();
							if(Dialog.YES.equals(btn.getItemId())) {

								//get model we combine other models
								Long firstId = list.get(0).getId();
								Long[] ids = new Long[list.size() - 1];
								for(int i = 0; i < ids.length; i++) {
									ids[i] = list.get(i + 1).getId();
			          }
								
								handler.combineNames(firstId, ids);
							}
		        }   
					}); 
					
				}				
			}
		});
		panelData.add(btnCombine);
		
		panelData.layout();
	}
}
