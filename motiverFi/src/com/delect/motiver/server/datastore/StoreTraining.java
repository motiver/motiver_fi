/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
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

import com.delect.motiver.server.Exercise;
import com.delect.motiver.server.ExerciseName;
import com.delect.motiver.server.ExerciseNameCount;
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
      System.out.println("Loading workout: "+workoutId);
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
  @SuppressWarnings("unchecked")
  public static ExerciseNameModel getExerciseNameModel(PersistenceManager pm, long nameId) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading exercise name: "+nameId);
      System.out.println("Loading exercise name: "+nameId);
    }
    
    ExerciseNameModel model = null;

    //load from cache
    WeekCache cache = new WeekCache();
    List<ExerciseName> names = cache.getExerciseNames();

    if(names == null) {
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Not found from cache");
      }
      
      Query q = pm.newQuery(ExerciseName.class);
      names = (List<ExerciseName>) q.execute();
      
      //save to memcache
      cache.addExerciseNames(names);
    }
    
    if(names != null) {
      for(ExerciseName name : names) {
        if(name.getId() != null && name.getId().longValue() == nameId) {
          //convert to client side model
          model = ExerciseName.getClientModel(name);
          break;
        }
      }
    }
    else {
      throw new Exception("Exercise names not found");
    }
    
    return model;
  }

  /**
   * Returns all exercises name
   * @param pm
   * @param locale
   * @throws Exception
   * @return list 
   */
  @SuppressWarnings("unchecked")
  public static List<ExerciseName> getExerciseNames(PersistenceManager pm) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading all exercise names: ");
      System.out.println("Loading all exercise names: ");
    }

    //load from cache
    WeekCache cache = new WeekCache();
    List<ExerciseName> n = cache.getExerciseNames();
    
    if(n == null) {
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Not found from cache");
      }

      Query q = pm.newQuery(ExerciseName.class);
      n = (List<ExerciseName>) q.execute();
      
      //save to cache
      cache.addExerciseNames(n);
    }
    
    if(n == null) {
      throw new Exception("Exercise names not found");
    }
    
    return n;
  }

  /**
   * Updates single workout. If workout is not found, new workout is created
   * @param pm
   * @param workout
   * @param uid : who is asking
   * @return added / updated workotu
   */
  @SuppressWarnings("deprecation")
  public static WorkoutModel updateWorkoutModel(PersistenceManager pm, WorkoutModel model, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating workout: "+model.getId());
    }
      
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {
        
        Workout w = pm.getObjectById(Workout.class, model.getId());

        if(w != null) {
          
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, uid, w.getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, uid, w.getUid());
          }
          
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
          tx.commit();

          //save to cache
          WeekCache cache = new WeekCache();
          cache.removeWorkout(w.getId());
          
          //get client side model
          model = getWorkoutModel(pm, w.getId(), uid);
          
          break;
        }
        else {
          throw new Exception("Workout not found");
        }
      }
      catch (Exception e) {
        if (tx.isActive()) {
          tx.rollback();
        }
        if(e instanceof NoPermissionException) {         
          throw e;
        }
        logger.log(Level.SEVERE, "Error updating workout", e);
        
        //retries used
        if (retries == 0) {
          throw e;
        }
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(MyServiceImpl.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
      finally {
        if (tx.isActive()) {
          tx.rollback();
        }
      }
    }
      
    
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
  public static Boolean removeWorkoutModel(PersistenceManager pm, Long workoutId, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing workout: "+workoutId);
      System.out.println("Removing workout: "+workoutId);
    }
    
    boolean ok = false;
      
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {

        Workout w = pm.getObjectById(Workout.class, workoutId);
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
          cache.removeWorkout(workoutId);
      
          break;
        }
        else {
          logger.log(Level.WARNING, "Could not find workout");
        }
      }
      catch (Exception e) {
        if (tx.isActive()) {
          tx.rollback();
        }
        if(e instanceof NoPermissionException) {         
          throw e;
        }
        logger.log(Level.WARNING, "Error deleting workout", e);
        
        //retries used
        if (retries == 0) {          
          throw e;
        }
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(MyServiceImpl.DELAY_BETWEEN_RETRIES);
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
    
    //get name
    ExerciseNameModel n = null;
    if(exercise.getName() != null) {
      n = getExerciseNameModel(pm, exercise.getName().getId());
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
          
          //update name
          if(n != null) {
            e.setNameId(n.getId());
            exercise.setName(n);
          }
          
          //update exercise
          e.setSets(exercise.getSets());
          e.setReps(exercise.getReps());
          e.setTempo(exercise.getTempo());
          e.setRest(exercise.getRest());
          e.setWeights(exercise.getWeights());
          e.setInfo(exercise.getInfo());
          
          tx.commit();
          
          //update workout to cache
          Workout w = e.getWorkout();
          WeekCache cache = new WeekCache();
          cache.addWorkout(w);
        
          break;
        }
        else {
          logger.log(Level.WARNING, "Could not find exercise with id "+exercise.getId());
        }
      }
      catch (Exception ex) {
        if (tx.isActive()) {
          tx.rollback();
        }
        if(ex instanceof NoPermissionException) {         
          throw ex;
        }
        logger.log(Level.WARNING, "Error updating exercise", ex);
        
        //retries used
        if (retries == 0) {          
          throw ex;
        }
        
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(MyServiceImpl.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ignored) { }
      }
    }

    return exercise;
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
  public static Boolean updateExerciseOrder(PersistenceManager pm, Long workoutId, Long[] ids, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating exercise order:");
    }
    
    boolean ok = false;
      
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {
        Workout w = pm.getObjectById(Workout.class, workoutId);
        if(w != null) {
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, uid, w.getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, uid, w.getUid());
          }
                      
          //get exercises
          for(int i=0; i < ids.length; i++) {
            Exercise e = null;
            for(Exercise ex : w.getExercises()) {
              if(ex.getId().longValue() == ids[i].longValue()) {
                e = ex;
                break;
              }
            }
            if(e != null) {
              e.setOrder(i);
            }
            else {
              ok = false;
            }
          }
          
          tx.commit();
          
          //update workout to cache
          WeekCache cache = new WeekCache();
          cache.addWorkout(w);
          
          break;
        }
        else {
          logger.log(Level.WARNING, "Could not find workout with id "+workoutId);
        }
      }
      catch (Exception ex) {
        if (tx.isActive()) {
          tx.rollback();
        }
        if(ex instanceof NoPermissionException) {         
          throw ex;
        }
        logger.log(Level.WARNING, "Error updating exercise order", ex);
        
        //retries used
        if (retries == 0) {          
          throw ex;
        }
        
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(MyServiceImpl.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ignored) { }
      }
    }

    return ok;
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
      System.out.println("Adding exercise name: '"+model.getName()+" "+model.getTarget()+"'");
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

      //add to cache
      WeekCache cache = new WeekCache();
      List<ExerciseName> names = cache.getExerciseNames();
      if(names != null) {
        names.add(mServer);
        cache.addExerciseNames(names);
      }
      
      nameId = mServer.getId();      
    }
    
    //get added name
    model = getExerciseNameModel(pm, nameId);
    
    return model;    
  }
  
  /**
   * Adds single exercise
   * @param pm
   * @param model
   * @param uid
   * @param locale
   * @return added name
   * @throws Exception 
   */
  public static ExerciseModel addExerciseModel(PersistenceManager pm, ExerciseModel model, String uid, String locale) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Adding exercise: '"+model.getId()+"'");
      System.out.println("Adding exercise: '"+model.getId()+"'");
    }

    Exercise modelServer = Exercise.getServerModel(model);
    ExerciseNameModel name = model.getName();
    
    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();
      
      try {
        //if new name
        if(model.getName() != null && model.getName().getId() == 0) {
          name = addExerciseNameModel(pm, model.getName(), uid, locale);
          tx.commit();
          tx.begin();
          
          if(name != null) {
            modelServer.setNameId(name.getId());
          }
        }
        
        //get workout
        Workout w = pm.getObjectById(Workout.class, model.getWorkoutId());
        
        if(w != null) {
          
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, uid, w.getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, uid, w.getUid());
          }
          
          //if no exercises
          if(w.getExercises() == null) {
            List<Exercise> list = new ArrayList<Exercise>();
            w.setExercises(list);
          }
          
          //add exercise
          w.getExercises().add(modelServer);
          
          pm.makePersistent(w);
          tx.commit();
          
          //get client side model
          //TODO needs improving
          Exercise eNew = w.getExercises().get(w.getExercises().size() - 1);
          model = Exercise.getClientModel(eNew);
          model.setName(name);
          model.setWorkoutId(w.getId());
          model.setUid(uid);

          //clear workout from cache
          WeekCache cache = new WeekCache();
          cache.removeWorkout(w.getId());
          
          break;
          
        }
        
      }
      catch (Exception ex) {
        if (tx.isActive()) {
          tx.rollback();
        }
        if(ex instanceof NoPermissionException) {         
          throw ex;
        }
        logger.log(Level.WARNING, "Error updating exercise", ex);
        
        //retries used
        if (retries == 0) {          
          throw ex;
        }
        
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(MyServiceImpl.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ignored) { }
      }
    }
    
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
        
        //get workout
        Workout w = pm.getObjectById(Workout.class, exercise.getWorkoutId());
        
        if(w != null) {
          //check permission
          if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_TRAINING, uid, w.getUid())) {
            throw new NoPermissionException(Permission.WRITE_TRAINING, uid, w.getUid());
          }

          //update
          final List<Exercise> list = w.getExercises();
          
          //search exercise
          int i = 0;
          for(final Exercise ee : list) {
            if(ee.getId().longValue() == exercise.getId()) { 
              list.remove(i);
              w.setExercises(list);
              break;
            }
            i++;
          } 
          
          //update workout
          pm.makePersistent(w);
          tx.commit();
          
          ok = true;

          //save to cache
          WeekCache cache = new WeekCache();
          cache.addWorkout(w);
        }
        else {
          logger.log(Level.WARNING, "Could not find workout with id "+w.getId());
        }
        
        break;
      }
      catch (Exception e) {
        if (tx.isActive()) {
          tx.rollback();
        }
        if(e instanceof NoPermissionException) {         
          throw e;
        }
        logger.log(Level.WARNING, "Error deleting exercise", e);
        
        //retries used
        if (retries == 0) {          
          throw e;
        }
        logger.log(Level.WARNING, " Retries left: "+retries);
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(MyServiceImpl.DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
    }
    
    return ok;
  }

  /**
   * Returns count value for single exercise name
   * @param pm
   * @param id
   * @param uid
   * @return
   */
  public static int getExerciseNameCount(PersistenceManager pm, Long id, String uid) {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading count for exercise name: "+id+", uid="+uid);
    }
    
    int count = 0;
    
    //load from cache
    WeekCache cache = new WeekCache();
    count = cache.getExerciseNameCount(id, uid);

    //not found
    if(count == -1) {    
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Not found from cache");
      }
      
      Query qUse = pm.newQuery(ExerciseNameCount.class);
      qUse.setFilter("nameId == nameIdParam && openId == openIdParam");
      qUse.declareParameters("java.lang.Long nameIdParam, java.lang.String openIdParam");
      qUse.setRange(0, 1);
      List<ExerciseNameCount> valueCount = (List<ExerciseNameCount>) qUse.execute(id, uid);
      if(valueCount.size() > 0) {
        count = valueCount.get(0).getCount();
      }

      //save to cache
      if(count >= 0) {
        cache.addExerciseNameCount(id, uid, count);
      }
      else {
        count = 0;
      }
      
    }
    
    return count;
    
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
