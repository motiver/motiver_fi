/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter;

import java.util.Date;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.event.DateChangedEvent;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.Functions;

/**
 * Shows two date selectors
 * <dl>
 * <dt><b>Events:</b></dt>
 * 
 * <dd><b>onDateChanged</b> : DateEvent()<br>
 * <div>Fires after one of the dates changes</div>
 * <ul>
 * <li>dateStart : new date</li>
 * <li>dateEnd : new date</li>
 * </ul>
 * </dd>
 * 
 * </dl>
 *
 */
public class DatesSelectorPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class DatesSelectorDisplay extends Display {
		/**
		 * Sets dates
		 * @param dateStart Date
		 * @param dateEnd Date
		 */
		public abstract void setDates(Date dateStart, Date dateEnd);
		/**
		 * Sets handler
		 * @param handler DatesSelectorHandler
		 */
		public abstract void setHandler(DatesSelectorHandler handler);
		/**
		 * Sets max difference (in days) between to dates.
		 * @param maxDiffInDays int
		 */
		public abstract void setMaxDiffInDays(int maxDiffInDays);
	}
	/** Handler for this presenter
	 */
	public interface DatesSelectorHandler {
		/**
		 * Called when date changes
		 * @param dateStart Date
		 * @param dateEnd Date
		 */
		void datesChanged(Date dateStart, Date dateEnd);
	}
	private Date dateEnd = new Date();

	private Date dateStart = new Date();
	private DatesSelectorDisplay display = null;
	private int maxDiffInDays;

	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param maxDiffInDays : max number of day difference allowed
	 * @param defaultIndex : index of default dates (0=this week, 1=last week, 2=last month, 3=last 6 months, 4=last year)
	 */
	public DatesSelectorPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, DatesSelectorDisplay display, int maxDiffInDays, int defaultIndex) {
		super(rpcService, eventBus);
		this.display = display;
	    
		this.maxDiffInDays = maxDiffInDays;

		//set dates
    //this week
		if(defaultIndex == 0) {
			dateStart = new Date();
			dateStart = new Date(Functions.findPreviousMonday(dateStart.getTime() / 1000) * 1000 );
			dateEnd = new Date((dateStart.getTime() / 1000 + 3600 * 24 * 6) * 1000);
		}
    //last week
		else if(defaultIndex == 1) {
			dateStart = new Date();
			dateStart.setTime( (dateStart.getTime() / 1000 - 3600 * 24 * 7) * 1000 );
			dateEnd = new Date();
		}
		//last month
		else if(defaultIndex == 2) {
			dateStart = new Date();
			dateStart.setTime( (dateStart.getTime() / 1000 - 3600 * 24 * 31) * 1000 );
			dateEnd = new Date();
		}
		//last 6 months
		else if(defaultIndex == 3) {
			dateStart = new Date();
			dateStart.setTime( (dateStart.getTime() / 1000 - 3600 * 24 * 31 * 6) * 1000 );
			dateEnd = new Date();
		}
		//last year
		else if(defaultIndex == 4) {
			dateStart = new Date();
			dateStart.setTime( (dateStart.getTime() / 1000 - 3600 * 24 * 365) * 1000 );
			dateEnd = new Date();
		}
	}
		
	/**
	 * Get end date
	 * @return Date
	 */
	public Date getDateEnd() {
		return dateEnd;
	}

	/**
	 * Get start date
	 * @return Date
	 */
	public Date getDateStart() {
		return dateStart;
	}
	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {

		display.setMaxDiffInDays(maxDiffInDays);
		display.setDates(dateStart, dateEnd);
		display.setHandler(new DatesSelectorHandler() {
			@Override
			public void datesChanged(Date dateStart, Date dateEnd) {
				setDates(dateStart, dateEnd);
				
				if(eventBus != null) {
					final DateChangedEvent event = new DateChangedEvent(dateStart, dateEnd);
					fireEvent(event);
				}
			}
		});
	}

	/**
	 * Sets dates
	 * @param dateStart
	 * @param dateEnd
	 */
	public void setDates(Date dateStart, Date dateEnd) {
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		display.setDates(dateStart, dateEnd);
	}
	
}
