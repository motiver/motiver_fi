/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.mobile;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.HeaderPresenter;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderHandler;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderTarget;
import com.delect.motiver.client.res.MyResources;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

/**
 * 
 * Init tab menu
 *  - 5 tabs (main, training, nutrition, measurements, statistics
 */
public class HeaderView extends HeaderPresenter.HeaderDisplay {

	private MessageBox box;

	private HeaderHandler handler;
	private Text linkA = new Text();
	private Text linkC = new Text();
	private Text linkCo = new Text();
	//links
	private Text linkM = new Text();
	private Text linkN = new Text();
	private Text linkP = new Text();
	private Text linkS = new Text();
	private Text linkT = new Text();
	private HeaderTarget target;

	
	public HeaderView() {		
		//layout
		HBoxLayout layout = new HBoxLayout();
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		this.setLayout(layout);
		
	}
	
	@Override
	public Widget asWidget() {

		//logo
		LayoutContainer panelLogo = new LayoutContainer();
		panelLogo.add(new Image(MyResources.INSTANCE.logoHeader()));
		panelLogo.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.onLogoClicked();
			}
		});
		panelLogo.setStyleAttribute("cursor", "pointer");
		this.add(panelLogo, new HBoxLayoutData(new Margins(0, 10, 0, 10)));
		
		//if user view
		if(target == HeaderTarget.USER) {
			
			//main
      linkM.addStyleName("header-link");
			linkM.setText("M");
			linkM.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(0);
				}
			});
			this.add(linkM, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
			//training
      linkT.addStyleName("header-link");
			linkT.setText("T");
			linkT.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(1);
				}
			});
			this.add(linkT, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
			//nutrition
      linkN.addStyleName("header-link");
			linkN.setText("N");
			linkN.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(2);
				}
			});
			this.add(linkN, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
			//cardio
      linkC.addStyleName("header-link");
			linkC.setText("C");
			linkC.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(3);
				}
			});
			this.add(linkC, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
			//statistic
      linkS.addStyleName("header-link");
			linkS.setText("S");
			linkS.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(4);
				}
			});
			this.add(linkS, new HBoxLayoutData(new Margins(5, 5, 0, 0)));
			//profile
      linkP.addStyleName("header-link");
			linkP.setText("P");
			linkP.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(5);
				}
			});
			this.add(linkP, new HBoxLayoutData(new Margins(5, 5, 0, 0)));

			//admin
			if(AppController.User.isAdmin()) {
        linkA.addStyleName("header-link");
				linkA.setText("A");
				linkA.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						handler.onTabClick(7);
					}
				});
				this.add(linkA, new HBoxLayoutData(new Margins(5, 5, 0, 0)));		
			}
			
		}

    //spacer
    HBoxLayoutData flex = new HBoxLayoutData();
    flex.setFlex(1);
    this.add(new Text(), flex);
		
		//signout
		Text linkSignout = new Text("Out");
		linkSignout.setStyleName("link");
		linkSignout.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
			  handler.logOut();
			}
		});
		this.add(linkSignout);
		
		return this;
	}

	@Override
	public void setHandler(HeaderHandler handler) {
		this.handler = handler;
	}
	@Override
	public void setTab(int tabIndex) {
		
		//remove style
		linkM.removeStyleName("header-link-sel");
		linkT.removeStyleName("header-link-sel");
		linkN.removeStyleName("header-link-sel");
		linkC.removeStyleName("header-link-sel");
		linkS.removeStyleName("header-link-sel");
		linkP.removeStyleName("header-link-sel");
		linkCo.removeStyleName("header-link-sel");
		linkA.removeStyleName("header-link-sel");
		
		if(tabIndex == 0) {
			linkM.addStyleName("header-link-sel");
    }
		else if(tabIndex == 1) {
			linkT.addStyleName("header-link-sel");
    }
		else if(tabIndex == 2) {
			linkN.addStyleName("header-link-sel");
    }
		else if(tabIndex == 3) {
			linkC.addStyleName("header-link-sel");
    }
		else if(tabIndex == 4) {
			linkS.addStyleName("header-link-sel");
    }
		else if(tabIndex == 5) {
			linkP.addStyleName("header-link-sel");
    }
		else if(tabIndex == 6) {
			linkCo.addStyleName("header-link-sel");
    }
		else if(tabIndex == 7) {
			linkA.addStyleName("header-link-sel");
    }
	}
	
	@Override
	public void setTarget(HeaderTarget target) {
		this.target = target;
	}

  @Override
  public void showLoadingDialog(boolean enabled) {
    if(box != null && box.isVisible()) {
      box.close();
    }
    
    if(enabled) {
      box = MessageBox.wait(AppController.Lang.SigningOut(), AppController.Lang.PleaseWait(), AppController.Lang.SigningOut() + "...");
      box.show(); 
    }
  }

}
