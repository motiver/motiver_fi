/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.server.Exercise;
import com.delect.motiver.server.MonthlySummary;
import com.delect.motiver.server.MonthlySummaryExercise;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.UserOpenid;
import com.delect.motiver.server.Workout;
import com.delect.motiver.server.service.MyServiceImpl;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Creates monthly summary for each user.
 * @author Antti
 *
 */
public class MonthlyReportServlet extends RemoteServiceServlet {

  private static final long serialVersionUID = 5384098111620397L;
  
  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    response.setContentType("text/html");

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
//    long uid = (request.getParameter("uid") != null)? Long.parseLong(request.getParameter("uid")) : 707249003L;  //for testing
    
    try {      
      //get users
      Query q = pm.newQuery(UserOpenid.class);
//      q.declareParameters("java.lang.Long uidParam");
      List<UserOpenid> users = (List<UserOpenid>) q.execute();
      
      for(UserOpenid user : users) {
        try {
          response.getWriter().write(user.getEmail()+"<br>");

          Calendar cal = Calendar.getInstance();
          cal.setTime(new Date());
          
          //get date from parameters
          try {
            if(request.getParameter("month") != null && request.getParameter("year") != null) {
              cal.set(Calendar.MONTH, Integer.parseInt(request.getParameter("month"))-1);
              cal.set(Calendar.YEAR, Integer.parseInt(request.getParameter("year")));
            }
          } catch (Exception e1) {
          }
          
          //get dates
          cal.set(Calendar.DATE, 1);
          Date d1 = cal.getTime();
          cal.add(Calendar.MONTH, 1);
          cal.add(Calendar.DATE, -1);
          Date d2 = cal.getTime();
          final Date dStart = MyServiceImpl.stripTime(d1, true);
          final Date dEnd = MyServiceImpl.stripTime(d2, false);
          
          //remove all this month's data
          Query qD = pm.newQuery(MonthlySummary.class);
          qD.setFilter("uid == uidParam && date >= dateParam");
          qD.declareParameters("java.lang.Long uidParam, java.util.Date dateParam");
          List<MonthlySummary> data = (List<MonthlySummary>) qD.execute(user.getUid(), dStart);
          pm.deletePersistentAll(data);

          //get this months workouts
          Query qW = pm.newQuery(Workout.class);
          qW.setFilter("uid == uidParam && date >= dateStartParam && date <= dateEndParam");
          qW.declareParameters("java.lang.Long uidParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          List<Workout> workouts = (List<Workout>) qW.execute(user.getUid(), dStart, dEnd);
          
          response.getWriter().write("<table width='100%' border=1 cellpadding=5>");
          response.getWriter().write("<tr><td>nameId"+
              "<td>Sets"+
              "<td>reps string"+
              "<td>repsTrimmed"+
              "<td><b>reps</b>"+
              "<td>weights string"+
              "<td>weightsTrimmed"+
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
            response.getWriter().write("<tr><td colspan=9>"+w.getName()+"</td></tr>");
            for(Exercise e : w.getExercises()) {
              try {
                final long nameId = e.getNameId();
  
                if(nameId > 0) {
                  final int sets = e.getSets();
                  final String reps = e.getReps();
                  final String weights = e.getWeights();
                  //remove everything except numbers
                  final String repsTrimmed = reps.replaceAll("[a-zA-Z]{1,}[.]", "").replaceAll("\\(.*\\)", "").replaceAll("[^0-9x.+,-]", "");
                  final String weightsTrimmed = weights.replaceAll("[a-zA-Z]{1,}[.]", "").replaceAll("\\(.*\\)", "").replaceAll("[^0-9x.+,-]", "");
                
                  //parse strings
                  double[] re = parseReps(sets, repsTrimmed);
                  double[] we = parseWeights(sets, weightsTrimmed);
                  
                  //if no reps found -> jump to next exercise
                  if(re.length == 0) {
                    continue;
                  }
                  
                  //find which group it belongs
                  double max = -1;
                  double work = 0;   //how good/bad the sets were
                  int length = 2;   //how many sets
                  if(re.length == we.length) {
                    for(int i = 0; i < re.length; i++) {
                      //if only one rep
                      if(re[i] == 1) {
                        //if max
                        if(we[i] > max) {
                          max = we[i];
                        }                      
                      }
                      
                      //calculate work
                      double factor = (sets < 5)? 1 - (sets * 0.1) : 0.5;
                      // (weights*weights) * (reps + 10) * factor
                      work += (we[i]*we[1]) * (re[i]+10) * factor;
                      
                      //calculate length (2-4, 5-12 tai 12-...)
                      //2-4
                      if(re[i] >= 2 && re[i] <  5) {
                        length = 0;
                      }
                      //5-12
                      else if(length != 0 && re[i] >= 5 && re[i] <  13) {
                        length = 1;
                      }
                      
                    }
                  }
                  
                  response.getWriter().write("<tr><td>"+nameId+
                      "<td>"+sets+
                      "<td>"+reps+
                      "<td>"+repsTrimmed+
                      "<td><b>"+((re.length == we.length)? Arrays.toString(re) : "ERR")+"</b>"+
                      "<td>"+weights+
                      "<td>"+weightsTrimmed+
                      "<td><b>"+((re.length == we.length)? Arrays.toString(we) : "ERR")+"</b>"+
                      "<td>"+max+
                      "<td>"+length+
                      "<td>"+work+
                      "</td></tr>");

                  //create model
                  MonthlySummaryExercise modelE = new MonthlySummaryExercise(0, max);
                  modelE.setReps(reps);
                  modelE.setSets(sets);
                  modelE.setWeights(weights);
                  modelE.setWorkoutDate(w.getDate());
                  modelE.setLength(length);
                  modelE.setNameId(e.getNameId());
                  modelE.setUid(user.getUid());
                  
                  //set date
                  cal.setTime(w.getDate());
                  
                  //if max found
                  if(max > 0) {
                    //save max
                    if(tableMax.containsKey(nameId)) {
                      if(tableMax.get(nameId).getValue() < max) {
                        tableMax.put(nameId, modelE);
                      }
                    }
                    else {
                      tableMax.put(nameId, modelE);
                    }
                  }
                  //save best exercises
                  else {
                    modelE.setType(1, work);
                    
                    boolean found = false;
                    if(tableBest.containsKey(nameId+"_"+length)) {
                      //if lower value and same length
                      if(tableBest.get(nameId+"_"+length).getValue() < work) {
                        tableBest.put(nameId+"_"+length, modelE);
                        found = true;
                      }
                    }
                    
                    //if not found -> insert new
                    if(!found) {
                      tableBest.put(nameId+"_"+length, modelE);
                    }
                  }
                }
                
              } catch (IOException ex) {
                // TODO Auto-generated catch block
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
            qIsBest.setFilter("uid == uidParam && nameId == nameIdParam && type == 0 && length == lengthParam && value > valueParam");
            qIsBest.declareParameters("java.lang.Long uidParam, java.lang.Long nameIdParam, java.lang.Integer lengthParam, java.lang.Double valueParam");
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
                "<td>"+
                "<td>"+modelE.getValue()+
                "<td>"+
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
            qIsBest.setFilter("uid == uidParam && nameId == nameIdParam && type == 1 && length == lengthParam && value > valueParam");
            qIsBest.declareParameters("java.lang.Long uidParam, java.lang.Long nameIdParam, java.lang.Integer lengthParam, java.lang.Double valueParam");
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
  
  /**
   * Parses reps value and return reps
   * @param reps
   * @param sets
   * @return reps
   */
  private static double[] parseReps(int sets, String reps) {

    //split reps
    String[] reps_arr = reps.split(",");
    
    double[] repsArray = new double[20];
    
    //go through each value
    int c = 0;
    for(int i=0; i < reps_arr.length; i++) {
      try {
        String rep = reps_arr[i];
        
        //remove spaces
        rep = rep.replace(" ", "");
        
        if(rep.length() > 0) {
          //if numeric
          boolean isNumeric = false;
          double nro = 0;
          try {
            nro = Double.parseDouble(rep);
            isNumeric = true;
          } catch (NumberFormatException e) {
          }
          
          //single number
          if(isNumeric) {
            //if single rep found -> use this value for rest of sets
            if(reps_arr.length == 1) {
              for(int k = 0; k < sets ; k++) {
                repsArray[c] = nro;
                c++; 
              }
            }
            else {
              repsArray[c] = nro;
              c++; 
            }
          }
          //if 2+1 (=3)
          else if(rep.matches("([0-9]*)([+]{1}([0-9]*))*")) {
            String[] arr = rep.split("\\+");
            repsArray[c] = Double.parseDouble(arr[0]);
            c++;
          }
          //if 3x10
          else if(rep.matches("([0-9]*)x([0-9]*)")) {
            String[] arr = rep.split("x");
            double s = Double.parseDouble(arr[0]);
            double r = Double.parseDouble(arr[1]);
            //add each sub set
            for(int j = 0; j < s; j++) {
              repsArray[c] = r;
              c++;
            }
          }
          //if 6-10 (means for example 6,7,8,9,10)
          else if(rep.matches("([0-9]*)-([0-9]*)")) {
            String[] arr = rep.split("-");
            //get total reps
            double first_rep = Double.parseDouble(arr[0]);
            double last_rep = Double.parseDouble(arr[1]);
            int sets_count = (sets - (reps_arr.length) + 1);
            //check each set
            //if increasing
            if(last_rep > first_rep) {
              for(double j=first_rep; j <= last_rep; j += ((last_rep - first_rep) / (sets_count - 1))) {
                repsArray[c] = j;
                c++;
              }
            }
            //decreasing
            else {
              for(double j=first_rep; j >= last_rep; j -= ((first_rep - last_rep) / (sets_count - 1))) {
                repsArray[c] = j;
                c++;
              }
            }
          }
        }
      } catch (Exception e) {
        Motiver.showException(e);
      }
    }
    
    //trim array
    double[] repsArrayTrim = new double[c];
    for(int i = 0; i < c; i++) {
      repsArrayTrim[i] = repsArray[i];
    }
    
    return repsArrayTrim;
  }
  
  /**
   * Parses reps value and return reps
   * @param reps
   * @param sets
   * @return reps
   */
  private static double[] parseWeights(int sets, String weights) {

    //if empty -> no weights
    if(weights.length() == 0) {
      weights = "0";
    }
    
    //split reps
    String[] weights_arr = weights.split(",");
    
    double[] weightsArray = new double[20];
    
    //go through each value
    int c = 0;
    for(int i=0; i < weights_arr.length; i++) {
      try {
        String rep = weights_arr[i];
        
        //remove spaces
        rep = rep.replace(" ", "");
        
        if(rep.length() > 0) {
          //if numeric
          boolean isNumeric = false;
          double nro = 0;
          try {
            nro = Double.parseDouble(rep);
            isNumeric = true;
          } catch (NumberFormatException e) {
          }
          
          //single number
          if(isNumeric) {
            //if single rep found -> use this value for rest of sets
            if(weights_arr.length == 1) {
              for(int k = 0; k < sets ; k++) {
                weightsArray[c] = nro;
                c++; 
              }
            }
            else {
              weightsArray[c] = nro;
              c++; 
            }
          }
          //if 2+1 (=3)
          else if(rep.matches("([0-9.]*)([+]{1}([0-9.]*))*")) {
            String[] arr = rep.split("\\+");
            //use only first weight
            weightsArray[c] = Double.parseDouble(arr[0]);
            c++;
          }
          //if 3x10
          else if(rep.matches("([0-9.]*)x([0-9.]*)")) {
            String[] arr = rep.split("x");
            double s = Double.parseDouble(arr[0]);
            double w = Double.parseDouble(arr[1]);
            //if weights are smaller than times (probably other way)
            if(w < s) {
              double wTemp = w;
              w = s;
              s = wTemp;
            }
            //add each sub set
            for(int j = 0; j < s; j++) {
              weightsArray[c] = w;
              c++;
            }
          }
          //if 6-10 (means for example 6,7,8,9,10)
          else if(rep.matches("([0-9.]*)-([0-9.]*)")) {
            String[] arr = rep.split("-");
            //get total reps
            double first_rep = Double.parseDouble(arr[0]);
            double last_rep = Double.parseDouble(arr[1]);
            int sets_count = (sets - (weights_arr.length) + 1);
            //check each set
            //if increasing
            if(last_rep > first_rep) {
              for(double j=first_rep; j <= last_rep; j += ((last_rep - first_rep) / (sets_count - 1))) {
                weightsArray[c] = j;
                c++;
              }
            }
            //decreasing
            else {
              for(double j=first_rep; j >= last_rep; j -= ((first_rep - last_rep) / (sets_count - 1))) {
                weightsArray[c] = j;
                c++;
              }
            }
          }
        }
      } catch (Exception e) {
        Motiver.showException(e);
      }
    }
    
    //trim array
    double[] repsArrayTrim = new double[c];
    for(int i = 0; i < c; i++) {
      repsArrayTrim[i] = weightsArray[i];
    }
    
    return repsArrayTrim;
  }
}
