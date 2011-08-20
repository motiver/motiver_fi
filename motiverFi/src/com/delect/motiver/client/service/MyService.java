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
package com.delect.motiver.client.service;

import java.util.Date;
import java.util.List;

import com.delect.motiver.shared.BlogData;
import com.delect.motiver.shared.CardioModel;
import com.delect.motiver.shared.CardioValueModel;
import com.delect.motiver.shared.CommentModel;
import com.delect.motiver.shared.ConnectionException;
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
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;
import com.delect.motiver.shared.TicketModel;
import com.delect.motiver.shared.TimeModel;
import com.delect.motiver.shared.UserModel;
import com.delect.motiver.shared.WorkoutModel;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("myServiceImpl")
public interface MyService extends RemoteService {

  
  public UserModel getUser() throws ConnectionException;
  
  /**
   * Adds cardio to db.
   *
   * @param cardio : model to be added
   * @return added cardio (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public CardioModel addCardio(CardioModel cardio) throws ConnectionException;
  
  /**
   * Adds cardio's value to db.
   *
   * @param cardio : cardio where we add this value
   * @param value : value to be added
   * @return added cardio (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public CardioValueModel addCardioValue(CardioModel cardio, CardioValueModel value) throws ConnectionException;
  
  /**
   * Adds comment to db.
   *
   * @param comment : model to be added
   * @return added comment (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public CommentModel addComment(CommentModel comment) throws ConnectionException;

  /**
   * Adds exercise to (workout).
   *
   * @param exercise : model to be added
   * @return added exercise (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public ExerciseModel addExercise(ExerciseModel exercise) throws ConnectionException;
  
  /**
   * Creates / updates exercisename (updates if already found).
   *
   * @param name the name
   * @return added exercise (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public ExerciseNameModel addExercisename(ExerciseNameModel name) throws ConnectionException;
  
  /**
   * Adds food to (meal).
   *
   * @param food : model to be added
   * @return added food (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public FoodModel addFood(FoodModel food) throws ConnectionException;
  
  /**
   * Creates / updates foodname (updates if already found).
   *
   * @param name : model to be added
   * @return added name (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public FoodNameModel addFoodname(FoodNameModel name) throws ConnectionException;
  
  /**
   * Creates / updates foodnames (updates if already found).
   *
   * @param names : models to be added
   * @return added names (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public List<FoodNameModel> addFoodnames(List<FoodNameModel> names) throws ConnectionException;
  
  /**
   * Adds guide value.
   *
   * @param model the model
   * @return the guide value model
   * @throws ConnectionException the connection exception
   */
  public GuideValueModel addGuideValue(GuideValueModel model) throws ConnectionException;
  
