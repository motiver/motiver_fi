/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.Comment;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.UserOpenid;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Creates monthly summary for each user.
 * @author Antti
 *
 */
public class TestServlet extends RemoteServiceServlet {

  private static final long serialVersionUID = 53840910397L;
  
  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    response.setContentType("text/html");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
//    long uid = (request.getParameter("uid") != null)? Long.parseLong(request.getParameter("uid")) : 707249003L;  //for testing
    
    try {
      PrintWriter out = response.getWriter();
      out.write("a.<br>");
      
      long uid_antti = 707249003L;
      String openid_antti = null;
      long uid_pekka = 799812870L;
      String openid_pekka = null;
      
      //find openIds
      //antti
      Query q = pm.newQuery(UserOpenid.class);
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      for(UserOpenid user : users) {
        if(user.getUid() != null && user.getNickName() != null ) {
          if(user.getNickName().contains("antti")) {
            openid_antti = user.getUid();
          }
          else if(user.getNickName().contains("pekka")) {
            openid_pekka = user.getUid();
          }
        }
      }
      out.write(openid_antti+".<br>");
      out.write(openid_pekka+".<br>");
      
      //data: Cardio
      int count_antti = 0;
      int count_pekka = 0;
//      q = pm.newQuery(Cardio.class);
//      List<Cardio> arrCardio = (List<Cardio>) q.execute();
//      for(Cardio c : arrCardio) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>Cardio: "+count_antti+" - "+count_pekka);
//      
//      //data: CardioValue
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(CardioValue.class);
//      List<CardioValue> arrCardioValue = (List<CardioValue>) q.execute();
//      for(CardioValue c : arrCardioValue) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>CardioValue: "+count_antti+" - "+count_pekka);
      
      //data: Comment
      count_antti = 0;
      count_pekka = 0;
      q = pm.newQuery(Comment.class);
      List<Comment> arrComment = (List<Comment>) q.execute();
      for(Comment c : arrComment) {
        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
          c.setUid(openid_antti);
          c.setUidTarget(openid_antti);
          count_antti++;
        }
        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
          c.setUid(openid_pekka);
          c.setUidTarget(openid_pekka);
          count_pekka++;
        }
      }
      pm.flush();
      out.write("<br>Comment: "+count_antti+" - "+count_pekka);
      
      //data: ExerciseName
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(ExerciseName.class);
//      List<ExerciseName> arrExerciseName = (List<ExerciseName>) q.execute();
//      for(ExerciseName c : arrExerciseName) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>ExerciseName: "+count_antti+" - "+count_pekka);
//      
//      //data: Food
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Food.class);
//      List<Food> arrFood = (List<Food>) q.execute();
//      for(Food c : arrFood) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>Food: "+count_antti+" - "+count_pekka);
//      
//      //data: FoodName
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(FoodName.class);
//      List<FoodName> arrFoodName = (List<FoodName>) q.execute();
//      for(FoodName c : arrFoodName) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>FoodName: "+count_antti+" - "+count_pekka);
//      
//      //data: GuideValue
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(GuideValue.class);
//      List<GuideValue> arrGuideValue = (List<GuideValue>) q.execute();
//      for(GuideValue c : arrGuideValue) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>GuideValue: "+count_antti+" - "+count_pekka);
//      
//      //data: Meal
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Meal.class);
//      List<Meal> arrMeal = (List<Meal>) q.execute();
//      for(Meal c : arrMeal) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>Meal: "+count_antti+" - "+count_pekka);
//      
//      //data: Measurement
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Measurement.class);
//      List<Measurement> arrMeasurement = (List<Measurement>) q.execute();
//      for(Measurement c : arrMeasurement) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>Measurement: "+count_antti+" - "+count_pekka);
//      
//      //data: MeasurementValue
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(MeasurementValue.class);
//      List<MeasurementValue> arrMeasurementValue = (List<MeasurementValue>) q.execute();
//      for(MeasurementValue c : arrMeasurementValue) {
//        if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//          c.setUid(openid_antti);
//          count_antti++;
//        }
//        else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//          c.setUid(openid_pekka);
//          count_pekka++;
//        }
//      }
//      pm.flush();
//      out.write("<br>MeasurementValue: "+count_antti+" - "+count_pekka);
//      
//      //data: MicroNutrient
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(MicroNutrient.class);
//      List<MicroNutrient> arrMicroNutrient = (List<MicroNutrient>) q.execute();
//      for(MicroNutrient c : arrMicroNutrient) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>MicroNutrient: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>MicroNutrient: "+count_antti+" - "+count_pekka);
//      
//      //data: MonthlySummary
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(MonthlySummary.class);
//      List<MonthlySummary> arrMonthlySummary = (List<MonthlySummary>) q.execute();
//      for(MonthlySummary c : arrMonthlySummary) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>MonthlySummary: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>MonthlySummary: "+count_antti+" - "+count_pekka);
//      
//      //data: Routine
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Routine.class);
//      List<Routine> arrRoutine = (List<Routine>) q.execute();
//      for(Routine c : arrRoutine) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>Routine: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>Routine: "+count_antti+" - "+count_pekka);
//      
//      //data: Run
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Run.class);
//      List<Run> arrRun = (List<Run>) q.execute();
//      for(Run c : arrRun) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>Run: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>Run: "+count_antti+" - "+count_pekka);
//      
//      //data: RunValue
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(RunValue.class);
//      List<RunValue> arrRunValue = (List<RunValue>) q.execute();
//      for(RunValue c : arrRunValue) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>RunValue: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>RunValue: "+count_antti+" - "+count_pekka);
//      
//      //data: Time
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Time.class);
//      List<Time> arrTime = (List<Time>) q.execute();
//      for(Time c : arrTime) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>Time: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>Time: "+count_antti+" - "+count_pekka);
//      
//      //data: Workout
//      count_antti = 0;
//      count_pekka = 0;
//      q = pm.newQuery(Workout.class);
//      List<Workout> arrWorkout = (List<Workout>) q.execute();
//      for(Workout c : arrWorkout) {
//        try {
//          if(openid_antti != null && c.getUidOld().longValue() == uid_antti) {
//            c.setUid(openid_antti);
//            count_antti++;
//          }
//          else if(openid_pekka != null && c.getUidOld().longValue() == uid_pekka) {
//            c.setUid(openid_pekka);
//            count_pekka++;
//          }
//        } catch (Exception e) {
//          out.write("<br>Workout: "+e.getMessage());
//        }
//      }
//      pm.flush();
//      out.write("<br>Workout: "+count_antti+" - "+count_pekka);

      
      out.flush();
      
    } catch (Exception e) {
      e.printStackTrace();
      try {
        response.getWriter().write(e.toString());
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
  }
  
}
