/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
/*
 * Shows dialog where user can add new measurement
 * Callbacks: ??
 */
package com.delect.motiver.client.view.profile;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.profile.AddNewMeasurementValuePresenter;
import com.delect.motiver.client.presenter.profile.AddNewMeasurementValuePresenter.AddNewMeasurementValueHandler;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.MeasurementModel;
import com.delect.motiver.shared.MeasurementValueModel;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class AddNewMeasurementValueView extends AddNewMeasurementValuePresenter.AddNewMeasurementValueDisplay {

	private AddNewMeasurementValueHandler handler;
	private MeasurementModel measurement;
	Window window = new Window();
	
	

	@Override
	public Widget asWidget() {

		//show window
		window.setSize(400, 150);   
		window.setPlain(true);   
		window.setModal(true);   
		window.setClosable(false);
		window.setResizable(false);
		window.setHeading(AppController.Lang.AddNew(AppController.Lang.MeasurementValue().toLowerCase()));   
		window.setLayout(new FitLayout());
		
		//form
		final FormData formData = new FormData("-20");
		FormPanel simple = new FormPanel();   
		simple.setHeaderVisible(false); 
		simple.setFrame(true);
		simple.setAutoWidth(true);
		simple.setLabelWidth(100);
		//date
		Date date1 = new Date();
		final DateField dfMeas = new DateField();
		final DateTimeFormat fmt = DateTimeFormat.getFormat(StringConstants.DATEFORMATS[AppController.User.getDateFormat()]);
		DateTimePropertyEditor pr = new DateTimePropertyEditor(fmt);
		dfMeas.setPropertyEditor(pr);
		dfMeas.setValue(date1);
		dfMeas.setFieldLabel(AppController.Lang.Date());
		simple.add(dfMeas, formData); 
		//value
		final MySpinnerField textValue = new MySpinnerField();   
		textValue.setFieldLabel(AppController.Lang.Value());   
		textValue.setAllowBlank(false);   
		textValue.setEditable(true);
		textValue.setMinValue(0);  
		textValue.setMaxValue(10000);
		textValue.setValue(0D);
		textValue.setPropertyEditorType(Double.class);
		textValue.setFormat(NumberFormat.getFormat("0.0 " + measurement.getUnit()));
		simple.add(textValue, formData); 

		//buttons eventhandler
		Button btnAdd = new Button(AppController.Lang.Add());
		btnAdd.setScale(ButtonScale.MEDIUM);
		btnAdd.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				try {
					if(handler != null) {
						final double val = textValue.getValue().doubleValue();
						Date date = dfMeas.getValue();
						date = Functions.trimDateToDatabase(date, true);
										
						MeasurementValueModel value =  new MeasurementValueModel();
						value.setDate(date);
						value.setValue(val);
						
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
		
		return this;
	}

	@Override
	public void onStop() {
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
	public void setHandler(AddNewMeasurementValueHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMeasurement(MeasurementModel measurement) {
		this.measurement = measurement;
	}
	
	
	
}
