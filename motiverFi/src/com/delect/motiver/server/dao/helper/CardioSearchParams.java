package com.delect.motiver.server.dao.helper;

import com.delect.motiver.shared.Constants;

public class CardioSearchParams {
  
  public String uid;
  public int offset = 0;
  public int limit = Constants.LIMIT_CARDIOS;
  public Order order = Order.NAME;
  
  public enum Order {
    NAME,
    DATE
  }
  public String toString() {
    return "CardioSearchParams: [" +
        "uid: " +uid +
        ", offset: " +offset +
        ", limit: " +limit +
    		"]";
  }
  private enum Target { ALL };
  
  private static CardioSearchParams all = null;
  
  public CardioSearchParams() {
    
  }
  
  public CardioSearchParams(Target target) {
    if(target == Target.ALL) {
      limit = 10000;
    }
  }

  public static CardioSearchParams all() {
    if(all == null) {
      all = new CardioSearchParams(Target.ALL);
    }
    
    return all;
  }
  
}
