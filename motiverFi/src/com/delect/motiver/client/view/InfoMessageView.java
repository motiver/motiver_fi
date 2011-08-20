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
import com.delect.motiver.client.presenter.InfoMessagePresenter;
import com.delect.motiver.client.presenter.InfoMessagePresenter.InfoMessageHandler;
import com.delect.motiver.client.presenter.InfoMessagePresenter.MessageColor;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.widget.ImageButton;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * Shows single user
 */
public class InfoMessageView extends InfoMessagePresenter.InfoMessageDisplay {

	private MessageColor color;
	private InfoMessageHandler handler;
	
	private String message = "";

	public InfoMessageView() {
		TableLayout layout = new TableLayout(2);
		layout.setWidth("100%");
		layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		layout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		layout.setCellPadding(5);
		this.setLayout(layout);
	}
	
	@Override
	public Widget asWidget() {
		
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(getData("btnClick") != null) {
					setData("btnClick", null);
					return;
				}
				handler.onClick();
			}
		});
		
		//style
		setStyleName("panel-infomessage");
		if(color == MessageColor.COLOR_RED) {
			addStyleName("panel-infomessage-red");
    }
		else if(color == MessageColor.COLOR_BLUE) {
			addStyleName("panel-infomessage-blue");
    }
        
		this.add(new Text(message)); 
        
    //close button
    ImageButton btnClose = new ImageButton(AppController.Lang.Close(), MyResources.INSTANCE.iconRemove());
    btnClose.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				setData("btnClick", true);
				handler.onClose();
			}
    });
    TableData td = new TableData();
    td.setHorizontalAlign(HorizontalAlignment.RIGHT);
    this.add(btnClose, td);
		
		return this;
	}

	@Override
	public void setColor(MessageColor color) {
		this.color = color;
	}

	@Override
	public void setHandler(InfoMessageHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	
}
