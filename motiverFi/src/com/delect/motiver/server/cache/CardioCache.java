package com.delect.motiver.server.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.delect.motiver.server.jdo.cardio.Cardio;
import com.delect.motiver.server.service.MyServiceImpl;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.prodeagle.java.counters.Counter;

public class CardioCache {

  private final static boolean CACHE_ON = true;
  
  private final static int CACHE_EXPIRE_SECONDS = 604800;
  
  private final static String PREFIX_CARDIO = "cc_c";
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(CardioCache.class.getName());
  
  private static Cache cache; 
  private static CardioCache cardioCache; 

  @SuppressWarnings("unchecked")
  public static CardioCache getInstance() {
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
    
    if(cardioCache == null) {
      cardioCache = new CardioCache();
    }
    return cardioCache;
  }

  public Cardio getCardio(Long cardioId) {
    
    if(cache == null || !CACHE_ON) {
      return null;
    }
    
    //cardio
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_CARDIO);
    builder.append(cardioId);
    Object obj = cache.get(builder.toString());
    
    Cardio t = null;
    if(obj != null && obj instanceof Cardio) {

      //prodeagle counter
      Counter.increment("Cache.Cardio");
      
      t = (Cardio)obj;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loaded cardio ("+cardioId+"): "+t);
    }
    
    return t;
  }
  
  public void addCardio(Cardio cardio) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving single cardio: "+cardio);
    }
    
    //cardio
    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_CARDIO);
    builder.append(cardio.getId());
    cache.put(builder.toString(), cardio);
    
  }
  
  public void removeCardio(Long cardioId) {
    
    if(cache == null || !CACHE_ON) {
      return;
    }
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing single cardio: "+cardioId);
    }

    StringBuilder builder = MyServiceImpl.getStringBuilder();
    builder.append(PREFIX_CARDIO);
    builder.append(cardioId);

    cache.remove(builder.toString());
  }

}
