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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout.BoxLayoutPack;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

/**
 * Base panel for workouts/meals/cardios/...
 * @author Antti
 *
 */
public class SmallNotePanel extends SmallNotePanelDisplay {
	
	public LayoutContainer panelButtons = new LayoutContainer();
	public LayoutContainer panelData = new LayoutContainer();
	//main panels
	public LayoutContainer panelHeader = new LayoutContainer();

	public Text textSpacer = new Text();
	public Text textTitle = new Text();
	
	private boolean isCollapsible = true;
	
	private Listener<BaseEvent> listenerDataVisible = new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			if(panelData.isVisible()) {
				removeStyleName("collapsed");
      }
			else {
				addStyleName("collapsed");
      }
		}
  };
	private Timer timerOut;
	
	public SmallNotePanel() {
		
		try {
			addStyleName("collapsed");
			
			//header
			HBoxLayout layoutHeader = new HBoxLayout();
      layoutHeader.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelHeader.setLayout(layoutHeader);
      panelHeader.setHeight(40);
      panelHeader.setAutoWidth(true);
      panelHeader.setStyleName("panel-cardio-header");
      panelHeader.setStyleAttribute("cursor", "pointer");
      panelHeader.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if(getData("btnClick") != null || !isCollapsible) {
						setData("btnClick", null);
						return;
					}
					if(panelData.isVisible()) {
						panelData.hide();
          }
					else {
						panelData.show();
          }
					panelButtons.setVisible(panelData.isVisible());
					panelHeader.layout(true);
					
					if(panelData.isVisible()) {
						removeStyleName("collapsed");
          }
					else {
						addStyleName("collapsed");
          }
				}
      });
			
			//title
			textTitle = new Text();
			textTitle.setStyleName("label-title-medium");
      panelHeader.add(textTitle);

			//spacer
			HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 10, 0, 10));
      flex.setFlex(1);  
      panelHeader.add(textSpacer, flex); 

			//buttons layout
			HBoxLayout layoutButtons = new HBoxLayout();
			layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
			layoutButtons.setPack(BoxLayoutPack.END);
      panelButtons.setLayout(layoutButtons);
      panelButtons.setHeight(30);
      panelButtons.setWidth(300);

      panelButtons.setVisible(false);
			panelHeader.add(panelButtons);
      this.add(panelHeader);

      //content
			panelData.setLayout(new RowLayout());
      panelData.addListener(Events.Show, listenerDataVisible );
      panelData.addListener(Events.Hide, listenerDataVisible );
			this.add(panelData);
			
			//show hide header buttons based on mouse position
			this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if(panelData.isVisible()) {
						//cancel timer
						if(timerOut != null) {
							timerOut.cancel();
							timerOut = null;
						}
						panelButtons.setVisible(true);
						panelHeader.layout(true);
					}
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
								if(panelButtons != null) {
									panelButtons.setVisible(false);
	              }
								if(panelHeader != null) {
									panelHeader.layout(true);
	              }
							}
						};
						timerOut.schedule(Constants.DELAY_HIDE_ICONS);
					}
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	@Override
  public void addHeaderButton(String text, Listener<BaseEvent> listener) {
		
		MyButton btn = new MyButton(text);
		btn.setColor(MyButton.Style.GREEN);
		btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				setData("btnClick", true);
			}
		});
		btn.addListener(Events.OnClick, listener);
		panelButtons.add(btn, new HBoxLayoutData(new Margins(0, 5, 0, 10)));
	}
	
	@Override
  public ImageButton addHeaderImageButton(String text, ImageResource image, Listener<BaseEvent> listener) {

		ImageButton btn = new ImageButton(text, image);
		btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				setData("btnClick", true);
			}
		});
		if(listener != null) {
			btn.addListener(Events.OnClick, listener);
    }
		panelButtons.add(btn, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
		
		return btn;
	}
	
	@Override
	public LayoutContainer getPanelData() {
		return panelData;
	}
  
  @Override
  public LayoutContainer getPanelButtons() {
    return panelButtons;
  }
	
	/**
	 * Sets if panel can be collapsed
	 * @param b
	 */
	@Override
  public void setCollapsible(boolean isCollapsible) {
		this.isCollapsible  = isCollapsible;
		
		//header's cursor
		panelHeader.setStyleAttribute("cursor", (isCollapsible)? "pointer" : "default");
	}

	@Override
  public void setStylePrefix(String style) {
		this.addStyleName(style);
		panelHeader.setStyleName(style + "-header");
		panelData.setStyleName(style + "-data");
	}

	@Override
	public void setTitleText(String title) {
		textTitle.setText(title);
	}

	@Override
  public void showContent() {
		panelData.show();
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
