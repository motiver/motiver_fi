/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.statistics;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.statistics.StatsTopMealsPresenter;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.MealModel;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.ToolTip;
import com.extjs.gxt.charts.client.model.ToolTip.MouseStyle;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.HorizontalBarChart;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class StatsTopMealsView extends StatsTopMealsPresenter.StatsTopMealsDisplay {

	LayoutContainer panelData = new LayoutContainer();
	
	public StatsTopMealsView() {
		this.setLayout(new RowLayout());
		
		//title
		Text textTitle = new Text(AppController.Lang.Top10Meals());
		textTitle.setStyleName("label-title-big");
		this.add(textTitle, new RowData(-1, -1, new Margins(0, 0, 20, 0)));
		
		panelData.setHeight(400);
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
	public void setMealsData(List<MealModel> meals) {

		try {
			getBodyContainer().removeAll();
			getBodyContainer().layout();
			
			if(meals == null) {
				return;
      }
			
			//show bar graph
			final Chart chart = new Chart(Constants.URL_APP_STATIC+"resources/chart/open-flash-chart.swf");
			int height = 100;
			if(meals.size() > 0) {
				height = meals.size() * 40;
      }
			chart.setHeight(height); 
			panelData.setHeight(height);
			chart.setWidth(750);

			HorizontalBarChart bchart = new HorizontalBarChart(); 
			bchart.setTooltip("#val#");

			//add values
			List<String> list = new ArrayList<String>();
			int maxCount = 0;
			int i = 0;
			for(MealModel m : meals) {
				try {
					list.add(0, "    " + m.getName());
					 
					int count = Integer.parseInt(m.get("count").toString());
					if(count > maxCount) {
						maxCount = count;
          }
					
					bchart.addBars(new HorizontalBarChart.Bar(count, Constants.COLOR_GRAPH[ (i < Constants.COLOR_GRAPH.length)? i : i - Constants.COLOR_GRAPH.length]));
					
					i++;
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
			
			ChartModel cm = new ChartModel();  
			cm.setBackgroundColour("-1");  

			//y-axis
      YAxis ya = new YAxis();
			ya.setOffset(true);
			ya.addLabels(list);   
			cm.setYAxis(ya); 

			//x-axis
			XAxis xa = new XAxis();  
			xa.setSteps(maxCount / 10);
			double max = (double)maxCount * 1.3;
			max -= max % 10;
			cm.setXAxis(xa);
	 	    
			chart.setChartModel(cm);
			cm.addChartConfig(bchart); 
			cm.setTooltipStyle(new ToolTip(MouseStyle.NORMAL));  

			getBodyContainer().add(chart);
			getBodyContainer().layout();

		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
