package com.delect.motiver.server.manager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.cache.NutritionCache;
import com.delect.motiver.server.cache.UserCache;
import com.delect.motiver.server.dao.NutritionDAO;
import com.delect.motiver.server.dao.UserDAO;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.exception.ConnectionException;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserManager {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(UserManager.class.getName());

  UserCache cache = UserCache.getInstance();
  UserDAO dao = UserDAO.getInstance();
  private static UserManager man; 

  public static UserManager getInstance() {
    if(man == null) {
      man = new UserManager();
    }
    return man;
  }
  


  /**
   * Returns user. Throws exception if user not found
   * @return null if no user found
   */
  public UserOpenid getUser(ThreadLocal<HttpServletRequest> request) throws ConnectionException {

    String coachModeUid = null;
    
    try {
      String s = request.get().getHeader("coach_mode_uid");
      if(s != null && s.length() > 1) {
        coachModeUid = s;
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error checkin coach mode", e);
      coachModeUid = null;
    }
    
    return _getUid(coachModeUid);
  }
  
  @SuppressWarnings("unchecked")
  private UserOpenid _getUid(String coachModeUid) throws ConnectionException {

    UserOpenid user = null;
    String openId = null;

    UserService userService = UserServiceFactory.getUserService();
    User userCurrent = userService.getCurrentUser();
    
    if(userCurrent != null) {
      openId = userCurrent.getUserId();
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
    
      //if coach mode -> return trainee
      if(coachModeUid != null) {
        if(logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, "Checking if user "+openId+" is coach to "+coachModeUid);
        }
        
        Query q = pm.newQuery(Circle.class);
        q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
        q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
        q.setRange(0,1);
        List<Circle> list = (List<Circle>)q.execute(coachModeUid, openId, Permission.COACH);
        
        if(list.size() > 0) {
          logger.log(Level.FINE, "Is coach!");
          openId = list.get(0).getUid();
        }
      }
      
      if(openId != null) {
        user = pm.getObjectById(UserOpenid.class, openId);
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading user", e);
    }
    finally {
      if(!pm.isClosed()) {
        pm.close();
      }
    }
    
    if(user == null) {
      throw new ConnectionException("_getUid", "");
    }
    
    return user;
  }



  private boolean getPermission(int target, String ourUid, String uid) throws ConnectionException {

    boolean ok = false;
    
    Circle circle = null;
    
    try {
      //if read
      if(target == Permission.READ_TRAINING
          || target == Permission.READ_NUTRITION
          || target == Permission.READ_NUTRITION_FOODS
          || target == Permission.READ_CARDIO
          || target == Permission.READ_MEASUREMENTS) {
        
        circle = dao.getCircle(target, ourUid, uid, true);
      }
      //write permission -> check if coach
      else {
        circle = dao.getCircle(Permission.COACH, ourUid, uid, false);
      }

      //update to cache
      cache.setCircle(target, ourUid, uid, circle);
      
      if(circle != null) {
        ok = true;      
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading permission", e);
      throw new ConnectionException("Error loading permission", e);
    }
    
    return ok;
  }


  /**
   * Checks if user has permission to given target. Throws NoPermissionException if not
   * @param target
   * @param ourUid
   * @param uid
   * @return
   * @throws NoPermissionException
   */
  public void checkPermission(int target, String ourUid, String uid) throws NoPermissionException {

    //if own data -> return always true
    if(ourUid.equals(uid)) {
      return;
    }
    
    UserManager userManager = getInstance();
    
    boolean ok = false;
    
    try {
      ok = userManager.getPermission(target, ourUid, uid);
    } catch (ConnectionException e) {
      logger.log(Level.SEVERE, "Error checking permissions", e);
    }
    
    if(!ok) {
      throw new NoPermissionException(target, ourUid, uid);
    }
    
  }

}
