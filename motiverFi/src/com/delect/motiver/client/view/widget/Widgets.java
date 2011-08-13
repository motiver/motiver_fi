/**
 * 
 */
package com.delect.motiver.client.view.widget;

import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.Functions;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Statics methods for different "widgets"
 * @author Antti
 *
 */
public class Widgets {

  /**
   * Returns read only exercise view
   * @param exercise
   * @return
   */
  public static HorizontalPanel getReadOnlyExercise(ExerciseModel exercise) {
    
    HorizontalPanel panel = new HorizontalPanel();
    panel.setSpacing(5);
    
    //name
    Text textName = new Text();
    if(exercise.getName() != null) {
      textName.setText( Functions.getExerciseName(exercise.getName()) );
    }
    textName.addStyleName("field-readonly field-name");
    textName.setWidth(250);
    panel.add(textName);

    //sets
    final Text textSets = new Text();
    if(exercise.getSets() != 0) {
      textSets.setText( String.valueOf(exercise.getSets()) );
    }
    else {
      textSets.setText("-");
    }
    panel.add(textSets);

    HtmlContainer labelX1 = new HtmlContainer("x");
    labelX1.setStyleName("label-x");
    panel.add(labelX1);
    
    //reps
    Text textReps = new Text();
    if(exercise.getReps().length() != 0) {
      textReps.setText( exercise.getReps() );
    }
    else {
      textReps.setText("-");
    }
    panel.add(textReps);

    HtmlContainer labelX2 = new HtmlContainer("x");
    labelX2.setStyleName("label-x");
    panel.add(labelX2);
    
    //weights
    Text textWeights = new Text();
    if(exercise.getWeights().length() != 0) {
      textWeights.setText( exercise.getWeights() );
    }
    else {
      textWeights.setText("-");
    }
    panel.add(textWeights);
    
    //personal best
    if(exercise.isPersonalBest()) {
      Html html = new Html("<b>PR</b>");
      panel.add(html);
    }
    
    return panel;
  }
}
