/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client;


public interface StringConstants {
  
	public static final String[] DATEFORMATS = new String[] {
		"dd.MM.yyyy",	//metric
		"MM/dd/yyyy"	//us
	};
	public static final String[] DATEFORMATS_SHORT = new String[] {
		"dd.MM",	//metric
		"MM/dd"	//us
	};
	public static final int LIMIT_CAL_DAYS_MAX = 90;		//how many days are shown in main calendar view
	public static final int LIMIT_MEAS_GRAPH = 100;		//how is max in measurement graph
	public static final String[] MEAS_METRIC = new String[] {
		"µg",
		"mg",
		"g"
	};
	
	public static final String[] MEAS_US = new String[] {
		"µg",
		"mg",
		"g"
	};
	public static final int SLIDE_MAX_Y = 50;
	//sliding
	public static final int SLIDE_MIN_X = 100;
	public static final String[] TIMEFORMATS = new String[] {
		"HH:mm",		//20:05
		"K:mm a"		//08:05 pm
	};
	public static final String[] UNITS_DISTANCE = new String[] {
		"km",	//metric
		"miles"	//us
	};
	public static final String URL_ADDEXERCISE = "add_exercise.php";
	public static final String URL_ADDFOOD = "add_food.php";
	public static final String URL_ADDFOODTOCAL = "add_food_to_cal.php";

	public static final String URL_ADDMEAL = "add_meal.php";
	public static final String URL_ADDMEALTOCAL = "add_meal_to_cal.php";
	public static final String URL_ADDMEAS = "add_meas.php";
	public static final String URL_ADDROUTINETOCAL = "add_routine_to_cal.php";
	public static final String URL_ADDUSER = "add_user.php";
	public static final String URL_ADDWORKOUTTOCAL = "add_workout_to_cal.php";
	public static final String URL_ADDWORKOUTTOROUTINE = "add_workout_to_routine.php";

	public static final String URL_DELETEEXERCISE = "delete_exercise.php";
	public static final String URL_DELETEFOOD = "delete_food.php";
	public static final String URL_DELETEMEAL = "delete_meal.php";
	public static final String URL_DELETEMEASURMENT = "delete_measurement.php";
	
	public static final String URL_DELETEWORKOUT = "delete_workout.php";
	public static final String URL_ENAMES = "get_enames.php";

	public static final String URL_GETDAYFROMCAL = "get_day_from_cal.php";
	//URL: nutrition
	public static final String URL_GETFOODS = "get_foods.php";
	public static final String URL_GETFROMCAL = "get_from_cal.php";
	public static final String URL_GETLOGIN = "get_login.php";
	public static final String URL_GETMEALS = "get_meals.php";
	//URL: measurements
	public static final String URL_GETMEASUREMENTS = "get_meas.php";
	public static final String URL_GETNUTRITIONS = "get_nutritions.php";
	public static final String URL_GETROUTINEINFO = "get_routine_info.php";
	public static final String URL_GETROUTINES = "get_routines.php";
	public static final String URL_GETWEIGHTS = "get_weights.php";
	public static final String URL_GETWORKOUTINFO = "get_workout_info.php";
	//URL: training
	public static final String URL_GETWORKOUTS = "get_workouts.php";
	
	//URL: user/login
	public static final String URL_SERVER = "http://www.xlgain.com/feed_jsonp";
	public static final String URL_SETENAME = "set_ename.php";

	public static final String URL_SETEXERCISEINFO = "set_exercise_info.php";
	public static final String URL_SETEXERCISEORDER = "set_exercise_order.php";
	
	public static final String URL_SETFOODINFO = "set_food_info.php";
	public static final String URL_SETFOODNAME = "set_foodname.php";

	public static final String URL_SETMEALINFO = "set_meal_info.php";
	public static final String URL_SETROUTINEINFO = "set_routine_info.php";
	public static final String URL_SETUSERINFO = "set_user_info.php";
	public static final String URL_SETWORKOUTDONE = "set_workout_done.php";
	public static final String URL_SETWORKOUTINFO = "set_workout_info.php";
	public static final String URL_SETWORKOUTRATING = "set_workout_rating.php";
	
}
