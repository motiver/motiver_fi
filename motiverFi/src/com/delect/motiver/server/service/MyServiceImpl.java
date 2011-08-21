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
// $codepro.audit.disable indentCodeWithinBlocks, questionableName
/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.delect.motiver.client.service.MyService;
import com.delect.motiver.server.Base64;
import com.delect.motiver.server.Cardio;
import com.delect.motiver.server.CardioValue;
import com.delect.motiver.server.Circle;
import com.delect.motiver.server.Comment;
import com.delect.motiver.server.CommentsRead;
import com.delect.motiver.server.Exercise;
import com.delect.motiver.server.ExerciseName;
import com.delect.motiver.server.ExerciseNameCount;
import com.delect.motiver.server.ExerciseSearchIndex;
import com.delect.motiver.server.Food;
import com.delect.motiver.server.FoodInMeal;
import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.server.FoodInTime;
import com.delect.motiver.server.FoodName;
import com.delect.motiver.server.FoodSearchIndex;
import com.delect.motiver.server.GuideValue;
import com.delect.motiver.server.Meal;
import com.delect.motiver.server.MealInTime;
import com.delect.motiver.server.Measurement;
import com.delect.motiver.server.MeasurementValue;
import com.delect.motiver.server.MicroNutrient;
import com.delect.motiver.server.MonthlySummary;
import com.delect.motiver.server.MonthlySummaryExercise;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.Routine;
import com.delect.motiver.server.Run;
import com.delect.motiver.server.RunValue;
import com.delect.motiver.server.Time;
import com.delect.motiver.server.UserOpenid;
import com.delect.motiver.server.Workout;
import com.delect.motiver.server.datastore.StoreNutrition;
import com.delect.motiver.server.datastore.StoreTraining;
import com.delect.motiver.server.datastore.StoreUser;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.CommentModel;
import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.ExerciseNameModel;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.GuideValueModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.MeasurementValueModel;
import com.delect.motiver.shared.MicroNutrientModel;
import com.delect.motiver.shared.MonthlySummaryExerciseModel;
import com.delect.motiver.shared.MonthlySummaryModel;
import com.delect.motiver.shared.NutritionDayModel;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;
import com.delect.motiver.shared.TicketModel;
import com.delect.motiver.shared.TimeModel;
import com.delect.motiver.shared.UserModel;
import com.delect.motiver.shared.WorkoutModel;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MyServiceImpl extends RemoteServiceServlet implements MyService {

  /**
   * 
   */
  private static final long serialVersionUID = -7106279162988246661L;

  /**
   * How long we wait between retries (milliseconds)
   */
  public static final int DELAY_BETWEEN_RETRIES = 75;
  

  private static final class MyListItem implements Comparable<MyListItem> {
    
    public int count = 0;
    public long id = 0;
    public MealModel meal = null;
    public String name = "";
    
    private MyListItem(long id) {
      this.id = id;
      count = 1;
    }

    @Override
    public int compareTo(MyListItem item) {

    logger.log(Level.FINE, "compareTo()");
      return item.count - count;
    }
  }
  
  private static final Logger logger = Logger.getLogger(MyServiceImpl.class.getName()); 
  
  static final int MAX_COUNT = 4000;
    
  /**
   * Checks if current user has permission to coach given uid
   * @param uid
   * @return accesstoken for user
   * @throws ConnectionException
   */
//  @SuppressWarnings("unchecked")
//  public static String getCoachAccess(String uid) {
//
//    log.log(Level.FINE, "getCoachAccess()");
//
//    //get uid
//    final String UID = getUid();
//    if(UID == null) {
//      return null;
//    }
//
//    String str = "";
//    PersistenceManager pm =  PMF.get().getPersistenceManager();
//    
//    try {
//          
//        //check if found in our database AND has set as coach
//        Query q = pm.newQuery(UserOpenid.class, "openId == openIdParam && shareCoach == shareCoachParam");
//        q.declareParameters("java.lang.String openIdParam, java.lang.Long shareCoachParam");
//        List<UserOpenid> users = (List<UserOpenid>) q.execute(uid, UID);
//
//        //data found
//        if(users.size() > 0) {
//          //reset token
//          final UserOpenid user = users.get(0);
//          str = user.getFbAuthToken();
//        }
//
//    } catch (Exception e) {
//      log.log(Level.SEVERE, "getCoachAccess", e);
//      
//      return null;
//    }
//    finally {
//      if (!pm.isClosed()) {
//        pm.close();
//      } 
//    }
//    
//    return str;
//  }
  
  /**
   * Checks if current user is friend with given uid
   * @param uid
   * @return
   * @throws ConnectionException 
   */
  private static boolean areFriends(String uid) {

    logger.log(Level.FINE, "areFriends()");

    boolean areFriends = false;
    
//    try {
//      //if coach mode -> get token from trainee
//      if(token.contains("____")) {
//        token = getTraineesToken(token);
//      }
//      
//      //get friends from facebook
//      URL url = new URL("https://graph.facebook.com/me/friends?access_token=" + URLEncoder.encode(token.replaceAll("____.*", "")));
//      BufferedReader reader = null;
//      String line = null;
//      try {
//        reader = new BufferedReader(new InputStreamReader(url.openStream()));
//        line = reader.readLine();
//      } catch (Exception e1) {
//        log.log(Level.SEVERE, "", e1);
//        throw new ConnectionException("areFriends", "Could not connect to Facebook.com");
//      }
//      finally {
//        reader.close();
//      } 
//      if(line != null) {
//        JSONObject json = new JSONObject(line);
//        JSONArray groups = json.getJSONArray("data");
//        
//        for(int i=0; i < groups.length(); i++) {
//          JSONObject obj = groups.getJSONObject(i);
//          final long id = obj.getLong("id");
//          
//          //check if IDs match
//          if(id == uid) {
//            areFriends = true;
//            break;
//          }
//        }
//      }
//    } catch (Exception e) {
//      log.log(Level.SEVERE, "", e);
//    }
    
    return areFriends;
  }
  
  /**
   * Calculates energy from times (searches meals and foods)
   * @param pm 
   * @param times
   * @return
   */
  private static NutritionDayModel calculateEnergyFromTimes(PersistenceManager pm, List<Time> times, String UID) {

    logger.log(Level.FINE, "calculateEnergyFromTimes()");
    double energy = 0;
    double protein = 0; 
    double carbs = 0;
    double fet = 0;

    try {
      List<TimeModel> list = new ArrayList<TimeModel>();
      for(Time time : times) {
        list.add(StoreNutrition.getTimeModel(pm, time.getId(), UID));
      }
      
      //each time
      for(TimeModel tClient : list) {
        
        //each meal
        for(MealModel m : tClient.getMeals()) {
          
            if(m.getFoods() != null) {
              for(FoodModel food : m.getFoods()) {

                final double amount = food.getAmount();
                final FoodNameModel name = food.getName();
                if(name != null) {
                  energy += (name.getEnergy() / 100) * amount;
                  protein += (name.getProtein() / 100) * amount;
                  carbs += (name.getCarb() / 100) * amount;
                  fet += (name.getFet() / 100) * amount;
                }
              }
            }
          
        }

        if(tClient.getFoods() != null) {
          for(FoodModel food : tClient.getFoods()) {

            final double amount = food.getAmount();
            final FoodNameModel name = food.getName();
            if(name != null) {
              energy += (name.getEnergy() / 100) * amount;
              protein += (name.getProtein() / 100) * amount;
              carbs += (name.getCarb() / 100) * amount;
              fet += (name.getFet() / 100) * amount;
            }
          }
        }
      }
      
    } catch (Exception e1) {
      logger.log(Level.SEVERE, "calculateEnergyFromTimes", e1);
    }

    return new NutritionDayModel(energy, protein, carbs, fet);
  }
  
  /**
   * Duplicates single exercise
   * @param exercise
   * @return duplicated exercise
   */
  public static Exercise duplicateExercise(Exercise e) {

    logger.log(Level.FINE, "duplicateExercise()");

    Exercise eNew = new Exercise();
    
    try {
      eNew.setNameId(e.getNameId());
      eNew.setInfo(e.getInfo());
      eNew.setOrder(e.getOrder());
      eNew.setReps(e.getReps());
      eNew.setSets(e.getSets());
      eNew.setTempo(e.getTempo());
      eNew.setWeights(e.getWeights());
      
    } catch (Exception e1) {
      logger.log(Level.SEVERE, "duplicateExercise", e1);
    }
    
    return eNew;
  }
  
  /**
   * Duplicates single food
   * @param food
   * @return duplicated food
   */
  private static FoodInMeal duplicateFood(FoodInMeal f) throws ConnectionException {

    logger.log(Level.FINE, "duplicateFood()");

    try {
      FoodInMeal fNew = new FoodInMeal();
      fNew.setNameId(f.getNameId());
      fNew.setAmount(f.getAmount());
      
      return fNew;
      
    } catch (Exception e1) {
      logger.log(Level.SEVERE, "duplicateFood", e1);
      throw new ConnectionException("duplicateFood", e1.getMessage());
    }
  }

  /**
   * Duplicates meal model
   * @param meal
   * @return duplicated meal
   */
  private static Meal duplicateMeal(Meal m) throws ConnectionException {

    logger.log(Level.FINE, "duplicateMeal()");

    try {
      Meal mNew = new Meal(m.getName());
      return mNew;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "duplicateMeal", e);
      throw new ConnectionException("duplicateMeal", e.getMessage());
    }
  }

  /**
   * Duplicates meal model
   * @param meal
   * @return duplicated meal
   */
  private static MealInTime duplicateMeal(MealInTime m) throws ConnectionException {

    logger.log(Level.FINE, "duplicateMeal()");

    try {
      MealInTime mNew = new MealInTime(m.getName());
      return mNew;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "duplicateMeal", e);
      throw new ConnectionException("duplicateMeal", e.getMessage());
    }
  }

  /**
   * Duplicates time model
   * @param time
   * @return duplicated time
   */
  private static Time duplicateTime(Time t) throws ConnectionException {

    logger.log(Level.FINE, "duplicateTime()");
    
    try {
      Time tNew = new Time(t.getDate(), t.getTime());
      return tNew;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "duplicateTime", e);
      throw new ConnectionException("duplicateTime", e.getMessage());
    }
  }

  /**
   * Duplicates workout model (including exercises)
   * @param workout
   * @return duplicated workout
   * @throws ConnectionException 
   */
  private static Workout duplicateWorkout(Workout w) throws ConnectionException {

    logger.log(Level.FINE, "duplicateWorkout()");

    try {
      Workout wNew = new Workout(w.getName());
      wNew.setDate(w.getDate());
      wNew.setDayInRoutine(w.getDayInRoutine());
      wNew.setRoutineId(w.getRoutineId());
      //exercises
      List<Exercise> eNew = new ArrayList<Exercise>();
      for(Exercise e : w.getExercises())
        eNew.add(duplicateExercise(e));
      wNew.setExercises(eNew);
      
      return wNew;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "duplicateWorkout", e);
      throw new ConnectionException("duplicateWorkout", e.getMessage());
    }
  }

  /**
   * Get food name model
   * @param pm
   * @param nameId
   * @return
   */
  private static FoodNameModel getFoodName(PersistenceManager pm, Long nameId) throws ConnectionException {

    logger.log(Level.FINE, "getFoodName()");
    
    try {
      FoodName name = pm.getObjectById(FoodName.class, nameId);
      if(name != null) {
        return FoodName.getClientModel(name);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getFoodName", e);
      throw new ConnectionException("getFoodName", e.getMessage());
    }
    
    return null;
  }

//  /**
//   * Gets client side model of single meals (with foods)
//   * @param pm
//   * @param t
//   * @return
//   */
//  @SuppressWarnings("unchecked")
//  private static MealModel getSingleMeal(PersistenceManager pm, Meal meal, boolean fetchFoods) {
//
//    logger.log(Level.FINE, "getSingleMeal()");
//
//    logger.log(Level.FINE, "getSingleMeal()");
//    MealModel m = Meal.getClientModel(meal);
//
//    if(fetchFoods) {
//      //fetch names first
//      List<Long> arrNameId = new ArrayList<Long>();
//      for(FoodInMeal f : meal.getFoods()) {
//        if(f.getNameId() != 0 && !arrNameId.contains(f.getNameId())) {
//            arrNameId.add(f.getNameId());
//        }
//      }
//
//      List<FoodName> foodNames = null;
//      try {
//        Query query = pm.newQuery(FoodName.class);
//        query.setFilter("idParam.contains(id)");
//        query.declareParameters("java.lang.Long idParam");
//        foodNames = (List<FoodName>) query.execute(arrNameId);
//      } catch (Exception e1) {
//        logger.log(Level.SEVERE, "getSingleMeal", e1);
//      }
//          
//      //get foods
//      final List<FoodModel> listFoods = new ArrayList<FoodModel>();
//      for(FoodInMeal f : meal.getFoods()) {
//        try {
//          FoodModel fClient = FoodInMeal.getClientModel(f);
//
//          //get name from array
//          if(f.getNameId() != 0 && foodNames != null) {
//            for(FoodName fn : foodNames) {
//              if(fn.getId().longValue() == f.getNameId().longValue()) {
//                fClient.setName(FoodName.getClientModel(fn));
//                break;
//              }
//            }
//          }
//          
//          listFoods.add(fClient);
//          
//        } catch (Exception e) {
//          logger.log(Level.SEVERE, "getSingleMeal", e);
//        }
//      }
//      m.setFoods(listFoods);
//    }
//    
//    return m;
//  }

  /**
   * Gets client side model of single meal (int time) (with foods)
   * @param pm
   * @param t
   * @return
   */
  @SuppressWarnings("unchecked")
  private static MealModel getSingleMeal(PersistenceManager pm, MealInTime meal, boolean fetchFoods) {

    logger.log(Level.FINE, "getSingleMeal()");

    logger.log(Level.FINE, "getSingleMeal()");
    MealModel m = MealInTime.getClientModel(meal);

    if(fetchFoods) {
      //fetch names first
      List<Long> arrNameId = new ArrayList<Long>();
      for(FoodInMealTime f : meal.getFoods()) {
        if(f.getNameId() != 0 && !arrNameId.contains(f.getNameId())) {
            arrNameId.add(f.getNameId());
        }
      }
      
      List<FoodName> foodNames = null;
      try {
        Query query = pm.newQuery(FoodName.class);
        query.setFilter("idParam.contains(id)");
        query.declareParameters("java.lang.Long idParam");
        foodNames = (List<FoodName>) query.execute(arrNameId);
      } catch (Exception e1) {
        logger.log(Level.SEVERE, "getSingleMeal", e1);
      }
          
      //get foods
      final List<FoodModel> listFoods = new ArrayList<FoodModel>();
      for(FoodInMealTime f : meal.getFoods()) {
        try {
          FoodModel fClient = FoodInMealTime.getClientModel(f);

          //get name from array
          if(f.getNameId() != 0 && foodNames != null) {
            for(FoodName fn : foodNames) {
              if(fn.getId().longValue() == f.getNameId().longValue()) {
                fClient.setName(FoodName.getClientModel(fn));
                break;
              }
            }
          }
          
          listFoods.add(fClient);
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "getSingleMeal", e);
        }
      }
      m.setFoods(listFoods);
    }
    
    return m;
  }

  /**
   * Gets client side model of single time (with meals and foods)
   * @param pm
   * @param t
   * @return
   */
//  @SuppressWarnings("unchecked")
//  private static List<TimeModel> getSingleTimes(PersistenceManager pm, List<Time> times) {
//
//    log.log(Level.FINE, "getSingleTimes()");
//
//    log.log(Level.FINE, "getSingleTimes()");
//
//    List<TimeModel> timesClient = new ArrayList<TimeModel>();
//    
//    if(times == null || times.size() == 0) {
//      return timesClient;
//    }
//    
//    //fetch names first
//    List<Long> arrNameId = new ArrayList<Long>();
//    for(Time t : times) {
//      final List<MealInTime> listM = t.getMeals();
//      for(MealInTime w : listM) {
//        final List<FoodInMealTime> listF = w.getFoods();
//        for(FoodInMealTime f : listF) {
//          if(f.getNameId() != 0 && !arrNameId.contains(f.getNameId())) {
//              arrNameId.add(f.getNameId());
//          }
//        }
//      }
//      for(FoodInTime f : t.getFoods()) {
//        if(f.getNameId() != 0 && !arrNameId.contains(f.getNameId())) {
//            arrNameId.add(f.getNameId());
//        }
//      }
//    }
//    
//    List<FoodName> foodNames = null;
//    try {
//      Query query = pm.newQuery(FoodName.class);
//      query.setFilter("idParam.contains(id)");
//      query.declareParameters("java.lang.Long idParam");
//      foodNames = (List<FoodName>) query.execute(arrNameId);
//    } catch (Exception e1) {
//    }
//
//    for(Time t : times) {
//      TimeModel m = Time.getClientModel(t);
//      
//      //get meals
//      List<MealInTime> meals = t.getMeals();
//      List<MealModel> listMeals = new ArrayList<MealModel>();
//      if(meals != null) {
//        
//        for(MealInTime w : t.getMeals()) {
//          MealModel meal = MealInTime.getClientModel(w);
//          
//          //get foods
//          List<FoodModel> listFoods = new ArrayList<FoodModel>();
//          for(FoodInMealTime f : w.getFoods()) {
//            FoodModel fClient = FoodInMealTime.getClientModel(f);
//            fClient.setMealId(meal.getId());
//            fClient.setTimeId(meal.getTimeId());
//            fClient.setUid(meal.getUid());
//
//            //get name from array
//            if(f.getNameId() != 0 && foodNames != null) {
//              for(FoodName fn : foodNames) {
//                if(fn.getId().longValue() == f.getNameId().longValue()) {
//                  fClient.setName(FoodName.getClientModel(fn));
//                  break;
//                }
//              }
//            }
//            
//            listFoods.add(fClient);
//          }
//          meal.setFoods(listFoods);
//          
//          //set time
//          meal.setTimeId(t.getId());
//                  
//          listMeals.add(meal);
//        }
//      }
//      m.setMeals(listMeals);
//      
//      //get foods
//      final List<FoodModel> listFoods = new ArrayList<FoodModel>();
//      for(FoodInTime f : t.getFoods()) {
//        try {
//          FoodModel fClient = FoodInTime.getClientModel(f);
//
//          //get name from array
//          if(f.getNameId() != 0 && foodNames != null) {
//            for(FoodName fn : foodNames) {
//              if(fn.getId().longValue() == f.getNameId().longValue()) {
//                fClient.setName(FoodName.getClientModel(fn));
//                break;
//              }
//            }
//          }
//          
//          listFoods.add(fClient);
//          
//        } catch (Exception e) {
//          log.log(Level.SEVERE, "getSingleTimes", e);
//        }
//      }
//      m.setFoods(listFoods);
//      
//      timesClient.add(m);
//    }
//    
//    return timesClient;
//  }

  /**
   * Gets client side model of single workout
   * @param pm
   * @param fetchExercises : if exercises are also fetched
   * @return
   */
