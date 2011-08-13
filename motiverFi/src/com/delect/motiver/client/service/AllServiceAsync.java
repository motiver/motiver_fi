/**
 * 
 */
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
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Antti
 *
 */
public interface AllServiceAsync extends TrainingServiceAsync, NutritionServiceAsync {

  public Request getUser(AsyncCallback<UserModel> callback);
  
  /**
   * Adds cardio to db.
   *
   * @param cardio : model to be added
   * @param callback the callback
   * @return added cardio (null if add not successful)
   */
  public Request addCardio(CardioModel cardio, AsyncCallback<CardioModel> callback);
  
  /**
   * Adds cardio's value to db.
   *
   * @param cardio : cardio where we add this value
   * @param value : value to be added
   * @param callback the callback
   * @return added cardio (null if add not successful)
   */
  public Request addCardioValue(CardioModel cardio, CardioValueModel value, AsyncCallback<CardioValueModel> callback);
  
  /**
   * Adds comment to db.
   *
   * @param comment : model to be added
   * @param callback the callback
   * @return added comment (null if add not successful
   */
  public Request addComment(CommentModel comment, AsyncCallback<CommentModel> callback);

  /**
   * Adds exercise to (workout).
   *
   * @param exercise : model to be added
   * @param callback the callback
   * @return added exercise (null if add not successful)
   */
  public Request addExercise(ExerciseModel exercise, AsyncCallback<ExerciseModel> callback);
  
  /**
   * Creates / updates exercisename (updates if already found).
   *
   * @param name the name
   * @param callback the callback
   * @return added exercise (null if add not successful)
   */
  public Request addExercisename(ExerciseNameModel name, AsyncCallback<ExerciseNameModel> callback);
  
  /**
   * Adds food to (meal).
   *
   * @param food : model to be added
   * @param callback the callback
   * @return added food (null if add not successful)
   */
  public Request addFood(FoodModel food, AsyncCallback<FoodModel> callback);
  
  /**
   * Creates / updates foodname (updates if already found).
   *
   * @param name : model to be added
   * @param callback the callback
   * @return added name (null if add not successful)
   */
  public Request addFoodname(FoodNameModel name, AsyncCallback<FoodNameModel> callback);
  
  /**
   * Creates / updates foodnames (updates if already found).
   *
   * @param names : models to be added
   * @param callback the callback
   * @return added names (null if add not successful)
   */
  public Request addFoodnames(List<FoodNameModel> names, AsyncCallback<List<FoodNameModel>> callback);
  
  /**
   * Adds guide value.
   *
   * @param model the model
   * @param callback the callback
   * @return the request
   */
  public Request addGuideValue(GuideValueModel model, AsyncCallback<GuideValueModel> callback);
  
  /**
   * Adds meal to db.
   *
   * @param meal : model to be added
   * @param callback the callback
   * @return added meal (null if add not successful)
   */
  public Request addMeal(MealModel meal, AsyncCallback<MealModel> callback);
  
  /**
   * Adds meal from time to db.
   *
   * @param meal : model to be added
   * @param timeId the time id
   * @param callback the callback
   * @return added meal (null if add not successful)
   */
  public Request addMeal(MealModel meal, Long timeId, AsyncCallback<MealModel> callback);
  
  /**
   * Adds meals to db.
   *
   * @param meals : models to be added
   * @param callback the callback
   * @return added meal (null if add not successful)
   */
  public Request addMeals(List<MealModel> meals, AsyncCallback<List<MealModel>> callback);
  
  /**
   * Adds measurement to db.
   *
   * @param measurement : model to be added
   * @param callback the callback
   * @return added measurement (null if add not successful)
   */
  public Request addMeasurement(MeasurementModel measurement, AsyncCallback<MeasurementModel> callback);
  
  /**
   * Adds measurement's value to db.
   *
   * @param measurement : measurement where we add this value
   * @param value : value to be added
   * @param callback the callback
   * @return added measurement (null if add not successful)
   */
  public Request addMeasurementValue(MeasurementModel measurement, MeasurementValueModel value, AsyncCallback<MeasurementValueModel> callback);
  
