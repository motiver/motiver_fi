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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.DateWeekSelectorPresenter;
import com.delect.motiver.client.presenter.DateWeekSelectorPresenter.DateWeekSelectorHandler;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * 
 * Date selector panel with X days
 */
public class DateWeekSelectorView extends DateWeekSelectorPresenter.DateWeekSelectorDisplay {

  /**
   * How many days are shown in week calendar.
   */
  public static final int TOTAL_DAY_COUNT = 21;
  
	private Date dateSelected = new Date();
	
	//date variables
	private Date dateStart = new Date();
	private Date dateStartLast = null;
	
	private DateWeekSelectorHandler handler;
	private boolean isSliding = false;
	
	//mouse slide variables
	private int lastX = 0;
	private int lastY = 0;
	private boolean[] markers;
	
	private HorizontalPanel panelDays = new HorizontalPanel();
	private Popup popup; 
	private boolean slideOn = false;
	private boolean slidePrev = false;
	final DateTimeFormat fmt2 = DateTimeFormat.getFormat("d.M.YYYY");

	//date formats
	final DateTimeFormat fmtDate = DateTimeFormat.getFormat("d");
	final DateTimeFormat fmtDay = DateTimeFormat.getFormat("E");
	
	final Text linkSelectDate = new Text();
	long timeAni = 0;
	final String todayStr = fmt2.format(new Date());

