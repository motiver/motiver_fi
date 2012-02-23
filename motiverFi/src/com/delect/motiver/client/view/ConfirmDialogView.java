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
package com.delect.motiver.client.view;

import com.delect.motiver.client.presenter.ConfirmDialogPresenter;
import com.delect.motiver.client.presenter.ConfirmDialogPresenter.ConfirmDialogHandler;
import com.delect.motiver.client.presenter.ConfirmDialogPresenter.DialogType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shows single user
 */
public class ConfirmDialogView extends ConfirmDialogPresenter.ConfirmDialogDisplay {

  private MessageBox box = null;
	private ConfirmDialogHandler handler;
	
	private String message = "";
  private DialogType type = DialogType.CONFIRM;
  private String title;

	public ConfirmDialogView() {
		TableLayout layout = new TableLayout(2);
		layout.setWidth("100%");
		layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		layout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		layout.setCellPadding(5);
		this.setLayout(layout);
	}
	
	@Override
	public Widget asWidget() {
	  
	  //confirm dialog
	  if(type == DialogType.CONFIRM) {
	    box = MessageBox.confirm(title, message, new Listener<MessageBoxEvent>() {   
	      public void handleEvent(MessageBoxEvent be) {
	        Button btn = be.getButtonClicked();
	        if(Dialog.YES.equals(btn.getItemId())) {
	          handler.onYes();
	        }
	        else {
	          handler.onNo();
	        }
	      }
	    });
	  }
	  else if(type == DialogType.ALERT) {
	    box = MessageBox.alert(title, message, null);
	  }
		
		return this;
	}

	@Override
	public void setHandler(ConfirmDialogHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMessage(String title, String message) {
    this.title = title;
		this.message = message;
	}

  @Override
  public void onStop() {
    if(box != null && box.isVisible()) {
      box.close();
    }
  }

  @Override
  public void setDialogType(DialogType type) {
    this.type  = type;
  }
	
}
