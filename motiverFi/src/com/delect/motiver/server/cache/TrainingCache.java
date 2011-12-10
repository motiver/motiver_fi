package com.delect.motiver.server.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class TrainingCache {

  private final static boolean CACHE_ON = true;

  private final static String PREFIX_WORKOUTS = "tc_ws";
  private final static String PREFIX_WORKOUT = "tc_w";
  private final static String PREFIX_ROUTINE = "tc_r";
  private final static String PREFIX_EXERCISE_NAMES = "tc_en";
  private final static String PREFIX_EXERCISE_NAME_COUNT = "tc_en_c";
  
  private final static int CACHE_EXPIRE_SECONDS = 604800;
  
  private SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(TrainingCache.class.getName());
  
  private static Cache cache; 
  private static TrainingCache trainingCache; 

  @SuppressWarnings("unchecked")
  public static TrainingCache getInstance() {
    if(cache == null) {
      try {
        Map props = new HashMap();
        props.put(GCacheFactory.EXPIRATION_DELTA, CACHE_EXPIRE_SECONDS);
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        cache = cacheFactory.createCache(props);
      } catch (CacheException e) {
        logger.log(Level.SEVERE, "Error loading cache", e);
      }
    }
    
    if(trainingCache == null) {
      trainingCache = new TrainingCache();
    }
    return trainingCache;
  }

  @SuppressWarnings("unchecked")
  public List<Workout> getWorkouts(String uid, Date date) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUTS);
    builder.append("_");
    builder.append(uid);
    builder.append("_");
    builder.append(fmt.format(date));
    builder.append("_");
    
    Object obj = cache.get(builder.toString());

    List<Workout> workouts = null;
    
    if(obj instanceof Map) {
      workouts = new ArrayList<Workout>();
      Map<Long, Workout> map = (Map<Long, Workout>)obj;
      
      Collection<Workout> c = map.values();
      Iterator<Workout> itr = c.iterator();
      while(itr.hasNext()) {
        workouts.add(itr.next());
      }
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded workouts ("+uid+", "+date+"): "+workouts);
    }
    
    return workouts;
  }

  public void setWorkouts(String uid, Date date, List<Workout> list) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    Map<Long, Workout> map = null;
    if(list != null) {
      map = new HashMap<Long, Workout>();
      for(Workout w : list) {
        map.put(w.getId(), w);
      }
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUTS);
    builder.append("_");
    builder.append(uid);
    builder.append("_");
    builder.append(fmt.format(date));
    builder.append("_");
    
    cache.put(builder.toString(), map);
  }

  public Workout getWorkout(Long workoutId) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    //workout
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUT);
    builder.append(workoutId);
    Object obj = cache.get(builder.toString());
    
    Workout t = null;
    if(obj != null && obj instanceof Workout) {
      t = (Workout)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded workout ("+workoutId+"): "+t);
    }
    
    return t;
  }
  
  public void addWorkout(Workout workout) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving single workout: "+workout);
    }
    
    //workout
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUT);
    builder.append(workout.getId());
    cache.put(builder.toString(), workout);
    
  }
  
  public void removeWorkout(Long workoutId) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing single workout: "+workoutId);
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUT);
    builder.append(workoutId);

    cache.remove(builder.toString());
  }
  
  public void removeRoutine(Long routineId) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing single routine: "+routineId);
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_ROUTINE);
    builder.append(routineId);

    cache.remove(builder.toString());
  }
  
  @SuppressWarnings("unchecked")
  public List<ExerciseName> getExerciseNames() {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISE_NAMES);
    Object obj = cache.get(builder.toString());

    List<ExerciseName> names = null;
    
    if(obj instanceof Map) {
      names = new ArrayList<ExerciseName>();
      Map<Long, ExerciseName> map = (Map<Long, ExerciseName>)obj;
      
      Collection<ExerciseName> c = map.values();
      Iterator<ExerciseName> itr = c.iterator();
      while(itr.hasNext()) {
        names.add(itr.next());
      }
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded exercise names: "+((names != null)? names.size() : 0));
    }
    
    return names;
  }
  
  public void setExerciseNames(List<ExerciseName> names) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving exercise names: "+names.size());
    }
    
    Map<Long, ExerciseName> map = new HashMap<Long, ExerciseName>();
    
    //add each name
    for(ExerciseName name : names) {
      map.put(name.getId(), name);
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISE_NAMES);
    
    cache.put(builder.toString(), map);
    
  }


  public int getExerciseNameCount(UserOpenid user, Long id) {
    
    if(cache == null || !CACHE_ON) {
      return -1;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISE_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(user.getUid());
    Object obj = cache.get(builder.toString());

    int c = (obj != null)? (Integer)obj : -1;
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded exercise name count ("+user.getUid()+", "+id+"): "+c);
    }
    
    return  c;
  }
  
  public void setExerciseNameCount(UserOpenid user, Long id, int count) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving exercise name count ("+user.getUid()+", "+id+", "+count+")");
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISE_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(user.getUid());
    cache.put(builder.toString(), count);
    
  }

  public Routine getRoutine(Long routineId) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    //routine
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_ROUTINE);
    builder.append(routineId);
    Object obj = cache.get(builder.toString());
    
    Routine t = null;
    if(obj != null && obj instanceof Routine) {
      t = (Routine)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded single routine ("+routineId+"): "+t);
    }
    
    return t;
  }
  
  public void addRoutine(Routine routine) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving single routine: "+routine);
    }
    
    //routine
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_ROUTINE);
    builder.append(routine.getId());
    cache.put(builder.toString(), routine);
    
  }
}
