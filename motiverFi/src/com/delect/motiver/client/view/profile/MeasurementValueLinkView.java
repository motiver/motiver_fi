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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.profile.MeasurementValueLinkPresenter;
import com.delect.motiver.client.presenter.profile.MeasurementValueLinkPresenter.MeasurementValueLinkHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.CustomListener;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;
import com.delect.motiver.shared.MeasurementValueModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;


public class MeasurementValueLinkView extends MeasurementValueLinkPresenter.MeasurementValueLinkDisplay {

	private MeasurementValueLinkHandler handler;
	private MeasurementValueModel measurementValue;
	private LayoutContainer panelButtons = new LayoutContainer();

	protected Timer timerOut;
	MessageBox box = null;

	LayoutContainer panelBody = new LayoutContainer();
	LayoutContainer panelHeader = new LayoutContainer();
	
	public MeasurementValueLinkView() {
		
		this.setLayout(new RowLayout());
		this.setStyleName("panel-link");
		
		//header
		panelHeader.setStyleName("panel-link-data");
		panelHeader.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
		panelHeader.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
		panelHeader.sinkEvents(Event.MOUSEEVENTS);
		
		panelHeader.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(getData("btnClick") != null) {
					setData("btnClick", null);
					return;
				}
				handler.selected();
			}
		});

		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    panelHeader.setLayout(layout);
    this.add(panelHeader, new RowData(-1, -1, new Margins(0)));
        
    panelBody.setAutoHeight(true);
    panelBody.setAutoWidth(true);
    this.add(panelBody, new RowData(-1, -1, new Margins(0)));
	}
	
	@Override
	public Widget asWidget() {
		
		try {
			
			//show hide header buttons based on mouse position
      if(measurementValue.getUid().equals(AppController.User.getUid())) {
        this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            //cancel timer
            if(timerOut != null) {
              timerOut.cancel();
              timerOut = null;
            }
	    				
            panelButtons.setVisible(true);
            panelHeader.layout(true);
          }
        });
        this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
          @Override
          public void handleEvent(BaseEvent be) {
            //cancel timer
            if(timerOut == null) {
              timerOut = new Timer() {
                @Override
                public void run() {
                  panelButtons.setVisible(false);
                  panelHeader.layout(true);
                }
              };
              timerOut.schedule(Constants.DELAY_HIDE_ICONS);
            }
          }
        });
      }
	        
			//icon
			Image icon = new Image(MyResources.INSTANCE.iconMeasurement());
			panelHeader.add(icon, new HBoxLayoutData(new Margins(0, 20, 0, 0)));			
					
			//name
			if(measurementValue.getName() != null) {
				Text textName = new Text(measurementValue.getName().getNameClient() + ": " + measurementValue.getValue() + " " + measurementValue.getName().getUnit());
				panelHeader.add(textName, new HBoxLayoutData(new Margins(0)));
			}

      if(measurementValue.getUid().equals(AppController.User.getUid())) {
	        	
				//spacer
				HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, 0, 5));
        flex.setFlex(1);  
        panelHeader.add(new Text(), flex); 

				//buttons layout
				HBoxLayout layoutButtons = new HBoxLayout();
				layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
				layoutButtons.setPack(BoxLayoutPack.END);
        panelButtons.setLayout(layoutButtons);
        panelButtons.setHeight(16);
        panelButtons.setWidth(50);
		        
        //remove exercise link
				ImageButton btnRemove = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.ThisValue().toLowerCase()), MyResources.INSTANCE.iconRemove());
				btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						setData("btnClick", true);
						//ask for confirm
						box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.ThisValue().toLowerCase()), new MessageBoxHandler() {
							@Override
							public void okPressed(String text) {
								handler.valueRemoved();
							}
						});
						box.show();
					}
				});
				panelButtons.add(btnRemove);
				
				panelHeader.add(panelButtons, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
				panelButtons.setVisible(false);
      }

		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return panelBody;
	}

	@Override
	public void setHandler(MeasurementValueLinkHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(MeasurementValueModel measurementValue) {
		this.measurementValue = measurementValue;
	}
	
}
