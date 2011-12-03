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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.delect.motiver.client.service.MyService;
import com.delect.motiver.server.FoodInMeal;
import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.server.Meal;
import com.delect.motiver.server.MealInTime;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.jdo.Cardio;
import com.delect.motiver.server.jdo.CardioValue;
import com.delect.motiver.server.jdo.Circle;
import com.delect.motiver.server.jdo.Comment;
import com.delect.motiver.server.jdo.CommentsRead;
import com.delect.motiver.server.jdo.ExerciseSearchIndex;
import com.delect.motiver.server.jdo.FoodSearchIndex;
import com.delect.motiver.server.jdo.Measurement;
import com.delect.motiver.server.jdo.MeasurementValue;
import com.delect.motiver.server.jdo.MicroNutrient;
import com.delect.motiver.server.jdo.MonthlySummary;
import com.delect.motiver.server.jdo.MonthlySummaryExercise;
import com.delect.motiver.server.jdo.Run;
import com.delect.motiver.server.jdo.RunValue;
import com.delect.motiver.server.jdo.UserOpenid;
import com.delect.motiver.server.jdo.nutrition.FoodJDO;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.GuideValue;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.delect.motiver.server.jdo.training.Exercise;
import com.delect.motiver.server.jdo.training.ExerciseName;
import com.delect.motiver.server.jdo.training.Routine;
import com.delect.motiver.server.jdo.training.Workout;
import com.delect.motiver.server.manager.NutritionManager;
import com.delect.motiver.server.manager.TrainingManager;
import com.delect.motiver.server.manager.UserManager;
import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.CommentModel;
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
import com.delect.motiver.shared.exception.ConnectionException;
import com.delect.motiver.shared.exception.NoPermissionException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MyServiceImpl extends RemoteServiceServlet implements MyService {

  /**
   * 
   */
  private static final long serialVersionUID = -7106279162988246661L;
  
  UserManager userManager = UserManager.getInstance();

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
      return item.count - count;
    }
  }
  
  private static final Logger logger = Logger.getLogger(MyServiceImpl.class.getName()); 
  
  static final int MAX_COUNT = 4000;
    
  
  /**
   * Calculates energy from times (searches meals and foods)
   * @param pm 
   * @param times
   * @return
   */
  @Deprecated private static NutritionDayModel calculateEnergyFromTimesOfDays(List<TimeJDO> times, String UID) {

    logger.log(Level.FINE, "calculateEnergyFromTimes()");
    double energy = 0;
    double protein = 0; 
    double carbs = 0;
    double fet = 0;

    try {
      
      //each time
      for(TimeJDO tClient : times) {
        
        //each meal
        for(MealJDO m : tClient.getMealsNew()) {
          
            if(m.getFoods() != null) {
              for(FoodJDO food : m.getFoods()) {

                final double amount = food.getAmount();
                final FoodName name = food.getName();
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
          for(FoodJDO food : tClient.getFoods()) {

            final double amount = food.getAmount();
            final FoodName name = food.getName();
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
  @Deprecated public static Exercise duplicateExercise(Exercise e) {

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
   * Checks if user has permission to given target
   * @param target : what item is shared, 0=training, 1=nutrition, 4=nutrition foods, 2=cardio, 3=measurement
   *        edit permissions: 10=training, 11=nutrition, 14=nutrition foods, 12=cardio, 13=measurement
   * @param ourUid : our user id
   * @param uid : target's user id (if same that ours -> returns always true)
   * @return has permission
   */
//  @SuppressWarnings("unchecked")
//  private static boolean hasPermission(int target, String ourUid, String uid) {
//
//    logger.log(Level.FINE, "hasPermission()");
//    
//    if(ourUid.equals(uid)) {
//      return true;
//    }
//    
//    PersistenceManager pm =  PMF.get().getPersistenceManager();
//    
//    boolean hasPermission = false;
//    try {
//
//      Query q = pm.newQuery(Circle.class);
//      q.setFilter("openId == openIdParam && (friendId == friendIdParam || friendId == '-1') && target == targetParam");
//      q.declareParameters("java.lang.String openIdParam, java.lang.String friendIdParam, java.lang.Integer targetParam");
//      q.setRange(0,1);
//      List<Circle> list = (List<Circle>)q.execute(uid, ourUid, target);
//      
//      hasPermission = (list.size() > 0);
//      
//    } catch (Exception e) {
//      logger.log(Level.SEVERE, "hasPermission", e);
//    }
//    finally {
//      if (!pm.isClosed()) {
//        pm.close();
//      } 
//    }
//    
//    return hasPermission;
//  }

  /**
   * Checks if user has permission to given target
   * @param target : PERMISSION::READ_XXX or PERMISSION::WRITE_XXX
   * @param ourUid : our user id
   * @param uid : target's user id (if same that ours -> returns always true)
   * @return has permission
   */
  @SuppressWarnings("unchecked")
  @Deprecated public static boolean hasPermission(PersistenceManager pm, int target, String ourUid, String uid) {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Checking permission "+target+" for "+ourUid+", "+uid);
    }
    
    //if own data -> return always true
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
  @Deprecated public static Date stripTime(Date date, boolean isStart) {

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
  
  @Override
  public UserModel getUser() throws ConnectionException {

    UserModel jdo = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    //user found
    if(user != null) {
      
      jdo = UserOpenid.getClientModel(user);

      UserService userService = UserServiceFactory.getUserService();
      jdo.setLogoutUrl(userService.createLogoutURL("http://www.motiver.fi"));
      
    }
    
    return jdo;
  }
  
  
  /**
   * Adds cardio to db
   * @param cardio : model to be added
   * @return added cardio (null if add not successful)
   */
  @Override
  @Deprecated public CardioModel addCardio(CardioModel cardio) throws ConnectionException {

    logger.log(Level.FINE, "addCardio()");
    
    CardioModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public CardioValueModel addCardioValue(CardioModel cardio, CardioValueModel value) throws ConnectionException {

    logger.log(Level.FINE, "addCardioValue()");
    
    CardioValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public CommentModel addComment(CommentModel comment) throws ConnectionException  {

    logger.log(Level.FINE, "addComment()");
    
    CommentModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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

    ExerciseModel m = null;
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    TrainingManager trainingManager = TrainingManager.getInstance();
    Exercise jdo = Exercise.getServerModel(exercise);
    trainingManager.addExercise(user, jdo, exercise.getWorkoutId());
    m = Exercise.getClientModel(jdo);

    
    return m;
  }

  /**
   * Creates / updates exercisename (updates if already found)
   * @param name : model to be added
   * @return added name (null if add not successful)
   */
  @Override
  public ExerciseNameModel addExercisename(ExerciseNameModel name) throws ConnectionException {

      List<ExerciseNameModel> list = new ArrayList<ExerciseNameModel>();
      list.add(name);
      
      list = addExercisename(list);
      if(list.size() > 0) {
        return list.get(0);
      }
      
      return null;
  }

  /**
   * Creates / updates exercisename (updates if already found)
   * @param title : model to be added
   * @return added name (null if add not successful)
   */
  @Override
  public List<ExerciseNameModel> addExercisename(List<ExerciseNameModel> names) throws ConnectionException {

    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    TrainingManager trainingManager = TrainingManager.getInstance();  
    
    List<ExerciseName> jdoList = new ArrayList<ExerciseName>();
    for(ExerciseNameModel n : names) {
      jdoList.add(ExerciseName.getServerModel(n));
    }
    jdoList = trainingManager.addExerciseName(user, jdoList);
    
    //convert to client side models
    List<ExerciseNameModel> list = new ArrayList<ExerciseNameModel>();
    for(ExerciseName n : jdoList) {
      list.add(ExerciseName.getClientModel(n));
    }
    
    return list;
    
  }

  /**
   * Adds food to (meal)
   * @param food : model to be added
   * @param meal : meal where food is added (null if not added in any meal)
   * @return added food (null if add not successful)
   */
  public FoodModel addFood(FoodModel food) throws ConnectionException {

    FoodModel m = null;
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    try {
      NutritionManager nutritionManager = NutritionManager.getInstance();
      FoodJDO jdo = FoodJDO.getServerModel(food);
      nutritionManager.addFood(user, jdo, food.getTimeId(), food.getMealId());
      m = FoodJDO.getClientModel(jdo);

    }
    catch (Exception e) {
      throw new ConnectionException("addFood", e.getMessage());
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

      List<FoodNameModel> list = new ArrayList<FoodNameModel>();
      list.add(name);
      
      list = addFoodname(list);
      if(list.size() > 0) {
        return list.get(0);
      }
      
      return null;
  }

  /**
   * Creates / updates foodname (updates if already found)
   * @param title : model to be added
   * @return added name (null if add not successful)
   */
  @Override
  public List<FoodNameModel> addFoodname(List<FoodNameModel> names) throws ConnectionException {

    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    NutritionManager nutritionManager = NutritionManager.getInstance();  
    
    List<FoodName> jdoList = new ArrayList<FoodName>();
    for(FoodNameModel n : names) {
      jdoList.add(FoodName.getServerModel(n));
    }
    jdoList = nutritionManager.addFoodName(user, jdoList);
    
    //convert to client side models
    List<FoodNameModel> list = new ArrayList<FoodNameModel>();
    for(FoodName n : jdoList) {
      list.add(FoodName.getClientModel(n));
    }
    
    return list;
    
  }

  /**
   * Adds guide value
   * @param model
   * @return
   */
  @Override @Deprecated public GuideValueModel addGuideValue(GuideValueModel model) throws ConnectionException {

    logger.log(Level.FINE, "addGuideValue()");
    
    GuideValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  public MealModel addMeal(MealModel meal, long timeId) throws ConnectionException {

    List<MealModel> list = new ArrayList<MealModel>();
    list.add(meal);
    
    list = addMeals(list, timeId);
    
    if(list != null && list.size() > 0) {
        return list.get(0);
    }
    
    return null;
  }

  /**
   * Adds meal to db
   * @param meal : model to be added
   * @return added meal (null if add not successful)
   * @throws ConnectionException 
   */
  @Override
  public List<MealModel> addMeals(List<MealModel> meals, long timeId) throws ConnectionException {
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    
    List<MealModel> list = new ArrayList<MealModel>();
    
    List<MealJDO> jdos = new ArrayList<MealJDO>();
    for(MealModel m : meals) {
      jdos.add(MealJDO.getServerModel(m));
    }
    
    NutritionManager nutritionManager = NutritionManager.getInstance();
    List<MealJDO> jdosCopy = nutritionManager.addMeals(user, jdos, timeId);

    for(MealJDO m : jdosCopy) {
      list.add(MealJDO.getClientModel(m));
    }
    
    return list;
  }

  /**
   * Adds measurement to db
   * @param measurement : model to be added
   * @return added measurement (null if add not successful)
   */
  @Override
  @Deprecated public MeasurementModel addMeasurement(MeasurementModel meal) throws ConnectionException {

    logger.log(Level.FINE, "addMeasurement()");
    
    MeasurementModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public MeasurementValueModel addMeasurementValue(MeasurementModel measurement, MeasurementValueModel value) throws ConnectionException {

    logger.log(Level.FINE, "addMeasurementValue()");
    
    MeasurementValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
      throw new ConnectionException("addMeasurementValue", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }
    
    return m;
  }

  @Override
  public RoutineModel addRoutine(RoutineModel routine) throws ConnectionException  {

    List<RoutineModel> list = new ArrayList<RoutineModel>();
    list.add(routine);
    list = addRoutines(list);
    
    //get new routine
    RoutineModel model = null;
    
    if(list.size() > 0) {
      model = list.get(0);
    }
    
    return model;
  }
  
  @Override
  public List<RoutineModel> addRoutines(List<RoutineModel> routines) throws ConnectionException  {
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    
    List<RoutineModel> list;
    
    try {
      List<Routine> jdos = new ArrayList<Routine>();
      for(RoutineModel t : routines) {
        jdos.add(Routine.getServerModel(t));
      }
      
      TrainingManager trainingManager = TrainingManager.getInstance();
      List<Routine> jdosCopy = trainingManager.addRoutines(user, jdos);

      list = new ArrayList<RoutineModel>();
      for(Routine t : jdosCopy) {
        list.add(Routine.getClientModel(t));
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding routines", e);
      throw new ConnectionException("Error adding routines", e);
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
  @Deprecated public RunModel addRun(RunModel meal) throws ConnectionException {

    logger.log(Level.FINE, "addRun()");
    
    RunModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public RunValueModel addRunValue(RunModel run, RunValueModel value) throws ConnectionException {

    logger.log(Level.FINE, "addRunValue()");
    
    RunValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
        
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return null;
    }
          
    try {

      //send email with info
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("antti@motiver.fi", "Motiver.fi user"));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress("jira@delect.atlassian.net", "JIRA"));
      msg.setSubject(ticket.getTitle());
      msg.setText(ticket.getTitle());
      Transport.send(msg);
      
      //TODO we don't check the response!
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding ticket", e);
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
  @Override
  public TimeModel addTime(TimeModel time) throws ConnectionException {

    TimeModel[] times = new TimeModel[] { time };    
    times = addTimes(times);
    
    return (times != null && times.length > 0)? times[0] : null;
  }

  /**
   * Adds time to db
   * @param time : model to be added
   * @return added time (null if add not successful)
   * @throws ConnectionException 
   */
  @Override
  public TimeModel[] addTimes(TimeModel[] times) throws ConnectionException {
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    
    TimeModel[] list;
    try {
      List<TimeJDO> jdos = new ArrayList<TimeJDO>();
      for(TimeModel t : times) {
        jdos.add(TimeJDO.getServerModel(t));
      }
      
      NutritionManager nutritionManager = NutritionManager.getInstance();
      List<TimeJDO> jdosCopy = nutritionManager.addTimes(user, jdos);

      list = new TimeModel[jdosCopy.size()];
      for(int i = 0; i < jdosCopy.size(); i++) {
        list[i] = TimeJDO.getClientModel(jdosCopy.get(i));
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding times", e);
      throw new ConnectionException("Error adding times", e);
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
  @Override
  public List<WorkoutModel> addWorkouts(List<WorkoutModel> workouts) throws ConnectionException  {
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    
    List<WorkoutModel> list;
    
    try {
      List<Workout> jdos = new ArrayList<Workout>();
      for(WorkoutModel t : workouts) {
        jdos.add(Workout.getServerModel(t));
      }
      
      TrainingManager trainingManager = TrainingManager.getInstance();
      List<Workout> jdosCopy = trainingManager.addWorkouts(user, jdos);

      list = new ArrayList<WorkoutModel>();
      for(Workout t : jdosCopy) {
        list.add(Workout.getClientModel(t));
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error adding workouts", e);
      throw new ConnectionException("Error adding workouts", e);
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
  @Deprecated public Boolean combineExerciseNames(Long firstId, Long[] ids) throws ConnectionException {

    logger.log(Level.FINE, "combineExerciseNames()");

    boolean ok = true;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }

    //if not admin
    if( !isAdmin(UID) ) {
      return false;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
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
   * @throws ConnectionException 
   */
  @Override @SuppressWarnings("unchecked")
  @Deprecated public Boolean combineFoodNames(Long firstId, Long[] ids) throws ConnectionException {

    logger.log(Level.FINE, "combineFoodNames()");

    boolean ok = true;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }

    //if not admin
    if( !isAdmin(UID) ) {
      return false;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      for(int i=0; i < ids.length; i++) {
        
        //get name
        FoodName name = pm.getObjectById(FoodName.class, ids[i]);
        
        if(name != null) {
          Query q = pm.newQuery(FoodJDO.class);
          q.setFilter("name == nameParam");
          q.declareParameters("java.lang.Long nameParam");
          List<FoodJDO> foods = (List<FoodJDO>) q.execute(ids[i]);

          //update other IDs
          for(FoodJDO f : foods)
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

  @Deprecated public String convertStreamToString(InputStream is) {

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

  @Override @Deprecated public Boolean dummy(MicroNutrientModel model) {

    logger.log(Level.FINE, "dummy()");
    return false;
  }

  @Override @Deprecated public MonthlySummaryExerciseModel dummy2(MonthlySummaryExerciseModel model) {

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
  @Deprecated public Boolean fetchRemoveAll(Boolean removeTraining, Boolean removeCardio, Boolean removeNutrition, Boolean removeMeasurement) throws ConnectionException {

    logger.log(Level.FINE, "fetchRemoveAll()");
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
          q = pm.newQuery(FoodJDO.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<FoodJDO> l = (List<FoodJDO>)q.execute(UID);
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
          q = pm.newQuery(TimeJDO.class); 
          q.setFilter("openId == openIdParam");
          q.declareParameters("java.lang.String openIdParam");
          q.setRange(0, MAX_COUNT - count);
          List<TimeJDO> l = (List<TimeJDO>)q.execute(UID);
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
  @Override @Deprecated public Boolean fetchSaveCardios(List<CardioModel> cardios, List<List<CardioValueModel>> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveCardios()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean fetchSaveFoodNames(List<FoodNameModel> names) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveFoodNames()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean fetchSaveGuideValues(List<GuideValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveGuideValues()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean fetchSaveMeals(List<MealModel> meals) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveMeals()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each food
      for(MealModel meal : meals) {

        //add meal
        final MealJDO mMealAdded = MealJDO.getServerModel(meal);
        mMealAdded.setUid(UID);
        
        //foods
        List<FoodJDO> list = new ArrayList<FoodJDO>();
        for(FoodModel food : meal.getFoods()) {
          //add food
          FoodJDO foodServer = FoodJDO.getServerModel(food);
          
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
  @Deprecated public Boolean fetchSaveMeasurements(MeasurementModel measurement, List<MeasurementValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveMeasurements()");

    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean fetchSaveRoutines(List<RoutineModel> routines, List<List<WorkoutModel>> workouts) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveRoutines()");

    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean fetchSaveRuns(List<RunModel> runs, List<List<RunValueModel>> values) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveRuns()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean fetchSaveTimes(List<TimeModel> times) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveTimes()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      //each food
      for(TimeModel mTime : times) {

        TimeJDO mTimeAdded = TimeJDO.getServerModel(mTime);
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
          List<FoodJDO> list = new ArrayList<FoodJDO>();
          for(FoodModel food : mTime.getFoods()) {
            //add food
            FoodJDO foodServer = FoodJDO.getServerModel(food);
            
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
  @Deprecated public Boolean fetchSaveWorkouts(List<WorkoutModel> workouts) throws ConnectionException {

    logger.log(Level.FINE, "fetchSaveWorkouts()");

    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public List<BlogData> getBlogData(int index, int limit, int target, Date dateStartParam, Date dateEndParam, String uidObj, Boolean showEmptyDays) throws ConnectionException {

    logger.log(Level.FINE, "getBlogData()");

    //new methods
    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    NutritionManager nutritionManager = NutritionManager.getInstance();
    TrainingManager trainingManager = TrainingManager.getInstance();
    
    //check if no uid -> use ours
    if(uidObj == null) {
      uidObj = user.getUid();
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
      UserOpenid userOther = userManager.getUser(uidObj);
      
      if(userOther != null) {
        
        String uid = userOther.getUid();
        if(!hasPermission(pm, Permission.READ_TRAINING, user.getUid(), uid)) {
          throw new NoPermissionException(Permission.READ_TRAINING, user.getUid(), uid);
        }
        if(!hasPermission(pm, Permission.READ_NUTRITION, user.getUid(), uid)) {
          throw new NoPermissionException(Permission.READ_NUTRITION, user.getUid(), uid);
        }
        if(!hasPermission(pm, Permission.READ_NUTRITION_FOODS, user.getUid(), uid)) {
          throw new NoPermissionException(Permission.READ_NUTRITION_FOODS, user.getUid(), uid);
        }
        if(!hasPermission(pm, Permission.READ_CARDIO, user.getUid(), uid)) {
          throw new NoPermissionException(Permission.READ_CARDIO, user.getUid(), uid);
        }
        if(!hasPermission(pm, Permission.READ_MEASUREMENTS, user.getUid(), uid)) {
          throw new NoPermissionException(Permission.READ_MEASUREMENTS, user.getUid(), uid);
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
          
          final Object[] arrParams = new Object[] {userOther.getUid(), dStart, dEnd};
          
          //variables
          List<Workout> workouts = null;
          List<TimeJDO> times = null;
          List<CardioValue> cValues = null;
          List<RunValue> rValues = null;
          List<MeasurementValue> mValues = null;
    
          Query q;
          
          //TRAINING
          if(permissionTraining && (target == 0 || target == 1 || target == 5)) {
            workouts = trainingManager.getWorkouts(user, dStart, dEnd, userOther.getUid());
          }
          
          //NUTRITION
          if(permissionNutrition && (target == 0 || target == 2)) {
            times = nutritionManager.getTimes(user, dStart, dEnd, userOther.getUid());
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
            bd.setUser(UserOpenid.getClientModel(userOther));
            bd.setDate(d);
    
            boolean found = false;
            
            //go through WORKOUTS
            if(workouts != null) {
              List<WorkoutModel> arrW = new ArrayList<WorkoutModel>();
              for(Workout w : workouts) {
                if(fmt.format(w.getDate()).equals(strD)) {
                  Workout jdo = trainingManager.getWorkout(user, w.getId());                  
                  arrW.add( Workout.getClientModel(jdo) );
                  
                  found = true;
                }
              }
              bd.setWorkouts(arrW);
            }
            
            //go through TIMES
            if(times != null) {
              List<TimeJDO> arrT = new ArrayList<TimeJDO>();
              for(TimeJDO t : times) {
                if(fmt.format(t.getDate()).equals(strD)) {
                  arrT.add(t);
                }
              }
              //if times found
              if(arrT.size() > 0) {
                NutritionDayModel ndm = calculateEnergyFromTimesOfDays(arrT, user.getUid());
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
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getBlogData", e);
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
  @Deprecated public List<CardioModel> getCardios(int index) throws ConnectionException {

    logger.log(Level.FINE, "getCardios()");

    List<CardioModel> list = new ArrayList<CardioModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public CardioValueModel getCardioValue(Long cardioId) throws ConnectionException {

    logger.log(Level.FINE, "getCardioValue()");
    
    CardioValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Cardio w = pm.getObjectById(Cardio.class, cardioId);
      if(w != null) {
        if(!hasPermission(pm, Permission.READ_CARDIO, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_CARDIO, UID, w.getUid());
        }

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
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getCardioValue", e);
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
  @Deprecated public List<CardioValueModel> getCardioValues(CardioModel cardio, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getCardioValues()");
    
    List<CardioValueModel> list = new ArrayList<CardioValueModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
        if(!hasPermission(pm, Permission.READ_CARDIO, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_CARDIO, UID, w.getUid());
        }
        
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
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getCardioValues", e);
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
  @Deprecated public List<CommentModel> getComments(int index, int limit, String target, String uid, boolean markAsRead) throws ConnectionException {

    logger.log(Level.FINE, "getComments()");

    List<CommentModel> list = new ArrayList<CommentModel>();
    
    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
    if(user.getUid() == null) {
      return null;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    TrainingManager trainingManager = TrainingManager.getInstance();
    
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
            if(!user.getUid().equals(c.getUid()) && user.getUid().equals(c.getUidTarget())) {
              Query qUnread = pm.newQuery(CommentsRead.class); 
              qUnread.setFilter("comment == commentParam && openId == openIdParam");
              qUnread.declareParameters("com.google.appengine.api.datastore.Key commentParam, java.lang.String openIdParam");
              qUnread.setRange(0, 1);
              List<CommentsRead> unreads = (List<CommentsRead>)qUnread.execute(cc.getKey(), user.getUid());
              c.setUnread(unreads.size() == 0);

              //mark this as read
              if(markAsRead && unreads.size() == 0) {
                CommentsRead cr = new CommentsRead();
                cr.setComment(cc.getKey());
                cr.setUid(user.getUid());
                pm.makePersistent(cr);
              }
            }
                  
            //if all comments -> don't return user's own comments
            if(target != null || !user.getUid().equals(c.getUid())) {
              
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
                Workout jdo = trainingManager.getWorkout(user, id);
                WorkoutModel w = Workout.getClientModel(jdo);
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
                  boolean hasPermission = hasPermission(pm, Permission.READ_TRAINING, user.getUid(), r.getUid());
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
                  boolean hasPermission = hasPermission(pm, Permission.READ_NUTRITION, user.getUid(), m.getUid());
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
                  boolean hasPermission = hasPermission(pm, Permission.READ_MEASUREMENTS, user.getUid(), m.getUid());
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
                  boolean hasPermission = hasPermission(pm, Permission.READ_CARDIO, user.getUid(), m.getUid());
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
                  boolean hasPermission = hasPermission(pm, Permission.READ_CARDIO, user.getUid(), m.getUid());
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
  @Override
  public List<Double> getEnergyInCalendar(Date dateStart, Date dateEnd) throws ConnectionException {
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    NutritionManager nutritionManager = NutritionManager.getInstance();

    return nutritionManager.getTotalEnergy(user, dateStart, dateEnd, user.getUid());
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
  @Deprecated public List<ExerciseModel> getExercisesFromName(Long nameId, Date dateStart, Date dateEnd, int limit) throws ConnectionException {

    logger.log(Level.FINE, "getExercisesFromName()");
        
    List<ExerciseModel> list = new ArrayList<ExerciseModel>();
    
    if(dateStart == null && dateEnd == null) {
      return list;
    }
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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

    UserOpenid user = userManager.getUser(perThreadRequest);
    NutritionManager nutritionManager = NutritionManager.getInstance();
    
    FoodName jdo = nutritionManager.getFoodName(user, id);                  
    return FoodName.getClientModel(jdo);
  }
  
  /**
   * Returns all foods from single meal
   * @param meal
   * @return foods
   * @throws ConnectionException 
   */
  /*@Override
  @Deprecated public List<FoodModel> getFoods(MealModel meal) throws ConnectionException {

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

    NutritionManagerOld nutritionManager = NutritionManagerOld.getInstance();

    try {      
      //if in time
      if(meal.getTimeId() != 0) {
        TimeModel time = nutritionManager.getTimeModel(meal.getTimeId(), UID);
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
        MealModel m = nutritionManager.getMealModel(meal.getId(), UID);
        list = m.getFoods();
      }
      
    } catch (Exception e) {
      throw new ConnectionException("getFoods", e.getMessage());
    }
    
    return list;
    
  }*/

  /**
   * Returns user all facebook friends that have logged to motiver
   * @return
   * @throws ConnectionException 
   */
  @Deprecated public List<UserModel> getFriends() throws ConnectionException {

    logger.log(Level.FINE, "getFriends()");
    
    List<UserModel> list = new ArrayList<UserModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  public Boolean addUserToCircle(int target, String uid) throws ConnectionException {

    UserOpenid user = userManager.getUser(perThreadRequest);

    Circle circle = new Circle(target, user.getUid(), uid);
    
    userManager.addUserToCircle(user, circle);
    
    return (circle != null);
  }

  
  /**
   * Sets single user's permission to view current user data
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  public Boolean removeUserFromCircle(int target, String uid) throws ConnectionException {

    UserOpenid user = userManager.getUser(perThreadRequest);
    
    userManager.removeUserFromCircle(user, target, uid);
    
    return true;
  }
  
  /**
   * Returns single meal
   * @param mealId
   * @return
   * @throws ConnectionException 
   */
  @Override public MealModel getMeal(Long mealId) throws ConnectionException {

    UserOpenid user = userManager.getUser(perThreadRequest);
    NutritionManager nutritionManager = NutritionManager.getInstance();
    
    MealJDO jdo = nutritionManager.getMeal(user, mealId);                  
    return MealJDO.getClientModel(jdo);
  }

  /**
   * Returns meals from time
   * @param time : if 0 -> we return all the meals not in calendar
   * @return meal' models
   * @throws ConnectionException 
   */
  @Override
  public List<MealModel> getMeals(int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading meals");
    }

    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
    
    List<MealModel> list = new ArrayList<MealModel>();
      
    NutritionManager nutritionManager = NutritionManager.getInstance();
    List<MealJDO> meals = nutritionManager.getMeals(user, index, user.getUid());
    if(meals != null) {
      for(MealJDO m : meals) {
        list.add(MealJDO.getClientModel(m));
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
  @Deprecated public List<MeasurementModel> getMeasurements(int index) throws ConnectionException {

    logger.log(Level.FINE, "getMeasurements()");

    List<MeasurementModel> list = new ArrayList<MeasurementModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public MeasurementValueModel getMeasurementValue(Long measurementId) throws ConnectionException {

    logger.log(Level.FINE, "getMeasurementValue()");
    
    MeasurementValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Measurement w = pm.getObjectById(Measurement.class, measurementId);
      if(w != null) {
        if(!hasPermission(pm, Permission.READ_MEASUREMENTS, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_MEASUREMENTS, UID, w.getUid());
        }

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
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeasurementValue", e);
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
  @Deprecated public List<MeasurementValueModel> getMeasurementValues(MeasurementModel measurement, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getMeasurementValues()");

    List<MeasurementValueModel> list = new ArrayList<MeasurementValueModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
        if(!hasPermission(pm, Permission.READ_MEASUREMENTS, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_MEASUREMENTS, UID, w.getUid());
        }
        
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
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getMeasurementValues", e);
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
  @Deprecated public List<MicroNutrientModel> getMicroNutrientsInCalendar(String uid, Date date) throws ConnectionException {

    logger.log(Level.FINE, "getMicroNutrientsInCalendar()");
    
    List<MicroNutrientModel> list = new ArrayList<MicroNutrientModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return list;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      
      //check permission
      if(!hasPermission(pm, Permission.READ_NUTRITION_FOODS, UID, uid)) {
        throw new NoPermissionException(Permission.READ_NUTRITION_FOODS, UID, uid);
      }
    
      //strip time
      final Date dStart = stripTime(date, true);
      final Date dEnd = stripTime(date, false);
      
      //get times
      Query q = pm.newQuery(TimeJDO.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<TimeJDO> times = (List<TimeJDO>) q.execute(uid, dStart, dEnd);

      //each time
      for(TimeJDO t : times) {

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
          for(FoodJDO food : t.getFoods()) {
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
  @Deprecated public MonthlySummaryModel getMonthlySummary(Long id) throws ConnectionException {

    logger.log(Level.FINE, "getMonthlySummary()");
    
    MonthlySummaryModel model = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public List<MonthlySummaryModel> getMonthlySummaries() throws ConnectionException {

    logger.log(Level.FINE, "getMonthlySummaries()");
    
    List<MonthlySummaryModel> list = new ArrayList<MonthlySummaryModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override
  public List<MealModel> getMostPopularMeals(int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading most popular meals");
    }

    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    NutritionManager nutritionManager = NutritionManager.getInstance();

    List<MealJDO> meals = nutritionManager.getMostPopularMeals(user, index);

    List<MealModel> list = new ArrayList<MealModel>();
    if(meals != null) {
      for(MealJDO m : meals) {
        list.add(MealJDO.getClientModel(m));
      }
    }
    
    return list;
  }

  /**
   * Returns most popular
   * @return routines' models
   */
  @Override
  public List<RoutineModel> getMostPopularRoutines(int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading most popular routines");
    }

    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    TrainingManager trainingManager = TrainingManager.getInstance();

    List<Routine> routines = trainingManager.getMostPopularRoutines(user, index);

    List<RoutineModel> list = new ArrayList<RoutineModel>();
    if(routines != null) {
      for(Routine m : routines) {
        list.add(Routine.getClientModel(m));
      }
    }
    
    return list;
  }

  /**
   * Returns most popular workouts
   * @return workouts' models
   */
  @Override
  public List<WorkoutModel> getMostPopularWorkouts(int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading most popular workouts");
    }

    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    TrainingManager trainingManager = TrainingManager.getInstance();

    List<Workout> workouts = trainingManager.getMostPopularWorkouts(user, index);

    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    if(workouts != null) {
      for(Workout m : workouts) {
        list.add(Workout.getClientModel(m));
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

    UserOpenid user = userManager.getUser(perThreadRequest);
    TrainingManager trainingManager = TrainingManager.getInstance();
    
    Routine jdo = trainingManager.getRoutine(user, routineId);                  
    return Routine.getClientModel(jdo);
  }

  /**
   * Returns all routines that aren't in calendar
   * @return routines' models
   */
  @Override
  public List<RoutineModel> getRoutines(int index) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading routines");
    }

    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);

    TrainingManager trainingManager = TrainingManager.getInstance();

    List<Routine> routines = trainingManager.getRoutines(user, index, user.getUid());

    List<RoutineModel> list = new ArrayList<RoutineModel>();
    if(routines != null) {
      for(Routine m : routines) {
        list.add(Routine.getClientModel(m));
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
  @Deprecated public List<RunModel> getRuns(int index) throws ConnectionException {

    logger.log(Level.FINE, "getRuns()");

    List<RunModel> list = new ArrayList<RunModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public RunValueModel getRunValue(Long runId) throws ConnectionException {

    logger.log(Level.FINE, "getRunValue()");
    
    RunValueModel m = null;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      final Run w = pm.getObjectById(Run.class, runId);
      if(w != null) {
        if(!hasPermission(pm, Permission.READ_CARDIO, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_CARDIO, UID, w.getUid());
        }

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
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRunValue", e);
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
  @Deprecated public List<RunValueModel> getRunValues(RunModel run, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getRunValues()");
    
    List<RunValueModel> list = new ArrayList<RunValueModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
        //check permission
        if(!hasPermission(pm, Permission.READ_CARDIO, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_CARDIO, UID, w.getUid());
        }
        
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
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "getRunValues", e);
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
  @Deprecated public List<ExerciseNameModel> getStatisticsTopExercises(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTopExercises()");

    List<ExerciseNameModel> list = new ArrayList<ExerciseNameModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public List<MealModel> getStatisticsTopMeals(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTopMeals()");

    List<MealModel> list = new ArrayList<MealModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return list;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {

      //strip dates
      final Date dStart = stripTime(dateStart, true);
      final Date dEnd = stripTime(dateEnd, false);

      Query q = pm.newQuery(TimeJDO.class);
      q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
      q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
      List<TimeJDO> times = (List<TimeJDO>) q.execute(UID, dStart, dEnd);
      
      List<MyListItem> listIds = new ArrayList<MyListItem>();
      
      //go through each workouts' exercises and calculate count for each nameId
      //TODO huge server load??
      for(TimeJDO t : times) {
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
  @Deprecated public int[] getStatisticsTrainingDays(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTrainingDays()");
    
    int[] count = new int[] {0, 0, 0, 0, 0, 0, 0};
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Deprecated public int[] getStatisticsTrainingTimes(Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getStatisticsTrainingTimes()");
    
    int[] count = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override
  public List<TimeModel> getTimesInCalendar(String uid, Date date) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading times for "+date);
    }

    List<TimeModel> list = new ArrayList<TimeModel>();
    
    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    NutritionManager nutritionManager = NutritionManager.getInstance();
    List<TimeJDO> times = nutritionManager.getTimes(user, date, uid);
    if(times != null) {
      for(TimeJDO m : times) {
        list.add(TimeJDO.getClientModel(m));
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
  @Deprecated public List<UserModel> getTrainees() throws ConnectionException {

    logger.log(Level.FINE, "getTrainees()");

    List<UserModel> list = new ArrayList<UserModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
        UserModel u = UserOpenid.getClientModel(userManager.getUser(users.get(0).getUid()));
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
  @Deprecated public Boolean removeCardio(CardioModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeCardio()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  @Override @Deprecated public Boolean removeCardioValues(CardioModel model, List<CardioValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "removeCardioValues()");

    if(values.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Cardio w = pm.getObjectById(Cardio.class, model.getId());
      
      if(w != null) {
        if(!hasPermission(pm, Permission.READ_CARDIO, UID, w.getUid())) {
          throw new NoPermissionException(Permission.READ_CARDIO, UID, w.getUid());
        }
          
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
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeCardioValues", e);
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
  @Deprecated public boolean removeComments(List<CommentModel> comments) throws ConnectionException {

    logger.log(Level.FINE, "removeComments()");

    if(comments.size() < 1) {
      return false;
    }
    
    boolean ok = true;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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

    boolean ok = true;
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    try {
      TrainingManager trainingManager = TrainingManager.getInstance();
      
      for(ExerciseModel exercise : exercises) {
        Exercise jdo = Exercise.getServerModel(exercise);
        boolean res = trainingManager.removeExercise(user, jdo, exercise.getWorkoutId());
        
        if(!res) {
          ok = false;
        }
      }

    }
    catch (Exception e) {
      throw new ConnectionException("addExercise", e.getMessage());
    }
    
    return ok;
  }
  
  /**
   * Delete foods
   * @return delete successfull
   */
  @Override
  public boolean removeFoods(List<FoodModel> foods) throws ConnectionException {

    boolean ok = true;
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    try {
      NutritionManager nutritionManager = NutritionManager.getInstance();
      
      for(FoodModel food : foods) {
        FoodJDO jdo = FoodJDO.getServerModel(food);
        boolean res = nutritionManager.removeFood(user, jdo, food.getTimeId(), food.getMealId());
        
        if(!res) {
          ok = false;
        }
      }

    }
    catch (Exception e) {
      throw new ConnectionException("addFood", e.getMessage());
    }
    
    return ok;
  }

  /**
   * Deletes list of guide values
   * @param list
   * @return
   */
  @Override @Deprecated public Boolean removeGuideValues(List<GuideValueModel> list) throws ConnectionException {

    logger.log(Level.FINE, "removeGuideValues()");

    if(list.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
          if(!hasPermission(pm, Permission.WRITE_NUTRITION, UID, mServer.getUid())) {
            throw new NoPermissionException(Permission.WRITE_NUTRITION, UID, mServer.getUid());
          }
          
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
    
    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
    
    NutritionManager nutritionManager = NutritionManager.getInstance();
    
    MealJDO jdo = MealJDO.getServerModel(model);
    boolean ok = nutritionManager.removeMeal(user, jdo, model.getTimeId());
    
    return ok;
  }
  
  /**
   * Removes measurement
   * @param model to remove
   * @return remove successful
   */
  @Override
  @Deprecated public Boolean removeMeasurement(MeasurementModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeMeasurement()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Measurement m = pm.getObjectById(Measurement.class, model.getId());
      if(m != null) {
        if(!hasPermission(pm, Permission.WRITE_MEASUREMENTS, UID, m.getUid())) {
          throw new NoPermissionException(Permission.WRITE_MEASUREMENTS, UID, m.getUid());
        }
        
        pm.deletePersistent(m);
        
        ok = true;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeMeasurement", e);
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
  @Override @Deprecated public Boolean removeMeasurementValues(MeasurementModel model, List<MeasurementValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "removeMeasurementValues()");

    if(values.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Measurement w = pm.getObjectById(Measurement.class, model.getId());
      
      if(w != null) {
        if(!hasPermission(pm, Permission.WRITE_MEASUREMENTS, UID, w.getUid())) {
          throw new NoPermissionException(Permission.WRITE_MEASUREMENTS, UID, w.getUid());
        }
          
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
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeMeasurementValues", e);
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
  @Deprecated public Boolean removeRoutine(RoutineModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeRoutine()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    TrainingManager trainingManager = TrainingManager.getInstance();
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Routine r = pm.getObjectById(Routine.class, model.getId());
      if(r != null) {
        if(!hasPermission(pm, Permission.WRITE_TRAINING, user.getUid(), r.getUid())) {
          throw new NoPermissionException(Permission.WRITE_TRAINING, user.getUid(), r.getUid());
        }
        
        pm.deletePersistent(r);
        
        //remove also workouts which belongs to this routine
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && routineId == routineIdParam");
        q.declareParameters("java.lang.String openIdParam, java.lang.Long routineIdParam");
        List<Workout> workouts = (List<Workout>) q.execute(user.getUid(), r.getId());

        ok = trainingManager.removeWorkouts(user, workouts);
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeRoutine", e);
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
  @Deprecated public Boolean removeRun(RunModel model) throws ConnectionException {

    logger.log(Level.FINE, "removeRun()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      Run m = pm.getObjectById(Run.class, model.getId());
      if(m != null) {
        if(!hasPermission(pm, Permission.WRITE_CARDIO, UID, m.getUid())) {
          throw new NoPermissionException(Permission.WRITE_CARDIO, UID, m.getUid());
        }
        
        pm.deletePersistent(m);
        
        ok = true;
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "removeRun", e);
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
  @Override @Deprecated public Boolean removeRunValues(RunModel model, List<RunValueModel> values) throws ConnectionException {

    logger.log(Level.FINE, "removeRunValues()");

    if(values.size() < 1) {
      return false;
    }
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      
      Run w = pm.getObjectById(Run.class, model.getId());
      
      if(w != null) {
        if(!hasPermission(pm, Permission.WRITE_CARDIO, UID, w.getUid())) {
          throw new NoPermissionException(Permission.WRITE_CARDIO, UID, w.getUid());
        }
          
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
    catch (Exception e) {
      logger.log(Level.SEVERE, "removeRunValues", e);
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
  @Override
  public Boolean removeTimes(TimeModel[] models) throws ConnectionException {
        
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    if(user == null) {
      return false;
    }
    final String UID = user.getUid();
    
    NutritionManager nutritionManager = NutritionManager.getInstance();
    
    List<TimeJDO> jdos = new ArrayList<TimeJDO>();
    for(TimeModel t : models) {
      jdos.add(TimeJDO.getServerModel(t));
    }
    
    return nutritionManager.removeTimes(jdos, UID);
  }
  
  /**
   * Removes workout
   * @param model to remove
   * @return removed successfull
   */
  @Override
  public Boolean removeWorkout(WorkoutModel model) throws ConnectionException {

    UserOpenid user = userManager.getUser(perThreadRequest);
    TrainingManager trainingManager = TrainingManager.getInstance();
    
    List<Workout> list = new ArrayList<Workout>();
    list.add(Workout.getServerModel(model));
    Boolean ok = trainingManager.removeWorkouts(user, list);
    
    return ok;
  }
  
  /**
   * Saves access_token which will be used for confirming the current user
   * @return
   * @throws ConnectionException 
   */
  @Override
  @Deprecated public UserModel saveToken() throws ConnectionException {

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


    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    
    
    UserOpenid jdo = UserOpenid.getServerModel(u);
    userManager.saveUser(user, jdo);
    u = UserOpenid.getClientModel(jdo);
    
    return u;
  }
  
  /**
   * Returns all exercise names
   * @return names' models
   */
  @Override
  public List<ExerciseNameModel> searchExerciseNames(String query, int limit) throws ConnectionException {

    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    TrainingManager trainingManager = TrainingManager.getInstance();    
    List<ExerciseName> jdoList = trainingManager.searchExerciseNames(user, query, limit);
    
    //convert to client side models
    List<ExerciseNameModel> list = new ArrayList<ExerciseNameModel>();
    for(ExerciseName n : jdoList) {
      list.add(ExerciseName.getClientModel(n));
    }
    
    return list;
    
  }
  
  /**
   * Search food names
   * @return names' models
   */
  @Override
  public List<FoodNameModel> searchFoodNames(String query, int limit) throws ConnectionException {

    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    NutritionManager nutritionManager = NutritionManager.getInstance();    
    List<FoodName> jdoList = nutritionManager.searchFoodNames(user, query, limit);
    
    //convert to client side models
    List<FoodNameModel> list = new ArrayList<FoodNameModel>();
    for(FoodName n : jdoList) {
      list.add(FoodName.getClientModel(n));
    }
    
    return list;
    
  }

  /**
   * Search meals from other users
   * @return meals' models
   * @throws ConnectionException 
   */
  @Override
  public List<MealModel> searchMeals(int index, String query) throws ConnectionException {


    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    NutritionManager nutritionManager = NutritionManager.getInstance();    
    List<MealJDO> jdoList = nutritionManager.searchMeals(user, query, index);
    
    //convert to client side models
    List<MealModel> list = new ArrayList<MealModel>();
    for(MealJDO n : jdoList) {
      list.add(MealJDO.getClientModel(n));
    }
    
    return list;
  }
  
  /**
   * Search routines from other users
   * @return routines' models
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  @Override
  @Deprecated public List<RoutineModel> searchRoutines(int index, String query) throws ConnectionException {

    logger.log(Level.FINE, "searchRoutines()");
    
    List<RoutineModel> list = new ArrayList<RoutineModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
          
          boolean hasPermission = hasPermission(pm, Permission.READ_TRAINING, UID, r.getUid());
          
          if(hasPermission) {
            RoutineModel m = Routine.getClientModel(r);
            list.add(m);
            
            i++;
          }
          
        }
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "searchRoutines", e);
      throw new ConnectionException("searchRoutines", e.getMessage());
    }
    finally {
      if (!pm.isClosed()) {
        pm.close();
      } 
    }

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, " query: "+query+", results: "+list.size());
    }
    
    return list;
  }
  
  /**
   * Search workouts from other users
   * @return workouts' models
   * @throws ConnectionException 
   */
  @Override
  public List<WorkoutModel> searchWorkouts(int index, String query) throws ConnectionException {


    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    TrainingManager trainingManager = TrainingManager.getInstance();    
    List<Workout> jdoList = trainingManager.searchWorkouts(user, query, index);
    
    //convert to client side models
    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    for(Workout n : jdoList) {
      list.add(Workout.getClientModel(n));
    }
    
    return list;
  }
  
  
  /**
   * Search users
   * @param index
   * @param query
   * @return
   * @throws ConnectionException
   */
  public List<UserModel> searchUsers(int index, String query) throws ConnectionException {


    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    List<UserOpenid> jdoList = userManager.searchUsers(user, query, index);
    
    //convert to client side models
    List<UserModel> list = new ArrayList<UserModel>();
    for(UserOpenid n : jdoList) {
      list.add(UserOpenid.getClientModel(n));
    }
    
    return list;
  }
  
  
  /**
   * Get users from circle
   * @param target
   * @return
   * @throws ConnectionException
   */
  public List<UserModel> getUsersFromCircle(int target) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading user from circle: "+target);
    }

    List<UserModel> list = new ArrayList<UserModel>();
    
    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    List<UserOpenid> users = userManager.getUsersFromCircle(user, target);
    if(users != null) {
      for(UserOpenid m : users) {
        list.add(UserOpenid.getClientModel(m));
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
  @Deprecated public Boolean updateCardio(CardioModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateCardio()");

    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Cardio m = pm.getObjectById(Cardio.class, model.getId());
      if(m != null) {
        if(!hasPermission(pm, Permission.WRITE_CARDIO, UID, m.getUid())) {
          throw new NoPermissionException(Permission.WRITE_CARDIO, UID, m.getUid());
        }
          
        //update
        m.setName(model.getNameServer());
        
        ok = true;
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateCardio", e);
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

    ExerciseModel m = null;
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    try {
      TrainingManager trainingManager = TrainingManager.getInstance();
      Exercise jdo = Exercise.getServerModel(exercise);
      trainingManager.addExercise(user, jdo, exercise.getWorkoutId());
      m = Exercise.getClientModel(jdo);

    }
    catch (Exception e) {
      throw new ConnectionException("addExercise", e.getMessage());
    }
    
    return m;
  }

  /**
   * Updates exercise name
   * @param exercise : model to be updated
   * @return updated exercise name (null if add not successful)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Deprecated public Boolean updateExerciseName(ExerciseNameModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateExerciseName()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }

    //if not admin
    if( !isAdmin(UID) ) {
      return false;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
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
    
    boolean ok = true;

    final UserOpenid user = userManager.getUser(this.perThreadRequest);
    
    TrainingManager trainingManager = TrainingManager.getInstance();
    
    ok = trainingManager.updateExerciseOrder(user, workout.getId(), ids);
    
    return ok;
  }

  /**
   * Updates food
   * @param food : model to be updated
   * @return updated food (null if add not successful)
   */
  @Override
  public FoodModel updateFood(FoodModel food) throws ConnectionException {

    FoodModel m = null;
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    try {
      NutritionManager nutritionManager = NutritionManager.getInstance();
      FoodJDO jdo = FoodJDO.getServerModel(food);
      nutritionManager.addFood(user, jdo, food.getTimeId(), food.getMealId());
      m = FoodJDO.getClientModel(jdo);

    }
    catch (Exception e) {
      throw new ConnectionException("addFood", e.getMessage());
    }
    
    return m;
  }
  
  /**
   * Updates exercise name
   * @param exercise : model to be updated
   * @return updated exercise name (null if add not successful)
   */
  @SuppressWarnings("unchecked")
  @Override
  @Deprecated public Boolean updateFoodName(FoodNameModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateFoodName()");
    
    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
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
  public Boolean updateMeal(MealModel model) throws ConnectionException {
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    NutritionManager nutritionManager = NutritionManager.getInstance();
    MealJDO jdo = MealJDO.getServerModel(model);
    
    nutritionManager.updateMeal(user, jdo);

    return true;
  }

  /**
   * Updates measurement
   * @param model to be updated
   * @return update successful
   */
  @Override
  @Deprecated public Boolean updateMeasurement(MeasurementModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateMeasurement()");

    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Measurement m = pm.getObjectById(Measurement.class, model.getId());
      if(m != null) {
        if(!hasPermission(pm, Permission.WRITE_MEASUREMENTS, UID, m.getUid())) {
          throw new NoPermissionException(Permission.WRITE_MEASUREMENTS, UID, m.getUid());
        }
          
          //update
        m.setName(model.getNameServer());
        m.setUnit(model.getUnit());
        m.setDate(model.getDate());
        m.setTarget(model.getTarget());
        
        ok = true;
      }
    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateMeasurement", e);
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
  @Override
  public Boolean updateRoutine(RoutineModel model) throws ConnectionException {
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    TrainingManager trainingManager = TrainingManager.getInstance();
    Routine jdo = Routine.getServerModel(model);
    
    trainingManager.updateRoutine(user, jdo);

    return true;
  }
  
  /**
   * Updates run
   * @param model to be updated
   * @return update successful
   */
  @Override
  @Deprecated public Boolean updateRun(RunModel model) throws ConnectionException {

    logger.log(Level.FINE, "updateRun()");

    boolean ok = false;
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return ok;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Run m = pm.getObjectById(Run.class, model.getId());
      if(m != null) {
        if(!hasPermission(pm, Permission.WRITE_CARDIO, UID, m.getUid())) {
          throw new NoPermissionException(Permission.WRITE_CARDIO, UID, m.getUid());
        }
        
        //update
        m.setName(model.getNameServer());
        m.setDistance(model.getDistance());
        
        ok = true;
      }

    }
    catch (Exception e) {
      logger.log(Level.SEVERE, "updateRun", e);
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
  @Override
  public Boolean updateTime(TimeModel model) throws ConnectionException {
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    NutritionManager nutritionManager = NutritionManager.getInstance();
    TimeJDO jdo = TimeJDO.getServerModel(model);
    
    nutritionManager.updateTime(user, jdo);

    return true;
  }
  
  /**
   * Updates workout model
   * @param model to be updated
   * @return update successfull
   */
  @Override
  public Boolean updateWorkout(WorkoutModel model) throws ConnectionException {
    
    //get user
    final UserOpenid user = userManager.getUser(this.perThreadRequest);

    TrainingManager trainingManager = TrainingManager.getInstance();
    Workout jdo = Workout.getServerModel(model);
    
    trainingManager.updateWorkout(user, jdo);

    return true;
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

    UserOpenid user = userManager.getUser(perThreadRequest);
    TrainingManager trainingManager = TrainingManager.getInstance();
    
    Workout jdo = trainingManager.getWorkout(user, workoutId);                  
    return Workout.getClientModel(jdo);
  }

  /**
   * Returns all workouts that aren't in calendar
   * @param routine : if null returns all workouts
   * @return workouts' models (if routine set -> also exercises are returned)
   * @throws ConnectionException 
   */
  @Override
  public List<WorkoutModel> getWorkouts(int index, RoutineModel routine) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading workouts");
    }

    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);

    TrainingManager trainingManager = TrainingManager.getInstance();

    List<Workout> workouts = null;
    if(routine == null) {
      workouts = trainingManager.getWorkouts(user, index, user.getUid());
    }
    //from routine
    else {
      workouts = trainingManager.getWorkouts(user, Routine.getServerModel(routine), user.getUid());
    }

    List<WorkoutModel> list = new ArrayList<WorkoutModel>();
    if(workouts != null) {
      for(Workout m : workouts) {
        list.add(Workout.getClientModel(m));
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
  @Override
  public List<WorkoutModel[]> getWorkoutsInCalendar(String uid, Date dateStart, Date dateEnd) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading workouts for "+dateStart+" - "+dateEnd);
    }

    List<WorkoutModel[]> list = new ArrayList<WorkoutModel[]>();
    
    //get user
    UserOpenid user = userManager.getUser(perThreadRequest);
      
    TrainingManager trainingManager = TrainingManager.getInstance();

    //go through days
    final int days = (int)((dateEnd.getTime() - dateStart.getTime()) / (24 * 60 * 60 * 1000)) + 1;
    
    for(int i=0; i < days; i++) {
      final Date d = new Date((dateStart.getTime() / 1000 + 3600 * 24 * i) * 1000);
            
      List<Workout> workouts = trainingManager.getWorkouts(user, d, uid);
      if(workouts != null) {
        WorkoutModel[] arr = new WorkoutModel[workouts.size()];
        
        int j = 0;
        for(Workout m : workouts) {
          arr[j] = Workout.getClientModel(m);
          j++;
        }
        
        list.add(arr);
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
  @Deprecated public List<GuideValueModel> getGuideValues(String uid, int index, Date date) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Loading guide values: "+date);
    }

    List<GuideValueModel> list = new ArrayList<GuideValueModel>();
    
    //get uid
    UserOpenid user = userManager.getUser(perThreadRequest);
    final String UID = user.getUid();
    if(UID == null) {
      return null;
    }

    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      
      //check permission
      if(!hasPermission(pm, Permission.READ_NUTRITION, UID, uid)) {
        throw new NoPermissionException(Permission.READ_TRAINING, UID, uid);
      }    
    
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
      logger.log(Level.SEVERE, "Error loading guide values", e);
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
  @Deprecated public boolean hasTraining(String uid, Date date) throws ConnectionException {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Checking if date '"+date+"' has training");
    }
  
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
      //update TimeJDO
      if(arr != null && arr.size() > 0) {
          found = true;
      }

      //update TimeJDO
      if(found) {
        id = arr.get(0).getId();
      }
      //create new
      else {
        FoodName mServer = FoodName.getServerModel(food.getName());

        //get uid
        UserManager userManager = UserManager.getInstance();
        UserOpenid user = userManager.getUser(perThreadRequest);
        final String UID = user.getUid();
        if(UID == null) {
          return id;
        }
        
        mServer.setUid(UID);
        FoodName added = pm.makePersistent(mServer);
        id = added.getId();
      }
      
    } catch (Exception e) {
      logger.log(Level.SEVERE, "fetchAddFoodName", e);
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
