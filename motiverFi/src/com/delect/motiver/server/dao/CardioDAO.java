package com.delect.motiver.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.dao.helper.CardioSearchParams;
import com.delect.motiver.server.jdo.cardio.Cardio;
import com.delect.motiver.server.service.MyServiceImpl;
import com.prodeagle.java.counters.Counter;

public class CardioDAO {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(CardioDAO.class.getName());
  
  private static CardioDAO dao; 

  public static CardioDAO getInstance() {
    if(dao == null) {
      dao = new CardioDAO();
    }
    return dao;
  }

  public Cardio getCardio(long cardioId) throws Exception {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading cardio: "+cardioId);
    }

    //prodeagle counter
    Counter.increment("DAO.Cardio");

    Cardio cardio = null;
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Cardio jdo = pm.getObjectById(Cardio.class, cardioId);
      
      if(jdo != null) {
        cardio = pm.detachCopy(jdo);
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return cardio;
  }

  @SuppressWarnings("unchecked")
  public List<Long> getCardios(CardioSearchParams params) throws Exception {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading cardios: "+params);
    }

    List<Long> list = new ArrayList<Long>();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      int c = 0; 
      while(true){
        int offset = params.offset + c;
        int limit = offset + (params.limit - c);
        if(limit - offset > 100) {
          limit = offset + 100; 
        }
        limit++;
        
        Query q = pm.newQuery(Cardio.class);
        StringBuilder builder = MyServiceImpl.getStringBuilder();
        if(params.uid != null) {
          builder.append("openId == openIdParam");
        }

        q.setFilter(builder.toString());
        q.declareParameters("java.lang.String openIdParam");
        q.setRange(offset, limit);
        List<Cardio> cardios = (List<Cardio>) q.execute(params.uid);
              
        //get cardios
        if(cardios != null) {
          for(Cardio m : cardios) {
            
            //if limit reached -> add null value
            if(list.size() >= params.limit) {
              list.add(null);
              break;
            }
            
            list.add(m.getId());
          }
          
          //if enough found or last query didn't return enough rows
          if(list.size() >= params.limit || cardios.size() < limit) {
            break;
          }
          
          c+= 100;
          
        }
        else {
          break;
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
    
    return list;
  }

}
