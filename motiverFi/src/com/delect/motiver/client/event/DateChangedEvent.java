/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.DateChangedEventHandler;

public class DateChangedEvent extends GwtEvent<DateChangedEventHandler> {
  public static Type<DateChangedEventHandler> TYPE = new Type<DateChangedEventHandler>();

  private Date date;
  private Date dateEnd;
  private int random = 0;

  /**
  * Date change event (single date)
  * @param date : new date
  */
  public DateChangedEvent(Date date) {
    this.date = date;
  }
  /**
  * Date change event (two dates)
  * @param dateStart : start date
  * @param dateEnd : end date
  */
  public DateChangedEvent(Date dateStart, Date dateEnd) {
    date = dateStart;
    this.dateEnd = dateEnd;
  }
	  
  @Override
  public Type<DateChangedEventHandler> getAssociatedType() {
    return TYPE;
  }
  /**
  * Get date associated with this event
  * @return date
  */
  public Date getDate() {
		return date;  
  }
	  
  /**
  * Get date associated with this event (end date)
  * @return date
  */
  public Date getDateEnd() {
		return dateEnd;  
  }
  public int getRandom() {
    return random;
  }
	  
  public void setRandom(int random) {
    this.random = random;
  }

  @Override
  protected void dispatch(DateChangedEventHandler handler) {
    handler.onDateChanged(this);
  }
}