  /**
   * Adds routine to db.
   *
   * @param routine : model to be added
   * @param callback the callback
   * @return added workout (null if add not successful)
   */
  public Request addRoutine(RoutineModel routine, AsyncCallback<RoutineModel> callback);
  
  /**
   * Adds routines to db.
   *
   * @param routines : models to be added
   * @param callback the callback
   * @return added routines (null if add not successful)
   */
  public Request addRoutines(List<RoutineModel> routines, AsyncCallback<List<RoutineModel>> callback);

  /**
   * Adds run to db.
   *
   * @param run : model to be added
   * @param callback the callback
   * @return added run (null if add not successful)
   */
  public Request addRun(RunModel run, AsyncCallback<RunModel> callback);

  /**
   * Adds run's value to db.
   *
   * @param run : run where we add this value
   * @param value : value to be added
   * @param callback the callback
   * @return added run (null if add not successful)
   */
  public Request addRunValue(RunModel run, RunValueModel value, AsyncCallback<RunValueModel> callback);

  /**
   * Adds ticket to db.
   *
   * @param ticket : model to be added
   * @param callback the callback
   * @return added ticket (null if add not successful
   */
  public Request addTicket(TicketModel ticket, AsyncCallback<TicketModel> callback);
  
  /**
   * Adds time to db.
   *
   * @param time : model to be added
   * @param callback the callback
   * @return added time (null if add not successful)
   */
  public Request addTime(TimeModel time, AsyncCallback<TimeModel> callback);
  
  /**
   * Adds times to db.
   *
   * @param times : models to be added
   * @param callback the callback
   * @return added times (null if add not successful)
   */
  public Request addTimes(TimeModel[] times, AsyncCallback<TimeModel[]> callback);
  
  /**
   * Adds workout to db.
   *
   * @param workout : model to be added
   * @param callback the callback
   * @return added workout (null if add not successful
   */
  public Request addWorkout(WorkoutModel workout, AsyncCallback<WorkoutModel> callback);
  
  /**
   * Adds workouts to db.
   *
   * @param workouts the workouts
   * @param callback the callback
   * @return added workouts (null if add not successful
   */
  public Request addWorkouts(List<WorkoutModel> workouts, AsyncCallback<List<WorkoutModel>> callback);

  /**
   * Combines exercise names together.
   *
   * @param firstId : where other IDs are combined
   * @param ids : other IDs
   * @param callback the callback
   * @return the request
   */
  public Request combineExerciseNames(Long firstId, Long[] ids, AsyncCallback<Boolean> callback);
  
  
  /**
   * Combines food names together.
   *
   * @param firstId : where other IDs are combined
   * @param ids : other IDs
   * @param callback the callback
   * @return the request
   */
  public Request combineFoodNames(Long firstId, Long[] ids, AsyncCallback<Boolean> callback);

  /**
   * Dummy.
   *
   * @param model the model
   * @param callback the callback
   * @return the request
   */
  public Request dummy(MicroNutrientModel model, AsyncCallback<Boolean> callback);

  /**
   * Dummy.
   *
   * @param model the model
   * @param callback the callback
   * @return the request
   */
  public Request dummy2(MonthlySummaryExerciseModel model, AsyncCallback<MonthlySummaryExerciseModel> callback);
  
  /**
   * Removes all data from user.
   *
   * @param removeTraining the remove training
   * @param removeCardio the remove cardio
   * @param removeNutrition the remove nutrition
   * @param removeMeasurements the remove measurements
   * @param callback the callback
   * @return the request
   */
  public Request fetchRemoveAll(Boolean removeTraining, Boolean removeCardio, Boolean removeNutrition, Boolean removeMeasurements, AsyncCallback<Boolean> callback);

