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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.view.MySpinnerField;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

public abstract class Functions {
	
	public interface MessageBoxHandler {
		void okPressed(String text);
	}
	public static DateTimeFormat Fmt = DateTimeFormat.getFormat( StringConstants.DATEFORMATS[AppController.User.getDateFormat()] );


	public static DateTimeFormat FmtShort = DateTimeFormat.getFormat( StringConstants.DATEFORMATS_SHORT[AppController.User.getDateFormat()] );
  //	public static DateTimeFormat FmtDay = DateTimeFormat.getFormat( StringConstants.DATEFORMATS[AppController.User.getDateFormat()]+" EEEE" );
	
	/**
	 * Converts value to correct unit
	 * @param value : in metric IN GRAMS (for example from database)
	 * @return
	 */
	public static String convertNutritionValueFromDB(double value) {
		
		//if metric -> just return value with correct unit
		if(AppController.User.getMeasurementSystem() == 0) {
			int unit = 2;
			//shown in micrograms
			if(Math.abs(value) < 0.001 && value > 0) {
				value *= 1000000;
				unit = 0;
			}
			//show in milligrams
			else if(Math.abs(value) < 0.1 && value > 0) {
				value *= 1000;
				unit = 1;
			}
			//ELSE show in grams
			return NumberFormat.getFormat("0.0").format(value) + " " + StringConstants.MEAS_METRIC[unit];
			
		}
		//US system
		else {
			//TODO measurement system US (different from metric??)
			
			int unit = 2;
			//shown in micrograms
			if(Math.abs(value) < 0.001) {
				value *= 1000000;
				unit = 0;
			}
			//show in milligrams
			else if(Math.abs(value) < 0.1) {
				value *= 1000;
				unit = 1;
			}
			//ELSE show in grams
			return NumberFormat.getFormat("0.0").format(value) + " " + StringConstants.MEAS_US[unit];

		}

		
	}
//	/**
//	 * To be called after an XFBML-tag has been inserted into the DOM
//	 * @param id : container id
//	 */
//	public static native void fbParseDomTree(String id) /*-{
//    if($wnd.FB)
//      if($wnd.FB.XFBML)
//        $wnd.FB.XFBML.parse($wnd.document.getElementById(id));
//	}-*/;
	
	/**
	 * Find start of the week of given date. If date is monday -> returns same date 
	 * Parameters: date (in seconds)
	 * @param d : date (in seconds)
	 * @return : date (in seconds)
	 */
	@SuppressWarnings("deprecation")
	public static long findPreviousMonday(long d) {

		Date dMon = new Date();
		dMon.setYear(2010);
		dMon.setMonth(7);
		dMon.setDate(22);
		
		String mon = DateTimeFormat.getFormat("E").format(dMon);
		//get monday from given week
		long dMonday = d * 1000;
		for(int i=0; i < 7; i++) {
			dMonday = d * 1000 - 1000 * 3600 * 24 * i;
			if(DateTimeFormat.getFormat("E").format(new Date(dMonday)).equals(mon)) {
				break;
			}
		}
		return dMonday / 1000;
	}
	
