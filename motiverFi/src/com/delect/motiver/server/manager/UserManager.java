package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.delect.motiver.server.cache.UserCache;
import com.delect.motiver.server.dao.UserDAO;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.Constants;
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
  
  private UserOpenid _getUid(String coachModeUid) throws ConnectionException {

    UserOpenid user = null;
    String openId = null;

    UserService userService = UserServiceFactory.getUserService();
    User userCurrent = userService.getCurrentUser();
    
    if(userCurrent != null) {
      openId = userCurrent.getUserId();
    }

    try {
    
      //if coach mode -> return trainee
      if(coachModeUid != null) {
        if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
          logger.log(Level.FINE, "Checking if user "+openId+" is coach to "+coachModeUid);
        }
        
        Circle circle = _getCircle(Permission.COACH, coachModeUid, openId);
        
        if(circle != null) {
          logger.log(Constants.LOG_LEVEL_MANAGER, "Is coach!");
          openId = circle.getUid();
        }
      }
      
      if(openId != null) {
        user = getUser(openId);
        
        //if not found -> create
        if(user == null) {
          user = createUser(userCurrent);
        }
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading user", e);
    }
    
    if(user == null) {
      throw new ConnectionException("_getUid", "");
    }
    
    return user;
  }



  private Circle _getCircle(int target, String uid, String friendUid) throws Exception {
    
    Circle circle = cache.getCircle(target, uid, friendUid);
    
    if(circle == null) {
      circle = dao.getCircle(target, uid, friendUid);
      
      if(circle != null) {
        cache.addCircle(circle);
      }
    }
    
    return circle;
  }



  private boolean _checkPermission(int target, String ourUid, String uid) throws ConnectionException {

    boolean ok = false;
    
    Circle circle = null;
    
    try {
      //if read
      if(target == Permission.READ_TRAINING
          || target == Permission.READ_NUTRITION
          || target == Permission.READ_NUTRITION_FOODS
          || target == Permission.READ_CARDIO
          || target == Permission.READ_MEASUREMENTS) {
        
        circle = _getCircle(target, uid, ourUid);
      }
      //write permission -> check if coach
      else {
        circle = _getCircle(Permission.COACH, uid, ourUid);
      }
      
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
    
    boolean ok = false;
    
    try {
      ok = _checkPermission(target, ourUid, uid);
    } catch (ConnectionException e) {
      logger.log(Level.SEVERE, "Error checking permissions", e);
    }
    
    if(!ok) {
      throw new NoPermissionException(target, ourUid, uid);
    }
    
  }


  /**
   * Return true if user has permission to given target.
   * @param target
   * @param ourUid
   * @param uid
   * @return
   * @throws NoPermissionException
   */
  public boolean hasPermission(int target, String ourUid, String uid) {

    //if own data -> return always true
    if(ourUid.equals(uid)) {
      return true;
    }
    
    boolean ok = false;
    
    try {
      ok = _checkPermission(target, ourUid, uid);
    } catch (ConnectionException e) {
      logger.log(Level.SEVERE, "Error checking permissions", e);
    }
    
    return ok;    
  }

  public void addUserToCircle(UserOpenid user, Circle circle) throws ConnectionException {
    
    try {
      
      circle.setUid(user.getUid());      
      dao.addCircle(circle);
      
      cache.addCircle(circle);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding user to circle", e);
      throw new ConnectionException("Error adding user to circle", e);
    }    
  }

  public void removeUserFromCircle(UserOpenid user, int target, String uid) throws ConnectionException {
    
    try {

      Circle circle = dao.getCircle(target, user.getUid(), uid);
      if(circle != null) {
        dao.removeCircle(circle);
      
        cache.removeCircle(circle);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding user to circle", e);
      throw new ConnectionException("Error adding user to circle", e);
    } 
  }



  public List<UserOpenid> getUsersFromCircle(UserOpenid user, int target) throws ConnectionException {

    List<UserOpenid> list = new ArrayList<UserOpenid>();
    
    try {

      List<Circle> circles = dao.getCircles(user.getUid(), target);
      
      if(circles != null) {
        for(Circle circle : circles) {
                    
          if(!circle.getFriendId().equals("-1")) {
            UserOpenid u = getUser(circle.getFriendId());
            
            if(u != null) {
              list.add(u);
            }
          }
          //all users
          else {
            list.add(new UserOpenid("-1"));
          }
          
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading users", e);
      throw new ConnectionException("Error loading users", e);
    } 
    
    return list;
  }

  /**
   * Returns user based on given uid. Returns null if user not found
   * @param uid
   * @return
   * @throws Exception
   */
  public UserOpenid getUser(String uid) {
    
    UserOpenid u = null;
    
    try {
      u = cache.getUser(uid);
      
      if(u == null) {
        u = dao.getUser(uid);

        if(u != null) {
          cache.setUser(u);
        }
      }
    } catch (Exception e) {
      logger.log(Level.WARNING, "User "+uid+" not found");
    }
    
    return u;
  }


  public List<UserOpenid> searchUsers(UserOpenid user, String query, int index) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching users ("+index+")");
    }

    List<UserOpenid> list = new ArrayList<UserOpenid>();
    
    try {

      //split query string
      String[] arr = query.split(" ");

      //load from cache
      List<UserOpenid> listAll = dao.getUsers();

      int i = 0;
      for(UserOpenid m : listAll) {
        
        if(!m.getUid().equals(user.getUid()))  {

          if(i >= index) {
            
            final String name = m.getNickName();
            
            //filter by query
            boolean match = false;
            for(String s : arr) {
              match = name.toLowerCase().contains( s.toLowerCase() );
              if(match) {
                break;
              }
            }
            
            if(match) {              
              list.add(m);
            }
          }
          
          i++;
          
        }
        
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise names", e);
      throw new ConnectionException("Error adding exercise names", e);
    }
    
    
    return list;
    
  }

  public void saveUser(UserOpenid user, UserOpenid updatedModel) throws ConnectionException {
    
    
    try {
      //to make sure we update our model
      updatedModel.setUid(user.getUid());
      
      dao.updateUser(updatedModel);
        
      //update cache
      cache.setUser(updatedModel);
        
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error saving user", e);
      throw new ConnectionException("Error saving user", e);
    }
  }

  /**
   * Creates new user
   * @param user
   * @param userAppengine
   * @throws ConnectionException
   */
  public UserOpenid createUser(User userAppengine) throws ConnectionException {
    
    UserOpenid user = new UserOpenid();
    
    try {
      
      user.update(userAppengine);
      
      dao.updateUser(user);
        
      //update cache
      cache.setUser(user);
        
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error creating user", e);
      throw new ConnectionException("Error creating user", e);
    }
    
    return user;
  }

}