  /**
   * Saves cardio.
   *
   * @param cardios the cardios
   * @param values (values for each cardio)
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveCardios(List<CardioModel> cardios, List<List<CardioValueModel>> values, AsyncCallback<Boolean> callback);
  
  
  /**
   * Saves foods' names.
   *
   * @param names the names
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveFoodNames(List<FoodNameModel> names, AsyncCallback<Boolean> callback);
  
  /**
   * Saves guide values (nutrition).
   *
   * @param values the values
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveGuideValues(List<GuideValueModel> values, AsyncCallback<Boolean> callback);
  
  /**
   * Saves meals.
   *
   * @param meals the meals
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveMeals(List<MealModel> meals, AsyncCallback<Boolean> callback);
  
  /**
   * Saves measurements.
   *
   * @param measurement the measurement
   * @param values (values for each measurement)
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveMeasurements(MeasurementModel measurement, List<MeasurementValueModel> values, AsyncCallback<Boolean> callback);
  
  
  /**
   * Saves routins.
   *
   * @param routines the routines
   * @param workouts (workouts for each routine)
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveRoutines(List<RoutineModel> routines, List<List<WorkoutModel>> workouts, AsyncCallback<Boolean> callback);
  
  
  /*
   * NUTRITION
   */
  
  
  /**
   * Saves runs.
   *
   * @param runs the runs
   * @param values (values for each run)
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveRuns(List<RunModel> runs, List<List<RunValueModel>> values, AsyncCallback<Boolean> callback);
  
  /**
   * Saves times.
   *
   * @param times the times
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveTimes(List<TimeModel> times, AsyncCallback<Boolean> callback);
  
  /**
   * Saves workouts.
   *
   * @param workouts the workouts
   * @param callback the callback
   * @return the request
   */
  public Request fetchSaveWorkouts(List<WorkoutModel> workouts, AsyncCallback<Boolean> callback);
  
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
   * @param callback the callback
   * @return blog data for each day
   */
  public Request getBlogData(int index, int limit, int target, Date dateStart, Date dateEnd, String uid, Boolean showEmptyDays, AsyncCallback<List<BlogData>> callback);
  
  /**
   * Returns meaasurements.
   *
   * @param index the index
   * @param callback the callback
   * @return cardios' models
   */
  public Request getCardios(int index, AsyncCallback<List<CardioModel>> callback);

  /**
   * Returns last value from given cardio.
   *
   * @param cardioId the cardio id
   * @param callback the callback
   * @return model
   */
  public Request getCardioValue(Long cardioId, AsyncCallback<CardioValueModel> callback);
  
  /**
   * Returns all values from single cardio.
   *
   * @param meal the meal
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return values
   */
  public Request getCardioValues(CardioModel meal, Date dateStart, Date dateEnd, AsyncCallback<List<CardioValueModel>> callback);
  
  /**
   * Returns last comments.
   *
   * @param index the index
   * @param limit the limit
   * @param target the target
   * @param uid the uid
   * @param markAsRead the mark as read
   * @param callback the callback
   * @return comments
   */
  public Request getComments(int index, int limit, String target, String uid, boolean markAsRead, AsyncCallback<List<CommentModel>> callback);
  
  /**
   * Get nutrition in calendar between dates.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return energies in each days
   */
  public Request getEnergyInCalendar(Date dateStart, Date dateEnd, AsyncCallback<List<Double>> callback);

  
  /**
   * Returns all exercises from single workout.
   *
   * @param workout the workout
   * @param callback the callback
   * @return exercises
   */
  public Request getExercises(WorkoutModel workout, AsyncCallback<List<ExerciseModel>> callback);

  
  /**
   * Returns all exercises from given "name". Used for fetching last weights
   *
   * @param nameId the name id
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param limit : -1 if no limit
   * @param callback the callback
   * @return exercises
   */
  public Request getExercisesFromName(Long nameId, Date dateStart, Date dateEnd, int limit, AsyncCallback<List<ExerciseModel>> callback);

  
  /**
   * Returns single food name.
   *
   * @param id the id
   * @param callback the callback
   * @return name
   */
  public Request getFoodname(Long id, AsyncCallback<FoodNameModel> callback);
  
  /**
   * Returns all foods from single meal.
   *
   * @param meal the meal
   * @param callback the callback
   * @return foods
   */
  public Request getFoods(MealModel meal, AsyncCallback<List<FoodModel>> callback);
  
  /**
   * Returns user all facebook friends that have logged to xlgain.
   *
   * @param callback the callback
   * @return the friends
   */
  public Request getFriends(AsyncCallback<List<UserModel>> callback);
  
  /**
   * Sets single user's permission to view given target
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  public Request addUserToCircle(int target, String uid, AsyncCallback<Boolean> callback);
  
  /**
   * Sets single user's permission to view given target
   * @param target : permission target
   * @param uid : user we give permission
   * @return 
   * @throws ConnectionException
   */
  public Request removeUserFromCircle(int target, String uid, AsyncCallback<Boolean> callback);
  
  
  
