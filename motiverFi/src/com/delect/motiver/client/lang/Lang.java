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

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;


@DefaultLocale("en")
public interface Lang extends Messages {
	
	@DefaultMessage("Account")
	String Account();
	@DefaultMessage("Add")
	String Add();
	@DefaultMessage("Add new {0}")
	String AddNew(String target);
	@DefaultMessage("Add {0}")
	String AddTarget(String target);
	@DefaultMessage("Add to {0}")
	String AddTo(String target);
	@DefaultMessage("Admin")
	String Admin();
	@DefaultMessage("All")
	String All();
	@DefaultMessage("All times")
	String AllTimes();
	@DefaultMessage("Are you sure?")
	String AreYouSure();
	@DefaultMessage("Back")
	String Back();
	@DefaultMessage("Back to {0}")
	String BackTo(String target);
	@DefaultMessage("Blog")
	String Blog();
	@DefaultMessage("Blogs")
	String Blogs();
	@DefaultMessage("Blog''s owner")
	String BlogsOwner();
	@DefaultMessage("Breakfast")
	String Breakfast();
	@DefaultMessage("Calories")
	String Calories();
	@DefaultMessage("Cancel")
	String Cancel();
	@DefaultMessage("Cancel selection")
	String CancelSelection();
	@DefaultMessage("Carbohydrates")
	String Carbohydrates();
	@DefaultMessage("Cardio")
	String Cardio();
  @DefaultMessage("Cardios")
  String Cardios();
	@DefaultMessage("Cardio value")
	String CardioValue();
	@DefaultMessage("Category")
	String Category();
	@DefaultMessage("Change exercise order by dragging")
	String ChangeExerciseOrder();
	@DefaultMessage("Click here")
	String ClickHere();
	@DefaultMessage("Click to hide {0}")
	String ClickToHide(String target);
	@DefaultMessage("Click to show {0}")
	String ClickToShow(String target);
	@DefaultMessage("Click to show/hide foods")
	String ClickToShowHideFoods();
	@DefaultMessage("Click to view {0}")
	String ClickToView(String target);
	@DefaultMessage("Close")
	String Close();
	@DefaultMessage("Coach")
	String Coach();
	@DefaultMessage("Coach mode ON")
	String CoachModeOn();
	@DefaultMessage("Comment")
	String Comment();
	@DefaultMessage("Comments")
	String Comments();
	@DefaultMessage("Comments are disabled in coach mode")
	String CommentsDisabledInCoachMode();
	@DefaultMessage("Comments for {0}")
	String CommentsFor(String target);
	@DefaultMessage("Complete")
	String Complete();
	@DefaultMessage("Confirm")
	String Confirm();
	@DefaultMessage("Are you sure? This will remove last day''s workouts")
	String ConfirmRemoveLastDayInRoutine();
	@DefaultMessage("Copy {0}")
	String Copy(String target);
	@DefaultMessage("Copy ''{0}'' to {1}")
	String CopyTargetTo(String name, String target);
	@DefaultMessage("Copy to ''{0}''")
	String CopyTo(String target);
	@DefaultMessage("Country")
	String Country();
	@DefaultMessage("Create")
	String Create();
	@DefaultMessage("Create {0}")
	String CreateTarget(String target);
	@DefaultMessage("Data")
	String Data();
	@DefaultMessage("All data fetched successfully")
	String DataFetchedSuccessfully();
	@DefaultMessage("Date")
	String Date();
	@DefaultMessage("Date end")
	String DateEnd();
	@DefaultMessage("Dateformat")
	String DateFormat();
	@DefaultMessage("Dates")
	String Dates();
	@DefaultMessage("Date start")
	String DateStart();
	@DefaultMessage("Day")
	String Day();
	@DefaultMessage("Days")
	String Days();
	@DefaultMessage("Days'' calories")
	String DaysCalories();
	@DefaultMessage("Delete")
	String Delete();
	@DefaultMessage("Delete ''{0}''")
	String DeleteTarget(String target);
	@DefaultMessage("Description")
	String Description();
	@DefaultMessage("Details")
	String Details();
	@DefaultMessage("Dinner")
	String Dinner();
	@DefaultMessage("Distance")
	String Distance();
	@DefaultMessage("Drag to copy")
	String DragToCopy();
	@DefaultMessage("Duration")
	String Duration();
	@DefaultMessage("Edit {0}")
	String EditTarget(String target);
	@DefaultMessage("No foods on this day.<br><br>Click ''Add time'' button to add current time.")
	String EmptyNutritionDayDesc();
	@DefaultMessage("No foods.<br><br>Click ''Add...'' buttons on top right corner to add new meals/foods.")
	String EmptyTimeDesc();
	@DefaultMessage("No workouts on this day.<br><br>Click ''Add ...'' buttons to select training activity.")
	String EmptyTrainingDayDesc();
	@DefaultMessage("No exercises.<br><br>Click ''Add exercise'' to add new exercise.")
	String EmptyWorkoutDesc();
	@DefaultMessage("End coach mode")
	String EndCoachMode();
	@DefaultMessage("Energy")
	String Energy();
	@DefaultMessage("Enter keyword to search for exercises...")
	String EnterKeywordToSearchForExercises();
	@DefaultMessage("Enter keyword to search for foods...")
	String EnterKeywordToSearchForFoods();
	@DefaultMessage("Enter keyword to search for meals...")
	String EnterKeywordToSearchForMeals();
	@DefaultMessage("Enter keyword to search for routines...")
	String EnterKeywordToSearchForRoutines();
	@DefaultMessage("Enter keyword to search for workouts...")
	String EnterKeywordToSearchForWorkouts();
	@DefaultMessage("Enter name")
	String EnterName();
	@DefaultMessage("Enter your xlGain.com username and password")
	String EnterXlGainInfo();
	@DefaultMessage("Equipment")
	String Equipment();
	@DefaultMessage("Error")
	String Error();
	@DefaultMessage("Error fetching data ({0})")
	String ErrorFetchingData(String target);
	@DefaultMessage("Something went wrong!")
	String ErrorMessage();
	@DefaultMessage("Everybody")
	String Everybody();
	@DefaultMessage("Exercise")
	String Exercise();
	@DefaultMessage("Exercise history")
	String ExerciseHistory();
	@DefaultMessage("Exercises")
	String Exercises();
	@DefaultMessage("Facebook group")
	String FacebookGroup();
	@DefaultMessage("Facebook groups")
	String FacebookGroups();
	@DefaultMessage("Fet")
	String Fet();
	@DefaultMessage("Fetch data")
	String FetchData();
	@DefaultMessage("Fetch the data from old xlGain.com.")
	String FetchFromXlGain();
	@DefaultMessage("Select what you want to fetch. Old data is removed before fetching data from xlGain.com. For example if you select ''Training'', all the current workouts/routines are removed before fething workouts/routines from xlGain.com!")
	String FetchSelectDesc();
	@DefaultMessage("Food")
	String Food();
	@DefaultMessage("Foods")
	String Foods();
	@DefaultMessage("Foods for {0}")
	String FoodsFor(String date);
	@DefaultMessage("Friends")
	String Friends();
	@DefaultMessage("Global")
	String Global();
	@DefaultMessage("Goal")
	String Goal();
	@DefaultMessage("Graph")
	String Graph();
	@DefaultMessage("Guide value")
	String GuideValue();
	@DefaultMessage("Guide values")
	String GuideValues();
	@DefaultMessage("Help")
	String Help();
	@DefaultMessage("You can share your data with your friends or with people in Facebook Groups.")
	String HelpShareGroups1();
	@DefaultMessage("to open Facebook in new window and create groups and select some friends on them. Close the window when you''re ready.")
	String HelpShareGroups2();
	@DefaultMessage("Hide")
	String Hide();
	@DefaultMessage("Hide {0}")
	String HideTarget(String target);
	@DefaultMessage("{0}h {1}min")
	String HourMin(int h, int m);
	@DefaultMessage("Info")
	String Info();
	@DefaultMessage("Last day")
	String LastDay();
	@DefaultMessage("Last month")
	String LastMonth();
	@DefaultMessage("Last six months")
	String LastSixMonths();
	@DefaultMessage("Last week")
	String LastWeek();
	@DefaultMessage("Last weights for this exercise")
	String LastWeightsForThisExercise();
	@DefaultMessage("Last year")
	String LastYear();
	@DefaultMessage("List")
	String List();
	@DefaultMessage("Loading")
	String Loading();
	@DefaultMessage("Lunch")
	String Lunch();
	@DefaultMessage("Main")
	String Main();
	@DefaultMessage("Mark as done")
	String MarkAsDone();
  @DefaultMessage("Max pulse")
  String MaxPulse();
	@DefaultMessage("Meal")
	String Meal();
	@DefaultMessage("Meals")
	String Meals();
	@DefaultMessage("Meal''s stats")
	String MealsStats();
	@DefaultMessage("Measurement")
	String Measurement();
	@DefaultMessage("Measurements")
	String Measurements();
	@DefaultMessage("Measurement value")
	String MeasurementValue();
	@DefaultMessage("Micronutrient")
	String Micronutrient();
	@DefaultMessage("Micronutrients")
	String Micronutrients();
	@DefaultMessage("Miles")
	String Miles();
	@DefaultMessage("Most popular")
	String MostPopular();
	@DefaultMessage("Motiver is a goal-oriented athlete's training and nutrition journal. The app is designed specifically for active gym trainees. Intelligent result tracking will adjust based on the user's needs and help achieve the desired goals.")
	String MotiverDesc();
	@DefaultMessage("Using Motiver is free and easy. You can sign in with your Facebook account, which makes it easy and safe - and no separate registration is required!")
	String MotiverDescAccount();
	@DefaultMessage("The data generated by Motiver can be used to easily follow personal development in training. Because of different analysis user can set clear goals, which motivates and encourages for better results.")
	String MotiverDescData();
	@DefaultMessage("FRIENDS feature allows you to share your workouts, diets or individual results with your friends, which makes the training more fun. Similarly, COACH feature allows you to share your profile with single person, for example with your personal trainer. Your trainer can then prepare workouts/diets and set desired goals for you.")
	String MotiverDescFriends();
	@DefaultMessage("Motiver.fi")
	String MotiverFi();
	@DefaultMessage("Motiver is still beta version so there still might be some problems. Please report us any problems!")
	String MotiverIsInBeta();
	@DefaultMessage("Move")
	String Move();
	@DefaultMessage("Move selected to ''{0}''")
	String MoveSelectedTo(String target);
	@DefaultMessage("Move {0}")
	String MoveTarget(String target);
	@DefaultMessage("Move ''{0}'' to {1}")
	String MoveTargetTo(String name, String target);
	@DefaultMessage("Move to ''{0}''")
	String MoveTo(String target);
	@DefaultMessage("My friends")
	String MyFriends();
  @DefaultMessage("My meals")
  String MyMeals();
  @DefaultMessage("My routines")
  String MyRoutines();
  @DefaultMessage("My workouts")
  String MyWorkouts();
	@DefaultMessage("Name")
	String Name();
	@DefaultMessage("Network error")
	String NetworkError();
	@DefaultMessage("{0} new comment(s)")
	String NewComments(int count);
	@DefaultMessage("No activities")
	String NoActivities();
	@DefaultMessage("Nobody")
	String Nobody();
	@DefaultMessage("No cardios")
	String NoCardios();
	@DefaultMessage("No comments")
	String NoComments();
	@DefaultMessage("No data")
	String NoData();
	@DefaultMessage("No exercises")
	String NoExercises();
	@DefaultMessage("No exercises found")
	String NoExercisesFound();
	@DefaultMessage("No foods")
	String NoFoods();
	@DefaultMessage("No friends :(")
	String NoFriends();
	@DefaultMessage("No meals")
	String NoMeals();
	@DefaultMessage("No meals found")
	String NoMealsFound();
	@DefaultMessage("No measurements")
	String NoMeasurements();
	@DefaultMessage("No micronutrients")
	String NoMicroNutrients();
	@DefaultMessage("No name")
	String NoName();
	@DefaultMessage("No permission")
	String NoPermission();
	@DefaultMessage("No permission to view this blog")
	String NoPermissionToViewThisBlog();
	@DefaultMessage("No recent comments")
	String NoRecentComments();
	@DefaultMessage("No routines")
	String NoRoutines();
	@DefaultMessage("No routines found")
	String NoRoutinesFound();
	@DefaultMessage("No runs")
	String NoRuns();
	@DefaultMessage("No stats selected")
	String NoStatsSelected();
	@DefaultMessage("No times")
	String NoTimes();
	@DefaultMessage("No trainees")
	String NoTrainees();
	@DefaultMessage("Your browser is not supported, so Motiver might not work properly. Click here to update your browser.")
	String NotSupportedAlert();
	@DefaultMessage("No values")
	String NoValues();
	@DefaultMessage("No workouts")
	String NoWorkouts();
	@DefaultMessage("No workouts found")
	String NoWorkoutsFound();
	@DefaultMessage("Nutrition")
	String Nutrition();
	@DefaultMessage("Nutrition (foods)")
	String NutritionFoods();
	@DefaultMessage("Offline mode is ON.")
	String OfflineModeIsOn();
	@DefaultMessage("Your browser''s version is not the newest one, so Motiver might not work properly. Click here to update your browser.")
	String OldBrowserAlert();
	@DefaultMessage("Only single value found")
	String OnlyOneValueFound();
	@DefaultMessage("Or")
	String Or();
	@DefaultMessage("Password")
	String Password();
	@DefaultMessage("Users can see any cardio you''ve done")
	String PermissionDescCardio();
	@DefaultMessage("Users can get FULL access to your profile! Use with caution!")
	String PermissionDescCoach();
	@DefaultMessage("Users can see measurements you''ve added")
	String PermissionDescMeasurements();
	@DefaultMessage("Users can browse your meals and see your total calories each day")
	String PermissionDescNutrition();
	@DefaultMessage("Users can also see all the foods you''ve eaten (not only total calories).")
	String PermissionDescNutritionFoods();
	@DefaultMessage("Users can browse your workouts and training history.")
	String PermissionDescTraining();
	@DefaultMessage("Permissions")
	String Permissions();
  @DefaultMessage("Here you can select which users can see your data. You can search for users and allow them access by dragging them to one of the boxes.")
  String PermissionsDesc();
	@DefaultMessage("Personal information")
	String PersonalInformation();
	@DefaultMessage("Please wait")
	String PleaseWait();
	@DefaultMessage("Portion")
	String Portion();
	@DefaultMessage("{0} pcs.")
	String Portions(String text);
	@DefaultMessage("1 piece")
	String PortionsOne();
	@DefaultMessage("Price")
	String Price();
	@DefaultMessage("Print view")
	String PrintView();
	@DefaultMessage("Profile")
	String Profile();
	@DefaultMessage("Protein")
	String Protein();
	@DefaultMessage("Publish also on Facebook")
	String PublishAlsoOnFacebook();
	@DefaultMessage("Pulse")
	String Pulse();
	@DefaultMessage("Ready to start?")
	String ReadyToStart();
	@DefaultMessage("Recent comments")
	String RecentComments();
	@DefaultMessage("Recovery drink")
	String RecoveryDrink();
	@DefaultMessage("Refresh")
	String Refresh();
	@DefaultMessage("Remove")
	String Remove();
	@DefaultMessage("Remove all {0}")
	String RemoveAllTarget(String target);
	@DefaultMessage("Are you sure you want to remove {0}?")
	String RemoveConfirm(String target);
	@DefaultMessage("Remove {0}")
	String RemoveTarget(String target);
	@DefaultMessage("Rename")
	String Rename();
	@DefaultMessage("Rename {0}")
	String RenameTarget(String target);
	@DefaultMessage("Report problem")
	String ReportProblem();
	@DefaultMessage("Report problem on this page")
	String ReportProblemOnThisPage();
	@DefaultMessage("Reps")
	String Reps();
	@DefaultMessage("Rest day")
	String RestDay();
	@DefaultMessage("Routine")
	String Routine();
	@DefaultMessage("Routines")
	String Routines();
	@DefaultMessage("Run")
	String Run();
  @DefaultMessage("Runs")
  String Runs();
	@DefaultMessage("Run value")
	String RunValue();
	@DefaultMessage("Save")
	String Save();
	@DefaultMessage("Saved!")
	String Saved();
	@DefaultMessage("Search")
	String Search();
	@DefaultMessage("Search results")
	String SearchResults();
	@DefaultMessage("Select")
	String Select();
	@DefaultMessage("Select date")
	String SelectDate();
	@DefaultMessage("''{0}'' selected")
	String Selected(String name);
	@DefaultMessage("Selected values")
	String SelectedValues();
	@DefaultMessage("Please select meal")
	String SelectMeal();
	@DefaultMessage("Please select routine")
	String SelectRoutine();