  /**
   * Adds meal to db.
   *
   * @param meal : model to be added (including timeId if set)
   * @return added meal (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public MealModel addMeal(MealModel meal) throws ConnectionException;
  
  /**
   * Adds meal from time to db.
   *
   * @param meal : model to be added
   * @param timeId the time id
   * @return added meal (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public MealModel addMeal(MealModel meal, Long timeId) throws ConnectionException;
  
  /**
   * Adds meals to db.
   *
   * @param meals : models to be added (including timeId if set)
   * @return added meal (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public List<MealModel> addMeals(List<MealModel> meals) throws ConnectionException;
  
  
  /**
   * Adds measurement to db.
   *
   * @param measurement : model to be added
   * @return added measurement (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public MeasurementModel addMeasurement(MeasurementModel measurement) throws ConnectionException;
  
  
  /**
   * Adds measurement's value to db.
   *
   * @param measurement : measurement where we add this value
   * @param value : value to be added
   * @return added measurement (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public MeasurementValueModel addMeasurementValue(MeasurementModel measurement, MeasurementValueModel value) throws ConnectionException;
  
  /**
   * Adds routine to db.
   *
   * @param routine : model to be added
   * @return added workout (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public RoutineModel addRoutine(RoutineModel routine) throws ConnectionException;
  
  /**
   * Adds routines to db.
   *
   * @param routines : models to be added
   * @return added routines (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public List<RoutineModel> addRoutines(List<RoutineModel> routines) throws ConnectionException;
  
  /**
   * Adds run to db.
   *
   * @param run : model to be added
   * @return added run (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public RunModel addRun(RunModel run) throws ConnectionException;
  
  /**
   * Adds run's value to db.
   *
   * @param run : run where we add this value
   * @param value : value to be added
   * @return added run (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public RunValueModel addRunValue(RunModel run, RunValueModel value) throws ConnectionException;

  
  /**
   * Adds ticket to db.
   *
   * @param ticket : model to be added
   * @return added ticket (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public TicketModel addTicket(TicketModel ticket) throws ConnectionException;

  
  /**
   * Adds time to db.
   *
   * @param time : model to be added
   * @return added time (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public TimeModel addTime(TimeModel time) throws ConnectionException;
  
  /**
   * Adds times to db.
   *
   * @param times : models to be added
   * @return added times (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public TimeModel[] addTimes(TimeModel[] times) throws ConnectionException;

  
  /**
   * Adds workout to db.
   *
   * @param workout : model to be added
   * @return added workout (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public WorkoutModel addWorkout(WorkoutModel workout) throws ConnectionException;
  
  
  /**
   * Adds workouts to db.
   *
   * @param workouts : models to be added
   * @return added workouts (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public List<WorkoutModel> addWorkouts(List<WorkoutModel> workouts) throws ConnectionException;
  
  
  /**
   * Combines exercise names together.
   *
   * @param firstId : where other IDs are combined
   * @param ids : other IDs
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean combineExerciseNames(Long firstId, Long[] ids) throws ConnectionException;
  
  
  /**
   * Combines food names together.
   *
   * @param firstId : where other IDs are combined
   * @param ids : other IDs
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean combineFoodNames(Long firstId, Long[] ids) throws ConnectionException;
  
  /**
   * Dummy.
   *
   * @param model the model
   * @return the boolean
   */
  public Boolean dummy(MicroNutrientModel model);
  
  /**
   * Dummy.
   *
   * @param model the model
   * @return the boolean
   */
  public MonthlySummaryExerciseModel dummy2(MonthlySummaryExerciseModel model);
  
  /**
   * Removes all data from user.
   *
   * @param removeTraining the remove training
   * @param removeCardio the remove cardio
   * @param removeNutrition the remove nutrition
   * @param removeMeasurements the remove measurements
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchRemoveAll(Boolean removeTraining, Boolean removeCardio, Boolean removeNutrition, Boolean removeMeasurements) throws ConnectionException;
  
  
  /**
   * Saves cardio.
   *
   * @param cardios the cardios
   * @param values (values for each cardio)
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveCardios(List<CardioModel> cardios, List<List<CardioValueModel>> values) throws ConnectionException;
  
  
  /**
   * Saves foods' names.
   *
   * @param names the names
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveFoodNames(List<FoodNameModel> names) throws ConnectionException;
  
  /**
   * Saves guide values (nutrition).
   *
   * @param values the values
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveGuideValues(List<GuideValueModel> values) throws ConnectionException;
  
  /**
   * Saves meals.
   *
   * @param meals the meals
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveMeals(List<MealModel> meals) throws ConnectionException;
  
  /**
   * Saves measurements.
   *
   * @param measurement the measurement
   * @param values (values for each measurement)
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveMeasurements(MeasurementModel measurement, List<MeasurementValueModel> values) throws ConnectionException;
  
  
  /**
   * Saves routins.
   *
   * @param routines the routines
   * @param workouts (workouts for each routine)
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveRoutines(List<RoutineModel> routines, List<List<WorkoutModel>> workouts) throws ConnectionException;
  
  
  /*
   * NUTRITION
   */
  
  
  /**
   * Saves runs.
   *
   * @param runs the runs
   * @param values (values for each run)
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveRuns(List<RunModel> runs, List<List<RunValueModel>> values) throws ConnectionException;
  
  /**
   * Saves times.
   *
   * @param times the times
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveTimes(List<TimeModel> times) throws ConnectionException;
  
  /**
   * Saves workouts.
   *
   * @param workouts the workouts
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean fetchSaveWorkouts(List<WorkoutModel> workouts) throws ConnectionException;
  
  /**
   * Gets data for blog.
   *
   * @param index the index
   * @param limit the limit
   * @param target the target
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param uid : if null use current user's uid
   * @param showEmptyDays : if empty days are returned
   * @return blog data for each day
   * @throws ConnectionException the connection exception
   */
  public List<BlogData> getBlogData(int index, int limit, int target, Date dateStart, Date dateEnd, String uid, Boolean showEmptyDays) throws ConnectionException;
  
