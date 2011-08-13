/**
 * 
 */
package com.delect.motiver.server.datastore;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.delect.motiver.server.service.TrainingServiceImpl;
import com.delect.motiver.shared.WorkoutModel;

/**
 * @author Antti
 *
 */
public class dbTraining {


  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(dbTraining.class.getName()); 


  /**
   * Returns single workout from datastore or from cache
   * @param pm
   * @param workoutId
   * @return
   */
  private static WorkoutModel dbGetWorkout(PersistenceManager pm, Long workoutId) {

    logger.log(Level.FINEST, "Fetching workout "+workoutId);
    
    WorkoutModel m = null;

    //check if found on memcache
    
    //found
    if(true) {
      logger.log(Level.FINEST, "Found workout from MemCache. Returning it");
    }
    //not found
    else {
      logger.log(Level.FINEST, "Fetching workout ("+workoutId+") from datastore");
      
      //fetch (+exercises)
      
      //convert to client side model
      
      //save to memcache
      logger.log(Level.FINEST, "Saving workout ("+workoutId+") to memcache");
    }
    
    return m;
  }
}