	@DefaultMessage("Please select workout")
	String SelectWorkout();
	@DefaultMessage("Select workout to ''{0}'' (day {1})")
	String SelectWorkoutToRoutine(String routine, int day);
	@DefaultMessage("Send")
	String Send();
	@DefaultMessage("Sets")
	String Sets();
	@DefaultMessage("Share {0} with")
	String ShareWith(String target);
	@DefaultMessage("Shortcut keys")
	String ShortcutKeys();
	@DefaultMessage("Show")
	String Show();
	@DefaultMessage("Show date")
	String ShowDate();
	@DefaultMessage("Show in graph")
	String ShowInGraph();
	@DefaultMessage("Show in list")
	String ShowInList();
	@DefaultMessage("Show more")
	String ShowMore();
	@DefaultMessage("Show {0}")
	String ShowTarget(String target);
	@DefaultMessage("Signing in")
	String SigningIn();
	@DefaultMessage("Signing out")
	String SigningOut();
  @DefaultMessage("Sign in")
  String SignIn();
	@DefaultMessage("Sign in to Facebook")
	String SignInToFacebook();
	@DefaultMessage("Sign in to see the blogs!")
	String SignInToSeeBlogs();
	@DefaultMessage("Sign out")
	String SignOut();
	@DefaultMessage("Snack")
	String Snack();
	@DefaultMessage("Statistics")
	String Statistics();
	@DefaultMessage("Supper")
	String Supper();
	@DefaultMessage("These times")
	String TheseTimes();

