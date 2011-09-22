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
package com.delect.motiver.server.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.jdo.ExerciseName;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.Workout;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.Time;
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
	
	//prefixes
	private final static String PREFIX_WORKOUT = "w";
  private final static String PREFIX_EXERCISENAMES = "en";
  private final static String PREFIX_EXERCISE_NAME_COUNT = "en_c";
  private final static String PREFIX_FOOD_NAMES = "fnames";
  private final static String PREFIX_FOOD_NAME = "fn";
  private final static String PREFIX_FOOD_NAME_COUNT = "fn_c";
  private final static String PREFIX_TIME = "t";
  private final static String PREFIX_MEAL = "m";
  private final static String PREFIX_USERS = "users";
  
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
    builder.append(PREFIX_WORKOUT);
    builder.append(workoutId);
    Object obj = cache.get(builder.toString());
    
    return (obj instanceof Workout)? (Workout)obj : null;
  }
  
  public void addWorkout(Workout workout) {
    if(cache == null) {
      return;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUT);
    builder.append(workout.getId());
    
    cache.put(builder.toString(), workout);
  }
  
  public void removeWorkout(Long workoutId) {
    if(cache == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_WORKOUT);
    builder.append(workoutId);

    cache.remove(builder.toString());
  }

  @SuppressWarnings("unchecked")
  public List<ExerciseName> getExerciseNames() {
    if(cache == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISENAMES);
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
    builder.append(PREFIX_EXERCISENAMES);
    
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

    Object obj = cache.get(PREFIX_USERS);
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
    
    cache.put(PREFIX_USERS, map);
  }

  /**
   * 
   */
  public void removeUsers() {
    if(cache == null) {
      return;
    }
    
    cache.remove(PREFIX_USERS);
  }

  @SuppressWarnings("unchecked")
  public FoodName getFoodName(Long nameId) {
    if(cache == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME);
    builder.append(nameId);
    Object obj = cache.get(builder.toString());
    
    return (obj instanceof FoodName)? (FoodName)obj : null;
  }
  
  public void addFoodName(FoodName name) {
    if(cache == null) {
      return;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME);
    builder.append(name.getId());
    
    cache.put(builder.toString(), name);
  }

  public Time getTime(Long timeId) {
    if(cache == null) {
      return null;
    }
    
    //time
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIME);
    builder.append(timeId);
    Object obj = cache.get(builder.toString());
    
    Time t = null;
    if(obj != null && obj instanceof Time) {
      t = (Time)obj;
    }
    
    return t;
  }
  
  public void addTime(Time time) {
    if(cache == null) {
      return;
    }
    
    //time
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIME);
    builder.append(time.getId());
    cache.put(builder.toString(), time);
    
  }
  
  public void removeTime(Long timeId) {
    if(cache == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIME);
    builder.append(timeId);

    cache.remove(builder.toString());
  }

  public Meal getMeal(Long mealId) {
    if(cache == null) {
      return null;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_MEAL);
    builder.append(mealId);
    Object obj = cache.get(builder.toString());
    
    Meal t = null;
    if(obj != null && obj instanceof Meal) {
      t = (Meal)obj;
    }
    
    return t;
  }
  
  public void addMeal(Meal meal) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_MEAL);
    builder.append(meal.getId());
    cache.put(builder.toString(), meal);
    
  }
  
  public void removeMeal(Long mealId) {
    if(cache == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_MEAL);
    builder.append(mealId);

    cache.remove(builder.toString());
  }
  @SuppressWarnings("unchecked")
  public List<FoodName> getFoodNames() {
    if(cache == null) {
      return null;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAMES);
    Object obj = cache.get(builder.toString());

    List<FoodName> names = null;
    
    if(obj instanceof Map) {
      names = new ArrayList<FoodName>();
      Map<Long, FoodName> map = (Map<Long, FoodName>)obj;
      
      Collection<FoodName> c = map.values();
      Iterator<FoodName> itr = c.iterator();
      while(itr.hasNext()) {
        names.add(itr.next());
      }
    }
    
    return names;
  }
  
  public void addFoodNames(List<FoodName> names) {
    if(cache == null) {
      return;
    }
    
    Map<Long, FoodName> map = new HashMap<Long, FoodName>();
    
    //add each name
    for(FoodName name : names) {
      map.put(name.getId(), name);
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAMES);
    
    cache.put(builder.toString(), map);
    
  }
  
  public void removeFoodNames() {
    if(cache == null) {
      return;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAMES);

    cache.remove(builder.toString());
  }

  public int getFoodNameCount(String uid, Long id) {
    if(cache == null) {
      return -1;
    }
    
    //count
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(uid);
    Object obj = cache.get(builder.toString());
        
    return  (obj != null)? (Integer)obj : -1;
  }
  
  public void addFoodNameCount(String uid, Long id, int count) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(uid);
    cache.put(builder.toString(), count);
    
  }

  public void addExerciseNameCount(Long id, String uid, int count) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISE_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(uid);
    cache.put(builder.toString(), count);
    
  }

  public int getExerciseNameCount(Long id, String uid) {
    if(cache == null) {
      return -1;
    }
    
    //count
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_EXERCISE_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(uid);
    Object obj = cache.get(builder.toString());
        
    return  (obj != null)? (Integer)obj : -1;
  }
}
