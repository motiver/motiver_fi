package com.delect.motiver.server.servlet.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.cache.NutritionCache;
import com.delect.motiver.server.cache.TrainingCache;
import com.delect.motiver.server.dao.NutritionDAO;
import com.delect.motiver.server.dao.TrainingDAO;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.manager.NutritionManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.prodeagle.java.counters.Counter;

public class LoadNamesServlet extends RemoteServiceServlet {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(LoadNamesServlet.class.getName());

  /**
   * 
   */
  private static final long serialVersionUID = -5887529519599351903L;

  NutritionCache cacheNutrition = NutritionCache.getInstance();
  NutritionDAO daoNutrition = NutritionDAO.getInstance();
  TrainingCache cacheTraining = TrainingCache.getInstance();
  TrainingDAO daoTraining = TrainingDAO.getInstance();

  static boolean loadingFoodNames = false;
  static boolean loadingExerciseNames = false;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {

    String target = request.getParameter("target");

      
    if(target != null) {

      long t = System.currentTimeMillis();
      
      if(target.equals("foodname") && !loadingFoodNames) {

        try {
          loadingFoodNames = true;
          
          //load from cache
          Map<Long, FoodName> mapAll = cacheNutrition.getFoodNames();
    
          if(mapAll == null) {
    
            List<FoodName> list;
            list = daoNutrition.getFoodNames();
            
            //create map
            mapAll = new HashMap<Long, FoodName>();      
            for(FoodName name : list) {
              mapAll.put(name.getId(), name);
            }
            
            //save to cache
            cacheNutrition.setFoodNames(mapAll);
            
            Counter.increment("Task.FoodNameLoad.Latency", System.currentTimeMillis()-t);
            Counter.increment("Task.FoodNameLoad.Count");
          }
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Error running task", e);
        }
        
        loadingFoodNames = false;
      }
      
      else if(target.equals("exercisename") && !loadingExerciseNames) {

        try {
          loadingExerciseNames = true;
          
          //load from cache
          Map<Long, ExerciseName> mapAll = cacheTraining.getExerciseNames();
    
          if(mapAll == null) {
    
            List<ExerciseName> list;
            list = daoTraining.getExerciseNames();
            
            //create map
            mapAll = new HashMap<Long, ExerciseName>();      
            for(ExerciseName name : list) {
              mapAll.put(name.getId(), name);
            }
            
            //save to cache
            cacheTraining.setExerciseNames(mapAll);
            
            Counter.increment("Task.ExerciseNameLoad.Latency", System.currentTimeMillis()-t);
            Counter.increment("Task.ExerciseNameLoad.Count");
          }
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Error running task", e);
        }
        
        loadingExerciseNames = false;
      }
    }
  }
}
