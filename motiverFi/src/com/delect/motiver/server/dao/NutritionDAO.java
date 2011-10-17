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
import com.delect.motiver.server.jdo.FoodNameCount;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.server.util.DateUtils;
import com.delect.motiver.shared.Constants;
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

  @SuppressWarnings("unchecked")
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
        try {
          if(time.getMealsKeys().size() > 0) {
            List<Object> ids = new ArrayList<Object>();
            for (Key key : time.getMealsKeys()) {
               ids.add(pm.newObjectIdInstance(Meal.class, key));
            }
            meals = (List<Meal>) pm.getObjectsById(ids);
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Error loading meals", e);
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

  @SuppressWarnings("unchecked")
  public List<Long> getMeals(int index, String uid) throws Exception {

    List<Long> list = new ArrayList<Long>();
    
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
                          
          list.add(m.getId());
          
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

  public void addTimes(List<Time> models) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      for(Time time : models) {
        pm.makePersistent(time);
      }
      tx.commit();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding times", e);
    }
    finally {
      if(tx.isActive()) {
        tx.rollback();
      }
      if (!pm.isClosed()) {
        pm.close();
      } 
    }    
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

        for(Food f : meal.getFoods()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(pm.getObjectById(FoodName.class, f.getNameId()));
          }
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
  }

  public boolean removeMeal(Meal model) throws Exception {
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
    //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          Meal t = pm.getObjectById(Meal.class, model.getId());
          
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

  public Meal getMeal(long mealId) throws Exception {

    Meal meal = null;
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      meal = pm.getObjectById(Meal.class, mealId);
      
      for(Food f : meal.getFoods()) {
        if(f.getNameId().longValue() > 0) {
          f.setName(pm.getObjectById(FoodName.class, f.getNameId()));
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
        t.update(time, false);
      }
      
      tx.commit();
      
      time.getFoods();
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if(tx.isActive()) {
        tx.rollback();
      }
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
        t.update(meal, false);
      }
      
      tx.commit();
      
      meal.getFoods();
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if(tx.isActive()) {
        tx.rollback();
      }
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }

  @SuppressWarnings("unchecked")
  public List<FoodName> getFoodNames() throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    List<FoodName> n = new ArrayList<FoodName>();
    
    try {
      
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
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return n;
  }

  @SuppressWarnings("unchecked")
  public int getFoodNameCount(UserOpenid user, Long id) throws Exception {

    int count = -1;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query qUse = pm.newQuery(FoodNameCount.class);
      qUse.setFilter("nameId == nameIdParam && openId == openIdParam");
      qUse.declareParameters("java.lang.Long nameIdParam, java.lang.String openIdParam");
      qUse.setRange(0, 1);
      List<FoodNameCount> valueCount = (List<FoodNameCount>) qUse.execute(id, user.getUid());
      if(valueCount.size() > 0) {
        count = valueCount.get(0).getCount();
      }
      
      if(count < 0) {
        count = 0;
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return count;
  }

  public void addFoodName(FoodName name) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      pm.makePersistent(name);
      tx.commit();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food name", e);
    }
    finally {
      if(tx.isActive()) {
        tx.rollback();
      }
      if (!pm.isClosed()) {
        pm.close();
      } 
    }    
  }

  /**
   * Return single meals
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<Meal> getMeals() throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    List<Meal> n = new ArrayList<Meal>();
    
    try {
      
      int i = 0;
      while(true){
        Query q = pm.newQuery(Meal.class);
        q.setFilter("timeId == null");
        q.setOrdering("name ASC");
        q.setRange(i, i+100);
        List<Meal> u = (List<Meal>) q.execute();
        n.addAll(u);
        
        if(u.size() < 100) {
          break;
        }
        i += 100;
      }
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return n;
  }


}
