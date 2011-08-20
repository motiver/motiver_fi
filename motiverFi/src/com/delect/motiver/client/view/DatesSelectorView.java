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
package com.delect.motiver.client.view;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.DatesSelectorPresenter;
import com.delect.motiver.client.presenter.DatesSelectorPresenter.DatesSelectorHandler;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Date selector panel with X days
 */
public class DatesSelectorView extends DatesSelectorPresenter.DatesSelectorDisplay {

	
	private Date dateEnd;
	private Date dateStart;

	private DatesSelectorHandler handler;
	private Text labelDate1 = new Text();
	private Text labelDate2 = new Text();
	private Text linkLast6Month = new Text();
	private Text linkLastMonth = new Text();
	
	private Text linkLastWeek = new Text();
	private Text linkLastYear = new Text();
	private Text linkThisWeek = new Text();
	private int maxDiffInDays = 0;

	private Popup popup;
	
	public DatesSelectorView() {
		
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
    this.setWidth(650);
    this.setHeight(20);
		
    //link styles
    labelDate1.setStyleName("link");
    labelDate2.setStyleName("link");
    linkThisWeek.setStyleName("link");
    linkLastWeek.setStyleName("link");
    linkLastMonth.setStyleName("link");
    linkLast6Month.setStyleName("link");
    linkLastYear.setStyleName("link");
		
	}
	
