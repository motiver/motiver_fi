/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;

/**
 * Base class for view
 * @author Antti
 *
 */
public abstract class Display extends LayoutContainer {
	
	public LayoutContainer panelMenuLeft = null; //left menu
	
	private Timer tScroll;

  /**
	 * Returns display's content
	 * @return
	 */
  public abstract Widget asWidget();
    
  public LayoutContainer getBaseContainer() {
  	return this;
  }


	/**
	 * Scrolls panel to view and highlights panel for xx seconds
	 */
	public void highlight() {
		
		scrollToView();
		
		//highlight
		addStyleName("panel-highlight");
		
		//run timer to turn of highlighting
		Timer timer = new Timer() {
			public void run() {
				try {
					removeStyleName("panel-highlight");
				} catch (Exception e) {
				}
			}
    };
    timer.schedule(Constants.DELAY_HIGHLIGHT);
	}

	/**
   * Called when display is stopped
   */
  public void onStop() {
  	
  }

	/**
	 * Scrolls browser so panel is complete visible
	 */
	public void scrollToView() {
			
		try {
		  //cancel timer if previous scroll is going on
		  if(tScroll != null) {
		    tScroll.cancel();
		    tScroll = null;
		  }
		  
			int height = this.getHeight();
			int top = this.getAbsoluteTop();
			int scroll = Window.getScrollTop();
			int windowH = Window.getClientHeight();

			//scrolled too much
			if(scroll > top && scroll + windowH > height + top) {
				scrollToView(Window.getScrollTop(), top - 50);
			}
			//if scrolled too little
			else if(scroll + windowH < height + top) {
				int newScroll = (height + top) - windowH + 50;
				scrollToView(Window.getScrollTop(), newScroll);
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	private void scrollToView(int current, final int target) {
	  boolean scrollMore = true;
	  
	  //if scrolling up
	  if(current > target) {
	    current -= 15;
	    if(current <= target) {
	      current = target;
	      scrollMore = false;
	    }
	  }
	  //scrolling down
	  else {
      current += 15;
      if(current > target) {
        current = target;
        scrollMore = false;
      }
	  }
	  
	  //scroll
    Window.scrollTo(Window.getScrollLeft(), current);
    
    //if still left to scroll
    if(scrollMore) {
      final int newScroll = current;
      tScroll = new Timer() {
        @Override
        public void run() {
          scrollToView(newScroll, target);
        }
      };
      tScroll.schedule(30);
    }
	}
	
	public void setContentEnabled(boolean b) {
		this.setEnabled(b);
	}

	/**
	 * Set partial transparency on/off when mouse over/out
	 * @param enabled
	 */
	public void setTransparencyOnMouseOver(boolean enabled) {

		//transparent when mouse not over
		if(enabled) {
			this.addListener(Events.OnMouseOut, CustomListener.listenerMouseOut );
			this.addListener(Events.OnMouseOver, CustomListener.listenerMouseOver );
			this.addStyleName("panel-out");
		}
		else {
			this.removeListener(Events.OnMouseOut, CustomListener.listenerMouseOut );
			this.removeListener(Events.OnMouseOver, CustomListener.listenerMouseOver );
			this.removeStyleName("panel-out");
		}
	}
	
	/**
	 * Adds menu item to left menu. Creates menu if not already created.
	 * @param text
   * @param selected
	 * @param listener
	 */
	protected void addMenuItem(String text, boolean selected, Listener<BaseEvent> listener) {

	  //add menu
	  if(panelMenuLeft == null) {
	    panelMenuLeft = new LayoutContainer();
	    panelMenuLeft.setStyleName("panel-menu-left");
	    
	    this.add(panelMenuLeft);
	  }

    //training
    final Text link = new Text(text);
    link.setStyleName("link");
    if(selected) {
      link.addStyleName("sel");
    }
    //change selected item on click
    link.addListener(Events.OnClick, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        //go through items
        for(Component c : panelMenuLeft.getItems()) {
          final Text t = (Text)c;
          
          //clicked item
          if(c.equals(be.getSource())) {
            t.addStyleName("sel");
          }
          else {
            t.removeStyleName("sel");
          }
        }
      }
    });
    link.addListener(Events.OnClick, listener );
    panelMenuLeft.add(link);
    panelMenuLeft.layout();
	}
	
	/**
	 * Selects correct menu item
	 * @param index
	 */
	public void setSelectedMenuItem(int index) {
	  if(panelMenuLeft != null) {
	    //go through items
	    int i = 0;
      for(Component c : panelMenuLeft.getItems()) {
        final Text t = (Text)c;
        
        //clicked item
        if(i == index) {
          t.addStyleName("sel");
        }
        else {
          t.removeStyleName("sel");
        }
        
        i++;
      }
	  }
	}
}
