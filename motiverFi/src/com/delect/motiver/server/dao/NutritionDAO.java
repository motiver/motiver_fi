package com.delect.motiver.server.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.FoodInMealTime;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.MealInTime;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.server.util.DateUtils;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.google.appengine.api.datastore.Key;

public class NutritionDAO {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionDAO.class.getName());
  
  private static NutritionDAO dao; 

  public static NutritionDAO getInstance() {
    if(dao == null) {
      dao = new NutritionDAO();
    }
    return dao;
  }

  public List<Time> getTimes(Date date, String uid) throws Exception {
    
    ArrayList<Time> list = new ArrayList<Time>();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      //strip time
      final Date dStart = DateUtils.stripTime(date, true);
      final Date dEnd = DateUtils.stripTime(date, false);
      
      Query q = pm.newQuery(Time.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Time> times = (List<Time>) q.execute(uid, dStart, dEnd);
      for(Time time : times) {
        Time copy = pm.detachCopy(time);
        //get meals
        List<Meal> meals = new ArrayList<Meal>();
        if(time.getMealsKeys().size() > 0) {
          List<Object> ids = new ArrayList<Object>();
          for (Key key : time.getMealsKeys()) {
             ids.add(pm.newObjectIdInstance(Meal.class, key));
          }
          meals = (List<Meal>) pm.getObjectsById(ids);
        }
        copy.setMealsNew(meals);
        
        //get foods
        copy.setFoods(new ArrayList<Food>(pm.detachCopyAll(time.getFoods())));
        
        //find names for each food
        for(Meal m : copy.getMealsNew()) {
          for(Food f : m.getFoods()) {
            if(f.getNameId().longValue() > 0) {
              f.setName(pm.getObjectById(FoodName.class, f.getNameId()));
            }
          }
        }
        for(Food f : copy.getFoods()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(pm.getObjectById(FoodName.class, f.getNameId()));
          }
        }
        
        list.add(copy);
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

  public List<Meal> getMeals(int index, String uid) throws Exception {

    List<Meal> list = new ArrayList<Meal>();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(Meal.class);
      q.setFilter("openId == openIdParam && timeId == null");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(index, index + Constants.LIMIT_MEALS + 1);
      List<Meal> meals = (List<Meal>) q.execute(uid);
            
      //get meals
      if(meals != null) {        
        int i = 0;
        for(Meal m : meals) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_MEALS) {
            list.add(null);
            break;
          }
                          
          list.add(m);
          
          i++;
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

  public boolean removeTimes(Long[] keys, String uid) throws Exception {
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      
      for (Long key : keys) {
        
      //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

          
          Transaction tx = pm.currentTransaction();
          tx.begin();
          
          try {
            
            Time t = pm.getObjectById(Time.class, key);
            
            if(t != null) {
              
              pm.deletePersistent(t);
              tx.commit();
    
              ok = true;
              break;
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

  public List<Time> addTimes(List<Time> models) throws Exception {
    
    List<Time> list = new ArrayList<Time>();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      pm.makePersistentAll(models);
      
      for(Time time : models) {
        Time copy = pm.detachCopy(time);
        copy.setMeals(new ArrayList<MealInTime>(pm.detachCopyAll(time.getMeals())));
        copy.setFoods(new ArrayList<Food>(pm.detachCopyAll(time.getFoods())));
        
        //find names for each food
        for(MealInTime m : copy.getMeals()) {
          for(FoodInMealTime f : m.getFoods()) {
            if(f.getNameId().longValue() > 0) {
              f.setName(pm.detachCopy(pm.getObjectById(FoodName.class, f.getNameId())));
            }
          }
        }
        for(Food f : copy.getFoods()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(pm.detachCopy(pm.getObjectById(FoodName.class, f.getNameId())));
          }
        }
        
        list.add(copy);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding times", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return list;
    
  }

  public Time addMeals(long timeId, List<Meal> models) throws Exception {

    Time t = null;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      //save meals
      for(Meal meal : models) {
        meal.setTime(timeId);
        
        pm.makePersistent(meal);
        meal.getFoods();
      }
      pm.flush();
      
      t = pm.getObjectById(Time.class, timeId);
  
      if(t != null) {
        List<Key> arr = t.getMealsKeys();
        
        for(Meal m : models) {
          arr.add(m.getKey());
        }
        
        pm.makePersistent(t);
        pm.flush();
        
        t = pm.detachCopy(t);
      }
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return t;    
  }

  public void addMeals(List<Meal> models) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      for(Meal meal : models) {
        pm.makePersistent(meal);
        meal.getFoods();
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }

  public boolean removeMeal(long id, String uid) throws Exception {
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
    //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          Meal t = pm.getObjectById(Meal.class, id);
          
          if(t != null) {
            
            pm.deletePersistent(t);
            tx.commit();
  
            ok = true;
            break;
          }
          
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
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return ok;
  }

  public Time removeMeal(long id, long timeId, String uid) throws Exception {
    
    Time t = null;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
    //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          t = pm.getObjectById(Time.class, timeId);
          
          if(t != null) {
            
            for(MealInTime m : t.getMeals()) {
              if(m.getId().longValue() == id) {
                t.getMeals().remove(m);
                break;
              }
            }
            pm.flush();
            tx.commit();

            t = pm.detachCopy(t);
            
            break;
          }
          
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
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return t;
  }

  public Meal getMeal(long mealId) throws Exception {

    Meal meal = null;
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      meal = pm.getObjectById(Meal.class, mealId);
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return meal;
  }

  public Time getTime(long timeId) throws Exception {
    
    Time t = null;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      t = pm.getObjectById(Time.class, timeId);
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return t;
  }

  public void updateTime(Time time) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      Time t = pm.getObjectById(Time.class, time.getId());
      
      if(t != null) {
        t.update(time);
      }
      
      tx.commit();
      
      time.getFoods();
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }

  public void updateMeal(Meal meal) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      Meal t = pm.getObjectById(Meal.class, meal.getId());
      
      if(t != null) {
        t.update(meal);
      }
      
      tx.commit();
      
      meal.getFoods();
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }


}
