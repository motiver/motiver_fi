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
  
  @DefaultMessage("P‰‰sivulla n‰et aktiviteetti historia")
  String Main1();
  @DefaultMessage("Treeni-osiossa voi merkit‰ treenisi")
  String Main2();
  @DefaultMessage("Ravinto-osiossa voit laskea kalorit")
  String Main3();
  @DefaultMessage("Aerobinen kuvaus")
  String Main4();
  @DefaultMessage("Tilastot kuvaus")
  String Main5();
  @DefaultMessage("Profiili kuvaus")
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
  @DefaultMessage("Nyt voit valita harjoituksen joko ruksaamalla sen vieress‰ olevaa valintaruutua tai klikkaamalla harjoituksen nime‰.")
  String WorkoutCreate10();
  @DefaultMessage("Lis‰‰ harjoitus valitulle p‰iv‰lle painamalla ''Siirr‰ ...''-nappia")
  String WorkoutCreate11();
  @DefaultMessage("Harjoitus on nyt tallennettu t‰lle p‰iv‰lle ja muokata sit‰ sen perusteella mit‰ olet salilla tehnyt. Tekem‰si muutokset tallentuvat vain t‰lle p‰iv‰lle, joten alkuper‰inen treeni s‰ilyy ennallaan.")
  String WorkoutCreate12();
  @DefaultMessage("Harjoitusosion tutustumiskierros valmis. Voit kokeilla tehd‰ myˆs harjoitusohjelmia aivan sivun alareunasta. Harjoitusohjelma sis‰lt‰‰ useita harjoituksia, joten voit suunnitella vaikka koko viikon treenin kerralla.")
  String WorkoutCreate13();

}
