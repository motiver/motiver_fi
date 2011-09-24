package com.delect.motiver.server.manager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.cache.WeekCache;
import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.FoodInMeal;
import com.delect.motiver.server.jdo.nutrition.FoodInMealTime;
import com.delect.motiver.server.jdo.nutrition.FoodInTime;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.MealInTime;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.server.service.MyServiceImpl;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.exception.ConnectionException;
import com.delect.motiver.shared.exception.NoPermissionException;

public class NutritionManager {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionManager.class.getName());
  
  private static NutritionManager dao; 

  public static NutritionManager getInstance() {
    if(dao == null) {
      dao = new NutritionManager();
    }
    return dao;
  }


  /**
   * Removes single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public void updateFood(Food model, long timeId, long mealId, String uid) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Updating food: '"+model.getId()+"'");
    }
    
    WeekCache cache = new WeekCache();

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {
        
        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          //if food is in meal (which is in time)
          if(timeId != 0 && mealId != 0) {
            //get 
            Time time = pm.getObjectById(Time.class, timeId);
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }

              //get meal
              for(MealInTime meal : time.getMeals()) {
                if(meal.getId() == mealId) {
                  
                  //get food
                  List<FoodInMealTime> listFoods = meal.getFoods();
                  if(listFoods != null) {
                    for(FoodInMealTime f : listFoods) {
                      if(f.getId() == model.getId()) {

                        f.setNameId(model.getNameId());
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
          else if(timeId != 0) {
            
            //get time
            Time time = pm.getObjectById(Time.class, timeId);
            if(time != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, time.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, time.getUid());
              }
              
              //get food
              List<FoodInTime> listFoods = time.getFoods();
              if(listFoods != null) {
                for(FoodInTime f : listFoods) {
                  if(f.getId() == model.getId()) {

                    f.setNameId(model.getNameId());
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
          else if(mealId != 0) {
            
            //get meal
            Meal meal = pm.getObjectById(Meal.class, mealId);
            if(meal != null) {
              //check permission
              if(!MyServiceImpl.hasPermission(pm, Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid())) {
                throw new NoPermissionException(Permission.WRITE_NUTRITION_FOODS, uid, meal.getUid());
              }
              
              //get food
              List<FoodInMeal> listFoods = meal.getFoods();
              if(listFoods != null) {
                for(FoodInMeal f : listFoods) {
                  if(f.getId() == model.getId()) {

                    f.setNameId(model.getNameId());
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
  }
  

}
