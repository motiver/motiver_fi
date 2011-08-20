/**
 * 
 */
package com.delect.motiver.server.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.ExerciseName;
import com.delect.motiver.server.UserOpenid;
import com.delect.motiver.server.Workout;
import com.delect.motiver.shared.ExerciseNameModel;
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
	private static final Logger logger = Logger.getLogger(WeekCache.class.getName()); 
	
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

  @SuppressWarnings("unchecked")
  public List<ExerciseName> getExerciseNames() {
    if(cache == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append("en");
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
    
    return names;
  }
  
  public void addExerciseNames(List<ExerciseName> names) {
    if(cache == null) {
      return;
    }
    
    Map<Long, ExerciseName> map = new HashMap<Long, ExerciseName>();
    
    //add each name
    for(ExerciseName name : names) {
      map.put(name.getId(), name);
    }

    StringBuilder builder = new StringBuilder();
    builder.append("en");
    
    cache.put(builder.toString(), map);
    
  }

  /**
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<UserOpenid> getUsers() {
    if(cache == null) {
      return null;
    }

    List<UserOpenid> names = null;

    Object obj = cache.get("users");
    if(obj instanceof Map) {
      names = new ArrayList<UserOpenid>();
      Map<String, UserOpenid> map = (Map<String, UserOpenid>)obj;
      
      Collection<UserOpenid> c = map.values();
      Iterator<UserOpenid> itr = c.iterator();
      while(itr.hasNext()) {
        names.add(itr.next());
      }
    }
    
    return names;
  
  }

  /**
   * @param users
   */
  public void setUsers(List<UserOpenid> users) {
    if(cache == null) {
      return;
    }

    Map<String, UserOpenid> map = new HashMap<String, UserOpenid>();
    
    //add each name
    for(UserOpenid u : users) {
      map.put(u.getId(), u);
    }
    
    cache.put("users", map);
  }

  /**
   * 
   */
  public void removeUsers() {
    if(cache == null) {
      return;
    }
    
    cache.remove("users");
  }
}
