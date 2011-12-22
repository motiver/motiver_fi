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

import java.util.Date;
import java.util.Map.Entry;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.profile.ProfilePresenter;
import com.delect.motiver.client.presenter.profile.ProfilePresenter.ProfileHandler;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ProfileView extends ProfilePresenter.ProfileDisplay {


	static ComponentPlugin plugin = new ComponentPlugin() {  
    public void init(Component component) {  
      component.addListener(Events.Render, new Listener<ComponentEvent>() {  
        public void handleEvent(ComponentEvent be) {  
          El elem = be.getComponent().el().findParent(".x-form-element", 3);  
          // should style in external CSS  rather than directly  
          elem.appendChild(XDOM.create("<div style='color: #615f5f;padding: 2px 0 4px 0px;'>" + be.getComponent().getData("text") + "</div>"));  
        }  
      });  
    }  
  };

	private ProfileHandler handler;
	private LayoutContainer panelContent = new LayoutContainer();

	private LayoutContainer panelPic = new LayoutContainer();  
	private UserModel user;
  final SimpleComboBox<String> comboLocale = new SimpleComboBox<String>(); 
	final SimpleComboBox<String> comboDateformat = new SimpleComboBox<String>();    
	final SimpleComboBox<String> comboMeas = new SimpleComboBox<String>();  
	final SimpleComboBox<String> comboTimeformat = new SimpleComboBox<String>();  
  TextField<String> tfAlias = new TextField<String>(); 
	
  boolean clearInvalid = false;
  
	//listener for comboboxes
	final Listener<BaseEvent> listener = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
						
			try {
			  //get locale
			  String s = (comboLocale.getValue() != null && comboLocale.isValid())? comboLocale.getValue().getValue() : StringConstants.LOCALE_DEFAULT;
			  String locale = StringConstants.LOCALE_DEFAULT;
		    for(Entry<String, String> entry : StringConstants.LOCALES.entrySet()) {
		      if(entry.getValue().equals(s)) {
		        locale = entry.getKey();
		        break;
		      }
		    }
		    
			  int df = comboDateformat.getSelectedIndex();
				int tf = comboTimeformat.getSelectedIndex();
				int meas = comboMeas.getSelectedIndex();
				String alias = tfAlias.getValue();

				//if data has changed
				if(!locale.equals(user.getLocale())
				    || user.getDateFormat() != df
				    || user.getTimeFormat() != tf
				    || user.getMeasurementSystem() != meas
				    || (alias == null && user.getAlias() != null)
				    || (alias != null && !alias.equals(user.getAlias()))) {
				  
	        user.setLocale(locale);
	        user.setDateFormat(df);
	        user.setTimeFormat(tf);
	        user.setMeasurementSystem(meas);
	        user.setAlias(alias);
	        
	        handler.saveData(user);
				}
				
			} catch (Exception e) {
	      Motiver.showException(e);
			}
			
		}
	};
	
	public ProfileView() {		
		this.add(panelContent);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Widget asWidget() {

		panelContent.removeAll();
		panelPic.removeAll();
		
		LayoutContainer panelProfile = new LayoutContainer();
		HBoxLayout l = new HBoxLayout();  
    l.setPadding(new Padding(5));  
    l.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
    panelProfile.setLayout(l); 
		
		final FormData formData = new FormData("-10");
		
		//profile pic
		HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
    flex.setFlex(3);
    panelPic.setWidth(200);
    panelPic.setHeight(200);
    LayoutContainer lc = new LayoutContainer();
		HTML htmlPic = new HTML();
		htmlPic.setHTML("<fb:profile-pic uid=\"" + AppController.User.getUid() + "\" facebook-logo=true size=normal linked=false></fb:profile-pic>");
    lc.add(htmlPic);
    panelPic.add(lc);
		panelProfile.add(panelPic, flex);
		
		//form
		final FormPanel form = new FormPanel();
		form.setHeaderVisible(false);
		form.setFrame(true);  
		form.setWidth(525);  
		form.setLayout(new RowLayout()); 
		
		//fieldset 1 (name, settings, etc...)
		FieldSet fieldSet = new FieldSet();  
    fieldSet.setHeading(AppController.Lang.UserInformation());
    FormLayout layout = new FormLayout();  
    layout.setLabelWidth(200);
    fieldSet.setLayout(layout);
    
    //locale
    comboLocale.setFieldLabel(AppController.Lang.Language()); 
    comboLocale.setForceSelection(true);
    comboLocale.setEditable(false);
    comboLocale.setTriggerAction(TriggerAction.ALL);
    int i = 0;
    int sel = 0;
    for(Entry<String, String> entry : StringConstants.LOCALES.entrySet()) {
      comboLocale.add(entry.getValue());
      
      if(entry.getKey().equals(user.getLocale())) {
        sel = i;
      }
      i++;
    }
    comboLocale.addPlugin(plugin);
    comboLocale.setData("text", AppController.Lang.LanguageDesc());
    fieldSet.add(comboLocale, formData);
    comboLocale.setValue( comboLocale.getStore().getAt( sel ));
		
		//dateformat
		comboDateformat.setFieldLabel(AppController.Lang.DateFormat()); 
		comboDateformat.setForceSelection(true);
		comboDateformat.setEditable(false);
		comboDateformat.setTriggerAction(TriggerAction.ALL);
		Date d = new Date();
		d.setDate(20);
		d.setMonth(5);
		d.setYear(110);
		d.setHours(19);
		d.setMinutes(30);
		comboDateformat.add(DateTimeFormat.getFormat(StringConstants.DATEFORMATS[0]).format(d));
		comboDateformat.add(DateTimeFormat.getFormat(StringConstants.DATEFORMATS[1]).format(d));
		fieldSet.add(comboDateformat, formData);
		comboDateformat.setValue( comboDateformat.getStore().getAt( user.getDateFormat() ));

		//timeformat
		comboTimeformat.setFieldLabel(AppController.Lang.TimeFormat()); 
		comboTimeformat.setForceSelection(true);
		comboTimeformat.setEditable(false);
		comboTimeformat.setTriggerAction(TriggerAction.ALL);
		comboTimeformat.add(DateTimeFormat.getFormat(StringConstants.TIMEFORMATS[0]).format(d));
		comboTimeformat.add(DateTimeFormat.getFormat(StringConstants.TIMEFORMATS[1]).format(d));
		fieldSet.add(comboTimeformat, formData);
		comboTimeformat.setValue( comboTimeformat.getStore().getAt( user.getTimeFormat() ));

		//units
		comboMeas.setFieldLabel(AppController.Lang.Units()); 
		comboMeas.setForceSelection(true);
		comboMeas.setEditable(false);
		comboMeas.setTriggerAction(TriggerAction.ALL);
		comboMeas.add(AppController.Lang.UnitMetric());
		comboMeas.add(AppController.Lang.UnitUS());
		fieldSet.add(comboMeas, formData);
		comboMeas.setValue( comboMeas.getStore().getAt( user.getMeasurementSystem() ));
		
		//domain alias
		tfAlias.setFieldLabel(AppController.Lang.CustomAlias());
		tfAlias.setMaxLength(30);
		tfAlias.setMinLength(4);
		tfAlias.setAllowBlank(true);
		tfAlias.setRegex("[a-zA-Z]*");
		Functions.setWarningMessages(tfAlias);
		tfAlias.getMessages().setRegexText(AppController.Lang.OnlyLettersAllowed());
		if(user.getAlias() != null) {
		  tfAlias.setValue(user.getAlias());
		}
		tfAlias.addPlugin(plugin);
		tfAlias.setData("text", AppController.Lang.CustomAliasDesc()); 
    fieldSet.add(tfAlias, formData);
    
		form.add(fieldSet);  
				
		//listeners
		comboDateformat.addListener(Events.Change, listener);
		comboTimeformat.addListener(Events.Change, listener);
		comboMeas.addListener(Events.Change, listener);
		comboLocale.addListener(Events.Change, listener);
		tfAlias.addListener(Events.Valid, listener);
    tfAlias.addListener(Events.KeyPress, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        if(clearInvalid) {
          tfAlias.clearInvalid();
          clearInvalid = false;
        }
      }
    });

		HBoxLayoutData flex2 = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
    flex2.setFlex(7);  
		panelProfile.add(form, flex2);
		
		panelContent.add(panelProfile);
		
		return this;
	}

	@Override
	public void setHandler(ProfileHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setUserModel(UserModel user) {
		this.user = user;
	}

  @Override
  public void showAliasTaken(boolean taken) {
    tfAlias.disableEvents(true);
    if(taken) {
      tfAlias.forceInvalid(AppController.Lang.AliasTaken());
      clearInvalid = true;
    }
    else {
      tfAlias.clearInvalid();
      clearInvalid = false;
    }
    tfAlias.enableEvents(true);
  }

}
