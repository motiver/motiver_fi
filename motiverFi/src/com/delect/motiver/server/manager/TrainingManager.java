package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.cache.TrainingCache;
import com.delect.motiver.server.dao.TrainingDAO;
import com.delect.motiver.server.dao.helper.WorkoutSearchParams;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.util.DateIterator;
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
        WorkoutSearchParams params = new WorkoutSearchParams(date, uid);
        List<Long> keys = dao.getWorkouts(params);
        
        list = new ArrayList<Workout>();
        for(Long key : keys) {
          list.add(_getWorkout(key));
        }

        //add to cache
        cache.setWorkouts(uid, date, list);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("Error loading workouts", e);
    }
  
    return list;
  }

  public List<Workout> getWorkouts(UserOpenid user, Date dateStart, Date dateEnd, String uid) throws ConnectionException {

    List<Workout> list = new ArrayList<Workout>();
    
    Iterator<Date> i = new DateIterator(dateStart, dateEnd);
    while(i.hasNext())
    {
      final Date date = i.next();
      list.addAll(getWorkouts(user, date, uid));
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
      WorkoutSearchParams params = new WorkoutSearchParams();
      params.offset = offset;
      params.limit = Constants.LIMIT_WORKOUTS;
      params.uid = uid;
      List<Long> keys = dao.getWorkouts(params);
      
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

  public List<Workout> getMostPopularWorkouts(UserOpenid user, int offset) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading most popular workouts ("+offset+")");
    }
    
    List<Workout> list = new ArrayList<Workout>();
    
    try {
      WorkoutSearchParams params = new WorkoutSearchParams();
      params.limit = Constants.LIMIT_WORKOUTS * 2;
      params.minCopyCount = 1;
      
      //while enough found
      int i = 0;
      while(list.size() < Constants.LIMIT_WORKOUTS) {
        params.offset = i;
        List<Long> keys = dao.getWorkouts(params);
        
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
        
        //if no workouts found -> stop
        if(keys.size() == 0) {
          break;
        }
        
        i += Constants.LIMIT_WORKOUTS * 2;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("getMostPopularWorkouts", e.getMessage());
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

  /**
   * Returns workout based on key
   * Fetchs also user and exercises names
   * @param key
   * @return
   * @throws Exception
   */
  private Workout _getWorkout(Long key) throws Exception {
    
    Workout jdo = cache.getWorkout(key);
    
    if(jdo == null) {
      jdo = dao.getWorkout(key);
      jdo.setUser(userManager.getUser(jdo.getUid()));
      
      //find names for each exercise
      for(Exercise f : jdo.getExercises()) {
        if(f.getNameId().longValue() > 0) {
          f.setName(_getExerciseName(f.getNameId()));
        }
      }
      
      cache.addWorkout(jdo);
    }
    
    //sort exercises
    if(jdo != null) {
      Collections.sort(jdo.getExercises());
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

  private ExerciseName _getExerciseName(Long key) throws Exception {
    
    List<ExerciseName> names = _getExerciseNames();
    
    if(names != null) {
      for(ExerciseName name : names) {
        if(name.getId().equals(key)) {
          return name;
        }
      }
    }
    
    return null;
  }

  private List<ExerciseName> _getExerciseNames() throws Exception {

    //load from cache
    List<ExerciseName> listAll = cache.getExerciseNames();
    
    if(listAll == null) {
      listAll = dao.getExerciseNames();
      
      //save to cache
      cache.setExerciseNames(listAll);
    }
    
    return listAll;
  }

  private void _updateWorkout(Workout workout) throws Exception {
    
    dao.updateWorkout(workout);
    
    cache.addWorkout(workout);
  }


  public boolean removeWorkouts(UserOpenid user, List<Workout> models) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Removing workouts");
    }
    
    if(models.size() == 0) {
      return false;
    }
    
    boolean ok = false;
    
    try {
      Long[] keys = new Long[models.size()];
      
      //check permission for each workout
      int i = 0;
      for(Workout workout : models) {
        Workout w = _getWorkout(workout.getId());
        
        userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), w.getUid());

        //remove cache
        if(w.getDate() != null) {
          cache.setWorkouts(w.getUid(), w.getDate(), null);
        }
        else {
          cache.removeWorkout(w.getId());
        }

        keys[i] = w.getId();
        
        i++;
      }
      
      //remove all at once
      ok = dao.removeWorkouts(keys);
      
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
          
          //check permission
          userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid());
          
          //increment count
          if(!user.getUid().equals(jdo.getUid())) {
            //increment count
            jdo.setCount(jdo.getCount() + 1);
            _updateWorkout(jdo);
          }
          
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

      //get exercises
      for(Workout workout : modelsCopy) {
        for(Exercise f : workout.getExercises()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(_getExerciseName(f.getNameId()));
          }
        }

        //cache
        cache.addWorkout(workout);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding workouts", e);
      throw new ConnectionException("Error adding workouts", e);
    }
    
    return modelsCopy;
  }


  public List<ExerciseName> searchExerciseNames(UserOpenid user, String query, int limit) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching exercise names: "+query);
    }
    
    List<ExerciseName> list = new ArrayList<ExerciseName>();    

    try {

      //load from cache
      List<ExerciseName> listAll = _getExerciseNames();
      
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
      List<Long> keysAll = dao.getWorkouts(WorkoutSearchParams.all());

      int i = 0;
      for(Long key : keysAll) {

        Workout m = _getWorkout(key);
        
        if(!m.getUid().equals(user.getUid()) 
            && userManager.hasPermission(Permission.READ_TRAINING, user.getUid(), m.getUid()))  {

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

  public void updateWorkout(UserOpenid user, Workout model) throws ConnectionException {
    
    try {
      
      Workout workout = dao.getWorkout(model.getId());
      
      userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), workout.getUid());

      //save old date
      Date dOld = workout.getDate();
      
      workout.update(model, false, false);
      dao.updateWorkout(workout);

      //update workout given as parameter
      model.update(workout, true, true);

      //remove from cache (also old date if moved)
      cache.setWorkouts(workout.getUid(), workout.getDate(), null);
      if(!dOld.equals(workout.getDate())) {
        cache.setWorkouts(workout.getUid(), dOld, null);
      }
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating workout", e);
      throw new ConnectionException("Error updating workout", e);
    }
  
  }

  public Workout getWorkout(UserOpenid user, long id) throws ConnectionException {

    Workout workout = null;
    
    try {
      workout = _getWorkout(id);
      
      userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), workout.getUid());

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workout", e);
      throw new ConnectionException("Error loading workout", e);
    }
    
    return workout;
  }  

  /**
   * Updates exercise order for single workout
   */
  public boolean updateExerciseOrder(UserOpenid user, Long workoutId, Long[] ids) throws ConnectionException {
  
    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating exercise order:");
    }
    
    boolean ok = false;

    try {
    
      //get workout
      Workout w = _getWorkout(workoutId);
      
      //reorder exercises
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
  
      updateWorkout(user, w);
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workout", e);
      throw new ConnectionException("Error loading workout", e);
    }
  
    return ok;
  }
  
}
