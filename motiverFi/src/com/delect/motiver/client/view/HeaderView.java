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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.HeaderPresenter;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderHandler;
import com.delect.motiver.client.presenter.HeaderPresenter.HeaderTarget;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.util.CommonUtils;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Document;
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
	
	private Listener<ComponentEvent> listenerShortcut = new Listener<ComponentEvent>() {

		@Override
		public void handleEvent(ComponentEvent ce) {
			try {
				//if valid key comco
				if(CommonUtils.isValidKeyCombo(ce)) {
					
					//if enough time elapsed
					if(System.currentTimeMillis() - timeLastKeyEvent < Constants.DELAY_KEY_EVENTS) {
						return;
          }
					
					switch(ce.getKeyCode()) {
          //shift + Q
						case 81:
		    				timeLastKeyEvent = System.currentTimeMillis();
                handler.logOut();
                break;
					}
				}
			} catch (Exception e) {
        Motiver.showException(e);
			}
		}
	};
	private HeaderTarget target;

	private long timeLastKeyEvent = 0;
	
	public HeaderView() {
		
		this.setWidth(975);
		this.setHeight(41);
		this.setStyleAttribute("margin", "0 auto 0 auto");
		
		//layout
		HBoxLayout layout = new HBoxLayout();
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		this.setLayout(layout);
		
		//listener for shift + key
		Document.get().addListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcut );
		
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
		this.add(panelLogo, new HBoxLayoutData(new Margins(0, 17, 0, 10)));
		
		//if user view
		if(target == HeaderTarget.USER) {
			
			//main
      linkM.addStyleName("header-link");
      linkM.setId("header-link-1");
			linkM.setText(AppController.Lang.Main());
			linkM.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(0);
				}
			});
			this.add(linkM, new HBoxLayoutData(new Margins(5, 3, 0, 0)));
			//training
      linkT.addStyleName("header-link");
      linkT.setId("header-link-2");
			linkT.setText(AppController.Lang.Training());
			linkT.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(1);
				}
			});
			this.add(linkT, new HBoxLayoutData(new Margins(5, 3, 0, 0)));
			//nutrition
      linkN.addStyleName("header-link");
      linkN.setId("header-link-3");
			linkN.setText(AppController.Lang.Nutrition());
			linkN.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(2);
				}
			});
			this.add(linkN, new HBoxLayoutData(new Margins(5, 3, 0, 0)));
			//cardio
      linkC.addStyleName("header-link");
      linkC.setId("header-link-4");
			linkC.setText(AppController.Lang.Cardio());
			linkC.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(3);
				}
			});
			this.add(linkC, new HBoxLayoutData(new Margins(5, 3, 0, 0)));
			//statistic
      linkS.addStyleName("header-link");
      linkS.setId("header-link-5");
			linkS.setText(AppController.Lang.Statistics());
			linkS.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(4);
				}
			});
			this.add(linkS, new HBoxLayoutData(new Margins(5, 3, 0, 0)));
			//profile
      linkP.addStyleName("header-link");
      linkP.setId("header-link-6");
			linkP.setText(AppController.Lang.Profile());
			linkP.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onTabClick(5);
				}
			});
			this.add(linkP, new HBoxLayoutData(new Margins(5, 3, 0, 0)));

			//coach
			if(AppController.User.isCoach()) {
				linkCo.addStyleName("header-link");
				linkCo.setId("header-link-7");
				linkCo.setText(AppController.Lang.Coach());
				linkCo.addListener(Events.OnClick, new Listener<BaseEvent>() {
					@Override
					public void handleEvent(BaseEvent be) {
						handler.onTabClick(6);
					}
				});
				this.add(linkCo, new HBoxLayoutData(new Margins(5, 3, 0, 0)));			
			}

//			//admin
//			if(AppController.User.isAdmin()) {
//        linkA.addStyleName("header-link");
//				linkA.setText(AppController.Lang.Admin());
//				linkA.addListener(Events.OnClick, new Listener<BaseEvent>() {
//					@Override
//					public void handleEvent(BaseEvent be) {
//						handler.onTabClick(7);
//					}
//				});
//				this.add(linkA, new HBoxLayoutData(new Margins(5, 3, 0, 0)));		
//			}
			
		}

		//spacer
		HBoxLayoutData flex = new HBoxLayoutData();
		flex.setFlex(1);
		this.add(new Text(), flex);
        
		//name
		LayoutContainer panelUser = new LayoutContainer();
		panelUser.setHeight(16);
		HTML htmlName = new HTML();
		htmlName.setHTML(AppController.User.getNickName());
		panelUser.add(htmlName);
		this.add(panelUser, new HBoxLayoutData(new Margins(0, 20, 0, 0)));

		//blog
		Text linkViewBlog = new Text(AppController.Lang.ViewYourBlog());
		linkViewBlog.setStyleName("link");
		linkViewBlog.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.viewBlog();
			}
		});
		this.add(linkViewBlog, new HBoxLayoutData(new Margins(0, 20, 0, 0)));
		
		//signout
		Text linkSignout = new Text(AppController.Lang.SignOut());
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