  /**
   * Returns all cardios.
   *
   * @param index the index
   * @return cardios' models
   * @throws ConnectionException the connection exception
   */
  public List<CardioModel> getCardios(int index) throws ConnectionException;

  /**
   * Returns last value from given cardio.
   *
   * @param cardioId the cardio id
   * @return model
   * @throws ConnectionException the connection exception
   */
  public CardioValueModel getCardioValue(Long cardioId) throws ConnectionException;
  
  /**
   * Returns all values from single cardio.
   *
   * @param meal the meal
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return values
   * @throws ConnectionException the connection exception
   */
  public List<CardioValueModel> getCardioValues(CardioModel meal, Date dateStart, Date dateEnd) throws ConnectionException;
  
  /**
   * Returns last comments.
   *
   * @param index the index
   * @param limit the limit
   * @param target the target
   * @param uid the uid
   * @param markAsRead the mark as read
   * @return comments
   * @throws ConnectionException the connection exception
   */
  public List<CommentModel> getComments(int index, int limit, String target, String uid, boolean markAsRead) throws ConnectionException;
  
  /**
   * Get nutrition in calendar between dates.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return energies in each days
   * @throws ConnectionException the connection exception
   */
  public List<Double> getEnergyInCalendar(Date dateStart, Date dateEnd) throws ConnectionException;

  
  /**
   * Returns all exercises from single workout.
   *
   * @param workout the workout
   * @return exercises
   * @throws ConnectionException the connection exception
   */
  public List<ExerciseModel> getExercises(WorkoutModel workout) throws ConnectionException;

  
  /**
   * Returns all exercises from given "name". Used for fetching last weights
   *
   * @param nameId the name id
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param limit : -1 if no limit
   * @return exercises
   * @throws ConnectionException the connection exception
   */
  public List<ExerciseModel> getExercisesFromName(Long nameId, Date dateStart, Date dateEnd, int limit) throws ConnectionException;

  
  /**
   * Returns single food name.
   *
   * @param id the id
   * @return name
   * @throws ConnectionException the connection exception
   */
  public FoodNameModel getFoodname(Long id) throws ConnectionException;
  
  /**
   * Returns all foods from single meal.
   *
   * @param meal the meal
   * @return foods
   * @throws ConnectionException the connection exception
   */
  public List<FoodModel> getFoods(MealModel meal) throws ConnectionException;
  
  /**
   * Returns user all facebook friends that have logged to xlgain.
   *
   * @return the friends
   * @throws ConnectionException the connection exception
   */
  public List<UserModel> getFriends() throws ConnectionException;
  
  /**
   * Sets single user's permission to view given target
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  public Boolean addUserToCircle(int target, String uid) throws ConnectionException;
  
  /**
   * Removes single user's permission to view given target
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  public Boolean removeUserFromCircle(int target, String uid) throws ConnectionException;
  
  
  /**
   * Returns single meal.
   *
   * @param mealId the meal id
   * @return the meal
   * @throws ConnectionException the connection exception
   */
  public MealModel getMeal(Long mealId) throws ConnectionException;
  
  
  /**
   * Returns meals from time.
   *
   * @param index the index
   * @return meal' models
   * @throws ConnectionException the connection exception
   */
  public List<MealModel> getMeals(int index) throws ConnectionException;
  
  /**
   * Returns all measurements.
   *
   * @param index the index
   * @return measurements' models
   * @throws ConnectionException the connection exception
   */
  public List<MeasurementModel> getMeasurements(int index) throws ConnectionException;
  
  /**
   * Returns last value from given measurement.
   *
   * @param measurementId the measurement id
   * @return model
   * @throws ConnectionException the connection exception
   */
  public MeasurementValueModel getMeasurementValue(Long measurementId) throws ConnectionException;

