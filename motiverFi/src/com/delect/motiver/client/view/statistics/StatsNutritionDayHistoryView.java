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
package com.delect.motiver.client.view.statistics;

import java.util.List;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.statistics.StatsNutritionDayHistoryPresenter;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.NutritionDayModel;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.LineDataProvider;
import com.extjs.gxt.charts.client.model.Scale;
import com.extjs.gxt.charts.client.model.ScaleProvider;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.ChartConfig;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.charts.client.model.charts.LineChart.LineStyle;
import com.extjs.gxt.charts.client.model.charts.dots.Dot;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class StatsNutritionDayHistoryView extends StatsNutritionDayHistoryPresenter.StatsNutritionDayHistoryDisplay {

	LayoutContainer panelData = new LayoutContainer();
	ListStore<NutritionDayModel> store = new ListStore<NutritionDayModel>();
	
	public StatsNutritionDayHistoryView() {
		this.setLayout(new RowLayout());
		
		//title
		Text textTitle = new Text(AppController.Lang.DaysCalories());
		textTitle.setStyleName("label-title-big");
		this.add(textTitle, new RowData(-1, -1, new Margins(0, 0, 20, 0)));
		
		panelData.setHeight(500);
		this.add(panelData);
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}

	@Override
	public void setDaysData(List<NutritionDayModel> values) {

		try {
			getBodyContainer().removeAll();
			getBodyContainer().layout();
			
			if(values == null) {
				return;
      }

			//if only single value -> show as text
			if(values.size() == 1) {
				VerticalPanel panelSingle = new VerticalPanel();
				panelSingle.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				panelSingle.setSpacing(10);
				panelSingle.setWidth("100%");
				
				Text text = new Text(AppController.Lang.OnlyOneValueFound() + ":");
				text.setStyleName("label-title-small");
				panelSingle.add(text);
				
				//value
				NutritionDayModel model = values.get(0);
				Text textValue = new Text();
				textValue.setText(Functions.getDateString(model.getDate(), true, true) + ": " +
        "<b>" + AppController.Lang.Energy() + ":</b> " + (int)model.getEnergy() + "kcal | " +
        "<b>" + AppController.Lang.Protein() + ":</b> " + (int)model.getProtein() + "g | " +
        "<b>" + AppController.Lang.Carbohydrates() + ":</b> " + (int)model.getCarb() + "g | " +
        "<b>" + AppController.Lang.Fet() + ":</b> " + (int)model.getFet() + "g");
				panelSingle.add(textValue);
				
				getBodyContainer().add(panelSingle, new RowData(-1, -1, new Margins(100, 0, 100, 0)));
				getBodyContainer().layout();
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

				getBodyContainer().add(chart);
				getBodyContainer().layout();
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
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
        min = (int)(min * ((min > 0) ? 0.95 : 1.05));
        max = (int)(max * ((max > 0) ? 1.05 : 0.95)) + 1;
			      
        //round to closest five
        min -= min % 5;
        max += (5 - max % 5);

        double interval = 40;
        double diff = Math.abs(max - min);
        if(diff <= 600) {
          interval = 100;
        }
        else if(diff <= 1200) {
          interval = 200;
        }
        else {
          double i = diff / 5;
          long l = Math.round(i / 100);
          interval = l * 100;
        }
			      
        return new Scale(min, max, interval);
			}
			
		});  
		model.setScaleProviderRightAxis(new ScaleProvider() {
			@Override
			public Scale calcScale(double min, double max) {
				if (min == 0 && max == 0) {
          return new Scale(-1, 1, 1);
        }
        min = (int)(min * ((min > 0) ? 0.95 : 1.05));
        max = (int)(max * ((max > 0) ? 1.05 : 0.95)) + 1;
			      
        //round to closest five
        min -= min % 5;
        max += (5 - max % 5);
        double interval = 4;
        double diff = Math.abs(max - min);
        if(diff <= 60) {
          interval = 10;
        }
        else if(diff <= 120) {
          interval = 20;
        }
        else {
          interval = (int)(diff / 2.5);
        }
			      
        return new Scale(min, max, interval);
			}
			
		}); 
	    
		//protein
		LineChart lineP = new LineChart();
		lineP.setWidth(2);
		lineP.setLineStyle(new LineStyle(10, 10));
    lineP.setColour(Constants.COLOR_GRAPH[1]);  
    LineDataProvider lineProviderP = new LineDataProvider("p") {
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
            dot.setTooltip(AppController.Lang.Protein() + ": " + value.intValue() + "g (" + Functions.getDateString(((NutritionDayModel)m).getDate(), true, true) + ")");
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
    lineProviderP.bind(store);
    lineP.setDataProvider(lineProviderP);
    lineP.setRightAxis(true);
    model.addChartConfig(lineP);
	    
		//carbs
		LineChart lineC = new LineChart();
		lineC.setWidth(2);
		lineC.setLineStyle(new LineStyle(10, 10));
		lineC.setColour(Constants.COLOR_GRAPH[2]);
    LineDataProvider lineProviderC = new LineDataProvider("c") {
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
            dot.setTooltip(AppController.Lang.Carbohydrates() + ": " + value.intValue() + "g (" + Functions.getDateString(((NutritionDayModel)m).getDate(), true, true) + ")");
            chart.addDots(dot);
            //	    	        chart.addValues(n);
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
    lineProviderC.bind(store);
    lineC.setDataProvider(lineProviderC);
    lineC.setRightAxis(true);
    model.addChartConfig(lineC);
	    
		//fet
		LineChart lineF = new LineChart();
		lineF.setWidth(2);
		lineF.setLineStyle(new LineStyle(10, 10));
		lineF.setColour(Constants.COLOR_GRAPH[3]);
    LineDataProvider lineProviderF = new LineDataProvider("f") {
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
            dot.setTooltip(AppController.Lang.Fet() + ": " + value.intValue() + "g (" + Functions.getDateString(((NutritionDayModel)m).getDate(), true, true) + ")");
            chart.addDots(dot);
            //	    	        chart.addValues(n);
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
    lineProviderF.bind(store);
    lineF.setDataProvider(lineProviderF);
    lineF.setRightAxis(true);
    model.addChartConfig(lineF);
	    
		//energy
		LineChart line = new LineChart();
		line.setWidth(6);
		line.setColour(Constants.COLOR_GRAPH[0]);
    LineDataProvider lineProvider = new LineDataProvider("e") {
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
          Number n = getValue(m);
          if (n == null) {
            chart.addNullValue();
          } else {
            Dot dot = new Dot();
            dot.setValue(n);
            dot.setTooltip(AppController.Lang.Energy() + ": " + n.intValue() + "kcal (" + Functions.getDateString(((NutritionDayModel)m).getDate(), true, true) + ")");
            chart.addDots(dot);
            //	    	        chart.addValues(n);
            maxYValue = first ? n.doubleValue() : Math.max(maxYValue, n.doubleValue());
            minYValue = first ? n.doubleValue() : Math.min(minYValue, n.doubleValue());
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
					//don't show labels if too many values
					if(store.getModels().size() * 43 <= (getWidth() - 25)) {
						NutritionDayModel m = (NutritionDayModel)model;
						return Functions.getDateString(m.getDate(), false, true, true);
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
		    
    YAxis axis = new YAxis();
    model.setYAxisRight(axis);
	    
    return model;  
	 }

}
