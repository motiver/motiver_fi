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

public interface LangConstants extends Constants {

	@DefaultStringArrayValue({"(no category)", "Drinks", "Meat", "Fish", "Breads", "Eggs", "Fat", "Milk", "Cereals", "Potatoes", "Sallads", "Vegetables", "Fruits & berries", "Sugar & candys", "Miscellaneous", "Nutritional supplements", "Fast foods", "Meals", "Pizzas", "Cold cuts and sausages"})
	String[] Categories();
	
	@DefaultStringArrayValue({
		"",	//0
		"Alanine",
		"Arginine",
		"Aspartic acid (aspartate)",
		"Asparagine",
		"Cystine",
		"Glutamic acid (glutamate)",
		"Glutamine",
		"Glycine",
		"Histidine",
		"Isoleucine",	//10
		"Leucine",
		"Lysine",
		"Methionine",
		"Phenylalanine",
		"Proline",
		"Serine",
		"Threonine",
		"Tryptophan",
		"Tyrosine",
		"Valine",	//20
		"EPA",
		"DPA",
		"DHA",
		"Vitamin A",
		"Vitamin B",
		"Vitamin C",
		"Vitamin D",
		"Vitamin E",
		"Vitamin K",
		"Calcium",	//30
		"Chloride",
		"Magnesium",
		"Phosphorus",
		"Potassium",
		"Sodium",
		"Iron",
		"Taurine",
		"Caffeine"
	})
	String[] MicroNutrients();

	@DefaultStringArrayValue({"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"})
	String[] Month();

	@DefaultStringArrayValue({"Awful", "Below average", "Average", "Good", "Excellent"})
	String[] Rating();

	@DefaultStringArrayValue({" - ", "Barbell", "Dumbbell", "Cable", "Machine", "EZ barbell", "Kettlebell"})
	String[] Targets();

	@DefaultStringArrayValue({"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"})
	String[] WeekDays();
}
