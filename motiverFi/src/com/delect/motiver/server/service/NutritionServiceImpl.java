/**
 * 
 */
package com.delect.motiver.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.delect.motiver.client.service.NutritionService;
import com.delect.motiver.server.GuideValue;
import com.delect.motiver.server.PMF;
import com.delect.motiver.server.Workout;
import com.delect.motiver.server.datastore.StoreTraining;
import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.GuideValueModel;
import com.delect.motiver.shared.WorkoutModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Antti
 *
 */
public class NutritionServiceImpl extends RemoteServiceServlet implements NutritionService {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1930822872413338185L;

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionServiceImpl.class.getName()); 

  
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
    final String UID = AllServiceImpl.getUid();
    if(UID == null) {
      return null;
    }

    //check permission
    if(!AllServiceImpl.hasPermission(1, UID, uid)) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();
    
    try {
      Query q = pm.newQuery(GuideValue.class);

      List<GuideValue> values = null;
      
      if(date != null) {
        //strip dates
        final Date d1 = AllServiceImpl.stripTime(date, true);
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
   * Get workouts in calendar between dates
   * @param uid : who's workouts
   * @param dateStart
   * @param dateEnd
   * @return workoutmodels in each days ( model[days][day's workouts] )
   * @throws ConnectionException 
   */
  @SuppressWarnings("unchecked")
  public List<WorkoutModel[]> getWorkoutsInCalendar(String uid, Date dateStart, Date dateEnd) throws ConnectionException {

    logger.log(Level.FINE, "getWorkoutsInCalendar()");

    List<WorkoutModel[]> list = new ArrayList<WorkoutModel[]>();
    
    //get uid
    final String UID = AllServiceImpl.getUid();
    if(UID == null) {
      return null;
    }
    
    //check dates
    if(dateStart.getTime() > dateEnd.getTime()) {
      return null;
    }

    //check permission
    if(!AllServiceImpl.hasPermission(0, UID, uid)) {
      return null;
    }
    
    PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      //go through days
      final int days = (int)((dateEnd.getTime() - dateStart.getTime()) / (24 * 60 * 60 * 1000)) + 1;
      
      for(int i=0; i < days; i++) {
        
        final Date d = new Date((dateStart.getTime() / 1000 + 3600 * 24 * i) * 1000);
        //strip time
        final Date dStart = AllServiceImpl.stripTime(d, true);
        final Date dEnd = AllServiceImpl.stripTime(d, false);
        
        Query q = pm.newQuery(Workout.class);
        q.setFilter("openId == openIdParam && date >= dateStartParam && date <= dateEndParam");
        q.declareParameters("java.lang.String openIdParam, java.util.Date dateStartParam, java.util.Date dateEndParam");
        List<Workout> workouts = (List<Workout>) q.execute(uid, dStart, dEnd);

        //convert to client side models
        WorkoutModel[] arr = new WorkoutModel[workouts.size()];
        int c = 0;
        for(Workout w : workouts) {
          WorkoutModel m = StoreTraining.getWorkout(pm, w.getId(), UID);

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
  
}
