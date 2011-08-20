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
package com.delect.motiver.client.presenter.statistics;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.SimpleEventBus;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.MonthlySummaryExerciseModel;
import com.delect.motiver.shared.MonthlySummaryModel;

public class MonthlyReportPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class MonthlyReportDisplay extends Display {
	  public abstract void setModel(MonthlySummaryModel model);
	}
	
	private MonthlyReportDisplay display;
  private MonthlySummaryModel summary;

	/**
	 * History for single exercise
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public MonthlyReportPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, MonthlyReportDisplay display, MonthlySummaryModel summary) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.summary = summary;
	}
	

	@Override
	public Display getView() {
		return display;
	}


	@Override
	public void onBind() {
	  //sort exercises
	  List<MonthlySummaryExerciseModel> ex = summary.getExercises();
	  Collections.sort(ex);
	  summary.setExercises(ex);
	  
	  display.setModel(summary);
	}
}
