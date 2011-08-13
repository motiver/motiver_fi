/**
 * 
 */
package com.delect.motiver.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.delect.motiver.client.service.TrainingService;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.Routine;
import com.delect.motiver.server.Workout;
import com.delect.motiver.server.datastore.StoreTraining;
import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

/**
 * @author Antti
 *
 */
public class TrainingServiceImpl extends NutritionServiceImpl implements TrainingService {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1930822872413338185L;

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(TrainingServiceImpl.class.getName()); 
  


  /**
   * Returns single workout
   * @param workoutId
   * @return
   * @throws ConnectionException 
   */
  @Override public WorkoutModel getWorkout(Long workoutId) throws ConnectionException {

    logger.log(Level.FINE, "Loading single workout ("+workoutId+")");
    
    WorkoutModel m = null;
    
    //get uid
    final String UID = AllServiceImpl.getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      m = StoreTraining.getWorkout(pm, workoutId, UID);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getWorkout", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getWorkout", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Returns all workouts that aren't in calendar
   * @param routine : if null returns all workouts
   * @return workouts' models (if routine set -> also exercises are returned)
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<WorkoutModel> getWorkouts(int index, RoutineModel routine) throws ConnectionException {

    logger.log(Level.FINE, "Loading workouts. Index="+index);

    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    
    //get uid
    final String UID = AllServiceImpl.getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query q = pm.newQuery(Workout.class);

      List<Workout> workouts = null;
      //if from single routine
      if(routine != null) {
        
        //get routine so we know is it shared
        Routine r = pm.getObjectById(Routine.class, routine.getId());
        if(r == null) {
          throw new Exception();
        }
        
        //check permission if not our routine
        if(!r.getUid().equals(UID)) {
          boolean hasPermission = AllServiceImpl.hasPermission(0, UID, r.getUid());
          
          //if no permission for the routine -> return empty list
          if(!hasPermission) {
            throw new Exception();
          }
        }

        q.setFilter("date == null && routineId == routineIdParam");
        q.declareParameters("java.lang.Long routineIdParam");
        workouts = (List<Workout>) q.execute(r.getId());
        
      }
      //all single workouts
      else {
        q.setFilter("date == null && routineId == 0 && openId == openIdParam");
        q.declareParameters("java.lang.String openIdParam");
        q.setRange(index, 100);
        workouts = (List<Workout>) q.execute(UID);
      }

      Collections.sort(workouts);
      
      int i = 0;
      for(Workout w : workouts) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_WORKOUTS) {
          list.add(null);
          break;
        }

        WorkoutModel m = StoreTraining.getWorkout(pm, w.getId(), UID);
          
        list.add(m);
        
        i++;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getWorkouts", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getWorkouts", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Get workouts in calendar between dates
   * @param uid : who's workouts
   * @param dateStart
   * @param dateEnd
   * @return workoutmodels in each days ( model[days][day's workouts] )
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<WorkoutModel[]> getWorkoutsInCalendar(String uid, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getWorkoutsInCalendar()");

    List<WorkoutModel[]> list = new ArrayList<WorkoutModel[]>();
    
    //get uid
    final String UID = AllServiceImpl.getUid();
    if(UID == null) {
      return null;
    }
    
    //check dates
    if(dateStart.getTime() > dateEnd.getTime()) {
      return null;
    }

    //check permission
    if(!AllServiceImpl.hasPermission(0, UID, uid)) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //go through days
      final int days = (int)((dateEnd.getTime() - dateStart.getTime()) / (24 * 60 * 60 * 1000)) + 1;
      
      for(int i=0; i < days; i++) {
        
        final Date d = new Date((dateStart.getTime() / 1000 + 3600 * 24 * i) * 1000);
        //strip time
        final Date dStart = AllServiceImpl.stripTime(d, true);
        final Date dEnd = AllServiceImpl.stripTime(d, false);
        
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
        List<Workout> workouts = (List<Workout>) q.execute(uid, dStart, dEnd);

        //convert to client side models
        WorkoutModel[] arr = new WorkoutModel[workouts.size()];
        int c = 0;
        for(Workout w : workouts) {
          WorkoutModel m = StoreTraining.getWorkout(pm, w.getId(), UID);

          arr[c] = m;
          c++;
        }
        list.add(arr);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getWorkoutsInCalendar", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getWorkoutsInCalendar", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return list;
  }


  /**
   * Checks if current date has training (used for determining guide value)
   * @param date
   * @return
   * @throws ConnectionException 
   */
  public boolean hasTraining(String uid, Date date) throws ConnectionException {

    logger.log(Level.FINE, "Checking if date '"+date+"' has training");
  
    boolean hasTraining = getWorkoutsInCalendar(uid, date, date).get(0).length > 0;
      
    //TODO check also if day contains cardio!
    
    
    return hasTraining;
  }
  
}