	@DefaultMessage("This {0}")
	String This(String target);
	@DefaultMessage("This cardio")
	String ThisCardio();
	@DefaultMessage("This exercise")
	String ThisExercise();
	@DefaultMessage("This food")
	String ThisFood();
	@DefaultMessage("This meal")
	String ThisMeal();
	@DefaultMessage("This measurement")
	String ThisMeasurement();
	@DefaultMessage("This routine")
	String ThisRoutine();
	@DefaultMessage("This run")
	String ThisRun();
	
	@DefaultMessage("This time")
	String ThisTime();
	@DefaultMessage("This value")
	String ThisValue();
	@DefaultMessage("This week")
	String ThisWeek();
	@DefaultMessage("This workout")
	String ThisWorkout();
	@DefaultMessage("Time")
	String Time();
	@DefaultMessage("Timeformat")
	String TimeFormat();
	@DefaultMessage("Times")
	String Times();
	@DefaultMessage("Time''s stats")
	String TimesStats();
	@DefaultMessage("Today")
	String Today();
	@DefaultMessage("Top 10 exercises")
	String Top10Exercises();
	@DefaultMessage("Top 10 meals")
	String Top10Meals();
	@DefaultMessage("Total")
	String Total();
	@DefaultMessage("Training")
	String Training();
	
