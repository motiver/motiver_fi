/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.Exercise;
import com.delect.motiver.server.ExerciseNameCount;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.UserOpenid;
import com.delect.motiver.server.Workout;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ExerciseCountServlet extends RemoteServiceServlet {

  private static final long serialVersionUID = 5384098111620397L;

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //remove old count values
      Query qC = pm.newQuery(ExerciseNameCount.class);
      List<ExerciseNameCount> values = (List<ExerciseNameCount>) qC.execute();
      pm.deletePersistentAll(values);
      
      //get users
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      for(UserOpenid user : users) {
        try {
          response.getWriter().write(user.getEmail()+"\n");
          
          Hashtable<Long, Integer> tableExercises = new Hashtable<Long, Integer>();
          
          //get workouts
          Query qW = pm.newQuery(Workout.class);
          qW.setFilter("uid == uidParam");
          qW.declareParameters("java.lang.Long uidParam");
          List<Workout> workouts = (List<Workout>) qW.execute(user.getUid());
          
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
          
          //save each count to datastore
          List<ExerciseNameCount> counts = new ArrayList<ExerciseNameCount>();
          
          Set<Long> set = tableExercises.keySet();
          Iterator<Long> itr = set.iterator();
          for(int i = 0; i < tableExercises.size(); i++) {
            Long nameId = itr.next();
            response.getWriter().write("      "+nameId + ": " + tableExercises.get(nameId)+"\n");
            
            //Create model
            ExerciseNameCount model = new ExerciseNameCount(nameId, tableExercises.get(nameId), user.getUid());
            counts.add(model);
          }
          
          pm.makePersistentAll(counts);
          pm.flush();
          
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
}
