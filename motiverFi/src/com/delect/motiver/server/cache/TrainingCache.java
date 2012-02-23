package com.delect.motiver.server.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.delect.motiver.server.dao.helper.WorkoutSearchParams;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.service.MyServiceImpl;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.prodeagle.java.counters.Counter;

public class TrainingCache {

  private final static boolean CACHE_ON = true;

  private final static String PREFIX_WORKOUTS = "tc_ws2";
  private final static String PREFIX_WORKOUT = "tc_w";
  private final static String PREFIX_ROUTINE = "tc_r";
  private final static String PREFIX_EXERCISE_NAME = "tc_en1_";
  private final static String PREFIX_EXERCISE_NAMES = "tc_en2_";
  private final static String PREFIX_EXERCISE_NAME_COUNT = "tc_en_c";
  
  private final static int CACHE_EXPIRE_SECONDS = 604800;
  
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
  public Set<Long> getWorkouts(WorkoutSearchParams params) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }

    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_WORKOUTS);
    builder.append("_");
    params.getCacheKey(builder);
    
    Object obj = cache.get(builder.toString());

    Set<Long> workouts = null;
    
    if(obj instanceof Set) {
      workouts = (Set<Long>)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded workouts ("+params.uid+", "+params.date+"): "+workouts);
    }
    
    return workouts;
  }

  public void setWorkouts(WorkoutSearchParams params, Set<Long> workouts) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }

    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_WORKOUTS);
    builder.append("_");
    params.getCacheKey(builder);
    
    cache.put(builder.toString(), workouts);
  }

  public Workout getWorkout(Long workoutId) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    //workout
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_WORKOUT);
    builder.append(workoutId);
    Object obj = cache.get(builder.toString());
    
    Workout t = null;
    if(obj != null && obj instanceof Workout) {

      //prodeagle counter
      Counter.increment("Cache.Workout");
      
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
    StringBuilder builder = MyServiceImpl.getStringBuilder();
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

    StringBuilder builder = MyServiceImpl.getStringBuilder();
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

    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_ROUTINE);
    builder.append(routineId);

    cache.remove(builder.toString());
  }
  
  @SuppressWarnings("unchecked")
  public Map<Long, String> getExerciseNames(String locale) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_EXERCISE_NAMES);
    builder.append(locale);
    builder.append("_");
    Object obj = cache.get(builder.toString());

    Map<Long, String> names = null;
    
    if(obj instanceof Map) {

      //prodeagle counter
      Counter.increment("Cache.ExerciseNames");
      
      names = (Map<Long, String>)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded exercise names: "+((names != null)? names.size() : 0));
    }
    
    return names;
  }
  
  public void setExerciseNames(String locale, Map<Long, String> map) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving exercise names: "+map.size());
    }

    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_EXERCISE_NAMES);
    builder.append(locale);
    builder.append("_");
    
    cache.put(builder.toString(), map);
    
  }


  public int getExerciseNameCount(UserOpenid user, Long id) {
    
    if(cache == null || !CACHE_ON) {
      return -1;
    }
    
    StringBuilder builder = MyServiceImpl.getStringBuilder();
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
    
    StringBuilder builder = MyServiceImpl.getStringBuilder();
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
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_ROUTINE);
    builder.append(routineId);
    Object obj = cache.get(builder.toString());
    
    Routine t = null;
    if(obj != null && obj instanceof Routine) {

      //prodeagle counter
      Counter.increment("Cache.Routine");
      
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
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_ROUTINE);
    builder.append(routine.getId());
    cache.put(builder.toString(), routine);
    
  }

  public ExerciseName getExerciseName(Long key) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    //workout
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_EXERCISE_NAME);
    builder.append(key);
    Object obj = cache.get(builder.toString());
    
    ExerciseName t = null;
    if(obj != null && obj instanceof ExerciseName) {

      //prodeagle counter
      Counter.increment("Cache.ExerciseName");
      
      t = (ExerciseName)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded exercise name ("+key+"): "+t);
    }
    
    return t;
  }

  public void addExerciseName(ExerciseName jdo) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving single exercise name: "+jdo);
    }
    
    //exercise name
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_EXERCISE_NAME);
    builder.append(jdo.getId());
    cache.put(builder.toString(), jdo);
    
    //add to "search" cache
    Map<Long, String> map = getExerciseNames(jdo.getLocale());
    if(map == null)
      map = new HashMap<Long, String>();
    map.put(jdo.getId(), jdo.getName());
    this.setExerciseNames(jdo.getLocale(), map);
  }
}