  /**
   * Returns all values from single measurement.
   *
   * @param meal the meal
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return values
   * @throws ConnectionException the connection exception
   */
  public List<MeasurementValueModel> getMeasurementValues(MeasurementModel meal, Date dateStart, Date dateEnd) throws ConnectionException;
  
  /**
   * Get micronutrients from single day.
   *
   * @param uid the uid
   * @param date the date
   * @return micronutrients
   * @throws ConnectionException the connection exception
   */
  public List<MicroNutrientModel> getMicroNutrientsInCalendar(String uid, Date date) throws ConnectionException;
  
  /**
   * Returns single monthly summary
   * @param id
   * @return
   * @throws ConnectionException
   */
  public MonthlySummaryModel getMonthlySummary(Long id) throws ConnectionException;
  
  /**
   * Returns saved monthly summaries
   * @return
   * @throws ConnectionException
   */
  public List<MonthlySummaryModel> getMonthlySummaries() throws ConnectionException;
  
  /**
   * Returns most popular meals.
   *
   * @param index the index
   * @return meals' models
   * @throws ConnectionException the connection exception
   */
  public List<MealModel> getMostPopularMeals(int index) throws ConnectionException;
  
  
  /**
   * Returns most popular routines.
   *
   * @param index the index
   * @return routines' models
   * @throws ConnectionException the connection exception
   */
  public List<RoutineModel> getMostPopularRoutines(int index) throws ConnectionException;

  /**
   * Returns most popular workouts.
   *
   * @param index the index
   * @return workouts' models
   * @throws ConnectionException the connection exception
   */
  public List<WorkoutModel> getMostPopularWorkouts(int index) throws ConnectionException;
  
  /**
   * Returns single routine.
   *
   * @param routineId the routine id
   * @return the routine
   * @throws ConnectionException the connection exception
   */
  public RoutineModel getRoutine(Long routineId) throws ConnectionException;
  
  /**
   * Returns all routines that aren't in calendar.
   *
   * @param index the index
   * @return routines' models
   * @throws ConnectionException the connection exception
   */
  public List<RoutineModel> getRoutines(int index) throws ConnectionException;
  
  
  /*
   * MEASUREMENT
   */ 
  
  /**
   * Returns all runs.
   *
   * @param index the index
   * @return runs' models
   * @throws ConnectionException the connection exception
   */
  public List<RunModel> getRuns(int index) throws ConnectionException;
  
  /**
   * Returns last value from given run.
   *
   * @param runId the run id
   * @return model
   * @throws ConnectionException the connection exception
   */
  public RunValueModel getRunValue(Long runId) throws ConnectionException;
  
  /**
   * Returns all values from single run.
   *
   * @param meal the meal
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return values
   * @throws ConnectionException the connection exception
   */
  public List<RunValueModel> getRunValues(RunModel meal, Date dateStart, Date dateEnd) throws ConnectionException;
  
  /**
   * Gets top ten exercises.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return exercises array, each model have "count" value which has total count
   * @throws ConnectionException the connection exception
   */
  public List<ExerciseNameModel> getStatisticsTopExercises(Date dateStart, Date dateEnd) throws ConnectionException;

  /**
   * Gets top ten meals.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return meals array, each model have "count" value which has total count
   * @throws ConnectionException the connection exception
   */
  public List<MealModel> getStatisticsTopMeals(Date dateStart, Date dateEnd) throws ConnectionException;

  /**
   * Gets statistics for each day.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return array : training count for each day (monday-friday)
   * @throws ConnectionException the connection exception
   */
  public int[] getStatisticsTrainingDays(Date dateStart, Date dateEnd) throws ConnectionException;
  
  /**
   * Gets statistics for each day.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return array : training count for each time (00-03, 03-06, 06-09, 09-12, 12-15, 15-18, 18-21, 21-24)
   * @throws ConnectionException the connection exception
   */
  public int[] getStatisticsTrainingTimes(Date dateStart, Date dateEnd) throws ConnectionException;
  
  /**
   * Get times in calendar in given day.
   *
   * @param uid : who's nutrition
   * @param date the date
   * @return time models
   * @throws ConnectionException the connection exception
   */
  public List<TimeModel> getTimesInCalendar(String uid, Date date) throws ConnectionException;

  
  