//  @SuppressWarnings("unchecked")
//  private static WorkoutModel getSingleWorkout(PersistenceManager pm, Workout w, boolean fetchExercises) {
//
//    log.log(Level.FINE, "getSingleWorkout()");
//    
//    WorkoutModel m = Workout.getClientModel(w);
//
//    //fetch exercises
//    if(fetchExercises) {
//
//      try {
//        
//      List<ExerciseModel> listEClient = new ArrayList<ExerciseModel>();
//      List<Exercise> listE = w.getExercises();
//      if(listE != null) {
//        //fetch names first
//        List<Long> arrNameId = new ArrayList<Long>();
//        for(Exercise e : listE) {
//          if(e.getNameId() != 0 && !arrNameId.contains(e.getNameId())) {
//            arrNameId.add(e.getNameId());
//          }
//        }
//        
//        List<ExerciseName> exercises = null;
//          Query query = pm.newQuery(ExerciseName.class);
//          query.setFilter("idParam.contains(id)");
//          query.declareParameters("java.lang.Long idParam");
//          exercises = (List<ExerciseName>) query.execute(arrNameId);
//
//        //go through each exercise
//        for(Exercise exercise : listE) {
//          ExerciseModel eNew = Exercise.getClientModel(exercise);
//          
//          //set correct workout id
//          eNew.setWorkoutId(w.getId());
//          eNew.setUid(w.getUid());
//          
//          //get name from array
//          if(exercise.getNameId() != 0 && exercises != null) {
//            for(ExerciseName en : exercises) {
//              if(en.getId().longValue() == exercise.getNameId().longValue()) {
//                eNew.setName(ExerciseName.getClientModel(en));
//                break;
//              }
//            }
//          }
//          listEClient.add(eNew);
//        }
//        
//      }
//      m.setExercises(listEClient);
//      
//      } catch (Exception e1) {
//        log.log(Level.SEVERE, "getSingleWorkout", e1);
//      }
//    }
//    
//    return m;
//  }

  /**
   * Get trainees token -> can be used in coachmode -> when we want trainee's token and client has given only our own token
   * @return
   */
  @SuppressWarnings("unchecked")
  private static String getTraineesToken(String token) {

    logger.log(Level.FINE, "getTraineesToken()");

    String ret = token;

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      //get our uid
      Query q = pm.newQuery(UserOpenid.class, "fbAuthToken == fbAuthTokenParam");
      q.declareParameters("java.lang.String fbAuthTokenParam");
      List<UserOpenid> users = (List<UserOpenid>) q.execute(token.replaceAll("____.*", ""));
      String tempUid = null;
      if(users.size() > 0) {
        tempUid = users.get(0).getUid();
      }
      
      //check that coach has right to
      long traineeUID = Long.parseLong(token.replaceAll(".*____", ""));
      
      //check if found in our database AND has set as coach
      q = pm.newQuery(UserOpenid.class, "openId == openIdParam && shareCoach == shareCoachParam");
      q.declareParameters("java.lang.String openIdParam, java.lang.Long shareCoachParam");
      users = (List<UserOpenid>) q.execute(traineeUID, tempUid);
  
      //data found
      if(users.size() > 0) {
        ret = users.get(0).getFbAuthToken();
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getTraineesToken", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ret;
    
  }

  /**
   * Gets openId string
   * @return null if no user found
   */
  private String getUid() {

    String coachModeUid = null;
    
    try {
      String s = this.perThreadRequest.get().getHeader("coach_mode_uid");
      if(s != null && s.length() > 1) {
        coachModeUid = s;
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error checkin coach mode", e);
      coachModeUid = null;
    }
    
    return _getUid(coachModeUid);
  }
  @SuppressWarnings("unchecked")
  static String _getUid(String coachModeUid) {

    logger.log(Level.FINE, "getUid()");

    String openId = null;

    UserService userService = UserServiceFactory.getUserService();
    User userCurrent = userService.getCurrentUser();
    
    if(userCurrent != null) {
      openId = userCurrent.getUserId();
    }
  
    //if coach mode -> return trainee's uid
    if(coachModeUid != null) {
      logger.log(Level.FINE, "Checking if user "+openId+" is coach to "+coachModeUid);

      PersistenceManager pm =  PMF.get().getPersistenceManager();
      
      try {
        Query q = pm.newQuery(Circle.class);
        q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
        q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
        q.setRange(0,1);
        List<Circle> list = (List<Circle>)q.execute(coachModeUid, openId, Permission.COACH);
        
        if(list.size() > 0) {
          logger.log(Level.FINE, "Is coach!");
          openId = list.get(0).getUid();
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "Error checkin coach", e);
      }
      finally {
        if(!pm.isClosed()) {
          pm.close();
        }
      }
    }
    
    return openId;
    
//    String uid = 0L;
//    PersistenceManager pm =  PMF.get().getPersistenceManager();
//    
//    try {
//      String tokenOur = token.replaceAll("____.*", "");
//      
//      //get our uid
//      Query q = pm.newQuery(UserOpenid.class, "fbAuthToken == fbAuthTokenParam");
//      q.declareParameters("java.lang.String fbAuthTokenParam");
//      q.setRange(0, 1);
//      List<UserOpenid> users = (List<UserOpenid>) q.execute(tokenOur);
//      if(users.size() > 0) {
//        uid = users.get(0).getUid();
//      }
//      
//      //if coach mode -> token is format: mytoken____traineeUID
//      if(token.contains("____")) {
//        //check that coach has right to
//        long traineeUID = Long.parseLong(token.replaceAll(".*____", ""));
//        
//        //check if found in our database AND has set as coach
//        q = pm.newQuery(UserOpenid.class, "openId == openIdParam && shareCoach == shareCoachParam");
//        q.declareParameters("java.lang.String openIdParam, java.lang.Long shareCoachParam");
//        users = (List<UserOpenid>) q.execute(traineeUID, uid);
//
//        //data found
//        if(users.size() > 0) {
//          uid = traineeUID;
//        }
//      }
//      
//    } catch (Exception e) {
//      log.log(Level.SEVERE, "", e);
//    }
//    finally {
//      if (!pm.isClosed()) {
//        pm.close();
//      }
//    }
//    
//    return uid;
  }

  /**
   * Gets user's facebook id (uid) based on auth token
   * @param object array: uid (long), locale (string)
   */
  private Object[] getUidAndLocale() {

    logger.log(Level.FINE, "getUidAndLocale()");

    String uid = getUid();
    String locale = "";
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      //get our uid
      UserModel u = StoreUser.getUserModel(pm, uid);
      if(u != null) {
        locale = u.getLocale();
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error fetching locale", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return new Object[] {uid, locale};
  }

  /**
   * Checks if user has permission to given target
   * @param target : what item is shared, 0=training, 1=nutrition, 4=nutrition foods, 2=cardio, 3=measurement
   *        edit permissions: 10=training, 11=nutrition, 14=nutrition foods, 12=cardio, 13=measurement
   * @param ourUid : our user id
   * @param uid : target's user id (if same that ours -> returns always true)
   * @return has permission
   */
  @SuppressWarnings("unchecked")
  private static boolean hasPermission(int target, String ourUid, String uid) {

    logger.log(Level.FINE, "hasPermission()");
    
    if(ourUid.equals(uid)) {
      return true;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    boolean hasPermission = false;
    try {

      Query q = pm.newQuery(Circle.class);
      q.setFilter("openId == openIdParam && (friendId == friendIdParam || friendId == '-1') && target == targetParam");
      q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
      q.setRange(0,1);
      List<Circle> list = (List<Circle>)q.execute(uid, ourUid, target);
      
      hasPermission = (list.size() > 0);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "hasPermission", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return hasPermission;
  }

  /**
   * Checks if user has permission to given target
   * @param target : PERMISSION::READ_XXX or PERMISSION::WRITE_XXX
   * @param ourUid : our user id
   * @param uid : target's user id (if same that ours -> returns always true)
   * @return has permission
   */
  @SuppressWarnings("unchecked")
  public static boolean hasPermission(PersistenceManager pm, int target, String ourUid, String uid) {

    logger.log(Level.FINE, "hasPermission()");
    
    if(ourUid.equals(uid)) {
      return true;
    }
    
    boolean hasPermission = false;
    try {
      Query q = pm.newQuery(Circle.class);

      //if read
      if(target == Permission.READ_TRAINING
          || target == Permission.READ_NUTRITION
          || target == Permission.READ_NUTRITION_FOODS
          || target == Permission.READ_CARDIO
          || target == Permission.READ_MEASUREMENTS) {
        q.setFilter("openId == openIdParam && (friendId == friendIdParam || friendId == '-1') && target == targetParam");
        q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
        q.setRange(0,1);
        List<Circle> list = (List<Circle>)q.execute(uid, ourUid, target);
        hasPermission = (list.size() > 0);
      }
      //write permission -> check if coach
      else {
        q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
        q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
        q.setRange(0,1);
        List<Circle> list = (List<Circle>)q.execute(uid, ourUid, Permission.COACH);
        hasPermission = (list.size() > 0);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error checking permission", e);
    }
    
    return hasPermission;
  }
  
  /**
   * Strip time from date
   * @param date
   * @param start or end date (time: 00:00:01 or 23:59:59)
   * @return date
   */
  public static Date stripTime(Date date, boolean isStart) {

    logger.log(Level.FINE, "stripTime()");

        GregorianCalendar gc = new GregorianCalendar(); 
        gc.setTime(date); 
        int year = gc.get(Calendar.YEAR); 
        int month = gc.get(Calendar.MONTH); 
        int day = gc.get(Calendar.DATE); 
        int h = 0;
        int m = 0;
        int s = 0;
        if(!isStart) {
          h = 23;
          m = 59;
          s = 59;
        }
        GregorianCalendar output = new GregorianCalendar(year, month, day, h, m, s); 
        return output.getTime(); 
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public UserModel getUser() throws ConnectionException {

    logger.log(Level.FINE, "getUser()");
    
    UserModel user = new UserModel();

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      UserService userService = UserServiceFactory.getUserService();
      User userCurrent = userService.getCurrentUser();
      
      //user found
      if(userCurrent != null) {
        
        user = StoreUser.addUser(pm, userCurrent);
        user.setLogoutUrl(userService.createLogoutURL("http://www.motiver.fi"));
        
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading user", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return user;
  }
  
  
  /**
   * Adds cardio to db
   * @param cardio : model to be added
   * @return added cardio (null if add not successful)
   */
  @Override
  public CardioModel addCardio(CardioModel cardio) throws ConnectionException {

    logger.log(Level.FINE, "addCardio()");
    
    CardioModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    Cardio modelServer = null;
    try {

      //convert to server side model
      modelServer = Cardio.getServerModel(cardio);
      
      //save user
      modelServer.setUid(UID);
      
      //save to db
      modelServer = pm.makePersistent(modelServer);

      //convert to client side model (which we return)
      m = Cardio.getClientModel(modelServer);
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addCardio", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addCardio", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds cardio's value to db
   * @param cardio : cardio where we add this value
   * @param value : value to be added
   * @return added cardio (null if add not successful)
   */
  @Override
  public CardioValueModel addCardioValue(CardioModel cardio, CardioValueModel value) throws ConnectionException {

    logger.log(Level.FINE, "addCardioValue()");
    
    CardioValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Cardio modelServer = null;
    try {
      
      //get cardio (if old)
      if(cardio.getId() > 0) {
        modelServer = pm.getObjectById(Cardio.class, cardio.getId());
      }
      //new cardio -> add
      else {
        modelServer = Cardio.getServerModel(this.addCardio(cardio));
        modelServer.setUid(UID);
      }

      if(modelServer != null) {
        if(modelServer.getUid().equals(UID)) {
          
          //convert to server side model
          CardioValue model = CardioValue.getServerModel(value);
          //save user
          model.setUid(UID);
          
          //add exercise
          modelServer.getValues().add(model);

          //save to db
          pm.makePersistent(modelServer);

          //get added value
          CardioValue vNew = modelServer.getValues().get(modelServer.getValues().size() - 1);
          m = CardioValue.getClientModel(vNew);
          m.setName( Cardio.getClientModel(vNew.getCardio()) );
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addCardioValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addCardioValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds comment to db
   * @param comment : model to be added
   * @return added comment (null if add not successful
   */
  @Override
  public CommentModel addComment(CommentModel comment) throws ConnectionException  {

    logger.log(Level.FINE, "addComment()");
    
    CommentModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
      
    try {
      Comment modelServer = null;
        
      //convert to server side model
      modelServer = Comment.getServerModel(comment);

      //save user
      modelServer.setUid(UID);

      //save comment to db
      pm.makePersistent(modelServer);
      
      //convert to client side model (which we return)
      m = Comment.getClientModel(modelServer);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addComment", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addComment", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds workout to db
   * @param workout : model to be added
   * @return added workout (null if add not successful)
   */
  @Override
  public ExerciseModel addExercise(ExerciseModel exercise) throws ConnectionException {

    logger.log(Level.FINE, "addExercise()");
    
    //get uid
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      exercise = StoreTraining.addExerciseModel(pm, exercise, UID, LOCALE);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise", e);
      if (!pm.isClosed()) {
        pm.close();
      }
      throw new ConnectionException("addExercise", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return exercise;
  }

  /**
   * Creates / updates exercisename (updates if already found)
   * @param workout : model to be added
   * @return added exercise (null if add not successful)
   */
  @Override
  public ExerciseNameModel addExercisename(ExerciseNameModel name) throws ConnectionException {

    ExerciseNameModel m = null;
    
    //get uid
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return m;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      m = StoreTraining.addExerciseNameModel(pm, name, UID, LOCALE);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding exercise name", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addExercisename", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds food to (meal)
   * @param food : model to be added
   * @param meal : meal where food is added (null if not added in any meal)
   * @return added food (null if add not successful)
   */
  @Override @SuppressWarnings("unchecked")
  public FoodModel addFood(FoodModel food) throws ConnectionException {

    logger.log(Level.FINE, "addFood()");

    FoodModel m = null;
    
    //get uid
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {

      m = StoreNutrition.addFoodModel(pm, food, UID, LOCALE);
      
      long nameId = 0;
      FoodNameModel name = food.getName();
      
      //if no food name -> search for it
      if(food.getName() != null) {
        nameId = food.getName().getId();
      }

      if(nameId == 0) {
        try {
          Query q = pm.newQuery(FoodName.class);
          q.setFilter("name == nameParam && energy == energyParam");
          q.declareParameters("java.lang.String nameParam, java.lang.Double energyParam");
          List<FoodName> arr = (List<FoodName>) q.execute(food.getName().getName(), food.getName().getEnergy());
                
          //if found
          boolean found = false;
          if(arr != null && arr.size() > 0) {
              found = true;
          }
          
          if(found) {
            nameId = arr.get(0).getId();
          }
          //create new
          else {
            FoodName mServer = FoodName.getServerModel(food.getName());
            mServer.setUid(UID);
            FoodName added = pm.makePersistent(mServer);
            nameId = added.getId();
            name = FoodName.getClientModel(added);
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, "addFood", e);
        }
      }
      
      //if food is in meal (which is in time)
      if(food.getTimeId() != 0 && food.getMealId() != 0) {
        //get time
        Time time = pm.getObjectById(Time.class, food.getTimeId());
        if(time != null && hasPermission(1, UID, time.getUid())) {
          //get meal
          for(MealInTime meal : time.getMeals()) {
            if(meal.getId() == food.getMealId()) {
              //if no foods
              if(meal.getFoods() == null) {
                List<FoodInMealTime> list = new ArrayList<FoodInMealTime>();
                meal.setFoods(list);
              }
              
              FoodInMealTime f = FoodInMealTime.getServerModel(food);
              f.setNameId(nameId);
              meal.getFoods().add(f);
              pm.makePersistent(meal);
              
              m = FoodInMealTime.getClientModel(f);
              m.setMealId(food.getMealId());
              m.setTimeId(food.getTimeId());
              
              break;
            }
          }
        }
      }
      //if added to some time -> save key
      else if(food.getTimeId() != 0) {
        
        //get time
        Time timeServer = pm.getObjectById(Time.class, food.getTimeId());
        if(timeServer != null) {
          //if no foods
          if(timeServer.getFoods() == null) {
            List<FoodInTime> list = new ArrayList<FoodInTime>();
            timeServer.setFoods(list);
          }
          
          FoodInTime f = FoodInTime.getServerModel(food);
          f.setNameId(nameId);
          timeServer.getFoods().add(f);
          pm.makePersistent(timeServer);
          
          m = FoodInTime.getClientModel(f);
          m.setTimeId(food.getTimeId());
          
        }
        else {
          throw new Exception();
        }
        
      }
      //if added to some meal -> save key
      else if(food.getMealId() != 0) {
        
        //get meal
        Meal mealServer = pm.getObjectById(Meal.class, food.getMealId());
        if(mealServer != null) {
          //if no foods
          if(mealServer.getFoods() == null) {
            List<FoodInMeal> list = new ArrayList<FoodInMeal>();
            mealServer.setFoods(list);
          }
          
          FoodInMeal f = FoodInMeal.getServerModel(food);
          f.setNameId(nameId);
          mealServer.getFoods().add(f);
          pm.makePersistent(mealServer);
          
          //return client side model
          m = FoodInMeal.getClientModel(f);
          m.setMealId(food.getMealId());
          m.setTimeId(food.getTimeId());
          
        }
        else {
          throw new Exception();
        }
        
      }
      m.setName(name);

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addFood", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addFood", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Creates / updates foodname (updates if already found)
   * @param name : model to be added
   * @return added name (null if add not successful)
   */
  @Override
  public FoodNameModel addFoodname(FoodNameModel name) throws ConnectionException {

    logger.log(Level.FINE, "addFoodname()");
    
    FoodNameModel m = null;
    
    //get uid
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return m;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      m = StoreNutrition.addFoodNameModel(pm, name, UID, LOCALE);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding food name", e);
      throw new ConnectionException("addFoodname", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Creates / updates foodname (updates if already found)
   * @param title : model to be added
   * @return added name (null if add not successful)
   */
  @Override
  public List<FoodNameModel> addFoodnames(List<FoodNameModel> names) throws ConnectionException {

    logger.log(Level.FINE, "addFoodnames()");
    
    List<FoodNameModel> list = new ArrayList<FoodNameModel>();
    for(FoodNameModel name : names)
      list.add(addFoodname(name));
    
    return list;
  }

  /**
   * Adds guide value
   * @param model
   * @return
   */
  @Override public GuideValueModel addGuideValue(GuideValueModel model) throws ConnectionException {

    logger.log(Level.FINE, "addGuideValue()");
    
    GuideValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    GuideValue modelServer = null;
    try {
      //convert to server side model
      modelServer = GuideValue.getServerModel(model);
      
      //save user
      modelServer.setUid(UID);
      
      //save to db
      modelServer = pm.makePersistent(modelServer);

      //convert to client side model (which we return)
      m = GuideValue.getClientModel(modelServer);
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addGuideValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addGuideValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds meal to db
   * @param meal : model to be added
   * @return added meal (null if add not successful)
   * @throws ConnectionException 
   */
  @Override
  public MealModel addMeal(MealModel meal) throws ConnectionException {

    logger.log(Level.FINE, "addMeal()");

    List<MealModel> list = new ArrayList<MealModel>();
    list.add(meal);
    
    list = addMeals(list);
    
    if(list != null && list.size() > 0) {
        return list.get(0);
    }
    
    return null;
  }

  /**
   * Adds meal from time to db
   * @param meal : model to be added
   * @return added meal (null if add not successful)
   * @throws ConnectionException 
   */
  @Override
  public MealModel addMeal(MealModel meal, Long timeId) throws ConnectionException {

    logger.log(Level.FINE, "addMeal()");

    MealModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    MealInTime modelServer = null;
    MealInTime mealServerOrig = null;
    try {

      //get old meal from time
      mealServerOrig = null;
      Time t = pm.getObjectById(Time.class, timeId);
      for(MealInTime model : t.getMeals()) {
        if(model.getId().longValue() == meal.getId()) {
          mealServerOrig = model;
          break;
        }
      }
            
      //create a copy
      if(hasPermission(1, UID, t.getUid())) {
        
        modelServer = duplicateMeal(mealServerOrig);
        modelServer.setId(null);

        //if added to time
        Time timeServer = pm.getObjectById(Time.class, meal.getTimeId());
        if(timeServer != null) {
          List<MealInTime> listMeals = timeServer.getMeals();
          //create new meal-in-time model
          MealInTime mealInTime = new MealInTime();
          mealInTime.setName(modelServer.getName());

          //copy foods from old meal
          List<FoodInMealTime> listFoodsServer = new ArrayList<FoodInMealTime>();
          for(FoodInMealTime food : mealServerOrig.getFoods()) {
            //add food
            FoodInMealTime foodServer = new FoodInMealTime();
            foodServer.setNameId(food.getNameId());
            foodServer.setAmount(food.getAmount());
            listFoodsServer.add(foodServer);
          }
          mealInTime.setFoods(listFoodsServer);
          
          listMeals.add(mealInTime);
          timeServer.setMeals(listMeals);

          //save to db
          pm.makePersistent(timeServer);
          pm.flush();

          //convert to client side model (which we return)
          m = MealInTime.getClientModel(mealInTime);
          
          //return also foods
          List<FoodModel> listFoods = new ArrayList<FoodModel>();
          for(FoodInMealTime f : listFoodsServer) {
            FoodModel fClient = FoodInMealTime.getClientModel(f);
            //get name
            if(f.getNameId() != null) {
              if(f.getNameId() != 0) {
                fClient.setName( getFoodName(pm, f.getNameId()) );
              }
            }
            //set time
            fClient.setMealId(meal.getId());
            fClient.setTimeId(meal.getTimeId());
            fClient.setUid(meal.getUid());
            listFoods.add(fClient);
          }
          m.setFoods(listFoods);
          
        }
      }
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addMeal", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addMeal", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds meal to db
   * @param meal : model to be added
   * @return added meal (null if add not successful)
   * @throws ConnectionException 
   */
  @Override
  public List<MealModel> addMeals(List<MealModel> meals) throws ConnectionException {

    logger.log(Level.FINE, "addMeals()");
    
    List<MealModel> list = new ArrayList<MealModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    Meal modelServer = null;
    Meal mealServerOrig = null;
    try {
      //each meal
      for(MealModel meal : meals) {

        MealModel m = null;
        
        //if ID is null -> create new one (no foods)
        if(meal.getId() == 0) {
          
          //convert to server side model
          modelServer = Meal.getServerModel(meal);

          //add few empty foods
          List<FoodInMeal> foods = new ArrayList<FoodInMeal>();
          foods.add(new FoodInMeal());
          foods.add(new FoodInMeal());
          modelServer.setFoods(foods);

          modelServer.setUid(UID);

          //save to db
          pm.makePersistent(modelServer);

          //convert to client side model (which we return)
          m = Meal.getClientModel(modelServer);
        }
        //copied old meal
        else {
            
          //get old meal
          mealServerOrig = pm.getObjectById(Meal.class, meal.getId());

          //create a copy
          if(hasPermission(1, UID, mealServerOrig.getUid())) {

            //add one to copy count IF NOT our meal
            if(!mealServerOrig.getUid().equals(UID)) {
              mealServerOrig.incrementCopyCount();
            }
            
            modelServer = duplicateMeal(mealServerOrig);
            modelServer.setId(null);
            modelServer.setUid(UID);

            //if added to time
            if(meal.getTimeId() != 0) {

              Time timeServer = pm.getObjectById(Time.class, meal.getTimeId());
              List<MealInTime> listMeals = timeServer.getMeals();
              //create new meal-in-time model
              MealInTime mealInTime = new MealInTime();
              mealInTime.setName(modelServer.getName());

              //copy foods from old meal
              List<FoodInMealTime> listFoodsServer = new ArrayList<FoodInMealTime>();
              for(FoodInMeal food : mealServerOrig.getFoods()) {
                //add food
                FoodInMealTime foodServer = new FoodInMealTime();
                foodServer.setNameId(food.getNameId());
                foodServer.setAmount(food.getAmount());
                listFoodsServer.add(foodServer);
              }
              mealInTime.setFoods(listFoodsServer);
              
              listMeals.add(mealInTime);
              timeServer.setMeals(listMeals);

              //save to db
              pm.makePersistent(timeServer);
              pm.flush();

              //convert to client side model (which we return)
              m = MealInTime.getClientModel(mealInTime);
              
              //return also foods
              List<FoodModel> listFoods = new ArrayList<FoodModel>();
              for(FoodInMealTime f : listFoodsServer) {
                FoodModel fClient = FoodInMealTime.getClientModel(f);
                //get name
                if(f.getNameId() != null) {
                  if(f.getNameId() != 0) {
                    fClient.setName( getFoodName(pm, f.getNameId()) );
                  }
                }
                //set time
                fClient.setMealId(meal.getId());
                fClient.setTimeId(meal.getTimeId());
                fClient.setUid(meal.getUid());
                listFoods.add(fClient);
              }
              m.setFoods(listFoods);
              
            }
            //not added to time
            else {
              
              //copy foods from old meal
              List<FoodInMeal> listFoodsServer = new ArrayList<FoodInMeal>();
              for(FoodInMeal food : mealServerOrig.getFoods()) {
                //add food
                FoodInMeal foodServer = duplicateFood(food);
                listFoodsServer.add(foodServer);
              }
              modelServer.setFoods(listFoodsServer);

              //save to db
              pm.makePersistent(modelServer);

              //convert to client side model (which we return)
              m = Meal.getClientModel(modelServer);
            }
          }
        }
        
        //add to array
        if(m != null) {
          list.add(m);
        }
        
      }
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addMeals", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addMeals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Adds measurement to db
   * @param measurement : model to be added
   * @return added measurement (null if add not successful)
   */
  @Override
  public MeasurementModel addMeasurement(MeasurementModel meal) throws ConnectionException {

    logger.log(Level.FINE, "addMeasurement()");
    
    MeasurementModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    Measurement modelServer = null;
    try {
      
      //convert to server side model
      modelServer = Measurement.getServerModel(meal);
      
      //save user
      modelServer.setUid(UID);
      
      //save to db
      pm.makePersistent(modelServer);

      //convert to client side model (which we return)
      m = Measurement.getClientModel(modelServer);
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addMeasurement", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addMeasurement", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }
  
  /**
   * Adds measurement's value to db
   * @param measurement : measurement where we add this value
   * @param value : value to be added
   * @return added measurement (null if add not successful)
   */
  @Override
  public MeasurementValueModel addMeasurementValue(MeasurementModel measurement, MeasurementValueModel value) throws ConnectionException {

    logger.log(Level.FINE, "addMeasurementValue()");
    
    MeasurementValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    Measurement modelServer = null;
    try {
      
      //get measurement
      modelServer = pm.getObjectById(Measurement.class, measurement.getId());
      if(modelServer != null) {
        if(modelServer.getUid().equals(UID)) {
          
          //convert to server side model
          MeasurementValue model = MeasurementValue.getServerModel(value);
          //save user
          model.setUid(UID);
          
          //add value
          modelServer.getValues().add(model);

          //save to db
          pm.makePersistent(modelServer);

          //get added value
          MeasurementValue vNew = modelServer.getValues().get(modelServer.getValues().size() - 1);
          m = MeasurementValue.getClientModel(vNew);
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addMeasurementValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addMeasurementValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  public RoutineModel addRoutine(RoutineModel routine) throws ConnectionException  {

    logger.log(Level.FINE, "addRoutine()");

    RoutineModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Routine modelServer = null;
      
      //if ID is null -> create new one (no exercises)
      if(routine.getId() == 0) {
        
        //convert to server side model
        modelServer = Routine.getServerModel(routine);
      }
      else {
        
        //get model
        final Routine r = pm.getObjectById(Routine.class, routine.getId());
        
        boolean hasPermission = true;
        
        //if not ours -> check if we have permission to copy it
        if(!r.getUid().equals(UID)) {
          hasPermission = hasPermission(0, UID, r.getUid());
        }
        
        //create a copy
        if(hasPermission) {
          modelServer = duplicateRoutine(r);
          
          //increment copy count IF NOT our routine
          if(!r.getUid().equals(UID)) {
            r.incrementCopyCount();
          }
        }
        //no permission
        else {
          throw new Exception();
        }
        
        modelServer.setDate(routine.getDate());
        
      }

      //reset time from date
      Date d = modelServer.getDate();
      if(d != null) {
        d.setHours(0);
        d.setMinutes(0);
        d.setSeconds(0);
        modelServer.setDate(d);
      }
      
      //save user
      modelServer.setUid(UID);
      //default length: 7 days
      if(modelServer.getDays() == 0) {
        modelServer.setDays(7);
      }

      //save routine to db
      modelServer = pm.makePersistent(modelServer);

      //if workouts set -> add those
      if(routine.getWorkouts() != null) {
        for(WorkoutModel w : routine.getWorkouts()) {
          //new workout
          if(w.getId() == 0) {
            w.setRoutineId(modelServer.getId());
            this.addWorkout(w);
          }
        }
      }
      
      //convert to client side model (which we return)
      m = Routine.getClientModel(modelServer);
      
      //get workouts and copy those also (IF NOT new routine)
      if(routine.getId() != 0) {

        Query q = pm.newQuery(Workout.class);
        q.setFilter("date == null && routineId == routineIdParam && dayInRoutine > 0");
        q.declareParameters("java.lang.Long routineIdParam");
        List<Workout> workouts = (List<Workout>) q.execute(routine.getId());

        for(Workout w : workouts) {
          Workout wNew = duplicateWorkout(w);
          //set new routine id
          wNew.setRoutineId(modelServer.getId());
          //if routine has date -> set workout date based on that
          if(routine.getDate() != null) {
            final Date dateNew = new Date( (routine.getDate().getTime() / 1000 + 3600 * 24 * (wNew.getDayInRoutine().intValue() - 1) ) * 1000);
            //reset time from date
            dateNew.setHours(0);
            dateNew.setMinutes(0);
            dateNew.setSeconds(0);

            wNew.setDate(dateNew);
          }
          wNew.setUid(UID);

          pm.makePersistent(wNew);
        }
        
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addRoutine", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addRoutine", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }
  
  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  public List<RoutineModel> addRoutines(List<RoutineModel> routines) throws ConnectionException  {

    logger.log(Level.FINE, "addRoutines()");

    List<RoutineModel> list = new ArrayList<RoutineModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Routine modelServer = null;
      
      for(RoutineModel routine : routines) {
        //if ID is null -> create new one (no exercises)
        if(routine.getId() == 0) {
          
          //convert to server side model
          modelServer = Routine.getServerModel(routine);
        }
        else {
          
          //get model
          final Routine r = pm.getObjectById(Routine.class, routine.getId());
          
          boolean hasPermission = true;
          
          //if not ours -> check if we have permission to copy it
          if(!r.getUid().equals(UID)) {
            hasPermission = hasPermission(0, UID, r.getUid());
          }
          
          //create a copy
          if(hasPermission) {
            modelServer = duplicateRoutine(r);
            
            //increment copy count IF NOT our routine
            if(!r.getUid().equals(UID)) {
              r.incrementCopyCount();
            }
          }
            //no permission
          else {
            throw new Exception();
          }
          
          modelServer.setDate(routine.getDate());
          
        }

        //reset time from date
        Date d = modelServer.getDate();
        if(d != null) {
          d.setHours(0);
          d.setMinutes(0);
          d.setSeconds(0);
          modelServer.setDate(d);
        }
        
        //save user
        modelServer.setUid(UID);
        //default length: 7 days
        if(modelServer.getDays() == 0) {
          modelServer.setDays(7);
        }

        //save routine to db
        modelServer = pm.makePersistent(modelServer);

        //if workouts set -> add those
        if(routine.getWorkouts() != null) {
          for(WorkoutModel w : routine.getWorkouts()) {
            //new workout
            if(w.getId() == 0) {
              w.setRoutineId(modelServer.getId());
              this.addWorkout(w);
            }
          }
        }
        
        //convert to client side model (which we return)
        RoutineModel m = Routine.getClientModel(modelServer);
        
        //get workouts and copy those also (IF NOT new routine)
        if(routine.getId() != 0) {

          Query q = pm.newQuery(Workout.class);
          q.setFilter("date == null && routineId == routineIdParam && dayInRoutine > 0");
          q.declareParameters("java.lang.Long routineIdParam");
          List<Workout> workouts = (List<Workout>) q.execute(routine.getId());

          for(Workout w : workouts) {
            Workout wNew = duplicateWorkout(w);
            //set new routine id
            wNew.setRoutineId(modelServer.getId());
            //if routine has date -> set workout date based on that
            if(routine.getDate() != null) {
              final Date dateNew = new Date( (routine.getDate().getTime() / 1000 + 3600 * 24 * (wNew.getDayInRoutine().intValue() - 1) ) * 1000);
              //reset time from date
              dateNew.setHours(0);
              dateNew.setMinutes(0);
              dateNew.setSeconds(0);

              wNew.setDate(dateNew);
            }
            wNew.setUid(UID);

            pm.makePersistent(wNew);
          }
          
        }
        
        //add to array
        list.add(m);
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addRoutines", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addRoutine", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Adds run to db
   * @param run : model to be added
   * @return added run (null if add not successful)
   * @throws ConnectionException 
   */
  @Override
  public RunModel addRun(RunModel meal) throws ConnectionException {

    logger.log(Level.FINE, "addRun()");
    
    RunModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    Run modelServer = null;
    try {
      
      //convert to server side model
      modelServer = Run.getServerModel(meal);
      
      //save user
      modelServer.setUid(UID);
      
      //save to db
      modelServer = pm.makePersistent(modelServer);

      //convert to client side model (which we return)
      m = Run.getClientModel(modelServer);
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addRun", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addRun", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds run's value to db
   * @param run : run where we add this value
   * @param value : value to be added
   * @return added run (null if add not successful)
   */
  @Override
  public RunValueModel addRunValue(RunModel run, RunValueModel value) throws ConnectionException {

    logger.log(Level.FINE, "addRunValue()");
    
    RunValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    Run modelServer = null;
    try {

      //get run (if old)
      if(run.getId() > 0) {
        modelServer = pm.getObjectById(Run.class, run.getId());
      }
      //new run -> add
      else {
        modelServer = Run.getServerModel(this.addRun(run));
        modelServer.setUid(UID);
      }
      
      if(modelServer != null) {
        if(modelServer.getUid().equals(UID)) {
          
          //convert to server side model
          RunValue model = RunValue.getServerModel(value);
          //save user
          model.setUid(UID);
          
          //add exercise
          modelServer.getValues().add(model);

          //save to db
          pm.makePersistent(modelServer);

          //get added value
          RunValue vNew = modelServer.getValues().get(modelServer.getValues().size() - 1);
          m = RunValue.getClientModel(vNew);
          m.setName( Run.getClientModel(vNew.getRun()) );
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addRunValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addRunValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Adds ticket to db
   * @param ticket : model to be added
   * @return added ticket (null if add not successful
   */
  @Override
  public TicketModel addTicket(TicketModel ticket) throws ConnectionException  {

    logger.log(Level.FINE, "addTicket()");
        
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
          
    try {
      String data = "<ticket><priority>" + ticket.getPriority() + "</priority><summary>" + ticket.getTitle() + "</summary><version-id>1</version-id><milestone-id>4</milestone-id><description>" + ticket.getDesc() + "\n\nBy: " + ticket.getUid() + "</description></ticket>";
      HTTPRequest req = new HTTPRequest(new URL("http://motiver.unfuddle.com/api/v1/projects/1/tickets"), HTTPMethod.POST); 
      req.setHeader(new HTTPHeader("Authorization", "Basic " + Base64.encode("user:HCz1d7").trim())); 
      req.setHeader(new HTTPHeader("Content-Type", "application/xml")); 
      req.setPayload(data.getBytes()); 
      URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService(); 
      urlFetchService.fetch(req);
      
      //TODO we don't check the response!
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addTicket", e);
      throw new ConnectionException("addTicket", e.getMessage());
    }
    
    return ticket;
  }

  /**
   * Adds time to db
   * @param time : model to be added
   * @return added time (null if add not successful)
   * @throws ConnectionException 
   */
  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  public TimeModel addTime(TimeModel time) throws ConnectionException {

    logger.log(Level.FINE, "addTime()");

    TimeModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Time modelServer = null;
      
      //check if same time already exists -> return that instead
      final Date dStart = stripTime(time.getDate(), true);
      final Date dEnd = stripTime(time.getDate(), false);
        
      Query q = pm.newQuery(Time.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Time> times = (List<Time>) q.execute(UID, dStart, dEnd);
      for(Time t : times) {
        if(t.getTime() == time.getTime()) {
          modelServer = t;
          break;
        }
      }
      
      //if no similar model found
      if(modelServer == null) {
      
        //if ID is null -> create new one)
        if(time.getId() == 0) {
          
          //convert to server side model
          modelServer = Time.getServerModel(time);
        }
        else {
          
          //get model
          final Time t = pm.getObjectById(Time.class, time.getId());
          
          boolean hasPermission = true;
          
          //if not ours -> check if we have permission to copy it
          if(!t.getUid().equals(UID)) {
            hasPermission = hasPermission(1, UID, t.getUid());
          }
          
          //create a copy
          if(hasPermission) {
  
            modelServer = duplicateTime(t);
          }
        //no permission
          else {
            throw new Exception();
          }

          //set date if set
          modelServer.setDate(time.getDate());
        }

        //reset time from date
        Date d = modelServer.getDate();
        if(d != null) {
          d.setHours(0);
          d.setMinutes(0);
          d.setSeconds(0);
          modelServer.setDate(d);
        }
  
        //save user
        modelServer.setUid(UID);
  
        //save time to db
        pm.makePersistent(modelServer);
      }

      //convert to client side model (which we return)
      m = Time.getClientModel(modelServer);
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addTime", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addTime", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
    
  }

  /**
   * Adds time to db
   * @param time : model to be added
   * @return added time (null if add not successful)
   * @throws ConnectionException 
   */
  @SuppressWarnings({ "deprecation", "unchecked" })
  @Override
  public TimeModel[] addTimes(TimeModel[] timesParam) throws ConnectionException {

    logger.log(Level.FINE, "addTimes()");

    TimeModel[] list = new TimeModel[timesParam.length];
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      int i = 0;
      for(TimeModel time : timesParam) {
    
        
        Time modelServer = null;
        
        //check if same time already exists -> return that instead
        final Date dStart = stripTime(time.getDate(), true);
        final Date dEnd = stripTime(time.getDate(), false);
          
        Query q = pm.newQuery(Time.class);
        q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
        List<Time> times = (List<Time>) q.execute(UID, dStart, dEnd);
        for(Time t : times) {
          if(t.getTime() == time.getTime()) {
            modelServer = t;
            break;
          }
        }
        
        //if no similar model found
        if(modelServer == null) {
        
          //if ID is null -> create new one)
          if(time.getId() == 0) {
            
            //convert to server side model
            modelServer = Time.getServerModel(time);
          }
          else {
            
            //get model
            final Time t = pm.getObjectById(Time.class, time.getId());
            
            boolean hasPermission = true;
            
            //if not ours -> check if we have permission to copy it
            if(!t.getUid().equals(UID)) {
              hasPermission = hasPermission(1, UID, t.getUid());
            }
            
            //create a copy
            if(hasPermission) {
    
              modelServer = duplicateTime(t);
            }
            //no permission
            else {
              throw new Exception();
            }

            //set date if set
            modelServer.setDate(time.getDate());
          }

          //reset time from date
          Date d = modelServer.getDate();
          if(d != null) {
            d.setHours(0);
            d.setMinutes(0);
            d.setSeconds(0);
            modelServer.setDate(d);
          }
    
          //save user
          modelServer.setUid(UID);
    
          //save time to db
          pm.makePersistent(modelServer);
        }

        //convert to client side model (which we return)
        TimeModel m = Time.getClientModel(modelServer);
        
        list[i] = m;
        
        i++;
        
      }
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addTimes", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addTimes", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Adds workout to db
   * @param workout : model to be added
   * @return added workout (null if add not successful
   */
  @Override
  public WorkoutModel addWorkout(WorkoutModel workout) throws ConnectionException  {

    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    list.add(workout);
    list = addWorkouts(list);
    
    //get new workout
    WorkoutModel model = null;
    
    if(list.size() > 0) {
      model = list.get(0);
    }
    
    return model;
  }
  
  /**
   * Adds workouts to db
   * @param workouts : models to be added
   * @return added workouts (null if add not successful
   */
  @SuppressWarnings("deprecation")
  @Override
  public List<WorkoutModel> addWorkouts(List<WorkoutModel> workouts) throws ConnectionException  {

    logger.log(Level.FINE, "addWorkouts()");
    
    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
      
    try {
      Workout modelServer = null;
      
      for(WorkoutModel workout : workouts) {
        //if ID is null -> create new one (no exercises)
        if(workout.getId() == 0) {
          
          //convert to server side model
          modelServer = Workout.getServerModel(workout);
        }
        else {
          
          //get model
          final Workout w = pm.getObjectById(Workout.class, workout.getId());
                    
          //create a copy
          if(hasPermission(0, UID, w.getUid())) {
            modelServer = duplicateWorkout(w);
            
            //increment copy count IF NOT our workout
            if(!w.getUid().equals(UID)) {
              w.incrementCopyCount();
            }
          }
          //no permission
          else {
            throw new Exception();
          }
          
          //set routine or date if set
          modelServer.setDate(workout.getDate());
          modelServer.setRoutineId(workout.getRoutineId());
          modelServer.setDayInRoutine(workout.getDayInRoutine());
          
        }

        //reset time from date
        Date d = modelServer.getDate();
        if(d != null) {
          d.setHours(0);
          d.setMinutes(0);
          d.setSeconds(0);
          modelServer.setDate(d);
        }

        //save user
        modelServer.setUid(UID);

        //save workout to db
        modelServer = pm.makePersistent(modelServer);

        //if workouts set -> add those
        if(workout.getExercises() != null) {
          for(ExerciseModel e : workout.getExercises()) {
            //new workout
            if(e.getId() == 0) {
              e.setWorkoutId(modelServer.getId());
              this.addExercise(e);
            }
          }
        }
        
        //convert to client side model (which we return)
        WorkoutModel m = Workout.getClientModel(modelServer);
        
        //add to array
        list.add(m);
      }
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "addWorkouts", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addWorkouts", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Combines exercise names together
   * @param firstId : where other IDs are combined
   * @param ids : other IDs
   * @return
   */
  @Override @SuppressWarnings("unchecked")
  public Boolean combineExerciseNames(Long firstId, Long[] ids) throws ConnectionException {

    logger.log(Level.FINE, "combineExerciseNames()");

    boolean ok = true;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    //if not admin
    if( !isAdmin(UID) ) {
      return false;
    }
    
    try {

      for(int i=0; i < ids.length; i++) {
        
        //get name
        ExerciseName name = pm.getObjectById(ExerciseName.class, ids[i]);
        
        if(name != null) {
          Query q = pm.newQuery(Exercise.class);
          q.setFilter("name == nameParam");
          q.declareParameters("java.lang.Long nameParam");
          List<Exercise> exercises = (List<Exercise>) q.execute(ids[i]);

          //update other IDs
          for(Exercise e : exercises)
            e.setNameId(firstId);
          
          //delete name
          pm.deletePersistent(name);
          
          //remove search indexes which has this name as query
          final String strName = name.getName();
          Query q1 = pm.newQuery(ExerciseSearchIndex.class);
          List<ExerciseSearchIndex> arrQuery = (List<ExerciseSearchIndex>) q1.execute();
          for(ExerciseSearchIndex index : arrQuery) {
            //check if query words that match added name
            int count = 0;
            for(String s : index.getQuery().split(" ")) {
              //if word long enough and match
              if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD && strName.toLowerCase().contains( s.toLowerCase() )) {
                  count++;
              }
            }
            
            //if found -> remove index
            if(count > 0) {
              pm.deletePersistent(index);
            }
          }
        }
      }
      
      ok = true;

    } catch (Exception e) {
      ok = false;
      logger.log(Level.SEVERE, "combineExerciseNames", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("combineExerciseNames", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Combines food names together
   * @param firstId : where other IDs are combined
   * @param ids : other IDs
   * @return
   */
  @Override @SuppressWarnings("unchecked")
  public Boolean combineFoodNames(Long firstId, Long[] ids) {

    logger.log(Level.FINE, "combineFoodNames()");

    boolean ok = true;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    //if not admin
    if( !isAdmin(UID) ) {
      return false;
    }
    
    try {

      for(int i=0; i < ids.length; i++) {
        
        //get name
        FoodName name = pm.getObjectById(FoodName.class, ids[i]);
        
        if(name != null) {
          Query q = pm.newQuery(Food.class);
          q.setFilter("name == nameParam");
          q.declareParameters("java.lang.Long nameParam");
          List<Food> foods = (List<Food>) q.execute(ids[i]);

          //update other IDs
          for(Food f : foods)
            f.setNameId(firstId);
          
          //delete name
          pm.deletePersistent(name);
          
          //remove search indexes which has this name as query
          final String strName = name.getName();
          Query q1 = pm.newQuery(FoodSearchIndex.class);
          List<FoodSearchIndex> arrQuery = (List<FoodSearchIndex>) q1.execute();
          for(FoodSearchIndex index : arrQuery) {
            //check if query words that match added name
            int count = 0;
            for(String s : index.getQuery().split(" ")) {
              //if word long enough and match
              if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD && strName.toLowerCase().contains( s.toLowerCase() )) {
                  count++;
              }
            }
            
            //if found -> remove index
            if(count > 0) {
              pm.deletePersistent(index);
            }
          }
        }
      }
      
      ok = true;

    } catch (Exception e) {
      logger.log(Level.SEVERE, "combineFoodNames", e);
      ok = false;
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  public String convertStreamToString(InputStream is) {

    logger.log(Level.FINE, "convertStreamToString()");
  /*
   * To convert the InputStream to String we use the
   * Reader.read(char[] buffer) method. We iterate until the
   * Reader return -1 which means there's no more data to
   * read. We use the StringWriter class to produce the string.
   */
  if (is != null) {
    String response = "";
      Writer writer = new StringWriter();
      Reader reader = null;
  
      char[] buffer = new char[1024];
      try {
          reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
          int n;
          while ((n = reader.read(buffer)) != -1) {
              writer.write(buffer, 0, n);
          }
          response = writer.toString();
      }
      catch(Exception ex) {
        logger.log(Level.SEVERE, "convertStreamToString", ex);
      }
      
      //close everything
      try {
        is.close();
      } catch (Exception e) {
        logger.log(Level.SEVERE, "convertStreamToString", e);
      }
      try {
        reader.close();
      } catch (Exception e) {
        logger.log(Level.SEVERE, "convertStreamToString", e);
      }
      try {
        reader.close();
      } catch (Exception e) {
        logger.log(Level.SEVERE, "convertStreamToString", e);
      }
      
      return response;
  } else {        
      return "";
  }
  }

  @Override public Boolean dummy(MicroNutrientModel model) {

    logger.log(Level.FINE, "dummy()");
    return false;
  }

  @Override public MonthlySummaryExerciseModel dummy2(MonthlySummaryExerciseModel model) {

    logger.log(Level.FINE, "dummy2()");
    return new MonthlySummaryExerciseModel();
  }

  /**
   * Removes all data from user
   * @param target : what to remove (0=training, 1=nutrition, 2=measurement, 3=cardio&run)
   * @return if entities left
   */
  @SuppressWarnings("unchecked")
  @Override
  public Boolean fetchRemoveAll(Boolean removeTraining, Boolean removeCardio, Boolean removeNutrition, Boolean removeMeasurement) throws ConnectionException {

    logger.log(Level.FINE, "fetchRemoveAll()");
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return true;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    int count = 0;
    try {
      Query q = null;

      if(removeCardio) {
        //cardio
        if(count < MAX_COUNT) {
          q = pm.newQuery(Cardio.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Cardio> l = (List<Cardio>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        //values
        if(count < MAX_COUNT) {
          q = pm.newQuery(CardioValue.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<CardioValue> l = (List<CardioValue>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        
        //run
        if(count < MAX_COUNT) {
          q = pm.newQuery(Run.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Run> l = (List<Run>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        //values
        if(count < MAX_COUNT) {
          q = pm.newQuery(RunValue.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<RunValue> l = (List<RunValue>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
      }

      if(removeNutrition) {
        //foods
        if(count < MAX_COUNT) {
          q = pm.newQuery(Food.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Food> l = (List<Food>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }

        //guide values
        if(count < MAX_COUNT) {
          q = pm.newQuery(GuideValue.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<GuideValue> l = (List<GuideValue>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        //meals
        if(count < MAX_COUNT) {
          q = pm.newQuery(Meal.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Meal> l = (List<Meal>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        
        //times
        if(count < MAX_COUNT) {
          q = pm.newQuery(Time.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Time> l = (List<Time>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        
      }
      
      if(removeMeasurement) {

        //measurement
        if(count < MAX_COUNT) {
          q = pm.newQuery(Measurement.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Measurement> l = (List<Measurement>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        //values
        if(count < MAX_COUNT) {
          q = pm.newQuery(MeasurementValue.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<MeasurementValue> l = (List<MeasurementValue>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        
      }
      
      if(removeTraining) {
        
        //routines
        if(count < MAX_COUNT) {
          q = pm.newQuery(Routine.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Routine> l = (List<Routine>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }

        //workouts
        if(count < MAX_COUNT) {
          q = pm.newQuery(Workout.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<Workout> l = (List<Workout>)q.execute(UID);
          //if something found -> delete it
          if(l.size() > 0) {
            count += l.size();
            //delete all
            pm.deletePersistentAll(l);
          }
        }
        
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchRemoveAll", e);
      if (!pm.isClosed()) {
        pm.close();
      }
      throw new ConnectionException("fetchRemoveAll", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
        
    return count >= MAX_COUNT;
        
  }

  /**
   * Saves cardios
   * @param cardios
   * @param values (values for each cardio)
   * @return
   */
  @Override public Boolean fetchSaveCardios(List<CardioModel> cardios, List<List<CardioValueModel>> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveCardios()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    try {
      //each cardio
      int i = 0;
      for(CardioModel mCardio : cardios) {

        CardioModel mCardioAdded = addCardio(mCardio);
        
        //values
        for(CardioValueModel m : values.get(i)) {

          try {
            addCardioValue(mCardioAdded, m);
          } catch (Exception e) {
            logger.log(Level.SEVERE, "", e);
          }
        }
        
        i++;
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveCardios", e);
      throw new ConnectionException("fetchSaveCardios", e.getMessage());
    }
    
    return ok;
  }

  /**
   * Saves foods' names
   * @param names
   * @return
   */
  @Override public Boolean fetchSaveFoodNames(List<FoodNameModel> names) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveFoodNames()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    try {
      //each food
      for(FoodNameModel mName : names) {
        addFoodname(mName);
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveFoodNames", e);
      throw new ConnectionException("fetchSaveFoodNames", e.getMessage());
    }
    
    return ok;
  }

  /**
   * Saves guide values (nutrition)
   * @param values
   * @return
   */
  @Override public Boolean fetchSaveGuideValues(List<GuideValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveGuideValues()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each run
      List<GuideValue> listServer = new ArrayList<GuideValue>();
      for(GuideValueModel mValue : values) {

        GuideValue m = GuideValue.getServerModel(mValue);
        m.setUid(UID);
        listServer.add(m);
      }
      
      //save all
      pm.makePersistentAll(listServer);

      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveGuideValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchSaveGuideValues", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Saves meals
   * @param meals
   * @return
   */
  @Override public Boolean fetchSaveMeals(List<MealModel> meals) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveMeals()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each food
      for(MealModel meal : meals) {

        //add meal
        final Meal mMealAdded = Meal.getServerModel(meal);
        mMealAdded.setUid(UID);
        
        //foods
        List<FoodInMeal> list = new ArrayList<FoodInMeal>();
        for(FoodModel food : meal.getFoods()) {
          //add food
          FoodInMeal foodServer = FoodInMeal.getServerModel(food);
          
          //if no food name -> search for it
          if(food.getName() != null && food.getName().getId() != 0) {
            foodServer.setNameId(fetchAddFoodName(food));
          }

          list.add(foodServer);
        }
        mMealAdded.setFoods(list);
        
        pm.makePersistent(mMealAdded);
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveMeals", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchSaveMeals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Saves measurements
   * @param measurements
   * @param values (values for each measurement)
   * @return
   */
  @Override @SuppressWarnings("unchecked")
  public Boolean fetchSaveMeasurements(MeasurementModel measurement, List<MeasurementValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveMeasurements()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each measurement
      //check if already found
      Query q = pm.newQuery(Measurement.class);
      q.setFilter("name == nameParam && openId == openIdParam");
      q.declareParameters("java.lang.String nameParam, java.lang.String openIdParam");
      List<Measurement> arr = (List<Measurement>) q.execute(measurement.getNameServer(), UID);
      
      MeasurementModel mMeasurementAdded = null;
      
      //if found
      if(arr.size() > 0) {
        mMeasurementAdded = Measurement.getClientModel(arr.get(0));
      }
      //create new
      else {
        
        //convert to server side model
        Measurement modelServer = Measurement.getServerModel(measurement);
        
        //save user
        modelServer.setUid(UID);
        
        //save to db
        pm.makePersistent(modelServer);

        //convert to client side model (which we return)
        mMeasurementAdded = Measurement.getClientModel(modelServer);
      }
      
      //values
      for(MeasurementValueModel m : values) {

        try {
          addMeasurementValue(mMeasurementAdded, m);
        } catch (Exception e) {
          logger.log(Level.SEVERE, "fetchSaveMeasurements", e);
        }
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveMeasurements", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchSaveMeasurements", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Saves routins
   * @param routines
   * @param workouts (workouts for each routine)
   * @return
   */
  @Override public Boolean fetchSaveRoutines(List<RoutineModel> routines, List<List<WorkoutModel>> workouts) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveRoutines()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each routine
      int i = 0;
      for(RoutineModel mRoutine : routines) {

        Routine mRoutineAdded = Routine.getServerModel(mRoutine);
        mRoutineAdded.setUid(UID);
        pm.makePersistent(mRoutineAdded);
        
        //workouts
        List<Workout> list = new ArrayList<Workout>();
        for(WorkoutModel m : workouts.get(i)) {

          try {
            //add workout
            Workout wModelServer = Workout.getServerModel(m);
            wModelServer.setRoutineId(mRoutineAdded.getId());
            wModelServer.setUid(UID);
            wModelServer = pm.makePersistent(wModelServer);
            
            if(wModelServer != null) {
              
              //add exercises
              List<ExerciseModel> ex = m.getExercises();
              for(ExerciseModel exercise : ex) {
                
                Exercise modelServer = Exercise.getServerModel(exercise);
                
                //if no exercise name ID
                if(modelServer.getNameId() == 0) {
                  ExerciseNameModel mName = this.addExercisename(exercise.getName());
                  //update name/target
                  modelServer.setNameId(mName.getId());
                }
                
                //add exercise
                wModelServer.getExercises().add(modelServer);
              }
              
              list.add(wModelServer);
            }
          } catch (Exception e) {
            logger.log(Level.SEVERE, "fetchSaveRoutines", e);
          }
        }

        //save to db
        pm.makePersistentAll(list);
        
        i++;
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveRoutines", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchSaveRoutines", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Saves runs
   * @param runs
   * @param values (values for each run)
   * @return
   */
  @Override public Boolean fetchSaveRuns(List<RunModel> runs, List<List<RunValueModel>> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveRuns()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    try {
      //each run
      int i = 0;
      for(RunModel mRun : runs) {

        RunModel mRunAdded = addRun(mRun);
        
        //values
        for(RunValueModel m : values.get(i)) {

          try {
            addRunValue(mRunAdded, m);
          } catch (Exception e) {
            logger.log(Level.SEVERE, "fetchSaveRuns", e);
          }
        }
        
        i++;
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "", e);
      throw new ConnectionException("fetchSaveRuns", e.getMessage());
    }
    
    return ok;
  }

  /**
   * Saves times
   * @param times
   * @return
   */
  @Override public Boolean fetchSaveTimes(List<TimeModel> times) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveTimes()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each food
      for(TimeModel mTime : times) {

        Time mTimeAdded = Time.getServerModel(mTime);
        mTimeAdded.setUid(UID);
        
        if(mTimeAdded.getId() != 0)  {
          //meals
          List<MealInTime> listMeals = new ArrayList<MealInTime>();
          for(MealModel meal : mTime.getMeals()) {
            //add meal
            final MealInTime mMealAdded = MealInTime.getServerModel(meal);
            
            //foods
            List<FoodInMealTime> list = new ArrayList<FoodInMealTime>();
            for(FoodModel food : meal.getFoods()) {
              //add food
              FoodInMealTime foodServer = FoodInMealTime.getServerModel(food);
              
              //if no food name -> search for it
              if(food.getName() != null && food.getName().getId() != 0) {
                foodServer.setNameId(fetchAddFoodName(food));
              }
              
              list.add(foodServer);
            }
            mMealAdded.setFoods(list);
            listMeals.add(mMealAdded);
          }
          mTimeAdded.setMeals(listMeals);
          
          //foods
          List<FoodInTime> list = new ArrayList<FoodInTime>();
          for(FoodModel food : mTime.getFoods()) {
            //add food
            FoodInTime foodServer = FoodInTime.getServerModel(food);
            
            //if no food name -> search for it
            if(food.getName() != null && food.getName().getId() != 0) {
              foodServer.setNameId(fetchAddFoodName(food));
            }

            list.add(foodServer);
          }
          mTimeAdded.setFoods(list);
          mTimeAdded = pm.makePersistent(mTimeAdded);
        }
        
      }
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveTimes", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchSaveTimes", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  @Override
  public Boolean fetchSaveWorkouts(List<WorkoutModel> workouts) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveWorkouts()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
        
    try {
      List<Workout> list = new ArrayList<Workout>();
      for(WorkoutModel m : workouts) {

        try {
          //add workout
          Workout wModelServer = Workout.getServerModel(m);
          wModelServer.setUid(UID);
            
          //add exercises
          List<ExerciseModel> ex = m.getExercises();
          for(ExerciseModel exercise : ex) {
            
            Exercise modelServer = Exercise.getServerModel(exercise);
            
            //if no exercise name ID
            if(modelServer.getNameId() == 0) {
              ExerciseNameModel mName = this.addExercisename(exercise.getName());
              //update name/target
              modelServer.setNameId(mName.getId());
            }
            
            //add exercise
            wModelServer.getExercises().add(modelServer);
          }
          
          list.add(wModelServer);
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "fetchSaveWorkouts", e);
        }
      }

      //save to db
      pm.makePersistentAll(list);
      
      ok = true;
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchSaveWorkouts", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchSaveWorkouts", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Gets data for blog
   * @param target : what is returned (0=all, 1=training, 2=nutrition, 3=cardio, 4=measurements, 5=training&cardio)
   * @param dateStartParam
   * @param dateEndParam
   * @param uid : if null use current user's uid
   * @param showEmptyDays : if empty days are returned
   * @return blog data for each day
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  public List<BlogData> getBlogData(int index, int limit, int target, Date dateStartParam, Date dateEndParam, String uidObj, Boolean showEmptyDays) throws ConnectionException {

    logger.log(Level.FINE, "getBlogData()");

    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    //check if no uid -> use ours
    if(uidObj == null) {
      uidObj = UID;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    List<BlogData> data = new ArrayList<BlogData>();
    
    try {

      //if not our uid -> check permission
      boolean permissionTraining = true;
      boolean permissionNutrition = true;
      boolean permissionNutritionFoods = true;
      boolean permissionCardio = true;
      boolean permissionMeasurement = true;
            
      //get user
      UserModel user = StoreUser.getUserModel(pm, uidObj);
      
      if(user != null) {
        
        String uid = user.getUid();
        if(!uid.equals(UID)) {
          permissionTraining = hasPermission(0, UID, uid);
          permissionNutrition = hasPermission(1, UID, uid);
          permissionNutritionFoods = hasPermission(2, UID, uid);
          permissionCardio = hasPermission(3, UID, uid);
          permissionMeasurement = hasPermission(4, UID, uid);
        }
        //if no permission at all -> return null
        if(!permissionTraining && !permissionNutrition && !permissionCardio && !permissionMeasurement) {
          return null;
        }
      }
      //if no user found -> return null
      if(user == null) {
        return null;
      }
      
      //reset start date
      if(dateStartParam != null) {
        dateStartParam = stripTime(dateStartParam, true);
      }
            
      final int fetchDaysBack = 11; //how many days we search at once
      int fetchDays = 0;  //day counter
      boolean stopSearch = false;

      int dataSize = 0;
      //search one week at time -> until we stop (or hit limit)
      while(!stopSearch && fetchDays < Constants.LIMIT_BLOG_DAY_BACK) {

        //strip time
        Date dStart = new Date((dateEndParam.getTime() / 1000 - 3600 * 24 * (fetchDays + fetchDaysBack - 1)) * 1000);
        dStart = stripTime(dStart, true);
        Date dEnd = new Date((dateEndParam.getTime() / 1000 - 3600 * 24 * (fetchDays)) * 1000);
        dEnd = stripTime(dEnd, false);
        
        final Object[] arrParams = new Object[] {user.getUid(), dStart, dEnd};
        
        //variables
        List<Workout> workouts = null;
        List<Time> times = null;
        List<CardioValue> cValues = null;
        List<RunValue> rValues = null;
        List<MeasurementValue> mValues = null;

        Query q;
        
        //TRAINING
        if(permissionTraining && (target == 0 || target == 1 || target == 5)) {
          q = pm.newQuery(Workout.class);
          q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
          q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          q.setOrdering("date DESC");
          workouts = (List<Workout>) q.executeWithArray(arrParams);
        }
        
        //NUTRITION
        if(permissionNutrition && (target == 0 || target == 2)) {
          q = pm.newQuery(Time.class);
          q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
          q.setOrdering("date DESC");
          q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          times = (List<Time>) q.executeWithArray(arrParams);
        }
        
        //CARDIO
        if(permissionCardio && (target == 0 || target == 3 || target == 5)) {
          q = pm.newQuery(CardioValue.class);
          q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
          q.setOrdering("date DESC");
          q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          cValues = (List<CardioValue>) q.executeWithArray(arrParams);
        
          //RUN
          q = pm.newQuery(RunValue.class);
          q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
          q.setOrdering("date DESC");
          q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          rValues = (List<RunValue>) q.executeWithArray(arrParams);

        }
        
        //MEASUREMENTS
        if(permissionMeasurement && (target == 0 || target == 4)) {
          
          q = pm.newQuery(MeasurementValue.class);
          q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
          q.setOrdering("date DESC");
          q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
          mValues = (List<MeasurementValue>) q.executeWithArray(arrParams);
        }

        //go through each day and search fetched arrays
        for(int i=0; i < fetchDaysBack; i++) {

          //if limit reached -> add null value
          if(data.size() == limit) {
            data.add(null);
            stopSearch = true;
            break;
          }
          
          final Date d = new Date((dateEndParam.getTime() / 1000 - 3600 * 24 * (fetchDays + i)) * 1000);
          final SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yy");
          final String strD = fmt.format(d);
          
          //END if start date is before given start date
          if(dateStartParam != null && d.getTime() < dateStartParam.getTime()) {
            stopSearch = true;
            break;
          }
          
          //blog data
          BlogData bd = new BlogData();
          bd.setUser(user);
          bd.setDate(d);

          boolean found = false;
          
          //go through WORKOUTS
          if(workouts != null) {
            List<WorkoutModel> arrW = new ArrayList<WorkoutModel>();
            for(Workout w : workouts) {
              if(fmt.format(w.getDate()).equals(strD)) {
                arrW.add( StoreTraining.getWorkoutModel(pm, w.getId(), UID));
                
                found = true;
              }
            }
            bd.setWorkouts(arrW);
          }
          
          //go through TIMES
          if(times != null) {
            List<Time> arrT = new ArrayList<Time>();
            for(Time t : times) {
              if(fmt.format(t.getDate()).equals(strD)) {
                arrT.add(t);
              }
            }
            //if times found
            if(arrT.size() > 0) {
              NutritionDayModel ndm = calculateEnergyFromTimes(pm, arrT, UID);
              if(ndm.getEnergy() > 0) {
                ndm.setFoodsPermission(permissionNutritionFoods);
                bd.setNutrition(ndm);
                
                found = true;
              }
            }
          }
          
          //go through CARDIOS
          if(cValues != null) {
            List<CardioValueModel> arrC = new ArrayList<CardioValueModel>();
            for(CardioValue c : cValues) {
              if(fmt.format(c.getDate()).equals(strD)) {
                CardioValueModel m = CardioValue.getClientModel(c);
                m.setName( Cardio.getClientModel(c.getCardio()) );
                arrC.add(m);
                
                found = true;
              }
            }
            bd.setCardios(arrC);
          }
          
          //go through RUNS
          if(rValues != null) {
            List<RunValueModel> arrC = new ArrayList<RunValueModel>();
            for(RunValue c : rValues) {
              if(fmt.format(c.getDate()).equals(strD)) {
                RunValueModel m = RunValue.getClientModel(c);
                m.setName( Run.getClientModel(c.getRun()) );
                arrC.add(m);

                found = true;
              }
            }
            bd.setRuns(arrC);
          }
          
          //go through MEASUREMENTS
          if(mValues != null) {
            List<MeasurementValueModel> arrC = new ArrayList<MeasurementValueModel>();
            for(MeasurementValue c : mValues) {
              if(fmt.format(c.getDate()).equals(strD)) {
                MeasurementValueModel m = MeasurementValue.getClientModel(c);
                m.setName( Measurement.getClientModel(c.getMeasurement()) );
                arrC.add(m);
                
                found = true;
              }
            }
            bd.setMeasurements(arrC);
          }
          
          //if after index (has data or showing each day)
          if(found || showEmptyDays) {
            if(dataSize >= index) {
              data.add(bd);
            }
            dataSize++;
          }
        }
        
        fetchDays += fetchDaysBack;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getBlogData", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getBlogData", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return data;
  }
  
  /**
   * Returns all cardios
   * @return cardios' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<CardioModel> getCardios(int index) throws ConnectionException {

    logger.log(Level.FINE, "getCardios()");

    List<CardioModel> list = new ArrayList<CardioModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Cardio.class);

      q.setFilter("openId == openIdParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setOrdering("name ASC");
      q.setRange(index, index + Constants.LIMIT_CARDIOS + 1);
      List<Cardio> cardios = (List<Cardio>) q.execute(UID);
      
      //get cardios
      if(cardios != null) {

        int i = 0;
        for(Cardio w : cardios) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_CARDIOS) {
            list.add(null);
            break;
          }
          
          CardioModel m = Cardio.getClientModel(w);
                  
          list.add(m);
          
          i++;
        }
        
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getCardios", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getCardios", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns last value from given cardio
   * @param cardioId
   * @return model
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  public CardioValueModel getCardioValue(Long cardioId) throws ConnectionException {

    logger.log(Level.FINE, "getCardioValue()");
    
    CardioValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Cardio w = pm.getObjectById(Cardio.class, cardioId);
      if(w != null) {
        if(hasPermission(3, UID, w.getUid())) {

          //get last value
          Query q = pm.newQuery(CardioValue.class);
          q.setFilter("cardio == cardioParam");
          q.setOrdering("date DESC");
          q.declareParameters("com.delect.motiver.server.Cardio cardioParam");
          List<CardioValue> values = (List<CardioValue>) q.execute(w);
          if(values.size() > 0) {
            m = CardioValue.getClientModel(values.get(0));
            m.setName( Cardio.getClientModel(values.get(0).getCardio()) );
          }
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getCardioValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getCardioValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }
  
  /**
   * Returns all foods from single meal
   * @param cardio
   * @return foods
   * @throws ConnectionException 
   */
  @Override
  public List<CardioValueModel> getCardioValues(CardioModel cardio, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getCardioValues()");
    
    List<CardioValueModel> list = new ArrayList<CardioValueModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    //strip time
    final Date dStart = stripTime(dateStart, true);
    final Date dEnd = stripTime(dateEnd, false);
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //get workout
      Cardio w = pm.getObjectById(Cardio.class, cardio.getId());
      
      if(w != null) {
        //if our cardio OR shared
        boolean hasPermission = true;
        if(!w.getUid().equals(UID)) {
          hasPermission = hasPermission(3, UID, w.getUid());
        }
        
        if(hasPermission) {
          List<CardioValue> listE = w.getValues();
          if(listE != null) {
            //go through each value
            for(CardioValue e : w.getValues()) {
              //check dates
              if(e.getDate().getTime() >= dStart.getTime() && e.getDate().getTime() <= dEnd.getTime()) {
                CardioValueModel eNew = CardioValue.getClientModel(e);

                list.add(eNew);
              }
            }
          }
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getCardioValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getCardioValues", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Returns last comments
   * @param target : which comments are loaded (null: all comments, wNNNN: workout with id NNNN)
   * @return comments
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  public List<CommentModel> getComments(int index, int limit, String target, String uid, boolean markAsRead) throws ConnectionException {

    logger.log(Level.FINE, "getComments()");

    List<CommentModel> list = new ArrayList<CommentModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
            
            //get comments
      List<Comment> comments = null;
      Query q = pm.newQuery(Comment.class);
      q.setOrdering("date DESC");
      q.setRange(index, index + Constants.LIMIT_COMMENTS + 1);
      if(target != null) {
        q.setFilter("target == targetParam && idTarget == idTargetParam");
        q.declareParameters("java.lang.String targetParam, java.lang.String idTargetParam");
        comments = (List<Comment>)q.execute(target, uid);
      }
      //all comments
      else {
        q.setFilter("idTarget == idTargetParam");
        q.declareParameters("java.lang.String idTargetParam");
        comments = (List<Comment>)q.execute(uid);
      }
      
      if(comments != null) {
        
        int i=0;
        for(Comment cc : comments) {
          
          try {
            //if limit reached -> add null value
            if(i == limit) {
              list.add(null);
              break;
            }
            
            CommentModel c = Comment.getClientModel(cc);
            
            //is unread? (if not our comment and meant for us)
            if(!UID.equals(c.getUid()) && UID.equals(c.getUidTarget())) {
              Query qUnread = pm.newQuery(CommentsRead.class); 
              qUnread.setFilter("comment == commentParam && openId == openIdParam");
              qUnread.declareParameters("com.google.appengine.api.datastore.Key commentParam, java.lang.String openIdParam");
              qUnread.setRange(0, 1);
              List<CommentsRead> unreads = (List<CommentsRead>)qUnread.execute(cc.getKey(), UID);
              c.setUnread(unreads.size() == 0);

              //mark this as read
              if(markAsRead && unreads.size() == 0) {
                CommentsRead cr = new CommentsRead();
                cr.setComment(cc.getKey());
                cr.setUid(UID);
                pm.makePersistent(cr);
              }
            }
                  
            //if all comments -> don't return user's own comments
            if(target != null || !UID.equals(c.getUid())) {
              
              //if all comments -> cut long texts
              if(target == null) {
                String text = c.getText();
                if(text.length() > Constants.LIMIT_COMMENT_LENGTH) {
                  text = text.substring(0, Constants.LIMIT_COMMENT_LENGTH - 2) + "...";
                  c.setText(text);
                }
              }
              
              String xid = cc.getTarget();
              if(xid == null) {
                xid = "";
              }
              
              boolean found = false;
              
              //get model//workout
              if(xid.matches("w[0-9]*")) {
                final long id = Long.parseLong(xid.replace("w", ""));
                WorkoutModel w = StoreTraining.getWorkoutModel(pm, id, UID);
                if(w != null) {
                  found = true;
                  c.setWorkout( w );
                }
              }
              //routine
              else if(xid.matches("r[0-9]*")) {
                final long id = Long.parseLong(xid.replace("r", ""));
                Routine r = pm.getObjectById(Routine.class, id);
                if(r != null) {
                  //check permission
                  boolean hasPermission = true;
                  if(!r.getUid().equals(UID)) {
                    hasPermission = hasPermission(0, UID, r.getUid());
                  }
                  if(hasPermission) {
                    found = true;
                    c.setRoutine( Routine.getClientModel(r) );
                  }
                }
              }
              //meal
              else if(xid.matches("m[0-9]*")) {
                final long id = Long.parseLong(xid.replace("m", ""));
                Meal m = pm.getObjectById(Meal.class, id);
                if(m != null) {
                  //check permission
                  boolean hasPermission = true;
                  if(!m.getUid().equals(UID)) {
                    hasPermission = hasPermission(1, UID, m.getUid());
                  }
                  if(hasPermission) {
                    found = true;
                    c.setMeal( Meal.getClientModel(m) );
                  }
                }
              }
              //measurement
              else if(xid.matches("me[0-9]*")) {
                final long id = Long.parseLong(xid.replace("me", ""));
                Measurement m = pm.getObjectById(Measurement.class, id);
                if(m != null) {
                  //check permission
                  boolean hasPermission = true;
                  if(!m.getUid().equals(UID)) {
                    hasPermission = hasPermission(4, UID, m.getUid());
                  }
                  if(hasPermission) {
                    found = true;
                    c.setMeasurement( Measurement.getClientModel(m) );
                  }
                }
              }
              //cardio
              else if(xid.matches("c[0-9]*")) {
                final long id = Long.parseLong(xid.replace("c", ""));
                Cardio m = pm.getObjectById(Cardio.class, id);
                if(m != null) {
                  //check permission
                  boolean hasPermission = true;
                  if(!m.getUid().equals(UID)) {
                    hasPermission = hasPermission(3, UID, m.getUid());
                  }
                  if(hasPermission) {
                    found = true;
                    c.setCardio( Cardio.getClientModel(m) );
                  }
                }
              }
              //run
              else if(xid.matches("ru[0-9]*")) {
                final long id = Long.parseLong(xid.replace("ru", ""));
                Run m = pm.getObjectById(Run.class, id);
                if(m != null) {
                  //check permission
                  boolean hasPermission = true;
                  if(!m.getUid().equals(UID)) {
                    hasPermission = hasPermission(3, UID, m.getUid());
                  }
                  if(hasPermission) {
                    found = true;
                    c.setRun( Run.getClientModel(m) );
                  }
                }
              }
              //nutrition
              else if(xid.matches("n[0-9]*")) {
                final Date d = new Date(Long.parseLong(xid.replace("n", "")) * 1000);
                c.setNutritionDate(d);
                found = true;
              }
              
              if(found) {
                list.add(c);
                i++;
              }
            }
          } catch (NoPermissionException e) {
            //no permission -> skipping this one
          }
        }
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "getComments", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getComments", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }
  
  /**
   * Get micronutrients from single day
   * @param date
   * @return micronutrients
   */
  @Override @SuppressWarnings("unchecked")
  public List<Double> getEnergyInCalendar(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getEnergyInCalendar()");

    if(dateStart.getTime() > dateEnd.getTime()) {
      return null;
    }
    
    List<Double> list = new ArrayList<Double>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //go through days
      final int days = (int)((dateEnd.getTime() - dateStart.getTime()) / (24 * 60 * 60 * 1000)) + 1;
      
      for(int i=0; i < days; i++) {
        
        final Date d = new Date((dateStart.getTime() / 1000 + 3600 * 24 * i) * 1000);
        //strip time
        final Date dStart = stripTime(d, true);
        final Date dEnd = stripTime(d, false);
        
        //get times
        Query q = pm.newQuery(Time.class);
        q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
        List<Time> times = (List<Time>) q.execute(UID, dStart, dEnd);

        NutritionDayModel m = calculateEnergyFromTimes(pm, times, UID);

        //round
        list.add(m.getEnergy());
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getEnergyInCalendar", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getEnergyInCalendar", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return list;
  }

  /**
   * Returns all exercises from single workout
   * @param workout
   * @return exercises
   * @throws ConnectionException 
   */
  @Override
  public List<ExerciseModel> getExercises(WorkoutModel workout) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading exercises from workout: "+workout.getId());
    }
    
    List<ExerciseModel> list = new ArrayList<ExerciseModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      WorkoutModel w = StoreTraining.getWorkoutModel(pm, workout.getId(), UID);      
      list = w.getExercises();
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading exercises", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getExercises", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns all exercises from given "name". Used for fetching last weights
   * @param nameId: exercise name's Id
   * @param dateStart
   * @param dateEnd
   * @param limit : -1 if no limit
   * @return exercises
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<ExerciseModel> getExercisesFromName(Long nameId, Date dateStart, Date dateEnd, int limit) throws ConnectionException {

    logger.log(Level.FINE, "getExercisesFromName()");
        
    List<ExerciseModel> list = new ArrayList<ExerciseModel>();
    
    if(dateStart == null && dateEnd == null) {
      return list;
    }
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      
      List<Workout> workouts = null;
      
      //if only end date -> get all exercises before that
      if(dateStart == null) {
        Date d1 = stripTime(dateEnd, true);
        
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && date != null && date <= dateParam");
        q.setOrdering("date DESC");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateParam");
        workouts = (List<Workout>) q.execute(UID, d1);
      }
      //if only start date -> get all exercises after that
      else if(dateEnd == null) {
        Date d1 = stripTime(dateEnd, false);
        
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && date != null && date >= dateParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateParam");
        workouts = (List<Workout>) q.execute(UID, d1);
      }
      //both dates
      else {
        Date d1 = stripTime(dateStart, false);
        Date d2 = stripTime(dateEnd, true);
        
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && date != null && date >= d1Param && date <= d2Param");
        q.setOrdering("date DESC");
        q.declareParameters("java.lang.String openIdParam, java.util.Date d1Param, java.util.Date d2Param");
        workouts = (List<Workout>) q.execute(UID, d1, d2);
        
      }
      
      for(Workout w : workouts) {
        //check if correct exercise name found
        for(Exercise e : w.getExercises()) {
          if(e.getNameId().longValue() == nameId.longValue()) {
            //save to list (including date, workoutId)
            ExerciseModel m = Exercise.getClientModel(e);
            m.setWorkoutId(e.getWorkout().getId());
            m.setDate(e.getWorkout().getDate());
            list.add(m);
          }
          
          if(limit != -1 && list.size() >= limit) {
            break;
          }
        }
        if(limit != -1 && list.size() >= limit) {
          break;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getExercisesFromName", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getExercises", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns single food name
   * @param id
   * @return name
   */
  @Override
  public FoodNameModel getFoodname(Long id) throws ConnectionException {

    logger.log(Level.FINE, "getFoodname()");
    
    FoodNameModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return m;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
  
      FoodName f = pm.getObjectById(FoodName.class, id);
      
      if(f != null) {
        m = FoodName.getClientModel(f);
        
        //micronutrients
        List<MicroNutrientModel> list = new ArrayList<MicroNutrientModel>();
        for(MicroNutrient mn : f.getMicroNutrients()) {
          list.add(MicroNutrient.getClientModel(mn));
        }
        m.setMicronutrients(list);
      }
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "getFoodname", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getFoodname", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }
  
  /**
   * Returns all foods from single meal
   * @param meal
   * @return foods
   * @throws ConnectionException 
   */
  @Override
  public List<FoodModel> getFoods(MealModel meal) throws ConnectionException {

    logger.log(Level.FINE, "getFoods()");

    if(meal == null) {
      return null;
    }
    
    List<FoodModel> list = new ArrayList<FoodModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
        
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {      
      //if in time
      if(meal.getTimeId() != 0) {
        TimeModel time = StoreNutrition.getTimeModel(pm, meal.getTimeId(), UID);
        if(time != null) {
          for(MealModel m : time.getMeals()) {
            if(m.getId() == meal.getId()) {
              list = m.getFoods();
              break;
            }
          }
        }
      }
      //not in time
      else {
        MealModel m = StoreNutrition.getMealModel(pm, meal.getId(), UID);
        list = m.getFoods();
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading foods", e);
      throw new ConnectionException("getFoods", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Returns user all facebook friends that have logged to motiver
   * @return
   * @throws ConnectionException 
   */
  public List<UserModel> getFriends() {

    logger.log(Level.FINE, "getFriends()");
    
    List<UserModel> list = new ArrayList<UserModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }

//    PersistenceManager pm =  null;
//    
//    try {
//      //if coach mode -> get token from trainee
//      if(token.contains("____")) {
//        token = getTraineesToken(token);
//      }
//      
//      //get friends from facebook
//      URL url = new URL("https://graph.facebook.com/me/friends?access_token=" + URLEncoder.encode(token.replaceAll("____.*", "")));
//      BufferedReader reader = null;
//      try {
//        reader = new BufferedReader(new InputStreamReader(url.openStream()));
//      } catch (Exception e1) {
//        log.log(Level.SEVERE, "", e1);
//        throw new ConnectionException("getFriends", "Could not connect to Facebook.com");
//      }
//      String line = reader.readLine();
//      reader.close();
//      if(line != null) {
//        JSONObject json = new JSONObject(line);
//        JSONArray groups = json.getJSONArray("data");
//        
//        List<Long> friendsIds = new ArrayList<Long>();
//        for(int i=0; i < groups.length(); i++) {
//          JSONObject obj = groups.getJSONObject(i);
//          final String uid = obj.getLong("id");
//          
//          if(uid > 0) {
//            friendsIds.add(uid);
//          }
//        }
//        
//        //check if found in our database
//        pm =  PMF.get().getPersistenceManager();
//        Query q = pm.newQuery(UserOpenid.class);
//        q.setFilter("uid == :friendsIds"); 
//        List<UserOpenid> users = (List<UserOpenid>) q.execute(friendsIds);
//
//        //to client side models
//        for(UserOpenid u : users)
//          list.add( UserOpenid.getClientModel(u) );
//      }
//    } catch (Exception e) {
//      log.log(Level.SEVERE, "getFriends", e);
//    }
//    finally {
//      if (!pm.isClosed()) {
//        pm.close();
//      } 
//    }

    return list;

  }

  
  /**
   * Sets single user's permission to view current user data
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  @SuppressWarnings("unchecked")
  public Boolean addUserToCircle(int target, String uid) throws ConnectionException {

    logger.log(Level.FINE, "addUserToCircle()");
    
    boolean ok = false;

    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      //check if permission already found
      Query q = pm.newQuery(Circle.class);
      q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
      q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
      q.setRange(0,1);
      System.out.println(UID+", "+uid+", "+target);
      List<Circle> list = (List<Circle>)q.execute(UID, uid, target);
      
      //if no previous permissions found
      if(list.size() == 0) {
        Circle permissionNew = new Circle(target, UID, uid);
        pm.makePersistent(permissionNew);
        ok = true;
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "addUserToCircle", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("addUserToCircle", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  
  /**
   * Sets single user's permission to view current user data
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  @SuppressWarnings("unchecked")
  public Boolean removeUserFromCircle(int target, String uid) throws ConnectionException {

    logger.log(Level.FINE, "removeUserFromCircle()");
    
    boolean ok = false;

    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      //check if permission already found
      Query q = pm.newQuery(Circle.class);
      q.setFilter("openId == openIdParam && friendId == friendIdParam && target == targetParam");
      q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
      q.setRange(0,1);
      List<Circle> list = (List<Circle>)q.execute(UID, uid, target);
      
      //if no previous permissions found
      if(list.size() != 0) {
        pm.deletePersistent(list.get(0));
        ok = true;
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeUserFromCircle", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeUserFromCircle", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Returns single meal
   * @param mealId
   * @return
   * @throws ConnectionException 
   */
  @Override public MealModel getMeal(Long mealId) throws ConnectionException {

    logger.log(Level.FINE, "getMeal()");
    
    MealModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Meal w = pm.getObjectById(Meal.class, mealId);
      if(w != null) {
        if(hasPermission(1, UID, w.getUid())) {
          m = Meal.getClientModel(w);
          
          //get date from time
          if(m.getTimeId() != 0) {
            Time t = pm.getObjectById(Time.class, m.getTimeId());
            if(t != null) {
              m.setDate(t.getDate());
            }
          }
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeal", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMeal", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Returns meals from time
   * @param time : if 0 -> we return all the meals not in calendar
   * @return meal' models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MealModel> getMeals(int index) throws ConnectionException {

    logger.log(Level.FINE, "getMeals()");

    List<MealModel> list = new ArrayList<MealModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Meal.class);
      q.setFilter("openId == openIdParam && time == null");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(index, index + Constants.LIMIT_MEALS + 1);
      List<Meal> meals = (List<Meal>) q.execute(UID);
            
      //get meals
      if(meals != null) {
        
        Collections.sort(meals);
        
        int i = 0;
        for(Meal w : meals) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_MEALS) {
            list.add(null);
            break;
          }
          
          MealModel m = StoreNutrition.getMealModel(pm, w.getId(), UID);                  
          list.add(m);
          
          i++;
        }
        
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeals", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMeals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns all measurements
   * @return measurements' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MeasurementModel> getMeasurements(int index) throws ConnectionException {

    logger.log(Level.FINE, "getMeasurements()");

    List<MeasurementModel> list = new ArrayList<MeasurementModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Measurement.class);
      q.setFilter("openId == openIdParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setOrdering("name ASC");
      q.setRange(index, index + Constants.LIMIT_MEASUREMENTS + 1);
      List<Measurement> measurements = (List<Measurement>) q.execute(UID);
      
      //get meals
      if(measurements != null) {

        int i = 0;
        for(Measurement w : measurements) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_MEASUREMENTS) {
            list.add(null);
            break;
          }
          
          MeasurementModel m = Measurement.getClientModel(w);
                  
          list.add(m);
          
          i++;
        }
        
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeasurements", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMeasurements", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns last value from given measurement
   * @param measurementId
   * @return model
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  public MeasurementValueModel getMeasurementValue(Long measurementId) throws ConnectionException {

    logger.log(Level.FINE, "getMeasurementValue()");
    
    MeasurementValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Measurement w = pm.getObjectById(Measurement.class, measurementId);
      if(w != null) {
        if(hasPermission(4, UID, w.getUid())) {

          //get last value
          Query q = pm.newQuery(MeasurementValue.class);
          q.setFilter("measurement == measurementParam");
          q.setOrdering("date DESC");
          q.declareParameters("com.delect.motiver.server.Measurement measurementParam");
          List<MeasurementValue> values = (List<MeasurementValue>) q.execute(w);
          if(values.size() > 0) {
            m = MeasurementValue.getClientModel(values.get(0));
            m.setName( Measurement.getClientModel(values.get(0).getMeasurement()) );
          }
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeasurementValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMeasurementValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Returns all values from single measurement
   * @param measurement
   * @return values
   * @throws ConnectionException 
   */
  @Override
  public List<MeasurementValueModel> getMeasurementValues(MeasurementModel measurement, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getMeasurementValues()");

    List<MeasurementValueModel> list = new ArrayList<MeasurementValueModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    //strip time
    final Date dStart = stripTime(dateStart, true);
    final Date dEnd = stripTime(dateEnd, false);
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //get workout
      Measurement w = pm.getObjectById(Measurement.class, measurement.getId());
      
      if(w != null) {
        //if we have permission
        if(hasPermission(4, UID, w.getUid())) {
          List<MeasurementValue> listE = w.getValues();
          if(listE != null) {
            //go through each value
            for(MeasurementValue e : w.getValues()) {
              //check dates
              if(e.getDate().getTime() >= dStart.getTime() && e.getDate().getTime() <= dEnd.getTime()) {
                MeasurementValueModel eNew = MeasurementValue.getClientModel(e);
                list.add(eNew);
              }
            }
          }
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeasurementValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMeasurementValues", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Get micronutrients from single day
   * @param uid
   * @param date
   * @return energies in each days
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MicroNutrientModel> getMicroNutrientsInCalendar(String uid, Date date) throws ConnectionException {

    logger.log(Level.FINE, "getMicroNutrientsInCalendar()");
    
    List<MicroNutrientModel> list = new ArrayList<MicroNutrientModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    //check permission
    if(!hasPermission(1, UID, uid)) {
      return null;
    }
    
    try {
      //strip time
      final Date dStart = stripTime(date, true);
      final Date dEnd = stripTime(date, false);
      
      //get times
      Query q = pm.newQuery(Time.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Time> times = (List<Time>) q.execute(uid, dStart, dEnd);

      //each time
      for(Time t : times) {

        //each meal
        List<MealInTime> meals = t.getMeals();
        if(meals != null) {
          for(MealInTime m : meals) {
            
            try {

              if(m.getFoods() != null) {
                for(FoodInMealTime food : m.getFoods()) {
                  try {
                    //get name
                    if(food.getNameId() != 0) {
                      FoodName name = pm.getObjectById(FoodName.class, food.getNameId());
                      if(name != null) {
                        for(MicroNutrient mn : name.getMicroNutrients()) {
                          //check if already found in array
                          int i=0;
                          double val = -1;
                          for(MicroNutrientModel model : list) {
                            if(model.getNameId() == mn.getNameId()) {
                              val = model.getValue();
                              break;
                            }
                            i++;
                          }
                          //found -> update value
                          if(val != -1) {
                            list.get(i).setValue(val + mn.getValue() * (food.getAmount() / 100));
                          }
                          //not found
                          else {
                            MicroNutrientModel mn2 = MicroNutrient.getClientModel(mn);
                            mn2.setValue(mn.getValue() * (food.getAmount() / 100));
                            list.add(mn2);
                          }
                        }
                      }
                    }
                  } catch (Exception e1) {
                    logger.log(Level.SEVERE, "getMicroNutrientsInCalendar", e1);
                  }
                }
              }
            } catch (Exception e1) {
              logger.log(Level.SEVERE, "getMicroNutrientsInCalendar", e1);
            }
            
          }

        }

        if(t.getFoods() != null) {
          for(FoodInTime food : t.getFoods()) {
            try {
              //get name
              if(food.getNameId() != 0) {
                FoodName name = pm.getObjectById(FoodName.class, food.getNameId());
                if(name != null) {
                  for(MicroNutrient mn : name.getMicroNutrients()) {
                    //check if already found in array
                    int i=0;
                    double val = -1;
                    for(MicroNutrientModel model : list) {
                      if(model.getNameId() == mn.getNameId()) {
                        val = model.getValue();
                        break;
                      }
                      i++;
                    }
                    //found -> update value
                    if(val != -1) {
                      list.get(i).setValue(val + mn.getValue() * (food.getAmount() / 100));
                    }
                    //not found
                    else {
                      MicroNutrientModel mn2 = MicroNutrient.getClientModel(mn);
                      mn2.setValue(mn.getValue() * (food.getAmount() / 100));
                      list.add(mn2);
                    }
                  }
                }
              }
            } catch (Exception e1) {
              logger.log(Level.SEVERE, "getMicroNutrientsInCalendar", e1);
              //TODO antaa v�lill� virheilmoitusta???
            }
          }
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMicroNutrientsInCalendar", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMicroNutrientsInCalendar", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return list;
  }
  
  /**
   * Returns saved monthly summaries
   * @return
   * @throws ConnectionException
   */
  public MonthlySummaryModel getMonthlySummary(Long id) throws ConnectionException {

    logger.log(Level.FINE, "getMonthlySummary()");
    
    MonthlySummaryModel model = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //get summaries from given month
      MonthlySummary modelServer = pm.getObjectById(MonthlySummary.class, id);
      //if found and our summary
      if(modelServer != null && modelServer.getUid().equals(UID)) {
        model = MonthlySummary.getClientModel(modelServer);
        
        //exercises
        List<MonthlySummaryExerciseModel> listExercises = new ArrayList<MonthlySummaryExerciseModel>();
        for(MonthlySummaryExercise exercise : modelServer.getExercises()) {
          MonthlySummaryExerciseModel exerciseClient = MonthlySummaryExercise.getClientModel(exercise);
          
          //get exercise name
          ExerciseName exerciseName = pm.getObjectById(ExerciseName.class, exercise.getNameId());
          if(exerciseName != null) {
            exerciseClient.setExerciseName(ExerciseName.getClientModel(exerciseName));
          }
          
          listExercises.add(exerciseClient);
        }

        model.setExercises(listExercises);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMonthlySummary", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMonthlySummary", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return model;
    
  }
  
  /**
   * Returns saved monthly summaries
   * @return
   * @throws ConnectionException
   */
  @SuppressWarnings("unchecked")
  public List<MonthlySummaryModel> getMonthlySummaries() throws ConnectionException {

    logger.log(Level.FINE, "getMonthlySummaries()");
    
    List<MonthlySummaryModel> list = new ArrayList<MonthlySummaryModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //get summaries from given month
      Query q = pm.newQuery(MonthlySummary.class);
      q.setFilter("openId == openIdParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setOrdering("date ASC");
      List<MonthlySummary> summaries = (List<MonthlySummary>)q.execute(UID);
      
      //convert to client side models
      for(MonthlySummary summary : summaries) {
        MonthlySummaryModel modelClient = MonthlySummary.getClientModel(summary);
        
        //exercises
//        List<MonthlySummaryExerciseModel> listExercises = new ArrayList<MonthlySummaryExerciseModel>();
//        for(MonthlySummaryExercise exercise : summary.getExercises()) {
//          MonthlySummaryExerciseModel exerciseClient = MonthlySummaryExercise.getClientModel(exercise);
//          
//          //get exercise name
//          ExerciseName exerciseName = pm.getObjectById(ExerciseName.class, exercise.getNameId());
//          if(exerciseName != null) {
//            exerciseClient.setExerciseName(ExerciseName.getClientModel(exerciseName));
//          }
//          listExercises.add(exerciseClient);
//        }
        
        list.add(modelClient);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMonthlySummaries", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMonthlySummaries", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Returns most popular meals
   * @return meals' models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MealModel> getMostPopularMeals(int index) throws ConnectionException {

    logger.log(Level.FINE, "getMostPopularMeals()");

    List<MealModel> list = new ArrayList<MealModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query q = pm.newQuery(Meal.class);

      List<Meal> meals = null;
      
      //copy count > 1 and not our meal
      q.setFilter("time == null && copyCount > 0");
      q.setRange(index, index + Constants.LIMIT_MEALS + 1);
      q.setOrdering("copyCount DESC");
      meals = (List<Meal>) q.execute();

      int i = 0;
      for(Meal w : meals) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_WORKOUTS) {
          list.add(null);
          break;
        }
        
        //check permission
        boolean hasPermission = hasPermission(1, UID, w.getUid());
        
        if(hasPermission) {
          MealModel m = Meal.getClientModel(w);
          list.add(m);
          
          i++;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMostPopularMeals", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMostPopularMeals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns most popular
   * @return routines' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<RoutineModel> getMostPopularRoutines(int index) throws ConnectionException {

    logger.log(Level.FINE, "getMostPopularRoutines()");

    //convert to client side models
    List<RoutineModel> list = new ArrayList<RoutineModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Routine.class);
      q.setFilter("date == null && copyCount > 0");
      q.setOrdering("copyCount DESC");
      q.setRange(index, index + Constants.LIMIT_ROUTINES + 1);
      List<Routine> routines = (List<Routine>) q.execute();

      int i = 0;
      for(Routine r : routines) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_ROUTINES) {
          list.add(null);
          break;
        }
        
        //check permission
        boolean hasPermission = hasPermission(0, UID, r.getUid());
        
        if(hasPermission) {
          RoutineModel m = Routine.getClientModel(r);
          list.add(m);
          
          i++;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMostPopularRoutines", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMostPopularRoutines", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns most popular workouts
   * @return workouts' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<WorkoutModel> getMostPopularWorkouts(int index) throws ConnectionException {

    logger.log(Level.FINE, "getMostPopularWorkouts()");

    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query q = pm.newQuery(Workout.class);

      List<Workout> workouts = null;
      
      q.setFilter("date == null && routineId == 0 && copyCount > 0");
      q.setRange(index, index + Constants.LIMIT_WORKOUTS + 1);
      q.setOrdering("copyCount DESC");
      workouts = (List<Workout>) q.execute();

      int i = 0;
      for(Workout w : workouts) {
        
        try {
          //if limit reached -> add null value
          if(i == Constants.LIMIT_WORKOUTS) {
            list.add(null);
            break;
          }
          
          WorkoutModel m = StoreTraining.getWorkoutModel(pm, w.getId(), UID);
          list.add(m);
          
          i++;
        } catch (NoPermissionException e) {
          //no permission skipping this one
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMostPopularWorkouts", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getMostPopularWorkouts", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns single routine
   * @param routineId
   * @return
   * @throws ConnectionException 
   */
  @Override public RoutineModel getRoutine(Long routineId) throws ConnectionException {

    logger.log(Level.FINE, "getRoutine()");
    
    RoutineModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Routine w = pm.getObjectById(Routine.class, routineId);
      if(w != null) {
        if(hasPermission(0, UID, w.getUid())) {
          m = Routine.getClientModel(w);
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRoutine", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getRoutine", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Returns all routines that aren't in calendar
   * @return routines' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<RoutineModel> getRoutines(int index) throws ConnectionException {

    logger.log(Level.FINE, "getRoutines()");

    //convert to client side models
    List<RoutineModel> list = new ArrayList<RoutineModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Routine.class);
      q.setFilter("openId == openIdParam && date == null");
      q.declareParameters("java.lang.String openIdParam");
      q.setRange(index, 100);
      List<Routine> routines = (List<Routine>) q.execute(UID);
      
      Collections.sort(routines);

      int i = 0;
      for(Routine r : routines) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_ROUTINES) {
          list.add(null);
          break;
        }
        
        RoutineModel m = Routine.getClientModel(r);
        list.add(m);
        
        i++;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRoutines", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getRoutines", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns all runs
   * @return runs' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<RunModel> getRuns(int index) throws ConnectionException {

    logger.log(Level.FINE, "getRuns()");

    List<RunModel> list = new ArrayList<RunModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    try {
      
      Query q = pm.newQuery(Run.class);

      q.setFilter("openId == openIdParam");
      q.declareParameters("java.lang.String openIdParam");
      q.setOrdering("name ASC");
      q.setRange(index, index + Constants.LIMIT_RUNS + 1);
      List<Run> runs = (List<Run>) q.execute(UID);
      
      //get meals
      if(runs != null) {

        int i = 0;
        for(Run w : runs) {
          
          //if limit reached -> add null value
          if(i == Constants.LIMIT_RUNS) {
            list.add(null);
            break;
          }
          
          RunModel m = Run.getClientModel(w);
                  
          list.add(m);
          
          i++;
        }
        
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRuns", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getRuns", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Returns last value from given run
   * @param runId
   * @return model
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  public RunValueModel getRunValue(Long runId) throws ConnectionException {

    logger.log(Level.FINE, "getRunValue()");
    
    RunValueModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Run w = pm.getObjectById(Run.class, runId);
      if(w != null) {
        if(hasPermission(3, UID, w.getUid())) {

          //get last value
          Query q = pm.newQuery(RunValue.class);
          q.setFilter("run == runParam");
          q.setOrdering("date DESC");
          q.declareParameters("com.delect.motiver.server.Run runParam");
          List<RunValue> values = (List<RunValue>) q.execute(w);
          if(values.size() > 0) {
            m = RunValue.getClientModel(values.get(0));
            m.setName( Run.getClientModel(values.get(0).getRun()) );
          }
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRunValue", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getRunValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Returns all foods from single meal
   * @param run
   * @return foods
   * @throws ConnectionException 
   */
  @Override
  public List<RunValueModel> getRunValues(RunModel run, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getRunValues()");
    
    List<RunValueModel> list = new ArrayList<RunValueModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    //strip time
    final Date dStart = stripTime(dateStart, true);
    final Date dEnd = stripTime(dateEnd, false);
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //get workout
      Run w = pm.getObjectById(Run.class, run.getId());
      
      if(w != null) {
        //if our run OR shared
        boolean hasPermission = true;
        if(!w.getUid().equals(UID)) {
          hasPermission = hasPermission(3, UID, w.getUid());
        }
        
        if(hasPermission) {
          List<RunValue> listE = w.getValues();
          if(listE != null) {
            //go through each value
            for(RunValue e : w.getValues()) {
              //check dates
              if(e.getDate().getTime() >= dStart.getTime() && e.getDate().getTime() <= dEnd.getTime()) {
                RunValueModel eNew = RunValue.getClientModel(e);
                list.add(eNew);
              }
            }
          }
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRunValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getRunValues", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Gets top ten exercises
   * @return exercises array, each model have "count" value which has total count
   * 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<ExerciseNameModel> getStatisticsTopExercises(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTopExercises()");

    List<ExerciseNameModel> list = new ArrayList<ExerciseNameModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      Query q = pm.newQuery(Workout.class);

      //strip dates
      final Date dStart = stripTime(dateStart, true);
      final Date dEnd = stripTime(dateEnd, false);
      
      q.setFilter("date != null && openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Workout> workouts = (List<Workout>) q.execute(UID, dStart, dEnd);
      
      List<MyListItem> listIds = new ArrayList<MyListItem>();
      
      //go through each workouts' exercises and calculate count for each nameId
      //TODO huge server load??
      for(Workout w : workouts) {
        try {
          for(Exercise e : w.getExercises()) {
            try {
              if(e.getNameId() != null) {
                if(e.getNameId().longValue() != 0) {
                  
                  int pos = -1;
                  for(int i=0; i < listIds.size(); i++) {
                    if(listIds.get(i).id == e.getNameId().longValue()) {
                      pos = i;
                      break;
                    }
                  }

                  //if found in array
                  if(pos >= 0) {
                    final MyListItem item = listIds.get(pos);
                    //update count
                    item.count++;
                    listIds.set(pos, item);
                  }
                  //add id to array
                  else {
                    listIds.add(new MyListItem(e.getNameId().longValue()));
                  }
                    
                }
              }
            } catch (Exception e1) {
              logger.log(Level.SEVERE, "getStatisticsTopExercises", e1);
            }
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, "getStatisticsTopExercises", e);
        }

      }
      
      //sort
      Collections.sort(listIds);
      
      //return top top
      int top = (listIds.size() >= 10)? 10 : listIds.size();
      for(int i=0; i < top; i++) {
        //get name
        ExerciseNameModel m = ExerciseName.getClientModel(pm.getObjectById(ExerciseName.class, listIds.get(i).id));
        m.set("count", listIds.get(i).count);
        list.add(m);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getStatisticsTopExercises", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getStatisticsTopExercises", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }

  /**
   * Gets top ten exercises
   * @return exercises array, each model have "count" value which has total count
   * 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MealModel> getStatisticsTopMeals(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTopMeals()");

    List<MealModel> list = new ArrayList<MealModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      //strip dates
      final Date dStart = stripTime(dateStart, true);
      final Date dEnd = stripTime(dateEnd, false);

      Query q = pm.newQuery(Time.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Time> times = (List<Time>) q.execute(UID, dStart, dEnd);
      
      List<MyListItem> listIds = new ArrayList<MyListItem>();
      
      //go through each workouts' exercises and calculate count for each nameId
      //TODO huge server load??
      for(Time t : times) {
        try {
          //get meals
          List<MealInTime> meals = t.getMeals();
          
          for(MealInTime m : meals) {
            try {
              int pos = -1;
              for(int i=0; i < listIds.size(); i++) {
                if(listIds.get(i).name.equals( m.getName() )) {
                  pos = i;
                  break;
                }
              }

              //if found in array
              if(pos >= 0) {
                final MyListItem item = listIds.get(pos);
                //update count
                item.count++;
                listIds.set(pos, item);
              }
              //add id to array
              else {
                MyListItem item = new MyListItem(m.getId().longValue());
                item.name = m.getName();
                item.meal = MealInTime.getClientModel(m);
                listIds.add(item);
              }
            } catch (Exception e1) {
              logger.log(Level.SEVERE, "getStatisticsTopMeals", e1);
            }
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, "getStatisticsTopMeals", e);
        }
      }
      
      //sort
      Collections.sort(listIds);
      
      //return top top
      int top = (listIds.size() >= 10)? 10 : listIds.size();
      for(int i=0; i < top; i++) {
        //get name
        MealModel m = listIds.get(i).meal;
        m.set("count", listIds.get(i).count);
        list.add(m);
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getStatisticsTopMeals", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getStatisticsTopMeals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Gets statistics for each day
   * @return array : training count for each day (monday-friday)
   */
  @SuppressWarnings("unchecked")
  @Override
  public int[] getStatisticsTrainingDays(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTrainingDays()");
    
    int[] count = new int[] {0, 0, 0, 0, 0, 0, 0};
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return count;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(Workout.class);

      //strip dates
      final Date dStart = stripTime(dateStart, true);
      final Date dEnd = stripTime(dateEnd, false);
      
      q.setFilter("date != null && openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Workout> workouts = (List<Workout>) q.execute(UID, dStart, dEnd);
      
      Calendar cal = Calendar.getInstance();
      
      //calculate count for each time
      for(Workout w : workouts) {
        try {
          Date d = w.getDate();
          
          if(d != null) {
            cal.setTimeInMillis(d.getTime());
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 5=Thursday
            
            //make sure monday is first
            dayOfWeek--;
            if(dayOfWeek < 1) {
              dayOfWeek = 7;
            }

            count[dayOfWeek - 1]++;
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, "getStatisticsTrainingDays", e);
        }

      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getStatisticsTrainingDays", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getStatisticsTrainingDays", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return count;
  }

  /**
   * Gets statistics for each day
   * @return array : training count for each time (00-03, 03-06, 06-09, 09-12, 12-15, 15-18, 18-21, 21-24)
   * 
   */
  @SuppressWarnings("unchecked")
  @Override
  public int[] getStatisticsTrainingTimes(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTrainingTimes()");
    
    int[] count = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return count;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //strip dates
      final Date dStart = stripTime(dateStart, true);
      final Date dEnd = stripTime(dateEnd, false);

      Query q = pm.newQuery(Workout.class);
      q.setFilter("date != null && openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Workout> workouts = (List<Workout>) q.execute(UID, dStart, dEnd);
      
      //calculate count for each time
      for(Workout w : workouts) {
        try {
          long t1 = (w.getTimeStart() != null)? w.getTimeStart() : -1;
          
          //check which time
          if(t1 > 0) {
            //started: 23:30...02:30
            if(t1 >= 84600 || t1 < 9000) {
              count[0]++;
            }
            //started: 02:30...05:30
            else if(t1 >= 9000 && t1 < 19800) {
              count[1]++;
            }
            //started: 05:30...08:30
            else if(t1 >= 19800 && t1 < 30600) {
              count[2]++;
            }
            //started: 08:30...11:30
            else if(t1 >= 30600 && t1 < 41400) {
              count[3]++;
            }
            //started: 11:30...14:30
            else if(t1 >= 41400 && t1 < 52200) {
              count[4]++;
            }
            //started: 14:30...17:30
            else if(t1 >= 52200 && t1 < 63000) {
              count[5]++;
            }
            //started: 17:30...20:30
            else if(t1 >= 63000 && t1 < 73800) {
              count[6]++;
            }
            //started: 20:30...23:30
            else if(t1 >= 73800 && t1 < 84600) {
              count[7]++;
            }
          }
        } catch (Exception e) {
          logger.log(Level.SEVERE, "getStatisticsTrainingTimes", e);
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getStatisticsTrainingTimes", e);
      throw new ConnectionException("getStatisticsTrainingTimes", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return count;
    
  }

  /**
   * Get times in calendar in given day
   * @param uid : who's nutrition
   * @param date
   * @return time models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<TimeModel> getTimesInCalendar(String uid, Date date) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading times for "+date);
    }

    List<TimeModel> list = new ArrayList<TimeModel>();

    if(date == null) {
      return null;
    }
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //strip time
      final Date dStart = stripTime(date, true);
      final Date dEnd = stripTime(date, false);
        
      Query q = pm.newQuery(Time.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<Time> times = (List<Time>) q.execute(uid, dStart, dEnd);
      
      //convert to client side models
      for(Time time : times) {
        TimeModel model = StoreNutrition.getTimeModel(pm, time.getId(), UID);
        if(model != null) {
          list.add(model);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading time for date: "+date, e);
      throw new ConnectionException("getTimesInCalendar", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return list;
  }

  /**
   * Returns user all facebook friends that have set user as coach
   * @return
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  public List<UserModel> getTrainees() {

    logger.log(Level.FINE, "getTrainees()");

    List<UserModel> list = new ArrayList<UserModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();    
    
    try {
      Query q = pm.newQuery(Circle.class);
      q.setFilter("friendId == friendIdParam && target == targetParam");
      q.declareParameters("java.lang.String friendIdParam, java.lang.Integer targetParam");
      List<Circle> users = (List<Circle>) q.execute(UID, Permission.COACH);

      if(users.size() > 0) {
        UserModel u = StoreUser.getUserModel(pm, users.get(0).getUid());
        if(u != null) {
          list.add(u);
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error loading trainees", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return list;

  }

  /**
   * Removes cardio
   * @param model to remove
   * @return remove successful
   */
  @Override
  public Boolean removeCardio(CardioModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeCardio()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Cardio m = pm.getObjectById(Cardio.class, model.getId());
      if(m != null) {
        //check if correct user
        if(m.getUid().equals(UID)) {
          pm.deletePersistent(m);
          
          ok = true;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeCardio", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeCardio", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Removes cardio values
   * @param values to remove
   * @return remove successful
   */
  @Override public Boolean removeCardioValues(CardioModel model, List<CardioValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "removeCardioValues()");

    if(values.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Cardio w = pm.getObjectById(Cardio.class, model.getId());
      
      if(w != null) {
        if(w.getUid().equals(UID)) {
          
          //delete exercise
          for(CardioValueModel m : values) {
  
            //remove from list
            for(CardioValue mFromList : w.getValues()) {
              if(mFromList.getId().longValue() == m.getId().longValue()) {
                w.getValues().remove(mFromList);
                break;
              }
            }
          }
          ok = true;
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeCardioValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeCardioValues", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Delete comments
   * @return delete successfull
   */
  @Override
  public boolean removeComments(List<CommentModel> comments) throws ConnectionException {

    logger.log(Level.FINE, "removeComments()");

    if(comments.size() < 1) {
      return false;
    }
    
    boolean ok = true;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //delete exercise
      for(CommentModel m : comments) {

        Comment comment = pm.getObjectById(Comment.class, m.getId());
        
        //check if our comment
        if(comment.getUid().equals(UID)) {
          pm.deletePersistent(comment);
        }
        else {
          ok = false;
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeComments", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeComments", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Delete exercises
   * @return delete successfull
   */
  @Override
  public boolean removeExercises(List<ExerciseModel> exercises) throws ConnectionException {

    if(exercises.size() < 1) {
      return false;
    }

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing exercises. Count: "+exercises.size());
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      //TODO needs improving
      for(ExerciseModel e : exercises) {
        StoreTraining.removeExerciseModel(pm, e, UID);
      }
      
      ok = true;
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing exercise", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeExercises", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Delete foods
   * @return delete successfull
   */
  @Override
  public boolean removeFoods(List<FoodModel> foods) throws ConnectionException {

    logger.log(Level.FINE, "removeFoods()");

    if(foods.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //delete exercise
      for(FoodModel m : foods) {

        //if food is in meal (which is in time)
        if(m.getTimeId() != 0 && m.getMealId() != 0) {
          //get time
          Time time = pm.getObjectById(Time.class, m.getTimeId());
          if(time != null && hasPermission(1, UID, time.getUid())) {
            //get meal
            for(MealInTime meal : time.getMeals()) {
              if(meal.getId() == m.getMealId()) {
                for(FoodInMealTime f : meal.getFoods()) {
                  if(m.getId() == f.getId()) {
                    pm.deletePersistent(f);
                    ok = true;
                    break;
                  }
                }
                break;
              }
            }
          }
        }
        //if food is in time
        else if(m.getTimeId() != 0) {
          Time time = pm.getObjectById(Time.class, m.getTimeId());
          //meal found and we have permission
          if(time != null && hasPermission(1, UID, time.getUid())) {
            for(FoodInTime f : time.getFoods()) {
              if(f.getId() == m.getId()) {
                pm.deletePersistent(f);
                ok = true;
                break;
              }
            }
          }
        }
        //if food is in meal
        else if(m.getMealId() != 0) {
          Meal time = pm.getObjectById(Meal.class, m.getMealId());
          //meal found and we have permission
          if(time != null && hasPermission(1, UID, time.getUid())) {
            for(FoodInMeal f : time.getFoods()) {
              if(f.getId() == m.getId()) {
                pm.deletePersistent(f);
                ok = true;
                break;
              }
            }
          }
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeFoods", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeFoods", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Deletes list of guide values
   * @param list
   * @return
   */
  @Override public Boolean removeGuideValues(List<GuideValueModel> list) throws ConnectionException {

    logger.log(Level.FINE, "removeGuideValues()");

    if(list.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //delete values
      List<GuideValue> listServer = new ArrayList<GuideValue>();
      for(GuideValueModel m : list) {
        try {
          GuideValue mServer = pm.getObjectById(GuideValue.class, m.getId());
          if(mServer != null) {
            listServer.add( mServer );
          }
          
        } catch (Exception e) {
          logger.log(Level.SEVERE, "removeGuideValues", e);
        }
      }
      
      pm.deletePersistentAll(listServer);
      
      ok = true;
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeGuideValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeGuideValues", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Removes meal
   * @param model to remove
   * @return removed successfull
   */
  @Override
  public Boolean removeMeal(MealModel model) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing meal "+model.getId());
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return false;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      ok = StoreNutrition.removeMealModel(pm, model, UID);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing meal", e);
      throw new ConnectionException("removeMeal", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Removes measurement
   * @param model to remove
   * @return remove successful
   */
  @Override
  public Boolean removeMeasurement(MeasurementModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeMeasurement()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Measurement m = pm.getObjectById(Measurement.class, model.getId());
      if(m != null) {
        //check if correct user
        if(m.getUid().equals(UID)) {
          pm.deletePersistent(m);
          
          ok = true;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeMeasurement", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeMeasurement", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Removes measurement values
   * @param values to remove
   * @return remove successful
   */
  @Override public Boolean removeMeasurementValues(MeasurementModel model, List<MeasurementValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "removeMeasurementValues()");

    if(values.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Measurement w = pm.getObjectById(Measurement.class, model.getId());
      
      if(w != null) {
        if(w.getUid().equals(UID)) {
          
          //delete exercise
          for(MeasurementValueModel m : values) {
  
            //remove from list
            for(MeasurementValue mFromList : w.getValues()) {
              if(mFromList.getId().longValue() == m.getId().longValue()) {
                w.getValues().remove(mFromList);
                break;
              }
            }
          }
          ok = true;
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeMeasurementValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeMeasurementValues", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Removes routine
   * @param model to remove
   * @return remove successful
   */
  @SuppressWarnings("unchecked")
  @Override
  public Boolean removeRoutine(RoutineModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeRoutine()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Routine r = pm.getObjectById(Routine.class, model.getId());
      if(r != null) {
        //check if correct user
        if(r.getUid().equals(UID)) {
          pm.deletePersistent(r);
          
          //remove also workouts which belongs to this routine
          Query q = pm.newQuery(Workout.class);
          q.setFilter("openId == openIdParam && routineId == routineIdParam");
          q.declareParameters("java.lang.String openIdParam, java.lang.Long routineIdParam");
          List<Workout> workouts = (List<Workout>) q.execute(UID, r.getId());
          for(Workout mWorkout : workouts) {
            StoreTraining.removeWorkoutModel(pm, mWorkout.getId(), UID);
          }
            
          
          ok = true;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeRoutine", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeRoutine", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Removes run
   * @param model to remove
   * @return remove successful
   */
  @Override
  public Boolean removeRun(RunModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeRun()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Run m = pm.getObjectById(Run.class, model.getId());
      if(m != null) {
        //check if correct user
        if(m.getUid().equals(UID)) {
          pm.deletePersistent(m);
          
          ok = true;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeRun", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeRun", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Removes run values
   * @param values to remove
   * @return remove successful
   */
  @Override public Boolean removeRunValues(RunModel model, List<RunValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "removeRunValues()");

    if(values.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Run w = pm.getObjectById(Run.class, model.getId());
      
      if(w != null) {
        if(w.getUid().equals(UID)) {
          
          //delete exercise
          for(RunValueModel m : values) {
  
            //remove from list
            for(RunValue mFromList : w.getValues()) {
              if(mFromList.getId().longValue() == m.getId().longValue()) {
                w.getValues().remove(mFromList);
                break;
              }
            }
          }
          ok = true;
        }
      }
      
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeRunValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeRunValues", e.getMessage());
    }
    finally { 
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Removes time (and meals/foods it contains)
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException 
   */
  @Override public Boolean removeTimes(TimeModel[] models) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Removing times. Count: "+models.length);
    }
    
    boolean ok = true;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return false;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //each time
      for(TimeModel model : models) {
        
        boolean removeOk = StoreNutrition.removeTimeModel(pm, model.getId(), UID);
        
        if(!removeOk) {
          ok = false;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing times", e);
      throw new ConnectionException("removeTime", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Removes workout
   * @param model to remove
   * @return removed successfull
   */
  @Override
  public Boolean removeWorkout(WorkoutModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeWorkout()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      ok = StoreTraining.removeWorkoutModel(pm, model.getId(), UID);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error removing workout", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("removeWorkout", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Saves access_token which will be used for confirming the current user
   * @return
   * @throws ConnectionException 
   */
  @Override
  public UserModel saveToken() throws ConnectionException {

    logger.log(Level.FINE, "saveToken()");
    
      UserModel userdata = null;

//      //get UID
//      InputStream reader = null;
//    try {
//      
//      URL url = new URL("https://graph.facebook.com/me?access_token=" + URLEncoder.encode(token) + "&random=" + System.currentTimeMillis());
//      URLConnection urlConnection = url.openConnection();
//      urlConnection.setReadTimeout(10000);
//      urlConnection.setUseCaches(false);
//      urlConnection.setConnectTimeout(10000);
//      reader = new BufferedInputStream(urlConnection.getInputStream());
//    } catch (Exception e1) {
//      log.log(Level.SEVERE, "", e1);
//      throw new ConnectionException("saveToken", "Could not connect to Facebook.com");
//    }
//    PersistenceManager pm =  PMF.get().getPersistenceManager();
//    String line = "";
//      try {
//      line = this.convertStreamToString(reader);
//      reader.close();
//      if(line != null) {
//        JSONObject obj = new JSONObject(line);
//        String uid = obj.getLong("id");
//        String firstName = obj.getString("first_name");
//        String lastName = obj.getString("last_name");
//        String gender = obj.getString("gender");
//        int timezone = obj.getInt("timezone");
//        String locale = obj.getString("locale");
//        
//        //check if user has data in OUR DATABASE
//        Query q = pm.newQuery(UserOpenid.class, "openId == openIdParam");
//        q.declareParameters("java.lang.String openIdParam");
//        List<UserOpenid> users = (List<UserOpenid>) q.execute(uid);
//
//        UserOpenid u = null;
//        
//        //data found
//        if(users.size() > 0) {
//          u = users.get(0);
//        }
//        //no data -> add new data for this user
//        else {
//          u = new UserOpenid("");
//        }
//        
//        //if user added
//        if(u != null) {
//          //save facebook data
//          u.setFirstName(firstName);
//          u.setLastName(lastName);
//          u.setGender(gender);
//          u.setTimezone(timezone);
//          u.setLocale(locale);
//          u.setFbAuthToken(token);
//          u.setBanned(false);
//
//          pm.makePersistent(u);
//          pm.flush();
//          userdata = UserOpenid.getClientModel(u);
//                  
//          //check if someone has set user as coach
//          q = pm.newQuery(UserOpenid.class, "shareCoach == shareCoachParam");
//          q.setRange(0, 1);
//          q.declareParameters("java.lang.Long shareCoachParam");
//          users = (List<UserOpenid>) q.execute(uid);
//          userdata.setCoach(users.size() > 0);
//  
//        }
//      }
//      
//    } catch (Exception e) {
//      log.severe("saveToken: token: " + token + ", line: " + line);
//    }
//    finally {
//      if (!pm.isClosed()) {
//        pm.close();
//      } 
//    }
    
    //if user is banned -> return null
    return (userdata != null && userdata.isBanned())? null : userdata;
  }
  
  /**
   * Saves user data
   * @param user
   * @return save successfull
   * @throws ConnectionException 
   */
  public UserModel saveUserData(UserModel u) throws ConnectionException {

    logger.log(Level.FINE, "saveUserData()");
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return u;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      u = StoreUser.saveUserModel(pm, u);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error saving user", e);
      throw new ConnectionException("saveUserData", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return u;
  }
  
  /**
   * Returns all exercise names
   * @return names' models
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<ExerciseNameModel> searchExerciseNames(String query, int limit) throws ConnectionException {

    logger.log(Level.FINE, "Searching exercises: "+query);
    
    //convert to client side models
    List<ExerciseNameModel> list = new ArrayList<ExerciseNameModel>();
    
    //get uid
    //get uid and locale
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //split query string
      query = query.toLowerCase();
      String[] arr = query.split(" ");
      
      //if some equipment
//      int equipment = -1;
//      try {
//        String s3 = query.replaceAll(".*--([0-9])--.*", "$1");
//        equipment = Integer.parseInt(s3);
//        
//        //remove index from query
//        query = query.replaceAll("\\(--[0-9]--\\)", "");
//        query = query.replaceAll("--[0-9]--\\)", "");
//        query = query.replaceAll("\\(--[0-9]--", "");
//        query = query.replaceAll("--[0-9]--", "");
//        query = query.trim();
//      } catch (Exception e) {
//        log.log(Level.SEVERE, "searchExerciseNames", e);
//      }
      
      //TODO missing equipment search and locale
      List<ExerciseName> names = StoreTraining.getExerciseNames(pm);

      List<ExerciseName> arrNames = new ArrayList<ExerciseName>();

      arr = query.split(" ");
      
      for(int i=0; i < names.size(); i++) {
        ExerciseName n = names.get(i);
        
        final String name = n.getName();
        
        //filter by query (add count variable)
        int count = 0;
        for(String s : arr) {
          //if word long enough
          if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD) {
            //exact match
            if(name.toLowerCase().equals( s )) {
              count += 2;
            }
            //partial match
            else if(name.toLowerCase().contains( s )) {
              count++;
            }
          }
        }
        
        //if found
        if(count > 0) {
          
          //get count from use table
          int countUse = 0;
          try {
            Query qUse = pm.newQuery(ExerciseNameCount.class);
            qUse.setFilter("nameId == nameIdParam && openId == openIdParam");
            qUse.declareParameters("java.lang.Long nameIdParam, java.lang.String openIdParam");
            qUse.setRange(0, 1);
            List<ExerciseNameCount> valueCount = (List<ExerciseNameCount>) qUse.execute(n.getId(), UID);
            if(valueCount.size() > 0) {
              countUse = valueCount.get(0).getCount();
            }
          } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "searchExerciseNames", e);
          }
          
          n.setCount(count, countUse);
          arrNames.add(n);
        }
      }
      
      //sort array based on count
      Collections.sort(arrNames);
      
      //convert to client model
      for(int i=0; i < arrNames.size() && i < limit; i++) {
        ExerciseName n = arrNames.get(i);
        if(n.getCountQuery() > 0) {
          list.add(ExerciseName.getClientModel(n));
        }
        else {
          break;
        }
        //limit query (only if not "admin search" (==no query word)
        if(list.size() >= limit) {
          break;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchExerciseNames", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("searchExerciseNames", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    
    return list;
  }
  
  /**
   * Search food names
   * @return names' models
   */
  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  public List<FoodNameModel> searchFoodNames(String query, int limit) throws ConnectionException {

    logger.log(Level.FINE, "searchFoodNames()");
    
    //convert to client side models
    List<FoodNameModel> list = new ArrayList<FoodNameModel>();
    
    //get uid and locale
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return list;
    }
      
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //split query string
      //strip special characters
      query = query.replace("(", "");
      query = query.replace(")", "");
      query = query.replace(",", "");
      query = query.toLowerCase();
      String[] arr = query.split(" ");
      
      //TODO missing equipment search and locale
      List<FoodName> names = StoreNutrition.getFoodNames(pm);

      List<FoodName> arrNames = new ArrayList<FoodName>();
        
      for(int i=0; i < names.size(); i++) {
        FoodName n = names.get(i);

        String name = n.getName();
        //strip special characters
        name = name.replace("(", "");
        name = name.replace(")", "");
        name = name.replace(",", "");
        
        //filter by query (add count variable)
        int count = 0;
        for(String s : arr) {
          //if word long enough
          if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD) {
            //exact match
            if(name.toLowerCase().equals( s )) {
              count += 3;
            }
            //partial match
            else if(name.toLowerCase().contains( s )) {
              count++;
            }
          }
        }
        //if motiver's food -> add count
        if(count > 0) {
          if(n.getTrusted() == 100) {
            count += 2;
          }
          //if verified
          else if(n.getTrusted() == 1) {
            count++;
          }
        }
        
        //if found
        if(count > 0) {
          n.setCount(count);
          arrNames.add(n);
        }
      }
      
      //sort array based on count
      Collections.sort(arrNames);
      
      //convert to client model
      for(int i=0; i < arrNames.size() && i < limit; i++) {
        FoodName n = arrNames.get(i);
        
        if(n.getCount() > 0) {
          FoodNameModel nameClient = FoodName.getClientModel(n);
          //if admin -> return also micronutrients
          if(limit > 100) {
            List<MicroNutrientModel> listMN = new ArrayList<MicroNutrientModel>();
            for(MicroNutrient mn : n.getMicroNutrients())
              listMN.add(MicroNutrient.getClientModel(mn));
            nameClient.setMicronutrients(listMN);
          }
          //add to list
          list.add(nameClient);
        }
        else {
          break;
        }
        //limit query
        if(list.size() >= limit) {
          break;
        }
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchFoodNames", e);
      //TODO virhe jos ei ruokia??
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("searchFoodNames", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    
    return list;
    
  }

  /**
   * Search meals from other users
   * @return meals' models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<MealModel> searchMeals(int index, String query) throws ConnectionException {

    logger.log(Level.FINE, "searchMeals()");
    
    List<MealModel> list = new ArrayList<MealModel>();
    
    if(query == null) {
      return list;
    }
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Meal.class);
      q.setFilter("time == null");
      q.setOrdering("name ASC");
      q.setRange(index, index + Constants.LIMIT_MEALS + 1);
      List<Meal> meals = (List<Meal>) q.execute();
      
      //split query string
      String[] arr = query.split(" ");

      int i = 0;
      for(Meal w : meals) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_MEALS) {
          list.add(null);
          break;
        }

        final String name = w.getName();
        
        //filter by query
        boolean ok = false;
        for(String s : arr) {
          ok = name.toLowerCase().contains( s.toLowerCase() );
          if(ok) {
            break;
          }
        }

        //if name matched -> check permission
        if(ok) {
          
          boolean hasPermission = hasPermission(1, UID, w.getUid());
          
          if(hasPermission) {
            MealModel m = Meal.getClientModel(w);
            list.add(m);
            
            i++;
          }
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchMeals", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("searchMeals", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    
    return list;
  }
  
  /**
   * Search routines from other users
   * @return routines' models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<RoutineModel> searchRoutines(int index, String query) throws ConnectionException {

    logger.log(Level.FINE, "searchRoutines()");
    
    List<RoutineModel> list = new ArrayList<RoutineModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query q = pm.newQuery(Routine.class);
      q.setFilter("date == null");
      q.setOrdering("name ASC");
      q.setRange(index, index + Constants.LIMIT_ROUTINES + 1);
      List<Routine> routines = (List<Routine>) q.execute();
      
      //split query string
      String[] arr = query.split(" ");

      int i = 0;
      for(Routine r : routines) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_ROUTINES) {
          list.add(null);
          break;
        }

        final String name = r.getName();
        
        //filter by query
        boolean ok = false;
        for(String s : arr) {
          ok = name.toLowerCase().contains( s.toLowerCase() );
          if(ok) {
            break;
          }
        }

        //if name matched -> check permission
        if(ok) {
          
          boolean hasPermission = hasPermission(0, UID, r.getUid());
          
          if(hasPermission) {
            RoutineModel m = Routine.getClientModel(r);
            list.add(m);
            
            i++;
          }
          
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchRoutines", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("searchRoutines", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    
    return list;
  }
  
  /**
   * Search workouts from other users
   * @return workouts' models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<WorkoutModel> searchWorkouts(int index, String query) throws ConnectionException {

    logger.log(Level.FINE, "searchWorkouts()");
    
    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    
    if(query == null) {
      return list;
    }
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Query q = pm.newQuery(Workout.class);
      q.setFilter("date == null && routineId == 0");
      q.setOrdering("name ASC");
      q.setRange(index, index + Constants.LIMIT_WORKOUTS + 1);
      List<Workout> workouts = (List<Workout>) q.execute();
      
      //split query string
      String[] arr = query.split(" ");

      int i = 0;
      for(Workout w : workouts) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_WORKOUTS) {
          list.add(null);
          break;
        }
        

        final String name = w.getName();
        
        //filter by query
        boolean ok = false;
        for(String s : arr) {
          ok = name.toLowerCase().contains( s.toLowerCase() );
          if(ok) {
            break;
          }
        }

        //if name matched -> check permission
        if(ok) {
          System.out.println(w.getName()+": 0, "+UID+", "+w.getUid());
          
          WorkoutModel m = null;
          try {
            m = StoreTraining.getWorkoutModel(pm, w.getId(), UID);
          } catch (NoPermissionException e) {
            //no permission -> skipping
          }

          if(m != null) {
              list.add(m);                
              i++;
          }
          
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchWorkouts", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("searchWorkouts", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    
    return list;
  }
  
  
  /**
   * Search users
   * @param index
   * @param query
   * @return
   * @throws ConnectionException
   */
  @SuppressWarnings("unchecked")
  public List<UserModel> searchUsers(int index, String query) throws ConnectionException {

    logger.log(Level.FINE, "searchUsers()");
    
    List<UserModel> list = new ArrayList<UserModel>();
    
    if(query == null) {
      return list;
    }
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //everything but current user
      Query q = pm.newQuery(UserOpenid.class);
      q.setFilter("id != idParam");
      q.declareParameters("java.lang.String idParam");
      q.setRange(index, index + Constants.LIMIT_USERS + 1);
      List<UserOpenid> users = (List<UserOpenid>) q.execute(UID);
      
      //split query string
      String[] arr = query.split(" ");

      int i = 0;
      for(UserOpenid u : users) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_USERS) {
          list.add(null);
          break;
        }
        
        final String name = u.getNickName();
        
        //filter by query
        boolean ok = false;
        for(String s : arr) {
          ok = name.toLowerCase().contains( s.toLowerCase() );
          if(ok) {
            break;
          }
        }
        
        if(ok) {
          UserModel m = UserOpenid.getClientModel(u);
          list.add(m);
          
          i++;
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchUsers", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("searchUsers", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    
    return list;
  }
  
  
  /**
   * Get users from circle
   * @param target
   * @return
   * @throws ConnectionException
   */
  @SuppressWarnings("unchecked")
  public List<UserModel> getUsersFromCircle(int target) throws ConnectionException {

    logger.log(Level.FINE, "getUsersFromCircle()");
    
    List<UserModel> list = new ArrayList<UserModel>();
        
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //everything but current user
      Query q = pm.newQuery(Circle.class);
      q.setFilter("openId == openIdParam && target == targetParam");
      q.declareParameters("java.lang.String openIdParam, java.lang.Integer targetParam");
      List<Circle> users = (List<Circle>) q.execute(UID, target);
      
      for(Circle c : users) {
        UserModel m;
        
        //get user (id not -1)
        if(!c.getFriendId().equals("-1")) {
          UserOpenid u = pm.getObjectById(UserOpenid.class, c.getFriendId());
        
          m = UserOpenid.getClientModel(u);
        }
        //id == -1 (all users enabled)
        else {
          m = new UserModel("-1");
        }
        list.add(m);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getUsersFromCircle", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getUsersFromCircle", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }
  

  /**
   * Updates cardio
   * @param model to be updated
   * @return update successful
   */
  @Override
  public Boolean updateCardio(CardioModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateCardio()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Cardio m = pm.getObjectById(Cardio.class, model.getId());
      if(m != null) {
        //check if correct user
        if(m.getUid().equals(UID)) {
          
          //update
          m.setName(model.getNameServer());
          
          ok = true;
        }
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateCardio", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateCardio", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Updates exercise
   * @param workout : model to be added
   * @return updated exercise (null if add not successful)
   */
  @Override
  public ExerciseModel updateExercise(ExerciseModel exercise) throws ConnectionException {

    logger.log(Level.FINE, "Updating exercise");
    
    //get uid
    final Object[] obj = getUidAndLocale();
    final String UID = (String)obj[0];
    final String LOCALE = (String)obj[1];
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      exercise = StoreTraining.updateExerciseModel(pm, exercise, UID, LOCALE);
        
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Error updating exercise", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      
      throw new ConnectionException("updateExercise", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return exercise;
  }

  /**
   * Updates exercise name
   * @param exercise : model to be updated
   * @return updated exercise name (null if add not successful)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Boolean updateExerciseName(ExerciseNameModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateExerciseName()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    //if not admin
    if( !isAdmin(UID) ) {
      return false;
    }
    
    try {
      ExerciseName m = pm.getObjectById(ExerciseName.class, model.getId());
      if(m != null) {

        //remove search indexes which has this name as query
        final String strName = m.getName();
        //if name or locale changed
        if(!strName.equals(model.getName()) || !m.getLocale().equals(model.getLocale())) {
          Query q1 = pm.newQuery(ExerciseSearchIndex.class);
          List<ExerciseSearchIndex> arrQuery = (List<ExerciseSearchIndex>) q1.execute();
          for(ExerciseSearchIndex index : arrQuery) {
            //check if query words that match added name
            int count = 0;
            for(String s : index.getQuery().split(" ")) {
              //if word long enough and match
              if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD && strName.toLowerCase().contains( s.toLowerCase() )) {
                  count++;
              }
            }
            
            //if found -> remove index
            if(count > 0) {
              pm.deletePersistent(index);
            }
          }
        }
        
        //update name
        m.setName(model.getName());
        m.setTarget(model.getTarget());
        m.setVideo(model.getVideo());
        m.setLocale(model.getLocale());
        
        ok = true;
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateExerciseName", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateExerciseName", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Updates exercises order in given workout
   * @param workout : workout's model
   * @param ids: exercises' ids in correct order
   * @return update successful
   */
  @Override
  public Boolean updateExerciseOrder(WorkoutModel workout, Long[] ids) throws ConnectionException {

    logger.log(Level.FINE, "updateExerciseOrder()");
    
    boolean ok = true;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Workout w = pm.getObjectById(Workout.class, workout.getId());
      if(w != null) {
        //check if correct user
        if(w.getUid().equals(UID)) {
                    
          //get exercises
          for(int i=0; i < ids.length; i++) {
            Exercise e = null;
            for(Exercise ex : w.getExercises()) {
              if(ex.getId().longValue() == ids[i].longValue()) {
                e = ex;
                break;
              }
            }
            if(e != null) {
              e.setOrder(i);
            }
            else {
              ok = false;
            }
          }
        }
        
      }
    } catch (Exception e) {
      ok = false;
      logger.log(Level.SEVERE, "updateExerciseOrder", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateExerciseOrder", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Updates food
   * @param food : model to be updated
   * @return updated food (null if add not successful)
   */
  @Override
  public FoodModel updateFood(FoodModel food) throws ConnectionException {

    logger.log(Level.FINE, "updateFood()");

    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      
      try {
      
        //if existing food
        if(food.getId() != 0) {
          FoodNameModel nameModel = food.getName();
          
          //if food is in meal (which is in time)
          if(food.getTimeId() != 0 && food.getMealId() != 0) {
            //get time
            Time time = pm.getObjectById(Time.class, food.getTimeId());
            
            if(time != null && hasPermission(1, UID, time.getUid())) {
              //get meal
              for(MealInTime meal : time.getMeals()) {
                if(meal.getId() == food.getMealId()) {
                  for(FoodInMealTime f : meal.getFoods()) {
                    if(food.getId() == f.getId()) {
                      
                      //set nameId IF name changed
                      long nameId = -1;
                      if(food.getName() == null || f.getNameId() != food.getName().getId()) {
                        //name
                        if(food.getName() != null && food.getName().getId() > 0) {
                          final FoodName n = pm.getObjectById(FoodName.class, food.getName().getId());
                          if(n != null) {
                            //get client side model
                            nameModel = FoodName.getClientModel(n);
                            //save new id
                            nameId = n.getId();
                          }
                          
                        }
                      }
                      
                      //save
                      tx.begin();
                      f.setAmount(food.getAmount());
                      if(nameId != -1) {
                        f.setNameId(nameId);
                      }
                      pm.flush();
                      tx.commit();
                      
                      break;
                    }
                  }
                  break;
                }
              }
            }
          }
          //if food is in time
          else if(food.getTimeId() != 0) {
            Time time = pm.getObjectById(Time.class, food.getTimeId());
            
            //meal found and we have permission
            if(time != null && hasPermission(1, UID, time.getUid())) {
              for(FoodInTime f : time.getFoods()) {
                if(food.getId() == f.getId()) {
                  
                  //set nameId IF name changed
                  long nameId = -1;
                  if(food.getName() == null || f.getNameId() != food.getName().getId()) {
                    //name
                    if(food.getName() != null && food.getName().getId() > 0) {
                      final FoodName n = pm.getObjectById(FoodName.class, food.getName().getId());
                      if(n != null) {
                        //get client side model
                        nameModel = FoodName.getClientModel(n);
                        //save new id
                        nameId = n.getId();
                      }
                      
                    }
                  }
                  
                  //save
                  tx.begin();
                  f.setAmount(food.getAmount());
                  if(nameId != -1) {
                    f.setNameId(nameId);
                  }
                  pm.flush();
                  tx.commit();
                  
                  break;
                }
              }
            }
          }
          //if food is in meal
          else if(food.getMealId() != 0) {
            Meal time = pm.getObjectById(Meal.class, food.getMealId());

            //meal found and we have permission
            if(time != null && hasPermission(1, UID, time.getUid())) {
              for(FoodInMeal f : time.getFoods()) {
                if(food.getId() == f.getId()) {
                  
                  //set nameId IF name changed
                  long nameId = -1;
                  if(food.getName() == null || f.getNameId() != food.getName().getId()) {
                    //name
                    if(food.getName() != null && food.getName().getId() > 0) {
                      final FoodName n = pm.getObjectById(FoodName.class, food.getName().getId());
                      if(n != null) {
                        //get client side model
                        nameModel = FoodName.getClientModel(n);
                        //save new id
                        nameId = n.getId();
                      }
                      
                    }
                  }
                  
                  //save
                  tx.begin();
                  f.setAmount(food.getAmount());
                  if(nameId != -1) {
                    f.setNameId(nameId);
                  }
                  pm.flush();
                  tx.commit();
                  
                  break;
                }
              }
            }
          }
          
          //update model (which we return)
          food.setName(nameModel);
        }
        
        break;
  
      }
      catch (Exception e) {
        logger.log(Level.SEVERE, "updateFood", e);
        
        //retries used
        if (retries == 0) {
          if (!pm.isClosed()) {
            pm.close();
          } 
          
          throw new ConnectionException("updateFood", e.getMessage());
        }
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }        
      }
      finally {
        if (tx.isActive()) {
          tx.rollback();
        }
      }
    }
    if (!pm.isClosed()) {
      pm.close();
    }
    
    return food;
  }
  
  /**
   * Updates exercise name
   * @param exercise : model to be updated
   * @return updated exercise name (null if add not successful)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Boolean updateFoodName(FoodNameModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateFoodName()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    FoodName m;
    try {
      m = pm.getObjectById(FoodName.class, model.getId());
      
      //admin or our food
      if(m != null && (isAdmin(UID) || m.getUid().equals(UID))) {
        
        //remove search indexes which has this name as query
        final String strName = m.getName();
        //if name or locale changed
        if(!strName.equals(model.getName()) || m.getTrusted() != model.getTrusted() || !m.getLocale().equals(model.getLocale())) {
          Query q1 = pm.newQuery(FoodSearchIndex.class);
          List<FoodSearchIndex> arrQuery = (List<FoodSearchIndex>) q1.execute();
          for(FoodSearchIndex index : arrQuery) {
            //check if query words that match added name
            int count = 0;
            for(String s : index.getQuery().split(" ")) {
              //if word long enough and match
              if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD && strName.toLowerCase().contains( s.toLowerCase() )) {
                  count++;
              }
            }
            
            //if found -> remove index
            if(count > 0) {
              pm.deletePersistent(index);
            }
          }
        }
        
        //update name
        m.setName(model.getName());
        m.setEnergy(model.getEnergy());
        m.setProtein(model.getProtein());
        m.setCarb(model.getCarb());
        m.setFet(model.getFet());
        m.setPortion(model.getPortion());
        m.setLocale(model.getLocale());
        m.setTrusted(model.getTrusted());
        m.setUid(model.getUid());
        //update micronutrients
        List<MicroNutrient> arr = new ArrayList<MicroNutrient>();
        for(MicroNutrientModel mn : model.getMicroNutrients()) {
          arr.add(MicroNutrient.getServerModel(mn));
        }
        m.setMicronutrients(arr);
        pm.makePersistent(m);
        ok = true;
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateFoodName", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateFoodName", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Updates meal
   * @param model to be updated
   * @return update successful
   */
  @Override
  public Boolean updateMeal(MealModel meal) throws ConnectionException {

    logger.log(Level.FINE, "updateMeal()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Meal mealServer = null;
      //if in time
      if(meal.getTimeId() != 0) {
        //get time
        Time time = pm.getObjectById(Time.class, meal.getTimeId());
        if(time != null && time.getUid().equals(UID)) {
          //get meal
          for(MealInTime m : time.getMeals()) {
            if(m.getId() == meal.getId()) {
              //update meal
              m.setName(meal.getName());
              
              ok = true;
              
              break;
            }
          }
        }
      }
      //not in time
      else {
        mealServer = pm.getObjectById(Meal.class, meal.getId());
        //check if correct user
        if(mealServer != null && mealServer.getUid().equals(UID)) {
          mealServer.setName(meal.getName());
          
          ok = true;
        }
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateMeal", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateMeal", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Updates measurement
   * @param model to be updated
   * @return update successful
   */
  @Override
  public Boolean updateMeasurement(MeasurementModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateMeasurement()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Measurement m = pm.getObjectById(Measurement.class, model.getId());
      if(m != null) {
        //check if correct user
        if(m.getUid().equals(UID)) {
          
          //update
          m.setName(model.getNameServer());
          m.setUnit(model.getUnit());
          m.setDate(model.getDate());
          m.setTarget(model.getTarget());
          
          ok = true;
        }
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateMeasurement", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateMeasurement", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Updates routine model
   * @param model to be updated
   * @return update successfull
   */
  @SuppressWarnings({ "unchecked", "deprecation" })
  @Override
  public Boolean updateRoutine(RoutineModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateRoutine()");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Routine r = pm.getObjectById(Routine.class, model.getId());
      if(r != null) {
        //check if correct user
        if(r.getUid().equals(UID)) {
          
          boolean daysRemoved = r.getDays() > model.getDays(); 

          //reset time from date
          Date d = model.getDate();
          if(d != null) {
            d.setHours(0);
            d.setMinutes(0);
            d.setSeconds(0);
          }
          
          //update workout
          r.setDate(d);
          r.setDays(model.getDays());
          r.setName(model.getName());
          
          //if days removed -> remove workouts
          if(daysRemoved) {
            Query q = pm.newQuery(Workout.class);
            q.setFilter("date == null && routineId == routineIdParam && dayInRoutine > daysParam");
            q.declareParameters("java.lang.Long routineIdParam, java.lang.Integer daysParam");
            List<Workout> workouts = (List<Workout>) q.execute(r.getId(), r.getDays());
            if(workouts != null) {
              pm.deletePersistentAll(workouts);
            }
            
          }
          
          ok = true;
        }
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateRoutine", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateRoutine", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }
  
  /**
   * Updates run
   * @param model to be updated
   * @return update successful
   */
  @Override
  public Boolean updateRun(RunModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateRun()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Run m = pm.getObjectById(Run.class, model.getId());
      if(m != null) {
        //check if correct user
        if(m.getUid().equals(UID)) {
          
          //update
          m.setName(model.getNameServer());
          m.setDistance(model.getDistance());
          
          ok = true;
        }
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateRun", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateRun", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Updates time
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public Boolean updateTime(TimeModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateTime()");

    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    //try to update X times
    int retries = Constants.LIMIT_UPDATE_RETRIES;
    while (true) {
      
      Transaction tx = pm.currentTransaction();
      tx.begin();

      try {
        
        Time m = pm.getObjectById(Time.class, model.getId());
        if(m != null) {
          //check if correct user
          if(m.getUid().equals(UID)) {
            
            //check if same time already exists -> remove this one and add its foods/meals to previous
            Time timeSimilar = null;
            final Date dStart = stripTime(model.getDate(), true);
            final Date dEnd = stripTime(model.getDate(), false);
              
            Query q = pm.newQuery(Time.class);
            q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
            q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
            List<Time> times = (List<Time>) q.execute(UID, dStart, dEnd);
            for(Time t : times) {
              if(t.getTime() == model.getTime() && t.getId() != model.getId()) {
                timeSimilar = t;
                break;
              }
            }
            
            //if similar time found -> add meals/foods from old time to similar times
            if(timeSimilar != null) {
              
              //meals
              List<MealInTime> listMeals = timeSimilar.getMeals();
              for(MealInTime meal : m.getMeals()) {
                listMeals.add(meal);
              }
              timeSimilar.setMeals(listMeals);
              
              //foods
              List<FoodInTime> listFoods = timeSimilar.getFoods();
              for(FoodInTime food : m.getFoods()) {
                listFoods.add(food);
              }
              timeSimilar.setFoods(listFoods);
              
              //remove given time
              pm.deletePersistent(m);
            }
            //update Time
            else {
              m.setTime((long) model.getTime());
            }
            
            ok = true;
          }
        }
        
        pm.flush();
        tx.commit();
        
        break;
  
      }
      catch (Exception e) {
        logger.log(Level.SEVERE, "updateTime", e);
        
        //retries used
        if (retries == 0) {
          if (!pm.isClosed()) {
            pm.close();
          } 
          
          throw new ConnectionException("updateTime", e.getMessage());
        }
        
        --retries;
        
        //small delay between retries
        try {
          Thread.sleep(DELAY_BETWEEN_RETRIES);
        }
        catch(Exception ex) { }
      }
      finally {
        if (tx.isActive()) {
          tx.rollback();
        }
      }
    }
    if (!pm.isClosed()) {
      pm.close();
    } 
    
    return ok;
  }
  
  /**
   * Updates workout model
   * @param model to be updated
   * @return update successfull
   */
  @Override
  public Boolean updateWorkout(WorkoutModel model) throws ConnectionException {

    logger.log(Level.FINE, "Updating workout");
    
    boolean ok = false;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      StoreTraining.updateWorkoutModel(pm, model, UID);
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "updateWorkout", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("updateWorkout", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return ok;
  }

  /**
   * Duplicates routine
   * @param routine
   * @return duplicated routine
   */
  private Routine duplicateRoutine(Routine r) throws ConnectionException {

    logger.log(Level.FINE, "duplicateRoutine()");

    try {
      Routine rNew = new Routine(r.getName());
      rNew.setDays(r.getDays());

      return rNew;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "duplicateRoutine", e);
      throw new ConnectionException("duplicateRoutine", e.getMessage());
    }
  }




  /**
   * Returns single workout
   * @param workoutId
   * @return
   * @throws ConnectionException 
   */
  @Override public WorkoutModel getWorkout(Long workoutId) throws ConnectionException {

    logger.log(Level.FINE, "Loading single workout ("+workoutId+")");
    
    WorkoutModel m = null;
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      m = StoreTraining.getWorkoutModel(pm, workoutId, UID);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getWorkout", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getWorkout", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  /**
   * Returns all workouts that aren't in calendar
   * @param routine : if null returns all workouts
   * @return workouts' models (if routine set -> also exercises are returned)
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<WorkoutModel> getWorkouts(int index, RoutineModel routine) throws ConnectionException {

    logger.log(Level.FINE, "Loading workouts. Index="+index);

    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Query q = pm.newQuery(Workout.class);

      List<Workout> workouts = null;
      //if from single routine
      if(routine != null) {
        
        //get routine so we know is it shared
        Routine r = pm.getObjectById(Routine.class, routine.getId());
        if(r == null) {
          throw new Exception();
        }
        
        //check permission if not our routine
        if(!r.getUid().equals(UID)) {
          boolean hasPermission = hasPermission(0, UID, r.getUid());
          
          //if no permission for the routine -> return empty list
          if(!hasPermission) {
            throw new Exception();
          }
        }

        q.setFilter("date == null && routineId == routineIdParam");
        q.declareParameters("java.lang.Long routineIdParam");
        workouts = (List<Workout>) q.execute(r.getId());
        
      }
      //all single workouts
      else {
        q.setFilter("date == null && routineId == 0 && openId == openIdParam");
        q.declareParameters("java.lang.String openIdParam");
        q.setRange(index, 100);
        workouts = (List<Workout>) q.execute(UID);
      }

      Collections.sort(workouts);
      
      int i = 0;
      for(Workout w : workouts) {
        
        //if limit reached -> add null value
        if(i == Constants.LIMIT_WORKOUTS) {
          list.add(null);
          break;
        }

        WorkoutModel m = StoreTraining.getWorkoutModel(pm, w.getId(), UID);
          
        list.add(m);
        
        i++;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getWorkouts", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getWorkouts", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
  }

  /**
   * Get workouts in calendar between dates
   * @param uid : who's workouts
   * @param dateStart
   * @param dateEnd
   * @return workoutmodels in each days ( model[days][day's workouts] )
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<WorkoutModel[]> getWorkoutsInCalendar(String uid, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getWorkoutsInCalendar()");

    List<WorkoutModel[]> list = new ArrayList<WorkoutModel[]>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }
    
    //check dates
    if(dateStart.getTime() > dateEnd.getTime()) {
      return null;
    }

    //check permission
    if(!hasPermission(0, UID, uid)) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //go through days
      final int days = (int)((dateEnd.getTime() - dateStart.getTime()) / (24 * 60 * 60 * 1000)) + 1;
      
      for(int i=0; i < days; i++) {
        
        final Date d = new Date((dateStart.getTime() / 1000 + 3600 * 24 * i) * 1000);
        //strip time
        final Date dStart = stripTime(d, true);
        final Date dEnd = stripTime(d, false);
        
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
        List<Workout> workouts = (List<Workout>) q.execute(uid, dStart, dEnd);

        //convert to client side models
        WorkoutModel[] arr = new WorkoutModel[workouts.size()];
        int c = 0;
        for(Workout w : workouts) {
          WorkoutModel m = StoreTraining.getWorkoutModel(pm, w.getId(), UID);

          arr[c] = m;
          c++;
        }
        list.add(arr);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getWorkoutsInCalendar", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getWorkoutsInCalendar", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    return list;
  }

  
  /**
   * Return guide values
   * @param date : if null -> all values are returned
   * @return values
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  public List<GuideValueModel> getGuideValues(String uid, int index, Date date) throws ConnectionException {

    logger.log(Level.FINE, "Loading guide values: "+date);

    List<GuideValueModel> list = new ArrayList<GuideValueModel>();
    
    //get uid
    final String UID = getUid();
    if(UID == null) {
      return null;
    }

    //check permission
    if(!hasPermission(1, UID, uid)) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(GuideValue.class);

      List<GuideValue> values = null;
      
      if(date != null) {
        //strip dates
        final Date d1 = stripTime(date, true);
        final long d2 = d1.getTime();

        q.setFilter("openId == openIdParam && dateStart <= dateStartParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam");
        values = (List<GuideValue>) q.execute(uid, d1);
        
        //check if this date has training
        boolean hasTraining = hasTraining(uid, date);

        if(values != null) {
          for(GuideValue m : values) {
            
            //check if date end bigger than given date
            if(m.getDateEnd().getTime() >= d2) {
              final GuideValueModel mClient = GuideValue.getClientModel(m);
              //if date set -> set also hasTraining variable
              mClient.setHasTraining(hasTraining);
              list.add( mClient );

              break;
            }
          }
        }
      }
      //return all values
      else {
        q.setFilter("openId == openIdParam");
        q.declareParameters("java.lang.String openIdParam");
        q.setOrdering("dateStart DESC");
        q.setRange(index, index + Constants.LIMIT_GUIDE_VALUES + 1);
        values = (List<GuideValue>) q.execute(uid);

        if(values != null) {
          int i = 0;
          for(GuideValue m : values) {
            //if limit reached -> add null value
            if(i == Constants.LIMIT_GUIDE_VALUES) {
              list.add(null);
              break;
            }
            
            final GuideValueModel mClient = GuideValue.getClientModel(m);
            list.add( mClient );
            i++;
          }
        }
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, "getGuideValues", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("getGuideValues", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return list;
    
  }


  /**
   * Checks if current date has training (used for determining guide value)
   * @param date
   * @return
   * @throws ConnectionException 
   */
  public boolean hasTraining(String uid, Date date) throws ConnectionException {

    logger.log(Level.FINE, "Checking if date '"+date+"' has training");
  
    boolean hasTraining = getWorkoutsInCalendar(uid, date, date).get(0).length > 0;
      
    //TODO check also if day contains cardio!
    
    
    return hasTraining;
  }
  
  /**
   * Returns correct food name
   * @param food
   * @return
   */
  @SuppressWarnings("unchecked")
  private Long fetchAddFoodName(FoodModel food) throws ConnectionException {

    logger.log(Level.FINE, "fetchAddFoodName()");
    
    long id = 0;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(FoodName.class);
      q.setFilter("name == nameParam && energy == energyParam && locale == localeParam");
      q.declareParameters("java.lang.String nameParam, java.lang.Double energyParam, java.lang.String localeParam");
      List<FoodName> arr = (List<FoodName>) q.execute(food.getName().getName(), food.getName().getEnergy(), food.getName().getLocale());
            
      //if found
      boolean found = false;
      //update Time
      if(arr != null && arr.size() > 0) {
          found = true;
      }

      //update Time
      if(found) {
        id = arr.get(0).getId();
      }
      //create new
      else {
        FoodName mServer = FoodName.getServerModel(food.getName());

        //get uid
        final String UID = getUid();
        if(UID == null) {
          return id;
        }
        
        mServer.setUid(UID);
        FoodName added = pm.makePersistent(mServer);
        id = added.getId();
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchAddFoodName", e);
      if (!pm.isClosed()) {
        pm.close();
      } 
      throw new ConnectionException("fetchAddFoodName", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    return id;
  }
  
  /**
   * Checks if current user is admin
   * @param uID
   * @return is admin
   */
  @SuppressWarnings("unchecked")
  private boolean isAdmin(String uid) {

    logger.log(Level.FINE, "isAdmin()");

    boolean isAdmin = false;
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(UserOpenid.class, "id == idParam");
      q.declareParameters("java.lang.String idParam");
      List<UserOpenid> users = (List<UserOpenid>) q.execute(uid);
      
      //data found
      if(users.size() > 0) {
        isAdmin = users.get(0).isAdmin();
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "isAdmin", e);
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return isAdmin;
  }

  
}
