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

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.statistics.MonthlyReportListPresenter;
import com.delect.motiver.client.presenter.statistics.MonthlyReportListPresenter.MonthlyReportListHandler;
import com.delect.motiver.shared.MonthlySummaryModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class MonthlyReportListView extends MonthlyReportListPresenter.MonthlyReportListDisplay {
	
  LayoutContainer panelDates = new LayoutContainer();
  LayoutContainer panelBody = new LayoutContainer();
  private MonthlyReportListHandler handler;
  
	public MonthlyReportListView() {
	  panelDates.setLayout(new FlowLayout()); 
	  panelDates.setWidth(750);
	  this.add(panelDates, new RowData(-1, -1, new Margins(0,0,20,0)));
	  
	  this.add(panelBody);
	}
	
	@Override
	public Widget asWidget() {
	  return this;
	}

  @SuppressWarnings("deprecation")
  @Override
  public void setMonthlySummaries(List<MonthlySummaryModel> summaries) {
    panelDates.removeAll();
    
    int lastYear = 0;
    for(final MonthlySummaryModel summary : summaries) {
      
      final Date date = CommonUtils.getDateGmt(summary.getDate());
      
      //new year
      if(date.getYear() != lastYear) {
        //new row
        if(lastYear != 0) {
          Text linebreak = new Text();
          linebreak.setHeight(1);
          panelDates.add(linebreak);
        }
        
        Html html = new Html("<b>"+(1900+date.getYear())+": </b>");
        html.setTagName("span");
        
        panelDates.add(html, new FlowData(new Margins(0,10,0,0)));
        
        lastYear = date.getYear();
      }
      
      //add link to month
      Text link = new Text(AppController.LangConstants.Month()[date.getMonth()]);
      link.setStyleName("link");
      link.setTagName("span");
      link.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.summarySelected(summary.getId());
        }
      });
      panelDates.add(link, new FlowData(new Margins(0,5,0,0)));
      
      System.out.println("Summary: "+summary.getDate().getYear()+" "+summary.getDate().getMonth());
    }
    
    panelDates.layout();
  }

  @Override
  public void setHandler(MonthlyReportListHandler handler) {
    this.handler = handler;
  }

  @Override
  public LayoutContainer getBodyContainer() {
    return panelBody;
  }
}
