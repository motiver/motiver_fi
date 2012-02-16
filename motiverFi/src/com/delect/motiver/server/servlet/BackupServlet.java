package com.delect.motiver.server.servlet;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.Measurement;
import com.delect.motiver.server.jdo.Run;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.google.appengine.api.datastore.Cursor;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BackupServlet extends RemoteServiceServlet {
  
  private static final long serialVersionUID = -3367983932040779238L;

  SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(BackupServlet.class.getName()); 

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("application/json");
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      PrintWriter writer = response.getWriter();
      
      JSONObject obj = new JSONObject();
      
      //users
      List<UserOpenid> users = getAll(pm, UserOpenid.class);
      JSONArray list = new JSONArray();      
      for(UserOpenid user : users) {
        list.add(user.getJson());
      }
      obj.put("UserOpenid", list);
      
      /*
       * TRAINING
       */
      
      //exercise names
      List<ExerciseName> names = getAll(pm, ExerciseName.class);
      JSONArray listExerciseName = new JSONArray();      
      for(ExerciseName name : names) {
        listExerciseName.add(name.getJson());
      }
      obj.put("ExerciseName", listExerciseName);
      
      //workouts
      List<Workout> workouts = getAll(pm, Workout.class);
      JSONArray listWorkout = new JSONArray();      
      for(Workout workout : workouts) {
        listWorkout.add(workout.getJson());
      }
      obj.put("Workout", listWorkout);
      
      //routines
      List<Routine> routines = getAll(pm, Routine.class);
      JSONArray listRoutine = new JSONArray();      
      for(Routine routine : routines) {
        listRoutine.add(routine.getJson());
      }
      obj.put("Routine", listRoutine);
      
      /*
       * NUTRITION
       */
      
      //food names
      List<FoodName> namesF = getAll(pm, FoodName.class);
      JSONArray listFoodName = new JSONArray();      
      for(FoodName name : namesF) {
        listFoodName.add(name.getJson());
      }
      obj.put("FoodName", listFoodName);
      
      //foodJDO
      List<FoodJDO> foods = getAll(pm, FoodJDO.class);
      JSONArray listFoodJDO = new JSONArray();      
      for(FoodJDO food : foods) {
        listFoodJDO.add(food.getJson());
      }
      obj.put("FoodJDO", listFoodJDO);
      
      //mealJDO
      List<MealJDO> meals = getAll(pm, MealJDO.class);
      JSONArray listMealJDO = new JSONArray();      
      for(MealJDO meal : meals) {
        listMealJDO.add(meal.getJson());
      }
      obj.put("MealJDO", listMealJDO);
      
      //timeJDO
      List<TimeJDO> times = getAll(pm, TimeJDO.class);
      JSONArray listTimeJDO = new JSONArray();      
      for(TimeJDO time : times) {
        listTimeJDO.add(time.getJson());
      }
      obj.put("TimeJDO", listTimeJDO);
      
      /*
       * CARDIO
       */
      
      //cardio
      List<Cardio> cardios = getAll(pm, Cardio.class);
      JSONArray listCardio = new JSONArray();      
      for(Cardio cardio : cardios) {
        listCardio.add(cardio.getJson());
      }
      obj.put("Cardio", listCardio);
      
      //run
      List<Run> runs = getAll(pm, Run.class);
      JSONArray listRun = new JSONArray();      
      for(Run run : runs) {
        listRun.add(run.getJson());
      }
      obj.put("Run", listRun);

      /*
       * MISC
       */
      
      //measurements
      List<Measurement> measurements = getAll(pm, Measurement.class);
      JSONArray listMeasurement = new JSONArray();      
      for(Measurement measurement : measurements) {
        listMeasurement.add(measurement.getJson());
      }
      obj.put("Measurement", listMeasurement);
      
      //permissions
      List<Circle> permissions = getAll(pm, Circle.class);
      JSONArray listPermission = new JSONArray();      
      for(Circle permission : permissions) {
        listPermission.add(permission.getJson());
      }
      obj.put("Permission", listPermission);
      

      //save to Google Cloud storage
      FileService fileService = FileServiceFactory.getFileService();
      GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
        .setBucket("motiver-backups")
        .setKey("backup_"+fmt.format(new Date()))
        .setAcl("project-private")
        .setMimeType("application/json");
  
      // Create new file
      AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
      FileWriteChannel writeChannel = fileService.openWriteChannel(writableFile, true);
      PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
      obj.writeJSONString(out);
      out.close();
      writeChannel.closeFinally();
    
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

  private List getAll(PersistenceManager pm, Class class1) {
    
    List list = new ArrayList();
    
    Cursor cursor = null;
    Map<String, Object> extensionMap = new HashMap<String, Object>();
    while(true){
      Query q = pm.newQuery(class1);
      q.setRange(0, 700);
      if(cursor != null) {
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        q.setExtensions(extensionMap);
      }
      List u = (List) q.execute();        
      cursor = JDOCursorHelper.getCursor(u);

      list.addAll(u);
      
      if(u.size() == 0) {
        break;
      }
    }
    
    return list;
  }
}
