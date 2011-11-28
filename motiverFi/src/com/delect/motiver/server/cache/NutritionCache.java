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
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class NutritionCache {

  private final static boolean CACHE_ON = true;
  
  private final static String PREFIX_FOOD_NAMES = "fnames";
  private final static String PREFIX_FOOD_NAME_COUNT = "fn_c";
  private final static String PREFIX_TIMES = "fn_t";
  private final static String PREFIX_TIME = "t";
  private final static String PREFIX_MEAL = "m";
  
  private final static int CACHE_EXPIRE_SECONDS = 604800;
  
  private SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionCache.class.getName());
  
  private static Cache cache; 
  private static NutritionCache nutritionCache; 

  @SuppressWarnings("unchecked")
  public static NutritionCache getInstance() {
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
    
    if(nutritionCache == null) {
      nutritionCache = new NutritionCache();
    }
    return nutritionCache;
  }

  public List<TimeJDO> getTimes(String uid, Date date) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIMES);
    builder.append("_");
    builder.append(uid);
    builder.append("_");
    builder.append(fmt.format(date));
    builder.append("_");
    
    Object obj = cache.get(builder.toString());

    List<TimeJDO> times = null;
    
    if(obj instanceof Map) {
      times = new ArrayList<TimeJDO>();
      Map<Long, TimeJDO> map = (Map<Long, TimeJDO>)obj;
      
      Collection<TimeJDO> c = map.values();
      Iterator<TimeJDO> itr = c.iterator();
      while(itr.hasNext()) {
        times.add(itr.next());
      }
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded times ("+uid+", "+date+"): "+times);
    }
    
    return times;
  }

  public void setTimes(String uid, Date date, List<TimeJDO> list) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding times ("+uid+", "+date+"): "+list);
    }
    
    Map<Long, TimeJDO> map = null;
    if(list != null) {
      map = new HashMap<Long, TimeJDO>();
      for(TimeJDO t : list) {
        map.put(t.getId(), t);
      }
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIMES);
    builder.append("_");
    builder.append(uid);
    builder.append("_");
    builder.append(fmt.format(date));
    builder.append("_");
    
    cache.put(builder.toString(), map);
  }


  public TimeJDO getTime(Long timeId) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    //time
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIME);
    builder.append(timeId);
    Object obj = cache.get(builder.toString());
    
    TimeJDO t = null;
    if(obj != null && obj instanceof TimeJDO) {
      t = (TimeJDO)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded single time ("+timeId+"): "+t);
    }
    
    return t;
  }

  public MealJDO getMeal(Long mealId) {   
    
    if(cache == null || !CACHE_ON) {
      return null;
    } 
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_MEAL);
    builder.append(mealId);
    Object obj = cache.get(builder.toString());
    
    MealJDO t = null;
    if(obj != null && obj instanceof MealJDO) {
      t = (MealJDO)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded single meal ("+mealId+"): "+t);
    }
    
    return t;
  }
  
  public void addMeal(MealJDO meal) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving single meal: "+meal);
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_MEAL);
    builder.append(meal.getId());
    cache.put(builder.toString(), meal);
    
  }
  
  public void removeMeal(Long mealId) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing single meal: "+mealId);
    }

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_MEAL);
    builder.append(mealId);

    cache.remove(builder.toString());
  }
  
  @SuppressWarnings("unchecked")
  public List<FoodName> getFoodNames() {
    
    if(cache == null || !CACHE_ON) {
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
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded food names: "+ ((names != null)? names.size() : 0));
    }
    
    return names;
  }
  
  public void setFoodNames(List<FoodName> names) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving food names: "+names.size());
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


  public int getFoodNameCount(UserOpenid user, Long id) {
    
    if(cache == null || !CACHE_ON) {
      return -1;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(user.getUid());
    Object obj = cache.get(builder.toString());

    int c = (obj != null)? (Integer)obj : -1;
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded food name count ("+user.getUid()+", "+id+"): "+c);
    }
    
    return  c;
  }
  
  public void setFoodNameCount(UserOpenid user, Long id, int count) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving food name count ("+user.getUid()+", "+id+", "+count+")");
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(user.getUid());
    cache.put(builder.toString(), count);
    
  }
}
