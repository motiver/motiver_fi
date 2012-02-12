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
public interface LangTutorial extends Messages {

  @DefaultMessage("Welcome to use Motiver. From here you can go through all the features, so you know how to use Motiver. Click ''Next'' button to start.")
  String Main0();
  @DefaultMessage("On main page you can see all the activities you''ve done. It is of course empty when you sign in for the first time.")
  String Main1();
  @DefaultMessage("On training section there are all the workouts you''ve done. You can create your own or search workouts made by other users.")
  String Main2();
  @DefaultMessage("Here you can create your own meals and calculate the total calories for each day")
  String Main3();
  @DefaultMessage("Cardio section can be used to save runs and other sport specific training.")
  String Main4();
  @DefaultMessage("Stats sections contains many useful graphs for you.")
  String Main5();
  @DefaultMessage("From profile section you see your profile and can give permissions to other users to view your activities.")
  String Main6();
  
  @DefaultMessage("Now we go through features in Training section. Open section by selection ''Training'' from top menu.")
  String WorkoutCreate1();
  @DefaultMessage("In calendar view you can see workouts added to each day. From calendar above the view you can select any day you want.")
  String WorkoutCreate2();
  @DefaultMessage("Under the calendar view there are your workouts. There you can create and edit new workouts. Click the header to open the panel.")
  String WorkoutCreate3();
  @DefaultMessage("On the left you can see your own workouts and with search you can look for workouts made by other users. Try now to create new workout by clicking ''Create workout'' button.")
  String WorkoutCreate4();
  @DefaultMessage("Give descriptive name for your workout. Name could be, for example, ''Chest, triceps''.")
  String WorkoutCreate5();
  @DefaultMessage("Now you can edit the workout as you like. Move cursor over the workout/exercise to see different action icons. Look next guide for how to add new exercise.")
  String WorkoutCreate6();
  @DefaultMessage("Add new exercise by searching it. For example, enter ''Bench'' as search word and select exercise from the list.")
  String WorkoutCreate7();
  @DefaultMessage("You can also edit sets, reps and weights. Look next guide when you''re ready.")
  String WorkoutCreate8();
  @DefaultMessage("Now we add workout to some day. Select day from calendar on top of the page and click ''Add workout''.")
  String WorkoutCreate9();
  @DefaultMessage("Now you can choose the workout by selecting its checkbox or clicking the name of the workout.")
  String WorkoutCreate10();
  @DefaultMessage("Add workout to selected day by clicking ''Move ...'' button")
  String WorkoutCreate11();
  @DefaultMessage("Workout is now saved to this day and you can edit it based on what you''ve done in gym. Changes will be saved to this day only, so original workout remains unchanged.")
  String WorkoutCreate12();
  @DefaultMessage("You finished the tutorial for the training section. You can also try to create training routines from bottom of the page. Training routine contains multiple workouts, so you can plan whole week''s workouts at once.")
  String WorkoutCreate13();
  
  @DefaultMessage("Tutorials for other sections are coming soon. Feel free to try them yourself before that!")
  String Nutrition1();

}
