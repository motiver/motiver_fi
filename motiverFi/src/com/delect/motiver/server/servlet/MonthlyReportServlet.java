/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.MonthlySummary;
import com.delect.motiver.server.jdo.MonthlySummaryExercise;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.service.MyServiceImpl;
import com.delect.motiver.shared.util.WorkoutUtils;
import com.delect.motiver.shared.util.WorkoutUtils.ExerciseInfo;
import com.google.appengine.api.datastore.Cursor;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Creates monthly summary for each user.
 * @author Antti
 *
 */
public class MonthlyReportServlet extends RemoteServiceServlet {

  private static final long serialVersionUID = 5384098111620397L;
  
  @SuppressWarnings({"unchecked", "deprecation"})
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    response.setContentType("text/html");

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      
      //get users
      List<UserOpenid> users = this.getAll(pm, UserOpenid.class);
      response.getWriter().write(users.size()+" users found.<br><br>");

      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      cal.add(Calendar.MONTH, -1);
      
      //get date from parameters
      try {
        if(request.getParameter("month") != null && request.getParameter("year") != null) {
          cal.set(Calendar.MONTH, Integer.parseInt(request.getParameter("month"))-1);
          cal.set(Calendar.YEAR, Integer.parseInt(request.getParameter("year")));
        }
      } catch (Exception e1) {
        response.getWriter().write("error1: "+e1);
      }
      
      //get dates
      cal.set(Calendar.DATE, 1);
      Date d1 = cal.getTime();
      cal.add(Calendar.MONTH, 1);
      cal.add(Calendar.DATE, -1);
      Date d2 = cal.getTime();
      final Date dStart = MyServiceImpl.stripTime(d1, true);
      final Date dEnd = MyServiceImpl.stripTime(d2, false);
      cal.set(Calendar.DATE, 1);

