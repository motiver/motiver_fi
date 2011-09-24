/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
/**
 * 
 */
package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.delect.motiver.server.cache.WeekCache;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.UserModel;
import com.google.appengine.api.users.User;

/**
 * @author Antti
 *
 */
public final class UserManagerOld {

  /**
   * Restricted string for aliases
   */
  public static final String[] VALUES_RESTRICTED_ALIASES = new String[] {
    "http","blogs","blogit","admin","motiver","static"
  };
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(UserManagerOld.class.getName()); 

  
  /**
   * Returns single user
   * @param pm
   * @param uid
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static UserModel getUserModel(PersistenceManager pm, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading user: "+uid);
    }
    
    UserModel user = null;
    
    //load from cache
    WeekCache cache = new WeekCache();
    List<UserOpenid> users = cache.getUsers();

    //not found
    if(users == null) {    
      if(logger.isLoggable(Level.FINER)) {
        logger.log(Level.FINER, "Not found from cache");
      }

      //load in chunks
      users = new ArrayList<UserOpenid>();
      int i = 0;
      while(true){
        Query q = pm.newQuery(UserOpenid.class);
        q.setRange(i, i+100);
        List<UserOpenid> u = (List<UserOpenid>) q.execute();
        users.addAll(u);
        
        if(u.size() < 100) {
          break;
        }
        i += 100;
      }
      
      //save to cache
      cache.setUsers(users);
    }
    
    if(users != null) {
      for(UserOpenid u : users) {
        //if uid matches
        if(u.getId() != null && u.getId().equals(uid)) {
          user = UserOpenid.getClientModel(u);
          break;
        }
        //if alias matches
        else if(u.getAlias() != null && u.getAlias().equals(uid)) {
          user = UserOpenid.getClientModel(u);
          break;
        }
      }
    }
    
    return user;
  }

  /**
   * Returns single user
   * @param pm
   * @param uid
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static UserModel saveUserModel(PersistenceManager pm, UserModel u) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Saving user: "+u.getNickName());
    }
    
    UserOpenid userOpenid = pm.getObjectById(UserOpenid.class, u.getUid());
    
    //data found
    if(userOpenid != null) {
      userOpenid.setLocale(u.getLocale());
      userOpenid.setDateFormat(u.getDateFormat());
      userOpenid.setMeasurementSystem(u.getMeasurementSystem());
      userOpenid.setTimeFormat(u.getTimeFormat());
      userOpenid.setShareTraining(u.getShareTraining());
      userOpenid.setShareNutrition(u.getShareNutrition());
      userOpenid.setShareNutritionFoods(u.getShareNutritionFoods());
      userOpenid.setShareCardio(u.getShareCardio());
      userOpenid.setShareMeasurement(u.getShareMeasurement());
      userOpenid.setShareCoach(u.getShareCoach());
      
      //if alias changed -> check that alias not already taken
      String alias = u.getAlias();
      if(alias != null) {
        alias = alias.toLowerCase();
        
        //check if restricted value
        boolean restricted = false;
        for(String s : VALUES_RESTRICTED_ALIASES) {
          if(s.equals(alias)) {
            restricted = true;
            break;
          }
        }
        if( !restricted ) {
          //if changed
          if((userOpenid.getAlias() == null && userOpenid.getAlias() != null)
              || (userOpenid.getAlias() != null && !userOpenid.getAlias().equals(alias))) {
            Query qAlias = pm.newQuery(UserOpenid.class, "alias == aliasParam && openId != openIdParam");
            qAlias.declareParameters("java.lang.String aliasParam, java.lang.String openIdParam");
            qAlias.setRange(0, 1);
            List<UserOpenid> aliases = (List<UserOpenid>) qAlias.execute(u.getAlias(), u.getUid());
            //not found
            if(aliases.size() > 0) {
              //restore original value
              alias = userOpenid.getAlias();
            }
            
          }
          userOpenid.setAlias(alias);
        }
      }

      pm.makePersistent(userOpenid);
      
      u = UserOpenid.getClientModel(userOpenid);
      
      //remove cache
      //TODO needs improving
      WeekCache cache = new WeekCache();
      cache.removeUsers();
    }
    
    return u;
  }

  /**
   * Adds user 
   * @param pm
   * @param userCurrent
   * @return
   */
  @SuppressWarnings("unchecked")
  public static UserModel addUser(PersistenceManager pm, User userCurrent) {

    if(logger.isLoggable(Level.FINER) && userCurrent != null) {
      logger.log(Level.FINER, "Adding user: "+userCurrent.getEmail());
    }

    UserModel user = null;
    
    //check if user has data in OUR DATABASE
    Query q = pm.newQuery(UserOpenid.class, "id == openIdParam");
    q.declareParameters("java.lang.Long openIdParam");
    q.setRange(0,1);
    List<UserOpenid> users = (List<UserOpenid>) q.execute(userCurrent.getUserId());
    
    UserOpenid u;
    
    //data found
    if(users.size() > 0) {
      u = users.get(0);
    }
    //no data -> add new data for this user
    else {
      u = new UserOpenid();
      u.setId(userCurrent.getUserId());
    }
    
    //if user added
    if(u != null) {
      
      //save facebook data
      u.setLocale("fi_FI");
      u.setBanned(false);
      u.setNickName(userCurrent.getNickname());
      u.setEmail(userCurrent.getEmail());

      pm.makePersistent(u);
      pm.flush();
      user = UserOpenid.getClientModel(u);
              
      //check if someone has set user as coach
      q = pm.newQuery(Circle.class);
      q.setFilter("friendId == friendIdParam && target == targetParam");
      q.declareParameters("java.lang.String friendIdParam, java.lang.Integer targetParam");
      q.setRange(0,1);
      List<Circle> cicles = (List<Circle>) q.execute(user.getUid(), Permission.COACH);
      user.setCoach(cicles.size() > 0);

      //remove cache
      //TODO needs improving
      WeekCache cache = new WeekCache();
      cache.removeUsers();
      
    }
    
    return user;
    
  }

