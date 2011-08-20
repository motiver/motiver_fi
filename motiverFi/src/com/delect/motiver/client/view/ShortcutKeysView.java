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
import com.delect.motiver.client.presenter.ShortcutKeysPresenter;
import com.delect.motiver.client.presenter.ShortcutKeysPresenter.ShortcutKeysHandler;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Document;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * Shows single user
 */
public class ShortcutKeysView extends ShortcutKeysPresenter.ShortcutKeysDisplay {


	private ShortcutKeysHandler handler;
	private long timeLastKeyEvent = 0;

	private Popup window;
	Listener<ComponentEvent> listenerShortcut = new Listener<ComponentEvent>() {
		@Override
		public void handleEvent(ComponentEvent ce) {
			
			try {
				//if valid key comco
				if(Functions.isValidKeyCombo(ce)) {
				
					//if enough time elapsed
					if(System.currentTimeMillis() - timeLastKeyEvent < Constants.DELAY_KEY_EVENTS) {
						return;
          }
					
					handler.onShortcutKey(ce.getKeyCode());
				}

			} catch (Exception e) {
        Motiver.showException(e);
			}
		}
	};
	
	public ShortcutKeysView() {

		//listener for shift + key
		Document.get().addListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcut );
	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public void onStop() {
		if(window != null) {
			window.hide();
    }
		
		//remove key listeners
		Document.get().removeListener(Constants.EVENT_TYPE_GLOBAL_HOTKEYS, listenerShortcut);
	}

	@Override
	public void setHandler(ShortcutKeysHandler handler) {
		this.handler = handler;
	}

	/**
	 * Shows help window
	 */
	@Override
	public void showHelpWindow() {
		if(window != null) {
			if(window.isVisible()) {
				window.hide();
				return;
			}
		}
		
		window = new Popup();
		window.setAutoHide(true);
		window.setStyleName("window-shortcuts");
		window.addListener(Events.Hide, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent arg0) {
				handler.windowHidden();
			}
		});
		
		TableLayout tl = new TableLayout(2);
		tl.setCellSpacing(5);
		window.setLayout(tl);
		
		TableData td = new TableData();
		td.setColspan(2);
		
		Text t0 = new Text(AppController.Lang.ShortcutKeys());
		t0.setStyleName("label-title-big");
		window.add(t0, td);
		
		//GLOBAL
		Text t1 = new Text(AppController.Lang.Global());
		t1.setStyleName("label-title-medium");
		window.add(t1, td);
		//your blog
		Text t1a = new Text("Shift+B :");
		t1a.setStyleName("label-shortcut");
		window.add(t1a);
		window.add(new Text(AppController.Lang.YourBlog()));
		//sign out
		Text t1b = new Text("Shift+Q :");
		t1b.setStyleName("label-shortcut");
		window.add(t1b);
		window.add(new Text(AppController.Lang.SignOut()));
		//help
		Text t1c = new Text("Shift+H :");
		t1c.setStyleName("label-shortcut");
		window.add(t1c);
		window.add(new Text(AppController.Lang.Help()));
		
		//TRAINING
		Text t2 = new Text(AppController.Lang.Training());
		t2.setStyleName("label-title-medium");
		window.add(t2, td);
		//new workout
		Text t2a = new Text("Shift+W :");
		t2a.setStyleName("label-shortcut");
		window.add(t2a);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Workout().toLowerCase())));
		//new routine
		Text t2b = new Text("Shift+R :");
		t2b.setStyleName("label-shortcut");
		window.add(t2b);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Routine().toLowerCase())));
		//new cardio
		Text t2c = new Text("Shift+C :");
		t2c.setStyleName("label-shortcut");
		window.add(t2c);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Cardio().toLowerCase())));
		//new run
		Text t2d = new Text("Shift+V :");
		t2d.setStyleName("label-shortcut");
		window.add(t2d);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Run().toLowerCase())));
		//new exercise
		Text t2e = new Text("Shift+E :");
		t2e.setStyleName("label-shortcut");
		window.add(t2e);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Exercise().toLowerCase())));
		
		//NUTRITION
		Text t3 = new Text(AppController.Lang.Nutrition());
		t3.setStyleName("label-title-medium");
		window.add(t3, td);
		//new time
		Text t3a = new Text("Shift+T :");
		t3a.setStyleName("label-shortcut");
		window.add(t3a);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Time().toLowerCase())));
		//new meal
		Text t3b = new Text("Shift+M :");
		t3b.setStyleName("label-shortcut");
		window.add(t3b);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Meal().toLowerCase())));
		//new food
		Text t3c = new Text("Shift+F :");
		t3c.setStyleName("label-shortcut");
		window.add(t3c);
		window.add(new Text(AppController.Lang.AddTarget(AppController.Lang.Food().toLowerCase())));

		//close
		Text linkClose = new Text(AppController.Lang.Close());
		linkClose.setStyleName("link");
		linkClose.setStyleAttribute("margin-top", "20px");
		linkClose.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent arg0) {
				if(window != null) {
					window.hide();
        }
			}
		});
		window.add(linkClose, td);
		
		window.show();
		window.center();
	}
	
}
