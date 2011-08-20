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
package com.delect.motiver.client.view.statistics;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.statistics.MonthlyReportPresenter;
import com.delect.motiver.client.view.widget.Widgets;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.MonthlySummaryExerciseModel;
import com.delect.motiver.shared.MonthlySummaryModel;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class MonthlyReportView extends MonthlyReportPresenter.MonthlyReportDisplay {

  private LayoutContainer panelMax = new LayoutContainer();
  private LayoutContainer panelBest = new LayoutContainer();
  private MonthlySummaryModel model;
  private Text textTitle;
  
	public MonthlyReportView() {
	  
	  this.setLayout(new RowLayout());
	  
	  //title
    textTitle = new Text("");
    textTitle.setStyleName("label-title-big");
    this.add(textTitle, new RowData(-1, -1, new Margins(0,0,20,0)));
	  
	  //max
	  Text textTitleMax = new Text(AppController.Lang.OneRepMaxes());
	  textTitleMax.setStyleName("label-title-medium");
	  panelMax.add(textTitleMax);
	  this.add(panelMax, new RowData(-1, -1, new Margins(0,0,20,0)));
    
    //best
    Text textTitleBest = new Text(AppController.Lang.BestSets());
    textTitleBest.setStyleName("label-title-medium");
    panelBest.add(textTitleBest);
    this.add(panelBest, new RowData(-1, -1, new Margins(0,0,20,0)));
	}
	
	@SuppressWarnings("deprecation")
  @Override
	public Widget asWidget() {
	  
	  //update title
	  textTitle.setText(AppController.Lang.MonthlyReport()+": "+AppController.LangConstants.Month()[model.getDate().getMonth()] +" "+ (model.getDate().getYear()+1900));
	  
	  boolean foundMax = false;
	  boolean foundBest = false;
	  
	  for(MonthlySummaryExerciseModel exercise : model.getExercises()) {
	    
	    //create temp exercise
	    ExerciseModel ex = new ExerciseModel();
	    ex.setName(exercise.getExerciseName());
	    ex.setPersonalBest(exercise.isPersonalBest());
	    ex.setReps(exercise.getReps());
	    ex.setSets(exercise.getSets());
	    ex.setUid(model.getUid());
	    ex.setWeights(exercise.getWeights());
	    
	    HorizontalPanel p = Widgets.getReadOnlyExercise(ex);
	    	    
	    //max
	    if(exercise.getType() == 0) {
	      panelMax.add(p);
	      foundMax = true;
	    }
	    //best
	    else {
	      panelBest.add(p);
	      foundBest = true;
	    }
	  }
	  
	  //if not found hide -> title
	  if(!foundMax) {
	    panelMax.setVisible(false);
	  }
    if(!foundBest) {
      panelBest.setVisible(false);
    }
	  
	  return this;
	}

  @Override
  public void setModel(MonthlySummaryModel model) {
    this.model = model;
  }
}
