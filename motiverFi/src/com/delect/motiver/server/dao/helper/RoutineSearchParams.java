package com.delect.motiver.server.dao.helper;

import java.util.Date;

import com.delect.motiver.shared.Constants;

public class RoutineSearchParams {
  
  public String uid;
  public int offset = 0;
  public int limit = Constants.LIMIT_ROUTINES;
  public int minCopyCount = 0;
  public Date date = null;
  public Order order = Order.NAME;
  
  public enum Order {
    NAME,
    DATE,
    COUNT
  }
  public String toString() {
    return "WorkoutSearchParam: [" +
        ", uid: " +uid +
        ", offset: " +offset +
        ", limit: " +limit +
        ", minCopyCount: " +minCopyCount +
        ", date: " +date +
    		"]";
  }
  private enum Target { ALL };
  
  private static RoutineSearchParams all = null;
  
  public RoutineSearchParams() {
    
  }
  
  public RoutineSearchParams(Target target) {
    if(target == Target.ALL) {
      limit = 10000;
    }
  }
  
  public RoutineSearchParams(Date date, String uid) {
    this.date = date;
    this.uid = uid;
    limit = 5;
  }

  public static RoutineSearchParams all() {
    if(all == null) {
      all = new RoutineSearchParams(Target.ALL);
    }
    
    return all;
  }
  
}