  /**
   * Returns single meal.
   *
   * @param mealId the meal id
   * @param callback the callback
   * @return the meal
   */
  public Request getMeal(Long mealId, AsyncCallback<MealModel> callback);
  
  
  /**
   * Returns meals from time.
   *
   * @param index the index
   * @param callback the callback
   * @return meal' models
   */
  public Request getMeals(int index, AsyncCallback<List<MealModel>> callback);
  
  /**
   * Returns meaasurements.
   *
   * @param index the index
   * @param callback the callback
   * @return measurements' models
   */
  public Request getMeasurements(int index, AsyncCallback<List<MeasurementModel>> callback);
  
  /**
   * Returns last value from given measurement.
   *
   * @param measurementId the measurement id
   * @param callback the callback
   * @return model
   */
  public Request getMeasurementValue(Long measurementId, AsyncCallback<MeasurementValueModel> callback);

  /**
   * Returns all values from single measurement.
   *
   * @param meal the meal
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return values
   */
  public Request getMeasurementValues(MeasurementModel meal, Date dateStart, Date dateEnd, AsyncCallback<List<MeasurementValueModel>> callback);

  /**
   * Return single monthly summary
   * @param id
   * @return
   * @throws ConnectionException
   */
  public Request getMonthlySummary(Long id, AsyncCallback<MonthlySummaryModel> callback);
  
  /**
   * Returns saved monthly summaries
   * @return
   * @throws ConnectionException
   */
  public Request getMonthlySummaries(AsyncCallback<List<MonthlySummaryModel>> callback);
  
  /**
   * Get micronutrients from single day.
   *
   * @param uid the uid
   * @param date the date
   * @param callback the callback
   * @return micronutrients
   */
  public Request getMicroNutrientsInCalendar(String uid, Date date, AsyncCallback<List<MicroNutrientModel>> callback);
  
  
  /**
   * Returns most popular meals.
   *
   * @param index the index
   * @param callback the callback
   * @return meals' models
   */
  public Request getMostPopularMeals(int index, AsyncCallback<List<MealModel>> callback);
  
  
  /**
   * Returns most popular routines.
   *
   * @param index the index
   * @param callback the callback
   * @return routines' models
   */
  public Request getMostPopularRoutines(int index, AsyncCallback<List<RoutineModel>> callback);

  /**
   * Returns most popular workouts.
   *
   * @param index the index
   * @param callback the callback
   * @return workouts' models
   */
  public Request getMostPopularWorkouts(int index, AsyncCallback<List<WorkoutModel>> callback);
  
  /**
   * Returns single routine.
   *
   * @param routineId the routine id
   * @param callback the callback
   * @return the routine
   */
  public Request getRoutine(Long routineId, AsyncCallback<RoutineModel> callback);
  
  /**
   * Returns all routines that aren't in calendar.
   *
   * @param index the index
   * @param callback the callback
   * @return routines' models
   */
  public Request getRoutines(int index, AsyncCallback<List<RoutineModel>> callback);
  
  
  /*
   * MEASUREMENT
   */
    
  /**
   * Returns meaasurements.
   *
   * @param index the index
   * @param callback the callback
   * @return runs' models
   */
  public Request getRuns(int index, AsyncCallback<List<RunModel>> callback);
  
  /**
   * Returns last value from given run.
   *
   * @param runId the run id
   * @param callback the callback
   * @return model
   */
  public Request getRunValue(Long runId, AsyncCallback<RunValueModel> callback);
  
  /**
   * Returns all values from single run.
   *
   * @param meal the meal
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return values
   */
  public Request getRunValues(RunModel meal, Date dateStart, Date dateEnd, AsyncCallback<List<RunValueModel>> callback);
  
  /**
   * Gets top ten exercises.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return exercises array, each model have "count" value which has total count
   */
  public Request getStatisticsTopExercises(Date dateStart, Date dateEnd, AsyncCallback<List<ExerciseNameModel>> callback);

  
  /**
   * Gets top ten meals.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return meals array, each model have "count" value which has total count
   */
  public Request getStatisticsTopMeals(Date dateStart, Date dateEnd, AsyncCallback<List<MealModel>> callback);

