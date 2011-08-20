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

public interface LangConstantsWizard extends Constants {

	@DefaultStringArrayValue({"1-2 times in a week", "3-4 times in a week", "5-6 times in a week"})
	String[] HowOften();
	@DefaultStringArrayValue({"2 times in a week", "3 times in a week", "4 times in a week"})
	String[] HowOftenStrength();
	@DefaultStringArrayValue({"Quads", "Hamstrings", "Chest", "Back", "Biceps", "Triceps", "Shoulders", "Calves"})
	String[] MuscleGroups();
	@DefaultStringArrayValue({"Squat / deadlift / bench", "Squat", "Deadlift", "Bench"})
	String[] Priority();
	@DefaultStringArrayValue({"Full body workout", "2-split", "3-split", "4-split"})
	String[] Split();
	@DefaultStringArrayValue({"Legs,abs / upperbody", "Legs,arms / Back,chest,shoulders,abs", "Quads,chest,shoulders,triceps / Back,hamstrings,calves,biceps"})
	String[] SplitDivision2();
	@DefaultStringArrayValue({"Chest,shoulders,triceps / Legs / Back,biceps", "Chest,back / Legs / Shoulders,arms", "Chest,arms / Legs / Back,shoulders"})
	String[] SplitDivision3();
	@DefaultStringArrayValue({"Chest,biceps / Legs / Shoulders,triceps / Back", "Arms / Legs / Chest,shoulders / Back", "Chest,shoulders / Queads,calves / Back,triceps / Hamstrings,calves,biceps"})
	String[] SplitDivision4();
	@DefaultStringArrayValue({"1 training day / 2 rest days (1on, 2off)"})
	String[] TrainingDays1();
	@DefaultStringArrayValue({"Every other day", "1on 1off, 1on 1off, 1on 2off", "2on 1off, 2on 2off"})
	String[] TrainingDays2();
	@DefaultStringArrayValue({"3on 1off, 2on 1off", "5 training days, 2 rest days", "6 training days, 1 rest day"})
	String[] TrainingDays3();
}
