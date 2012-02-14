package com.delect.motiver.server.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.Cardio;
import com.delect.motiver.server.jdo.Measurement;
import com.delect.motiver.server.jdo.Permission;
import com.delect.motiver.server.jdo.Run;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.manager.TrainingManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BackupServlet extends RemoteServiceServlet {
  
  private static final long serialVersionUID = -3367983932040779238L;
  
  private static TrainingManager trainingManager = TrainingManager.getInstance();
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(BackupServlet.class.getName()); 

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("text/plain");
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    try {
      PrintWriter writer = response.getWriter();
      
      JSONObject obj = new JSONObject();
      
      //users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      JSONArray list = new JSONArray();      
      for(UserOpenid user : users) {
        list.add(user.getJson());
      }
      obj.put("UserOpenid", list);
      

      /*
       * TRAINING
       */
      
      //exercise names
      q = pm.newQuery(ExerciseName.class);
      List<ExerciseName> names = (List<ExerciseName>) q.execute();
      JSONArray listExerciseName = new JSONArray();      
      for(ExerciseName name : names) {
        listExerciseName.add(name.getJson());
      }
      obj.put("ExerciseName", listExerciseName);
      
      //workouts
      q = pm.newQuery(Workout.class);
      List<Workout> workouts = (List<Workout>) q.execute();
      JSONArray listWorkout = new JSONArray();      
      for(Workout workout : workouts) {
        listWorkout.add(workout.getJson());
      }
      obj.put("Workout", listWorkout);
      
      //routines
      q = pm.newQuery(Routine.class);
      List<Routine> routines = (List<Routine>) q.execute();
      JSONArray listRoutine = new JSONArray();      
      for(Routine routine : routines) {
        listRoutine.add(routine.getJson());
      }
      obj.put("Routine", listRoutine);
      
      /*
       * NUTRITION
       */
      
      //food names
      q = pm.newQuery(FoodName.class);
      List<FoodName> namesF = (List<FoodName>) q.execute();
      JSONArray listFoodName = new JSONArray();      
      for(FoodName name : namesF) {
        listFoodName.add(name.getJson());
      }
      obj.put("FoodName", listFoodName);
      
      //foodJDO
      q = pm.newQuery(FoodJDO.class);
      List<FoodJDO> foods = (List<FoodJDO>) q.execute();
      JSONArray listFoodJDO = new JSONArray();      
      for(FoodJDO food : foods) {
        listFoodJDO.add(food.getJson());
      }
      obj.put("FoodJDO", listFoodJDO);
      
      //mealJDO
      q = pm.newQuery(MealJDO.class);
      List<MealJDO> meals = (List<MealJDO>) q.execute();
      JSONArray listMealJDO = new JSONArray();      
      for(MealJDO meal : meals) {
        listMealJDO.add(meal.getJson());
      }
      obj.put("MealJDO", listMealJDO);
      
      //timeJDO
      q = pm.newQuery(TimeJDO.class);
      List<TimeJDO> times = (List<TimeJDO>) q.execute();
      JSONArray listTimeJDO = new JSONArray();      
      for(TimeJDO time : times) {
        listTimeJDO.add(time.getJson());
      }
      obj.put("TimeJDO", listTimeJDO);
      
      /*
       * CARDIO
       */
      
      //cardio
      q = pm.newQuery(Cardio.class);
      List<Cardio> cardios = (List<Cardio>) q.execute();
      JSONArray listCardio = new JSONArray();      
      for(Cardio cardio : cardios) {
        listCardio.add(cardio.getJson());
      }
      obj.put("Cardio", listCardio);
      
      //run
      q = pm.newQuery(Run.class);
      List<Run> runs = (List<Run>) q.execute();
      JSONArray listRun = new JSONArray();      
      for(Run run : runs) {
        listRun.add(run.getJson());
      }
      obj.put("Run", listRun);

      /*
       * MISC
       */
      
      //measurements
      q = pm.newQuery(Measurement.class);
      List<Measurement> measurements = (List<Measurement>) q.execute();
      JSONArray listMeasurement = new JSONArray();      
      for(Measurement measurement : measurements) {
        listMeasurement.add(measurement.getJson());
      }
      obj.put("Measurement", listMeasurement);
      
      //permissions
      q = pm.newQuery(Permission.class);
      List<Permission> permissions = (List<Permission>) q.execute();
      JSONArray listPermission = new JSONArray();      
      for(Permission permission : permissions) {
        listPermission.add(permission.getJson());
      }
      obj.put("Permission", listPermission);
      
      obj.writeJSONString(writer);
      writer.flush();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
      for(StackTraceElement el : e.getStackTrace()) {
        logger.log(Level.INFO, el.toString());
      }
    }
    finally {
      if(!pm.isClosed()) {
        pm.close();
      }
    }
    
  }
}
