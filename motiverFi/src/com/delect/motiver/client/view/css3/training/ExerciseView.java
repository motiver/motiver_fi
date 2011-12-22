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
package com.delect.motiver.client.view.css3.training;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.view.css3.ExtendedTextBox;
import com.delect.motiver.client.view.css3.FlexBox;
import com.delect.motiver.client.view.widget.Widgets;
import com.extjs.gxt.ui.client.widget.HtmlContainer;


public class ExerciseView extends com.delect.motiver.client.view.training.ExerciseView {
	
	private FlexBox flexBox = new FlexBox();	
	
	@Override
	public Widget asWidget() {
		
		try {
		  
		  //if our exercise
			if(exercise.getWorkout().getUser().equals(AppController.User)) {
			  
	      //change order "link" (=drag source)
				flexBox.add(getDragButton(), -1, 5);	

	      //food selection combo
	      comboName = addExerciseCombo();
	      containerName.add(comboName);
	      flexBox.add(containerName, -1, 10);
        
	      //video
	      panelVideo.setWidth(16);
	      flexBox.add(panelVideo, -1, 10);

	      //sets
        flexBox.add(getSpinnerField(), -1, 10);

        HtmlContainer labelX1 = new HtmlContainer("x");
        labelX1.setStyleName("label-x");
        flexBox.add(labelX1, -1, 10);

        //reps
        final ExtendedTextBox tfReps = new ExtendedTextBox();
        tfReps.addStyleName("field-amount");
        tfReps.setValue(exercise.getReps());
        tfReps.setEmptyText(AppController.Lang.Reps());
        tfReps.setMaxLength(100);
        tfReps.setWidth("96%");
        tfReps.addChangeHandler(new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent event) {
            exercise.setReps(tfReps.getValue());
            handler.saveData(exercise, false);
          }
        });
        flexBox.add(tfReps, 1, 10);

        HtmlContainer labelX2 = new HtmlContainer("x");
        labelX2.setStyleName("label-x");
        flexBox.add(labelX2, -1, 10);

        //weights
        final ExtendedTextBox tfWeights = new ExtendedTextBox();
        tfWeights.addStyleName("field-amount");
        tfWeights.setValue(exercise.getWeights());
        tfWeights.setEmptyText(AppController.Lang.Weights());
        tfWeights.addChangeHandler(new ChangeHandler() {
          @Override
          public void onChange(ChangeEvent event) {
            exercise.setWeights(tfWeights.getValue());
            handler.saveData(exercise, false);
          }
        });
        tfWeights.setMaxLength(100);
        tfWeights.setWidth("96%");
        flexBox.add(tfWeights, 1, 10);

        //buttons layout
        flexBox.add(getPanelButtons(), -1, -1);
			}
			//not our exercise
			else {			  
			  HorizontalPanel panel = Widgets.getReadOnlyExercise(exercise);        
        flexBox.add(panel, -1, 0);
			}
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelButtons.setVisible(false);
	    
		return this;
	}
}
