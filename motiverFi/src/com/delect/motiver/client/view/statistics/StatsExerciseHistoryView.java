/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.statistics;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.Templates;
import com.delect.motiver.client.presenter.statistics.StatsExerciseHistoryPresenter;
import com.delect.motiver.client.presenter.statistics.StatsExerciseHistoryPresenter.StatsExerciseHistoryHandler;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.ToolTip;
import com.extjs.gxt.charts.client.model.ToolTip.MouseStyle;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.HorizontalBarChart;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelProcessor;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class StatsExerciseHistoryView extends StatsExerciseHistoryPresenter.StatsExerciseHistoryDisplay {

	static String[] colors = new String[] {"#EFD279", "#95CBE9", "#024769", "#AFD775", "#2C5700", "#DE9D7F"};

	private StatsExerciseHistoryHandler handler;

	private ListStore<ExerciseNameModel> store;
	
	LayoutContainer panelData = new LayoutContainer();
	
	public StatsExerciseHistoryView() {
		this.setLayout(new RowLayout());
	}
	
	@Override
	public Widget asWidget() {

		//title
		Text textTitle = new Text(AppController.Lang.Exercises());
		textTitle.setStyleName("label-title-big");
		this.add(textTitle, new RowData(-1, -1, new Margins(0, 0, 20, 0)));
		
		//search box
		ComboBox<ExerciseNameModel> comboName = addExerciseCombo();
		comboName.setWidth("100%");
		this.add(comboName, new RowData(1, -1, new Margins(0, 0, 10, 0)));
		
		panelData.setAutoHeight(true);
		this.add(panelData);
		
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}

	@Override
	public void setExercisesData(List<ExerciseNameModel> exercises) {

		try {
			//show bar graph
			final Chart chart = new Chart(Constants.URL_APP_STATIC+"resources/chart/open-flash-chart.swf");
			int height = 100;
			if(exercises.size() > 0) {
				height = exercises.size() * 40;
      }
			chart.setHeight(height); 
			panelData.setHeight(height);
			chart.setWidth(600);

			HorizontalBarChart bchart = new HorizontalBarChart(); 
			bchart.setTooltip("#val#");

			//add values
			List<String> list = new ArrayList<String>();
			int maxCount = 0;
			int i = 0;
			for(ExerciseNameModel m : exercises) {
				try {
					list.add(0, Functions.getExerciseName(m));
					 
					int count = Integer.parseInt(m.get("count").toString());
					if(count > maxCount) {
						maxCount = count;
          }
					
					bchart.addBars(new HorizontalBarChart.Bar(count, colors[ (i < colors.length)? i : i - colors.length]));
					
					i++;
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
			
			ChartModel cm = new ChartModel();  
			cm.setBackgroundColour("#fffff5");  

			//y-axis
      YAxis ya = new YAxis();
			ya.setOffset(true);
			ya.addLabels(list);   
			cm.setYAxis(ya); 

			//x-axis
			XAxis xa = new XAxis();  
			double max = (double)maxCount * 1.3;
			max -= max % 10;
			cm.setXAxis(xa);
	 	    
			chart.setChartModel(cm);
			cm.addChartConfig(bchart); 
			cm.setTooltipStyle(new ToolTip(MouseStyle.NORMAL));  

			panelData.removeAll();
			panelData.add(chart);
			panelData.layout();

		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	@Override
	public void setHandler(StatsExerciseHistoryHandler handler) {
		this.handler = handler;
	}

	/**
	 * Adds exercise search combo
	 */
	protected ComboBox<ExerciseNameModel> addExerciseCombo() {

		final ComboBox<ExerciseNameModel> combo = new ComboBox<ExerciseNameModel>();
		//custom editors so we see also target correctly
		combo.setPropertyEditor(new ListModelPropertyEditor<ExerciseNameModel>() {
			@Override
			public ExerciseNameModel convertStringValue(String value) {
				return store.findModel("fn", value);
			}

			@Override
			public String getStringValue(ExerciseNameModel value) {
				return Functions.getExerciseName(value);
			}
		});
		//set fullname to "fn" so we see target correctly
    combo.getView().setModelProcessor(new ModelProcessor<ExerciseNameModel>() {
			@Override
			public ExerciseNameModel prepareData(ExerciseNameModel model) {
				model.set("fn", Functions.getExerciseName(model));
				return model;
			}
    });

		 // proxy, reader and loader
		RpcProxy<List<ExerciseNameModel>> proxy = new RpcProxy<List<ExerciseNameModel>>() {
      @Override
      protected void load(Object loadConfig, AsyncCallback<List<ExerciseNameModel>> callback) {
        BasePagingLoadConfig config = (BasePagingLoadConfig)loadConfig;
       		
        handler.query(config.get("query").toString(), callback);
      }
    };
       
    ModelReader reader = new ModelReader();
    BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);

    loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>() {
      public void handleEvent(LoadEvent be) {
        be.<ModelData>getConfig().set("start", be.<ModelData>getConfig().get("offset"));
      }
    });
        
    store = new ListStore<ExerciseNameModel>(loader);  

    combo.addListener(Events.OnMouseOver, CustomListener.fieldMouseOver);
    combo.addListener(Events.OnMouseOut, CustomListener.fieldMouseOut);
    combo.setWidth(300);   
    combo.setForceSelection(true);
    combo.setMessageTarget("none");
    combo.setDisplayField("fn");
    combo.setTemplate(XTemplate.create(Templates.getExerciseNameTemplate()) );  
    combo.setStore(store);
    combo.setEmptyText(AppController.Lang.EnterKeywordToSearchForExercises());
    combo.setHideTrigger(true);
    combo.setTriggerAction(TriggerAction.ALL); 
    combo.addListener(Events.Valid, new Listener<FieldEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(FieldEvent be) {
				try {
					ComboBox<ExerciseNameModel> cb = ((ComboBox<ExerciseNameModel>)be.getSource());
					
					//if selected something from the list
					if(cb.getSelection().size() > 0) {
						ExerciseNameModel mo = cb.getSelection().get(0);
						
						handler.selected(mo);
						
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}				
			}				    	
    });
	    
    return combo;
	}

}
