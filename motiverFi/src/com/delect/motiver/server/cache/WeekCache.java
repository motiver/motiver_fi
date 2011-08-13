/**
 * 
 */
package com.delect.motiver.server.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.service.MyServiceImpl;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

/**
 * @author Antti
 *
 */
public final class WeekCache {
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MyServiceImpl.class.getName()); 
	
	private final static int CACHE_EXPIRE_SECONDS = 604800;
	
	private static Cache cache;
	private static Map props = new HashMap();
    
  @SuppressWarnings("unchecked")
	public static Cache get() {
  	try {
			if(cache == null) {
			    props.put(GCacheFactory.EXPIRATION_DELTA, CACHE_EXPIRE_SECONDS);
				CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			    cache = cacheFactory.createCache(props);
			}
		} catch (CacheException e) {
			logger.log(Level.SEVERE, "Error creating cache", e);
		}
		
  	return cache;
  }
}
