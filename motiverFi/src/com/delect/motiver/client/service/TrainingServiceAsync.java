/**
 * 
 */
package com.delect.motiver.client.service;

import java.util.Date;
import java.util.List;

import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Antti
 *
 */
public interface TrainingServiceAsync {

  /**
   * Returns single workout.
   *
   * @param workoutId the workout id
   * @param callback the callback
   * @return the workout
   */
  public Request getWorkout(Long workoutId, AsyncCallback<WorkoutModel> callback);
  
  /**
   * Returns all workouts that aren't in calendar.
   *
   * @param index the index
   * @param routine the routine
   * @param callback the callback
   * @return workouts' models
   */
  public Request getWorkouts(int index, RoutineModel routine, AsyncCallback<List<WorkoutModel>> callback);
  
  /**
   * Get workouts in calendar between dates.
   *
   * @param uid : who's workouts
   * @param dateStart the date start
   * @param dateEnd the date end
   * @param callback the callback
   * @return workoutmodels in each days ( model[days][day's workouts] )
   */
  public Request getWorkoutsInCalendar(String uid, Date dateStart, Date dateEnd, AsyncCallback<List<WorkoutModel[]>> callback);

  
}
