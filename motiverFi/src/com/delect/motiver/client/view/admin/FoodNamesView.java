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

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.admin.FoodNamesPresenter;
import com.delect.motiver.client.presenter.admin.FoodNamesPresenter.FoodNamesHandler;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.MicroNutrientModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class FoodNamesView extends FoodNamesPresenter.FoodNamesDisplay {

	private MessageBox box;
	private List<FoodNameModel> foods = new ArrayList<FoodNameModel>();

	private EditorGrid<FoodNameModel> grid;
	private FoodNamesHandler handler;
	//panels
	private LayoutContainer panelData = new LayoutContainer();
	private LayoutContainer panelMicro = new LayoutContainer();
	private ListStore<FoodNameModel> store;
	private TextArea taQuickAdd = new TextArea();

	//widgets
	private TextField<String> tfFilter = new TextField<String>();
	
	public FoodNamesView() {
		
		//fetch foods button
		Button btn = new Button("Fetch foods");
		btn.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				handler.fetchFoods();
			}
			
		});
		this.add(btn, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
		
		tfFilter.setMinLength(0);
		tfFilter.setAutoValidate(true);
		tfFilter.addListener(Events.Valid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.search(tfFilter.getValue());
			}
		});
		this.add(tfFilter, new RowData(-1, -1, new Margins(0, 0, 10, 0)));

		this.add(panelData);
		panelMicro.setLayout(new RowLayout());
		this.add(panelMicro, new RowData(-1, -1, new Margins(20, 0, 10, 0)));
		
		//quick add
		taQuickAdd.setWidth(800);
		taQuickAdd.setHeight(300);
		this.add(taQuickAdd, new RowData(-1, -1, new Margins(20, 0, 10, 0)));
		
		//button
		Button btn2 = new Button("Quick add");
		btn2.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				handler.quickAdd(taQuickAdd.getValue());
			}
			
		});
		this.add(btn2, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
		
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}
	
	@Override
	public void clearQuickAdd() {
		taQuickAdd.clear();
	}
	
	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}

	@Override
	public void hideProgress() {
		if(box != null && box.isVisible()) {
      box.close();
		}
		box = null; 
	}

	/**
	 * Shows micro nutrients for single model
	 * @param model
	 */
	public void initMicroNutrientsPanel(final FoodNameModel model) {
		panelMicro.removeAll();

		panelMicro.add(new Text(model.getName()), new RowData(-1, -1, new Margins(10, 0, 10, 0)));
		
		int i = 0;
		for(final MicroNutrientModel m : model.getMicroNutrients()) {
			HorizontalPanel panel = new HorizontalPanel();
			panel.setSpacing(5);
			
			//name
			panel.add(new Text("Name: "));
			final TextField<String> tfName = new TextField<String>();
			tfName.setValue(String.valueOf(m.getNameId()));
			panel.add(tfName);

			//value
			panel.add(new Text("Value: "));
			final NumberField tfValue = new NumberField();
			tfValue.setFormat(NumberFormat.getFormat("0.0###"));
			tfValue.setValue(m.getValue());
			panel.add(tfValue);
			panel.add(new Text("mg"));
			
			//update
			final int c = i;
			Button btnAdd = new Button("Update");
			btnAdd.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					
					m.setNameId(Integer.parseInt(tfName.getValue()));
					m.setValue(tfValue.getValue().doubleValue());
					//update list
					List<MicroNutrientModel> list = model.getMicroNutrients();
					list.set(c, m);
					model.setMicronutrients(list);

					store.update(model);
					
					handler.updateModel(model);
					
					initMicroNutrientsPanel(model);
				}
			});
			panel.add(btnAdd);
			
			//delete
			Button btnDelete = new Button("Delete");
			btnDelete.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					
					//update list
					List<MicroNutrientModel> list = model.getMicroNutrients();
					list.remove(c);
					model.setMicronutrients(list);

					store.update(model);
					
					handler.updateModel(model);
					
					initMicroNutrientsPanel(model);
				}
			});
			panel.add(btnDelete);
			
			panelMicro.add(panel, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
			
			i++;
		}
		
		panelMicro.add(new Text("New value"), new RowData(-1, -1, new Margins(10, 0, 5, 0)));
		
		//add new value
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(5);
		
		//name
		panel.add(new Text("Name: "));
		final TextField<String> tfNameNew = new TextField<String>();
		panel.add(tfNameNew);

		//value
		panel.add(new Text("Value: "));
		final NumberField tfValueNew = new NumberField();
		tfValueNew.setFormat(NumberFormat.getFormat("0.0"));
		panel.add(tfValueNew);
		panel.add(new Text("mg"));
		
		//add
		Button btnAdd = new Button("Add");
		btnAdd.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				MicroNutrientModel modelN = new MicroNutrientModel();
				modelN.setValue(tfValueNew.getValue().doubleValue());
				
				List<MicroNutrientModel> list = model.getMicroNutrients();
				list.add(modelN);
				model.setMicronutrients(list);
				store.update(model);

				handler.updateModel(model);
				
				initMicroNutrientsPanel(model);
			}
		});
		panel.add(btnAdd);
		
		panelMicro.add(panel);

		panelMicro.layout();
	}

	@Override
	public void setFoods(List<FoodNameModel> foods) {
		this.foods  = foods;
		initGrid();
	}

	@Override
	public void setHandler(FoodNamesHandler handler) {
		this.handler = handler;
	}

	@Override
	public void showCompleted(boolean successful, String msg) {
		hideProgress();

		box = new MessageBox();
		box.setIcon((successful)? MessageBox.INFO : MessageBox.ERROR);  
		box.setMessage((successful)? AppController.Lang.DataFetchedSuccessfully() : AppController.Lang.ErrorFetchingData(msg));
		box.setTitle(""); 
    box.setModal(true);   
    box.setButtons(Dialog.OK);
    box.show();
	}

	@Override
	public void showProgress(String text, int count, int total) {
		hideProgress();
		box = MessageBox.progress(AppController.Lang.PleaseWait(), text + "...", "");
		final ProgressBar progress = box.getProgressBar();

		double percent = (total != 0)? count / (double)total : 0;
		if(percent > 1) {
			percent = 1;
    }
		progress.updateProgress(percent, (int)(percent * 100) + "% " + AppController.Lang.Complete().toLowerCase());  
		box.setModal(true);
		box.show();
	}

	private void initGrid() {
		
		panelData.removeAll();
	    
    //loader for data
    store = new ListStore<FoodNameModel>();
    store.add(foods);
		
		//columns
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		//name
		ColumnConfig column = new ColumnConfig("n", "Name", 200); 
    column.setMenuDisabled(true);  
    column.setEditor(new CellEditor(new TextField<String>()));
    configs.add(column);   
    //energy
    column = new ColumnConfig("e", "Energy", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0.0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column);    
    //protein
    column = new ColumnConfig("p", "Protein", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0.0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column);    
    //carb
    column = new ColumnConfig("c", "Carb", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0.0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column);    
    //fet
    column = new ColumnConfig("f", "Fet", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0.0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column);  
    //portion
    column = new ColumnConfig("po", "Portion", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0.0"));  
    column.setEditor(new CellEditor(new NumberField()));
    column.setMenuDisabled(true);
    configs.add(column); 
    //is verified
    column = new ColumnConfig("tr", "Trusted", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0"));
    NumberField nf = new NumberField();
    nf.setSelectOnFocus(true);
    nf.setPropertyEditorType(Integer.class);
    final CellEditor editorVerified = new CellEditor(nf);
    column.setEditor(editorVerified);
    column.setMenuDisabled(true);
    configs.add(column);     
    //locale
    column = new ColumnConfig("l", "Locale", 75);
    column.setEditor(new CellEditor(new TextField<String>()));
    column.setMenuDisabled(true);
    configs.add(column);  
    //id
    column = new ColumnConfig("uid", "User id", 75);
    column.setAlignment(HorizontalAlignment.RIGHT);
    column.setNumberFormat(NumberFormat.getFormat("0"));  
    NumberField nf2 = new NumberField();
    nf2.setSelectOnFocus(true);
    nf2.setPropertyEditorType(Long.class);
    final CellEditor editorVerified2 = new CellEditor(nf2);
    column.setEditor(editorVerified2);
    column.setMenuDisabled(true);
    configs.add(column); 
	    
    //grid
		ColumnModel cm = new ColumnModel(configs);
		grid = new EditorGrid<FoodNameModel>(store, cm);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<FoodNameModel>());
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		grid.setClicksToEdit(ClicksToEdit.ONE);
		grid.setBorders(true); 
		grid.setAutoWidth(true);
		grid.setAutoHeight(true);
		//save value
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<FoodNameModel>>() {
			@Override
			public void handleEvent(GridEvent<FoodNameModel> be) {

				//get model
				final FoodNameModel modelEdited = grid.getStore().getAt(be.getRowIndex());
				
				handler.saveName(modelEdited);
			}
		});
		//show micronutrients when clicked
		grid.addListener(Events.RowClick, new Listener<GridEvent<FoodNameModel>>() {
			@Override
			public void handleEvent(GridEvent<FoodNameModel> be) {
				initMicroNutrientsPanel(be.getModel());
			}
		});

		panelData.add(grid);

		//combine button
		Button btnCombine = new Button();
		btnCombine.setText("Combine selected values");
		btnCombine.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				final List<FoodNameModel> list = grid.getSelectionModel().getSelectedItems();
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
