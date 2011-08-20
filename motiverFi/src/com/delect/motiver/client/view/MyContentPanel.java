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

import com.delect.motiver.client.AppController;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class MyContentPanel extends ContentPanel {

	private LayoutContainer panelInfo = null;
		
	public MyContentPanel() {
    head = new MyHeader();

    setHideCollapseTool(true);
    setTitleCollapse(true);

		//set header's tooltip when collapsing/expanding
    this.addListener(Events.Expand, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        updateHeaderToolTip();
      }
    });
    this.addListener(Events.Collapse, new Listener<BaseEvent>() {
  		@Override
  		public void handleEvent(BaseEvent be) {
  			updateHeaderToolTip();
  		}
    });
    //show/hide header button when mouse over
    this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
  		@Override
  		public void handleEvent(BaseEvent be) {
  			for(int i=0; i < head.getTools().size(); i++) {
          getHeader().getTools().get(i).setVisible(true);
  			}
  		}
    });
    this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
  		@Override
  		public void handleEvent(BaseEvent be) {
  			for(int i=0; i < head.getTools().size(); i++) {
          getHeader().getTools().get(i).setVisible(false);
  			}
  		}
    });
	}
	
	/**
	 * Adds "info" button. Buttons are shown on top of panel's body
	 * @param text
	 * @param handler
	 */
	public void addInfoButton(String text, Listener<BaseEvent> listener) {
		
		//init panel
		if(panelInfo == null) {
			panelInfo = new LayoutContainer();
			final HBoxLayout layout = new HBoxLayout(); 
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelInfo.setLayout(layout);
      panelInfo.setHeight(32);
      panelInfo.setStyleName("panel-info");
			this.insert(panelInfo, 0, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
			this.layout();
		}
		
		final Button btnCopy = new Button();
		btnCopy.setScale(ButtonScale.MEDIUM);
		btnCopy.setText(text);
		btnCopy.addListener(Events.OnClick, listener);
		panelInfo.add(btnCopy, new HBoxLayoutData(new Margins(0, 10, 0, 0))); 

		panelInfo.layout();
	}

	/**
	 * Clears all widgets from info panel
	 */
	public void clearInfoPanel() {
		if(panelInfo != null) {
			panelInfo.removeAll();
			panelInfo.layout();
		}
	}

	@Override
	public void setHeading(String text) {
		super.setHeading(text);
		
		//reset tooltip because header changed
		updateHeaderToolTip();
	}

	public void setInfoStyle(String style) {

		//init panel
		if(panelInfo == null) {
			panelInfo = new LayoutContainer();
			final HBoxLayout layout = new HBoxLayout(); 
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelInfo.setLayout(layout);
      panelInfo.setHeight(32);
      panelInfo.setStyleName("panel-info");
			this.insert(panelInfo, 0, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
			this.layout();
		}
		panelInfo.addStyleName(style);
	}
	
	/**
	 * Adds "info" button. Buttons are shown on top of panel
	 * @param text
	 */
	@Override
  public void setTitle(String text) {

		//init panel
		if(panelInfo == null) {
			panelInfo = new LayoutContainer();
			final HBoxLayout layout = new HBoxLayout(); 
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelInfo.setLayout(layout);
      panelInfo.setHeight(32);
      panelInfo.setStyleName("panel-info");
			this.insert(panelInfo, 0, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
			this.layout();
		}
		
		final Text textInfo = new Text(text);
		panelInfo.add(textInfo, new HBoxLayoutData(new Margins(0, 10, 0, 0))); 

		panelInfo.layout();
	}
	
	/**
	 * Sets header tooltip based on header text
	 */
	protected void updateHeaderToolTip() {
		if(getHeader() != null && getCollapsible()) {
			//if collapsed
			if(this.isCollapsed()) {
				if(getHeader().getText() != null) {
				  getHeader().setToolTip(AppController.Lang.ClickToShow(getHeader().getText().toLowerCase()));
				}
				else {
				  getHeader().setToolTip(AppController.Lang.ClickToShow(""));
				}
			}
			//expanded
			else {
				if(getHeader().getText() != null) {
				  getHeader().setToolTip(AppController.Lang.ClickToHide(getHeader().getText().toLowerCase()));
				}
				else {
				  getHeader().setToolTip(AppController.Lang.ClickToHide(""));
			  }
			}
		}
	}
}
