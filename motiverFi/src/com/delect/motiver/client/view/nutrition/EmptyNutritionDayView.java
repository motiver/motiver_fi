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
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.EmptyNutritionDayPresenter;
import com.delect.motiver.client.presenter.nutrition.EmptyNutritionDayPresenter.EmptyNutritionDayHandler;
import com.delect.motiver.client.view.TimeSelectFieldView;
import com.delect.motiver.client.view.TimeSelectFieldView.TimeSelectFieldHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Empty view given text
 */
public class EmptyNutritionDayView extends EmptyNutritionDayPresenter.EmptyNutritionDayDisplay {

	private EmptyNutritionDayHandler handler;
  private int[] timesTraining = new int[6];
  private int[] timesRest = new int[5];
  
  private TimeSelectFieldView fieldR1;
  private TimeSelectFieldView fieldR2;
  private TimeSelectFieldView fieldR3;
  private TimeSelectFieldView fieldR4;
  private TimeSelectFieldView fieldR5;
  private TimeSelectFieldView fieldT1;
  private TimeSelectFieldView fieldT2;
  private TimeSelectFieldView fieldT3;
  private TimeSelectFieldView fieldT4;
  private TimeSelectFieldView fieldT5;
  private TimeSelectFieldView fieldT6;
  
  //handler for time change
  private TimeSelectFieldHandler handlerTimeField = new TimeSelectFieldHandler() {
    @Override
    public void timeChanged(int time) {
      final int[] timesTraining = new int[] {
          fieldT1.getTime(),
          fieldT2.getTime(),
          fieldT3.getTime(),
          fieldT4.getTime(),
          fieldT5.getTime(),
          fieldT6.getTime()
        };
        final int[] timesRest = new int[] {
          fieldR1.getTime(),
          fieldR2.getTime(),
          fieldR3.getTime(),
          fieldR4.getTime(),
          fieldR5.getTime()
        };
        
        handler.timesChanged(timesTraining, timesRest);
    }
  };
	
