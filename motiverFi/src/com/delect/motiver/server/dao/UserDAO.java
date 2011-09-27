package com.delect.motiver.server.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.shared.exception.ConnectionException;

public class UserDAO {

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

  public Circle getCircle(int target, String ourUid, String uid, boolean includeAll) throws Exception {

    Circle circle = null;
    
    PersistenceManager pm =  PMF.getUser().getPersistenceManager();
    
    try {      
      StringBuilder builder = new StringBuilder();
      builder.append("openId == openIdParam && ");
      if(includeAll) {
        builder.append("(friendId == friendIdParam || friendId == '-1')");
      }
      else {
        builder.append("friendId == friendIdParam");
      }
      builder.append(" && target == targetParam");
      
      Query q = pm.newQuery(Circle.class);
      q.setFilter(builder.toString());
      q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
      q.setRange(0,1);
      List<Circle> list = (List<Circle>)q.execute(uid, ourUid, target);
      
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

}