  /*
   * CARDIO
   */ 
  
  /**
   * Returns user all facebook friends that have set user as coach.
   *
   * @return the trainees
   * @throws ConnectionException the connection exception
   */
  public List<UserModel> getTrainees() throws ConnectionException;
  
  /**
   * Removes cardio.
   *
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeCardio(CardioModel model) throws ConnectionException;

  /**
   * Removes cardio values.
   *
   * @param model to remove
   * @param values the values
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeCardioValues(CardioModel model, List<CardioValueModel> values) throws ConnectionException;
  
  /**
   * Delete comments (from meal or without meal).
   *
   * @param comments the comments
   * @return delete successful
   * @throws ConnectionException the connection exception
   */
  public boolean removeComments(List<CommentModel> comments) throws ConnectionException;
  
  /**
   * Delete exercises.
   *
   * @param exercises the exercises
   * @return delete successful
   * @throws ConnectionException the connection exception
   */
  public boolean removeExercises(List<ExerciseModel> exercises) throws ConnectionException;

  
  
  /*
   * RUN
   */ 
  
  /**
   * Delete foods (from meal or without meal).
   *
   * @param foods the foods
   * @return delete successful
   * @throws ConnectionException the connection exception
   */
  public boolean removeFoods(List<FoodModel> foods) throws ConnectionException;
  
  /**
   * Deletes list of guide values.
   *
   * @param list the list
   * @return the boolean
   * @throws ConnectionException the connection exception
   */
  public Boolean removeGuideValues(List<GuideValueModel> list) throws ConnectionException;
  
  /**
   * Removes meal.
   *
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeMeal(MealModel model) throws ConnectionException;
  
  /**
   * Removes measurement.
   *
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeMeasurement(MeasurementModel model) throws ConnectionException;

  /**
   * Removes measurement values.
   *
   * @param model to remove
   * @param values the values
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeMeasurementValues(MeasurementModel model, List<MeasurementValueModel> values) throws ConnectionException;

  /**
   * Removes routine.
   *
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeRoutine(RoutineModel model) throws ConnectionException;
  
  /**
   * Removes run.
   *
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeRun(RunModel model) throws ConnectionException;
  
  /**
   * Removes run values.
   *
   * @param model to remove
   * @param values the values
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeRunValues(RunModel model, List<RunValueModel> values) throws ConnectionException;

  
  /*
   * STATISTICS
   */
  
  /**
   * Removes time (and meals/foods it contains).
   *
   * @param models the models
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeTimes(TimeModel[] models) throws ConnectionException;
  
  /**
   * Removes workout.
   *
   * @param model to remove
   * @return remove successful
   * @throws ConnectionException the connection exception
   */
  public Boolean removeWorkout(WorkoutModel model) throws ConnectionException;

  /**
   * Saves access_token which will be used for confirming the current user.
   *
   * @return the user model
   * @throws ConnectionException the connection exception
   */
  public UserModel saveToken() throws ConnectionException;

  /**
   * Saves user data.
   *
   * @param u the u
   * @return save successfull
   * @throws ConnectionException the connection exception
   */
  public UserModel saveUserData(UserModel u) throws ConnectionException;
  
  /*
   * GUIDES
   */
  
  /**
   * Search exercise names.
   *
   * @param query the query
   * @param limit the limit
   * @return names' models
   * @throws ConnectionException the connection exception
   */
  public List<ExerciseNameModel> searchExerciseNames(String query, int limit) throws ConnectionException;

  /**
   * Search food names.
   *
   * @param query the query
   * @param limit the limit
   * @return names' models
   * @throws ConnectionException the connection exception
   */
  public List<FoodNameModel> searchFoodNames(String query, int limit) throws ConnectionException;
  
  /**
   * Search meals from other users.
   *
   * @param index the index
   * @param query the query
   * @return meals' models
   * @throws ConnectionException the connection exception
   */
  public List<MealModel> searchMeals(int index, String query) throws ConnectionException;
    
  /**
   * Search routines from other users.
   *
   * @param index the index
   * @param query the query
   * @return routines' models
   * @throws ConnectionException the connection exception
   */
  public List<RoutineModel> searchRoutines(int index, String query) throws ConnectionException;
  
