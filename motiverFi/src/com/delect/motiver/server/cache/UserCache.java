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
import com.delect.motiver.server.jdo.UserOpenid;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class UserCache {
  
  private final static int CACHE_EXPIRE_SECONDS = 604800;
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(UserCache.class.getName());

  private static final String PREFIX_CIRCLE = "u_c";
  private static final String PREFIX_USER = "u_u";
  
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

  public void addCircle(Circle circle) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_CIRCLE);
    builder.append("_");
    builder.append(circle.getTarget());
    builder.append("_");
    builder.append(circle.getUid());
    builder.append("_");
    builder.append(circle.getFriendId());
    
    cache.put(builder.toString(), circle);
      
  }

  public void removeCircle(Circle circle) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_CIRCLE);
    builder.append("_");
    builder.append(circle.getTarget());
    builder.append("_");
    builder.append(circle.getUid());
    builder.append("_");
    builder.append(circle.getFriendId());
    
    cache.remove(builder.toString());
      
  }

  public UserOpenid getUser(String uid) {
    if(cache == null) {
      return null;
    }
    
    //time
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_USER);
    builder.append(uid);
    Object obj = cache.get(builder.toString());
    
    UserOpenid t = null;
    if(obj != null && obj instanceof UserOpenid) {
      t = (UserOpenid)obj;
    }
    
    return t;
  }

  public void setUser(UserOpenid user) {
    if(cache == null) {
      return;
    }
    
    //meal
    StringBuilder builder = new StringBuilder();
    builder.append(PREFIX_USER);
    builder.append(user.getUid());
    cache.put(builder.toString(), user);
    
  }
}
