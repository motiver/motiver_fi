package com.delect.motiver.server.manager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.shared.Permission;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserManager {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(UserManager.class.getName());
  
  private static UserManager dao; 

  public static UserManager getInstance() {
    if(dao == null) {
      dao = new UserManager();
    }
    return dao;
  }
  


  /**
   * Gets openId string
   * @return null if no user found
   */
  public String getUid(ThreadLocal<HttpServletRequest> request) {

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
  static String _getUid(String coachModeUid) {

    logger.log(Level.FINE, "getUid()");

    String openId = null;

    UserService userService = UserServiceFactory.getUserService();
    User userCurrent = userService.getCurrentUser();
    
    if(userCurrent != null) {
      openId = userCurrent.getUserId();
    }
  
    //if coach mode -> return trainee's uid
    if(coachModeUid != null) {
      if(logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "Checking if user "+openId+" is coach to "+coachModeUid);
      }

      PersistenceManager pm =  PMF.get().getPersistenceManager();
      
      try {
        Query q = pm.newQuery(Circle.class);
        q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
        q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
        q.setRange(0,1);
        List<Circle> list = (List<Circle>)q.execute(coachModeUid, openId, Permission.COACH);
        
        if(list.size() > 0) {
          logger.log(Level.FINE, "Is coach!");
          openId = list.get(0).getUid();
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error checkin coach", e);
      }
      finally {
        if(!pm.isClosed()) {
          pm.close();
        }
      }
    }
    
    return openId;
  }
  

}
