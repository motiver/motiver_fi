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
package com.delect.motiver.shared;

import java.util.logging.Level;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;

public interface Constants {

  
  //FOR LOCAL TESTING (change also API_KEY in index.html & blog.html!!!)
    public static final String URL_APP = "http://localhost:8888/?gwt.codesvr=127.0.0.1:9997";
    public static final String URL_APP_STATIC = "http://localhost:8888/";
    public static final String URL_APP_CURR = "www.motiver.fi";

  //FOR ONLINE TESTING (change also API_KEY in index.html & blog.html!!!)
//  public static final String URL_APP = "http://dev.motiver-app.appspot.com/";
//  public static final String URL_APP_STATIC = "http://dev.motiver-app.appspot.com/";
//  public static final String URL_APP_CURR = "dev.motiver-app.appspot.com";

  //FOR RELEASE (change also API_KEY in index.html & blog.html!!!)
//    public static final String URL_APP = "http://www.motiver.fi/";
//    public static final String URL_APP_STATIC = "http://static.motiver.fi/";
//    public static final String URL_APP_CURR = Window.Location.getHostName();
  
	/**
	 * Default value for how many days after today are shown
	 * <br>Total days = {@link #LIMIT_BLOG_DAYS}
	 */
	public static final int BLOG_DEFAULT_DAYS_AFTER_TODAY = 2;

	/**
	 * Color for graph's data
	 */
	public static final String[] COLOR_GRAPH = new String[] { "#6fa3ae", "#e1b051", "#acb564", "#acac90", "#cd6329",  "#2b606a", "#a07010", "#687120" };
	
	/**
	 * Cookie name for saving url token
	 */
	public static final String COOKIE_TOKEN = "urlToken2";
  /**
   * Cookie name for default training day times in empty nutrition day
   */
  public static final String COOKIE_DEFAULT_TIMES_TRAINING = "dtt";
  /**
   * Cookie name for default rest day times in empty nutrition day
   */
  public static final String COOKIE_DEFAULT_TIMES_REST = "dtr";
	
	/**
	 * Max datedifference for cardio
	 */
	public static final int DAYS_DIFF_MAX_CARDIO = 365;
	/**
	 * Max datedifference for measurement
	 */
	public static final int DAYS_DIFF_MAX_MEASUREMENT = 365;
	/**
	 * Max datedifference for run
	 */
	public static final int DAYS_DIFF_MAX_RUN = 365;
	/**
	 * Max datedifference for stats
	 */
	public static final int DAYS_DIFF_MAX_STATS = 180;
	/**
	 * Max datedifference for cardio
	 */
	public static final int DAYS_INDEX_CARDIO = 3;
	/**
	 * Max datedifference for measurement
	 */
	public static final int DAYS_INDEX_MEASUREMENT = 3;
	/**
	 * Max datedifference for run
	 */
	public static final int DAYS_INDEX_RUN = 3;
	/**
	 * Max datedifference for stats
	 */
	public static final int DAYS_INDEX_STATS = 0;
	/**
	 * Days when search indexes expires
	 */
	public static final int DAYS_SEARCH_INDEXES_EXPIRE = 21;
	/**
	 * How often comments are refreshed in comment box (milliseconds)
	 */
	public static final int DELAY_COMMENTS_REFRESH = 1000 * 60 * 5;	//5 min
	
	/**
	 * How long we wait after validating text field (after user stops entering value)
	 * <br>milliseconds
	 */
	public static final int DELAY_FIELD_VALIDATION = 1000;
	/**
	 * How long we wait before we hide icons (when mouse is out)
	 */
	public static final int DELAY_HIDE_ICONS = 200;
	/**
	 * How long presenter's hightlighting is on (milliseconds)
	 */
	public static final int DELAY_HIGHLIGHT = 500;
	/**
	 * Delay for turning the highlightin off
	 */
	public static final int DELAY_HIGHLIGHT_OFF = 500;
	/**
	 * Min delay for consecutive key events (milliseconds)
	 */
	public static final long DELAY_KEY_EVENTS = 2000;
	/**
	 * How long loading text is visible after RPC call starts
	 */
	public static final int DELAY_LOADING_VISIBLE = 1000;
	/**
	 * How long we try to login to facebook (milliseconds)
	 */
	public static final int DELAY_LOGIN_DIALOG = 10000;
	/**
	 * When page is reloaded after inactivity (milliseconds)
	 */
	public static final int DELAY_PAGE_RELOAD = 4 * 60 * 60 * 1000;	//4 hours
	/**
	 * How long we wait when search is started (after user stopped typing)
	 * <br>milliseconds
	 */
	public static final int DELAY_SEARCH = 750;

  //ERROR CODES
  public static final int ERROR_CANT_CONNECT_FACEBOOK = 95;
  public static final int ERROR_CANT_CONNECT_XLGAIN = 94;
  
