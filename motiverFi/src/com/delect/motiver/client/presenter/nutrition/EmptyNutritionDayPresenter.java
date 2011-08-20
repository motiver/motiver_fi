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
package com.delect.motiver.client.presenter.nutrition;

import java.util.Date;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Cookies;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.TimeCreatedEvent;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.TimeModel;

public class EmptyNutritionDayPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class EmptyNutritionDayDisplay extends Display {
		public abstract void setHandler(EmptyNutritionDayHandler emptyNutritionDayHandler);
		public abstract void setDefaultTimes(int[] timesTraining, int[] timesRest);
	}

	public interface EmptyNutritionDayHandler {
		void addTimeTemplate(int[] times);
		void timesChanged(int[] timesTraining, int[] timesRest);
	}
	private Date date;

	private EmptyNutritionDayDisplay display;

	/**
	 * Shows empty presenter for today nutrition day view
	 */
	public EmptyNutritionDayPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, EmptyNutritionDayDisplay display, Date date) { 
		super(rpcService, eventBus);
		this.display = display;
	    
    this.date = date;
	}


	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
		display.setHandler(new EmptyNutritionDayHandler() {
			@Override
			public void addTimeTemplate(int[] times) {
				display.setContentEnabled(false);
				
				//add times
				TimeModel[] timeModels = new TimeModel[times.length];
				for(int i=0; i < times.length; i++) {
          timeModels[i] = new TimeModel(Functions.trimDateToDatabase(date, true), times[i]);
		    }

				rpcService.addTimes(timeModels, new MyAsyncCallback<TimeModel[]>() {
					@Override
					public void onSuccess(TimeModel[] result) {
						display.setContentEnabled(true);
						
						//fire events
						for(int i=0; i < (result).length; i++) {
              fireEvent(new TimeCreatedEvent(result[i]));
				    }
					}
				});
			}

      @Override
      public void timesChanged(int[] timesTraining, int[] timesRest) {
        
        //date
        final Date date = new Date();
        long dateLong = date.getTime();
        dateLong += (1000 * 60 * 60 * 24 * 365);//year
        date.setTime(dateLong);
        
        //training day
        String str = "";
        for(long t : timesTraining)
          str += t + "//";
        Cookies.setCookie(Constants.COOKIE_DEFAULT_TIMES_TRAINING, str, date, "", "/", false);
        
        //rest day
        str = "";
        for(long t : timesRest)
          str += t + "//";
        Cookies.setCookie(Constants.COOKIE_DEFAULT_TIMES_REST, str, date, "", "/", false);
        
      }
		});
		
		//load default times from cookie
		
		//training day
		int[] timesTraining = new int[6];
		try {
		  String cookie = Cookies.getCookie(Constants.COOKIE_DEFAULT_TIMES_TRAINING);
		  if(cookie != null) {
	      final String[] str = cookie.split("//");
	      for(int i = 0; i < 6; i++) {
	        timesTraining[i] = Integer.parseInt(str[i]);
	      }
		  }
		  else {
	      timesTraining = Constants.VALUE_DEFAULT_TIMES_TRAINING;   //load default
		  }
    } catch (Exception e) {
      timesTraining = Constants.VALUE_DEFAULT_TIMES_TRAINING;   //load default
    }

    //rest day
    int[] timesRest = new int[6];
    try {
      String cookie = Cookies.getCookie(Constants.COOKIE_DEFAULT_TIMES_REST);
      if(cookie != null) {
        final String[] str = cookie.split("//");
        for(int i = 0; i < 5; i++) {
          timesRest[i] = Integer.parseInt(str[i]);
        }
      }
      else {
        timesRest = Constants.VALUE_DEFAULT_TIMES_REST;   //load default
      }
    } catch (Exception e) {
      timesRest = Constants.VALUE_DEFAULT_TIMES_REST;   //load default
    }
		
    display.setDefaultTimes(timesTraining, timesRest);
	}

}
