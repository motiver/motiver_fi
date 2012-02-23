package com.delect.motiver.server.dao.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.delect.motiver.shared.Constants;

public class WorkoutSearchParams {

  private static SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
  
  public Long routineId;
  public Integer routineDay;
  public String uid;
  public int offset = 0;
  public int limit = Constants.LIMIT_WORKOUTS;
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
        ", routineId: " +routineId +
        ", uid: " +uid +
        ", offset: " +offset +
        ", limit: " +limit +
        ", minCopyCount: " +minCopyCount +
        ", date: " +date +
    		"]";
  }
  private enum Target { ALL };
  
  private static WorkoutSearchParams all = null;
  
  public WorkoutSearchParams() {
    
  }
  
  public WorkoutSearchParams(Target target) {
    if(target == Target.ALL) {
      limit = 10000;
    }
  }
  
  public WorkoutSearchParams(Date date, String uid) {
    this.date = date;
    this.uid = uid;
    limit = 5;
  }
  
  public WorkoutSearchParams(Long routineId) {
    this.routineId = routineId;
  }
  
  public WorkoutSearchParams(Long routineId, Integer routineDay) {
    this.routineId = routineId;
    this.routineDay = routineDay;
  }

  public static WorkoutSearchParams all() {
    if(all == null) {
      all = new WorkoutSearchParams(Target.ALL);
    }
    
    return all;
  }

  public void getCacheKey(StringBuilder builder) {
    builder.append(uid);
    builder.append("_");
    builder.append(fmt.format(date));
    builder.append("_");
    builder.append(routineId);
    builder.append("_");
    builder.append(routineDay);
    builder.append("_");
    builder.append(offset);
    builder.append("_");
    builder.append(limit);
    builder.append("_");
    builder.append(minCopyCount);
  }
  
}
