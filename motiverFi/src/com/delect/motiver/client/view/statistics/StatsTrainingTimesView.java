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

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.statistics.StatsTrainingTimesPresenter;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class StatsTrainingTimesView extends StatsTrainingTimesPresenter.StatsTrainingTimesDisplay {

	LayoutContainer panelData = new LayoutContainer();
	
	public StatsTrainingTimesView() {
		this.setLayout(new RowLayout());
		
		//title
		Text textTitle = new Text(AppController.Lang.TrainingTimes());
		textTitle.setStyleName("label-title-big");
		this.add(textTitle, new RowData(-1, -1, new Margins(0, 0, 20, 0)));
		
		panelData.setHeight(200);
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
	public void setTimesData(int[] count) {

		try {
			getBodyContainer().removeAll();
			getBodyContainer().layout();
			
			if(count == null) {
				return;
      }
			
			//show pie graph in panelTimes
			final Chart chart = new Chart(Constants.URL_APP_STATIC+"resources/chart/open-flash-chart.swf");  
			chart.setHeight(200); 
			chart.setWidth(400);
			
			ChartModel cm = new ChartModel(AppController.Lang.Times(),  "font-size: 14px; font-family: Verdana; text-align: center;");  
			cm.setBackgroundColour("-1");  
			Legend lg = new Legend(Position.RIGHT, true);  
			lg.setPadding(10);  
			cm.setLegend(lg);  
			  
			PieChart pie = new PieChart();  
			pie.setAlpha(0.5f);  
			pie.setNoLabels(true);  
			pie.setTooltip("#label#<br>#val#");  
			pie.setColours(Constants.COLOR_GRAPH);  
			
			String[] times = new String[] { "00:00 - 03:00", "03:00 - 06:00", "06:00 - 09:00", "09:00 - 12:00", "12:00 - 15:00", "15:00 - 18:00", "18:00 - 21:00", "21:00 - 00:00" };
			//add values
			for(int i=0; i < count.length; i++) {
				if(count[i] > 0) {
          pie.addSlices(new PieChart.Slice(count[i], times[i], times[i]));
        }
      }
  
			cm.addChartConfig(pie);  	    
			chart.setChartModel(cm);
			
			getBodyContainer().add(chart);
			getBodyContainer().layout();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