	/*
	 * Populates timezone liststore
	 */
	public static final ListStore<CountryModel> getCountries() {
		ListStore<CountryModel> store = new ListStore<CountryModel>(); 
		store.add(new CountryModel("fi", "Finland"));
    //        store.add(new CountryModel("ac", "Ascension Island"));
    //        store.add(new CountryModel("ad", "Andorra"));
    //        store.add(new CountryModel("ae", "United Arab Emirates"));
    //        store.add(new CountryModel("af", "Afghanistan"));
    //        store.add(new CountryModel("ag", "Antigua and Barbuda"));
    //        store.add(new CountryModel("ai", "Anguilla"));
    //        store.add(new CountryModel("al", "Albania"));
    //        store.add(new CountryModel("am", "Armenia"));
    //        store.add(new CountryModel("an", "Netherlands Antilles"));
    //        store.add(new CountryModel("ao", "Angola"));
    //        store.add(new CountryModel("aq", "Antarctica"));
    //        store.add(new CountryModel("ar", "Argentina"));
    //        store.add(new CountryModel("as", "American Samoa"));
    //        store.add(new CountryModel("at", "Austria"));
    //        store.add(new CountryModel("au", "Australia"));
    //        store.add(new CountryModel("aw", "Aruba"));
    //        store.add(new CountryModel("az", "Azerbaijan"));
    //        store.add(new CountryModel("ba", "Bosnia and Herzegovina"));
    //        store.add(new CountryModel("bb", "Barbados"));
    //        store.add(new CountryModel("bd", "Bangladesh"));
    //        store.add(new CountryModel("be", "Belgium"));
    //        store.add(new CountryModel("bf", "Burkina Faso"));
    //        store.add(new CountryModel("bg", "Bulgaria"));
    //        store.add(new CountryModel("bh", "Bahrain"));
    //        store.add(new CountryModel("bi", "Burundi"));
    //        store.add(new CountryModel("bj", "Benin"));
    //        store.add(new CountryModel("bm", "Bermuda"));
    //        store.add(new CountryModel("bn", "Brunei Darussalam"));
    //        store.add(new CountryModel("bo", "Bolivia"));
    //        store.add(new CountryModel("br", "Brazil"));
    //        store.add(new CountryModel("bs", "Bahamas"));
    //        store.add(new CountryModel("bt", "Bhutan"));
    //        store.add(new CountryModel("bv", "Bouvet Island"));
    //        store.add(new CountryModel("bw", "Botswana"));
    //        store.add(new CountryModel("by", "Belarus"));
    //        store.add(new CountryModel("bz", "Belize"));
    //        store.add(new CountryModel("ca", "Canada"));
    //        store.add(new CountryModel("cc", "Cocos (Keeling) Islands"));
    //        store.add(new CountryModel("cd", "Congo, Democratic Republic of the"));
    //        store.add(new CountryModel("cf", "Central African Republic"));
    //        store.add(new CountryModel("cg", "Congo, Republic of"));
    //        store.add(new CountryModel("ch", "Switzerland"));
    //        store.add(new CountryModel("ci", "Cote d'Ivoire"));
    //        store.add(new CountryModel("ck", "Cook Islands"));
    //        store.add(new CountryModel("cl", "Chile"));
    //        store.add(new CountryModel("cm", "Cameroon"));
    //        store.add(new CountryModel("cn", "China"));
    //        store.add(new CountryModel("co", "Colombia"));
    //        store.add(new CountryModel("cr", "Costa Rica"));
    //        store.add(new CountryModel("cu", "Cuba"));
    //        store.add(new CountryModel("cv", "Cap Verde"));
    //        store.add(new CountryModel("cx", "Christmas Island"));
    //        store.add(new CountryModel("cy", "Cyprus"));
    //        store.add(new CountryModel("cz", "Czech Republic"));
    //        store.add(new CountryModel("de", "Germany"));
    //        store.add(new CountryModel("dj", "Djibouti"));
    //        store.add(new CountryModel("dk", "Denmark"));
    //        store.add(new CountryModel("dm", "Dominica"));
    //        store.add(new CountryModel("do", "Dominican Republic"));
    //        store.add(new CountryModel("dz", "Algeria"));
    //        store.add(new CountryModel("ec", "Ecuador"));
    //        store.add(new CountryModel("ee", "Estonia"));
    //        store.add(new CountryModel("eg", "Egypt"));
    //        store.add(new CountryModel("eh", "Western Sahara"));
    //        store.add(new CountryModel("er", "Eritrea"));
    //        store.add(new CountryModel("es", "Spain"));
    //        store.add(new CountryModel("et", "Ethiopia"));
    //        store.add(new CountryModel("fj", "Fiji"));
    //        store.add(new CountryModel("fk", "Falkland Islands (Malvina)"));
    //        store.add(new CountryModel("fm", "Micronesia, Federal State of"));
    //        store.add(new CountryModel("fo", "Faroe Islands"));
    //        store.add(new CountryModel("fr", "France"));
    //        store.add(new CountryModel("ga", "Gabon"));
    //        store.add(new CountryModel("gd", "Grenada"));
    //        store.add(new CountryModel("ge", "Georgia"));
    //        store.add(new CountryModel("gf", "French Guiana"));
    //        store.add(new CountryModel("gg", "Guernsey"));
    //        store.add(new CountryModel("gh", "Ghana"));
    //        store.add(new CountryModel("gi", "Gibraltar"));
    //        store.add(new CountryModel("gl", "Greenland"));
    //        store.add(new CountryModel("gm", "Gambia"));
    //        store.add(new CountryModel("gn", "Guinea"));
    //        store.add(new CountryModel("gp", "Guadeloupe"));
    //        store.add(new CountryModel("gq", "Equatorial Guinea"));
    //        store.add(new CountryModel("gr", "Greece"));
    //        store.add(new CountryModel("gs", "South Georgia and the South Sandwich Islands"));
    //        store.add(new CountryModel("gt", "Guatemala"));
    //        store.add(new CountryModel("gu", "Guam"));
    //        store.add(new CountryModel("gw", "Guinea-Bissau"));
    //        store.add(new CountryModel("gy", "Guyana"));
    //        store.add(new CountryModel("hk", "Hong Kong"));
    //        store.add(new CountryModel("hm", "Heard and McDonald Islands"));
    //        store.add(new CountryModel("hn", "Honduras"));
    //        store.add(new CountryModel("hr", "Croatia/Hrvatska"));
    //        store.add(new CountryModel("ht", "Haiti"));
    //        store.add(new CountryModel("hu", "Hungary"));
    //        store.add(new CountryModel("id", "Indonesia"));
    //        store.add(new CountryModel("ie", "Ireland"));
    //        store.add(new CountryModel("il", "Israel"));
    //        store.add(new CountryModel("im", "Isle of Man"));
    //        store.add(new CountryModel("in", "India"));
    //        store.add(new CountryModel("io", "British Indian Ocean Territory"));
    //        store.add(new CountryModel("iq", "Iraq"));
    //        store.add(new CountryModel("ir", "Iran (Islamic Republic of)"));
    //        store.add(new CountryModel("is", "Iceland"));
    //        store.add(new CountryModel("it", "Italy"));
    //        store.add(new CountryModel("je", "Jersey"));
    //        store.add(new CountryModel("jm", "Jamaica"));
    //        store.add(new CountryModel("jo", "Jordan"));
    //        store.add(new CountryModel("jp", "Japan"));
    //        store.add(new CountryModel("ke", "Kenya"));
    //        store.add(new CountryModel("kg", "Kyrgyzstan"));
    //        store.add(new CountryModel("kh", "Cambodia"));
    //        store.add(new CountryModel("ki", "Kiribati"));
    //        store.add(new CountryModel("km", "Comoros"));
    //        store.add(new CountryModel("kn", "Saint Kitts and Nevis"));
    //        store.add(new CountryModel("kp", "Korea, Democratic People's Republic"));
    //        store.add(new CountryModel("kr", "Korea, Republic of"));
    //        store.add(new CountryModel("kw", "Kuwait"));
    //        store.add(new CountryModel("ky", "Cayman Islands"));
    //        store.add(new CountryModel("kz", "Kazakhstan"));
    //        store.add(new CountryModel("la", "Lao People's Democratic Republic"));
    //        store.add(new CountryModel("lb", "Lebanon"));
    //        store.add(new CountryModel("lc", "Saint Lucia"));
    //        store.add(new CountryModel("li", "Liechtenstein"));
    //        store.add(new CountryModel("lk", "Sri Lanka"));
    //        store.add(new CountryModel("lr", "Liberia"));
    //        store.add(new CountryModel("ls", "Lesotho"));
    //        store.add(new CountryModel("lt", "Lithuania"));
    //        store.add(new CountryModel("lu", "Luxembourg"));
    //        store.add(new CountryModel("lv", "Latvia"));
    //        store.add(new CountryModel("ly", "Libyan Arab Jamahiriya"));
    //        store.add(new CountryModel("ma", "Morocco"));
    //        store.add(new CountryModel("mc", "Monaco"));
    //        store.add(new CountryModel("md", "Moldova, Republic of"));
    //        store.add(new CountryModel("mg", "Madagascar"));
    //        store.add(new CountryModel("mh", "Marshall Islands"));
    //        store.add(new CountryModel("mk", "Macedonia, Former Yugoslav Republic"));
    //        store.add(new CountryModel("ml", "Mali"));
    //        store.add(new CountryModel("mm", "Myanmar"));
    //        store.add(new CountryModel("mn", "Mongolia"));
    //        store.add(new CountryModel("mo", "Macau"));
    //        store.add(new CountryModel("mp", "Northern Mariana Islands"));
    //        store.add(new CountryModel("mq", "Martinique"));
    //        store.add(new CountryModel("mr", "Mauritania"));
    //        store.add(new CountryModel("ms", "Montserrat"));
    //        store.add(new CountryModel("mt", "Malta"));
    //        store.add(new CountryModel("mu", "Mauritius"));
    //        store.add(new CountryModel("mv", "Maldives"));
    //        store.add(new CountryModel("mw", "Malawi"));
    //        store.add(new CountryModel("mx", "Mexico"));
    //        store.add(new CountryModel("my", "Malaysia"));
    //        store.add(new CountryModel("mz", "Mozambique"));
    //        store.add(new CountryModel("na", "Namibia"));
    //        store.add(new CountryModel("nc", "New Caledonia"));
    //        store.add(new CountryModel("ne", "Niger"));
    //        store.add(new CountryModel("nf", "Norfolk Island"));
    //        store.add(new CountryModel("ng", "Nigeria"));
    //        store.add(new CountryModel("ni", "Nicaragua"));
    //        store.add(new CountryModel("nl", "Netherlands"));
    //        store.add(new CountryModel("no", "Norway"));
    //        store.add(new CountryModel("np", "Nepal"));
    //        store.add(new CountryModel("nr", "Nauru"));
    //        store.add(new CountryModel("nu", "Niue"));
    //        store.add(new CountryModel("nz", "New Zealand"));
    //        store.add(new CountryModel("om", "Oman"));
    //        store.add(new CountryModel("pa", "Panama"));
    //        store.add(new CountryModel("pe", "Peru"));
    //        store.add(new CountryModel("pf", "French Polynesia"));
    //        store.add(new CountryModel("pg", "Papua New Guinea"));
    //        store.add(new CountryModel("ph", "Philippines"));
    //        store.add(new CountryModel("pk", "Pakistan"));
    //        store.add(new CountryModel("pl", "Poland"));
    //        store.add(new CountryModel("pm", "St. Pierre and Miquelon"));
    //        store.add(new CountryModel("pn", "Pitcairn Island"));
    //        store.add(new CountryModel("pr", "Puerto Rico"));
    //        store.add(new CountryModel("ps", "Palestinian Territories"));
    //        store.add(new CountryModel("pt", "Portugal"));
    //        store.add(new CountryModel("pw", "Palau"));
    //        store.add(new CountryModel("py", "Paraguay"));
    //        store.add(new CountryModel("qa", "Qatar"));
    //        store.add(new CountryModel("re", "Reunion Island"));
    //        store.add(new CountryModel("ro", "Romania"));
    //        store.add(new CountryModel("ru", "Russian Federation"));
    //        store.add(new CountryModel("rw", "Rwanda"));
    //        store.add(new CountryModel("sa", "Saudi Arabia"));
    //        store.add(new CountryModel("sb", "Solomon Islands"));
    //        store.add(new CountryModel("sc", "Seychelles"));
    //        store.add(new CountryModel("sd", "Sudan"));
    //        store.add(new CountryModel("se", "Sweden"));
    //        store.add(new CountryModel("sg", "Singapore"));
    //        store.add(new CountryModel("sh", "St. Helena"));
    //        store.add(new CountryModel("si", "Slovenia"));
    //        store.add(new CountryModel("sj", "Svalbard and Jan Mayen Islands"));
    //        store.add(new CountryModel("sk", "Slovak Republic"));
    //        store.add(new CountryModel("sl", "Sierra Leone"));
    //        store.add(new CountryModel("sm", "San Marino"));
    //        store.add(new CountryModel("sn", "Senegal"));
    //        store.add(new CountryModel("so", "Somalia"));
    //        store.add(new CountryModel("sr", "Suriname"));
    //        store.add(new CountryModel("st", "Sao Tome and Principe"));
    //        store.add(new CountryModel("sv", "El Salvador"));
    //        store.add(new CountryModel("sy", "Syrian Arab Republic"));
    //        store.add(new CountryModel("sz", "Swaziland"));
    //        store.add(new CountryModel("tc", "Turks and Caicos Islands"));
    //        store.add(new CountryModel("td", "Chad"));
    //        store.add(new CountryModel("tf", "French Southern Territories"));
    //        store.add(new CountryModel("tg", "Togo"));
    //        store.add(new CountryModel("th", "Thailand"));
    //        store.add(new CountryModel("tj", "Tajikistan"));
    //        store.add(new CountryModel("tk", "Tokelau"));
    //        store.add(new CountryModel("tm", "Turkmenistan"));
    //        store.add(new CountryModel("tn", "Tunisia"));
    //        store.add(new CountryModel("to", "Tonga"));
    //        store.add(new CountryModel("tp", "East Timor"));
    //        store.add(new CountryModel("tr", "Turkey"));
    //        store.add(new CountryModel("tt", "Trinidad and Tobago"));
    //        store.add(new CountryModel("tv", "Tuvalu"));
    //        store.add(new CountryModel("tw", "Taiwan"));
    //        store.add(new CountryModel("tz", "Tanzania"));
    //        store.add(new CountryModel("ua", "Ukraine"));
    //        store.add(new CountryModel("ug", "Uganda"));
    //        store.add(new CountryModel("uk", "United Kingdom"));
    //        store.add(new CountryModel("um", "US Minor Outlying Islands"));
    //        store.add(new CountryModel("us", "United States"));
    //        store.add(new CountryModel("uy", "Uruguay"));
    //        store.add(new CountryModel("uz", "Uzbekistan"));
    //        store.add(new CountryModel("va", "Holy See (City Vatican State)"));
    //        store.add(new CountryModel("vc", "Saint Vincent and the Grenadines"));
    //        store.add(new CountryModel("ve", "Venezuela"));
    //        store.add(new CountryModel("vg", "Virgin Islands (British)"));
    //        store.add(new CountryModel("vi", "Virgin Islands (USA)"));
    //        store.add(new CountryModel("vn", "Vietnam"));
    //        store.add(new CountryModel("vu", "Vanuatu"));
    //        store.add(new CountryModel("wf", "Wallis and Futuna Islands"));
    //        store.add(new CountryModel("ws", "Western Samoa"));
    //        store.add(new CountryModel("ye", "Yemen"));
    //        store.add(new CountryModel("yt", "Mayotte"));
    //        store.add(new CountryModel("yu", "Yugoslavia"));
    //        store.add(new CountryModel("za", "South Africa"));
    //        store.add(new CountryModel("zm", "Zambia"));
    //        store.add(new CountryModel("zw", "Zimbabwe"));
	    
		return store;
	}
	
