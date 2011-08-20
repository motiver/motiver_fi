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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.FoodNameEditorPresenter;
import com.delect.motiver.client.presenter.nutrition.FoodNameEditorPresenter.FoodNameEditorHandler;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.MicroNutrientModel;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * "Food name editor" form with correct textfields
 *
 */
public class FoodNameEditorView extends FoodNameEditorPresenter.FoodNameEditorDisplay {

	
	/*
	 * Return spinner for macronutrients
	 */
	private static MySpinnerField getSpinnerMacronutrients(String name, double value) {
		
    final MySpinnerField spin = new MySpinnerField();  
    spin.setIncrement(.5d);  
    spin.getPropertyEditor().setType(Double.class);  
    spin.setFormat(NumberFormat.getFormat(NumberFormat.getDecimalFormat().getPattern() + " g"));
    spin.setFieldLabel(name);  
    spin.setMinValue(0);  
    spin.setMaxValue(1000d);
    spin.setAllowBlank(false);
    spin.setMessageTarget("none");
    spin.setValue(value); 
    return spin;
	}
	private FoodNameEditorHandler handler;

	private List<SimpleComboBox<String>> microCombos = new ArrayList<SimpleComboBox<String>>(); 
	private List<MySpinnerField> microValues = new ArrayList<MySpinnerField>();
	
	private FoodNameModel model; 

	private LayoutContainer panel = new LayoutContainer();
	
	public FoodNameEditorView() {
		this.setStyleAttribute("margin-top", "5px");
		this.setLayout(new FitLayout());
		this.setHeight(300);
		this.setWidth(325);
	}

	@Override
	public Widget asWidget() {

		try {
			final FormData formData = new FormData("-10");
			
			//create form1
			final FormPanel form = new FormPanel();  
			form.setHeaderVisible(false);
			form.setFrame(true);  
			form.setWidth(325); 
			FormLayout layout = new FormLayout();  
			layout.setLabelWidth(115);  
			form.setLayout(layout); 
			
			//name
			final TextField<String> tfName = new TextField<String>();  
			tfName.setFieldLabel(AppController.Lang.Name()); 
			tfName.setMinLength(3);
			tfName.setValue(model.getName());
			tfName.setMaxLength(50); 
			tfName.setAllowBlank(false);
			tfName.setMessageTarget("none");
			form.add(tfName, formData); 
			//energy
			final MySpinnerField tfEnergy = getSpinnerMacronutrients(AppController.Lang.Energy(), model.getEnergy());
			tfEnergy.setFormat(NumberFormat.getFormat(NumberFormat.getDecimalFormat().getPattern() + " kcal"));  
			form.add(tfEnergy, formData); 
			//Protein
			final MySpinnerField tfProtein = getSpinnerMacronutrients(AppController.Lang.Protein(), model.getProtein());
			form.add(tfProtein, formData); 
			//Carbs
			final MySpinnerField tfCarbs = getSpinnerMacronutrients(AppController.Lang.Carbohydrates(), model.getCarb());
			form.add(tfCarbs, formData); 
			//fet
			final MySpinnerField tfFet = getSpinnerMacronutrients(AppController.Lang.Fet(), model.getFet()); 
			form.add(tfFet, formData); 
			//portion
			final MySpinnerField tfPortion = getSpinnerMacronutrients(AppController.Lang.Portion(), model.getPortion()); 
			form.add(tfPortion, formData);
			
			//micronutrients
			Text text = new Text(AppController.Lang.Micronutrients());
			text.setStyleName("label-title-medium");
			text.setStyleAttribute("margin-top", "20px");
			text.setStyleAttribute("margin-bottom", "10px");
			form.add(text, formData);			

			form.add(panel, formData);
			
			//add micronutrient button
			final Button btnAddMicro = new Button(AppController.Lang.AddTarget(AppController.Lang.Micronutrient().toLowerCase()));
			btnAddMicro.setStyleAttribute("margin-top", "10px");
			btnAddMicro.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					addNewMicroNutrient(0, 0);
				}
			});
			form.add(btnAddMicro, formData);

			//save button
			final Button btnSave = new Button(AppController.Lang.Create()); 
			btnSave.setScale(ButtonScale.MEDIUM);    
			btnSave.setStyleAttribute("margin-top", "5px");
			btnSave.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					
					//disable form
					form.setEnabled(false);
					
					try {						
						model.setName(tfName.getValue());
						model.setEnergy(tfEnergy.getValue().doubleValue());
						model.setProtein(tfProtein.getValue().doubleValue());
						model.setCarb(tfCarbs.getValue().doubleValue());
						model.setFet(tfFet.getValue().doubleValue());
						model.setPortion(tfPortion.getValue().doubleValue());
						
						//micro nutrients
						List<MicroNutrientModel> list = new ArrayList<MicroNutrientModel>();
						for(int i=0; i < microCombos.size(); i++) {
							int nameId = microCombos.get(i).getSelectedIndex();
							double value = microValues.get(i).getValue().doubleValue();
							if(nameId > 0) {
								MicroNutrientModel model = new MicroNutrientModel(nameId);
								model.setValue(value);
								list.add(model);
							}
						}
						model.setMicronutrients(list);
						
					} catch (Exception e) {
			      Motiver.showException(e);
					}
					
					handler.nameSaved(model);
					
				}
			});
			form.addButton(btnSave);
			
			//cancel button
			final Button btnCancel = new Button(AppController.Lang.Cancel());
			btnCancel.setScale(ButtonScale.MEDIUM);
			btnCancel.setStyleAttribute("margin-top", "5px");
			btnCancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					
					//disable form
					form.setEnabled(false);
					
					handler.editCancelled();
					
				}
			});
			form.addButton(btnCancel);
			
			form.setButtonAlign(HorizontalAlignment.LEFT);  
			FormButtonBinding binding = new FormButtonBinding(form);  
			binding.addButton(btnSave);
			
			this.add(form);
			
			tfName.focus();
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	    
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FoodNameModel getModel() {
		return model;
	}

	@Override
	public void setHandler(FoodNameEditorHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(FoodNameModel model) {
		this.model = model;

		//micronutrients
		panel.removeAll();
		microCombos.clear();
		microValues.clear();
		for(MicroNutrientModel mn : model.getMicroNutrients()) {
			addNewMicroNutrient(mn.getNameId(), mn.getValue());
		}
	}

	private void addNewMicroNutrient(int nameId, double value) {
		HorizontalPanel panelSingle = new HorizontalPanel();
		
		final SimpleComboBox<String> combo = new SimpleComboBox<String>();
		for(String str : AppController.LangConstants.MicroNutrients())
    combo.add(str);
		combo.setForceSelection(true);
		combo.setEditable(false);
		combo.setWidth(115);
    combo.setTriggerAction(TriggerAction.ALL);
		combo.setSimpleValue(AppController.LangConstants.MicroNutrients()[nameId]);
		panelSingle.add(combo);
		microCombos.add(combo);
		
		//value
		final MySpinnerField tfVal = getSpinnerMacronutrients("", value);
		tfVal.setStyleAttribute("margin", "0 0 5px 5px");
		tfVal.setWidth(160);
		tfVal.setMaxValue(100000);
		tfVal.setFormat(NumberFormat.getFormat(NumberFormat.getDecimalFormat().getPattern() + " mg"));
		panelSingle.add(tfVal);
		microValues.add(tfVal);

		this.setHeight(300 + microCombos.size() * 32);
		
		panel.add(panelSingle);
		panel.layout();
	}

}
