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
package com.delect.motiver.client.view.cardio;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.cardio.AddNewRunValuePresenter;
import com.delect.motiver.client.presenter.cardio.AddNewRunValuePresenter.AddNewRunValueHandler;
import com.delect.motiver.client.view.MySpinnerField;
import com.delect.motiver.client.view.TimeSelectFieldView;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.RunModel;
import com.delect.motiver.shared.RunValueModel;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class AddNewRunValueView extends AddNewRunValuePresenter.AddNewRunValueDisplay {

	private ComboBox<RunModel> cbName = new ComboBox<RunModel>();
	private Date date;
	private AddNewRunValueHandler handler;
	private ListStore<RunModel> storeName = new ListStore<RunModel>();

	private Window window = new Window();
	
	

	@Override
	public Widget asWidget() {

		//show window
		window.setSize(400, 310);   
		window.setPlain(true);   
		window.setModal(true);   
		window.setClosable(false);
		window.setResizable(false);
		window.setHeading(AppController.Lang.AddNew(AppController.Lang.RunValue().toLowerCase()));   
		window.setLayout(new FitLayout());
		
		//form
		final FormData formData = new FormData("-20");
		FormPanel simple = new FormPanel();   
		simple.setHeaderVisible(false); 
		simple.setFrame(true);
		simple.setAutoWidth(true);
		simple.setLabelWidth(100);
		//run
		cbName.setDisplayField("n");
		cbName.setFieldLabel(AppController.Lang.Run());
		cbName.setStore(storeName);
		cbName.setEnabled(false);
		cbName.setMinLength(Constants.LIMIT_NAME_MIN);
		cbName.setMaxLength(Constants.LIMIT_NAME_MAX);
		cbName.setAllowBlank(false);
		cbName.setTriggerAction(TriggerAction.ALL);
		simple.add(cbName, formData);
		//date
    final DateField tfDate = CommonUtils.getDateField(date);
    simple.add(tfDate, formData); 
		//time
		final TimeSelectFieldView tfTime = new TimeSelectFieldView(0, null);
		tfTime.setFieldLabel(AppController.Lang.Time());
		simple.add(tfTime);
		
		//duration
		final MySpinnerField tfDuration = CommonUtils.getDurationSpinner();
		simple.add(tfDuration, formData);
    //pulse
    final SpinnerField tfPulse = CommonUtils.getPulseSpinner();
    simple.add(tfPulse, formData);
    //pulse max
    final SpinnerField tfPulseMax = CommonUtils.getPulseSpinner();
    tfPulseMax.setFieldLabel(AppController.Lang.MaxPulse());
    simple.add(tfPulseMax, formData);
    //calories
    final SpinnerField tfCalories = CommonUtils.getCaloriesSpinner();
    simple.add(tfCalories, formData);
    
		//info
		final TextArea tfInfo = new TextArea();  
		tfInfo.setPreventScrollbars(true);  
		tfInfo.setFieldLabel(AppController.Lang.Info());  
    simple.add(tfInfo, formData);  

		//buttons eventhandler
		Button btnAdd = new Button(AppController.Lang.Add());
		btnAdd.setScale(ButtonScale.MEDIUM);
		btnAdd.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				try {
					//return model
					if(handler != null) {
						
						final int pulse = (int) tfPulse.getValue().doubleValue();
            final int pulseMax = (int) tfPulseMax.getValue().doubleValue();
						final int calories = (int) tfCalories.getValue().doubleValue();
						//date and time
						Date date = tfDate.getValue();
						final double time = CommonUtils.getTimeToSeconds(tfTime.getValue());
						date.setHours((int) (time / 3600));
						date.setMinutes((int) ((time % 3600) / 60));
						date = CommonUtils.trimDateToDatabase(date, false);
						final String info = tfInfo.getValue();
						final long duration = tfDuration.getValue().intValue();
					
						RunValueModel value = new RunValueModel();
						value.setCalories(calories);
						value.setDate(date);
						value.setDuration(duration);
						value.setInfo(info);
						value.setPulse(pulse);
            value.setPulseMax(pulseMax);
						
						//get run
						RunModel run = null;
						//if no values
						if(cbName.getValue() == null) {
							final String str = cbName.getRawValue();
							run = new RunModel(0L, str);
						}
						//if user typed new value
						else if(!cbName.getRawValue().equals(cbName.getValue().getNameClient())) {
							final String str = cbName.getRawValue();
							run = new RunModel(0L, str);
						}
						//value selected from list
						else {
							run = cbName.getValue();
            }
						
						handler.newValue(run, value);
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
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public void setHandler(AddNewRunValueHandler handler) {
		this.handler = handler;
	}

	/**
	 * Populates runs
	 * @param models
	 * @param enabled : is combobox enabled
	 */
	@Override
	public void setModels(List<RunModel> models, boolean enabled) {
		if(models.size() > 0) {
			storeName.add(models);
			cbName.setValue(storeName.getAt(0));
		}
		cbName.setEnabled(enabled);
		
		
	}
	
	
	
}
