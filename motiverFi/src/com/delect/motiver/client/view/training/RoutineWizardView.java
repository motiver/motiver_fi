/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.training;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.training.RoutineWizardPresenter;
import com.delect.motiver.client.presenter.training.RoutineWizardPresenter.RoutineWizardHandler;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * Show users friends in list
 */
public class RoutineWizardView extends RoutineWizardPresenter.RoutineWizardDisplay {


	private Button btnCreateBB = new Button();
	private Button btnCreatePL = new Button();
	private RoutineWizardHandler handler;

	public RoutineWizardView() {
		
		//two tabs: bodybuilding and powerlifting
		TabPanel panel = new TabPanel();  
    panel.setPlain(true);  
    panel.setAutoHeight(true);
	    
    //bodybuilding
    TabItem tabBB = new TabItem(RoutineWizardPresenter.Lang.Bodybuilding());  
    tabBB.addStyleName("pad-text");  
    tabBB.add(initBodybuildingTab());
    panel.add(tabBB);  
	    
    //powerlifting
    TabItem tabPL = new TabItem(RoutineWizardPresenter.Lang.Powerlifting());  
    tabPL.addStyleName("pad-text"); 
    tabPL.add(initPowerliftingTab()); 
    panel.add(tabPL);  
	    
    this.add(panel);
	}

	@Override
	public Widget asWidget() {	    
		return this;
	}

