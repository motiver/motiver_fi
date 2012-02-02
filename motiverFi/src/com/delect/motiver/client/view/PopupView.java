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

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.PopupPresenter;
import com.delect.motiver.client.presenter.PopupPresenter.PopupHandler;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * Shows single user
 */
public class PopupView extends PopupPresenter.PopupDisplay {

  final Window window = new Window();
  LayoutContainer body = new LayoutContainer();
	private PopupHandler handler;

	public PopupView() {
	}
	
	@Override
	public Widget asWidget() {
	  window.setMaximizable(false);
	  window.setConstrain(false);
	  window.setSize(700, 500);
	  window.setMinWidth(400);
	  window.setMinHeight(300);
	  window.setScrollMode(Scroll.AUTO);
	  window.add(body);
	  window.addListener(Events.Hide, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        if(handler != null)
          handler.onClose();
      }
	  });
    window.addListener(Events.Resize, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        if(handler != null)
          handler.onResize();
      }
    });
	  window.show();
	  body.setBorders(true);
	  body.layout();
		return this;
	}

	@Override
	public void setHandler(PopupHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public LayoutContainer getBaseContainer() {
	  return body;
	}

  @Override
  public void onStop() {
    if(window != null && window.isVisible()) {
      window.hide();
    }
  }
	
}