	public DateWeekSelectorView() {
		
		this.setLayout(new RowLayout());
		this.setStyleName("panel-calendarweek");
		
		panelDays.setWidth("100%");
		panelDays.setSpacing(5);
		this.add(panelDays, new RowData(-1, -1));
		
		//links
		LayoutContainer panelLinks = new LayoutContainer();
		panelLinks.setLayout(new RowLayout(Orientation.HORIZONTAL));
		panelLinks.setHeight(18);
		
		//today
		Text linkToday = new Text(AppController.Lang.Today());
		linkToday.setStyleName("link");
		linkToday.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				dateSelected = new Date();
				dateStartLast = dateStart;
				
				if(dateSelected.getTime() == 0) {
					return;
        }

				//call handler
				handler.dateSelected(dateStart, dateSelected);
			}
		});
		panelLinks.add(linkToday, new RowData(-1, -1, new Margins(0, 10, 0, 0)));
		
		//date selection
		linkSelectDate.setStyleName("link");
		linkSelectDate.setText(AppController.Lang.SelectDate());
		linkSelectDate.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(popup != null && popup.isVisible()) {
          popup.hide();
				}

				popup = new Popup();
				final DatePicker picker = new DatePicker(); 
				picker.setValue(dateSelected);
        picker.addListener(Events.Select, new Listener<ComponentEvent>() {  
          public void handleEvent(ComponentEvent be) {
						dateSelected = picker.getValue();
						dateStartLast = dateStart;
						
						if(dateSelected.getTime() == 0) {
							return;
	          }

						if(popup != null && popup.isVisible()) {
              popup.hide();
	          }

						//call handler
						handler.dateSelected(dateStart, dateSelected);
          }
        });
        popup.add(picker);
        popup.show(linkSelectDate);
			}
		});
		panelLinks.add(linkSelectDate);
		
		this.add(panelLinks, new RowData(-1, -1, new Margins(5, 0, 0, 5)));
		
	}
	
	@Override
	public Widget asWidget() {
		
		//disable text selection
		this.setStyleAttribute("-webkit-user-select", "none");
		
		//set slide listeners (change dates when sliding left/right)
		this.addListener(Events.OnMouseDown, new Listener<DomEvent>() {
			@Override
			public void handleEvent(DomEvent be) {
				lastX = be.getClientX();
				lastY = be.getClientY();
				slideOn = true;
			}
			
		});
		this.addListener(Events.OnMouseUp, new Listener<DomEvent>() {
			@Override
			public void handleEvent(DomEvent be) {
				slideOn = false;
			}
			
		});
		this.addListener(Events.OnMouseMove, new Listener<DomEvent>() {
			@Override
			public void handleEvent(DomEvent be) {
				
				if(!slideOn) {
					return;
				}
				
				final int x = be.getClientX();
				final int y = be.getClientY();
				
				if(lastX > 0 && lastY > 0 && x > 0 && y > 0) {
					
					//slide to right
					if(x - lastX > StringConstants.SLIDE_MIN_X) {
						slideOn  = false;
						showPrevDates();
					}
					//slide to left
					else if(lastX - x > StringConstants.SLIDE_MIN_X) {
						slideOn = false;
						showNextDates();
					}
				}
				
			}
			
		});
		
		initDaysPanel();
		
		return this;
	}

	@Override
	public void onStop() {
		if(popup != null && popup.isVisible()) {
      popup.hide();
    }
	}

	@Override
	public void refreshView() {
		
		slidePrev  = (dateStart.getTime() < dateStartLast.getTime());

		//scroll animation
		if(this.isRendered()) {
			
			//init panel 21 times
			final DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
			if( !fmt.format(dateStartLast).equals(fmt.format(dateStart))) {
				isSliding = true;
				animatePanel(dateStartLast, dateStart);
			}
			else {
				initDaysPanel();
			}
			
		}
		else {
			initDaysPanel();
		}
	}

	@Override
	public void setDate(Date dateStart) {
		this.dateStart = dateStart;
		if(dateStartLast == null) {
			dateStartLast = this.dateStart;
    }
	}

	@Override
	public void setDateSelected(Date date) {
		dateSelected = date;
	}

	@Override
	public void setHandler(DateWeekSelectorHandler handler) {
		this.handler = handler;
	}

	/**
	 * Sets markers (if day has data) to each day
	 * @param markers
	 */
	@Override
	public void setMarkers(boolean[] markers) {
		this.markers = markers;
		
		if(!isSliding) {
			initDaysPanel();
    }
	}

	/**
	 * Inits days panel until we are in correct day
	 * @param dateStartLast2
	 */
	private void animatePanel(final Date d, final Date dateEnd) {
		
		Timer timer = new Timer() {
			@Override
			public void run() {
				//scroll speed based on datediff
				int speed = 1;
				int diff = Math.abs(CalendarUtil.getDaysBetween(d, dateEnd));
				if(diff <= 14) {
					speed = 1;
        }
				else if(diff <= 21) {
					speed = 2;
        }
				else if(diff <= 35) {
					speed = 5;
        }
				else if(diff <= 70) {
					speed = 10;
        }
				else {
					speed = 25;
        }
				
				if(slidePrev) {
					dateStart = new Date( (d.getTime() / 1000 - 3600 * 24 * speed) * 1000 );
        }
				else {
					dateStart = new Date( (d.getTime() / 1000 + 3600 * 24 * speed) * 1000 );
        }
				initDaysPanel();
				
				//if more days
				final DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd");
				if( !fmt.format(dateStart).equals(fmt.format(dateEnd))) {
					animatePanel(dateStart, dateEnd);
        }
				else {
					isSliding = false;
					initDaysPanel();	//refresh one more time, so event handler are set
				}
				
			}
		};
		long days = 0;
		if(slidePrev) {
			days = (d.getTime() - dateEnd.getTime()) / (1000 * 3600 * 24);
    }
		else {
			days = (dateEnd.getTime() - d.getTime()) / (1000 * 3600 * 24);
    }
		int del = 15;
		if(days > 0 && days < 6) {
			del += (6 - days) * 35;
    }
		else if(days > 16 && days < 22) {
			del += (days - 16) * 35;
    }
		
		//run timer
		timer.schedule(del);
	}

	/**
	 * Inits days panel
	 */
	private void initDaysPanel() {
		timeAni = System.currentTimeMillis();
		
		panelDays.clear();

		//set last start date
		dateStartLast = dateStart;
				
		try {
			//find prev monday
			long mon = dateStart.getTime() / 1000;

			final String selStr = fmt2.format(dateSelected);
			
			//left arrow
			HTML lLeft = new HTML("<div><</div>");
			lLeft.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showPrevDates();
				}
			});
			lLeft.setStyleName("panel-calendarweek-day-left");
			panelDays.add(lLeft);
			panelDays.setCellWidth(lLeft, "2.5%");
			Date d = new Date(mon * 1000);
			
			//load cells
			for(int i=0; i < TOTAL_DAY_COUNT; i++) {
				
				final long sec = d.getTime() / 1000;
				
				String dStr = fmtDate.format(d);
				if(dStr.length() == 1) {
					dStr = "&nbsp;" + dStr;
        }
				//day of week
				dStr += "<br>" + fmtDay.format(d);
				dStr = "<div>" + dStr + "</div>";
				
				//add day
				HTML l = new HTML(dStr);
				l.setTitle(AppController.Lang.ShowDate() + ": " + Functions.getDateString(d, true, false));
				
				//styles
				//if selected
				if(fmt2.format(d).equals(selStr)) {
					l.setStyleName("panel-calendarweek-day-sel");
				}
				//if workouts (only when not sliding)
				else if(!isSliding && markers != null && markers.length > i && markers[i]) {
					l.setStyleName("panel-calendarweek-day-workout");
        }
				else {
					l.setStyleName("panel-calendarweek-day");
        }
				
				//add these only when not in "animation"
				if(!isSliding) {
					l.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {

							dateSelected = new Date(sec * 1000);
							dateStartLast = dateStart;
							
							if(dateSelected.getTime() == 0) {
								return;
		          }

							handler.dateSelected(dateStart, dateSelected);
						}
					});
				}

				panelDays.add(l);
	      panelDays.setCellWidth(l, "4.5%");

				//add day
				CalendarUtil.addDaysToDate(d, 1);
			}
			
			//right arrow
			HTML lRight = new HTML("<div>></div>");
			lRight.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showNextDates();
				}
			});
			lRight.setStyleName("panel-calendarweek-day-right");
			panelDays.add(lRight);
      panelDays.setCellWidth(lRight, "2.5%");
			
		} catch (Exception e) {
			Info.display("error", e.getMessage());
		}
	}

	/**
	 * Shows next three weeks
	 */
	protected void showNextDates() {
		
		if(!isSliding) {

			dateStartLast = dateStart;
			
			//change both dates			
			dateStart = new Date(dateStart.getTime() + 1000 * 3600 * 24 * 21);
			dateSelected = new Date(dateSelected.getTime() + 1000 * 3600 * 24 * 21);
			
			handler.dateSelected(dateStart, dateSelected);
		}
	}

	/**
	 * Shows previous three weeks
	 */
	protected void showPrevDates() {
		
		if(!isSliding) {

			dateStartLast = dateStart;
			
			//change both dates
			dateStart = new Date(dateStart.getTime() - 1000 * 3600 * 24 * 21);
			dateSelected = new Date(dateSelected.getTime() - 1000 * 3600 * 24 * 21);
			
			handler.dateSelected(dateStart, dateSelected);
		}
	}

  @Override
  public int getTotalDays() {
    return TOTAL_DAY_COUNT;
  }

}