  /**
   * Gets statistics for each day.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return array : training count for each day
   */
  public Request getStatisticsTrainingDays(Date dateStart, Date dateEnd, AsyncCallback<int[]> callback);
  
  /**
   * Gets statistics for each day.
   *
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return array : training count for each time (00-03, 03-06, 06-09, 09-12, 12-15, 15-18, 18-21, 21-24)
   */
  public Request getStatisticsTrainingTimes(Date dateStart, Date dateEnd, AsyncCallback<int[]> callback);
  
  /**
   * Get times in calendar in given day.
   *
   * @param uid : who's nutrition
   * @param date the date
   * @param callback the callback
   * @return time models
   */
  public Request getTimesInCalendar(String uid, Date date, AsyncCallback<List<TimeModel>> callback);

  
  
  /*
   * CARDIO
   */
    
  /**
   * Returns user all facebook friends that have set user as coach.
   *
   * @param callback the callback
   * @return the trainees
   */
  public Request getTrainees(AsyncCallback<List<UserModel>> callback);
  
  
  /**
   * Removes cardio.
   *
   * @param model to remove
   * @param callback the callback
   * @return remove successful
   */
  public Request removeCardio(CardioModel model, AsyncCallback<Boolean> callback);

  /**
   * Removes cardio values.
   *
   * @param model to remove
   * @param values the values
   * @param callback the callback
   * @return remove successful
   */
  public Request removeCardioValues(CardioModel model, List<CardioValueModel> values, AsyncCallback<Boolean> callback);
  
  /**
   * Delete comments (from meal or without meal).
   *
   * @param comments the comments
   * @param callback the callback
   * @return delete successful
   */
  public Request removeComments(List<CommentModel> comments, AsyncCallback<Boolean> callback);
  
  /**
   * Delete exercises.
   *
   * @param exercises the exercises
   * @param callback the callback
   * @return delete successfull
   */
  public Request removeExercises(List<ExerciseModel> exercises, AsyncCallback<Boolean> callback);

  
  
  /*
   * RUN
   */
    
  /**
   * Delete foods (from meal or without meal).
   *
   * @param foods the foods
   * @param callback the callback
   * @return delete successful
   */
  public Request removeFoods(List<FoodModel> foods, AsyncCallback<Boolean> callback);
  
  /**
   * Deletes list of guide values.
   *
   * @param list the list
   * @param callback the callback
   * @return the request
   */
  public Request removeGuideValues(List<GuideValueModel> list, AsyncCallback<Boolean> callback);
  