  /**
   * Gives friend (friendid) permission to given target to user's (uid) data
   * @param pm
   * @param uid
   * @param friendid
   * @param target
   * @return
   */
  @SuppressWarnings("unchecked")
  public static boolean addUserToCircle(PersistenceManager pm, String uid, String friendid, int target) {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Adding user to circle: uid="+uid+", friendid="+friendid+", target="+target);
    }
    
    boolean ok = true;
    
    //check if permission already found
    Query q = pm.newQuery(Circle.class);
    q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
    q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
    q.setRange(0,1);
    List<Circle> list = (List<Circle>)q.execute(uid, friendid, target);
    
    //if no previous permissions found
    if(list.size() != 0) {
      pm.deletePersistent(list.get(0));
      ok = true;
    }
    
    //remove users from cache
    //TODO needs improving
    WeekCache cache = new WeekCache();
    cache.removeUsers();
    
    return ok;
  }

  /**
   * Removes friend's (friendid) permission to given target to user's (uid) data
   * @param pm
   * @param uid
   * @param friendid
   * @param target
   * @return
   */
  @SuppressWarnings("unchecked")
  public static boolean removeUserToCircle(PersistenceManager pm, String uid, String friendid, int target) {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing user from circle: uid="+uid+", friendid="+friendid+", target="+target);
    }
    
    boolean ok = true;
    
    //check if permission already found
    Query q = pm.newQuery(Circle.class);
    q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
    q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
    q.setRange(0,1);
    List<Circle> list = (List<Circle>)q.execute(uid, friendid, target);
    
    //if no previous permissions found
    if(list.size() == 0) {
      Circle permissionNew = new Circle(target, uid, friendid);
      pm.makePersistent(permissionNew);
      ok = true;
    }
    
    //remove users from cache
    //TODO needs improving
    WeekCache cache = new WeekCache();
    cache.removeUsers();
    
    return ok;
  }
  
}
