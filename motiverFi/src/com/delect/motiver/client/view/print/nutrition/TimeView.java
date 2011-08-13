/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.print.nutrition;

import java.util.Date;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.TimePresenter;
import com.delect.motiver.client.presenter.nutrition.TimePresenter.TimeHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.TimeModel;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class TimeView extends TimePresenter.TimeDisplay {

	private LayoutContainer panelData = new LayoutContainer();
	//panels
	private LayoutContainer panelHeader = new LayoutContainer();
	private LayoutContainer panelTotals = new LayoutContainer();

	private TimeModel time = null;
	
	
	/**
	 * Workout view
	 * @param showOnlyTitle : show title (TRUE, open workout when clicked) or show just workout (FALSE)
	 */
	public TimeView() {
		
		try {
			
			this.addStyleName("panel-time");
			this.setLayout(new RowLayout());
			
			//header
			HBoxLayout layoutHeader = new HBoxLayout();
      layoutHeader.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layoutHeader);
      panelHeader.setHeight(40);
      panelHeader.setAutoWidth(true);
      panelHeader.setStyleName("panel-time-header");
	        
      this.add(panelHeader);

      //content

			//totals panel
			HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelTotals.setLayout(layout);
      panelTotals.setHeight(30);
      panelData.add(panelTotals, new RowData(-1, -1, new Margins(10, 0, 0, 10)));
	        
			panelData.setLayout(new RowLayout());
			panelData.setStyleName("panel-time-data");
			panelData.setStyleAttribute("min-height", "150px");
			this.add(panelData);
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	@Override
	public Widget asWidget() {

		initTitlePanel();
		
		return this;
	}	

	@Override
	public LayoutContainer getBodyContainer() {		
		return panelData;
	}

	@Override
	public void setHandler(TimeHandler handler) {}
	
	@Override
	public void setModel(TimeModel time) {
		this.time = time;
		
		initTotals();
		
		//check if current time
		checkIfCurrentTime();
	}

	/**
	 * Checks if this time is current time
	 * <br>Adds / removes style 'panel-time-now'
	 */
	@SuppressWarnings("deprecation")
	private void checkIfCurrentTime() {
		Date d = new Date();
		//today
		if(Functions.Fmt.format(d).equals(Functions.Fmt.format(time.getDate()))) {
			long curr = d.getHours() * 3600 + d.getMinutes() * 60;

			//-10min ... +10min
			if(Math.abs(curr - time.getTime()) < 20 * 60) {
				this.addStyleName("panel-time-now");
      }
			else {
				this.removeStyleName("panel-time-now");
      }
		}
	}

	/**
	 * Inits panel which contains the title
	 */
	private void initTitlePanel() {

		try {

			//icon
			Image img = new Image(MyResources.INSTANCE.iconClock());
			panelHeader.add(img, new HBoxLayoutData(new Margins(0, 10, 0, 0)));

			//init time label
			Text textTitle = new Text(Functions.getTimeToString(time.getTime()));
			textTitle.setStyleName("label-title-big");
			panelHeader.add(textTitle);
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Inits totals panel based on model
	 */
	private void initTotals() {

		try {
			panelTotals.removeAll();
			panelTotals.add(new Text(AppController.Lang.TimesStats() + ":"), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			panelTotals.add(Functions.getTotalPanel(time.getEnergy(), time.getProtein(), time.getCarb(), time.getFet()));
			panelTotals.layout();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
}