	@DefaultMessage("Training day")
	String TrainingDay();
	@DefaultMessage("Training days")
	String TrainingDays();
	@DefaultMessage("Training times")
	String TrainingTimes();
	
	@DefaultMessage("Unit")
	String Unit();
	@DefaultMessage("Metric")
	String UnitMetric();
	@DefaultMessage("Units")
	String Units();
	@DefaultMessage("US")
	String UnitUS();
	@DefaultMessage("Unknown error")
	String UnknownError();
	@DefaultMessage("Update weight in profile")
	String UpdateWeightInProfile();
	@DefaultMessage("User information")
	String UserInformation();
	@DefaultMessage("Username")
	String Username();
	@DefaultMessage("Value")
	String Value();
	@DefaultMessage("Values as percent")
	String ValuesAsPercent();
	@DefaultMessage("Values should equal 100%")
	String ValuesShouldEqual100Percent();
	@DefaultMessage("Video")
	String Video();
	@DefaultMessage("Video URL")
	String VideoURL();
	@DefaultMessage("View on Motiver.fi")
	String ViewOnMotiver();
	
	
	@DefaultMessage("View your blog")
	String ViewYourBlog();
	@DefaultMessage("Waiting")
	String Waiting();
	@DefaultMessage("Weights")
	String Weights();
	@DefaultMessage("Workout")
	String Workout();
	@DefaultMessage("Workouts")
	String Workouts();
	@DefaultMessage("Write your comment")
	String WriteYourComment();
	@DefaultMessage("Your blog")
	String YourBlog();
	@DefaultMessage("Your friends on Motiver")
	String YourFriends();
	@DefaultMessage("Your trainees")
	String YourTrainees();
  @DefaultMessage("Custom alias")
  String CustomAlias();
  @DefaultMessage("Custom URL for your blog. Blog URL will be: ''http://alias.motiver.fi''.")
  String CustomAliasDesc();
  @DefaultMessage("This alias is already taken")
  String AliasTaken();
  @DefaultMessage("Monthly report")
  String MonthlyReport();
  @DefaultMessage("One rep maxes")
  String OneRepMaxes();
  @DefaultMessage("Best sets")
  String BestSets();
  @DefaultMessage("Language")
  