	@Override
	public Widget asWidget() {
		
		this.add(new Label(AppController.Lang.Dates() + ":&nbsp;"), new HBoxLayoutData(new Margins(0, 0, 0, 0)));
		
		//labels (opens datepicker)
		LayoutContainer lcDates = new LayoutContainer();
		TableLayout tl = new TableLayout(3);
    lcDates.setLayout(tl);
    lcDates.setWidth(150);
		lcDates.add(labelDate1);
		lcDates.add(new Label("&nbsp;-&nbsp;"));
		lcDates.add(labelDate2);
		this.add(lcDates, new HBoxLayoutData(new Margins(0, 0, 0, 0)));
		
		//eventhandler for labels
		labelDate1.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//popup with datepicker
				if(popup != null && popup.isVisible()) {
          popup.hide();
        }
				
				popup = new Popup();
				final DatePicker picker = new DatePicker(); 
				picker.setValue(dateStart);
        picker.addListener(Events.Select, new Listener<ComponentEvent>() {  
          public void handleEvent(ComponentEvent be) {  
            datesChanged(picker.getValue(), dateEnd);
            popup.hide();
          }
        });
        //set maxdate
        picker.setMaxDate(dateEnd);
        popup.add(picker);
        popup.show(labelDate1);
			}
		});
		labelDate2.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//popup with datepicker
				final Popup popup = new Popup();
				final DatePicker picker = new DatePicker(); 
				picker.setValue(dateEnd);  
        picker.addListener(Events.Select, new Listener<ComponentEvent>() {  
          public void handleEvent(ComponentEvent be) {
            datesChanged(dateStart, picker.getValue());
            popup.hide();
          }
        });
        picker.setMinDate(dateStart);
        popup.add(picker);
        popup.show(labelDate2);
			}
		});
		
		//links
		linkThisWeek.setText(AppController.Lang.ThisWeek());
		linkLastWeek.setText(AppController.Lang.LastWeek());
		linkLastMonth.setText(AppController.Lang.LastMonth());
		linkLast6Month.setText(AppController.Lang.LastSixMonths());
		linkLastYear.setText(AppController.Lang.LastYear());
		this.add(linkThisWeek, new HBoxLayoutData(new Margins(0, 0, 0, 15)));
		this.add(linkLastWeek, new HBoxLayoutData(new Margins(0, 0, 0, 15)));
		//add only if max day difference over 30
		if(maxDiffInDays >= 30) {
			this.add(linkLastMonth, new HBoxLayoutData(new Margins(0, 0, 0, 15)));
    }
		//add only if max day difference over 180
		if(maxDiffInDays >= 180) {
			this.add(linkLast6Month, new HBoxLayoutData(new Margins(0, 0, 0, 15)));
    }
		//add only if max day difference over 365
		if(maxDiffInDays >= 365) {
			this.add(linkLastYear, new HBoxLayoutData(new Margins(0, 0, 0, 15)));
    }
		
		//labels' events
		linkThisWeek.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//this week
				Date d = new Date();
				Date d1 = new Date(Functions.findPreviousMonday(d.getTime() / 1000) * 1000 );
				Date d2 = new Date((d1.getTime() / 1000 + 3600 * 24 * 6) * 1000);
				datesChanged(d1, d2);
			}
		});
		linkLastWeek.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//last week
				Date d1 = new Date();
				d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 7) * 1000 );
				Date d2 = new Date();
				datesChanged(d1, d2);
			}
		});
		linkLastMonth.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//last month
				Date d1 = new Date();
				d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 31) * 1000 );
				Date d2 = new Date();
				datesChanged(d1, d2);
			}
		});
		linkLast6Month.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//last 6 months
				Date d1 = new Date();
				d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 31 * 6) * 1000 );
				Date d2 = new Date();
				datesChanged(d1, d2);
			}
		});
		linkLastYear.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//last year
				Date d1 = new Date();
				d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 365) * 1000 );
				Date d2 = new Date();
				datesChanged(d1, d2);
			}
		});
		
		return this;
	}

	@Override
	public void onStop() {
		if(popup != null && popup.isVisible()) {
      popup.hide();
    }
	}

	@Override
	public void setDates(Date dateStart, Date dateEnd) {
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		
		//update labels
		labelDate1.setText(Functions.getDateString(dateStart, false, false));
		labelDate2.setText(Functions.getDateString(dateEnd, false, false));
		
		checkSelected();
	}

	@Override
	public void setHandler(DatesSelectorHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMaxDiffInDays(int maxDiffInDays) {
		this.maxDiffInDays  = maxDiffInDays;
	}

	/**
	 * Checks which link is selected
	 * @return
	 */
	private void checkSelected() {
		
		Date d1 = new Date();
		Date d2 = new Date();
		
		//clear all links
		linkThisWeek.removeStyleName("link-sel");
		linkLastWeek.removeStyleName("link-sel");
		linkLastMonth.removeStyleName("link-sel");
		linkLast6Month.removeStyleName("link-sel");
		linkLastYear.removeStyleName("link-sel");
		
		//this week
		d1 = new Date();
		d1 = new Date(Functions.findPreviousMonday(d1.getTime() / 1000) * 1000 );
		d2 = new Date((d1.getTime() / 1000 + 3600 * 24 * 6) * 1000);
		final DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
		if(fmt.format(d1).equals(fmt.format(dateStart)) && fmt.format(d2).equals(fmt.format(dateEnd))) {
			linkThisWeek.addStyleName("link-sel");
			return;
		}

		//last week
		d1 = new Date();
		d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 7) * 1000 );
		d2 = new Date();
		if(fmt.format(d1).equals(fmt.format(dateStart)) && fmt.format(d2).equals(fmt.format(dateEnd))) {
			linkLastWeek.addStyleName("link-sel");
			return;
		}
			
		//last month
		d1 = new Date();
		d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 31) * 1000 );
		d2 = new Date();
		if(fmt.format(d1).equals(fmt.format(dateStart)) && fmt.format(d2).equals(fmt.format(dateEnd))) {
			linkLastMonth.addStyleName("link-sel");
			return;
		}

		//last 6 months
		d1 = new Date();
		d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 31 * 6) * 1000 );
		d2 = new Date();
		if(fmt.format(d1).equals(fmt.format(dateStart)) && fmt.format(d2).equals(fmt.format(dateEnd))) {
			linkLast6Month.addStyleName("link-sel");
			return;
		}

		//last year
		d1 = new Date();
		d1.setTime( (d1.getTime() / 1000 - 3600 * 24 * 365) * 1000 );
		d2 = new Date();
		if(fmt.format(d1).equals(fmt.format(dateStart)) && fmt.format(d2).equals(fmt.format(dateEnd))) {
			linkLastYear.addStyleName("link-sel");
			return;
		}
		
		this.layout(true);
		
	}

	/**
	 * Called when dates changed
	 * @param d1
	 * @param d2
	 */
	@SuppressWarnings("deprecation")
	protected void datesChanged(Date d1, Date d2) {
		
		try {
			//reset hours, ...
			d1.setHours(0);
			d1.setMinutes(0);
			d1.setSeconds(0);
			d2.setHours(0);
			d2.setMinutes(0);
			d2.setSeconds(0);
			
			//only if changed
			if(d1.getTime() != dateStart.getTime() || d2.getTime() != dateEnd.getTime()) {
				
				boolean dateStartChanged = d1.getTime() != dateStart.getTime();
				
				dateStart = d1;
				dateEnd = d2;
				
				long diffDays = Math.abs(CalendarUtil.getDaysBetween(dateStart, dateEnd));
				
				//check if correct dates
				if(diffDays > maxDiffInDays) {
					if(dateStartChanged) {
						//set end date
						dateEnd = new Date( (dateStart.getTime() / 1000 + 3600 * 24 * maxDiffInDays) * 1000 );
					}
					else {
						//check start date
						dateStart = new Date( (dateEnd.getTime() / 1000 - 3600 * 24 * maxDiffInDays) * 1000 );
					}
				}

				//update labels
				labelDate1.setText(Functions.getDateString(dateStart, false, false));
				labelDate2.setText(Functions.getDateString(dateEnd, false, false));
				
				if(handler != null) {
					handler.datesChanged(dateStart, dateEnd);
				}
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
}
