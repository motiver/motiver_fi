package com.delect.motiver.server.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;

public final class DateUtils {

  /**
   * Strip time from date
   * @param date
   * @param start or end date (time: 00:00:01 or 23:59:59)
   * @return date
   */
  public static Date stripTime(Date date, boolean isStart) {

    GregorianCalendar gc = new GregorianCalendar(); 
    gc.setTime(date); 
    int year = gc.get(Calendar.YEAR); 
    int month = gc.get(Calendar.MONTH); 
    int day = gc.get(Calendar.DATE); 
    int h = 0;
    int m = 0;
    int s = 0;
    if(!isStart) {
      h = 23;
      m = 59;
      s = 59;
    }
    GregorianCalendar output = new GregorianCalendar(year, month, day, h, m, s); 
    return output.getTime(); 
  }
  
}
