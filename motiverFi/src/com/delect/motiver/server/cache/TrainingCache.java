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

public class TrainingCache {

  private final static String PREFIX_WORKOUT = "w";
  private final static String PREFIX_EXERCISENAMES = "en";
  private final static String PREFIX_EXERCISE_NAME_COUNT = "en_c";
  
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
}
