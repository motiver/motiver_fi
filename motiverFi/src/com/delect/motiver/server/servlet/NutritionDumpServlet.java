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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.FoodInMeal;
import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.server.FoodInTime;
import com.delect.motiver.server.Meal;
import com.delect.motiver.server.MealInTime;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.Time;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class NutritionDumpServlet extends RemoteServiceServlet {
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionDumpServlet.class.getName()); 

  private static final long serialVersionUID = 5384098111620397L;

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    response.setContentType("text/html");
    
    try {
      
      PrintWriter writer = response.getWriter();
      
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      for(UserOpenid user : users) {
        try {
          
        response.getWriter().write(user.getEmail()+"<br>"); 
      
        //get times (in chunks)
        int countTimes = 0;
        while(true) {
          Query qT = pm.newQuery(Time.class);
          qT.setFilter("openId == openIdParam");
          qT.declareParameters("java.lang.String openIdParam");
          qT.setRange(countTimes, countTimes+100);
          List<Time> times = (List<Time>) qT.execute(user.getUid());

          int s = times.size();
          
          if(s == 0) {
            break;
          }
          
          countTimes += s;
          
          //go through each workouts
          for(Time t : times) {
            response.getWriter().write("Time: "+t+"<br>");
            
            TimeJDO tNew = new TimeJDO();
            tNew.setDate(t.getDate());
            tNew.setTime(tNew.getTime());
            tNew.setUid(t.getUid());  
            pm.makePersistent(tNew);          
            
            List<Key> keys = new ArrayList<Key>();
            for(FoodInTime f : t.getFoods()) {
              FoodJDO fNew = addFood(writer, f, t.getUid());              
              keys.add(fNew.getKey());
            }
            tNew.setFoodsKeys(keys);

            List<Key> keysMeals = new ArrayList<Key>();
            for(MealInTime m : t.getMeals()) {              
              MealJDO mNew = addMeal(writer, m, tNew.getId(), t.getUid());
              keysMeals.add(mNew.getKey());
            }
            tNew.setMealsKeys(keysMeals);
            pm.flush();
          }
        }
        

        //get meals (in chunks)
        int countMeals = 0;
        while(true) {
          Query qT = pm.newQuery(Meal.class);
          qT.setFilter("openId == openIdParam && time == null");
          qT.declareParameters("java.lang.String openIdParam");
          qT.setRange(countMeals, countMeals+100);
          List<Meal> meals = (List<Meal>) qT.execute(user.getUid());

          int s = meals.size();
          
          if(s == 0) {
            break;
          }
          
          countMeals += s;
          
          //go through each workouts
          for(Meal t : meals) {
            MealJDO mNew = addMeal(writer, t);
          }
        }
        
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

  private MealJDO addMeal(PrintWriter writer, MealInTime m, long timeId, String uid) {
    writer.write("Meal: "+m+"<br>");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    MealJDO mNew = new MealJDO();
    mNew.setName(m.getName());
    mNew.setTime(timeId);
    mNew.setUid(uid);  

    ArrayList<Key> keys = new ArrayList<Key>();
    for(FoodInMealTime f : m.getFoods()) {      
      FoodJDO fNew = addFood(writer, f, uid);                
      keys.add(fNew.getKey());
    }
    mNew.setFoodsKeys(keys);
    pm.makePersistent(mNew);
    
    return mNew;
  }

  private MealJDO addMeal(PrintWriter writer, Meal m) {
    writer.write("Meal: "+m+"<br>");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    MealJDO tNew = new MealJDO();
    tNew.setName(m.getName());
    tNew.setUid(m.getUid());  
    pm.makePersistent(tNew);          
    
    List<Key> keys = new ArrayList<Key>();
    for(FoodInMeal f : m.getFoods()) {         
      FoodJDO fNew = addFood(writer, f, m.getUid());                
      keys.add(fNew.getKey());
    }
    tNew.setFoodsKeys(keys);
    
    pm.makePersistent(tNew);

    if (!pm.isClosed()) {
      pm.close();
    } 
    
    return tNew;
  }

  private FoodJDO addFood(PrintWriter writer, FoodInMeal f, String id) {
    writer.write("&nbsp;&nbsp;&nbsp;&nbsp;Food: "+f+"<br>");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    FoodJDO fNew = new FoodJDO();
    fNew.setAmount(f.getAmount());
    fNew.setNameId(f.getNameId());
    fNew.setUid(id);
    
    pm.makePersistent(fNew);

    if (!pm.isClosed()) {
      pm.close();
    } 
    
    
    return fNew;
  }

  private FoodJDO addFood(PrintWriter writer, FoodInTime f, String id) {
    writer.write("&nbsp;&nbsp;&nbsp;&nbsp;Food: "+f+"<br>");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    FoodJDO fNew = new FoodJDO();
    fNew.setAmount(f.getAmount());
    fNew.setNameId(f.getNameId());
    fNew.setUid(id);
    
    pm.makePersistent(fNew);

    if (!pm.isClosed()) {
      pm.close();
    } 
    
    
    return fNew;
  }

  private FoodJDO addFood(PrintWriter writer, FoodInMealTime f, String id) {
    writer.write("&nbsp;&nbsp;&nbsp;&nbsp;Food: "+f+"<br>");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    FoodJDO fNew = new FoodJDO();
    fNew.setAmount(f.getAmount());
    fNew.setNameId(f.getNameId());
    fNew.setUid(id);
    
    pm.makePersistent(fNew);

    if (!pm.isClosed()) {
      pm.close();
    } 
    
    
    return fNew;
  }
}
