package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.delect.motiver.server.cache.TrainingCache;
import com.delect.motiver.server.dao.TrainingDAO;
import com.delect.motiver.server.dao.helper.RoutineSearchParams;
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

  private static final Pattern PATTERN_EXERCISE_TARGET = Pattern.compile("--([0-9])--");
  
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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding/updating exercise: "+model);
    }    
          
    try {

      Workout workout = _getWorkout(workoutId);

      if(workout != null) {
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
        dao.updateWorkout(workout, true);
        
        //return updated model
        model.update(f, true);
        
        //find names for each exercise
        for(Exercise e : workout.getExercises()) {
          if(e.getNameId().longValue() > 0) {
            e.setName(_getExerciseName(e.getNameId()));
          }
        }
        if(model.getNameId().longValue() > 0) {
          model.setName(_getExerciseName(model.getNameId()));
        }
      
        //update cache
        if(workout.getDate() != null) {
          cache.setWorkouts(workout.getUid(), workout.getDate(), null);  //clear day's cache
        }
        if(workout.getRoutineId() != null) {
          cache.removeRoutine(workout.getRoutineId());
        }
        cache.addWorkout(workout);
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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing exercise: "+model);
    }
    
    boolean ok = false;
          
    try {

      Workout workout = _getWorkout(workoutId);
      
      if(workout != null) {        
        userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), workout.getUid());

        workout.getExercises().remove(model);
        dao.updateWorkout(workout, true);

        //update cache
        if(workout.getDate() != null) {
          cache.setWorkouts(workout.getUid(), workout.getDate(), null);  //clear day's cache
        }
        if(workout.getRoutineId() != null) {
          cache.removeRoutine(workout.getRoutineId());
        }
        cache.addWorkout(workout);
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise", e);
      throw new ConnectionException("Add exercise", e.getMessage());
    }
    
    return ok;
  }

  public List<Workout> getWorkouts(UserOpenid user, Date date, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading workouts ("+uid+", "+date+")");
    }

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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading workouts ("+uid+", "+dateStart+" - "+dateEnd+")");
    }

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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading workouts ("+offset+", "+uid+")");
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
        
        //can be null if results are cutted
        if(jdo != null) {
          //check permission
          userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid());
        }
        
        list.add(jdo);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workouts", e);
      throw new ConnectionException("getWorkouts", e.getMessage());
    }
    
    return list;
  }
  
  public List<Routine> getRoutines(UserOpenid user, int offset, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading routines ("+offset+", "+uid+")");
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), uid);
    
    List<Routine> list = new ArrayList<Routine>();
    
    try {
      RoutineSearchParams params = new RoutineSearchParams();
      params.offset = offset;
      params.limit = Constants.LIMIT_ROUTINES;
      params.uid = uid;
      List<Long> keys = dao.getRoutines(params);
      
      for(Long key : keys) {
        
        Routine jdo = _getRoutine(key);
        
        //can be null if results are cutted
        if(jdo != null) {
          //check permission
          userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid());
        }
        
        list.add(jdo);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading routines", e);
      throw new ConnectionException("getRoutines", e.getMessage());
    }
    
    return list;
  }

  public List<Workout> getMostPopularWorkouts(UserOpenid user, int offset) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading most popular workouts ("+offset+")");
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

  public List<Routine> getMostPopularRoutines(UserOpenid user, int offset) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading most popular routines ("+offset+")");
    }
    
    List<Routine> list = new ArrayList<Routine>();
    
    try {
      RoutineSearchParams params = new RoutineSearchParams();
      params.limit = Constants.LIMIT_ROUTINES * 2;
      params.minCopyCount = 1;
      
      //while enough found
      int i = 0;
      while(list.size() < Constants.LIMIT_ROUTINES) {
        params.offset = i;
        List<Long> keys = dao.getRoutines(params);
        
        for(Long key : keys) {
          
          if(list.size() == Constants.LIMIT_ROUTINES) {
            break;
          }

          Routine jdo = _getRoutine(key);
          if(jdo != null) {
            
            //check permission
            if(userManager.hasPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid())) {
              if(i >= offset) {
                list.add(jdo);
              }
            }
            
          }
        }
        
        //if no routines found -> stop
        if(keys.size() == 0) {
          break;
        }
        
        i += Constants.LIMIT_ROUTINES * 2;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading routines", e);
      throw new ConnectionException("getMostPopularRoutines", e.getMessage());
    }
    
    return list;
  }
  
  public List<Workout> getWorkouts(UserOpenid user, Routine routine, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading workouts from routine ("+routine+", "+uid+")");
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

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getWorkout ("+key+")");
    }
    
    if(key == null) {
      return null;
    }
    
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

  private Routine _getRoutine(Long routineId) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getRoutine ("+routineId+")");
    }
    
    if(routineId == null) {
      return null;
    }
    
    Routine jdo = cache.getRoutine(routineId);
    
    if(jdo == null) {
      jdo = dao.getRoutine(routineId);
      
      jdo.setUser(userManager.getUser(jdo.getUid()));
      
      //get workouts
      List<Long> keys = dao.getWorkouts(new WorkoutSearchParams(routineId));
      ArrayList<Workout> list = new ArrayList<Workout>();
      for(Long key : keys) {
        list.add(_getWorkout(key));
      }
      jdo.setWorkouts(list);
     
      cache.addRoutine(jdo);
    }
    
    return jdo;
  }

  private ExerciseName _getExerciseName(Long key) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getExerciseName ("+key+")");
    }
    
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

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getExerciseNames");
    }

    //load from cache
    List<ExerciseName> listAll = cache.getExerciseNames();
    
    if(listAll == null) {
      listAll = dao.getExerciseNames();
      
      //save to cache
      cache.setExerciseNames(listAll);
    }
    
    return listAll;
  }

