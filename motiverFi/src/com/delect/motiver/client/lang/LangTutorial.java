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

import com.google.gwt.i18n.client.Constants.DefaultStringArrayValue;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.Messages;

@DefaultLocale("en")
public interface LangTutorial extends Constants {

  @DefaultStringArrayValue({
    "P‰‰sivulla n‰et aktiviteetti historia",
    "Treeni-osiossa voi merkit‰ treenisi",
    "Ravinto-osiossa voit laskea kalorit",
    "Aerobinen kuvaus",
    "Tilastot kuvaus",
    "Profiili kuvaus"
  })
  String[] Main();
  
  @DefaultStringArrayValue({
    "Nyt yrit‰ luoda treeni. Mene treeniosioon",
    "Kalenteri n‰kym‰n kuvaus",
    "Omien treenien kuvaus. Klikkaa avataksesi.",
    "Luo uusi treeni. Paina nappia",
    "Anna nimi",
    "Nyt voit muokata harjoitusta. Vie hiiri harjoituksen/liikkeen p‰‰lle n‰hd‰ksesi eri toiminto-ikonit. Klikkaa seuraava niin n‰et kuinka voit lis‰t‰ liikkeen.",
    "Lis‰‰ nyt jokin liike. Anna hakusanaksi esim. ''Penkki'' ja valitse listasta haluamasi liike.",
    "Voit muokata halutessasi myˆs sarjoja, toistoja ja painoja. Klikkaa ''Seuraava ohje'' kun olet valmis.",
    "Nyt voit lis‰t‰ tekem‰si harjoituksen jollekin p‰iv‰lle. Valitse ylh‰‰lt‰ jokin p‰iv‰ ja klikkaa ''Lis‰‰ harjoitus''",
    "Nyt voit valita haluamasi harjoituksen joko klikkaamalla sen vieress‰ olevaa valintaruutua tai klikkaamalla harjoituksen nime‰.",
    "Lis‰‰ harjoitus painamalla ''Siirr‰ ...''-nappia",
    "Nyt voit muokata harjoitusta mielesi mukaan ja n‰et myˆhemmin mit‰ olet t‰n‰ p‰iv‰n‰ tehnyt.",
    "Harjoitusosion tutustumiskierros valmis. Nyt tied‰t miten luoda uusi harjoitus ja siirt‰‰ sen p‰iv‰lle jolloin olit salilla."
  })
  String[] WorkoutCreate();

}
