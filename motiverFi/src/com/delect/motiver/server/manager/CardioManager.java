package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.cache.CardioCache;
import com.delect.motiver.server.dao.CardioDAO;
import com.delect.motiver.server.dao.helper.CardioSearchParams;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.cardio.Cardio;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.exception.ConnectionException;

public class CardioManager extends AbstractManager {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(CardioManager.class.getName());

  UserManager userManager = UserManager.getInstance();
  CardioCache cache = CardioCache.getInstance();
  CardioDAO dao = CardioDAO.getInstance();
  
  private static CardioManager man; 

  public static CardioManager getInstance() {
    if(man == null) {
      man = new CardioManager();
    }
    return man;
  }

  /**
   * Returns cardio based on key
   * Fetchs also user
   * @param key
   * @return
   * @throws Exception
   */
  private Cardio _getCardio(Long key) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getCardio("+key+")");
    }
    
    if(key == null) {
      return null;
    }
    
    Cardio jdo = cache.getCardio(key);
    
    if(jdo == null) {      
      jdo = dao.getCardio(key);
      jdo.setUser(userManager.getUser(jdo.getUid()));
      
      cache.addCardio(jdo);
    }
    
    return jdo;
  }
  
  public List<Cardio> getCardios(UserOpenid user, int offset, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading cardios ("+offset+", "+uid+")");
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_CARDIO, user.getUid(), uid);
    
    List<Cardio> list = new ArrayList<Cardio>();
    
    try {
      CardioSearchParams params = new CardioSearchParams();
      params.offset = offset;
      params.limit = Constants.LIMIT_CARDIOS;
      params.uid = uid;
      List<Long> keys = dao.getCardios(params);
      
      for(Long key : keys) {
        
        Cardio jdo = _getCardio(key);
        
        //can be null if results are cutted
        if(jdo != null) {
          //check permission
          userManager.checkPermission(Permission.READ_CARDIO, user.getUid(), jdo.getUid());
        }
        
        list.add(jdo);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading cardios", e);
      handleException("CardioManager.getCardios", e);
    }
    
    return list;
  }

}