	/**
	 * Adds 
	 * @param d
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getDateGmt(Date d) {
		return new Date((d.getTime() / 1000 + 60 * d.getTimezoneOffset()) * 1000);
	}
	
	/**
	 * Returns date in string
	 * @param date : date to be formatted
	 * @param showDay : if day of week is shown
	 * @param showInUTC : if date is converted to UTC (if date is fetched from database)
	 */
	public static String getDateString(Date date, boolean showDay, boolean showInUTC) {
		return getDateString(date, showDay, showInUTC, false);
	}

	/**
	 * Returns date in string
	 * @param date : date to be formatted
	 * @param showDay : if day of week is shown
	 * @param showInUTC : if date is converted to UTC (if date is fetched from database)
	 * @param shortFormat : shows short date format (dd.mm)
	 */
	public static String getDateString(Date date, boolean showDay, boolean showInUTC, boolean shortFormat) {
		
		try {
			TimeZone tz = TimeZone.createTimeZone(0);
			Date today = new Date();
			String strToday = Fmt.format(today);
			String strCompared = (showInUTC)? Fmt.format(date, tz) : Fmt.format(date);
			
			//today
			if(strToday.equals(strCompared)) {
				return AppController.Lang.Today();
			}
			else {
				DateTimeFormat fmt = (shortFormat)? FmtShort : Fmt;
				String str = (showInUTC)? fmt.format(date, tz) : fmt.format(date);
				if(showDay) {
					str += " " + AppController.LangConstants.WeekDays()[ getDayInWeek(date, showInUTC) ];
				}
				
				return str;
			}
			
		} catch (Exception e) {
		}
		return "-";
	}

