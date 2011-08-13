/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.training;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.training.ExerciseNameEditorPresenter;
import com.delect.motiver.client.presenter.training.ExerciseNameEditorPresenter.ExerciseNameEditorHandler;
import com.delect.motiver.shared.ExerciseNameModel;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
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
 * "Exercise name editor" form with correct textfields
 *
 */
public class ExerciseNameEditorView extends ExerciseNameEditorPresenter.ExerciseNameEditorDisplay {

	
	private ExerciseNameEditorHandler handler;
	private ExerciseNameModel model;

	public ExerciseNameEditorView() {
		this.setStyleAttribute("margin-top", "5px");
		this.setLayout(new FitLayout());
		this.setHeight(175);
		this.setWidth(305);
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
			tfName.setMaxLength(40); 
			tfName.setAllowBlank(false);
			tfName.setMessageTarget("none");
			tfName.setAutoValidate(true);
			form.add(tfName, formData); 
			
			//target
			final SimpleComboBox<String> comboTarget = new SimpleComboBox<String>();  
			comboTarget.setFieldLabel(AppController.Lang.Equipment());
			for(String str : AppController.LangConstants.Targets())
      comboTarget.add(str);
			comboTarget.setForceSelection(true);
			comboTarget.setEditable(false);
      comboTarget.setTriggerAction(TriggerAction.ALL);
			comboTarget.setSimpleValue(AppController.LangConstants.Targets()[model.getTarget()]);
			form.add(comboTarget, formData);

			//video url
			final TextField<String> tfVideo = new TextField<String>();  
			tfVideo.setFieldLabel(AppController.Lang.VideoURL()); 
			tfVideo.setValue(model.getVideo());
			tfVideo.setMinLength(5);
			tfVideo.setMaxLength(100); 
			tfVideo.setAllowBlank(true);
			tfVideo.setMessageTarget("none");
			tfVideo.setAutoValidate(true);
			tfVideo.setRegex("^((https?|ftp)://|(www|ftp).)[a-z0-9-]+(.[a-z0-9-]+)+([/?].*)?$");
			form.add(tfVideo, formData); 
			
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
						int target = comboTarget.getSelectedIndex();
						
						model.setName(tfName.getValue());
						model.setTarget(target);
						model.setVideo(tfVideo.getValue());
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
	public ExerciseNameModel getModel() {
		return model;
	}

	@Override
	public void setHandler(ExerciseNameEditorHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(ExerciseNameModel model) {
		this.model = model;
	}

}
