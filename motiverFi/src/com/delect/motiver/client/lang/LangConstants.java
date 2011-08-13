// $codepro.audit.disable methodNamingConvention
/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
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