//  private void _updateWorkout(Workout workout) throws Exception {
//
//    if(logger.isLoggable(Level.FINER)) {
//      logger.log(Level.FINER, "_updateWorkout ("+workout+")");
//    }
//    
//    dao.updateWorkout(workout);
//    
//    cache.addWorkout(workout);
//  }

  public boolean removeWorkouts(UserOpenid user, List<Workout> models) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing workouts: "+models.size());
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
        cache.removeWorkout(w.getId());

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

  public boolean removeRoutines(UserOpenid user, List<Routine> models) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing routines: "+models.size());
    }
    
    if(models.size() == 0) {
      return false;
    }
    
    boolean ok = false;
    
    try {
      Long[] keys = new Long[models.size()];
      
      //check permission for each routine
      int i = 0;
      for(Routine routine : models) {
        Routine w = _getRoutine(routine.getId());
        
        userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), w.getUid());
        
        //remove workouts
        //TODO doesn't remove from cache
        List<Long> keysW = dao.getWorkouts(new WorkoutSearchParams(w.getId()));
        Long[] keysW2 = keysW.toArray(new Long[0]);
        dao.removeWorkouts(keysW2);
        
        //remove from cache
        cache.removeRoutine(w.getId());

        keys[i] = w.getId();
        
        i++;
      }
      
      //remove all at once
      ok = dao.removeRoutines(keys);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing routines", e);
      throw new ConnectionException("Error removing routines", e);
    }
    
    return ok;
    
  }

  @SuppressWarnings("deprecation")
  public List<Workout> addWorkouts(UserOpenid user, List<Workout> models) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding workouts: "+models.size());
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
            incrementWorkoutCount(jdo.getId());
          }
          
          //add copy
          clone = (Workout) jdo.clone();
        }

        clone.setUid(user.getUid());
        clone.setUser(user);
        clone.setDate(workout.getDate());
        clone.setRoutineId(workout.getRoutineId());
        clone.setDayInRoutine(workout.getDayInRoutine());
        modelsCopy.add(clone);
        
        //remove cache
        if(clone.getDate() != null) {
          cache.setWorkouts(user.getUid(), clone.getDate(), null);
        }
      }

      dao.addWorkouts(modelsCopy);

      //get workouts
      for(int i = 0; i < modelsCopy.size(); i++) {
        Workout workout = modelsCopy.get(i);
        
        //clear routine cache
        if(workout.getRoutineId() != null) {
          cache.removeRoutine(workout.getRoutineId());
        }
        
        //get correct workout
        //TODO we need to do this because exercise are doubled when adding to db
        modelsCopy.set(i, _getWorkout(workout.getId()));
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding workouts", e);
      throw new ConnectionException("Error adding workouts", e);
    }
    
    return modelsCopy;
  }

  @SuppressWarnings("deprecation")
  public List<Routine> addRoutines(UserOpenid user, List<Routine> models) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding routines: "+models.size());
    }
    
    if(models.size() == 0) {
      return null;
    }

    List<Routine> modelsCopy = new ArrayList<Routine>();
    
    try {
      
      //get routines
      for(Routine routine : models) {
        
        Routine clone = null;
        
        //new
        if(routine.getId() == 0) {
          //default number of days
          if(routine.getDays() == 0) {
            routine.setDays(Constants.DAYS_ROUTINE_DEFAULT);
          }
          
          clone = routine;
          clone.setUid(user.getUid());
          clone.setUser(user);
          clone.setDate(routine.getDate());
          dao.addRoutine(clone);
        }
        else {
          //check cache
          Routine jdo = _getRoutine(routine.getId());
          
          //check permission
          userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid());
          
          //increment count
          if(!user.getUid().equals(jdo.getUid())) {
            incrementRoutineCount(jdo.getId());
          }
          
          //add copy
          clone = (Routine) jdo.clone();
          clone.setUid(user.getUid());
          clone.setUser(user);
          clone.setDate(routine.getDate());
          dao.addRoutine(clone);

          //add workouts
          ArrayList<Workout> list = new ArrayList<Workout>();
          for(Workout wOld : jdo.getWorkouts()) {
            
            Workout wClone = (Workout) wOld.clone();

            //set also date
            if(routine.getDate() != null) {
              final Date dateNew = new Date( (routine.getDate().getTime() / 1000 + 3600 * 24 * (wClone.getDayInRoutine().intValue() - 1) ) * 1000);
              //reset time from date
              dateNew.setHours(0);
              dateNew.setMinutes(0);
              dateNew.setSeconds(0);
              wClone.setDate(dateNew);
              
              //clear cache
              cache.setWorkouts(user.getUid(), dateNew, null);
            }
            
            wClone.setUid(user.getUid());
            wClone.setUser(user);
            wClone.setRoutineId(clone.getId());
            
            list.add(wClone);
          }
          dao.addWorkouts(list);
          clone.setWorkouts(list);
        }
        
        modelsCopy.add(clone);
      }

      //cache
      for(Routine jdo : modelsCopy) {

        jdo.setUser(userManager.getUser(jdo.getUid()));
        
        //get workouts
        List<Long> keys = dao.getWorkouts(new WorkoutSearchParams(jdo.getId()));
        ArrayList<Workout> list = new ArrayList<Workout>();
        for(Long key : keys) {
          list.add(_getWorkout(key));
        }
        jdo.setWorkouts(list);
        
        cache.addRoutine(jdo);
      }

      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding routines", e);
      throw new ConnectionException("Error adding routines", e);
    }
    
    return modelsCopy;
  }


  public List<ExerciseName> searchExerciseNames(UserOpenid user, String query, int limit) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Searching exercise names: "+query);
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
         
        //save
        Set<Integer> targets = new HashSet<Integer>();
        try {
          Matcher matcher = PATTERN_EXERCISE_TARGET.matcher(query);
          while(matcher.find()) {
            targets.add(Integer.parseInt(matcher.group(1)));
          }
        } catch (Exception e1) {
          logger.log(Level.WARNING, "Error parsing target", e1);
        }
        
        //search
        List<ExerciseName> result = new ArrayList<ExerciseName>();

        String locale = user.getLocale();
        for(int i=0; i < listAll.size(); i++) {
          ExerciseName n = listAll.get(i);
          
          //if correct locale
          if(n.getLocale().equals(locale)) {
            String name = n.getName();
            int target = n.getTarget();
            
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
            
            //if targets matches
            if(targets.contains(target)) {
              count += 3;
            }
            
            logger.info(" count: "+count);

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

  public List<ExerciseName> addExerciseName(UserOpenid user, List<ExerciseName> names) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding exercise names: "+names.size());
    }
    
    List<ExerciseName> list = new ArrayList<ExerciseName>(); 

    try {
      
      //load from cache
      List<ExerciseName> listAll = cache.getExerciseNames();
      
      if(listAll == null) {
        listAll = dao.getExerciseNames();
      }
      
      for(ExerciseName name : names) {
        
        int i = listAll.indexOf(name);        
        
        //add if not found
        if(i == -1) {
          name.setUid(user.getUid());
          
          dao.addExerciseName(name);
          
          //update "cache" array
          listAll.add(name);
        }
        //otherwise update (if name we have added)
        else {
          ExerciseName nameOld = listAll.get(i);
          if(nameOld != null && user.getUid().equals(nameOld.getUid())) {
            nameOld.update(name, false);
            dao.updateExerciseName(nameOld);
          }
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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Searching workouts ("+query+")");
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
              list.add(m);
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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Searching routines ("+query+")");
    }

    List<Routine> list = new ArrayList<Routine>();
    
    try {

      //split query string
      String[] arr = query.split(" ");

      //load from cache
      List<Long> keysAll = dao.getRoutines(RoutineSearchParams.all());

      int i = 0;
      for(Long key : keysAll) {

        Routine m = _getRoutine(key);
        
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
              list.add(m);
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

  public void updateWorkout(UserOpenid user, Workout model, boolean updateExercises) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Updating workout: "+model);
    }
    
    try {
      
      Workout workout = _getWorkout(model.getId());
      
      userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), workout.getUid());

      //save old date
      Date dOld = workout.getDate();
      
      workout.update(model, false, updateExercises);
      dao.updateWorkout(workout, updateExercises);
      
      //find names for each exercise
      for(Exercise e : workout.getExercises()) {
        if(e.getNameId().longValue() > 0) {
          e.setName(_getExerciseName(e.getNameId()));
        }
      }

      //update workout given as parameter
      model.update(workout, true, updateExercises);

      //remove from cache (also old date if moved)
      if(workout.getDate() != null) {
        cache.setWorkouts(workout.getUid(), workout.getDate(), null);
        if(!dOld.equals(workout.getDate())) {
          cache.setWorkouts(workout.getUid(), dOld, null);
        }
      }
      cache.removeWorkout(workout.getId());
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating workout", e);
      throw new ConnectionException("Error updating workout", e);
    }
  
  }

  public void updateRoutine(UserOpenid user, Routine model) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Updating routine: "+model);
    }
    
    try {
      
      Routine routine = dao.getRoutine(model.getId());
      
      userManager.checkPermission(Permission.WRITE_TRAINING, user.getUid(), routine.getUid());
      
      routine.update(model, false);
      dao.updateRoutine(routine);

      //update routine given as parameter
      model.update(routine, true);

      //remove from cache
      cache.removeRoutine(routine.getId());
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating routine", e);
      throw new ConnectionException("Error updating routine", e);
    }
  
  }

  public Workout getWorkout(UserOpenid user, long id) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading single workout: "+id);
    }

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

  public ExerciseName getExerciseName(UserOpenid user, long id) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading single exercise name: "+id);
    }

    ExerciseName name = null;
    
    try {
      name = _getExerciseName(id);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading name", e);
      throw new ConnectionException("getExerciseName", e);
    }
    
    return name;
  }

  public Routine getRoutine(UserOpenid user, long id) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading single routine: "+id);
    }

    Routine routine = null;
    
    try {
      routine = _getRoutine(id);
      
      userManager.checkPermission(Permission.READ_TRAINING, user.getUid(), routine.getUid());

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading routine", e);
      throw new ConnectionException("Error loading routine", e);
    }
    
    return routine;
  }    

  /**
   * Updates exercise order for single workout
   */
  public boolean updateExerciseOrder(UserOpenid user, Long workoutId, Long[] ids) throws ConnectionException {
  
    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating exercise order ("+workoutId+", "+ids+")");
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
  
      updateWorkout(user, w, true);
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading workout", e);
      throw new ConnectionException("Error loading workout", e);
    }
  
    return ok;
  }

  public void incrementWorkoutCount(long workoutId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Inrementing workout count: "+workoutId);
    }
    
    try {
      
      Workout workout = _getWorkout(workoutId);
      dao.incrementWorkoutCount(workout);

      //update cache
      cache.addWorkout(workout);
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating workout", e);
      throw new ConnectionException("Error updating workout", e);
    }
  
  }

  public void incrementRoutineCount(long routineId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Inrementing routine count: "+routineId);
    }
    
    try {
      
      Routine routine = _getRoutine(routineId);
      dao.incrementRoutineCount(routine);

      //update cache
      cache.addRoutine(routine);
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating routine", e);
      throw new ConnectionException("Error updating routine", e);
    }
  
  }

  /**
   * Returns exercises which have given nameId from given time period
   * @param nameId
   * @param dateStart
   * @param dateEnd
   * @param limit
   * @return exercises : sorted by date desc
   * @throws ConnectionException 
   */
  public List<Exercise> getExercises(UserOpenid user, Long nameId, Date dateStart, Date dateEnd, int limit) throws ConnectionException {

//    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.WARNING, "Loading exercises for name "+nameId+", "+dateStart+" - "+dateEnd);
//    }
    
    if(nameId == null || (dateStart == null && dateEnd == null)) {
      return null;
    }
    
    List<Exercise> list = new ArrayList<Exercise>();
    
    try {

      //if only end date -> get all exercises before that
      if(dateStart == null) {
        dateStart = new Date( (dateEnd.getTime()/1000 - 3600*24*Constants.LIMIT_EXERCISE_HISTORY_BACK)*1000 );
      }
      //if only start date -> get all exercises after that
      else if(dateEnd == null) {
        dateEnd = new Date();
      }
      
      //get workouts
      List<Workout> workouts = getWorkouts(user, dateStart, dateEnd, user.getUid());
      
      if(workouts.size() > 0) {
        for(int i = workouts.size(); i > 0; i--) {
          Workout w = workouts.get(i-1);
          if(w != null) {
            //check if correct exercise name found
            for(Exercise e : w.getExercises()) {
              if(e.getNameId() != null && e.getNameId().equals(nameId)) {
                e.setWorkout(w);
                list.add(e);
              }
              
              if(limit != -1 && list.size() >= limit) {
                break;
              }
            }
            if(limit != -1 && list.size() >= limit) {
              break;
            }
          }
        }   
      }     

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading exercises", e);
      throw new ConnectionException("getExercises", e);
    }
    
    return list;
  }
  
}