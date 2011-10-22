package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.cache.NutritionCache;
import com.delect.motiver.server.dao.NutritionDAO;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.server.util.DateIterator;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Permission;
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


  public List<Time> getTimes(UserOpenid user, Date date, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading times ("+date+")");
    };

    if(date == null) {
      return null;
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_NUTRITION_FOODS, user.getUid(), uid);
    
    //get from cache
    List<Time> list;
    
    try {    
      //get from cache
      list = cache.getTimes(uid, date);
      
      if(list == null) {
        list = dao.getTimes(date, uid);

        //add to cache
        cache.setTimes(uid, date, list);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading times", e);
      throw new ConnectionException("getTimes", e);
    }
  
    return list;
  }

  public List<Time> getTimes(UserOpenid user, Date dateStart, Date dateEnd, String uid) throws ConnectionException {

    List<Time> list = new ArrayList<Time>();
    
    Iterator<Date> i = new DateIterator(dateStart, dateEnd);
    while(i.hasNext())
    {
      final Date date = i.next();
      list.addAll(getTimes(user, date, uid));
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
  public void addFood(UserOpenid user, Food model, long timeId, long mealId) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding/updating food: "+model);
    }    
          
    try {
      //update uid
      model.setUid(user.getUid());

      Time time = null;
      if(timeId != 0) {
        time = dao.getTime(timeId);
      }
      
      //if food is in time
      if(timeId != 0 && mealId == 0) {        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());
        
        //update if found, otherwise add
        int i = time.getFoods().indexOf(model);
        Food f;
        if(i == -1) {
          f = model;
          time.getFoods().add(model);
        }
        else {
          f = time.getFoods().get(i);
          f.update(model, false);
        }
        dao.updateTime(time);
        
        //return updated model
        model.update(f, true);
      }
      //if food is in meal
      else if(mealId != 0) {
        Meal meal = dao.getMeal(mealId);
        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), meal.getUid());

        //update if found, otherwise add
        int i = time.getFoods().indexOf(model);
        Food f;
        if(i == -1) {
          f = model;
          meal.getFoods().add(model);
        }
        else {
          f = meal.getFoods().get(i);
          f.update(model, false);
        }
        dao.updateMeal(meal);
        
        //return updated model
        model.update(f, true);
        
        cache.removeMeal(mealId); //clear cache
      }
      
      //clear cache if in time
      if(time != null) {
        cache.setTimes(time.getUid(), time.getDate(), null);  //clear day's cache
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food", e);
      throw new ConnectionException("Add food", e.getMessage());
    }
  }


  /**
   * Removes single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public boolean removeFood(UserOpenid user, Food model, long timeId, long mealId) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding/updating food: "+model);
    }
    
    boolean ok = false;
          
    try {
      //update uid
      model.setUid(user.getUid());

      Time time = null;
      if(timeId != 0) {
        time = dao.getTime(timeId);
      }
      
      //if food is in time
      if(timeId != 0 && mealId == 0) {        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());

        time.getFoods().remove(model);
        dao.updateTime(time);
      }
      //if food is in meal
      else if(mealId != 0) {
        Meal meal = dao.getMeal(mealId);
        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), meal.getUid());

        //update if found, otherwise add
        meal.getFoods().remove(model);
        dao.updateMeal(meal);
        
        cache.removeMeal(mealId); //clear cache
      }
      
      //clear cache if in time
      if(time != null) {
        cache.setTimes(time.getUid(), time.getDate(), null);  //clear day's cache
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food", e);
      throw new ConnectionException("Add food", e.getMessage());
    }
    
    return ok;
  }

  
  public List<Meal> getMeals(UserOpenid user, int index, String uid) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Loading meals ("+index+")");
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_NUTRITION_FOODS, user.getUid(), uid);
    
    List<Meal> list = new ArrayList<Meal>();
    
    try {
      List<Long> keys = dao.getMeals(index, uid);
      
      for(Long key : keys) {

        
        Meal jdo = _getMeal(key);
        
        if(jdo != null) {
          
          //check permission
          userManager.checkPermission(Permission.READ_NUTRITION, user.getUid(), jdo.getUid());
          
          list.add(jdo);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading meals", e);
      throw new ConnectionException("getMeals", e.getMessage());
    }
    
    return list;
  }


  private Meal _getMeal(Long key) throws Exception {
    
    Meal jdo = cache.getMeal(key);
    
    if(jdo == null) {
      jdo = dao.getMeal(key);
      jdo.setUser(userManager.getUser(jdo.getUid()));
     
      cache.addMeal(jdo);
    }
    
    return jdo;
  }


  private void _updateMeal(Meal meal) throws Exception {
    
    dao.updateMeal(meal);
    
    cache.addMeal(meal);
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
      
      //TODO missing permission check!!
      
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


  @SuppressWarnings("deprecation")
  public List<Time> addTimes(UserOpenid user, List<Time> models) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding times");
    }
    
    if(models.size() == 0) {
      return null;
    }
    
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
        t.setUid(user.getUid());
        
      }
      
      //remove cache
      //assume that all times have same date
      cache.setTimes(user.getUid(), models.get(0).getDate(), null);

      dao.addTimes(models);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding times", e);
      throw new ConnectionException("Error adding times", e);
    }
    
    return models;
  }


  public List<Meal> addMeals(UserOpenid user, List<Meal> models, long timeId) throws ConnectionException {

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
        
        //new
        if(meal.getId() == 0) {
          
          //add two foods
          List<Food> foods = new ArrayList<Food>();
          foods.add(new Food());
          foods.add(new Food());
          meal.setFoods(foods);

          meal.setUid(user.getUid());
          meal.setUser(user);
          modelsCopy.add(meal);
        }
        else {
          //check cache
          Meal jdo = _getMeal(meal.getId());
          
          //increment count
          jdo.setCount(jdo.getCount() + 1);
          _updateMeal(jdo);
          
          //add copy
          Meal clone = (Meal) jdo.clone();
          clone.setUid(user.getUid());
          clone.setUser(user);
          modelsCopy.add(clone);
        }
      }
      
      //added to time
      if(timeId != 0) {
        Time t = dao.getTime(timeId);
        
        if(t != null) {
          //check permission
          userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), t.getUid());
          
          dao.addMeals(timeId, modelsCopy);

          cache.setTimes(t.getUid(), t.getDate(), null);  //clear day's cache 
        }
      }
      //single meals
      else {
        dao.addMeals(modelsCopy);
        
        //doesnt cache names
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


  public boolean removeMeal(UserOpenid user, Meal model, long timeId) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Removing meal");
    }
    
    boolean ok = false;
    try {
      
      //update time
      if(timeId != 0) {
        Time time = dao.getTime(timeId);
        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());

        time.getMealsKeys().remove(model);
        dao.updateTime(time);
        
        //remove from cache
        cache.setTimes(time.getUid(), time.getDate(), null);
      }
      
      //remove meal & cache
      cache.removeMeal(model.getId());
      ok = dao.removeMeal(model);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing meal", e);
      throw new ConnectionException("Error removing meal", e);
    }
    
    return ok;
  }


  public List<FoodName> searchFoodNames(UserOpenid user, String query, int limit) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching food names: "+query);
    }
    
    List<FoodName> list = new ArrayList<FoodName>();    

    try {

      //load from cache
      List<FoodName> listAll = cache.getFoodNames();
      
      if(listAll == null) {
        listAll = dao.getFoodNames();
        
        //save to cache
        cache.setFoodNames(listAll);
      }
      
      if(listAll != null) {
      
        //split query string
        //strip special characters
        query = query.replace("(", "");
        query = query.replace(")", "");
        query = query.replace(",", "");
        query = query.toLowerCase();
        String[] arr = query.split(" ");
        
        //search
        List<FoodName> result = new ArrayList<FoodName>();
        
        for(int i=0; i < listAll.size(); i++) {
          FoodName n = listAll.get(i);
  
          String name = n.getName();
          
          //strip special characters
          name = name.replace("(", "");
          name = name.replace(")", "");
          name = name.replace(",", "");
          
          //filter by query (add count variable)
          int count = 0;
          for(String s : arr) {
            //if word long enough
            if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD) {
              //exact match
              if(name.toLowerCase().equals( s )) {
                count += 3;
              }
              //partial match
              else if(name.toLowerCase().contains( s )) {
                count++;
              }
            }
          }
          //if motiver's food -> add count
          if(count > 0) {
            if(n.getTrusted() == 100) {
              count += 2;
            }
            //if verified
            else if(n.getTrusted() == 1) {
              count++;
            }
          }

          //if found
          if(count > 0) {
  
            int countUse = 0;
            try {
              countUse = cache.getFoodNameCount(user, n.getId());
              
              if(countUse == -1) {
                countUse = dao.getFoodNameCount(user, n.getId());
                
                cache.setFoodNameCount(user, n.getId(), countUse);
              }
              
            } catch (Exception e) {
              logger.log(Level.SEVERE, "Error fetching food name count", e);
            }
            
            n.setCount(count, countUse);
            result.add(n);
          }
        }
        
        //sort array based on count
        Collections.sort(result);
        
        //convert to client model
        for(int i=0; i < result.size() && i < limit; i++) {
          FoodName n = result.get(i);
          if(n.getCountQuery() > 0) {
            list.add(n);
          }
          else {
            break;
          }
          //limit query
          if(list.size() >= limit) {
            break;
          }
        }
      
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error searching food names", e);
      throw new ConnectionException("Error searching food names", e);
    }
    
    return list;
  }


  @SuppressWarnings("unused")
  public List<FoodName> addFoodName(UserOpenid user, List<FoodName> names) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Adding food names");
    }
    
    List<FoodName> list = new ArrayList<FoodName>(); 

    try {
      
      //load from cache
      List<FoodName> listAll = cache.getFoodNames();
      
      if(list == null) {
        listAll = dao.getFoodNames();
      }
      
      for(FoodName name : names) {
        
        //add if not found
        if(!listAll.contains(name)) {
          name.setUid(user.getUid());
          
          dao.addFoodName(name);
          
          //update "cache" array
          listAll.add(name);
        }
        
        list.add(name);
      }

      //save to cache
      cache.setFoodNames(listAll);

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food names", e);
      throw new ConnectionException("Error adding food names", e);
    }
    
    return list;
  }


  public List<Meal> searchMeals(UserOpenid user, String query, int index) throws ConnectionException {

    if(logger.isLoggable(Constants.LOG_LEVEL_MANAGER)) {
      logger.log(Constants.LOG_LEVEL_MANAGER, "Searching meals ("+index+")");
    }

    List<Meal> list = new ArrayList<Meal>();
    
    try {

      //split query string
      String[] arr = query.split(" ");

      //load from cache
      List<Meal> listAll = dao.getMeals();

      int i = 0;
      for(Meal m : listAll) {
        
        if(!m.getUid().equals(user.getUid()) 
            && userManager.hasPermission(Permission.READ_NUTRITION, user.getUid(), m.getUid()))  {

          if(i >= index) {
            
            final String name = m.getName();
            
            //filter by query
            boolean match = false;
            for(String s : arr) {
              match = name.toLowerCase().contains( s.toLowerCase() );
              if(match) {
                break;
              }
            }
            
            if(match) {
  
              //get "whole" meal (which has also foods, etc..)
              Meal meal = _getMeal(m.getId());
              
              list.add(meal);
            }
          }
          
          i++;
          
        }
        
      }
      
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food names", e);
      throw new ConnectionException("Error adding food names", e);
    }
    
    
    return list;
    
  }

}
