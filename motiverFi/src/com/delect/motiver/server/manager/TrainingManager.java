package com.delect.motiver.server.manager;

import java.util.logging.Logger;

import com.delect.motiver.server.cache.TrainingCache;
import com.delect.motiver.server.dao.TrainingDAO;

public class TrainingManager {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(TrainingManager.class.getName());

  UserManager userManager = UserManager.getInstance();
  TrainingCache cache = TrainingCache.getInstance();
  TrainingDAO dao = TrainingDAO.getInstance();
  
  private static TrainingManager man; 

  public static TrainingManager getInstance() {
    if(man == null) {
      man = new TrainingManager();
    }
    return man;
  }
  
  
}
