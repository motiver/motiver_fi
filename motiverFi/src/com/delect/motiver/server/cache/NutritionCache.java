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
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class NutritionCache {

  private final static String PREFIX_FOOD_NAMES = "fnames";
  private final static String PREFIX_FOOD_NAME = "fn";
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

  public List<Time> getTimes(String uid, Date date) {

    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_TIMES);
    builder.append("_");
    builder.append(uid);
    builder.append("_");
    builder.append(fmt.format(date));
    builder.append("_");
    
    Object obj = cache.get(builder.toString());

    List<Time> times = null;
    
    if(obj instanceof Map) {
      times = new ArrayList<Time>();
      Map<Long, Time> map = (Map<Long, Time>)obj;
      
      Collection<Time> c = map.values();
      Iterator<Time> itr = c.iterator();
      while(itr.hasNext()) {
        times.add(itr.next());
      }
    }
    
    return times;
  }

  public void setTimes(String uid, Date date, List<Time> list) {
    
    Map<Long, Time> map = null;
    if(list != null) {
      map = new HashMap<Long, Time>();
      for(Time t : list) {
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
  
  public void setFoodNames(List<FoodName> names) {
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


  public int getFoodNameCount(UserOpenid user, Long id) {
    if(cache == null) {
      return -1;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(user.getUid());
    Object obj = cache.get(builder.toString());
        
    return  (obj != null)? (Integer)obj : -1;
  }
  
  public void setFoodNameCount(UserOpenid user, Long id, int count) {
    if(cache == null) {
      return;
    }
    
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_FOOD_NAME_COUNT);
    builder.append(id);
    builder.append("_");
    builder.append(user.getUid());
    cache.put(builder.toString(), count);
    
  }
}