	@Override
	public Widget asWidget() {
		this.setStyleName("panel-empty");
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		this.setLayout(tl);
		
		//text
		TableData td = new TableData();
		td.setColspan(2);
		td.setHeight("100px");
		this.add(new Text(AppController.Lang.EmptyNutritionDayDesc()), td);
		
		//or
		TableData td2 = new TableData();
		td2.setColspan(2);
		td2.setHeight("40px");
		this.add(new Text("<b>" + AppController.Lang.Or() + "</b>"), td2);
		
		//templates
		LayoutContainer panelTemplates = new LayoutContainer();
		panelTemplates.setStyleAttribute("margin-bottom", "10px");
		TableLayout layoutTemplates = new TableLayout(2);
		layoutTemplates.setCellSpacing(10);
		layoutTemplates.setCellVerticalAlign(VerticalAlignment.TOP);
		panelTemplates.setLayout(layoutTemplates);
        
		//training day
		LayoutContainer panelTempTraining = new LayoutContainer();
		panelTempTraining.setStyleAttribute("margin-right", "50px");
		TableLayout layout = new TableLayout(2);
		layout.setCellSpacing(5);
		panelTempTraining.setLayout(layout);
		TableData tdLeft = new TableData();
		tdLeft.setWidth("80px");
		//title
		Text text1 = new Text(AppController.Lang.TrainingDay());
		text1.setStyleName("label-title-medium");
		TableData tdColspan1 = new TableData();
		tdColspan1.setColspan(2);
		panelTempTraining.add(text1, tdColspan1);
		//fields
		fieldT1 = new TimeSelectFieldView(timesTraining[0], handlerTimeField );
		panelTempTraining.add(fieldT1, tdLeft);
		panelTempTraining.add(new Text(AppController.Lang.Breakfast()));
		fieldT2 = new TimeSelectFieldView(timesTraining[1], handlerTimeField);
		panelTempTraining.add(fieldT2, tdLeft);
		panelTempTraining.add(new Text(AppController.Lang.Lunch()));
		fieldT3 = new TimeSelectFieldView(timesTraining[2], handlerTimeField);
		panelTempTraining.add(fieldT3, tdLeft);
		panelTempTraining.add(new Text(AppController.Lang.Snack()));
		fieldT4 = new TimeSelectFieldView(timesTraining[3], handlerTimeField);
		panelTempTraining.add(fieldT4, tdLeft);
		panelTempTraining.add(new Text(AppController.Lang.RecoveryDrink()));
		fieldT5 = new TimeSelectFieldView(timesTraining[4], handlerTimeField);
		panelTempTraining.add(fieldT5, tdLeft);
		panelTempTraining.add(new Text(AppController.Lang.Dinner()));
		fieldT6 = new TimeSelectFieldView(timesTraining[5], handlerTimeField);
		panelTempTraining.add(fieldT6, tdLeft);
		panelTempTraining.add(new Text(AppController.Lang.Supper()));	
		
		panelTemplates.add(panelTempTraining);

		//rest day
		LayoutContainer panelTempRest = new LayoutContainer();
		panelTempRest.setStyleAttribute("margin-left", "50px");
		TableLayout layout2 = new TableLayout(2);
		layout2.setCellSpacing(5);
		panelTempRest.setLayout(layout2);
		//title
		Text text2 = new Text(AppController.Lang.RestDay());
		text2.setStyleName("label-title-medium");
		TableData tdColspan2 = new TableData();
		tdColspan2.setColspan(2);
		panelTempRest.add(text2, tdColspan2);
		//fields
		fieldR1 = new TimeSelectFieldView(timesRest[0], handlerTimeField);
		panelTempRest.add(fieldR1);
		panelTempRest.add(new Text(AppController.Lang.Breakfast()));
		fieldR2 = new TimeSelectFieldView(timesRest[1], handlerTimeField);
		panelTempRest.add(fieldR2, tdLeft);
		panelTempRest.add(new Text(AppController.Lang.Lunch()));
		fieldR3 = new TimeSelectFieldView(timesRest[2], handlerTimeField);
		panelTempRest.add(fieldR3, tdLeft);
		panelTempRest.add(new Text(AppController.Lang.Snack()));
		fieldR4 = new TimeSelectFieldView(timesRest[3], handlerTimeField);
		panelTempRest.add(fieldR4, tdLeft);
		panelTempRest.add(new Text(AppController.Lang.Dinner()));
		fieldR5 = new TimeSelectFieldView(timesRest[4], handlerTimeField);
		panelTempRest.add(fieldR5, tdLeft);
		panelTempRest.add(new Text(AppController.Lang.Supper()));
		
		panelTemplates.add(panelTempRest);

		//buttons
		//button
		MyButton btnAddTraining = new MyButton(AppController.Lang.AddTarget(AppController.Lang.TheseTimes().toLowerCase()));
		btnAddTraining.setScale(ButtonScale.MEDIUM);
		btnAddTraining.setColor(MyButton.Style.GREEN);
		btnAddTraining.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				
				//get times
			  int[] times = new int[] {
          Functions.getTimeToSeconds(fieldT1.getValue()),
          Functions.getTimeToSeconds(fieldT2.getValue()),
          Functions.getTimeToSeconds(fieldT3.getValue()),
          Functions.getTimeToSeconds(fieldT4.getValue()),
          Functions.getTimeToSeconds(fieldT5.getValue()),
          Functions.getTimeToSeconds(fieldT6.getValue())
				};
				
				handler.addTimeTemplate(times);
			}
		});
		panelTemplates.add(btnAddTraining);
		
		MyButton btnAddRest = new MyButton(AppController.Lang.AddTarget(AppController.Lang.TheseTimes().toLowerCase()));
		btnAddRest.setScale(ButtonScale.MEDIUM);
		btnAddRest.setColor(MyButton.Style.GREEN);
		btnAddRest.setStyleAttribute("margin-left", "40px");
		btnAddRest.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				
				//get times
			  int[] times = new int[] {
          Functions.getTimeToSeconds(fieldR1.getValue()),
          Functions.getTimeToSeconds(fieldR2.getValue()),
          Functions.getTimeToSeconds(fieldR3.getValue()),
          Functions.getTimeToSeconds(fieldR4.getValue()),
          Functions.getTimeToSeconds(fieldR5.getValue())
				};
				
				handler.addTimeTemplate(times);
			}
		});
		panelTemplates.add(btnAddRest);
		
		this.add(panelTemplates);
		
		return this;
	}

	@Override
	public void setHandler(EmptyNutritionDayHandler handler) {
		this.handler = handler;
	}

  @Override
  public void setDefaultTimes(int[] timesTraining, int[] timesRest) {
    this.timesTraining = timesTraining;
    this.timesRest = timesRest;
  }

}
