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
package com.delect.motiver.client.lang;

import com.google.gwt.i18n.client.Constants;

public interface LangConstantsError extends Constants {

	@DefaultStringArrayValue({
		"Error loading activities", //METHOD_GET_ACTIVITY_FEED
		"Error loading friends from Facebook", //METHOD_GET_FRIENDS
		"Error loading comments", //METHOD_GET_COMMENTS
		"Error loading workouts", //METHOD_GET_WORKOUTS_IN_CALENDAR
		"", //METHOD_SAVE_TOKEN
		"", //METHOD_COMBINE_EXERCISE_NAMES
		"", //METHOD_COMBINE_FOOD_NAMES
		"", //METHOD_FETCH_SAVE_FOOD_NAMES
		"Error searching exercises", //METHOD_SEARCH_EXERCISE_NAMES
		"Error searching foods", //METHOD_SEARCH_FOOD_NAMES
		"Error updating exercise", //METHOD_UPDATE_EXERCISE_NAME
		"Error updating food", //METHOD_UPDATE_FOOD_NAME
		"", //METHOD_GET_BLOG_DATA
		"Error loading cardio", //METHOD_GET_CARDIO_VALUE
		"Error loading meal", //METHOD_GET_MEAL
		"Error loading measurement", //METHOD_GET_MEASUREMENT_VALUE
		"Error loading routine", //METHOD_GET_ROUTINE
		"Error loading run", //METHOD_GET_RUN_VALUE
		"Error loading workout", //METHOD_GET_WORKOUT
		"Error creating cardio", //METHOD_ADD_CARDIO
		"Error creating cardio value", //METHOD_ADD_CARDIO_VALUE
		"Error creating run", //METHOD_ADD_RUN
		"Error creating run value", //METHOD_ADD_RUN_VALUE
		"Error loading cardio values", //METHOD_GET_CARDIO_VALUES
		"Error loading cardios", //METHOD_GET_CARDIOS
		"Error loading run values", //METHOD_GET_RUN_VALUES
		"Error loading runs", //METHOD_GET_RUNS
		"Error removing cardio", //METHOD_REMOVE_CARDIO
		"Error removing cardio values", //METHOD_REMOVE_CARDIO_VALUES
		"Error removing run", //METHOD_REMOVE_RUN
		"Error removing run values", //METHOD_REMOVE_RUN_VALUES
		"Error updating cardio", //METHOD_UPDATE_CARDIO
		"Error updating run", //METHOD_UPDATE_RUN
		"Error creating food", //METHOD_ADD_FOOD
		"Error creating food", //METHOD_ADD_FOOD_NAME
		"Error creating guide value", //METHOD_ADD_GUIDE_VALUE
		"Error creating meal", //METHOD_ADD_MEAL
		"Error creating time", //METHOD_ADD_TIME
		"Error creating times", //METHOD_ADD_TIMES
		"Error loading foods", //METHOD_GET_FOODS
		"Error loading guide values", //METHOD_GET_GUIDE_VALUES
		"Error loading meals", //METHOD_GET_MEALS
		"Error loading most popular meals", //METHOD_GET_MOST_POPULAR_MEALS
		"Error loading times", //METHOD_GET_TIMES_IN_CALENDAR
		"Error removing foods", //METHOD_REMOVE_FOODS
		"Error removing guide value", //METHOD_REMOVE_GUIDE_VALUES
		"Error removing meal", //METHOD_REMOVE_MEAL
		"Error removing time", //METHOD_REMOVE_TIME
		"Error searching meals", //METHOD_SEARCH_MEALS
		"Error updating food", //METHOD_UPDATE_FOOD
		"Error updating meal", //METHOD_UPDATE_MEAL
		"Error updating time", //METHOD_UPDATE_TIME
		"Error creating measurement", //METHOD_ADD_MEASUREMENT
		"Error creating measurement value", //METHOD_ADD_MEASUREMENT_VALUE
		"", //METHOD_FETCH_REMOVE_ALL
		"", //METHOD_FETCH_SAVE_CARDIOS
		"", //METHOD_FETCH_SAVE_GUIDE_VALUES
		"", //METHOD_FETCH_SAVE_MEALS
		"", //METHOD_FETCH_SAVE_MEASUREMENTS
		"", //METHOD_FETCH_SAVE_ROUTINES
		"", //METHOD_FETCH_SAVE_RUNS
		"", //METHOD_FETCH_SAVE_TIMES
		"", //METHOD_FETCH_SAVE_WORKOUTS
		"Error loading measurement values", //METHOD_GET_MEASUREMENT_VALUES
		"Error loading measurements", //METHOD_GET_MEASUREMENTS
		"Error loading stats", //METHOD_GET_STATISTICS_TOP_EXERCISES
		"Error loading stats", //METHOD_GET_STATISTICS_TOP_MEALS
		"Error loading stats", //METHOD_GET_STATISTICS_TRAINING_DAYS
		"Error loading stats", //METHOD_GET_STATISTICS_TRAINING_TIMES
		"Error removing measurement", //METHOD_REMOVE_MEASUREMENT
		"Error removing measurement values", //METHOD_REMOVE_MEASUREMENT_VALUES
		"Error saving user data", //METHOD_SAVE_USER_DATA
		"Error updating measurement", //METHOD_UPDATE_MEASUREMENT
		"Error creating routine", //METHOD_ADD_ROUTINE
		"Error creating workout", //METHOD_ADD_WORKOUT
		"Error loading exercises", //METHOD_GET_EXERCISES
		"Error loading most popular routines", //METHOD_GET_MOST_POPULAR_ROUTINES
		"Error loading most popular workouts", //METHOD_GET_MOST_POPULAR_WORKOUTS
		"Error loading routines", //METHOD_GET_ROUTINES
		"Error loading workouts", //METHOD_GET_WORKOUTS
		"Error removing exercise", //METHOD_REMOVE_EXERCISES
		"Error removing routine", //METHOD_REMOVE_ROUTINE
		"Error removing workout", //METHOD_REMOVE_WORKOUT
		"Error searching routines", //METHOD_SEARCH_ROUTINES
		"Error searching workouts", //METHOD_SEARCH_WORKOUTS
		"Error updating routine", //METHOD_UPDATE_ROUTINE
		"Error updating workout", //METHOD_UPDATE_WORKOUT
		"Error creating exercise", //METHOD_ADD_EXERCISE
		"Error creating exercise", //METHOD_ADD_EXERCISE_NAME
		"Error updating exercise", //METHOD_UPDATE_EXERCISE 90
		"Error updating exercises'' order", //METHOD_UPDATE_EXERCISE_ORDER 91
		"Error loading exercises", //METHOD_GET_EXERCISES_FROM_NAME 92
		"Motiver is not yet available for public. Please try again later.", //ERROR_NOT_YET_PUBLIC
		"Couldn''t connect to xlGain.com", //ERROR_CANT_CONNECT_XLGAIN
		"Couldn''t connect to Facebook.com" //ERROR_CANT_CONNECT_FACEBOOK

	})
	String[] ErrorMessages();
}
