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

import java.io.IOException;
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

import com.delect.motiver.server.Exercise;
import com.delect.motiver.server.ExerciseNameCount;
import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.server.FoodInTime;
import com.delect.motiver.server.FoodNameCount;
import com.delect.motiver.server.MealInTime;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.Time;
import com.delect.motiver.server.UserOpenid;
import com.delect.motiver.server.Workout;
import com.delect.motiver.server.datastore.StoreNutrition;
import com.extjs.gxt.ui.client.data.Model;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FoodNameCountServlet extends RemoteServiceServlet {
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(StoreNutrition.class.getName()); 

  private static final long serialVersionUID = 5384098111620397L;

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //remove old count values
//      Query qC = pm.newQuery(FoodNameCount.class);
//      List<FoodNameCount> values = (List<FoodNameCount>) qC.execute();
//      pm.deletePersistentAll(values);
      
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      for(UserOpenid user : users) {
        try {
          response.getWriter().write(user.getEmail()+"<br>");
          
          Hashtable<Long, Integer> tableFoods = new Hashtable<Long, Integer>();
          
          //get times
          Query qT = pm.newQuery(Time.class);
          qT.setFilter("openId == openIdParam");
          qT.declareParameters("java.lang.String openIdParam");
          List<Time> times = (List<Time>) qT.execute(user.getUid());

          response.getWriter().write("Times found: "+times.size());
          
          //go through each workouts
          for(Time t : times) {
            for(FoodInTime f : t.getFoods()) {
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
            for(MealInTime m : t.getMeals()) {

              for(FoodInMealTime f : m.getFoods()) {
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
          
          Set<Long> set = tableFoods.keySet();
          Iterator<Long> itr = set.iterator();
          for(int i = 0; i < tableFoods.size(); i++) {
            Long nameId = itr.next();
            response.getWriter().write("      "+nameId + ": " + tableFoods.get(nameId)+"<br>");
            
            Integer count = tableFoods.get(nameId);
            
            //check if found
            q = pm.newQuery(FoodNameCount.class);
            q.setFilter("nameId == nameIdParam && openId == openIdParam");
            q.declareParameters("java.lang.Long nameIdParam, java.lang.String openIdParam");
            q.setRange(0, 1);
            List<FoodNameCount> valueCount = (List<FoodNameCount>) q.execute(nameId, user.getUid());
            if(valueCount.size() > 0) {
              FoodNameCount model = valueCount.get(0);
              model.setCount(count);
            }
            //not found
            else {
              FoodNameCount model = new FoodNameCount(nameId, count, user.getUid());
              pm.makePersistent(model);
            }            
            pm.flush();
          }
          
        } catch (IOException e) {
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
