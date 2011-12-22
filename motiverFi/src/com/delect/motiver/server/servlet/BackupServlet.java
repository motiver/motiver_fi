package com.delect.motiver.server.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.Cardio;
import com.delect.motiver.server.jdo.Run;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.manager.TrainingManager;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.repackaged.org.json.JSONWriter;
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
      
      //json writer
      JSONWriter writerJson = new JSONWriter(writer);
    
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      writerJson.object();
      writerJson.key("UserOpenid").array();
      
      for(UserOpenid user : users) {
        writerJson.object();
        
        //user
        user.getJson(writerJson);
        
        //cardio
        List<Cardio> cardios = getCardios(pm, user);
        writerJson.key("Cardio").array();
        for(Cardio cardio : cardios) {
          writerJson.object();
          cardio.getJson(writerJson);
          writerJson.endObject();
        }
        writerJson.endArray();
        
        //run
        List<Run> runs = getRuns(pm, user);
        writerJson.key("Run").array();
        for(Run run : runs) {
          writerJson.object();
          run.getJson(writerJson);
          writerJson.endObject();
        }
        writerJson.endArray();
        
        //workout
//        List<Workout> workouts = getWorkouts(pm, user);
//        writerJson.key("Workout").array();
//        for(Workout workout : workouts) {
//          writerJson.object();
//          workout.getJson(writerJson);
//          writerJson.endObject();
//        }
//        writerJson.endArray();
        
        writerJson.endObject();
      }
      
      writerJson.endArray();
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
