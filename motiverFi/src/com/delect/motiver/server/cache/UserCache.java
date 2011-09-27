package com.delect.motiver.server.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.delect.motiver.server.jdo.Circle;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class UserCache {
  
  private final static int CACHE_EXPIRE_SECONDS = 604800;
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(UserCache.class.getName());

  private static final String PREFIX_CIRCLE = "u_c";
  
  private static Cache cache; 
  private static UserCache userCache; 

  public static UserCache getInstance() {
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
    
    if(userCache == null) {
      userCache = new UserCache();
    }
    return userCache;
  }

  public void setCircle(int target, String ourUid, String uid, Circle circle) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_CIRCLE);
    builder.append("_");
    builder.append(target);
    builder.append("_");
    builder.append(ourUid);
    builder.append("_");
    builder.append(uid);
    
    if(circle != null) {
      cache.put(builder.toString(), circle);
    }
    else {
      cache.remove(builder.toString());
    }
      
  }
}
