package com.delect.motiver.server.dao.helper;

import java.util.Date;

import com.delect.motiver.shared.Constants;

public class MealSearchParams {
  
  public Integer timeId;
  public String uid;
  public int offset = 0;
  public int limit = Constants.LIMIT_MEALS;
  public int minCopyCount = 0;
  public Order order = Order.NAME;
  
  public enum Order {
    NAME,
    DATE,
    COUNT
  }
  public String toString() {
    return "WorkoutSearchParam: [" +
        ", timeId: " +timeId +
        ", uid: " +uid +
        ", offset: " +offset +
        ", limit: " +limit +
        ", minCopyCount: " +minCopyCount +
    		"]";
  }
  private enum Target { ALL };
  
  private static MealSearchParams all = null;
  
  public MealSearchParams() {
    
  }
  
  public MealSearchParams(Target target) {
    if(target == Target.ALL) {
      limit = 10000;
    }
  }
  
  public MealSearchParams(Date date, String uid) {
    this.uid = uid;
    limit = 5;
  }

  public static MealSearchParams all() {
    if(all == null) {
      all = new MealSearchParams(Target.ALL);
    }
    
    return all;
  }
  
}
