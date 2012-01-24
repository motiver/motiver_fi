package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.delect.motiver.server.cache.UserCache;
import com.delect.motiver.server.dao.UserDAO;
import com.delect.motiver.server.dao.helper.CircleSearchParams;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.exception.ConnectionException;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.delect.motiver.shared.exception.NotLoggedInException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.prodeagle.java.counters.Counter;

public class UserManager extends AbstractManager {

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
  public UserOpenid getUser(ThreadLocal<HttpServletRequest> request) throws NotLoggedInException, ConnectionException {
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading user: "+request);
    }

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
    
    //increment
    HttpSession session = request.get().getSession();
    if(session == null || session.getAttribute("newLogin") == null) {
      session.setAttribute("newLogin", "true");
      Counter.increment("User.Login");
    }
    
    return _getUid(request, coachModeUid);
  }
  
  private UserOpenid _getUid(ThreadLocal<HttpServletRequest> request, String coachModeUid) throws NotLoggedInException, ConnectionException {
    
    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading user: "+coachModeUid);
    }

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
        if(logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "Checking if user "+openId+" is coach to "+coachModeUid);
        }
        
        Circle circle = _getCircle(Permission.COACH, coachModeUid, openId);
        
        if(circle != null) {
          logger.log(Level.FINER, "Is coach!");
          openId = circle.getUid();
        }
      }
      
      if(openId != null) {
        user = getUser(openId);
        
        //if not found -> create
        if(user == null) {
          //get locale
          String locale = Constants.LOCALE_DEFAULT;
          Locale loc = request.get().getLocale();
          if(loc != null) {
            locale = loc.getLanguage()+"_"+loc.getCountry();
          }
          
          user = createUser(userCurrent, locale);
        }
          
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading user", e);
    }
    
    if(user == null) {
      handleException("UserManager._getUid", new NotLoggedInException());
    }
    
    return user;
  }



  private Circle _getCircle(int target, String uid, String friendUid) throws Exception {
    
    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading permission circle ("+target+", "+uid+", "+friendUid+")");
    }
    
    Circle circle = cache.getCircle(target, uid, friendUid);
    
    if(circle == null) {
      CircleSearchParams params = new CircleSearchParams(target, uid, friendUid);
      params.limit = 1;
      List<Circle> list = dao.getCircle(params);
      
      if(list != null && list.size() > 0) {
        circle = list.get(0);
        cache.addCircle(circle);
      }
    }
    
    return circle;
  }



  private boolean _checkPermission(int target, String ourUid, String uid) throws ConnectionException {
    
    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Checking permissions ("+target+", "+ourUid+", "+uid+")");
    }

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
      handleException("UserManager._checkPermission", e);
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
  public void checkPermission(int target, String ourUid, String uid) throws NoPermissionException,ConnectionException {
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Checking permissions ("+target+", "+ourUid+", "+uid+")");
    }

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
      handleException("UserManager.checkPermission", new NoPermissionException(target, ourUid, uid));
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
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Checking if user has permission ("+target+", "+ourUid+", "+uid+")");
    }

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
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding permission to user ("+circle+")");
    }
    
    try {
      
      circle.setUid(user.getUid());      
      dao.addCircle(circle);
      
      cache.addCircle(circle);
      
      //remove user's from cache if set as coach
      if(circle.getTarget().equals(Permission.COACH)) {
        cache.removeUser(circle.getFriendId());
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding user to circle", e);
      handleException("UserManager.addUserToCircle", e);
    }    
  }

  public void removeUserFromCircle(UserOpenid user, int target, String uid) throws ConnectionException {
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing permission from user ("+target+", "+uid+")");
    }
    
    try {

      CircleSearchParams params = new CircleSearchParams(target, user.getUid(), uid);
      params.limit = 1;
      List<Circle> list = dao.getCircle(params);
      
      if(list != null && list.size() > 0) {
        Circle circle = list.get(0);
        cache.removeCircle(circle);
        
        dao.removeCircle(circle);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding user to circle", e);
      handleException("UserManager.removeUserFromCircle", e);
    } 
  }



  public List<UserOpenid> getUsersFromCircle(UserOpenid user, int target) throws ConnectionException {
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading permissions ("+target+")");
    }

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
      handleException("UserManager.getUsersFromCircle", e);
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
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading single user ("+uid+")");
    }
    
    UserOpenid u = null;
    
    try {
      u = cache.getUser(uid);
      
      if(u == null) {
        u = dao.getUser(uid);
        
        if(u != null) {

          //check if someone has set this user as coach
          Circle circle = _getCircle(Permission.COACH, null, u.getUid());
          if(circle != null)
            u.setCoach(true);
          
          cache.setUser(u);
        }
      }
    } catch (Exception e) {
      logger.log(Level.WARNING, "User "+uid+" not found");
    }
    
    return u;
  }


  public List<UserOpenid> searchUsers(UserOpenid user, String query, int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Searching users ("+index+", "+query+")");
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
      handleException("UserManager.searchUsers", e);
    }
    
    
    return list;
    
  }

  public void saveUser(UserOpenid user, UserOpenid updatedModel) throws ConnectionException {
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Saving user ("+user.getUid()+", "+updatedModel+")");
    }
    
    
    try {
      //to make sure we update our model
      updatedModel.setUid(user.getUid());
      
      dao.updateUser(updatedModel);
        
      //update cache
      cache.setUser(updatedModel);
        
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error saving user", e);
      handleException("UserManager.saveUser", e);
    }
  }

  /**
   * Creates new user
   * @param user
   * @param userAppengine
   * @throws ConnectionException
   */
  public UserOpenid createUser(User userAppengine, String locale) throws ConnectionException {
    
    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Creating new user ("+userAppengine+")");
    }
    
    //new user
    Counter.increment("User.New");
    
    UserOpenid user = new UserOpenid();
    
    try {
      
      user.update(userAppengine);
      user.setLocale(locale);
      
      dao.updateUser(user);
        
      //update cache
      cache.setUser(user);
        
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error creating user", e);
      handleException("UserManager.createUser", e);
    }
    
    return user;
  }

}
