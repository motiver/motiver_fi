package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.cache.NutritionCache;
import com.delect.motiver.server.dao.NutritionDAO;
import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.exception.ConnectionException;

public class NutritionManager {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionManager.class.getName());

  UserManager userManager = UserManager.getInstance();
  NutritionCache cache = NutritionCache.getInstance();
  NutritionDAO dao = NutritionDAO.getInstance();
  
  private static NutritionManager man; 

  public static NutritionManager getInstance() {
    if(man == null) {
      man = new NutritionManager();
    }
    return man;
  }


  public List<Time> getTimes(Date date, String uid, String ourUid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading times ("+date+")");
    }
    
    //check cache
    List<Time> list = cache.getTimes(uid, date);
    
    if(list == null) {
      list = dao.getTimes(date, uid, ourUid);

      //add to cache
      cache.setTimes(uid, date, list);
    }
  
    return list;
  }


  /**
   * Adds single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public void addFood(Food model, long timeId, long mealId, String uid, String locale) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding new food: "+model);
    }    
          
    try {
      //update uid
      model.setUid(uid);
      
      //if food is in meal (which is in time)
      if(timeId != 0 && mealId != 0) {
        Time time = dao.updateFoodInMealTime(timeId, mealId, model, uid);
        cache.setTimes(uid, time.getDate(), null);  //clear day's cache
      }
      //if added to some time -> save key
      else if(timeId != 0) {
        Time time = dao.updateFoodInTime(timeId, model, uid);
        cache.setTimes(uid, time.getDate(), null);  //clear day's cache
      }
      //if added to some meal -> save key
      else if(mealId != 0) {
        Meal meal = dao.updateFoodInMeal(mealId, model, uid);
        cache.addMeal(meal);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food", e);
      throw new ConnectionException("Add food", e.getMessage());
    }
  }

  
  public List<Meal> getMeals(int index, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading meals ("+index+")");
    }
    
    List<Meal> list = null;
    
    try {
      list = dao.getMeals(index, uid);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading meals", e);
      throw new ConnectionException("getMeals", e.getMessage());
    }
    
    return list;
  }


  public boolean removeTimes(List<Time> models, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Removing times");
    }
    
    if(models.size() == 0) {
      return false;
    }
    
    boolean ok = false;
    
    try {
      
      //remove cache
      //assume that all times have same date
      cache.setTimes(uid, models.get(0).getDate(), null);

      Long[] keys = new Long[models.size()];
      for(int i = 0; i < models.size(); i++) {
        keys[i] = models.get(i).getId();
      }
      ok = dao.removeTimes(keys, uid);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing times", e);
      throw new ConnectionException("Error removing times", e);
    }
    
    return ok;
    
  }


  public List<Time> addTimes(List<Time> models, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding times");
    }
    
    if(models.size() == 0) {
      return null;
    }
    
    List<Time> list = null;
    
    try {
      
      for(Time t : models) {

        //reset time from date
        Date d = t.getDate();
        if(d != null) {
          d.setHours(0);
          d.setMinutes(0);
          d.setSeconds(0);
          t.setDate(d);
        }
  
        //save user
        t.setUid(uid);
        
      }
      
      //remove cache
      //assume that all times have same date
      cache.setTimes(uid, models.get(0).getDate(), null);

      list = dao.addTimes(models);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding times", e);
      throw new ConnectionException("Error adding times", e);
    }
    
    return list;
  }


  public List<Meal> addMeals(List<Meal> models, long timeId, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding meals");
    }
    
    if(models.size() == 0) {
      return null;
    }

    List<Meal> modelsCopy = new ArrayList<Meal>();
    
    try {
      
      //get meals
      for(Meal meal : models) {
        //check cache
        Meal jdo = cache.getMeal(meal.getId());
        
        if(jdo == null) {
          jdo = dao.getMeal(meal.getId(), uid);
         
          cache.addMeal(jdo);
        }
        
        //TODO increment count
        
        //add copy
        Meal clone = (Meal) jdo.clone();
        clone.setUid(uid);
        modelsCopy.add(clone);
      }
      
      //added to time
      if(timeId != 0) {
        Time t = dao.addMeals(timeId, modelsCopy);

        cache.setTimes(uid, t.getDate(), null);  //clear day's cache
      }
      //single meals
      else {
        dao.addMeals(modelsCopy);
        
        for(Meal m : modelsCopy) {
          cache.addMeal(m);
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding meal", e);
      throw new ConnectionException("Error adding meal", e);
    }
    
    return modelsCopy;
  }


  public boolean removeMeal(long id, long timeId, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Removing meal");
    }
    
    boolean ok = false;
    try {
      if(timeId != 0) {
        Time t = dao.removeMeal(id, timeId, uid);
        if(t != null) {
          ok = true;

          //remove from cache
          cache.setTimes(uid, t.getDate(), null);
        }
      }
      else {
        ok = dao.removeMeal(id, uid); 

        //remove from cache
        cache.removeMeal(id);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing meal", e);
      throw new ConnectionException("Error removing meal", e);
    }
    
    return ok;
  }

}
