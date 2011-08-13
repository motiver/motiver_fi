// $codepro.audit.disable methodNamingConvention
/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.lang;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale("en")
public interface LangWizard extends Messages {

	 
	 @DefaultMessage("Bench press")
	 String Bench();
	 @DefaultMessage("Bent-over row")
	 String BentOverRow();
	 @DefaultMessage("Bicep curl")
	 String BicepCurl();
	 //routine wizard
	 @DefaultMessage("Bodybuilding")
	 String Bodybuilding();
	 @DefaultMessage("Chin-up")
	 String ChinUp();
	 @DefaultMessage("Close grip bench press")
	 String CloseGripBenchPress();
	 @DefaultMessage("Close grip pulldown")
	 String CloseGripPulldown();
	 @DefaultMessage("Concentration curl")
	 String ConcentrationCurl();
	 @DefaultMessage("Here you can create a bodybuilding routine to build muscle. We will create correct routine based on your selections.")
	 String CreateDesc();
	 @DefaultMessage("From here you can create powerlifting routines to increase your strength. We will calculate the correct weights to do each week. This will ensure that there is correct progression in your routine.")
	 String CreateStrengthDesc();
	 @DefaultMessage("Crunches")
	 String Crunches();
	 @DefaultMessage("Deadlift")
	 String Deadlift();
	 @DefaultMessage("Dip")
	 String Dip();
	 @DefaultMessage("Exercises'' max")
	 String ExerciseMax();
	 @DefaultMessage("Here you need to enter your current max weights (in lbs or kg) for these four exercises. We need these to calculate correct progression for you.")
	 String ExerciseMaxDesc();
	 @DefaultMessage("Flies")
	 String Flies();
	 @DefaultMessage("Front pulldown")
	 String FrontPulldown();
	 @DefaultMessage("Front squat")
	 String FrontSquat();
	 @DefaultMessage("How often")
	 String HowOften();
	 @DefaultMessage("How often you have the time to exercise? This depends on your goals, but three or four 1 hour exercises per week is average. With full body workout even less is enough. 1on 1off = one training day, on rest day")
	 String HowOftenDesc();
	 @DefaultMessage("How often you have the time to exercise? Powerlifting routines doesn''t need the same frequency as bodybuilding routines. If this is your first powerlifting routine you should select no more than 3 times a week!")
	 String HowOftenStrengthDesc();
	 @DefaultMessage("Incline bench press")
	 String InclineBenchPress();
	 @DefaultMessage("Lateral raise")
	 String LateralRaise();
	 @DefaultMessage("Leg extension")
	 String LegExtension();
	 @DefaultMessage("Leg press")
	 String LegPress();
	 @DefaultMessage("Lying triceps extension")
	 String LyingTricepsExtension();
	 @DefaultMessage("Shoulder press")
	 String Military();
	 @DefaultMessage("Back")
	 String MuscleBack();
	 @DefaultMessage("Muscle group divisions")
	 String MuscleGroupDivisions();
	 @DefaultMessage("Powerlifting")
	 String Powerlifting();
	 @DefaultMessage("Powerlifting routine")
	 String PowerliftingRoutine();
	 @DefaultMessage("Priority")
	 String Priority();
	 @DefaultMessage("Select what is your highest priority. For example when you select ''Bench press'', training will be focused to exercises which increase your bench.")
	 String PriorityDesc();
	 @DefaultMessage("Rear lateral raise")
	 String RearLateralRaise();
	 @DefaultMessage("Routine created")
	 String RoutineCreated();
	 @DefaultMessage("Seated calf extension")
	 String SeatedCalfExtension();
	 @DefaultMessage("Seated leg curl")
	 String SeatedLegCurl();
	 @DefaultMessage("Seated row")
	 String SeatedRow();
	 @DefaultMessage("Shoulder press")
	 String ShoulderPress();
	 @DefaultMessage("Shrug")
	 String Shrug();
	 @DefaultMessage("Situps")
	 String Situps();
	 @DefaultMessage("Split")
	 String Split();
	 @DefaultMessage("Splitting means dividing the workout to different days. Usually 2- and 3-splits works the best for the beginners")
	 String SplitDesc();
	 @DefaultMessage("Squat")
	 String Squat();
	 @DefaultMessage("Squat, bench")
	 String SquatBench();
	 @DefaultMessage("Standing calf raise")
	 String StandingCalfRaise();
	 @DefaultMessage("Standing flies")
	 String StandingFlies();
	 @DefaultMessage("Straight leg deadlift")
	 String StraightLegDeadlift();
	 @DefaultMessage("Tricep extension")
	 String TricepExtension();
	 @DefaultMessage("Weak muscle group")
	 String WeakMuscle();
	 @DefaultMessage("Let''s face it, we all have at least one muscle group which is lagging behind! Select yours so it will get an extra attention.")
	 String WeakMuscleDesc();
	 @DefaultMessage("Wrist curl")
	 String WristCurl();

}
