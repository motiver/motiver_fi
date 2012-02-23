package com.delect.motiver.server.dao.helper;

import com.delect.motiver.shared.Permission;

public class CircleSearchParams {
  
  public int target = Permission.READ_TRAINING;
  public String ourUid;
  public String uid;
  public int limit = 100;
  
  public String toString() {
    return "CircleSearchParams: [" +
        ", target: " +target +
        ", ourUid: " +ourUid +
        ", uid: " +uid+
    		"]";
  }
  
  public CircleSearchParams() {
    
  }
  
  public CircleSearchParams(int target, String ourUid, String uid) {
    this.target = target;
    this.ourUid = ourUid;
    this.uid = uid;
  }  
}