  /**
   * Search workouts from other users.
   *
   * @param index the index
   * @param query the query
   * @return workouts' models
   * @throws ConnectionException the connection exception
   */
  public List<WorkoutModel> searchWorkouts(int index, String query) throws ConnectionException;
  
  
  /**
   * Search users
   * @param index
   * @param query
   * @return
   * @throws ConnectionException
   */
  public List<UserModel> searchUsers(int index, String query) throws ConnectionException;
  
  
  /**
   * Get users from permission circle
   * @param target
   * @return
   * @throws ConnectionException
   */
  public List<UserModel> getUsersFromCircle(int target) throws ConnectionException;
  
  /**
   * Updates cardio.
   *
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateCardio(CardioModel model) throws ConnectionException;

  /**
   * Updates exercise.
   *
   * @param exercise : model to be updated
   * @return updated exercise (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public ExerciseModel updateExercise(ExerciseModel exercise) throws ConnectionException;
  
  /**
   * Updates exercise name.
   *
   * @param name the name
   * @return updated exercise name (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public Boolean updateExerciseName(ExerciseNameModel name) throws ConnectionException;
  
  /**
   * Updates exercises order in given workout.
   *
   * @param workout the workout
   * @param ids the ids
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateExerciseOrder(WorkoutModel workout, Long[] ids) throws ConnectionException;
  
  /**
   * Updates food.
   *
   * @param food : model to be updated
   * @return updated food (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public FoodModel updateFood(FoodModel food) throws ConnectionException;
  
  /**
   * Updates food name.
   *
   * @param name the name
   * @return updated exercise name (null if add not successful)
   * @throws ConnectionException the connection exception
   */
  public Boolean updateFoodName(FoodNameModel name) throws ConnectionException;
  
  /**
   * Updates meal.
   *
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateMeal(MealModel model) throws ConnectionException;
  
  /**
   * Updates measurement.
   *
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateMeasurement(MeasurementModel model) throws ConnectionException;
  
  /**
   * Updates routine model.
   *
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateRoutine(RoutineModel model) throws ConnectionException;
  
  /**
   * Updates run.
   *
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateRun(RunModel model) throws ConnectionException;
  
  /**
   * Gets activity feed.
   *
   * @param model the model
   * @return base models
   * @throws ConnectionException the connection exception
   */
  //  public List<BaseModelData> getActivityFeed(int index, Date dateStart, Date dateEnd, String uid) throws ConnectionException;
  
  
  /**
   * Updates time
   * @param model to be updated
   * @return update successful
   */
  public Boolean updateTime(TimeModel model) throws ConnectionException;

  /**
   * Updates workout model.
   *
   * @param model to be updated
   * @return update successful
   * @throws ConnectionException the connection exception
   */
  public Boolean updateWorkout(WorkoutModel model) throws ConnectionException;

  
  /**
   * Return guide values.
   *
   * @param uid the uid
   * @param index the index
   * @param date : if null -> all values are returned
   * @return values
   * @throws ConnectionException the connection exception
   */
  public List<GuideValueModel> getGuideValues(String uid, int index, Date date) throws ConnectionException;
  


  
  /**
   * Returns single workout.
   *
   * @param workoutId the workout id
   * @return the workout
   * @throws ConnectionException the connection exception
   */
  public WorkoutModel getWorkout(Long workoutId) throws ConnectionException;
  
  /**
   * Returns all workouts that aren't in calendar.
   *
   * @param index the index
   * @param routine : if set return all workouts from given routine
   * @return workouts' models
   * @throws ConnectionException the connection exception
   */
  public List<WorkoutModel> getWorkouts(int index, RoutineModel routine) throws ConnectionException;
  
  /**
   * Get workouts in calendar between dates.
   *
   * @param uid : who's workouts
   * @param dateStart the date start
   * @param dateEnd the date end
   * @return workoutmodels in each days ( model[days][day's workouts] )
   * @throws ConnectionException the connection exception
   */
  public List<WorkoutModel[]> getWorkoutsInCalendar(String uid, Date dateStart, Date dateEnd) throws ConnectionException;


}
