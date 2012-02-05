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
package com.delect.motiver.client.view.widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

public class NameInputWidget extends LayoutContainer {

	public interface EnterNamePanelHandler {
		/**
		 * Returns entered name
		 * @param name : null if cancelled
		 */
		void newName(String name);
	}
	
  TextField<String> textName = new TextField<String>();
	private MyButton btnAdd = new MyButton();

  private EnterNamePanelHandler handler = null;
  private boolean isCancelVisible = true;
  private boolean isAutoHeight = false;
  private boolean isLeftAlignment = false;
  private String desc = null;
	
	public NameInputWidget(EnterNamePanelHandler h) {
    this.handler = h;
    
    //key listeners
    new KeyNav<ComponentEvent>(this) {
      @Override
      public void onEnter(ComponentEvent event) {
        handler.newName(textName.getValue());
      }
      @Override
      public void onEsc(ComponentEvent event) {
        if(isCancelVisible) {
          handler.newName(null);
        }
      }
    };
		
	  this.addListener(Events.Render, new Listener<BaseEvent>() {

      @Override
      public void handleEvent(BaseEvent be) {
        if(isAutoHeight) {
          setHeight("auto");
        }
        else {
          setHeight(250);
        }
        HBoxLayout layout = new HBoxLayout();  
        layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
        layout.setPack( (!isLeftAlignment)? BoxLayoutPack.CENTER : BoxLayoutPack.START );  
        setLayout(layout);
        
        if(desc != null) {
          Text tDesc = new Text(desc);
          add(tDesc, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
        }
            
        //textview for name
        textName.setMinLength(Constants.LIMIT_NAME_MIN);
        textName.setMaxLength(Constants.LIMIT_NAME_MAX);
        textName.setAllowBlank(false);
        textName.setAutoValidate(true);
        textName.setValidateOnBlur(false);
        Functions.setWarningMessages(textName);
        textName.addListener(Events.Valid, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            btnAdd.setEnabled(true);
          }
        });
        textName.addListener(Events.Invalid, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            btnAdd.setEnabled(false);
          }
        });
        textName.setEmptyText(AppController.Lang.EnterName());
        add(textName, new HBoxLayoutData(new Margins(0, 20, 0, 0)));
        
        //add button
        btnAdd.setText(AppController.Lang.Create());
        btnAdd.setEnabled(false);
        btnAdd.setId("ni-btn-add");
        btnAdd.setScale(ButtonScale.MEDIUM);
        btnAdd.addListener(Events.OnClick, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            handler.newName(textName.getValue());
            
            //clear value
            textName.clear();
          }
        });
        add(btnAdd, new HBoxLayoutData(new Margins(0, 20, 0, 0)));

        //cancel button
        if(isCancelVisible) {
          MyButton btnCancel = new MyButton();
          btnCancel.setText(AppController.Lang.Cancel());
          btnCancel.setScale(ButtonScale.MEDIUM);
          btnCancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
              handler.newName(null);
            }
          });
          add(btnCancel, new HBoxLayoutData(new Margins(0, 0, 0, 0)));
        }

      }
	    
	  });
	}
	
	public void setDescription(String desc) {
	  this.desc  = desc;
	}
  
  public void setHandler(EnterNamePanelHandler handler) {
    this.handler = handler;
  }
  
  public void setCancelButtonVisiblity(boolean isCancelVisible) {
    this.isCancelVisible = isCancelVisible;
  }
  
  public void setAutoHeight(boolean isAutoHeight) {
    this.isAutoHeight  = isAutoHeight;
  }
  
  public void setLeftAligmend(boolean isLeftAlignment) {
    this.isLeftAlignment   = isLeftAlignment;
  }
}
