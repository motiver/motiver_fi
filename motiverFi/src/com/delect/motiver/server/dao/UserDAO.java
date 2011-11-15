package com.delect.motiver.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.shared.Constants;

public class UserDAO {

  /**
   * Restricted string for aliases
   */
  public static final String[] VALUES_RESTRICTED_ALIASES = new String[] {
    "http","blogs","blogit","admin","motiver","static"
  };
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
  
  private static UserDAO dao; 

  public static UserDAO getInstance() {
    if(dao == null) {
      dao = new UserDAO();
    }
    return dao;
  }

  @SuppressWarnings("unchecked")
  public Circle getCircle(int target, String ourUid, String uid) throws Exception {

    Circle circle = null;
    
    PersistenceManager pm =  PMF.getUser().getPersistenceManager();
    
    try {      
      StringBuilder builder = new StringBuilder();
      builder.append("openId == openIdParam && ");
      builder.append("(friendId == friendIdParam || friendId == '-1')");
      builder.append(" && target == targetParam");
      
      Query q = pm.newQuery(Circle.class);
      q.setFilter(builder.toString());
      q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
      q.setRange(0,1);
      List<Circle> list = (List<Circle>)q.execute(ourUid, uid, target);
      
      if(list.size() > 0) {
        circle = list.get(0);
      }
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return circle;
  }

  public void addCircle(Circle circle) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      pm.makePersistent(circle);
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }

  public boolean removeCircle(Circle model) throws Exception {
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {

      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          Circle t = pm.getObjectById(Circle.class, model.getId());
          
          if(t != null) {
            
            pm.deletePersistent(t);
            tx.commit();
  
            ok = true;
            break;
          }
          
        }
        catch (Exception e) {
          if (tx.isActive()) {
            tx.rollback();
          }
          logger.log(Level.WARNING, "Error deleting circle", e);
          
          //retries used
          if (retries == 0) {          
            throw e;
          }
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
    
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return ok;
  }

  @SuppressWarnings("unchecked")
  public List<Circle> getCircles(String uid, int target) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    List<Circle> list = null;
    
    try {
      
      Query q = pm.newQuery(Circle.class);
      q.setFilter("openId == openIdParam && target == targetParam");
      q.declareParameters("java.lang.String openIdParam, java.lang.Integer targetParam");
      List<Circle> jdo = (List<Circle>) q.execute(uid, target);
      
      if(jdo != null) {
        list = (List<Circle>) pm.detachCopyAll(jdo);
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  public UserOpenid getUser(String uid) throws Exception {

    UserOpenid user = null;
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      user = pm.getObjectById(UserOpenid.class, uid);
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return user;
  }

  /**
   * Return single users
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<UserOpenid> getUsers() throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    List<UserOpenid> n = new ArrayList<UserOpenid>();
    
    try {
      
      int i = 0;
      while(true){
        Query q = pm.newQuery(UserOpenid.class);
        q.setOrdering("name ASC");
        q.setRange(i, i+100);
        List<UserOpenid> u = (List<UserOpenid>) q.execute();
        n.addAll(u);
        
        if(u.size() < 100) {
          break;
        }
        i += 100;
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return n;
  }

  @SuppressWarnings("unchecked")
  public void updateUser(UserOpenid user) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      UserOpenid u = null;
      
      //throws exception if new user
      //TODO handle this better
      try {
        u = pm.getObjectById(UserOpenid.class, user.getId());
      } catch (Exception e) {
        logger.log(Level.WARNING, "User "+user.getId()+" not found");
      }

      //update old user
      if(u != null) {
        
        //save old alias so we can restore it if necessary
        String aliasOld = u.getAlias();
        boolean resetAlias = false;
        
        u.update(user);
      
        
        //check that alias not already taken
        String alias = u.getAlias();
        if(alias != null) {
          
          //check if restricted value
          for(String s : VALUES_RESTRICTED_ALIASES) {
            if(s.equals(alias)) {
              resetAlias = true;
              break;
            }
          }
          
          //if alias set and it have been changed
          if( !resetAlias && u.getAlias() != null && !u.getAlias().equals(user.getAlias()) ) {
            
            //check if not already found
            Query qAlias = pm.newQuery(UserOpenid.class, "alias == aliasParam && openId != openIdParam");
            qAlias.declareParameters("java.lang.String aliasParam, java.lang.String openIdParam");
            qAlias.setRange(0, 1);
            List<UserOpenid> aliases = (List<UserOpenid>) qAlias.execute(u.getAlias(), u.getUid());
            //not found
            if(aliases.size() > 0) {
              resetAlias = true;
            }
          }
          
          if(resetAlias) {
            u.setAlias(aliasOld);
          }
        }
      }
      //create new user
      else {
        pm.makePersistent(user);
      }
      
      tx.commit();

      //update user which was given as parameter
      if(u != null) {
        user.update(u); 
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if(tx.isActive()) {
        tx.rollback();
      }
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }

}