	/**
	 * Returns date+time in string
	 * @param date : date to be formatted
	 * @param showDay : if day of week is shown
	 * @param showInUTC : if date is converted to UTC (if date is fetched from database)
	 */
	public static String getDateTimeString(Date date, boolean showDay, boolean showInUTC) {

		String str = "";
		try {
			TimeZone tz = TimeZone.createTimeZone(0);
			Date today = new Date();
			String strToday = Fmt.format(today);
			String strCompared = (showInUTC)? Fmt.format(date, tz) : Fmt.format(date);
			
			//today
			if(strToday.equals(strCompared)) {
				str = AppController.Lang.Today();
				
				//time
				DateTimeFormat FmtTime = DateTimeFormat.getFormat( StringConstants.TIMEFORMATS[AppController.User.getTimeFormat()] );
				if(showInUTC) {
					str += " " + FmtTime.format(date, tz);
        }
				else {
					str += " " + FmtTime.format(date);
        }
			}
			else {
				str = strCompared;
				
				//time
				DateTimeFormat FmtTime = DateTimeFormat.getFormat( StringConstants.TIMEFORMATS[AppController.User.getTimeFormat()] );
				if(showInUTC) {
					str += " " + FmtTime.format(date, tz);
        }
				else {
					str += " " + FmtTime.format(date);
        }
				
				if(showDay) {
					str += " " + AppController.LangConstants.WeekDays()[ getDayInWeek(date, showInUTC) ];
				}
			}

		} catch (Exception e) {
		}
		return str;
	}
	
	/**
	 * Returns day in week
	 * @param date
	 * @param showInUTC : if date is converted to UTC (if date is fetched from database)
	 * @return dayInWeek : 0=monday, 1=tuesday, ...
	 */
	@SuppressWarnings("deprecation")
	public static int getDayInWeek(Date date, boolean showInUTC) {

		Date dMon = new Date();
		dMon.setYear(2010);
		dMon.setMonth(7);
		dMon.setDate(22);

		TimeZone tz = TimeZone.createTimeZone(0);
		String curr = (showInUTC)? DateTimeFormat.getFormat("E").format(date, tz) : DateTimeFormat.getFormat("E").format(date);
		//get monday from given week
		long d2 = dMon.getTime();
		for(int i=0; i < 7; i++) {
			d2 = dMon.getTime() + 1000 * 3600 * 24 * i;
			if(DateTimeFormat.getFormat("E").format(new Date(d2)).equals(curr)) {
				return i;
			}
		}
		return 0;
		
	}
	
	/**
	 * Returns distance string in correct format
	 * @param distance in KILOMETERS
	 * @return
	 */
	public static String getDistanceString(double distance) {
		
		String str = "";
		//us
		if(AppController.User.getMeasurementSystem() == 2) {
			str = NumberFormat.getFormat("0.0").format(distance / 1.609344) + " " + AppController.Lang.Miles();
    }
		//metric
		else {
			str = NumberFormat.getFormat("0.0").format(distance) + " km";
    }
		
		return str;
	}
	
	/**
	 * Returns distance value in km
	 * @param distance in user format
	 * @return
	 */
	public static double getDistanceValue(double distance) {
		double dist = distance;
		//us
		if(AppController.User.getMeasurementSystem() == 2) {
			dist = distance * 1.609344;
    }
		
		return dist;		
	}

	/**
	 * Returns html for drag panel
	 * @param text
	 * @return
	 */
	public static String getDragPanel(String text) {
		
		String html = "<div class='panel-drag'>";
		html += text;
		html += "</div>";
		return html;
	}
	

	/**
	 * Returns duration string based on seconds
	 * @param seconds
	 */
	public static String getDurationString(long seconds) {

		String str = "0 min";
		try {
      //if seconds
      if(seconds < 60) {
        DateTimeFormat fmt = DateTimeFormat.getFormat("s' s'");
        Date d = new Date(seconds * 1000);
        str = fmt.format(d, TimeZone.createTimeZone(0));
      }
			//if minutes
      else if(seconds < 3600) {
				DateTimeFormat fmt = DateTimeFormat.getFormat("m' min'"+((seconds % 60 != 0)? " s' s'" : ""));
				Date d = new Date(seconds * 1000);
				str = fmt.format(d, TimeZone.createTimeZone(0));
			}
			//equal hours
			else if(seconds % 3600 == 0) {
				DateTimeFormat fmt = DateTimeFormat.getFormat("h' h'");
				Date d = new Date(seconds * 1000);
				str = fmt.format(d, TimeZone.createTimeZone(0));
			}
			else {
				DateTimeFormat fmt = DateTimeFormat.getFormat("h' h 'm' min'"+((seconds % 60 != 0)? " s' s'" : ""));
				Date d = new Date(seconds * 1000);
				str = fmt.format(d, TimeZone.createTimeZone(0));
			}
		} catch (Exception e) {
		}
		return str;
	}

	/**
	 * Returns exercise name string: xxxx (equipment)
	 * @param model
	 * @return name string
	 */
	public static String getExerciseName(ExerciseNameModel model) {
		
		String str = model.getName();
		if(model.getTarget() != 0 && model.getTarget() < AppController.LangConstants.Targets().length) {
			str += " (" + AppController.LangConstants.Targets()[model.getTarget()] + ")";
    }
		
		return str;
	}

