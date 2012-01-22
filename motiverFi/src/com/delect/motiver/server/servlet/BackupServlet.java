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
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.cardio.Cardio;
import com.delect.motiver.server.jdo.cardio.Run;
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
    
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      JSONObject obj = new JSONObject();
      
      JSONArray list = new JSONArray();      
      for(UserOpenid user : users) {
        JSONObject objU = new JSONObject();
        
        //user
        objU.put("UserOpenid", user.getJson());
        
        //cardio
        List<Cardio> cardios = getCardios(pm, user);
        JSONArray listC = new JSONArray();  
        for(Cardio cardio : cardios) {
          listC.add(cardio.getJson());
        }
        objU.put("Cardio", list);
        
        //run
        List<Run> runs = getRuns(pm, user);
        JSONArray listR = new JSONArray();    
        for(Run run : runs) {
          listC.add(run.getJson());
        }
        objU.put("Run", list);
        
        //workout
//        List<Workout> workouts = getWorkouts(pm, user);
//        writerJson.key("Workout").array();
//        for(Workout workout : workouts) {
//          writerJson.object();
//          workout.getJson(writerJson);
//          writerJson.endObject();
//        }
//        writerJson.endArray();
      }
      
      obj.put("UserOpenid", list);

      
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

  @SuppressWarnings("unchecked")
  private List<Cardio> getCardios(PersistenceManager pm, UserOpenid user) {
    
    List<Cardio> data = new ArrayList<Cardio>();
    
    Cursor cursor = null;
    Map<String, Object> extensionMap = new HashMap<String, Object>();
    //get using cursors
    while(true){
      Query q = pm.newQuery(Cardio.class);
      q.setFilter("openId == openidParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(0, 700);
      if(cursor != null) {
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        q.setExtensions(extensionMap);
      }
      List<Cardio> u = (List<Cardio>) q.execute(user.getUid());        
      cursor = JDOCursorHelper.getCursor(u);

      data.addAll(u);
      
      if(u.size() == 0) {
        break;
      }
    }
    
    return data;
  }

  @SuppressWarnings("unchecked")
  private List<Run> getRuns(PersistenceManager pm, UserOpenid user) {
    
    List<Run> data = new ArrayList<Run>();
    
    Cursor cursor = null;
    Map<String, Object> extensionMap = new HashMap<String, Object>();
    //get using cursors
    while(true){
      Query q = pm.newQuery(Run.class);
      q.setFilter("openId == openidParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(0, 700);
      if(cursor != null) {
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        q.setExtensions(extensionMap);
      }
      List<Run> u = (List<Run>) q.execute(user.getUid());        
      cursor = JDOCursorHelper.getCursor(u);

      data.addAll(u);
      
      if(u.size() == 0) {
        break;
      }
    }
    
    return data;
  }

  @SuppressWarnings("unchecked")
  private List<Workout> getWorkouts(PersistenceManager pm, UserOpenid user) {
    
    List<Workout> data = new ArrayList<Workout>();
    
    Cursor cursor = null;
    Map<String, Object> extensionMap = new HashMap<String, Object>();
    //get using cursors
    while(true){
      Query q = pm.newQuery(Workout.class);
      q.setFilter("openId == openidParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(0, 700);
      if(cursor != null) {
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        q.setExtensions(extensionMap);
      }
      List<Workout> u = (List<Workout>) q.execute(user.getUid());        
      cursor = JDOCursorHelper.getCursor(u);

      data.addAll(u);
      
      if(u.size() == 0) {
        break;
      }
    }
    
    return data;
  }
}
