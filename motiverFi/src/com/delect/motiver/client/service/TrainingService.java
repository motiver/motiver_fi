/**
 * 
 */
package com.delect.motiver.client.service;

import java.util.Date;
import java.util.List;

import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Antti
 *
 */
public interface TrainingService extends RemoteService {

  
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