	/**
	 * Event type for global hotkeys
	 */
	public static final EventType EVENT_TYPE_GLOBAL_HOTKEYS = Events.OnKeyDown;
	/**
	 * Delay between (old data) fetch calls (milliseconds)
	 */
	public static int FETCH_DELAY = 500;
	/**
	 * Max number of recent activity are shown at once
	 */
	public static int LIMIT_ACTIVITY_ITEMS = 10;
	/**
	 * Max number of recent activity are shown in total (speeds up search)
	 */
	public static int LIMIT_ACTIVITY_ITEMS_MAX = 100;
	/**
	 * How many days user can browse back in blog
	 */
	public static final int LIMIT_BLOG_DAY_BACK = 100;
	/**
	 * Max number of recent activity are shown
	 */
	public static int LIMIT_BLOG_DAYS = 7;
	/**
	 * Max number of nutrition guide values shown
	 */
	public static int LIMIT_CARDIOS = 10;
	/**
	 * Max length for comment in comment feed
	 */
	public static final int LIMIT_COMMENT_LENGTH = 75;
	/**
	 * Max number of comments are shown below some target
	 */
	public static int LIMIT_COMMENTS = 3;
	/**
	 * Max number of recent comments are shown
	 */
	public static int LIMIT_COMMENTS_FEED = 6;
	/**
	 * Max number of nutrition guide values shown
	 */
	public static int LIMIT_GUIDE_VALUES = 3;
	/**
	 * Limit for last weights
	 */
	public static int LIMIT_LAST_WEIGHTS = 5;
  /**
   * Number of records per page in lists
   */
  public static final int LIMIT_LIST_RECORDS = 30;
	/**
   * Max width for the UI
   */
  public static final int LIMIT_MAX_WINDOW_WIDTH = 1000;
	/**
	 * Max number of meals shown
	 */
	public static int LIMIT_MEALS = 20;
	/**
	 * Max number of nutrition guide values shown
	 */
	public static int LIMIT_MEASUREMENTS = 10;
	/**
	 * Min number of characters for single query word
	 */
	public static final int LIMIT_MIN_QUERY_WORD = 2;
	/**
	 * Max length for name
	 */
	public static final int LIMIT_NAME_MAX = 30;
	/**
	 * Min length for name
	 */
	public static final int LIMIT_NAME_MIN = 5;
	/**
	 * How many days are shown in routine at once
	 */
	public static final int LIMIT_ROUTINE_DAYS = 7;
  /**
	 * Max number of routines shown
	 */
	public static int LIMIT_ROUTINES = 20;
	/**
	 * Max number of nutrition guide values shown
	 */
	public static int LIMIT_RUNS = 10;
	/**
	 * Max number of foods/exercise names from query
	 */
	public static int LIMIT_SEARCH_NAMES = 15;
	/**
	 * Max number of workouts shown
	 */
	public static int LIMIT_WORKOUTS = 20;
  /**
   * Max number of users shown
   */
  public static int LIMIT_USERS = 5;

  //error codes
  public static final int JSONP_GET_ALL = 77;
  public static final int JSONP_GET_GROUPS = 78;
    

	/**
	 * Storage name for food names
	 */
	public static final String STORAGE_FOOD_NAMES = "food_names";
	public static final long UID_MOTIVER = 224787470868700L;

	public static final long UID_VALIO = 493464655570L;
	
	/**
	 * How many times we try to update entity if there is some problems
	 */
  public static final int LIMIT_UPDATE_RETRIES = 3;
  
  /**
   * Default values for training day times
   */
  public static final int[] VALUE_DEFAULT_TIMES_TRAINING = new int[] { 25200, 41400, 57600, 64800, 70200, 79200 };
  /**
   * Default values for rest day times
   */
  public static final int[] VALUE_DEFAULT_TIMES_REST = new int[] { 25200, 41400, 57600, 64800, 79200 };
  

  public static final String TOKEN_MAIN = "user";
  public static final String TOKEN_TRAINING = "user/training";
  public static final String TOKEN_NUTRITION = "user/nutrition";
  public static final String TOKEN_CARDIO = "user/cardio";
  public static final String TOKEN_STATISTICS = "user/statistics";
  public static final String TOKEN_PROFILE = "user/profile";
  public static final String TOKEN_ADMIN = "user/admin";
  
  /**
   * Drag groups
   */
  public static final String DRAG_GROUP_USER = "user";
  public static final String DRAG_GROUP_WORKOUT = "wp";   // + workout's ID
	
  
  /**
   * Log levels
   */
  public static final Level LOG_LEVEL_SERVICE = Level.FINE;
  public static final Level LOG_LEVEL_MANAGER = Level.FINER;
  public static final Level LOG_LEVEL_DAO = Level.FINEST;
  public static final Level LOG_LEVEL_CACHE = Level.WARNING;
  
}