  String Language();
  @DefaultMessage("You need to refresh the page after changing the language")
  String LanguageDesc();

  @DefaultMessage("No users found")
  String NoUsersFound();
  @DefaultMessage("No users added")
  String NoUsersAdded();
  @DefaultMessage("Drop to give these permissions to: {0}")
  String DropToGiveThesePermissionTo(String email);
  @DefaultMessage("This user")
  String ThisUser();
  @DefaultMessage("Search users")
  String SearchUsers();
  @DefaultMessage("Include all users")
  String IncludeAllUsers();
  @DefaultMessage("Only letters allowed!")
  String OnlyLettersAllowed();

  @DefaultMessage("This field is required")
  String FieldBlankText();
  @DefaultMessage("The value in this field is invalid")
  String FieldInvalidText();
  @DefaultMessage("The maximum length for this field is {0}")
  String FieldMaxLengthText(int max);
  @DefaultMessage("Enter at least {0} characters")
  String FieldMinLengthText(int min);
  @DefaultMessage("The time must be in HH:MM format")
  String FieldTimeFormat();
  @DefaultMessage("The url is not in correct format")
  String FieldUrlFormat();
  @DefaultMessage("The maximum value is {0}")
  String FieldMaxText(int max);
  @DefaultMessage("The minimum value is {0}")
  String FieldMinText(int min);
  @DefaultMessage("Value is not a valid number")
  String FieldNanText();
  @DefaultMessage("The value must be greater or equal to 0")
  String FieldNegativeText();
 }
