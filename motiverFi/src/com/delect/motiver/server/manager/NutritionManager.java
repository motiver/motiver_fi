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
import com.delect.motiver.server.dao.helper.MealSearchParams;
import com.delect.motiver.server.jdo.MicroNutrient;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.delect.motiver.server.util.DateIterator;
import com.delect.motiver.server.util.NutritionUtils;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.NutritionDayModel;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.exception.ConnectionException;
import com.google.appengine.api.datastore.Key;

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


  public List<TimeJDO> getTimes(UserOpenid user, Date date, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading times ("+uid+", "+date+")");
    }

    if(date == null) {
      return null;
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_NUTRITION_FOODS, user.getUid(), uid);
    
    //get from cache
    List<TimeJDO> list;
    
    try {    
      //get from cache
      list = cache.getTimes(uid, date);
      
      if(list == null) {
        list = dao.getTimes(date, uid);
        
        for(TimeJDO time : list) {

          //get meals
          List<MealJDO> meals = new ArrayList<MealJDO>();
          for(Key key : time.getMealsKeys()) {
            meals.add(_getMeal(key.getId()));
          }
          time.setMealsNew(meals);

          List<Key> keys = time.getFoodsKeys();
          if(keys.size() > 0) {
            time.setFoods(dao.getFoods(keys));
            
            //find names for each food
            for(FoodJDO f : time.getFoods()) {
              if(f.getNameId().longValue() > 0) {
                f.setName(_getFoodName(f.getNameId()));
              }
            }
          }

          time.setUser(userManager.getUser(time.getUid()));
        }
        
        
        //add to cache
        cache.setTimes(uid, date, list);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading times", e);
      throw new ConnectionException("getTimes", e);
    }
  
    return list;
  }

  public List<TimeJDO> getTimes(UserOpenid user, Date dateStart, Date dateEnd, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading times ("+uid+", "+dateStart+" - "+dateEnd+")");
    }

    List<TimeJDO> list = new ArrayList<TimeJDO>();
    
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
  public void addFood(UserOpenid user, FoodJDO model, long timeId, long mealId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding/updating food ("+timeId+", "+mealId+"): "+model);
    }
          
    try {
      //update uid
      model.setUid(user.getUid());

      TimeJDO time = null;
      if(timeId != 0) {
        time = dao.getTime(timeId);
      }
      
      //add food
      dao.addFood(model);
      
      //if food is in time
      if(timeId != 0 && mealId == 0) {        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());
        
        //update if found, otherwise add
        int i = time.getFoodsKeys().indexOf(model.getKey());
        if(i == -1) {
          time.getFoodsKeys().add(model.getKey());
        }
        dao.updateTime(time);
      }
      //if food is in meal
      else if(mealId != 0) {
        MealJDO meal = dao.getMeal(mealId);
        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), meal.getUid());

        //update if found, otherwise add
        int i = meal.getFoodsKeys().indexOf(model.getKey());
        if(i == -1) {
          meal.getFoodsKeys().add(model.getKey());
        }
        dao.updateMeal(meal);
        
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
  public boolean removeFood(UserOpenid user, FoodJDO model, long timeId, long mealId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing food ("+timeId+", "+mealId+"): "+model);
    }
    
    boolean ok = false;
          
    try {
      //update uid
      model.setUid(user.getUid());

      TimeJDO time = null;
      if(timeId != 0) {
        time = dao.getTime(timeId);
      }
      
      //remove food
      dao.removeFood(model);
      
      //if food is in time
      if(timeId != 0 && mealId == 0) {        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());

        time.getFoodsKeys().remove(model.getKey());
        dao.updateTime(time);
      }
      //if food is in meal
      else if(mealId != 0) {
        MealJDO meal = dao.getMeal(mealId);
        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), meal.getUid());

        //update if found, otherwise add
        meal.getFoodsKeys().remove(model.getKey());
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

  
  public List<MealJDO> getMeals(UserOpenid user, int index, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading meals ("+index+", "+uid+")");
    }
    
    //check permissions
    userManager.checkPermission(Permission.READ_NUTRITION_FOODS, user.getUid(), uid);
    
    List<MealJDO> list = new ArrayList<MealJDO>();
    
    try {
      MealSearchParams params = new MealSearchParams();
      params.offset = index;
      params.limit = Constants.LIMIT_MEALS;
      params.uid = uid;
      List<Long> keys = dao.getMeals(params);
      
      for(Long key : keys) {

        
        MealJDO jdo = _getMeal(key);
        
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
  
  public List<MealJDO> getMostPopularMeals(UserOpenid user, int offset) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading most popular meals ("+offset+")");
    }
    
    List<MealJDO> list = new ArrayList<MealJDO>();
    
    try {
      int i = 0;
      
      //while enough found
      MealSearchParams params = new MealSearchParams();
      params.limit = Constants.LIMIT_MEALS * 2;
      params.minCopyCount = 1;
      while(list.size() < Constants.LIMIT_MEALS) {
        params.offset = i;
        List<Long> keys = dao.getMeals(params);
        
        for(Long key : keys) {
          
          if(list.size() == Constants.LIMIT_MEALS) {
            break;
          }
            
          MealJDO jdo = _getMeal(key);
          
          if(jdo != null) {
            
            //check permission
            if(userManager.hasPermission(Permission.READ_TRAINING, user.getUid(), jdo.getUid())) {
              if(i >= offset) {
                list.add(jdo);
              }
            }
            
          }
        }
        
        //if no meals found -> stop
        if(keys.size() == 0) {
          break;
        }
        
        i += Constants.LIMIT_MEALS * 2;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading meals", e);
      throw new ConnectionException("getMostPopularMeals", e.getMessage());
    }
    
    return list;
  }


  private MealJDO _getMeal(Long key) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getMeal ("+key+")");
    }
    
    if(key == null) {
      return null;
    }
    
    MealJDO jdo = cache.getMeal(key);
    
    if(jdo == null) {
      jdo = dao.getMeal(key);
      jdo.setUser(userManager.getUser(jdo.getUid()));

      List<Key> keys = jdo.getFoodsKeys();
      if(keys.size() > 0) {
        jdo.setFoods(dao.getFoods(keys));
        
        //find names for each food
        for(FoodJDO f : jdo.getFoods()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(_getFoodName(f.getNameId()));
          }
        }
      }
     
      cache.addMeal(jdo);
    }
    
    return jdo;
  }

  private FoodName _getFoodName(Long key) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getFoodName ("+key+")");
    }
    
    List<FoodName> names = _getFoodNames();
    
    if(names != null) {
      for(FoodName name : names) {
        if(name.getId().equals(key)) {
          return name;
        }
      }
    }
    
    return null;
  }

  private List<FoodName> _getFoodNames() throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_getFoodNames");
    }

    //load from cache
    List<FoodName> listAll = cache.getFoodNames();
    
    if(listAll == null) {
      listAll = dao.getFoodNames();
      
      //save to cache
      cache.setFoodNames(listAll);
    }
    
    return listAll;
  }


  private void _updateMeal(MealJDO meal) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "_updateMeal ("+meal+")");
    }
    
    dao.updateMeal(meal);
    
    cache.addMeal(meal);
  }


  public boolean removeTimes(List<TimeJDO> models, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing times ("+uid+"): "+models.size());
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
  public List<TimeJDO> addTimes(UserOpenid user, List<TimeJDO> models) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding times: "+models.size());
    }
    
    if(models.size() == 0) {
      return null;
    }
    
    try {
      
      for(TimeJDO t : models) {

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
        t.setUser(userManager.getUser(t.getUid()));
        
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


  public List<MealJDO> addMeals(UserOpenid user, List<MealJDO> models, long timeId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding meals ("+timeId+"): "+models.size());
    }
    
    if(models.size() == 0) {
      return null;
    }

    List<MealJDO> modelsCopy = new ArrayList<MealJDO>();
    
    try {
      
      //get meals
      for(MealJDO meal : models) {
        
        //new
        if(meal.getId() == 0) {
          
          //add two foods
          FoodJDO food1 = new FoodJDO();
          FoodJDO food2 = new FoodJDO();
          dao.addFood(food1);
          dao.addFood(food2);
          
          //set keys
          List<Key> foods = new ArrayList<Key>();
          foods.add(food1.getKey());
          foods.add(food2.getKey());
          meal.setFoodsKeys(foods);

          meal.setUid(user.getUid());
          meal.setUser(user);
          modelsCopy.add(meal);
        }
        else {
          //check cache
          MealJDO jdo = _getMeal(meal.getId());
          
          //check permission
          userManager.checkPermission(Permission.READ_NUTRITION, user.getUid(), jdo.getUid());
          
          //increment count
          if(!user.getUid().equals(jdo.getUid())) {
            incrementMealCount(jdo.getId());
          }
          
          //add copy
          MealJDO clone = (MealJDO) jdo.clone();
          clone.setUid(user.getUid());
          clone.setUser(user);
          
          //add copy of each food
          List<Key> keysNew = new ArrayList<Key>();
          List<FoodJDO> foodsOld = dao.getFoods(jdo.getFoodsKeys());
          for(FoodJDO f : foodsOld) {
            FoodJDO fNew = (FoodJDO)f.clone();
            dao.addFood(fNew);
            keysNew.add(fNew.getKey());
          }
          clone.setFoodsKeys(keysNew);
          
          modelsCopy.add(clone);
        }
      }
      
      //added to time
      if(timeId != 0) {
        TimeJDO t = dao.getTime(timeId);
        
        if(t != null) {
          //check permission
          userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), t.getUid());
          
          t = dao.addMeals(t.getId(), modelsCopy);

          //get meals
          List<MealJDO> meals = new ArrayList<MealJDO>();
          for(Key key : t.getMealsKeys()) {
            meals.add(_getMeal(key.getId()));
          }
          t.setMealsNew(meals);
          
          //find names for each food
          for(FoodJDO f : t.getFoods()) {
            if(f.getNameId().longValue() > 0) {
              f.setName(_getFoodName(f.getNameId()));
            }
          }

          cache.setTimes(t.getUid(), t.getDate(), null);  //clear day's cache 
        }
      }
      //single meals
      else {
        dao.addMeals(modelsCopy);
      }

      //get complete meals
      for(int i = 0; i < modelsCopy.size(); i++) {
        MealJDO meal = modelsCopy.get(i);
        modelsCopy.set(i, _getMeal(meal.getId()));
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding meal", e);
      throw new ConnectionException("Error adding meal", e);
    }
    
    return modelsCopy;
  }


  public boolean removeMeal(UserOpenid user, MealJDO model, long timeId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing meal ("+timeId+"): "+model);
    }
    
    boolean ok = false;
    try {
      
      //update time
      if(timeId != 0) {
        TimeJDO time = dao.getTime(timeId);
        
        userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());

        time.getMealsKeys().remove(model.getKey());
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

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Searching food names: "+query);
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

        String locale = user.getLocale();
        for(int i=0; i < listAll.size(); i++) {
          FoodName n = listAll.get(i);
  
          //if correct locale
          if(n.getLocale().equals(locale)) {
            
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


  public List<FoodName> addFoodName(UserOpenid user, List<FoodName> names) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Adding food names: "+names.size());
    }
    
    List<FoodName> list = new ArrayList<FoodName>(); 

    try {
      
      //load from cache
      List<FoodName> listAll = cache.getFoodNames();
      
      if(listAll == null) {
        listAll = dao.getFoodNames();
      }
      
      for(FoodName name : names) {

        int i = listAll.indexOf(name);
        
        //add if not found
        if(i == -1) {
          name.setUid(user.getUid());
          
          dao.addFoodName(name);
          
          //update "cache" array
          listAll.add(name);
        }
        //otherwise update (if name we have added)
        else {
          FoodName nameOld = listAll.get(i);
          if(nameOld != null && user.getUid().equals(nameOld.getUid())) {
            nameOld.update(name, false);
            dao.updateFoodName(nameOld);
          }
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


  public List<MealJDO> searchMeals(UserOpenid user, String query, int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Searching meals ("+index+"): "+query);
    }

    List<MealJDO> list = new ArrayList<MealJDO>();
    
    try {

      //split query string
      String[] arr = query.split(" ");

      //load from cache
      List<Long> keysAll = dao.getMeals(MealSearchParams.all());

      int i = 0;
      for(Long key : keysAll) {
        
        MealJDO m = _getMeal(key);
        
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
              MealJDO meal = _getMeal(m.getId());
              
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

  public void updateMeal(UserOpenid user, MealJDO model) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Updating meal: "+model);
    }
    
    try {
      
      MealJDO meal = _getMeal(model.getId());
      
      userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), meal.getUid());
      
      meal.update(model, false);
      dao.updateMeal(meal);

      //update meal given as parameter
      model.update(meal, true);
      model.setCount(meal.getCount());

      //update cache (also old date if moved)
      cache.addMeal(meal);
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating meal", e);
      throw new ConnectionException("Error updating meal", e);
    }
  
  }

  public void updateTime(UserOpenid user, TimeJDO model) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Updating time: "+model);
    }
    
    try {
      
      TimeJDO time = dao.getTime(model.getId());
      
      userManager.checkPermission(Permission.WRITE_NUTRITION, user.getUid(), time.getUid());
      
      time.update(model, false);
      dao.updateTime(time);

      //update time given as parameter
      model.update(time, true);
      
      //clear cache
      cache.setTimes(time.getUid(), time.getDate(), null);  //clear day's cache
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating time", e);
      throw new ConnectionException("Error updating time", e);
    }
  
  }

  public void incrementMealCount(long mealId) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Inrementing meal count: "+mealId);
    }
    
    try {
      
      MealJDO meal = _getMeal(mealId);
      dao.incrementMealCount(meal);

      //update cache
      cache.addMeal(meal);
    
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating meal", e);
      throw new ConnectionException("Error updating meal", e);
    }
  
  }

  public FoodName getFoodName(UserOpenid user, Long id) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading single name: "+id);
    }

    FoodName name = null;
    
    try {
      name = _getFoodName(id);
      
      userManager.checkPermission(Permission.READ_NUTRITION, user.getUid(), name.getUid());

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading name", e);
      throw new ConnectionException("Error loading name", e);
    }
    
    return name;
  }

  public MealJDO getMeal(UserOpenid user, long id) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading single meal: "+id);
    }

    MealJDO meal = null;
    
    try {
      meal = _getMeal(id);
      
      userManager.checkPermission(Permission.READ_NUTRITION, user.getUid(), meal.getUid());

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading meal", e);
      throw new ConnectionException("Error loading meal", e);
    }
    
    return meal;
  }


  public List<Double> getTotalEnergy(UserOpenid user, Date dateStart, Date dateEnd, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Calculating total energy: "+dateStart+" - "+dateEnd);
    }

    if(dateStart.getTime() > dateEnd.getTime()) {
      logger.log(Level.WARNING, "Invalid dates");
      return null;
    }
    
    List<Double> list = new ArrayList<Double>();

    try {

      userManager.checkPermission(Permission.READ_NUTRITION, user.getUid(), uid);
      
      Iterator<Date> i = new DateIterator(dateStart, dateEnd);
      while(i.hasNext())
      {
        final Date date = i.next();
        NutritionDayModel model = NutritionUtils.calculateEnergyFromTimes(getTimes(user, date, uid));
        
        list.add(model.getEnergy());        
      } 

    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading times", e);
      throw new ConnectionException("getTimes", e);
    }
    
    return list;
    
  }


  public List<MicroNutrient> getMicroNutrients(UserOpenid user, Date date, String uid) throws ConnectionException {

    List<MicroNutrient> list = new ArrayList<MicroNutrient>();
    
    try {
      
      List<TimeJDO> times = this.getTimes(user, date, uid);
      if(times != null) {
        for(TimeJDO t : times) {
  
          //each meal
          List<MealJDO> meals = t.getMealsNew();
          if(meals != null) {
            for(MealJDO m : meals) {
              
              //each food
              if(m.getFoods() != null) {
                for(FoodJDO food : m.getFoods()) {
                  //if has name
                  if(food.getName() != null) {
                    for(MicroNutrient mn : food.getName().getMicroNutrients()) {
                      //check if already found in array
                      int i=0;
                      double val = -1;
                      for(MicroNutrient model : list) {
                        if(model.getNameId().equals(mn.getNameId())) {
                          val = model.getValue();
                          break;
                        }
                        i++;
                      }
                      //found -> update value
                      if(val != -1) {
                        list.get(i).setValue(val + mn.getValue() * (food.getAmount() / 100));
                      }
                      //not found
                      else {
                        MicroNutrient mn2 = (MicroNutrient)mn.clone();
                        mn2.setValue(mn.getValue() * (food.getAmount() / 100));
                        list.add(mn2);
                      }
                    }
                  }
                }
              }
            }
  

            //each food
            if(t.getFoods() != null) {
              for(FoodJDO food : t.getFoods()) {
                //if has name
                if(food.getName() != null) {
                  for(MicroNutrient mn : food.getName().getMicroNutrients()) {
                    //check if already found in array
                    int i=0;
                    double val = -1;
                    for(MicroNutrient model : list) {
                      if(model.getNameId().equals(mn.getNameId())) {
                        val = model.getValue();
                        break;
                      }
                      i++;
                    }
                    //found -> update value
                    if(val != -1) {
                      list.get(i).setValue(val + mn.getValue() * (food.getAmount() / 100));
                    }
                    //not found
                    else {
                      MicroNutrient mn2 = (MicroNutrient)mn.clone();
                      mn2.setValue(mn.getValue() * (food.getAmount() / 100));
                      list.add(mn2);
                    }
                  }
                }
              }
            }
            
          }
        }
      }
  
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading micronutrients", e);
      throw new ConnectionException("getMicroNutrients", e);
    }
    
    
    return list;
  }

}
