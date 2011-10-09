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
package com.delect.motiver.server.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.cache.WeekCache;
import com.delect.motiver.server.jdo.FoodNameCount;
import com.delect.motiver.server.jdo.MicroNutrient;
import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.FoodInMealTime;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.MealInTime;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.server.service.MyServiceImpl;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.MicroNutrientModel;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.TimeModel;
import com.delect.motiver.shared.exception.ConnectionException;
import com.delect.motiver.shared.exception.NoPermissionException;

/**
 * @author Antti
 *
 */
public final class NutritionManagerOld {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionManagerOld.class.getName());
  
  private static NutritionManagerOld dao; 

  public static NutritionManagerOld getInstance() {
    if(dao == null) {
      dao = new NutritionManagerOld();
    }
    return dao;
  }
  
  /**
   * Adds single name
   * @param pm
   * @param model
   * @param uid
   * @param locale
   * @return added name
   * @throws Exception 
   */
  @SuppressWarnings("unchecked")
  public FoodNameModel addFoodNameModel(FoodNameModel model, String uid, String locale) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Adding food name: '"+model.getName()+" "+model.getEnergy()+"'");
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //check if similar found
      Query q = pm.newQuery(FoodName.class);
      q.setFilter("name == nameParam && energy == energyParam && locale == localeParam");
      q.declareParameters("java.lang.String nameParam, java.lang.Double energyParam, java.lang.String localeParam");
      List<FoodName> arr = (List<FoodName>) q.execute(model.getName(), model.getEnergy(), locale);

      long nameId = 0;
      
      if(arr.size() > 0) {
        nameId = arr.get(0).getId();
      }
      //not found
      else {
        if(logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "Name '"+model.getName()+" "+model.getEnergy()+"' not found. Creating a new one");
        }

        FoodName mServer = FoodName.getServerModel(model);
        mServer.setLocale(locale);
        mServer.setUid(uid);
        //add also micronutrients
        if(model.getMicroNutrients().size() > 0) {
          List<MicroNutrient> list = new ArrayList<MicroNutrient>();
          for(MicroNutrientModel mn : model.getMicroNutrients()) {
              list.add(MicroNutrient.getServerModel(mn));
          }
          mServer.setMicronutrients(list);
        }
        pm.makePersistent(mServer);

        //add to cache
        WeekCache cache = new WeekCache();
        cache.addFoodName(mServer);
        cache.removeFoodNames();  //TODO needs improvement
        
        nameId = mServer.getId();      
      }
      
      //get added name
      model = getFoodNameModel(nameId);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return model;    
  }

  /**
   * Returns exercise name
   * @param pm
   * @param nameId
   * @throws Exception 
   */
  public FoodNameModel getFoodNameModel(long nameId) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading food name: "+nameId);
    }
    
    if(nameId <= 0) {
      return null;
    }
    
    FoodNameModel model = null;

    //load all names
    List<FoodName> names = getFoodNames();
    
    for(FoodName name : names) {
      if(name.getId() != null && name.getId().longValue() == nameId) {
        //convert to client side model
        model = FoodName.getClientModel(name);
        break;
      }
    }
    
    return model;
  }

  /**
   * Returns all food name
   * @param pm
   * @param locale
   * @throws Exception
   * @return list 
   */
  @SuppressWarnings("unchecked")
  public List<FoodName> getFoodNames() throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading all food names: ");
    }

    //load from cache
    WeekCache cache = new WeekCache();
    List<FoodName> n = cache.getFoodNames();

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      if(n == null) {
        if(logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "Not found from cache");
        }

        //load in chunks
        n = new ArrayList<FoodName>();
        int i = 0;
        while(true){
          Query q = pm.newQuery(FoodName.class);
          q.setRange(i, i+100);
          List<FoodName> u = (List<FoodName>) q.execute();
          n.addAll(u);
          
          if(u.size() < 100) {
            break;
          }
          i += 100;
        }
        
        //save to cache
        cache.addFoodNames(n);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading food names", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return n;
  }

  /**
   * Returns single time
   * @param pm
   * @param id
   * @return
   */
  public TimeModel getTimeModel(Long timeId, String userUid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading time: "+timeId);
    }
    
    TimeModel model = null;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //load from cache
      WeekCache cache = new WeekCache();
      Time t = cache.getTime(timeId);

      //not found
      if(t == null) {
        if(logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "Not found from cache");
        }
        
        t = pm.getObjectById(Time.class, timeId);
      }
      
      if(t != null) {
        
        //check permission
        if(!MyServiceImpl.hasPermission(pm, Permission.READ_NUTRITION, userUid, t.getUid())) {
          throw new NoPermissionException(Permission.READ_NUTRITION, userUid, t.getUid());
        }
          
        //convert to client side model
        model = Time.getClientModel(t);
        List<MealModel> timeMeals = new ArrayList<MealModel>();
        List<FoodModel> timeFoods = new ArrayList<FoodModel>();
        
        //get meals
        if(t.getMeals() != null) {
          for(MealInTime w : t.getMeals()) {
            MealModel meal = MealInTime.getClientModel(w);
            
            //get foods
            List<FoodModel> listFoods = new ArrayList<FoodModel>();
            for(FoodInMealTime f : w.getFoods()) {
              FoodModel fClient = FoodInMealTime.getClientModel(f);
              fClient.setMealId(meal.getId());
              fClient.setTimeId(meal.getTimeId());
              fClient.setUid(meal.getUid());
              fClient.setName(getFoodNameModel(f.getNameId()));
              
              listFoods.add(fClient);
            }
            meal.setFoods(listFoods);
            
            //set time
            meal.setTimeId(t.getId());
                    
            timeMeals.add(meal);
          }
        }
        model.setMeals(timeMeals);
        
        //get foods
        if(t.getFoods() != null) {
          for(Food f : t.getFoods()) {
            FoodModel fClient = Food.getClientModel(f);
            fClient.setName(getFoodNameModel(f.getNameId()));

            timeFoods.add(fClient);
          }
        }
        model.setFoods(timeFoods);
        
        //save to cache
        cache.addTime(t);
      }
      else {
        throw new Exception("Time not found");
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading foods", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return model;
  }

  /**
   * Returns single meal
   * @param pm
   * @param id
   * @return
   */
  public MealModel getMealModel(Long mealId, String userUid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading meal: "+mealId);
    }
    
    MealModel model = null;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //load from cache
      WeekCache cache = new WeekCache();
      Meal t = cache.getMeal(mealId);

      //not found
      if(t == null) {    
        if(logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "Not found from cache");
        }
        
        t = pm.getObjectById(Meal.class, mealId);
      }
      
      if(t != null) {
        
        //check permission
        if(!MyServiceImpl.hasPermission(pm, Permission.READ_NUTRITION, userUid, t.getUid())) {
          throw new NoPermissionException(Permission.READ_NUTRITION, userUid, t.getUid());
        }
          
        //convert to client side model
        model = Meal.getClientModel(t);
        List<FoodModel> mealFoods = new ArrayList<FoodModel>();
        
        //get foods
        if(t.getFoods() != null) {
          for(Food f : t.getFoods()) {
            FoodModel fClient = Food.getClientModel(f);
            fClient.setName(getFoodNameModel(f.getNameId()));

            mealFoods.add(fClient);
          }
        }
        model.setFoods(mealFoods);
        
        //save to cache
        cache.addMeal(t);
      }
      else {
        throw new Exception("Meal not found");
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeals", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return model;
  }

  /**
   * Adds single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public FoodModel addFoodModel(FoodModel model, String uid, String locale) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Adding food: '"+model.getId()+"'");
      System.out.println("Adding food: '"+model.getId()+"'");
    }

    WeekCache cache = new WeekCache();
    FoodModel m = null;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          //if food is in meal (which is in time)
          if(model.getTimeId() != 0 && model.getMealId() != 0) {
            //get 
            Time time = pm.getObjectById(Time.class, model.getTimeId());
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }

              //get meal
              for(MealInTime meal : time.getMeals()) {
                if(meal.getId() == model.getMealId()) {
                  //if no foods
                  if(meal.getFoods() == null) {
                    List<FoodInMealTime> list = new ArrayList<FoodInMealTime>();
                    meal.setFoods(list);
                  }
                  
                  FoodInMealTime f = FoodInMealTime.getServerModel(model);
                  //TODO doesn't add name
                  f.setNameId(0L);
                  meal.getFoods().add(f);
                  tx.commit();
                  
                  m = FoodInMealTime.getClientModel(f);
                  m.setMealId(model.getMealId());
                  m.setTimeId(model.getTimeId());
                  
                  //update cache
                  cache.addTime(time);
                  
                  break;
                }
              }
            }
            else {
              throw new Exception("Time not found");
            }
          }
          //if added to some time -> save key
          else if(model.getTimeId() != 0) {
            
            //get time
            Time time = pm.getObjectById(Time.class, model.getTimeId());
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }
              
              //if no foods
              if(time.getFoods() == null) {
                List<Food> list = new ArrayList<Food>();
                time.setFoods(list);
              }
              
              Food f = Food.getServerModel(model);
              //TODO doesn't add name
              f.setNameId(0L);
              time.getFoods().add(f);
              tx.commit();
              
              long id = f.getId();
              m = Food.getClientModel(f);
              long id2 = m.getId();
              m.setTimeId(model.getTimeId());
              m.setMealId(model.getMealId());
              
              //update cache
              cache.addTime(time);
              
              break;            
            }
            else {
              throw new Exception("Time not found");
            }
            
          }
          //if added to some meal -> save key
          else if(model.getMealId() != 0) {
            
            //get meal
            Meal meal = pm.getObjectById(Meal.class, model.getMealId());
            if(meal != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid());
              }
              
              //if no foods
              if(meal.getFoods() == null) {
                List<Food> list = new ArrayList<Food>();
                meal.setFoods(list);
              }
              
              Food f = Food.getServerModel(model);
              //TODO doesn't add name
              f.setNameId(0L);
              meal.getFoods().add(f);
              tx.commit();
              
              //return client side model
              m = Food.getClientModel(f);
              m.setMealId(model.getMealId());
              m.setTimeId(model.getTimeId());
              
              //update cache
              cache.addMeal(meal);            
            }
            else {
              throw new Exception("Meal not found");
            }
            
          }
          
          break;
          
        }
        catch (Exception ex) {
          if (tx.isActive()) {
            tx.rollback();
          }
          if(ex instanceof NoPermissionException) {         
            throw ex;
          }
          logger.log(Level.WARNING, "Error updating exercise", ex);
          
          //retries used
          if (retries == 0) {          
            throw ex;
          }
          
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;    
  }

  /**
   * Removes single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public boolean removeFoodModel(FoodModel model, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing food: '"+model.getId()+"'");
    }

    WeekCache cache = new WeekCache();
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          //if food is in meal (which is in time)
          if(model.getTimeId() != 0 && model.getMealId() != 0) {
            //get 
            Time time = pm.getObjectById(Time.class, model.getTimeId());
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }

              //get meal
              for(MealInTime meal : time.getMeals()) {
                if(meal.getId() == model.getMealId()) {
                  
                  //get food
                  if(meal.getFoods() != null) {
                    for(FoodInMealTime f : meal.getFoods()) {
                      if(f.getId() == model.getId()) {
                        meal.getFoods().remove(f);
                        pm.makePersistent(time);
                        tx.commit();
                        
                        //update cache
                        cache.addTime(time);
                        
                        ok = true;
                        
                        break;
                      }
                    }
                  }
                  break;
                }
              }
            }
            else {
              throw new Exception("Time not found");
            }
          }
          //if added to some time -> save key
          else if(model.getTimeId() != 0) {
            
            //get time
            Time time = pm.getObjectById(Time.class, model.getTimeId());
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }
              
              //get food
              if(time.getFoods() != null) {
                for(Food f : time.getFoods()) {
                  if(f.getId() == model.getId()) {
                    time.getFoods().remove(f);
                    pm.makePersistent(time);
                    tx.commit();
                    
                    //update cache
                    cache.addTime(time);
                    
                    ok = true;
                    
                    break;
                  }
                }
              }          
            }
            else {
              throw new Exception("Time not found");
            }
            
          }
          //if added to some meal -> save key
          else if(model.getMealId() != 0) {
            
            //get meal
            Meal meal = pm.getObjectById(Meal.class, model.getMealId());
            if(meal != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid());
              }
              
              //get food
              if(meal.getFoods() != null) {
                for(Food f : meal.getFoods()) {
                  if(f.getId() == model.getId()) {
                    meal.getFoods().remove(f);
                    pm.makePersistent(meal);
                    tx.commit();
                    
                    //update cache
                    cache.addMeal(meal);
                    
                    ok = true;
                    
                    break;
                  }
                }
              }         
            }
            else {
              throw new Exception("Meal not found");
            }
            
          }
          
          break;
          
        }
        catch (Exception ex) {
          if (tx.isActive()) {
            tx.rollback();
          }
          if(ex instanceof NoPermissionException) {         
            throw ex;
          }
          logger.log(Level.WARNING, "Error updating exercise", ex);
          
          //retries used
          if (retries == 0) {          
            throw ex;
          }
          
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing food", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;    
  }

  /**
   * Removes single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public FoodModel updateFoodModel(FoodModel model, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Updating food: '"+model.getId()+"'");
    }
    
    WeekCache cache = new WeekCache();

    //get name
    FoodNameModel n = null;
    if(model.getName() != null) {
      n = getFoodNameModel(model.getName().getId());
    }

      PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          //if food is in meal (which is in time)
          if(model.getTimeId() != 0 && model.getMealId() != 0) {
            //get 
            Time time = pm.getObjectById(Time.class, model.getTimeId());
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }

              //get meal
              for(MealInTime meal : time.getMeals()) {
                if(meal.getId() == model.getMealId()) {
                  
                  //get food
                  List<FoodInMealTime> listFoods = meal.getFoods();
                  if(listFoods != null) {
                    for(FoodInMealTime f : listFoods) {
                      if(f.getId() == model.getId()) {

                        //update name
                        if(n != null) {
                          f.setNameId(n.getId());
                          model.setName(n);
                        }
                        
                        f.setAmount(model.getAmount());
                        tx.commit();
                        
                        //update cache
                        cache.addTime(time);
                        
                        break;
                      }
                    }
                  }
                  break;
                }
              }
            }
            else {
              throw new Exception("Time not found");
            }
          }
          //if added to some time -> save key
          else if(model.getTimeId() != 0) {
            
            //get time
            Time time = pm.getObjectById(Time.class, model.getTimeId());
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }
              
              //get food
              List<Food> listFoods = time.getFoods();
              if(listFoods != null) {
                for(Food f : listFoods) {
                  if(f.getId() == model.getId()) {

                    //update name
                    if(n != null) {
                      f.setNameId(n.getId());
                      model.setName(n);
                    }
                    
                    f.setAmount(model.getAmount());
                    tx.commit();
                    
                    //update cache
                    cache.addTime(time);
                    
                    break;
                  }
                }
              }          
            }
            else {
              throw new Exception("Time not found");
            }
            
          }
          //if added to some meal -> save key
          else if(model.getMealId() != 0) {
            
            //get meal
            Meal meal = pm.getObjectById(Meal.class, model.getMealId());
            if(meal != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid());
              }
              
              //get food
              List<Food> listFoods = meal.getFoods();
              if(listFoods != null) {
                for(Food f : listFoods) {
                  if(f.getId() == model.getId()) {

                    //update name
                    if(n != null) {
                      f.setNameId(n.getId());
                      model.setName(n);
                    }
                    
                    f.setAmount(model.getAmount());
                    tx.commit();
                    
                    //update cache
                    cache.addMeal(meal);
                    
                    break;
                  }
                }
              }         
            }
            else {
              throw new Exception("Meal not found");
            }
            
          }
          
          break;
          
        }
        catch (Exception ex) {
          if (tx.isActive()) {
            tx.rollback();
          }
          if(ex instanceof NoPermissionException) {         
            throw ex;
          }
          logger.log(Level.WARNING, "Error updating food", ex);
          
          //retries used
          if (retries == 0) {          
            throw ex;
          }
          
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating food", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return model;    
  }
  
  /**
   * Removes single time
   * @param pm
   * @param time
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public Boolean removeTimeModel(Long timeId, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing time: "+timeId);
    }
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {

          Time t = pm.getObjectById(Time.class, timeId);
          if(t != null) {
            
            //check permission
            if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, t.getUid())) {
              throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, t.getUid());
            }
              
            //remove exercise
            pm.deletePersistent(t);
            tx.commit();
            
            ok = true;
            
            //clear time from cache
            WeekCache cache = new WeekCache();
            cache.removeTime(timeId);
        
            break;
          }
          else {
            logger.log(Level.WARNING, "Could not find time");
          }
        }
        catch (Exception e) {
          if (tx.isActive()) {
            tx.rollback();
          }
          if(e instanceof NoPermissionException) {         
            throw e;
          }
          logger.log(Level.WARNING, "Error deleting time", e);
          
          //retries used
          if (retries == 0) {          
            throw e;
          }
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing times", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Removes single meal
   * @param pm
   * @param meal
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public Boolean removeMealModel(MealModel model, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Removing meal: "+model.getId());
    }
    
    WeekCache cache = new WeekCache();
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //try to update X meals
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {

          //if meal is in time
          if(model.getTimeId() != 0) {
            Time t = pm.getObjectById(Time.class, model.getTimeId());
            //meal found and we have permission
            if(t != null) {
              
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, t.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, t.getUid());
              }
              
              for(MealInTime f : t.getMeals()) {
                if(f.getId() == model.getId()) {
                  pm.deletePersistent(f);
                  tx.commit();
                  ok = true;
                  
                  //update cache
                  cache.addTime(t);
                  
                  break;
                }
              }
              
              
            }
          }
          //not in time
          else {
            Meal m = pm.getObjectById(Meal.class, model.getId());
            if(m != null) {
              
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, m.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, m.getUid());
              }
              
              pm.deletePersistent(m);
              tx.commit();
              ok = true;
              
              //update cache
              cache.removeMeal(m.getId());
            }
          }
          
          break;
        }
        catch (Exception e) {
          if (tx.isActive()) {
            tx.rollback();
          }
          if(e instanceof NoPermissionException) {         
            throw e;
          }
          logger.log(Level.WARNING, "Error deleting meal", e);
          
          //retries used
          if (retries == 0) {          
            throw e;
          }
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing meal", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Removes single meal
   * @param pm
   * @param meal
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public MealModel addMealModel(MealModel model, String uid) throws Exception {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Adding meal: "+model.getId());
    }
    
    MealModel mReturn = null;
    
    WeekCache cache = new WeekCache();
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //try to update X meals
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          List<FoodModel> listFoods = new ArrayList<FoodModel>();
          
          //if old -> get foods
          if(model.getId() > 0) {
            
            MealModel m = getMealModel(model.getId(), uid);
            
            //check permission
            if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, m.getUid())) {
              throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, m.getUid());
            }
            
            //if not our add count
            //TODO
            
            if(m != null && m.getFoods() != null) {
              listFoods = m.getFoods();
            }
          }
          //new -> add 2 empty foods
          else {
            listFoods.add(new FoodModel());
            listFoods.add(new FoodModel());
          }

          long tid = 0;
          long mid = 0;
          
          //if saved to time
          if(model.getTimeId() != 0) {
            Time t = pm.getObjectById(Time.class, model.getTimeId());
            if(t != null) {
              //create new meal
              MealInTime mealNew = MealInTime.getServerModel(model);
              mealNew.setId(null);
              List<FoodInMealTime> foods = new ArrayList<FoodInMealTime>();
              for(FoodModel f : listFoods) {
                FoodInMealTime f2 = FoodInMealTime.getServerModel(f);
                f2.setId(null);
                if(f.getName() != null) {
                  f2.setNameId(f.getName().getId());
                }
                foods.add(f2);
              }
              mealNew.setFoods(foods);
              t.getMeals().add(mealNew);
              
              tx.commit();
              
              //update cache
              cache.addTime(t);

              tid = t.getId();
              mid = mealNew.getId();
            }
          }
          //not in time -> just save new meal
          else {
            //create new meal
            Meal mealNew = Meal.getServerModel(model);
            mealNew.setId(null);
            mealNew.setUid(uid);
            
            List<Food> foods = new ArrayList<Food>();
            for(FoodModel f : listFoods) {
              foods.add(Food.getServerModel(f));
            }
            mealNew.setFoods(foods);
            
            pm.makePersistent(mealNew);
            tx.commit();
            
            //update cache
            cache.addMeal(mealNew);

            tid = 0;
            mid = mealNew.getId();
          }
          
          //get client model
          //if in time
          if(tid != 0) {
            TimeModel time = getTimeModel(tid, uid);
            if(time != null) {
              for(MealModel m : time.getMeals()) {
                if(m.getId() == mid) {
                  mReturn = m;
                  break;
                }
              }
            }
          }
          //not in time
          else {
            mReturn = getMealModel(mid, uid);
          }
          mReturn.setTimeId(tid);
          
          break;
        }
        catch (Exception e) {
          if (tx.isActive()) {
            tx.rollback();
          }
          if(e instanceof NoPermissionException) {         
            throw e;
          }
          logger.log(Level.WARNING, "Error adding meal", e);
          
          //retries used
          if (retries == 0) {          
            throw e;
          }
          logger.log(Level.WARNING, " Retries left: "+retries);
          
          --retries;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding meals", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return mReturn;
  }

  /**
   * Returns how often user have use single food name
   * @param uid
   * @param id
   * @return
   */
  @SuppressWarnings("unchecked")
  public int getFoodNameCount(String uid, Long id) {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Fetching count for food name: "+id);
    }
    
    int count = -1;
    WeekCache cache = new WeekCache();
    count = cache.getFoodNameCount(uid, id);

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      if(count < 0) {
        if(logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "Not found from cache");
        }
        
        Query qUse = pm.newQuery(FoodNameCount.class);
        qUse.setFilter("nameId == nameIdParam && openId == openIdParam");
        qUse.declareParameters("java.lang.Long nameIdParam, java.lang.String openIdParam");
        qUse.setRange(0, 1);
        List<FoodNameCount> valueCount = (List<FoodNameCount>) qUse.execute(id, uid);
        if(valueCount.size() > 0) {
          count = valueCount.get(0).getCount();
          
        }
        
        if(count < 0) {
          count = 0;
        }
        cache.addFoodNameCount(uid, id, count);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading count for food name", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return count;
  }

  
  public List<Meal> getMeals(int index, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "Loading meals ("+index+")");
    }
    
    WeekCache cache = new WeekCache();
    
    List<Meal> list = null;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(Meal.class);
      q.setFilter("openId == openIdParam && time == null");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(index, index + Constants.LIMIT_MEALS + 1);
      List<Meal> meals = (List<Meal>) q.execute(uid);
            
      //get meals
      if(meals != null) {
        
        Collections.sort(meals);
        
        int i = 0;
        for(Meal m : meals) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_MEALS) {
            list.add(null);
            break;
          }
                          
          list.add(m);
          
          //add to cache
          cache.addMeal(m);
          
          i++;
        }
        
      }
    } catch (Exception e) {
      throw new ConnectionException("Error loading meals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }
}
