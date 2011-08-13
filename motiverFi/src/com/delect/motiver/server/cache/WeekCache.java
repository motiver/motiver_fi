/**
 * 
 */
package com.delect.motiver.server.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.ExerciseName;
import com.delect.motiver.server.Workout;
import com.delect.motiver.server.service.MyServiceImpl;
import com.delect.motiver.shared.WorkoutModel;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

/**
 * @author Antti
 *
 */
public class WeekCache {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MyServiceImpl.class.getName()); 
	
	private final static int CACHE_EXPIRE_SECONDS = 604800;
	
	private static Cache cache;
	private static Map props = new HashMap();
    
  @SuppressWarnings("unchecked")
	public WeekCache() {
  	try {
			if(cache == null) {
			    props.put(GCacheFactory.EXPIRATION_DELTA, CACHE_EXPIRE_SECONDS);
				CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			    cache = cacheFactory.createCache(props);
			}
		} catch (CacheException e) {
			logger.log(Level.SEVERE, "Error creating cache", e);
		}
  }

  public Workout getWorkout(Long workoutId) {
    if(cache == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append("w");
    builder.append(workoutId);
    Object obj = cache.get(builder.toString());
    
    return (obj instanceof Workout)? (Workout)obj : null;
  }
  
  public void addWorkout(Workout workout) {
    if(cache == null) {
      return;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append("w");
    builder.append(workout.getId());
    
    cache.put(builder.toString(), workout);
  }
  
  public void removeWorkoutModel(Long workoutId) {
    if(cache == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("w");
    builder.append(workoutId);

    cache.remove(builder.toString());
  }

  public ExerciseName getExerciseName(Long nameId) {
    if(cache == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append("en");
    builder.append(nameId);
    Object obj = cache.get(builder.toString());
    
    return (obj instanceof ExerciseName)? (ExerciseName)obj : null;
  }
  
  public void addExerciseName(ExerciseName name) {
    if(cache == null) {
      return;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append("en");
    builder.append(name.getId());
    
    cache.put(builder.toString(), name);
  }
  
  public void removeExerciseNameModel(Long nameId) {
    if(cache == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("en");
    builder.append(nameId);

    cache.remove(builder.toString());
  }
}
