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
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.Permission;
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
  public static WorkoutModel getWorkoutModel(PersistenceManager pm, Long workoutId, String userUid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading workout: "+workoutId);
    }
    
    WorkoutModel model = null;
    
    //load from cache
    WeekCache cache = new WeekCache();
    Workout w = cache.getWorkout(workoutId);

    //not found
    if(w == null) {    
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Not found from cache");
      }
      
      w = pm.getObjectById(Workout.class, workoutId);
    }
    
    if(w != null) {
      
      //check permission
      if(!MyServiceImpl.hasPermission(pm, Permission.READ_TRAINING, userUid, w.getUid())) {
        throw new NoPermissionException(Permission.READ_TRAINING, userUid, w.getUid());
      }
      
      //get exercises
      List<ExerciseModel> listEClient = new ArrayList<ExerciseModel>();
      List<Exercise> listE = w.getExercises();
      if(listE != null) {
        
        for(Exercise exercise : listE) {
          ExerciseModel eNew = Exercise.getClientModel(exercise);
          
          //get exercisename
          if(exercise.getNameId() > 0) {
            eNew.setName(getExerciseNameModel(pm, exercise.getNameId()));
          }
          
          //set correct workout id
          eNew.setWorkoutId(w.getId());
          eNew.setUid(w.getUid());
          
          listEClient.add(eNew);
        }
        
      }
        
      //convert to client side model
      model = Workout.getClientModel(w);
      model.setUid(userUid);
      model.setExercises(listEClient);
      
      //save to cache
      cache.addWorkout(w);
    }
    else {
      throw new Exception("Workout not found");
    }
    
    
    return model;
  }

  /**
   * Returns exercise name
   * @param pm
   * @param nameId
   * @throws Exception 
   */
  private static ExerciseNameModel getExerciseNameModel(PersistenceManager pm, Long nameId) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading exercise name: "+nameId);
    }
    
    ExerciseNameModel model = null;

    //load from cache
    WeekCache cache = new WeekCache();
    ExerciseName n = cache.getExerciseName(nameId);
    
    if(n == null) {
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Not found from cache");
      }
      
      n = pm.getObjectById(ExerciseName.class, nameId);
    }
    
    if(n != null) {
      //convert to client side model
      model = ExerciseName.getClientModel(n);
      
      //save to memcache
      cache.addExerciseName(n);
    }
    else {
      throw new Exception("Exercise name not found");
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
  public static WorkoutModel updateWorkoutModel(PersistenceManager pm, WorkoutModel model, String userUid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating workout: "+model.getId());
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
          throw e;
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
  
  /**
   * Removes single workout
   * @param pm
   * @param workout
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public static Boolean removeWorkoutModel(PersistenceManager pm, WorkoutModel workout, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing exercise: "+workout.getId());
    }
    
    boolean ok = false;
    
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {

        Workout w = pm.getObjectById(Workout.class, workout.getId());
          
        if(w != null) {
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, uid, w.getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, uid, w.getUid());
          }
          
          //remove exercise
          pm.deletePersistent(w);
          tx.commit();
          
          ok = true;
          
          //clear workout from cache
          WeekCache cache = new WeekCache();
          cache.removeWorkoutModel(workout.getId());
        }
        else {
          logger.log(Level.WARNING, "Could not find workout with id "+workout.getId());
        }
        
        break;
      }
      catch (Exception e) {
        logger.log(Level.WARNING, "Error deleting workout", e);
        
        //retries used
        if (retries == 0) {          
          throw e;
        }
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(Constants.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
    }
    
    return ok;
  }
  
  /**
   * Updates single exercise
   * @param pm
   * @param exercise
   * @param userUid
   * @param locale
   * @return updated exercise
   * @throws ConnectionException 
   */
  public static ExerciseModel updateExerciseModel(PersistenceManager pm, ExerciseModel exercise, String userUid, String locale) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating exercise: "+exercise.getId());
    }
    
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {
        
        Exercise e = getExercise(pm, exercise.getWorkoutId(), exercise.getId());
          
        if(e != null) {
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, userUid, e.getWorkout().getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, userUid, e.getWorkout().getUid());
          }
          
          //update exercise
          e.setSets(exercise.getSets());
          e.setReps(exercise.getReps());
          e.setTempo(exercise.getTempo());
          e.setRest(exercise.getRest());
          e.setWeights(exercise.getWeights());
          e.setInfo(exercise.getInfo());
          
          //update name
          if(exercise.getName() != null && exercise.getName().getId() != e.getId().longValue()) {
            ExerciseNameModel n = addExerciseNameModel(pm, exercise.getName(), userUid, locale);
            e.setNameId(n.getId());
          }
          
          tx.commit();
          
          //clear workout from cache
          WeekCache cache = new WeekCache();
          cache.removeWorkoutModel(exercise.getWorkoutId());
        }
        else {
          logger.log(Level.WARNING, "Could not find exercise with id "+exercise.getId());
        }
        
        break;
      }
      catch (Exception e) {
        logger.log(Level.WARNING, "Error updating exercise", e);
        
        //retries used
        if (retries == 0) {          
          throw e;
        }
        
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(Constants.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
    }
    
    return exercise;
  }
  
  /**
   * Adds single name
   * @param pm
   * @param model
   * @param uid
   * @param locale
   * @return added name
   * @throws Exception 
   */
  @SuppressWarnings("unchecked")
  public static ExerciseNameModel addExerciseNameModel(PersistenceManager pm, ExerciseNameModel model, String uid, String locale) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Adding exercise name: '"+model.getName()+" "+model.getTarget()+"'");
    }
    
    //check if similar found
    Query q = pm.newQuery(ExerciseName.class);
    q.setFilter("name == nameParam && target == targetParam");
    q.declareParameters("java.lang.String nameParam, java.lang.Integer targetParam");
    q.setRange(0, 1);
    List<ExerciseName> arr = (List<ExerciseName>) q.execute(model.getName(), model.getTarget());

    long nameId = 0;
    
    if(arr.size() > 0) {
      nameId = arr.get(0).getId();
    }
    //not found
    else {
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Name '"+model.getName()+" "+model.getTarget()+"' not found. Creating a new one");
      }

      ExerciseName mServer = ExerciseName.getServerModel(model);
      mServer.setLocale(locale);
      mServer.setUid(uid);
      pm.makePersistent(mServer);
      
      nameId = mServer.getId();      
    }
    
    //get added name
    model = getExerciseNameModel(pm, nameId);
    
    return model;    
  }
  
  /**
   * Removes single exercise
   * @param pm
   * @param exercise
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public static Boolean removeExerciseModel(PersistenceManager pm, ExerciseModel exercise, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing exercise: "+exercise.getId());
    }
    
    boolean ok = false;
    
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {
        
        Exercise e = getExercise(pm, exercise.getWorkoutId(), exercise.getId());
          
        if(e != null) {
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, uid, e.getWorkout().getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, uid, e.getWorkout().getUid());
          }
          
          //remove exercise
          pm.deletePersistent(e);
          tx.commit();
          
          ok = true;
          
          //clear workout from cache
          WeekCache cache = new WeekCache();
          cache.removeWorkoutModel(exercise.getWorkoutId());
        }
        else {
          logger.log(Level.WARNING, "Could not find exercise with id "+exercise.getId());
        }
        
        break;
      }
      catch (Exception e) {
        logger.log(Level.WARNING, "Error deleting exercise", e);
        
        //retries used
        if (retries == 0) {          
          throw e;
        }
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(Constants.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
    }
    
    return ok;
  }
  
  
  // PRIVATE METHODS
  
  
  /**
   * Returns single exercise from workout
   */
  private static Exercise getExercise(PersistenceManager pm, Long workoutId, Long exerciseId) {

    Exercise e = null;
    
    //get workout
    Workout w = pm.getObjectById(Workout.class, workoutId);
    
    if(w != null) {

      //update
      final List<Exercise> list = w.getExercises();
      
      //search exercise
      int i = 0;
      for(final Exercise ee : list) {
        if(ee.getId().longValue() == exerciseId) {
          e = ee;
          break;
        }
        i++;
      } 
    }
    
    return e;
  }
}
