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
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.ExerciseNameCount;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.util.DateUtils;
import com.delect.motiver.shared.Constants;

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


  @SuppressWarnings("unchecked")
  public List<Workout> getWorkouts(Date date, String uid) throws Exception {
    
    ArrayList<Workout> list = new ArrayList<Workout>();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      //strip workout
      final Date dStart = DateUtils.stripTime(date, true);
      final Date dEnd = DateUtils.stripTime(date, false);
      
      Query q = pm.newQuery(Workout.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Workout> workouts = (List<Workout>) q.execute(uid, dStart, dEnd);
      for(Workout workout : workouts) {
        Workout copy = pm.detachCopy(workout);
        
        //get exercises
        copy.setExercises(new ArrayList<Exercise>(pm.detachCopyAll(workout.getExercises())));
        
        //find names for each exercise
        for(Exercise f : copy.getExercises()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(pm.getObjectById(ExerciseName.class, f.getNameId()));
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

  /**
   * Return single workouts
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public List<Workout> getWorkouts() throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    List<Workout> n = new ArrayList<Workout>();
    
    try {
      
      int i = 0;
      while(true){
        Query q = pm.newQuery(Workout.class);
        q.setFilter("routineId == 0");
        q.setOrdering("name ASC");
        q.setRange(i, i+100);
        List<Workout> u = (List<Workout>) q.execute();
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
  public Routine getRoutine(long routineId) throws Exception {

    Routine routine = null;
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      routine = pm.getObjectById(Routine.class, routineId);
      
      //get workouts
      Query q = pm.newQuery(Workout.class);
      q.setFilter("date == null && routineId == routineIdParam");
      q.declareParameters("java.lang.Long routineIdParam");
      List<Workout> workouts = (List<Workout>) q.execute(routine.getId());
      
      for(Workout w : workouts) {
        //find names for each exercise
        for(Exercise f : w.getExercises()) {
          if(f.getNameId().longValue() > 0) {
            f.setName(pm.getObjectById(ExerciseName.class, f.getNameId()));
          }
        }
      }
      
      routine.setWorkouts(workouts);
      
    } catch (Exception e) {
      throw e;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return routine;
  }

  public void updateWorkout(Workout workout) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      Workout t = pm.getObjectById(Workout.class, workout.getId());
      
      if(t != null) {
        t.update(workout);
      }
      
      tx.commit();
      
      workout.getExercises();
      
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

  public void updateRoutine(Routine routine) throws Exception {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Transaction tx = pm.currentTransaction();
    tx.begin();
    
    try {
      
      Routine t = pm.getObjectById(Routine.class, routine.getId());
      
      if(t != null) {
        t.update(routine);
      }
      
      tx.commit();
      
      routine.getWorkouts();
      
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
  public List<Long> getWorkouts(int index, String uid) throws Exception {

    List<Long> list = new ArrayList<Long>();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(Workout.class);
      q.setFilter("openId == openIdParam && routineId == 0");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(index, index + Constants.LIMIT_MEALS + 1);
      List<Workout> workouts = (List<Workout>) q.execute(uid);
            
      //get workouts
      if(workouts != null) {        
        int i = 0;
        for(Workout m : workouts) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_WORKOUTS) {
            list.add(null);
            break;
          }
          
          //find names for each exercise
          for(Exercise f : m.getExercises()) {
            if(f.getNameId().longValue() > 0) {
              f.setName(pm.getObjectById(ExerciseName.class, f.getNameId()));
            }
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


  public boolean removeWorkouts(Long[] keys, String uid) throws Exception {
    
    boolean ok = false;

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      
      for (Long key : keys) {
        
      //try to update X workouts
      int retries = Constants.LIMIT_UPDATE_RETRIES;
      while (true) {

          
          Transaction tx = pm.currentTransaction();
          tx.begin();
          
          try {
            
            Workout t = pm.getObjectById(Workout.class, key);
            
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
      
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing workouts", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    
    return ok;
  }

}