	/**
	 * Returns modal messagebox with label
	 * @param message : label's value
	 * @param handler : handler to be called when ok is pressed.
	 */
	@SuppressWarnings("rawtypes")
	public static MessageBox getMessageBoxConfirm(String message, final MessageBoxHandler handler) {

		final MessageBox box = new MessageBox();
		box.setButtons(MessageBox.OKCANCEL);
		box.setType(MessageBoxType.CONFIRM);
		box.setModal(true);
		box.setMessage(message);
		box.setTitle(AppController.Lang.Confirm());
    box.addCallback(new Listener<MessageBoxEvent>() {  
      public void handleEvent(MessageBoxEvent be) {
        //if ok buttons is pressed
        Button btnClicked = be.getButtonClicked();
        if(btnClicked != null && Dialog.OK.equals(btnClicked.getItemId())) {
          handler.okPressed("");
        }
      }
    });

    box.addListener(Events.Show, new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
        try {
					new KeyNav(box.getDialog().getButtonBar()) {
						@Override
						public void onEnter(ComponentEvent ce) {
							//if ok button has focus
							Button btnOK = box.getDialog().getButtonById(Dialog.OK);
							if(btnOK.getData("focus") != null) {
								if((Boolean)btnOK.getData("focus")) {
									handler.okPressed("");
	              }
							}
									
						}
						@Override
						public void onLeft(ComponentEvent ce) {
				        	
							//focus ok button
							Button btnOK = box.getDialog().getButtonById(Dialog.OK);
							if(btnOK != null) {
								btnOK.focus();
								btnOK.setData("focus", true);
							}
						}
						@Override
						public void onRight(ComponentEvent ce) {
				        	
							//focus cancel button
							Button btnCancel = box.getDialog().getButtonById(Dialog.CANCEL);
							if(btnCancel != null) {
								btnCancel.focus();
							}
							//focus ok button
							Button btnOK = box.getDialog().getButtonById(Dialog.OK);
							if(btnOK != null) {
								btnOK.setData("focus", false);
							}
						}
					};
				} catch (Exception e) {
				}
			}
    });
        
		return box;
	}
	/**
	 * Returns modal messagebox with textfield
	 * @param value : textfield's default value
	 * @param handler : handler to be called when ok is pressed. Handler is called with textfield's new value
	 */
	@SuppressWarnings("rawtypes")
	public static MessageBox getMessageBoxPrompt(final String value, final MessageBoxHandler handler) {

		final MessageBox box = new MessageBox();
		box.setButtons(MessageBox.OKCANCEL);
		box.setType(MessageBoxType.PROMPT);
		box.setModal(true);
    box.addCallback(new Listener<MessageBoxEvent>() {  
      public void handleEvent(MessageBoxEvent be) { 
        //if ok buttons is pressed
        if(be.getButtonClicked() != null && handler != null && box.getTextBox().isValid()) {
          if(be.getButtonClicked().getItemId().equals(MessageBox.OK)) {
            handler.okPressed(box.getTextBox().getValue());
          }
        }
      }
    });
    box.addListener(Events.Show, new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				//listeners for textfield
				((Button)box.getDialog().getButtonBar().getItem(0)).setEnabled(false);
				//enable / disable button
				box.getTextBox().addListener(Events.Valid, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						((Button)box.getDialog().getButtonBar().getItem(0)).setEnabled(true);
					}					
				});
				box.getTextBox().addListener(Events.Invalid, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						((Button)box.getDialog().getButtonBar().getItem(0)).setEnabled(false);
					}					
				});
				new KeyNav(box.getTextBox()) {
					@Override
					public void onEnter(ComponentEvent ce) {
						if(box.getTextBox().isValid()) {
							box.close();
							handler.okPressed(box.getTextBox().getValue());
						}
					}
				};

				box.getTextBox().setMinLength(3);
				box.getTextBox().setAllowBlank(false);
	      setWarningMessages(box.getTextBox());
				box.getTextBox().setAutoValidate(true);
				box.getTextBox().setFireChangeEventOnSetValue(true);
				box.getTextBox().setValue(value);
				
			}
    });

    return box;
	}

	/**
	 * Returns correct value for text field in "titlebar" based on given value
	 * @param value
	 * @return width
	 */
	public static int getTextFieldWidth(String value) {

		if(value == null) {
			return 50;
    }
		
		int w = (value.length() * 8);
		if(w < 50) {
			w = 50;
    }		
		return w;
	}
	
	/**
	 * Returns given time string in seconds based on user preferences
	 * @param time string
	 * @return seconds
	 */
	@SuppressWarnings("deprecation")
	public static int getTimeToSeconds(String time) {
		
		try {
			final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.TIMEFORMATS[AppController.User.getTimeFormat()]);
			Date d = fmt.parse(time);
			d.setDate(1);
			d.setMonth(0);
			d.setYear(70);
			return d.getHours() * 3600 + d.getMinutes() * 60;
			
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Returns given seconds to time string
	 * @param time : seconds
	 * @return
	 */
	public static String getTimeToString(long time) {
		try {

			final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.TIMEFORMATS[AppController.User.getTimeFormat()]);
			Date d = new Date(time * 1000);
			String str = fmt.format(d, TimeZone.createTimeZone(0));
			return str;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Returns total info panel used in time header and meal header
	 * @param big : if big values (used in nutrition day view)
	 * @param energy
	 * @param protein
	 * @param carb
	 * @param fet
	 * @param guide
	 * @return
	 */
	public static LayoutContainer getTotalPanel(boolean percents, double energy, double protein, double carb, double fet, GuideValueModel guide) {

		final NumberFormat fmt = NumberFormat.getFormat("0.0");
		
		final LayoutContainer lc = new LayoutContainer();
		lc.setAutoWidth(true);
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    lc.setLayout(layout);	
		
    //calculate percents
    int percentP = (int) (((protein * 4) / energy) * 100);
    int percentC = (int) (((carb * 4) / energy) * 100);
    int percentF = 100 - percentC - percentP;
        
		try {
			//energy
			lc.add(new Text(AppController.Lang.Energy() + ": "), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			Text tEVal = new Text(fmt.format(energy) + " kcal");
			tEVal.setStyleName("label-value");
			lc.add(tEVal, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			if(guide != null) {
				double diff = energy - guide.getEnergy(guide.hasTraining());
				Text textEGuide = new Text();
				textEGuide.setStyleName("label-guide-" + ((diff > 0)? "pos" : "neg"));
				textEGuide.setText(((diff <= 0)? "" : "+") + fmt.format(diff));
				lc.add(textEGuide);
			}
			
			//spacer
      HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
      flex.setFlex(1);
      if(percents) {
        lc.add(new Text(), flex);
      }
			
			//protein
			lc.add(new Text(AppController.Lang.Protein() + ": "), new HBoxLayoutData(new Margins(0, 5, 0, 5)));
			Text tPVal = new Text(Functions.convertNutritionValueFromDB(protein) + ((energy > 0 && percents)? " (" + percentP + "%)" : ""));
			tPVal.setStyleName("label-value");
			lc.add(tPVal, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			if(guide != null) {
				double diff = 0;
				//if percent
				if(guide.isPercent()) {
					diff = protein - (((guide.getProtein(guide.hasTraining()) * guide.getEnergy(guide.hasTraining())) / 100) / 4);
        }
				else {
					diff = protein - guide.getProtein(guide.hasTraining());
        }
				Text textPGuide = new Text();
				textPGuide.setStyleName("label-guide-" + ((diff > 0)? "pos" : "neg"));
				textPGuide.setText(((diff <= 0)? "" : "+") + fmt.format(diff) + "g");
				lc.add(textPGuide);
			}
			
			//spacer
			if(percents) {
        lc.add(new Text(), flex);
      }

			//carbs
			lc.add(new Text(AppController.Lang.Carbohydrates() + ": "), new HBoxLayoutData(new Margins(0, 5, 0, 5)));
			Text tCVal = new Text(Functions.convertNutritionValueFromDB(carb) + ((energy > 0 && percents)? " (" + percentC + "%)" : ""));
			tCVal.setStyleName("label-value");
			lc.add(tCVal, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			if(guide != null) {
				double diff = 0;
				//if percent
				if(guide.isPercent()) {
					diff = carb - (((guide.getCarbs(guide.hasTraining()) * guide.getEnergy(guide.hasTraining())) / 100) / 4);
        }
				else {
					diff = carb - guide.getCarbs(guide.hasTraining());
        }
				Text textCGuide = new Text();
				textCGuide.setStyleName("label-guide-" + ((diff > 0)? "pos" : "neg"));
				textCGuide.setText(((diff <= 0)? "" : "+") + fmt.format(diff) + "g");
				lc.add(textCGuide);
			}
			
			//spacer
			if(percents) {
        lc.add(new Text(), flex);
      }

			//fet
			lc.add(new Text(AppController.Lang.Fet() + ": "), new HBoxLayoutData(new Margins(0, 5, 0, 5)));
			Text tFVal = new Text(Functions.convertNutritionValueFromDB(fet) + ((energy > 0 && percents)? " (" + percentF + "%)" : ""));
			tFVal.setStyleName("label-value");
			lc.add(tFVal, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			if(guide != null) {
				double diff = 0;
				//if percent
				if(guide.isPercent()) {
					diff = fet - (((guide.getFet(guide.hasTraining()) * guide.getEnergy(guide.hasTraining())) / 100) / 9);
        }
				else {
					diff = fet - guide.getFet(guide.hasTraining());
        }
				Text textFGuide = new Text();
				textFGuide.setStyleName("label-guide-" + ((diff > 0)? "pos" : "neg"));
				textFGuide.setText(((diff <= 0)? "" : "+") + fmt.format(diff) + "g");
				lc.add(textFGuide);
			}
			
		} catch (Exception e) {
		}
		
		return lc;
	}
	
	/**
	 * Returns total info panel used in time header and meal header
	 * @param energy
	 * @param protein
	 * @param carb
	 * @param fet
	 * @return
	 */
	public static LayoutContainer getTotalPanel(double energy, double protein, double carb, double fet) {

		final NumberFormat fmt = NumberFormat.getFormat("0.0");
		
		final LayoutContainer lc = new LayoutContainer();
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    lc.setLayout(layout);
    lc.setHeight(25);
		
		//energy
		lc.add(new Text("<b>" + AppController.Lang.Energy() + ": </b>" + fmt.format(energy) + " kcal"), new HBoxLayoutData(new Margins(0, 0, 0, 0)));
		
		if(protein > 0 || carb > 0 || fet > 0) {
			//protein
			lc.add(new Text("<b>" + AppController.Lang.Protein() + ": </b>" + Functions.convertNutritionValueFromDB(protein)), new HBoxLayoutData(new Margins(0, 0, 0, 10)));
			//carb
			lc.add(new Text("<b>" + AppController.Lang.Carbohydrates() + ": </b>" + Functions.convertNutritionValueFromDB(carb)), new HBoxLayoutData(new Margins(0, 0, 0, 10)));
			//fet
			lc.add(new Text("<b>" + AppController.Lang.Fet() + ": </b>" + Functions.convertNutritionValueFromDB(fet)), new HBoxLayoutData(new Margins(0, 0, 0, 10)));
			
		}
		return lc;
	}
  
  /**
   * Returns total info panel used in time header and meal header
   * @param energy
   * @param protein
   * @param carb
   * @param fet
   * @return
   */
  public static LayoutContainer getTotalPanelFlow(double energy, double protein, double carb, double fet) {

    final NumberFormat fmt = NumberFormat.getFormat("0.0");
    
    final LayoutContainer lc = new LayoutContainer();
    FlowLayout layout = new FlowLayout();
    lc.setLayout(layout);
    lc.setHeight(25);
    
    //energy
    lc.add(new Text("<b>" + AppController.Lang.Energy() + ": </b>" + fmt.format(energy) + " kcal"), new FlowData(new Margins(0, 10, 0, 0)));
    
    if(protein > 0 || carb > 0 || fet > 0) {
      //protein
      lc.add(new Text("<b>" + AppController.Lang.Protein() + ": </b>" + Functions.convertNutritionValueFromDB(protein)), new FlowData(new Margins(0, 10, 0, 0)));
      //carb
      lc.add(new Text("<b>" + AppController.Lang.Carbohydrates() + ": </b>" + Functions.convertNutritionValueFromDB(carb)), new FlowData(new Margins(0, 10, 0, 0)));
      //fet
      lc.add(new Text("<b>" + AppController.Lang.Fet() + ": </b>" + Functions.convertNutritionValueFromDB(fet)), new FlowData(new Margins(0, 0, 0, 0)));
      
    }
    return lc;
  }

	/**
	 * Checks if valid key combo is valid. Not typed in textfield, etc..
	 * @return true if valid
	 */
	public static boolean isValidKeyCombo(ComponentEvent ce) {
		
		try {
			return !ce.isAltKey() 
      && !ce.isControlKey() 
      && ce.isShiftKey() 
      && ce.getTarget() != null 
      && ce.getTarget().getNodeName() != null 
      && !ce.getTarget().getNodeName().contains("INPUT")
      && !ce.getTarget().getNodeName().contains("TEXTAREA")
      ;
		} catch (Exception e) {
		}
		
		return false;
	}

	//md5
	public static native String md5(String string) /*-{ 
  function RotateLeft(lValue, iShiftBits) { 
  return (lValue<<iShiftBits) | (lValue>>>(32-iShiftBits)); 
  } 

  function AddUnsigned(lX,lY) { 
  var lX4,lY4,lX8,lY8,lResult; 
  lX8 = (lX & 0x80000000); 
  lY8 = (lY & 0x80000000); 
  lX4 = (lX & 0x40000000); 
  lY4 = (lY & 0x40000000); 
  lResult = (lX & 0x3FFFFFFF)+(lY & 0x3FFFFFFF); 
  if (lX4 & lY4) { 
  return (lResult ^ 0x80000000 ^ lX8 ^ lY8); 
  } 
  if (lX4 | lY4) { 
  if (lResult & 0x40000000) { 
  return (lResult ^ 0xC0000000 ^ lX8 ^ lY8); 
  } else { 
  return (lResult ^ 0x40000000 ^ lX8 ^ lY8); 
  } 
  } else { 
  return (lResult ^ lX8 ^ lY8); 
  } 
  } 

  function F(x,y,z) { return (x & y) | ((~x) & z); } 
  function G(x,y,z) { return (x & z) | (y & (~z)); } 
  function H(x,y,z) { return (x ^ y ^ z); } 
  function I(x,y,z) { return (y ^ (x | (~z))); } 

  function FF(a,b,c,d,x,s,ac) { 
  a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac)); 
  return AddUnsigned(RotateLeft(a, s), b); 
  }; 

  function GG(a,b,c,d,x,s,ac) { 
  a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac)); 
  return AddUnsigned(RotateLeft(a, s), b); 
  }; 

  function HH(a,b,c,d,x,s,ac) { 
  a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac)); 
  return AddUnsigned(RotateLeft(a, s), b); 
  }; 

  function II(a,b,c,d,x,s,ac) { 
  a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac)); 
  return AddUnsigned(RotateLeft(a, s), b); 
  }; 

  function ConvertToWordArray(string) { 
  var lWordCount; 
  var lMessageLength = string.length; 
  var lNumberOfWords_temp1=lMessageLength + 8; 
  var lNumberOfWords_temp2=(lNumberOfWords_temp1-(lNumberOfWords_temp1 
  % 64))/64; 
  var lNumberOfWords = (lNumberOfWords_temp2+1)*16; 
  var lWordArray=Array(lNumberOfWords-1); 
  var lBytePosition = 0; 
  var lByteCount = 0; 
  while ( lByteCount < lMessageLength ) { 
  lWordCount = (lByteCount-(lByteCount % 4))/4; 
  lBytePosition = (lByteCount % 4)*8; 
  lWordArray[lWordCount] = (lWordArray[lWordCount] | 
  (string.charCodeAt(lByteCount)<<lBytePosition)); 
  lByteCount++; 
  } 
  lWordCount = (lByteCount-(lByteCount % 4))/4; 
  lBytePosition = (lByteCount % 4)*8; 
  lWordArray[lWordCount] = lWordArray[lWordCount] | 
  (0x80<<lBytePosition); 
  lWordArray[lNumberOfWords-2] = lMessageLength<<3; 
  lWordArray[lNumberOfWords-1] = lMessageLength>>>29; 
  return lWordArray; 
  }; 

  function WordToHex(lValue) { 
  var WordToHexValue="",WordToHexValue_temp="",lByte,lCount; 
  for (lCount = 0;lCount<=3;lCount++) { 
  lByte = (lValue>>>(lCount*8)) & 255; 
  WordToHexValue_temp = "0" + lByte.toString(16); 
  WordToHexValue = WordToHexValue + 
  WordToHexValue_temp.substr(WordToHexValue_temp.length-2,2); 
  } 
  return WordToHexValue; 
  }; 

  function Utf8Encode(string) { 
  string = string.replace(/\r\n/g,"\n"); 
  var utftext = ""; 

  for (var n = 0; n < string.length; n++) { 

  var c = string.charCodeAt(n); 

  if (c < 128) { 
  utftext += String.fromCharCode(c); 
  } 
  else if((c > 127) && (c < 2048)) { 
  utftext += String.fromCharCode((c >> 6) | 192); 
  utftext += String.fromCharCode((c & 63) | 128); 
  } 
  else { 
  utftext += String.fromCharCode((c >> 12) | 224); 
  utftext += String.fromCharCode(((c >> 6) & 63) | 128); 
  utftext += String.fromCharCode((c & 63) | 128); 
  } 

  } 

  return utftext; 
  }; 

  var x=Array(); 
  var k,AA,BB,CC,DD,a,b,c,d; 
  var S11=7, S12=12, S13=17, S14=22; 
  var S21=5, S22=9 , S23=14, S24=20; 
  var S31=4, S32=11, S33=16, S34=23; 
  var S41=6, S42=10, S43=15, S44=21; 

  string = Utf8Encode(string); 

  x = ConvertToWordArray(string); 

  a = 0x67452301; b = 0xEFCDAB89; c = 0x98BADCFE; d = 0x10325476; 

  for (k=0;k<x.length;k+=16) { 
  AA=a; BB=b; CC=c; DD=d; 
  a=FF(a,b,c,d,x[k+0], S11,0xD76AA478); 
  d=FF(d,a,b,c,x[k+1], S12,0xE8C7B756); 
  c=FF(c,d,a,b,x[k+2], S13,0x242070DB); 
  b=FF(b,c,d,a,x[k+3], S14,0xC1BDCEEE); 
  a=FF(a,b,c,d,x[k+4], S11,0xF57C0FAF); 
  d=FF(d,a,b,c,x[k+5], S12,0x4787C62A); 
  c=FF(c,d,a,b,x[k+6], S13,0xA8304613); 
  b=FF(b,c,d,a,x[k+7], S14,0xFD469501); 
  a=FF(a,b,c,d,x[k+8], S11,0x698098D8); 
  d=FF(d,a,b,c,x[k+9], S12,0x8B44F7AF); 
  c=FF(c,d,a,b,x[k+10],S13,0xFFFF5BB1); 
  b=FF(b,c,d,a,x[k+11],S14,0x895CD7BE); 
  a=FF(a,b,c,d,x[k+12],S11,0x6B901122); 
  d=FF(d,a,b,c,x[k+13],S12,0xFD987193); 
  c=FF(c,d,a,b,x[k+14],S13,0xA679438E); 
  b=FF(b,c,d,a,x[k+15],S14,0x49B40821); 
  a=GG(a,b,c,d,x[k+1], S21,0xF61E2562); 
  d=GG(d,a,b,c,x[k+6], S22,0xC040B340); 
  c=GG(c,d,a,b,x[k+11],S23,0x265E5A51); 
  b=GG(b,c,d,a,x[k+0], S24,0xE9B6C7AA); 
  a=GG(a,b,c,d,x[k+5], S21,0xD62F105D); 
  d=GG(d,a,b,c,x[k+10],S22,0x2441453); 
  c=GG(c,d,a,b,x[k+15],S23,0xD8A1E681); 
  b=GG(b,c,d,a,x[k+4], S24,0xE7D3FBC8); 
  a=GG(a,b,c,d,x[k+9], S21,0x21E1CDE6); 
  d=GG(d,a,b,c,x[k+14],S22,0xC33707D6); 
  c=GG(c,d,a,b,x[k+3], S23,0xF4D50D87); 
  b=GG(b,c,d,a,x[k+8], S24,0x455A14ED); 
  a=GG(a,b,c,d,x[k+13],S21,0xA9E3E905); 
  d=GG(d,a,b,c,x[k+2], S22,0xFCEFA3F8); 
  c=GG(c,d,a,b,x[k+7], S23,0x676F02D9); 
  b=GG(b,c,d,a,x[k+12],S24,0x8D2A4C8A); 
  a=HH(a,b,c,d,x[k+5], S31,0xFFFA3942); 
  d=HH(d,a,b,c,x[k+8], S32,0x8771F681); 
  c=HH(c,d,a,b,x[k+11],S33,0x6D9D6122); 
  b=HH(b,c,d,a,x[k+14],S34,0xFDE5380C); 
  a=HH(a,b,c,d,x[k+1], S31,0xA4BEEA44); 
  d=HH(d,a,b,c,x[k+4], S32,0x4BDECFA9); 
  c=HH(c,d,a,b,x[k+7], S33,0xF6BB4B60); 
  b=HH(b,c,d,a,x[k+10],S34,0xBEBFBC70); 
  a=HH(a,b,c,d,x[k+13],S31,0x289B7EC6); 
  d=HH(d,a,b,c,x[k+0], S32,0xEAA127FA); 
  c=HH(c,d,a,b,x[k+3], S33,0xD4EF3085); 
  b=HH(b,c,d,a,x[k+6], S34,0x4881D05); 
  a=HH(a,b,c,d,x[k+9], S31,0xD9D4D039); 
  d=HH(d,a,b,c,x[k+12],S32,0xE6DB99E5); 
  c=HH(c,d,a,b,x[k+15],S33,0x1FA27CF8); 
  b=HH(b,c,d,a,x[k+2], S34,0xC4AC5665); 
  a=II(a,b,c,d,x[k+0], S41,0xF4292244); 
  d=II(d,a,b,c,x[k+7], S42,0x432AFF97); 
  c=II(c,d,a,b,x[k+14],S43,0xAB9423A7); 
  b=II(b,c,d,a,x[k+5], S44,0xFC93A039); 
  a=II(a,b,c,d,x[k+12],S41,0x655B59C3); 
  d=II(d,a,b,c,x[k+3], S42,0x8F0CCC92); 
  c=II(c,d,a,b,x[k+10],S43,0xFFEFF47D); 
  b=II(b,c,d,a,x[k+1], S44,0x85845DD1); 
  a=II(a,b,c,d,x[k+8], S41,0x6FA87E4F); 
  d=II(d,a,b,c,x[k+15],S42,0xFE2CE6E0); 
  c=II(c,d,a,b,x[k+6], S43,0xA3014314); 
  b=II(b,c,d,a,x[k+13],S44,0x4E0811A1); 
  a=II(a,b,c,d,x[k+4], S41,0xF7537E82); 
  d=II(d,a,b,c,x[k+11],S42,0xBD3AF235); 
  c=II(c,d,a,b,x[k+2], S43,0x2AD7D2BB); 
  b=II(b,c,d,a,x[k+9], S44,0xEB86D391); 
  a=AddUnsigned(a,AA); 
  b=AddUnsigned(b,BB); 
  c=AddUnsigned(c,CC); 
  d=AddUnsigned(d,DD); 
  } 

  var temp = WordToHex(a)+WordToHex(b)+WordToHex(c)+WordToHex(d); 

  return temp.toLowerCase(); 
  }-*/;

	/**
	 * Strip time from date
	 * @param date
	 * @param start or end date (time: 00:00:01 or 23:59:59)
	 * @return date
	 */
	@SuppressWarnings("deprecation")
	public static Date stripTime(Date date, boolean isStart) {
    int h = 0;
    int m = 0;
    int s = 0;
    if(!isStart) {
      h = 23;
      m = 59;
      s = 59;
    } 
    date.setHours(h);
    date.setMinutes(m);
    date.setSeconds(s);
    return date;
	}

	/**
	 * Trims timezone "off" from date. When saving to database
	 * @param date : with user timezone
	 * @param resetTime : true to reset time
	 * @return date : with UTC timezone
	 */
	@SuppressWarnings("deprecation")
	public static Date trimDateToDatabase(Date date, boolean resetTime) {
		try {
			if(resetTime) {
				date.setHours(0);
				date.setMinutes(0);
				date.setSeconds(0);
			}
			
			return new Date(date.getTime() - 1000 * 60 * date.getTimezoneOffset());
		} catch (Exception e) {
		}	
		return date;
	}
	
	static DateTimeFormat[] fmts = new DateTimeFormat[] {
	  DateTimeFormat.getFormat("h'h'm'min's's'"),
	  DateTimeFormat.getFormat("m'min's's'"), 
    DateTimeFormat.getFormat("h'h'm'min'"),
    DateTimeFormat.getFormat("m'min'"),
    DateTimeFormat.getFormat("h'h'"),
    DateTimeFormat.getFormat("s's'"),
	};
	
	public static MySpinnerField getDurationSpinner() {
	  
	  MySpinnerField tfDuration = new MySpinnerField() {
      @Override
      protected boolean validateValue(String value) {

        value = value.replaceAll(" ", "");
        //single digit
        try {
          Integer.parseInt(value);
          return true;
        } catch (IllegalArgumentException e) {
        }
        
        for(DateTimeFormat fmt : fmts) {
          try {
            fmt.parse(value);
            return true;
          } catch (IllegalArgumentException e) {
          }
        }
        
        return false;
      }
    };
    tfDuration.setBaseChars("0123456789hmins ");
    tfDuration.setFieldLabel(AppController.Lang.Duration());   
    tfDuration.setAllowBlank(false);   
    tfDuration.setEditable(true);
    tfDuration.setMinValue(0);
    tfDuration.setMaxValue(86400);
    tfDuration.setValue(3000);
    tfDuration.setAutoValidate(false);
    tfDuration.setIncrement(300);
    tfDuration.setPropertyEditorType(Integer.class);
    tfDuration.setPropertyEditor(new PropertyEditor<Number>() {
      @SuppressWarnings("deprecation")
      @Override
      public Number convertStringValue(String value) {
        value = value.replaceAll(" ", "");
        Date d = null;
        //single digit
        try {
          int l = Integer.parseInt(value);
          d = new Date();
          d.setDate(1);
          d.setMonth(0);
          d.setYear(70);
          d.setHours(0);
          d.setMinutes(l);
          d.setSeconds(0);
          
          return d.getTime() / 1000 - d.getTimezoneOffset() * 60;
        } catch (IllegalArgumentException e) {
        }

        for(DateTimeFormat fmt : fmts) {
          try {
            d = fmt.parse(value);
            d.setDate(1);
            d.setMonth(0);
            d.setYear(70);

            return d.getTime() / 1000 - d.getTimezoneOffset() * 60;
          } catch (IllegalArgumentException e) {
          }
        }
        
        return 0;
      }
      @Override
      public String getStringValue(Number value) {
        String str = "0min";
        try {
          //if minutes
          if(value.intValue() < 3600) {
            DateTimeFormat fmt = DateTimeFormat.getFormat("m'min' s's'");
            Date d = new Date(value.intValue() * 1000);
            str = fmt.format(d, TimeZone.createTimeZone(0));
          }
          else {
            DateTimeFormat fmt = DateTimeFormat.getFormat("h'h 'm'min' s's'");
            Date d = new Date(value.intValue() * 1000);
            str = fmt.format(d, TimeZone.createTimeZone(0));
          }
        } catch (Exception e) {
          Motiver.showException(e);
        }
        return str;
      }
      
    });
    
    return tfDuration;
	}
	
  /** Returns date field
   * @param date
   * @return
   */
  public static DateField getDateField(Date date) {
    
    Date date1 = (date != null)? date : new Date();
    final DateField tfDate = new DateField();
    final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()]);
    DateTimePropertyEditor pr = new DateTimePropertyEditor(fmt);
    tfDate.setPropertyEditor(pr);
    tfDate.setValue(date1);
    tfDate.setFieldLabel(AppController.Lang.Date());
    
    return tfDate;
  }
  
  /** Returns pulse spinner
   * @return
   */
  public static SpinnerField getPulseSpinner() {
    SpinnerField tfPulse = new SpinnerField();
    tfPulse.setFieldLabel(AppController.Lang.Pulse());
    tfPulse.setAllowBlank(false);
    tfPulse.setEditable(true);
    tfPulse.setMinValue(0);
    tfPulse.setMaxValue(10000);
    tfPulse.setValue(0D);
    tfPulse.setIncrement(10);
    tfPulse.setPropertyEditorType(Double.class);
    tfPulse.setFormat(NumberFormat.getFormat("0"));
    setWarningMessages(tfPulse);
    
    return tfPulse;
  }
  
  /** Returns calories spinner
   * @return
   */
  public static SpinnerField getCaloriesSpinner() {

    SpinnerField tfCalories = new SpinnerField();   
    tfCalories.setFieldLabel(AppController.Lang.Calories());   
    tfCalories.setAllowBlank(false);   
    tfCalories.setEditable(true);
    tfCalories.setMinValue(0);  
    tfCalories.setMaxValue(10000);
    tfCalories.setValue(0D);
    tfCalories.setIncrement(10);
    tfCalories.setPropertyEditorType(Double.class);
    tfCalories.setFormat(NumberFormat.getFormat("0 kcal"));
    setWarningMessages(tfCalories);
    
    return tfCalories;
  }
  
  /**
   * Sets warning messages to given textfield
   * @param field
   */
  public static void setWarningMessages(TextField<?> field) {
    field.setMessageTarget("side");
    field.getMessages().setBlankText(AppController.Lang.FieldBlankText());
    field.getMessages().setInvalidText(AppController.Lang.FieldInvalidText());
    field.getMessages().setMaxLengthText(AppController.Lang.FieldMaxLengthText(field.getMaxLength()));
    field.getMessages().setMinLengthText(AppController.Lang.FieldMinLengthText(field.getMinLength()));
  }
  
  /**
   * Sets warning messages to given spinnerfield
   * @param field
   */
  public static void setWarningMessages(SpinnerField field) {
    field.setMessageTarget("side");
    field.getMessages().setBlankText(AppController.Lang.FieldBlankText());
    field.getMessages().setInvalidText(AppController.Lang.FieldInvalidText());
    field.getMessages().setMaxText(AppController.Lang.FieldMaxText(field.getMaxValue().intValue()));
    field.getMessages().setMinText(AppController.Lang.FieldMinText(field.getMinValue().intValue()));
    field.getMessages().setNanText(AppController.Lang.FieldNanText());
    field.getMessages().setNegativeText(AppController.Lang.FieldNegativeText());
  }
	
}
