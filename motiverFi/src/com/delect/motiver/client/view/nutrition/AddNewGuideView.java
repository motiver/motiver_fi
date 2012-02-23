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
/**
 * Shows dialog where user can add new guide value
 */
package com.delect.motiver.client.view.nutrition;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.nutrition.AddNewGuidePresenter;
import com.delect.motiver.client.presenter.nutrition.AddNewGuidePresenter.AddNewGuideHandler;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.GuideValueModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class AddNewGuideView extends AddNewGuidePresenter.AddNewGuideDisplay {

	/*
	 * Return spinner for macronutrients
	 */
	private static MySpinnerField getSpinnerMacronutrients(String name) {
		
    final MySpinnerField spin = new MySpinnerField();  
    spin.setIncrement(5d);  
    spin.getPropertyEditor().setType(Double.class);  
    spin.getPropertyEditor().setFormat(NumberFormat.getFormat("0 '%'")); 
    spin.setFieldLabel(name);  
    spin.setMinValue(0);  
    spin.setMaxValue(100d); 
    spin.setAllowBlank(false);
    spin.setValue(0D); 
    return spin;
	}
	private MessageBox box;

	private Button btnAdd = new Button(AppController.Lang.Add());
	//widgets
	private CheckBox cbIsPercent = new CheckBox();
	private AddNewGuideHandler handler;
	private MySpinnerField tfCarbs1 = null;
	private MySpinnerField tfCarbs2 = null;
	private MySpinnerField tfFet1 = null;
	private MySpinnerField tfFet2 = null;
	private MySpinnerField tfProtein1 = null;
	private MySpinnerField tfProtein2 = null;
	    
	private  Window window = new Window();

	

	@Override
	public Widget asWidget() {

		//show window
		window.setSize(425, 625);
		window.setModal(true);   
		window.setClosable(false);
		window.setResizable(false);
		window.setHeading(AppController.Lang.AddNew(AppController.Lang.GuideValue().toLowerCase()));   
		window.setLayout(new FitLayout());
		
		//form
		final FormData formData = new FormData("-20");
		FormPanel simple = new FormPanel();   
		simple.setHeaderVisible(false); 
		simple.setFrame(true);
		simple.setAutoWidth(true);
		simple.setLabelWidth(130);
		
		//name
		final TextField<String> tfName = new TextField<String>();
		tfName.setFieldLabel(AppController.Lang.Name());
		tfName.setMinLength(Constants.LIMIT_NAME_MIN);
		tfName.setMaxLength(Constants.LIMIT_NAME_MAX);
		tfName.setAllowBlank(false);
    CommonUtils.setWarningMessages(tfName);
		simple.add(tfName, formData);
	    
    //start date
		Date date1 = new Date();
		final DateField dfDate1 = new DateField();
		final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()]);
		DateTimePropertyEditor pr = new DateTimePropertyEditor(fmt);
		dfDate1.setPropertyEditor(pr);
		dfDate1.setValue(date1);
		dfDate1.setFieldLabel(AppController.Lang.DateStart());
		simple.add(dfDate1, formData); 
	    
    //end date
		final DateField dfDate2 = new DateField();
		dfDate2.setPropertyEditor(pr);
		dfDate2.setValue(date1);
		dfDate2.setFieldLabel(AppController.Lang.DateEnd());
		simple.add(dfDate2, formData); 
		
		//is percent
		cbIsPercent.setFieldLabel(AppController.Lang.ValuesAsPercent());
		cbIsPercent.setMessageTarget("none");
		cbIsPercent.setValue(true);
		//change text field labels/max value when checkbox changes
		cbIsPercent.addListener(Events.OnChange, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				
				NumberFormat nf = null;
				Double maxValue = 0D;
				
				//percent
				if(cbIsPercent.getValue()) {
					nf = NumberFormat.getFormat("0 '%'");
					maxValue = 100D;
				}
				//grams
				else {
					nf = NumberFormat.getFormat("0 g");
					maxValue = 1000D;
				}
				
				//format
				tfProtein1.getPropertyEditor().setFormat(nf);
				tfCarbs1.getPropertyEditor().setFormat(nf);
				tfFet1.getPropertyEditor().setFormat(nf);
				tfProtein2.getPropertyEditor().setFormat(nf);
				tfCarbs2.getPropertyEditor().setFormat(nf);
				tfFet2.getPropertyEditor().setFormat(nf);
				
				//max value
				tfProtein1.setMaxValue(maxValue);
				tfCarbs1.setMaxValue(maxValue);
				tfFet1.setMaxValue(maxValue);
				tfProtein2.setMaxValue(maxValue);
				tfCarbs2.setMaxValue(maxValue);
				tfFet2.setMaxValue(maxValue);
				
				//reset values
				if(cbIsPercent.getValue()) {
					tfProtein1.setValue(40);
					tfCarbs1.setValue(40);
					tfFet1.setValue(20);
					tfProtein2.setValue(40);
					tfCarbs2.setValue(30);
					tfFet2.setValue(30);
				}
				else {
					tfProtein1.setValue(0);
					tfCarbs1.setValue(0);
					tfFet1.setValue(0);
					tfProtein2.setValue(0);
					tfCarbs2.setValue(0);
					tfFet2.setValue(0);
				}
				
			}
		});
		simple.add(cbIsPercent, formData);
		
		//training day
		FieldSet fieldSet = new FieldSet();  
		fieldSet.setStyleAttribute("margin-top", "20px");
    fieldSet.setHeading(AppController.Lang.TrainingDay());  
    fieldSet.setCollapsible(false);
    FormLayout layout = new FormLayout();  
    layout.setLabelWidth(130);  
    fieldSet.setLayout(layout); 
    //energy
		final SpinnerField tfEnergy1 = getSpinnerMacronutrients(AppController.Lang.Energy());
		tfEnergy1.setMaxValue(20000d); 
		tfEnergy1.setMinValue(500d);
		tfEnergy1.setValue(500d);
		tfEnergy1.getPropertyEditor().setFormat(NumberFormat.getFormat("0.0 kcal"));
		fieldSet.add(tfEnergy1, formData); 
		//Protein
		tfProtein1 = getSpinnerMacronutrients(AppController.Lang.Protein());
		tfProtein1.setValue(40);
		fieldSet.add(tfProtein1, formData); 
		//Carbs
		tfCarbs1 = getSpinnerMacronutrients(AppController.Lang.Carbohydrates());
		tfCarbs1.setValue(40);
		fieldSet.add(tfCarbs1, formData); 
		//fet
		tfFet1 = getSpinnerMacronutrients(AppController.Lang.Fet()); 
		tfFet1.setValue(20);
		fieldSet.add(tfFet1, formData); 
		simple.add(fieldSet, formData); 
		
		//rest day
		FieldSet fieldSet2 = new FieldSet();  
    fieldSet2.setHeading(AppController.Lang.RestDay());  
    fieldSet2.setCollapsible(false);
    FormLayout layout2 = new FormLayout();  
    layout2.setLabelWidth(130);  
    fieldSet2.setLayout(layout2); 
    //energy
		final SpinnerField tfEnergy2 = getSpinnerMacronutrients(AppController.Lang.Energy());
		tfEnergy2.setMaxValue(20000d);  
		tfEnergy2.setMinValue(500d);
		tfEnergy2.setValue(500d);
		tfEnergy2.getPropertyEditor().setFormat(NumberFormat.getFormat("0.0 kcal"));  
		fieldSet2.add(tfEnergy2, formData); 
		//Protein
		tfProtein2 = getSpinnerMacronutrients(AppController.Lang.Protein());
		tfProtein2.setValue(40);
		fieldSet2.add(tfProtein2, formData); 
		//Carbs
		tfCarbs2 = getSpinnerMacronutrients(AppController.Lang.Carbohydrates());
		tfCarbs2.setValue(30);
		fieldSet2.add(tfCarbs2, formData); 
		//fet
		tfFet2 = getSpinnerMacronutrients(AppController.Lang.Fet()); 
		tfFet2.setValue(30);
		fieldSet2.add(tfFet2, formData); 
		simple.add(fieldSet2, formData); 

		//buttons eventhandler
		btnAdd.setScale(ButtonScale.MEDIUM);
		btnAdd.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				try {
					//if percent check values
					if(cbIsPercent.getValue()) {
						double tot1 = tfProtein1.getValue().doubleValue() + tfCarbs1.getValue().doubleValue() + tfFet1.getValue().doubleValue();
						double tot2 = tfProtein2.getValue().doubleValue() + tfCarbs2.getValue().doubleValue() + tfFet2.getValue().doubleValue();
							
						if(tot1 != 100 || tot2 != 100) {
							box = MessageBox.alert(AppController.Lang.Error(), AppController.Lang.ValuesShouldEqual100Percent(), null);
							return;
						}
					}
					//return model
					if(handler != null) {
						GuideValueModel value = new GuideValueModel();
						value.setName(tfName.getValue());
						Date d1 = dfDate1.getValue();
						Date d2 = dfDate2.getValue();
						d1 = CommonUtils.trimDateToDatabase(d1, true);
						d2 = CommonUtils.trimDateToDatabase(d2, true);
						value.setDates(d1, d2);
						value.setPercent(cbIsPercent.getValue());
						
						value.setEnergy(true, tfEnergy1.getValue().doubleValue());
						value.setProtein(true, tfProtein1.getValue().doubleValue());
						value.setCarb(true, tfCarbs1.getValue().doubleValue());
						value.setFet(true, tfFet1.getValue().doubleValue());
						
						value.setEnergy(false, tfEnergy2.getValue().doubleValue());
						value.setProtein(false, tfProtein2.getValue().doubleValue());
						value.setCarb(false, tfCarbs2.getValue().doubleValue());
						value.setFet(false, tfFet2.getValue().doubleValue());
						
						handler.newValue(value);
					}
					
				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
		});
		simple.addButton(btnAdd);
		Button btnCancel = new Button(AppController.Lang.Cancel());  
		btnCancel.setScale(ButtonScale.MEDIUM);
		//hide window
		btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				handler.cancel();
			}
		}); 
		simple.addButton(btnCancel);   
		simple.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(simple);   
		binding.addButton(btnAdd);   		
		window.add(simple);
		
		window.show();
		
		tfName.focus();
		
		return this;
	}
	
	/**
	 * Panel for creating guide values based on user height,weight,age
	 * @return
	
	private FieldSet getHelpValuesPanel() {

  FieldSet fieldSet3 = new FieldSet();  
  fieldSet3.setHeading("_Examples_"+":");  
  fieldSet3.setCollapsible(false);
  fieldSet3.setLayout(new RowLayout());
	    
  //height
  HorizontalPanel p1 = new HorizontalPanel();
  p1.setSpacing(5);
  p1.addText("Height:");
  NumberField tfHeight = new NumberField();
  tfHeight.setMinValue(120);
  tfHeight.setMaxValue(250);
  //age
	    
  //bulk / weight loss

  //weights
  fieldSet3.addText("_Weight_"+":");
  for(int i=0; i<7; i++) {
  String w = (AppController.User.getMeasurementSystem() == 0)? (60+i*10)+"kg" : (132+i*12)+"lbs";
  Text linkBulk1 = new Text(w);
  linkBulk1.setStyleName("link");
  linkBulk1.setData("i", i);
  linkBulk1.setStyleAttribute("margin-left", "5px");
  linkBulk1.addListener(Events.OnClick, new Listener<BaseEvent>() {
  @Override
  public void handleEvent(BaseEvent be) {
  Text t = (Text)be.getSource();
  int i = Integer.parseInt(t.getData("i").toString());
  loadHelpValues(true, i);
  }
  });
  fieldSet3.add(linkBulk1);
  }
  //bulk
  FieldSet fieldSet4 = new FieldSet();  
  fieldSet4.setHeading("_Examples for diet_"+":");  
  fieldSet4.setCollapsible(false);
  fieldSet4.setLayout(new TableLayout(8));
  fieldSet4.addText("_Weight_"+":");
  for(int i=0; i<7; i++) {
  String w = (AppController.User.getMeasurementSystem() != 0)? (60+i*10)+"kg" : (132+i*12)+"lbs";
  Text linkBulk1 = new Text(w);
  linkBulk1.setStyleName("link");
  linkBulk1.setStyleAttribute("margin-left", "5px");
  fieldSet4.add(linkBulk1);
  }
	    
  return fieldSet3;
	} */

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
		if(window != null && window.isVisible()) {
      window.hide();
    }
	}
	

	@Override
	public void setContentEnabled(boolean enabled) {
		if(window != null) {
			window.setEnabled(enabled);
		}
	}

	@Override
	public void setHandler(AddNewGuideHandler handler) {
		this.handler = handler;
	}
}
