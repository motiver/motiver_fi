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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.PMF;
import com.delect.motiver.server.cache.WeekCache;
import com.delect.motiver.server.jdo.Exercise;
import com.delect.motiver.server.jdo.ExerciseNameCount;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.Workout;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ExerciseCountServlet extends RemoteServiceServlet {
  
  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(ExerciseCountServlet.class.getName()); 

  private static final long serialVersionUID = 5384098111620397L;

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    WeekCache cache = new WeekCache();
    
    response.setContentType("text/html");
    
    try {
      
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      for(UserOpenid user : users) {
        try {
//          response.getWriter().write(user.getEmail()+"<br>");
          
          Hashtable<Long, Integer> tableExercises = new Hashtable<Long, Integer>();

          //get times (in chunks)
          int countWorkouts = 0;
          while(true) {
            //get workouts
            Query qW = pm.newQuery(Workout.class);
            qW.setFilter("openId == openIdParam");
            qW.declareParameters("java.lang.String openIdParam");
            qW.setRange(countWorkouts, countWorkouts+100);
            List<Workout> workouts = (List<Workout>) qW.execute(user.getUid());

            int s = workouts.size();
//            response.getWriter().write("Workouts found: "+s+" ("+countWorkouts+")<br>");
            
            if(s == 0) {
              break;
            }
            
            countWorkouts += s;
            
            //go through each workouts
            for(Workout w : workouts) {
              for(Exercise e : w.getExercises()) {
                final long nameId = e.getNameId();
                
                //if name found
                if(nameId > 0) {
                  //if id found -> add one to count
                  int count = 0;
                  if(tableExercises.containsKey(nameId)) {
                    count = tableExercises.get(nameId);
                  }
                  count++;
                  tableExercises.put(nameId, count);
                }
              }
            }
          }
          
          //save each count to datastore
          List<ExerciseNameCount> counts = new ArrayList<ExerciseNameCount>();
          
          Set<Long> set = tableExercises.keySet();
          Iterator<Long> itr = set.iterator();
          for(int i = 0; i < tableExercises.size(); i++) {
            Long nameId = itr.next();
//            response.getWriter().write("      "+nameId + ": " + tableExercises.get(nameId)+"<br>");
            
            //Create model
            int count = tableExercises.get(nameId);
            ExerciseNameCount model = new ExerciseNameCount(nameId, count, user.getUid());
            counts.add(model);

            //update cache
            cache.addExerciseNameCount(nameId, user.getUid(), count);
          }
          
          pm.makePersistentAll(counts);
          pm.flush();
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "Error loading data from user: "+user.getUid(), e);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading data", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }
}
