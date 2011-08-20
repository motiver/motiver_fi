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
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.NotePanelPresenter;
import com.delect.motiver.client.presenter.NotePanelPresenter.NotePanelHandler;
import com.delect.motiver.client.view.widget.MyButton;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

/**
 * Panel that wraps content and header
 * <br>Header: title (left), buttons/panel(right), help (right)
 */
public class NotePanelView extends NotePanelPresenter.NotePanelDisplay  {

	private NotePanelHandler handler;
	private LayoutContainer panelData = new LayoutContainer();

	//panels
	private LayoutContainer panelHeader = new LayoutContainer();
	//widgets
	private Text textSpacer = new Text();
	
	private Text textTitle = new Text("");
		
	public NotePanelView() {
		
		try {
			
			this.addStyleName("panel-note");
			
			//header
			HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layout);
      panelHeader.setHeight(55);
      panelHeader.setAutoWidth(true);
      panelHeader.setLayoutOnChange(true);
      panelHeader.setStyleName("panel-note-header");
      panelHeader.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
      panelHeader.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
      panelHeader.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					//if button clicked -> cancel event
					if( panelHeader.getData("btnClick") != null && ((Boolean)panelHeader.getData("btnClick")) ) {
						panelHeader.setData("btnClick", false);
						return;
					}
					if(panelData.isVisible()) {
						panelData.hide();
          }
					else {
						panelData.show();
          }
					updateHeaderToolTip();
				}
      });
      textTitle.setStyleName("label-title-big");
      panelHeader.add(textTitle, new HBoxLayoutData(new Margins(0, 0, 0, 20)));
			//spacer
			HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 10, 0, 10));
      flex.setFlex(1);  
      panelHeader.add(textSpacer, flex); 
      this.add(panelHeader);

			panelData.setLayout(new RowLayout());
			panelData.setStyleName("panel-note-data");
			this.add(panelData);
			
			//call handler when panel expands/collapses
			panelData.addListener(Events.Show, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					updateHeaderToolTip();
					handler.contentVisible();
				}
			});
			panelData.addListener(Events.Hide, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					updateHeaderToolTip();
					handler.contentHidden();
				}
			});
			
		} catch (Exception e) {
			Motiver.showException(e);
		}
	}
	
	@Override
	public void addHeaderButton(String text, Listener<BaseEvent> listener) {
		MyButton btn = new MyButton(text);
		btn.setScale(ButtonScale.MEDIUM);
		btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				panelHeader.setData("btnClick", true);
				
				//open panel
				if(!panelData.isVisible()) {
					panelData.setVisible(true);
        }
			}
		});
		btn.addListener(Events.OnClick, listener);
		panelHeader.add(btn, new HBoxLayoutData(new Margins(0, 3, 0, 7)));
	}	

	@Override
	public Widget asWidget() {

		panelData.setStyleAttribute("min-height", "250px");
		panelData.hide();
		
		return this;
	}
	
	/**
	 * Add something to content
	 * Parameters: component
	 */
	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}
	
	@Override
	public void setHandler(NotePanelHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setTitle(String title) {
		textTitle.setText(title);
		updateHeaderToolTip();
	}

	@Override
	public void showContent() {
		if(!panelData.isVisible()) {
			panelData.show();
		}
	}

	/**
	 * Sets header tooltip based on header text
	 */
	protected void updateHeaderToolTip() {

		ToolTipConfig ttc = new ToolTipConfig();
		ttc.setShowDelay(250);
		
		//if collapsed
		if(!panelData.isVisible()) {
			ttc.setText(AppController.Lang.ClickToShow(textTitle.getText().toLowerCase()));
    }
		//expanded
		else {
			ttc.setText(AppController.Lang.ClickToHide(textTitle.getText().toLowerCase()));
    }
		
		textTitle.setToolTip(ttc);
		textSpacer.setToolTip(ttc);
	}
}
