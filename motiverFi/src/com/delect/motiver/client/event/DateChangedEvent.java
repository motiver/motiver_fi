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


