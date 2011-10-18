package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.cache.TrainingCache;
import com.delect.motiver.server.dao.TrainingDAO;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.exception.ConnectionException;

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

  /**
   * Adds single exercise
   * @param pm
   * @param exercise
   * @param uID
   * @return
   */
  public void addExercise(UserOpenid user, Exercise model, long workoutId) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding/updating exercise: "+model);
    }    
          
    try {

      Workout workout = _getWorkout(workoutId);
      
      if(workoutId != 0) {        
        userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), workout.getUid());
        
        //update if found, otherwise add
        int i = workout.getExercises().indexOf(model);
        Exercise f;
        if(i == -1) {
          f = model;
          workout.getExercises().add(model);
        }
        else {
          f = workout.getExercises().get(i);
          f.update(model, false);
        }
        dao.updateWorkout(workout);
        
        //return updated model
        model.update(f, true);
      }
      
      //clear cache
      if(workout != null) {
        //in date
        if(workout.getDate() != null) {
          cache.setWorkouts(workout.getUid(), workout.getDate(), null);  //clear day's cache
        }
        else {
          cache.addWorkout(workout);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise", e);
      throw new ConnectionException("Add exercise", e.getMessage());
    }
  }


  /**
   * Removes single exercise
   * @param pm
   * @param exercise
   * @param uID
   * @return
   */
  public boolean removeExercise(UserOpenid user, Exercise model, long workoutId) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding/updating exercise: "+model);
    }
    
    boolean ok = false;
          
    try {

      Workout workout = null;
      if(workoutId != 0) {
        workout = dao.getWorkout(workoutId);
      }
      
      if(workoutId != 0) {        
        userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), workout.getUid());

        workout.getExercises().remove(model);
        dao.updateWorkout(workout);
      }
      
      //clear cache
      if(workout != null && workout.getDate() != null) {
        cache.setWorkouts(workout.getUid(), workout.getDate(), null);  //clear day's cache
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise", e);
      throw new ConnectionException("Add exercise", e.getMessage());
    }
    
    return ok;
  }

  public List<Workout> getWorkouts(UserOpenid user, Date date, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading workouts ("+date+")");
    };

    if(date == null) {
      return null;
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), uid);
    
    //get from cache
    List<Workout> list;
    
    try {    
      //get from cache
      list = cache.getWorkouts(uid, date);
      
      if(list == null) {
        list = dao.getWorkouts(date, uid);

        //add to cache
        cache.setWorkouts(uid, date, list);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("Error loading workouts", e);
    }
  
    return list;
  }
  
  public List<Workout> getWorkouts(UserOpenid user, int offset, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading workouts ("+offset+")");
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), uid);
    
    List<Workout> list = new ArrayList<Workout>();
    
    try {
      List<Long> keys = dao.getWorkouts(offset, Constants.LIMIT_WORKOUTS, uid, 0, 0);
      
      for(Long key : keys) {

        
        Workout jdo = _getWorkout(key);
        
        if(jdo != null) {
          
          //check permission
          userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid());
          
          list.add(jdo);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("getWorkouts", e.getMessage());
    }
    
    return list;
  }
  
  public List<Workout> getMostPopularWorkouts(UserOpenid user, int offset, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading most popular workouts ("+offset+")");
    }
    
    List<Workout> list = new ArrayList<Workout>();
    
    try {
      int i = 0;
      
      //while enough found
      while(list.size() < Constants.LIMIT_WORKOUTS) {
        List<Long> keys = dao.getWorkouts(i, Constants.LIMIT_WORKOUTS * 2, null, 0, 1);
        
        for(Long key : keys) {
          
          if(list.size() == Constants.LIMIT_WORKOUTS) {
            break;
          }
            
          Workout jdo = _getWorkout(key);
          
          if(jdo != null) {
            
            //check permission
            if(userManager.hasPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid())) {
              if(i >= offset) {
                list.add(jdo);
              }
            }
            
          }
        }
        
        i += Constants.LIMIT_WORKOUTS * 2;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("getWorkouts", e.getMessage());
    }
    
    return list;
  }
  
  public List<Workout> getWorkouts(UserOpenid user, Routine routine, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading workouts from routine ("+routine+")");
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), uid);
    
    List<Workout> list = new ArrayList<Workout>();
    
    try {

      //TODO .!!!
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("getWorkouts", e.getMessage());
    }
    
    return list;
  }

  private Workout _getWorkout(Long key) throws Exception {
    
    Workout jdo = cache.getWorkout(key);
    
    if(jdo == null) {
      jdo = dao.getWorkout(key);
      jdo.setUser(userManager.getUser(jdo.getUid()));
     
      cache.addWorkout(jdo);
    }
    
    return jdo;
  }

  private Routine _getRoutine(Long key) throws Exception {
    
    Routine jdo = cache.getRoutine(key);
    
    if(jdo == null) {
      jdo = dao.getRoutine(key);
      jdo.setUser(userManager.getUser(jdo.getUid()));
     
      cache.addRoutine(jdo);
    }
    
    return jdo;
  }

  private void _updateWorkout(Workout workout) throws Exception {
    
    dao.updateWorkout(workout);
    
    cache.addWorkout(workout);
  }


  public boolean removeWorkouts(List<Workout> models, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Removing workouts");
    }
    
    if(models.size() == 0) {
      return false;
    }
    
    boolean ok = false;
    
    try {
      
      //TODO missing permission check!!
      
      //remove cache
      //assume that all workouts have same date
      cache.setWorkouts(uid, models.get(0).getDate(), null);

      Long[] keys = new Long[models.size()];
      for(int i = 0; i < models.size(); i++) {
        keys[i] = models.get(i).getId();
      }
      ok = dao.removeWorkouts(keys, uid);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing workouts", e);
      throw new ConnectionException("Error removing workouts", e);
    }
    
    return ok;
    
  }


  @SuppressWarnings("deprecation")
  public List<Workout> addWorkouts(UserOpenid user, List<Workout> models) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding workouts");
    }
    
    if(models.size() == 0) {
      return null;
    }

    List<Workout> modelsCopy = new ArrayList<Workout>();
    
    try {
      
      //get workouts
      for(Workout workout : models) {

        //reset workout from date
        Date d = workout.getDate();
        if(d != null) {
          d.setHours(0);
          d.setMinutes(0);
          d.setSeconds(0);
          workout.setDate(d);
        }
        
        Workout clone = null;
        
        //new
        if(workout.getId() == 0) {
          
          //add two exercises
          List<Exercise> exercises = new ArrayList<Exercise>();
          exercises.add(new Exercise());
          exercises.add(new Exercise());
          workout.setExercises(exercises);

          clone = workout;
        }
        else {
          //check cache
          Workout jdo = _getWorkout(workout.getId());
          
          //increment count
          jdo.setCount(jdo.getCount() + 1);
          _updateWorkout(jdo);
          
          //add copy
          clone = (Workout) jdo.clone();
        }

        clone.setUid(user.getUid());
        clone.setUser(user);
        clone.setDate(workout.getDate());
        modelsCopy.add(clone);
      }
      
      //remove cache
      //assume that all workouts have same date
      if(models.get(0).getDate() != null) {
        cache.setWorkouts(user.getUid(), models.get(0).getDate(), null);
      }

      dao.addWorkouts(modelsCopy);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding workouts", e);
      throw new ConnectionException("Error adding workouts", e);
    }
    
    return models;
  }


  public List<ExerciseName> searchExerciseNames(UserOpenid user, String query, int limit) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching exercise names: "+query);
    }
    
    List<ExerciseName> list = new ArrayList<ExerciseName>();    

    try {

      //load from cache
      List<ExerciseName> listAll = cache.getExerciseNames();
      
      if(listAll == null) {
        listAll = dao.getExerciseNames();
        
        //save to cache
        cache.setExerciseNames(listAll);
      }
      
      if(listAll != null) {
      
        //split query string
        //strip special characters
        query = query.replace("(", "");
        query = query.replace(")", "");
        query = query.replace(",", "");
        query = query.toLowerCase();
        String[] arr = query.split(" ");
        
        //search
        List<ExerciseName> result = new ArrayList<ExerciseName>();
        
        for(int i=0; i < listAll.size(); i++) {
          ExerciseName n = listAll.get(i);
  
          String name = n.getName();
          
          //strip special characters
          name = name.replace("(", "");
          name = name.replace(")", "");
          name = name.replace(",", "");
          
          //filter by query (add count variable)
          int count = 0;
          for(String s : arr) {
            //if word long enough
            if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD) {
              //exact match
              if(name.toLowerCase().equals( s )) {
                count += 3;
              }
              //partial match
              else if(name.toLowerCase().contains( s )) {
                count++;
              }
            }
          }

          //if found
          if(count > 0) {
  
            int countUse = 0;
            try {
              countUse = cache.getExerciseNameCount(user, n.getId());
              
              if(countUse == -1) {
                countUse = dao.getExerciseNameCount(user, n.getId());
                
                cache.setExerciseNameCount(user, n.getId(), countUse);
              }
              
            } catch (Exception e) {
              logger.log(Level.SEVERE, "Error fetching exercise name count", e);
            }
            
            n.setCount(count, countUse);
            result.add(n);
          }
        }
        
        //sort array based on count
        Collections.sort(result);
        
        //convert to client model
        for(int i=0; i < result.size() && i < limit; i++) {
          ExerciseName n = result.get(i);
          if(n.getCountQuery() > 0) {
            list.add(n);
          }
          else {
            break;
          }
          //limit query
          if(list.size() >= limit) {
            break;
          }
        }
      
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error searching exercise names", e);
      throw new ConnectionException("Error searching exercise names", e);
    }
    
    return list;
  }


  @SuppressWarnings("unused")
  public List<ExerciseName> addExerciseName(UserOpenid user, List<ExerciseName> names) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding exercise names");
    }
    
    List<ExerciseName> list = new ArrayList<ExerciseName>(); 

    try {
      
      //load from cache
      List<ExerciseName> listAll = cache.getExerciseNames();
      
      if(list == null) {
        listAll = dao.getExerciseNames();
      }
      
      for(ExerciseName name : names) {
        
        //add if not found
        if(!listAll.contains(name)) {
          name.setUid(user.getUid());
          
          dao.addExerciseName(name);
          
          //update "cache" array
          listAll.add(name);
        }
        
        list.add(name);
      }

      //save to cache
      cache.setExerciseNames(listAll);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise names", e);
      throw new ConnectionException("Error adding exercise names", e);
    }
    
    return list;
  }


  public List<Workout> searchWorkouts(UserOpenid user, String query, int index) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching workouts ("+index+")");
    }

    List<Workout> list = new ArrayList<Workout>();
    
    try {

      //split query string
      String[] arr = query.split(" ");

      //load from cache
      List<Workout> listAll = dao.getWorkouts();

      int i = 0;
      for(Workout m : listAll) {
        
        if(!m.getUid().equals(user.getUid()) 
            && userManager.hasPermission(Permission.READ_NUTRITION, user.getUid(), m.getUid()))  {

          if(i >= index) {
            
            final String name = m.getName();
            
            //filter by query
            boolean match = false;
            for(String s : arr) {
              match = name.toLowerCase().contains( s.toLowerCase() );
              if(match) {
                break;
              }
            }
            
            if(match) {
  
              //get "whole" workout (which has also exercises, etc..)
              Workout workout = _getWorkout(m.getId());
              
              list.add(workout);
            }
          }
          
          i++;
          
        }
        
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise names", e);
      throw new ConnectionException("Error adding exercise names", e);
    }
    
    
    return list;
    
  }

  public List<Routine> searchRoutines(UserOpenid user, String query, int index) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching routines ("+index+")");
    }

    List<Routine> list = new ArrayList<Routine>();
    
    try {

      //split query string
      String[] arr = query.split(" ");

      //load from cache
      List<Routine> listAll = dao.getRoutines();

      int i = 0;
      for(Routine m : listAll) {
        
        if(!m.getUid().equals(user.getUid()) 
            && userManager.hasPermission(Permission.READ_NUTRITION, user.getUid(), m.getUid()))  {

          if(i >= index) {
            
            final String name = m.getName();
            
            //filter by query
            boolean match = false;
            for(String s : arr) {
              match = name.toLowerCase().contains( s.toLowerCase() );
              if(match) {
                break;
              }
            }
            
            if(match) {
  
              //get "whole" routine (which has also workouts, exercises, etc..)
              Routine routine = _getRoutine(m.getId());
              
              list.add(routine);
            }
          }
          
          i++;
          
        }
        
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise names", e);
      throw new ConnectionException("Error adding exercise names", e);
    }
    
    
    return list;
    
  }
  
  
}
