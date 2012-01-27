package com.delect.motiver.server.manager.helpers;

import java.util.Comparator;

public class NameCountWrapper {

  public long id;
  public int countQuery;
  public int countUse;
  
  public static CountComparator COUNT_COMPARATOR = new CountComparator();

  public NameCountWrapper(long id, int countQuery, int countUse) {
    this.id = id;
    this.countQuery = countQuery;
    this.countUse = countUse;
  }
  
  public static class CountComparator implements Comparator<NameCountWrapper> {

    @Override
    public int compare(NameCountWrapper name1, NameCountWrapper name2) {
      
      int count = name1.countQuery;
      int count2 = name2.countQuery;
      
      //if equal count -> compare also use count
      if(count == count2) {
        return name2.countUse - name1.countUse;
      }
      else {
        return count2 - count;
      }
    }
    
  }
}