	@Override
	public void setHandler(RoutineWizardHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMessageBodybuilding(String msg) {
		if(msg == null) {
			btnCreateBB.setEnabled(true);
			btnCreateBB.setText(AppController.Lang.Create());
		}
		else {
			btnCreateBB.setEnabled(false);
			btnCreateBB.setText(msg);
		}
	}
	@Override
	public void setMessagePowerlifting(String msg) {
		if(msg == null) {
			btnCreatePL.setEnabled(true);
			btnCreatePL.setText(AppController.Lang.Create());
		}
		else {
			btnCreatePL.setEnabled(false);
			btnCreatePL.setText(msg);
		}
	}

	/**
	 * Initializes the bodybuilding tab
	 * @return tab's content
	 */
	private LayoutContainer initBodybuildingTab() {
		LayoutContainer panel = new LayoutContainer();
		
		//form
		FormPanel simple = new FormPanel();  
    simple.setHeaderVisible(false);
    simple.setFrame(true);  
    simple.setWidth(600);
    simple.setFieldWidth(200);
    FormData formData = new FormData("-20");
	    
    //desc
    Text textDesc = new Text(RoutineWizardPresenter.Lang.CreateDesc());
    textDesc.setStyleName("label-form-desc");
    simple.add(textDesc, formData);
	    
    //fieldset: split
    FieldSet fieldSet = new FieldSet();  
    fieldSet.setLayout(new FormLayout());
    fieldSet.setHeading(RoutineWizardPresenter.Lang.Split());
    Text textSplitDesc = new Text(RoutineWizardPresenter.Lang.SplitDesc());
    textSplitDesc.setStyleName("label-form-desc");
    fieldSet.add(textSplitDesc, formData);
	    
    //split
    final SimpleComboBox<String> cbSplit = new SimpleComboBox<String>();
    cbSplit.setTriggerAction(TriggerAction.ALL);
    cbSplit.setEditable(false);
    cbSplit.setFieldLabel(RoutineWizardPresenter.Lang.Split());
    cbSplit.setData("text", "split desc");
    for(int i=0; i < RoutineWizardPresenter.LangConstants.Split().length; i++) {
      cbSplit.add(RoutineWizardPresenter.LangConstants.Split()[i]);
    }
    fieldSet.add(cbSplit, formData);
	    
    //muscle groups
    final SimpleComboBox<String> cbMuscleGroupDiv = new SimpleComboBox<String>();
    cbMuscleGroupDiv.setTriggerAction(TriggerAction.ALL);
    cbMuscleGroupDiv.setEditable(false);
    cbMuscleGroupDiv.setFieldLabel(RoutineWizardPresenter.Lang.MuscleGroupDivisions());
    cbMuscleGroupDiv.setVisible(false);
    fieldSet.add(cbMuscleGroupDiv, formData);
	    
    simple.add(fieldSet);

    //fieldset: split
    FieldSet fieldSet2 = new FieldSet();  
    fieldSet2.setLayout(new FormLayout());
    fieldSet2.setHeading(RoutineWizardPresenter.Lang.HowOften());
    Text textHowOften = new Text(RoutineWizardPresenter.Lang.HowOftenDesc());
    textHowOften.setStyleName("label-form-desc");
    fieldSet2.add(textHowOften, formData);
	    
    //how often
    final SimpleComboBox<String> cbHowOften = new SimpleComboBox<String>();
    cbHowOften.setTriggerAction(TriggerAction.ALL);
    cbHowOften.setEditable(false);
    cbHowOften.setFieldLabel(RoutineWizardPresenter.Lang.HowOften());
    for(int i=0; i < RoutineWizardPresenter.LangConstants.HowOften().length; i++) {
      cbHowOften.add(RoutineWizardPresenter.LangConstants.HowOften()[i]);
    }
    fieldSet2.add(cbHowOften, formData);
	    
    //training days
    final SimpleComboBox<String> cbTrainingDays = new SimpleComboBox<String>();
    cbTrainingDays.setTriggerAction(TriggerAction.ALL);
    cbTrainingDays.setEditable(false);
    cbTrainingDays.setFieldLabel(AppController.Lang.TrainingDays());
    for(int i=0; i < RoutineWizardPresenter.LangConstants.TrainingDays1().length; i++) {
      cbTrainingDays.add(RoutineWizardPresenter.LangConstants.TrainingDays1()[i]);
    }
    cbTrainingDays.setValue( cbTrainingDays.getStore().getAt( 0 ));
    fieldSet2.add(cbTrainingDays, formData);

    simple.add(fieldSet2);

    //fieldset: split
    FieldSet fieldSet3 = new FieldSet();  
    fieldSet3.setLayout(new FormLayout());
    fieldSet3.setHeading(RoutineWizardPresenter.Lang.WeakMuscle());
    Text textWeakMuscle = new Text(RoutineWizardPresenter.Lang.WeakMuscleDesc());
    textWeakMuscle.setStyleName("label-form-desc");
    fieldSet3.add(textWeakMuscle, formData);
	    
    //weak muscle
    final SimpleComboBox<String> cbWeakMuscle = new SimpleComboBox<String>();
    cbWeakMuscle.setTriggerAction(TriggerAction.ALL);
    cbWeakMuscle.setEditable(false);
    cbWeakMuscle.setFieldLabel(RoutineWizardPresenter.Lang.WeakMuscle());
    for(int i=0; i < RoutineWizardPresenter.LangConstants.MuscleGroups().length; i++) {
      cbWeakMuscle.add(RoutineWizardPresenter.LangConstants.MuscleGroups()[i]);
    }
    fieldSet3.add(cbWeakMuscle, formData);

    simple.add(fieldSet3);
	    
    //select first item
    Listener<BaseEvent> listenerSelectFirst = new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(BaseEvent be) {
				SimpleComboBox<String> cb = (SimpleComboBox<String>) be.getSource();
				cb.setValue(cb.getStore().getAt(0));
			}
    };
    cbSplit.addListener(Events.Render, listenerSelectFirst);
    cbHowOften.addListener(Events.Render, listenerSelectFirst);
    cbTrainingDays.addListener(Events.Render, listenerSelectFirst);
    cbWeakMuscle.addListener(Events.Render, listenerSelectFirst);
	    
    //eventhandler for split combo
    cbSplit.addListener(Events.Select, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//2-split
				if(cbSplit.getSelectedIndex() == 1) {
					cbMuscleGroupDiv.removeAll();
          for(int i=0; i < RoutineWizardPresenter.LangConstants.SplitDivision2().length; i++) {
            cbMuscleGroupDiv.add(RoutineWizardPresenter.LangConstants.SplitDivision2()[i]);
          }
					//show
					cbMuscleGroupDiv.setVisible(true);
				}
				//3-split
				else if(cbSplit.getSelectedIndex() == 2) {
					cbMuscleGroupDiv.removeAll();
          for(int i=0; i < RoutineWizardPresenter.LangConstants.SplitDivision3().length; i++) {
            cbMuscleGroupDiv.add(RoutineWizardPresenter.LangConstants.SplitDivision3()[i]);
          }
					//show
					cbMuscleGroupDiv.setVisible(true);
				}
				//4-split
				else if(cbSplit.getSelectedIndex() == 3) {
					cbMuscleGroupDiv.removeAll();
          for(int i=0; i < RoutineWizardPresenter.LangConstants.SplitDivision4().length; i++) {
            cbMuscleGroupDiv.add(RoutineWizardPresenter.LangConstants.SplitDivision4()[i]);
          }
					//show
					cbMuscleGroupDiv.setVisible(true);
				}
				//fullbody
				else {
					cbMuscleGroupDiv.setVisible(false);
				}
				cbMuscleGroupDiv.setValue( cbMuscleGroupDiv.getStore().getAt( 0 ));
			}
    });
    //eventhandler for how often combo
    cbHowOften.addListener(Events.Select, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//3-4
				if(cbHowOften.getSelectedIndex() == 1) {
					cbTrainingDays.removeAll();
          for(int i=0; i < RoutineWizardPresenter.LangConstants.TrainingDays2().length; i++) {
            cbTrainingDays.add(RoutineWizardPresenter.LangConstants.TrainingDays2()[i]);
          }
				}
				//5-6
				else if(cbHowOften.getSelectedIndex() == 2) {
					cbTrainingDays.removeAll();
          for(int i=0; i < RoutineWizardPresenter.LangConstants.TrainingDays3().length; i++) {
            cbTrainingDays.add(RoutineWizardPresenter.LangConstants.TrainingDays3()[i]);
          }
				}
				//1-2
				else {
					cbTrainingDays.removeAll();
          for(int i=0; i < RoutineWizardPresenter.LangConstants.TrainingDays1().length; i++) {
            cbTrainingDays.add(RoutineWizardPresenter.LangConstants.TrainingDays1()[i]);
          }
				}
				cbTrainingDays.setValue( cbTrainingDays.getStore().getAt( 0 ));
			}
    });
	    
    //buttons eventhandler
		btnCreateBB.setText(AppController.Lang.Create());
		btnCreateBB.setScale(ButtonScale.MEDIUM);
		btnCreateBB.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				try {					
					//return model
					if(handler != null) {
						int split = cbSplit.getSelectedIndex();
						int muscleGroupDiv = cbMuscleGroupDiv.getSelectedIndex();
						int howOften = cbHowOften.getSelectedIndex();
						int trainingDays = cbTrainingDays.getSelectedIndex();
						int weakMuscle = cbWeakMuscle.getSelectedIndex();
						
						handler.newBodybuildingRoutine(split, muscleGroupDiv, howOften, trainingDays, weakMuscle);
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
		});
		simple.addButton(btnCreateBB);
		
		panel.add(simple);
		
		return panel;
	}


	/**
	 * Initializes the bodybuilding tab
	 * @return tab's content
	 */
	private LayoutContainer initPowerliftingTab() {
		LayoutContainer panel = new LayoutContainer();
		
		//form
		FormPanel simple = new FormPanel();  
    simple.setHeaderVisible(false);
    simple.setFrame(true);  
    simple.setWidth(600);
    simple.setFieldWidth(200);
    FormData formData = new FormData("-20");
	    
    //desc
    Text textDesc = new Text(RoutineWizardPresenter.Lang.CreateStrengthDesc());
    textDesc.setStyleName("label-form-desc");
    simple.add(textDesc, formData);
	    
    //fieldset: priority
    FieldSet fieldSet = new FieldSet();  
    fieldSet.setLayout(new FormLayout());
    fieldSet.setHeading(RoutineWizardPresenter.Lang.Priority());
    Text textSplitDesc = new Text(RoutineWizardPresenter.Lang.PriorityDesc());
    textSplitDesc.setStyleName("label-form-desc");
    fieldSet.add(textSplitDesc, formData);
	    
    //priority
    final SimpleComboBox<String> cbPriority = new SimpleComboBox<String>();
    cbPriority.setTriggerAction(TriggerAction.ALL);
    cbPriority.setEditable(false);
    cbPriority.setFieldLabel(RoutineWizardPresenter.Lang.Priority());
    for(int i=0; i < RoutineWizardPresenter.LangConstants.Priority().length; i++) {
      cbPriority.add(RoutineWizardPresenter.LangConstants.Priority()[i]);
    }
    fieldSet.add(cbPriority, formData);
	    
    simple.add(fieldSet);

    //fieldset: split
    FieldSet fieldSet2 = new FieldSet();  
    fieldSet2.setLayout(new FormLayout());
    fieldSet2.setHeading(RoutineWizardPresenter.Lang.HowOften());
    Text textHowOften = new Text(RoutineWizardPresenter.Lang.HowOftenStrengthDesc());
    textHowOften.setStyleName("label-form-desc");
    fieldSet2.add(textHowOften, formData);
	    
    //how often
    final SimpleComboBox<String> cbHowOften = new SimpleComboBox<String>();
    cbHowOften.setTriggerAction(TriggerAction.ALL);
    cbHowOften.setEditable(false);
    cbHowOften.setFieldLabel(RoutineWizardPresenter.Lang.HowOften());
    for(int i=0; i < RoutineWizardPresenter.LangConstants.HowOftenStrength().length; i++) {
      cbHowOften.add(RoutineWizardPresenter.LangConstants.HowOftenStrength()[i]);
    }
    fieldSet2.add(cbHowOften, formData);

    simple.add(fieldSet2);

    //fieldset: exercise max
    FieldSet fieldSet3 = new FieldSet();  
    fieldSet3.setLayout(new FormLayout());
    fieldSet3.setHeading(RoutineWizardPresenter.Lang.ExerciseMax());
    Text textExerciseMax = new Text(RoutineWizardPresenter.Lang.ExerciseMaxDesc());
    textExerciseMax.setStyleName("label-form-desc");
    fieldSet3.add(textExerciseMax, formData);
	    
    //squat
    final SpinnerField textSquat = new SpinnerField();
    textSquat.setFormat(NumberFormat.getFormat("0"));
    textSquat.setValue(100);
    textSquat.setMinValue(40);
    textSquat.setMaxValue(1000);
    textSquat.setIncrement(5);
    textSquat.setAllowBlank(false);
    textSquat.setFieldLabel(RoutineWizardPresenter.Lang.Squat());
    fieldSet3.add(textSquat, formData);
	    
    //deadlift
    final SpinnerField textDeadlift = new SpinnerField();
    textDeadlift.setFormat(NumberFormat.getFormat("0"));
    textDeadlift.setValue(100);
    textDeadlift.setMinValue(40);
    textDeadlift.setMaxValue(1000);
    textDeadlift.setIncrement(5);
    textDeadlift.setAllowBlank(false);
    textDeadlift.setFieldLabel(RoutineWizardPresenter.Lang.Deadlift());
    fieldSet3.add(textDeadlift, formData);
	    
    //bench
    final SpinnerField textBench = new SpinnerField();
    textBench.setFormat(NumberFormat.getFormat("0"));
    textBench.setValue(100);
    textBench.setMinValue(40);
    textBench.setMaxValue(1000);
    textBench.setIncrement(5);
    textBench.setAllowBlank(false);
    textBench.setFieldLabel(RoutineWizardPresenter.Lang.Bench());
    fieldSet3.add(textBench, formData);
	    
    //military
    final SpinnerField textMilitary = new SpinnerField();
    textMilitary.setFormat(NumberFormat.getFormat("0"));
    textMilitary.setValue(100);
    textMilitary.setMinValue(40);
    textMilitary.setMaxValue(1000);
    textMilitary.setIncrement(5);
    textMilitary.setAllowBlank(false);
    textMilitary.setFieldLabel(RoutineWizardPresenter.Lang.Military());
    fieldSet3.add(textMilitary, formData);

    simple.add(fieldSet3);
	    
    //select first item
    Listener<BaseEvent> listenerSelectFirst = new Listener<BaseEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(BaseEvent be) {
				SimpleComboBox<String> cb = (SimpleComboBox<String>) be.getSource();
				cb.setValue(cb.getStore().getAt(0));
			}
    };
    cbPriority.addListener(Events.Render, listenerSelectFirst);
    cbHowOften.addListener(Events.Render, listenerSelectFirst);
	    
    //buttons eventhandler
		btnCreatePL.setText(AppController.Lang.Create());
		btnCreatePL.setScale(ButtonScale.MEDIUM);
		btnCreatePL.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				try {					
					//return model
					if(handler != null) {
						int priority = cbPriority.getSelectedIndex();
						int howOften = cbHowOften.getSelectedIndex();
						int squat = textSquat.getValue().intValue();
						int deadlift = textDeadlift.getValue().intValue();
						int bench = textBench.getValue().intValue();
						int military = textMilitary.getValue().intValue();
						
						handler.newPowerliftingRoutine(priority, howOften, squat, deadlift, bench, military);
						
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
		});
		simple.addButton(btnCreatePL);
		
		panel.add(simple);
		
		return panel;
	}
	
}
