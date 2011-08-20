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
package com.delect.motiver.client.view.blog;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.blog.BlogDayPresenter;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * Single day in blog
 * 2 columns:
 *  - user pic, date
 *  - data
 * @author Antti
 *
 */
public class BlogDayView extends BlogDayPresenter.BlogDayDisplay {

	private Date date;
	
	private LayoutContainer panelData = new LayoutContainer();
	
	private boolean showOnlyLinks = false;
	private Text textTitle = new Text();
	
	@Override
	public Widget asWidget() {

		//show simplier ui IF showing only links
		if(showOnlyLinks) {
			
			this.setLayout(new RowLayout());
			this.setStyleName("panel-blog-day");
			
			//left panel
			LayoutContainer panelDate = new LayoutContainer();
			panelDate.setHeight(28);
			panelDate.setLayout(new RowLayout(Orientation.HORIZONTAL));

			//icon
			Text textIcon = new Text();
			textIcon.setStyleName("icon-date");
			panelDate.add(textIcon);

			TimeZone tz = TimeZone.createTimeZone(0);
			textIcon.setText(DateTimeFormat.getFormat("EE").format(date, tz).toLowerCase());			
			//if today
			final Date today = new Date();
			if(date != null && Functions.Fmt.format(today).equals(Functions.Fmt.format(date, tz))) {
				textIcon.addStyleName("icon-date-today");
				textTitle.setStyleAttribute("color", "#ff6600");
			}
			
			//date
			textTitle.setText(Functions.getDateString(date, false, true));
			textTitle.setStyleName("label-blog-date");
			panelDate.add(textTitle, new RowData(-1, -1, new Margins(7, 0, 0, 10)));
			
			this.add(panelDate);
			
			panelData.setLayout(new RowLayout());
			this.add(panelData, new RowData(-1, -1, new Margins(10, 0, 0, 38)));
						
		}
		//if showing everything -> show day in "notepanel"
		else {

			this.addStyleName("panel-blog-day");
			
			//header
			final LayoutContainer panelHeader = new LayoutContainer();
			HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layout);
      panelHeader.setHeight(55);
      panelHeader.setAutoWidth(true);
      panelHeader.setLayoutOnChange(true);
      panelHeader.setStyleName("panel-blog-day-header");
      panelHeader.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
      panelHeader.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
      panelHeader.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					//if button clicked -> cancel event
					if( panelHeader.getData("btnClick") != null && ((Boolean)panelHeader.getData("btnClick")) ) {
						panelHeader.setData("btnClick", false);
						return;
					}
					if(panelData.isVisible()) {
						panelData.hide();
          }
					else {
						panelData.show();
          }
				}
      });
      //title
			textTitle.setText(Functions.getDateString(date, true, true));
      textTitle.setStyleName("label-title-big");
      panelHeader.add(textTitle, new HBoxLayoutData(new Margins(0, 0, 0, 20)));
	        
      this.add(panelHeader);

			panelData.setStyleName("panel-blog-day-data");
			this.add(panelData);
		
		}
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}

	@Override
	public void setDate(Date date) {
		
		this.date = date;
	}

	@Override
	public void showOnlyLinks(boolean showOnlyLinks) {
		this.showOnlyLinks  = showOnlyLinks; 
	}

}
