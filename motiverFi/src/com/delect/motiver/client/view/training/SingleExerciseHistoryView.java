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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.training.SingleExerciseHistoryPresenter;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;


public class SingleExerciseHistoryView extends SingleExerciseHistoryPresenter.SingleExerciseHistoryDisplay {

	/**
	 * Parses reps value and return highest/lowest rep
	 * @param reps
	 * @param sets
	 * @return highest/lowest rep
	 */
	private static double parseReps(String reps, int sets, boolean highest) {

		//split reps
		String[] reps_arr = reps.split(",");
		
		//go through each value
		double value = (highest)? 0 : 10000;
		for(int i=0; i < reps_arr.length; i++) {
			try {
				String rep = reps_arr[i];
				
				//remove spaces
				rep = rep.replace(" ", "");
				
				if(rep.length() > 0) {
					//if numeric
					boolean isNumeric = false;
					double nro = 0;
					try {
						nro = Double.parseDouble(rep);
						isNumeric = true;
					} catch (NumberFormatException e) {
					}
					
					if(isNumeric) {
						//save to highest value
						if(highest && nro > value) {
							value = nro;
            }
						else if(!highest && nro < value) {
							value = nro;
            }
					}
					//if 2+1 (=3)
					else if(rep.matches("([0-9]*)([+]{1}([0-9]*))*")) {
						String[] arr = rep.split("\\+");
						double sum = 0;
						//sum all reps together
						for(int j=0; j < arr.length; j++) {
							sum += Double.parseDouble(arr[j]);
            }
						if(highest && sum > value) {
							value = sum;
            }
						else if(!highest && nro < value) {
							value = sum;
            }
					}
					//if 3x10
					else if(rep.matches("([0-9]*)x([0-9]*)")) {
						String[] arr = rep.split("x");
						double n = Double.parseDouble(arr[1]);
						if(highest && n > value) {
							value = n;
						}
						else if(!highest && nro < value) {
							value = n;
						}
					}
					//if 6-10 (means for example 6,7,8,9,10)
					else if(rep.matches("([0-9]*)-([0-9]*)")) {
						String[] arr = rep.split("-");
						//get total reps
						double first_rep = Double.parseDouble(arr[0]);
						double last_rep = Double.parseDouble(arr[1]);
						int sets_count = (sets - (reps_arr.length) + 1);
						//check each set
						//if increasing
						if(last_rep > first_rep) {
							for(double j=first_rep; j <= last_rep; j += ((last_rep - first_rep + 1) / sets_count)) {
								if(highest && j > value) {
									value = j;
								}
								else if(!highest && j < value) {
									value = j;
								}
							}
						}
						//decreasing
						else {
							for(double j=first_rep; j >= last_rep; j -= ((first_rep - last_rep + 1) / sets_count)) {
								if(highest && j > value) {
									value = j;
								}
								else if(!highest && j < value) {
									value = j;
								}
							}
						}
					}
				}
			} catch (Exception e) {
	      Motiver.showException(e);
			}
		}
		
		return value;
	}
	private Grid<ExerciseModel> grid;
	private BasePagingLoader<PagingLoadResult<ExerciseModel>> loader;
	private ListStore<ExerciseModel> store = new ListStore<ExerciseModel>();

	private StoreSorter<ExerciseModel> storeSorter = new StoreSorter<ExerciseModel>() {
    @Override
    public int compare(Store<ExerciseModel> st, ExerciseModel m1, ExerciseModel m2, String property) {
      if (property != null) {

        //if reps
        if(property.equals("r")) {
          //parse reps
          double rep1 = parseReps(m1.getReps(), m1.getSets(), store.getSortDir().equals(SortDir.DESC));
          double rep2 = parseReps(m2.getReps(), m2.getSets(), store.getSortDir().equals(SortDir.DESC));

          return (rep1 < rep2)? -1 : 1;
        }
        //if weights
        else if(property.equals("w")) {
          //parse reps
          double w1 = parseReps(m1.getWeights(), m1.getSets(), store.getSortDir().equals(SortDir.DESC));
          double w2 = parseReps(m2.getWeights(), m2.getSets(), store.getSortDir().equals(SortDir.DESC));

          return (w1 < w2)? -1 : 1;
        }
        else {
          return super.compare(store, m1, m2, property);
        }
      }
      return super.compare(store, m1, m2, property);
    }
  };
	private PagingToolBar toolBar = null;  
	
	//grid
	private List<ExerciseModel> values;
	
	
	

	@Override
	public Widget asWidget() {
		this.setStyleAttribute("min-height", "125px");
		this.setStyleAttribute("margin-top", "5px");
		return this;
	}
	
	@Override
	public void setLastWeights(List<ExerciseModel> result) {
		values = result;
		
		initList();
		
		this.removeAll();
		this.add(grid);
		if(values.size() > Constants.LIMIT_LIST_RECORDS*3) {
			this.add(toolBar);
    }
		this.layout();
	}

	/**
	 * Initializes listview
	 */
	private void initList() {

		//show list
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();   
		ColumnConfig column = new ColumnConfig("d", AppController.Lang.Date(), 100);
		column.setDateTimeFormat(DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()]));  
    column.setAlignment(HorizontalAlignment.LEFT);
    column.setMenuDisabled(true);  
    column.setSortable(true);
    configs.add(column);   
	  
    //sets
    column = new ColumnConfig("s", AppController.Lang.Sets(), 100); 
    column.setMenuDisabled(true);  
    column.setSortable(true);
    configs.add(column);   
    //reps
    column = new ColumnConfig("r", AppController.Lang.Reps(), 150);
    column.setMenuDisabled(true);  
    column.setSortable(true);
    configs.add(column);   
    //weights
    column = new ColumnConfig("w", AppController.Lang.Weights(), 200);
    column.setMenuDisabled(true);  
    column.setSortable(true);
    configs.add(column);  

    toolBar = new PagingToolBar(Constants.LIMIT_LIST_RECORDS*3);
	    
    //set paging if more than 25 rows
    if(values.size() > Constants.LIMIT_LIST_RECORDS*3) {
      PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(values);
	    	  
      // loader  
      loader = new BasePagingLoader<PagingLoadResult<ExerciseModel>>(proxy);
      store = new ListStore<ExerciseModel>(loader);
      loader.setRemoteSort(false);
      toolBar.bind(loader);  
	      
      loader.load(0, Constants.LIMIT_LIST_RECORDS*3);
    }
    else {
			//populate store
			store.removeAll();
			store.add(values);	    	
    }
	    
    //default sort
    store.sort("d", SortDir.DESC);
    store.setStoreSorter(storeSorter);
	    
    ColumnModel cm = new ColumnModel(configs);
		grid = new Grid<ExerciseModel>(store, cm);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<ExerciseModel>());
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setBorders(true);
		grid.setAutoExpandColumn("w");
		grid.setColumnResize(false);
		grid.setAutoHeight(true);
	}
	
}
