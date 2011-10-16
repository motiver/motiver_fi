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
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.ExerciseNameCount;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.util.DateUtils;
import com.delect.motiver.shared.Constants;
import com.google.appengine.api.datastore.Key;

public class TrainingDAO {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(TrainingDAO.class.getName());
  
  private static TrainingDAO dao; 

  public static TrainingDAO getInstance() {
    if(dao == null) {
      dao = new TrainingDAO();
    }
    return dao;
  }


  public void addWorkouts(List<Workout> models) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      for(Workout workout : models) {
        pm.makePersistent(workout);

        for(Exercise f : workout.getExercises()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(pm.getObjectById(ExerciseName.class, f.getNameId()));
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

  public boolean removeWorkout(Workout model) throws Exception {
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
    //try to update X times
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

        Transaction tx = pm.currentTransaction();
        tx.begin();
        
        try {
          
          Workout t = pm.getObjectById(Workout.class, model.getId());
          
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
          logger.log(Level.WARNING, "Error deleting workout", e);
          
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

  public Workout getWorkout(long workoutId) throws Exception {

    Workout workout = null;
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      workout = pm.getObjectById(Workout.class, workoutId);
      
      for(Exercise f : workout.getExercises()) {
        if(f.getNameId().longValue() > 0) {
          f.setName(pm.getObjectById(ExerciseName.class, f.getNameId()));
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
    
    return workout;
  }

  @SuppressWarnings("unchecked")
  public List<ExerciseName> getExerciseNames() throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    List<ExerciseName> n = new ArrayList<ExerciseName>();
    
    try {
      
      int i = 0;
      while(true){
        Query q = pm.newQuery(ExerciseName.class);
        q.setRange(i, i+100);
        List<ExerciseName> u = (List<ExerciseName>) q.execute();
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
  public int getExerciseNameCount(UserOpenid user, Long id) throws Exception {

    int count = -1;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query qUse = pm.newQuery(ExerciseNameCount.class);
      qUse.setFilter("nameId == nameIdParam && openId == openIdParam");
      qUse.declareParameters("java.lang.Long nameIdParam, java.lang.String openIdParam");
      qUse.setRange(0, 1);
      List<ExerciseNameCount> valueCount = (List<ExerciseNameCount>) qUse.execute(id, user.getUid());
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

  public void addExerciseName(ExerciseName name) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      pm.makePersistent(name);
      tx.commit();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise name", e);
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
  

}
