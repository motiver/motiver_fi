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
package com.delect.motiver.client.view.profile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.profile.OldDataFetchPresenter;
import com.delect.motiver.client.presenter.profile.OldDataFetchPresenter.OldDataFetchHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

/**
 * 
 * Init top view
 *  - logo
 *  - profile button
 *  - sign out button
 */
public class OldDataFetchView extends OldDataFetchPresenter.OldDataFetchDisplay {

	private OldDataFetchHandler handler;
  MessageBox box = null;
  final Dialog simple = new Dialog();  
  final TextField<String> tfName = new TextField<String>();
	final TextField<String> tfPass = new TextField<String>();  
	public OldDataFetchView() {

    simple.setHeading(AppController.Lang.EnterXlGainInfo());  
    simple.setBodyStyleName("pad-text");  
    simple.setModal(true);  
    simple.setWidth(335);
    simple.setButtons(Dialog.OK);
    simple.setHideOnButtonClick(true);
	    
    //form
    final FormData formData = new FormData("-10");
    //create form1
		final FormPanel form = new FormPanel();  
    form.setHeaderVisible(false);
    form.setFrame(true);  
    form.setWidth(325); 
    FormLayout layoutForm = new FormLayout();  
    layoutForm.setLabelWidth(115);  
    form.setLayout(layoutForm); 
	    
    //name	    
    tfName.setFieldLabel(AppController.Lang.Username()); 
    tfName.setMinLength(5);
    tfName.setMaxLength(40); 
    tfName.setValue("");
    tfName.setAllowBlank(false);
    CommonUtils.setWarningMessages(tfName);
    form.add(tfName, formData); 
	    
    //pass
    tfPass.setFieldLabel(AppController.Lang.Password()); 
    tfPass.setMinLength(3);
    tfPass.setValue("");
    tfPass.setPassword(true);
    tfPass.setMaxLength(40); 
    tfPass.setAllowBlank(false);
    CommonUtils.setWarningMessages(tfPass);
    form.add(tfPass, formData); 
	    
    Text textDesc = new Text(AppController.Lang.FetchSelectDesc());
    textDesc.setStyleAttribute("margin", "10px 0 5px 0");
    form.add(textDesc);

		//what to fetch
		final CheckBox cbTraining = new CheckBox();
		cbTraining.setFieldLabel(AppController.Lang.Training());
    form.add(cbTraining, formData); 
    final CheckBox cbCardio = new CheckBox();
		cbCardio.setFieldLabel(AppController.Lang.Cardio());
    form.add(cbCardio, formData); 
    final CheckBox cbMeasurements = new CheckBox();
		cbMeasurements.setFieldLabel(AppController.Lang.Measurements());
    form.add(cbMeasurements, formData); 
    final CheckBox cbNutrition = new CheckBox();
		cbNutrition.setFieldLabel(AppController.Lang.Nutrition());
    form.add(cbNutrition, formData); 
		
    simple.add(form);

    simple.addListener(Events.Hide, new Listener<WindowEvent>() {
      public void handleEvent(WindowEvent objEvent) {
        try {
					if(objEvent.getButtonClicked() != null && objEvent.getButtonClicked().equals(simple.getButtonById(Dialog.OK)) && tfName.getValue().length() >= 5 && tfPass.getValue().length() >= 5) {
            handler.go(tfName.getValue(), tfPass.getValue(), cbTraining.getValue(), cbCardio.getValue(), cbNutrition.getValue(), cbMeasurements.getValue());
          }
				} catch (Exception e) {
		      Motiver.showException(e);
				}
      }
    });
	    
    //layout
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
    this.setHeight(35);
	    		
	}
	
	@Override
	public Widget asWidget() {
	    
		this.add(new Label(AppController.Lang.FetchFromXlGain()), new HBoxLayoutData(new Margins(0, 10, 0, 0)));
		
    //button that opens dialog
    MyButton btnShow = new MyButton();
    btnShow.setText(AppController.Lang.FetchData());
    btnShow.setScale(ButtonScale.MEDIUM);
    btnShow.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
        simple.show();
			}
	    	
    });
    this.add(btnShow);
	    
    return this;
	}

	@Override
	public void hideProgress() {
		if(box != null && box.isVisible()) {
      box.close();
    } 
	}

	@Override
	public void onStop() {
		if(simple != null && simple.isVisible()) {
      simple.hide();
    }
		hideProgress();
	}

	@Override
	public void setHandler(OldDataFetchHandler handler) {
		this.handler = handler;
	}

	@Override
	public void showCompleted(boolean successful, String msg) {
		hideProgress();

		box = new MessageBox();
		box.setIcon((successful)? MessageBox.INFO : MessageBox.ERROR);  
		box.setMessage((successful)? AppController.Lang.DataFetchedSuccessfully() : AppController.Lang.ErrorFetchingData(msg));
		box.setTitle(""); 
    box.setModal(true);   
    box.setButtons(Dialog.OK);
    box.show();
	}

	@Override
	public void showProgress(String text, int count, int total) {
		hideProgress();
		box = MessageBox.progress(AppController.Lang.PleaseWait(), text + "...", "");
		final ProgressBar progress = box.getProgressBar();

		double percent = (total != 0)? count / (double)total : 0;
		if(percent > 1) {
			percent = 1;
    }
		progress.updateProgress(percent, (int)(percent * 100) + "% " + AppController.Lang.Complete().toLowerCase());  
		box.setModal(true);
		box.show();
	}

}
