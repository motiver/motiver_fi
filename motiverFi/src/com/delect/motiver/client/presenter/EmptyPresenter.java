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
package com.delect.motiver.client.presenter;

import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;

/** Empty presenter which shows loading animation or single text.
 */
public class EmptyPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class EmptyDisplay extends Display {
		/**
		 * Sets text to be left aligned.
		 */
		public abstract void setLeftAlign();
		/**
		 * Sets if loading animation is shown.
		 * @param isLoading boolean
		 */
		public abstract void setLoading(boolean isLoading);
		/**
		 * Sets text to be shown.
		 * @param text String
		 */
		public abstract void setText(String text);
	}
	
	//settings
	public static final boolean EMPTY_LOADING = false;
	public static final boolean EMPTY_LOADING_SMALL = true;
	public static final int OPTION_SMALLER = 0;
	public static final int OPTION_SMALLER_LEFT_ALIGN = 1;
	
	private EmptyDisplay display;

	private boolean isLoading = false;
	private String text = "";

	/**
	 * Constructor for EmptyPresenter.
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 * @param display EmptyDisplay
	 * @param smallLoading boolean
	 */
	public EmptyPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyDisplay display, boolean smallLoading) { 
		super(rpcService, eventBus);
		this.display = display;
	    
		isLoading  = true;
		if(smallLoading) {
      display.setHeight(50);
    }
		text = AppController.Lang.Loading();
	}
	/**
	 * Shows empty presenter
	 * @param display
	 * @param text :  
	 * <ul>
	 * <li>-1 = loading (small)</li>
	 * <li>0 = loading</li>
	 * <li>1 = workouts</li>
	 * <li>2 = workouts in cal</li>
	 * <li>3 = workouts in routine</li>
	 * <li>4 = routines</li>
	 * <li>5 = meals</li>
	 * <li>6 = foods in meal</li>
	 * <li>7 = foods in time</li>
	 * <li>8 = measurements</li>
	 * <li>9 = measurements' values</li>
	 * <li>10 = foods in cal</li>
	 * <li>11 = workouts search</li>
	 * <li>12 = routines search</li>
	 * <li>13 = meals search</li>
	 * <li>14 = no data</li>
	 * <li>15 = cardio</li>
	 * <li>16 = cardio's values</li>
	 * <li>17 = runs</li>
	 * <li>18 = runs' values</li>
	 * <li>19 = friends</li>
	 * <li>20 = exercises</li>
	 * <li>21 = blog day</li>
	 * <li>22 = last weights</li>
	 * <li>23 = no permission for blog</li>
	 * <li>24 = friends</li>
	 * <li>25 = guide values</li>
	 * <li>26 = recent comments</li>
	 * <li>27 = stats</li>
	 * <li>28 = empty blog</li>
	 * <li>29 = recent activities</li>
	 * @param rpcService MyServiceAsync
	 * @param eventBus SimpleEventBus
	 */
	public EmptyPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyDisplay display, String text) { 
		super(rpcService, eventBus);
		this.display = display;
	    
    this.text  = text;
	}
	
	/**
	 * Different panel
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param text
	 * @param option (0=smaller, 1=left aligned smaller)
	 */
	public EmptyPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyDisplay display, String text, int option) { 
		super(rpcService, eventBus);
		this.display = display;
	    
		//smaller
		if(option == OPTION_SMALLER || option == OPTION_SMALLER_LEFT_ALIGN) {
      display.setHeight(50);
    }
		//left aligned
		if(option == OPTION_SMALLER_LEFT_ALIGN) {
      display.setLeftAlign();
    }
		
		this.text = text;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setLoading(isLoading);
		display.setText(text);
	}

}
