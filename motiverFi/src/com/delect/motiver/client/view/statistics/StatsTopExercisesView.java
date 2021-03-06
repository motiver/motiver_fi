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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.statistics.StatsTopExercisesPresenter;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.util.CommonUtils;

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

public class StatsTopExercisesView extends StatsTopExercisesPresenter.StatsTopExercisesDisplay {

	LayoutContainer panelData = new LayoutContainer();
	
	public StatsTopExercisesView() {
		this.setLayout(new RowLayout());
		
		//title
		Text textTitle = new Text(AppController.Lang.Top10Exercises());
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
	public void setExercisesData(List<ExerciseNameModel> exercises) {

		try {
			getBodyContainer().removeAll();
			getBodyContainer().layout();
			
			if(exercises == null) {
				return;
      }
			
			//show bar graph
			final Chart chart = new Chart(Constants.URL_APP_STATIC+"resources/chart/open-flash-chart.swf");
			int height = 100;
			if(exercises.size() > 0) {
				height = exercises.size() * 40;
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
			for(ExerciseNameModel m : exercises) {
				try {
					list.add(0, "    " + CommonUtils.getExerciseName(m));
					 
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
	 	    
			cm.addChartConfig(bchart); 
			cm.setTooltipStyle(new ToolTip(MouseStyle.NORMAL));  

			chart.setChartModel(cm);
			getBodyContainer().add(chart);
			getBodyContainer().layout();

		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

}