      response.getWriter().write(dStart+" - "+dEnd+"<br>");
      
      
      for(UserOpenid user : users) {
        try {
          
          //remove all this month's data
          Query qD = pm.newQuery(MonthlySummary.class);
          qD.setFilter("openId == openIdParam && date >= dateParam");
          qD.declareParameters("java.lang.String openIdParam, java.util.Date dateParam");
          List<MonthlySummary> data = (List<MonthlySummary>) qD.execute(user.getUid(), dStart);
          pm.deletePersistentAll(data);

          //get this months workouts
          Query qW = pm.newQuery(Workout.class);
          qW.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
          qW.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          List<Workout> workouts = (List<Workout>) qW.execute(user.getUid(), dStart, dEnd);

          if(workouts.size() > 0) {

            response.getWriter().write(user.getEmail()+"<br>");
            response.getWriter().write("<table width='100%' border=1 cellpadding=5>");
            response.getWriter().write("<tr><td>nameId"+
                "<td>Sets"+
                "<td>reps string"+
                "<td><b>reps</b>"+
                "<td>weights string"+
                "<td><b>weights</b>"+
                "<td>max"+
                "<td>length"+
                "<td>work"+
                "</td></tr>");

            //hashtables which we save one rep maxes and best exercises
            Hashtable<Long, MonthlySummaryExercise> tableMax = new Hashtable<Long, MonthlySummaryExercise>();
            Hashtable<String, MonthlySummaryExercise> tableBest = new Hashtable<String, MonthlySummaryExercise>();
                      
            //model
            final MonthlySummary model = new MonthlySummary(cal.getTime());
            model.setUid(user.getUid());
            final List<MonthlySummaryExercise> list = new ArrayList<MonthlySummaryExercise>();
            
            //go through each workouts
            for(Workout w : workouts) {
              response.getWriter().write("<tr><td colspan=9>"+w.getName()+" - "+w.getDate()+"</td></tr>");
              for(Exercise e : w.getExercises()) {
                try {
                  final long nameId = e.getNameId();
    
                  if(nameId > 0) {

                    ExerciseInfo info = new ExerciseInfo(e.getSets(), e.getReps(), e.getWeights());
                    WorkoutUtils.parseExercise(info);
                    
                    if(!info.isOk()) {
                      continue;
                    }
                    
                    double[] re = info.reps;
                    double[] we = info.weights;
                    
                    response.getWriter().write("<tr><td>"+nameId+
                        "<td>"+e.getSets()+
                        "<td>"+e.getReps()+
                        "<td><b>"+((re.length == we.length)? Arrays.toString(re) : "ERR")+"</b>"+
                        "<td>"+e.getWeights()+
                        "<td><b>"+((re.length == we.length)? Arrays.toString(we) : "ERR")+"</b>"+
                        "<td>"+info.max+
                        "<td>"+info.sets+
                        "<td>"+info.work+
                        "</td></tr>");

                    //create model
                    MonthlySummaryExercise modelE = new MonthlySummaryExercise(0, info.max);
                    modelE.setReps(e.getReps());
                    modelE.setSets(e.getSets());
                    modelE.setWeights(e.getWeights());
                    modelE.setWorkoutDate(w.getDate());
                    modelE.setLength(info.sets);
                    modelE.setNameId(e.getNameId());
                    modelE.setUid(user.getUid());
                    
                    //set date
                    cal.setTime(w.getDate());
                    
                    //if max found
                    if(info.max > 0) {
                      //save max
                      if(tableMax.containsKey(nameId)) {
                        if(tableMax.get(nameId).getValue() < info.max) {
                          tableMax.put(nameId, modelE);
                        }
                      }
                      else {
                        tableMax.put(nameId, modelE);
                      }
                    }
                    //save best exercises
                    else {
                      modelE.setType(1, info.work);
                      
                      boolean found = false;
                      if(tableBest.containsKey(nameId+"_"+info.sets)) {
                        //if lower value and same length
                        if(tableBest.get(nameId+"_"+info.sets).getValue() < info.work) {
                          tableBest.put(nameId+"_"+info.sets, modelE);
                          found = true;
                        }
                      }
                      
                      //if not found -> insert new
                      if(!found) {
                        tableBest.put(nameId+"_"+info.sets, modelE);
                      }
                    }
                  }
                  
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
              }
            }
            
            //save max results
            response.getWriter().write("<tr><td colspan=9>Max</td></tr>");
            Set<Long> set = tableMax.keySet();
            Iterator<Long> itr = set.iterator();
            while (itr.hasNext()) {
              long nameId = itr.next();
              MonthlySummaryExercise modelE = tableMax.get(nameId);
              
              //check if personal best
              Query qIsBest = pm.newQuery(MonthlySummaryExercise.class);
              qIsBest.setFilter("openId == openIdParam && nameId == nameIdParam && type == 0 && length == lengthParam && value > valueParam");
              qIsBest.declareParameters("java.lang.String openIdParam, java.lang.Long nameIdParam, java.lang.Integer lengthParam, java.lang.Double valueParam");
              List<MonthlySummaryExercise> dataIsBest = (List<MonthlySummaryExercise>)qIsBest.executeWithArray( new Object[] {user.getUid(), nameId, modelE.getLength(), modelE.getValue()} );
              modelE.setPersonalBest(dataIsBest.size() == 0);
              
              //add to list
              list.add(modelE);
              
              response.getWriter().write("<tr><td>"+nameId+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+modelE.getValue()+
                  "</td></tr>");
            }
            
            //print best exercises
            response.getWriter().write("<tr><td colspan=9>Best</td></tr>");
            Set<String> set2 = tableBest.keySet();
            Iterator<String> itr2 = set2.iterator();
            while (itr2.hasNext()) {
              String nameId = itr2.next();
              MonthlySummaryExercise modelE = tableBest.get(nameId);
              
              //check if personal best
              Query qIsBest = pm.newQuery(MonthlySummaryExercise.class);
              qIsBest.setFilter("openId == openIdParam && nameId == nameIdParam && type == 1 && length == lengthParam && value > valueParam");
              qIsBest.declareParameters("java.lang.String openIdParam, java.lang.Long nameIdParam, java.lang.Integer lengthParam, java.lang.Double valueParam");
              qIsBest.setRange(0, 1);
              List<MonthlySummaryExercise> dataIsBest = (List<MonthlySummaryExercise>)qIsBest.executeWithArray( new Object[] {user.getUid(), modelE.getNameId(), modelE.getLength(), modelE.getValue()} );
              modelE.setPersonalBest(dataIsBest.size() == 0);
              
              //add to list
              list.add(modelE);
              
              response.getWriter().write("<tr><td>"+nameId+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+
                  "<td>"+modelE.getValue()+
                  "</td></tr>");
            }
            
            boolean found = false;
            
            if(list.size() > 0) {
              model.setExercises(list);
              found = true;
            }
            
            //if something found -> save entity
            if(found) {
              pm.makePersistent(model); 
            }
            
            response.getWriter().write("</table>");
          }
          
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (!pm.isClosed()) {
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