  /**
   * Removes meal.
   *
   * @param model to remove
   * @param callback the callback
   * @return remove successful
   */
  public Request removeMeal(MealModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Removes measurement.
   *
   * @param model to remove
   * @param callback the callback
   * @return remove successful
   */
  public Request removeMeasurement(MeasurementModel model, AsyncCallback<Boolean> callback);

  /**
   * Removes measurement values.
   *
   * @param model to remove
   * @param values the values
   * @param callback the callback
   * @return remove successful
   */
  public Request removeMeasurementValues(MeasurementModel model, List<MeasurementValueModel> values, AsyncCallback<Boolean> callback);

  
  /**
   * Removes routine.
   *
   * @param model to remove
   * @param callback the callback
   * @return remove successful
   */
  public Request removeRoutine(RoutineModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Removes run.
   *
   * @param model to remove
   * @param callback the callback
   * @return remove successful
   */
  public Request removeRun(RunModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Removes run values.
   *
   * @param model to remove
   * @param values the values
   * @param callback the callback
   * @return remove successful
   */
  public Request removeRunValues(RunModel model, List<RunValueModel> values, AsyncCallback<Boolean> callback);


  
  /*
   * STATISTICS
   */
  
  /**
   * Removes time (and meals/foods it contains).
   *
   * @param models the models
   * @param callback the callback
   * @return remove successful
   */
  public Request removeTimes(TimeModel[] models, AsyncCallback<Boolean> callback);
  
  /**
   * Removes workout.
   *
   * @param model to remove
   * @param callback the callback
   * @return removed successfull
   */
  public Request removeWorkout(WorkoutModel model, AsyncCallback<Boolean> callback);

  /**
   * Saves access_token which will be used for confirming the current user.
   *
   * @param callback the callback
   * @return the request
   */
  public Request saveToken(AsyncCallback<UserModel> callback);

  /**
   * Saves user data.
   *
   * @param user the user
   * @param callback the callback
   * @return save successfull
   */
  public Request saveUserData(UserModel user, AsyncCallback<UserModel> callback);
  
  /*
   * GUIDES
   */
  
  /**
   * Returns all exercise names.
   *
   * @param query the query
   * @param limit the limit
   * @param callback the callback
   * @return names' models
   */
  public Request searchExerciseNames(String query, int limit, AsyncCallback<List<ExerciseNameModel>> callback);

  /**
   * Search food names.
   *
   * @param query string
   * @param limit the limit
   * @param callback the callback
   * @return names' models
   */
  public Request searchFoodNames(String query, int limit, AsyncCallback<List<FoodNameModel>> callback);
  
  /**
   * Search meals from other users.
   *
   * @param index the index
   * @param query string
   * @param callback the callback
   * @return meals' models
   */
  public Request searchMeals(int index, String query, AsyncCallback<List<MealModel>> callback);

  /**
   * Search routines from other users.
   *
   * @param index the index
   * @param query string
   * @param callback the callback
   * @return routines' models
   */
  public Request searchRoutines(int index, String query, AsyncCallback<List<RoutineModel>> callback);
  
  /**
   * Search workouts from other users.
   *
   * @param index the index
   * @param query string
   * @param callback the callback
   * @return workouts' models
   */
  public Request searchWorkouts(int index, String query, AsyncCallback<List<WorkoutModel>> callback);
  
  
  /**
   * Search users
   * @param index
   * @param query
   * @return
   * @throws ConnectionException
   */
  public Request searchUsers(int index, String query, AsyncCallback<List<UserModel>> callback);
  
  
  /**
   * Get users from permission circle
   * @param target
   * @return
   * @throws ConnectionException
   */
  public Request getUsersFromCircle(int target, AsyncCallback<List<UserModel>> callback);
  
  /**
   * Updates cardio.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successful
   */
  public Request updateCardio(CardioModel model, AsyncCallback<Boolean> callback);

  /**
   * Updates exercise.
   *
   * @param exercise : model to be updated
   * @param callback the callback
   * @return updated exercise (null if add not successful)
   */
  public Request updateExercise( ExerciseModel exercise, AsyncCallback<ExerciseModel> callback);
  
  /**
   * Updates exercise name.
   *
   * @param name the name
   * @param callback the callback
   * @return updated exercise name (null if add not successful)
   */
  public Request updateExerciseName(ExerciseNameModel name, AsyncCallback<Boolean> callback);
  
  /**
   * Updates exercises order in given workout.
   *
   * @param workout the workout
   * @param ids the ids
   * @param callback the callback
   * @return update successful
   */
  public Request updateExerciseOrder(WorkoutModel workout, Long[] ids, AsyncCallback<Boolean> callback);
  
  /**
   * Updates food.
   *
   * @param food : model to be updated
   * @param callback the callback
   * @return updated food (null if add not successful)
   */
  public Request updateFood(FoodModel food, AsyncCallback<FoodModel> callback);
  
  /**
   * Updates food name.
   *
   * @param name the name
   * @param callback the callback
   * @return updated exercise name (null if add not successful)
   */
  public Request updateFoodName(FoodNameModel name, AsyncCallback<Boolean> callback);
  
  /**
   * Updates meal.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successful
   */
  public Request updateMeal(MealModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Updates measurement.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successful
   */
  public Request updateMeasurement(MeasurementModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Updates routine model.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successful
   */
  public Request updateRoutine(RoutineModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Updates run.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successful
   */
  public Request updateRun(RunModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Updates time.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successful
   */
  public Request updateTime(TimeModel model, AsyncCallback<Boolean> callback);
  
  /**
   * Updates workout model.
   *
   * @param model to be updated
   * @param callback the callback
   * @return update successfull
   */
  public Request updateWorkout(WorkoutModel model, AsyncCallback<Boolean> callback);

}
