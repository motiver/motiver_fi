/**
 * 
 */
package com.delect.motiver.server.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import net.sf.jsr107cache.Cache;

import com.delect.motiver.server.Exercise;
import com.delect.motiver.server.ExerciseName;
import com.delect.motiver.server.cache.WeekCache;
import com.delect.motiver.server.service.MyServiceImpl;
import com.delect.motiver.server.Workout;
import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.delect.motiver.shared.WorkoutModel;

/**
 * @author Antti
 *
 */
public class StoreTraining {


  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(StoreTraining.class.getName()); 


  /**
   * Returns single workout from datastore or from cache
   * @param pm
   * @param workoutId
   * @param userUid : user who made request
   * @return
   */
  @SuppressWarnings("unchecked")
  public static WorkoutModel getWorkout(PersistenceManager pm, Long workoutId, String userUid) throws Exception {

    logger.log(Level.FINEST, "Fetching workout "+workoutId);
    
    WorkoutModel model = null;

    Cache cache = WeekCache.get();
    
    //check if found on memcache
    StringBuilder builder = new StringBuilder();
    builder.append("w");
    builder.append(workoutId);
    Object obj = cache.get(builder.toString());
    
    //found
    if(obj != null && obj instanceof WorkoutModel) {
      model = (WorkoutModel)obj;
      
      //check permission
      if(!MyServiceImpl.hasPermission(0, userUid, model.getUid())) {
        throw new NoPermissionException(0, userUid, model.getUid());
      }
      
      logger.log(Level.FINEST, "Found workout from MemCache. Returning it");      
    }
    
    //not found
    else {
      logger.log(Level.FINEST, "Fetching workout ("+workoutId+") from datastore");
      
      //fetch (+exercises)
      final Workout w = pm.getObjectById(Workout.class, workoutId);
      
      if(w != null) {
        
        //check permission
        if(!MyServiceImpl.hasPermission(0, userUid, w.getUid())) {
          throw new NoPermissionException(0, userUid, w.getUid());
        }
        
        //convert to client side model
        model = Workout.getClientModel(w);
        
        //get exercises
        List<ExerciseModel> listEClient = new ArrayList<ExerciseModel>();
        List<Exercise> listE = w.getExercises();
        if(listE != null) {
          //fetch names first
          List<Long> arrNameId = new ArrayList<Long>();
          for(Exercise e : listE) {
            if(e.getNameId() != 0 && !arrNameId.contains(e.getNameId())) {
              arrNameId.add(e.getNameId());
            }
          }

          List<ExerciseName> exercises = null;
          if(arrNameId.size() > 0) {
            Query query = pm.newQuery(ExerciseName.class);
            query.setFilter("idParam.contains(id)");
            query.declareParameters("java.lang.Long idParam");
            exercises = (List<ExerciseName>) query.execute(arrNameId);
          }

          //go through each exercise
          for(Exercise exercise : listE) {
            ExerciseModel eNew = Exercise.getClientModel(exercise);
            
            //set correct workout id
            eNew.setWorkoutId(w.getId());
            eNew.setUid(w.getUid());
            
            //get name from array
            if(exercise.getNameId() != 0 && exercises != null) {
              for(ExerciseName en : exercises) {
                if(en.getId().longValue() == exercise.getNameId().longValue()) {
                  eNew.setName(ExerciseName.getClientModel(en));
                  break;
                }
              }
            }
            listEClient.add(eNew);
          }
          
        }
        model.setExercises(listEClient);
      }
      else {
        throw new Exception("Workout not found");
      }
      
      //save to memcache
      if(logger.isLoggable(Level.FINEST)) {
        logger.log(Level.FINEST, "Saving workout ("+workoutId+") to memcache");
      }
      cache.put(builder.toString(), model);
      
    }
    
    return model;
  }

  /**
   * Updates single workout. If workout is not found, new workout is created
   * @param pm
   * @param workout
   * @return added / updated workotu
   */
  @SuppressWarnings("deprecation")
  public static WorkoutModel updateWorkout(PersistenceManager pm, WorkoutModel model, String uid) throws Exception {

    if(logger.isLoggable(Level.FINEST)) {
      logger.log(Level.FINEST, "Updating workout "+model.getName());
    }
    
    Workout w = null;

    w = pm.getObjectById(Workout.class, model.getId());

    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {
        
        //reset time from date
        Date d = model.getDate();
        if(d != null) {
          d.setHours(0);
          d.setMinutes(0);
          d.setSeconds(0);
        }
        
        //update workout
        w.setDate(d);
        w.setDone(model.getDone());
        w.setName(model.getName());
        w.setRating(model.getRating());
        w.setRoutineId( model.getRoutineId() );
        w.setTimeEnd((long) model.getTimeEnd());
        w.setTimeStart((long) model.getTimeStart());
        
        pm.makePersistent(w);
        
        break;
      }
      catch (Exception e) {
        logger.log(Level.SEVERE, "Error updating workout", e);
        
        //retries used
        if (retries == 0) {
          if (!pm.isClosed()) {
            pm.close();
          } 
          
          throw new ConnectionException("updateWorkout", e.getMessage());
        }
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(Constants.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
      finally {
        if (tx.isActive()) {
          tx.rollback();
        }
      }
    }

    //get client side model
    model = Workout.getClientModel(w);
    
    return model;
  }
}
