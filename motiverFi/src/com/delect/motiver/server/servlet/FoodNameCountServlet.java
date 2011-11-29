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
package com.delect.motiver.server.servlet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.delect.motiver.server.cache.WeekCache;
import com.delect.motiver.server.jdo.FoodNameCount;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FoodNameCountServlet extends RemoteServiceServlet {
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(FoodNameCountServlet.class.getName()); 

  private static final long serialVersionUID = 5384098111620397L;

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    WeekCache cache = new WeekCache();
    
    response.setContentType("text/html");
    
    try {
      
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      for(UserOpenid user : users) {
        try {
          
//          response.getWriter().write(user.getEmail()+"<br>");  
          Hashtable<Long, Integer> tableFoods = new Hashtable<Long, Integer>();
        
          //get times (in chunks)
          int countTimes = 0;
          while(true) {
            Query qT = pm.newQuery(TimeJDO.class);
            qT.setFilter("openId == openIdParam");
            qT.declareParameters("java.lang.String openIdParam");
            qT.setRange(countTimes, countTimes+100);
            List<TimeJDO> times = (List<TimeJDO>) qT.execute(user.getUid());

            int s = times.size();
//            response.getWriter().write("Times found: "+s+" ("+countTimes+")<br>");
            
            if(s == 0) {
              break;
            }
            
            countTimes += s;
            
            //go through each workouts
            for(TimeJDO t : times) {
              for(FoodJDO f : t.getFoods()) {
                final long nameId = f.getNameId();
                
                //if name found
                if(nameId > 0) {
                  //if id found -> add one to count
                  int count = 0;
                  if(tableFoods.containsKey(nameId)) {
                    count = tableFoods.get(nameId);
                  }
                  count++;
                  tableFoods.put(nameId, count);
                }
              }
              
              //get foods
              List<Object> ids = new ArrayList<Object>();
              for (Key key : t.getMealsKeys()) {
                 ids.add(pm.newObjectIdInstance(MealJDO.class, key));
              }
              List<MealJDO> meals = (List<MealJDO>) pm.getObjectsById(ids);
              
              for(MealJDO m : meals) {

                for(FoodJDO f : m.getFoods()) {
                  final long nameId = f.getNameId();
                  
                  //if name found
                  if(nameId > 0) {
                    //if id found -> add one to count
                    int count = 0;
                    if(tableFoods.containsKey(nameId)) {
                      count = tableFoods.get(nameId);
                    }
                    count++;
                    tableFoods.put(nameId, count);
                  }
                }
              }
            }
          }
          
          //save each count to datastore
          List<FoodNameCount> counts = new ArrayList<FoodNameCount>();
          
          Set<Long> set = tableFoods.keySet();
          Iterator<Long> itr = set.iterator();
          for(int i = 0; i < tableFoods.size(); i++) {
            Long nameId = itr.next();
//            response.getWriter().write("      "+nameId + ": " + tableFoods.get(nameId)+"<br>");
            
            //Create model
            int count = tableFoods.get(nameId);
            FoodNameCount model = new FoodNameCount(nameId, count, user.getUid());
            counts.add(model);

            //update cache
            cache.addExerciseNameCount(nameId, user.getUid(), count);
          }
          
          pm.makePersistentAll(counts);
          pm.flush();
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Error loading data from user: "+user.getUid(), e);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading data", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }
}
