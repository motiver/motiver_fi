/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
/*
 * Creates rating panel with x stars
 */
package com.delect.motiver.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.res.MyResources;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class RatingPanel extends LayoutContainer {

	/**
	 * Handler for rating panel
	 */
	public interface RatingPanelHandler {
		public void ratingChanged(int rating);
	}
	private boolean enabled = true;
	private RatingPanelHandler handler;
	
	private int rating = 0;
	
	/**
	 * Rating panel
	 * @param rating : current rating
	 * @param handler : handler to be called on change
	 */
	public RatingPanel(int r, RatingPanelHandler handler) {
		this.setLayout(new RowLayout(Orientation.HORIZONTAL));
		this.setSize(5 * 16 + 10, 18);
		
		load(r, handler);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		//change cursor
		this.setStyleAttribute("cursor", (enabled)? "hand" : "auto");
	}
	
	/*
	 * Updates only stars
	 */
	public void updateValue(int r) {
		load(r, handler);
	}
	
	void load(int r, RatingPanelHandler h) {
		try {
			this.removeAll();
			this.setStyleAttribute("cursor", "pointer");
			
			rating = r;
			handler = h;
			
			int c = 1;
			for(int i=0; i < rating; i++) {
				final int rating = c;
				Image star = new Image(MyResources.INSTANCE.starOn());
				star.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(enabled) {
							starClicked(rating);
	          }
					}
				});
				star.setTitle(AppController.LangConstants.Rating()[rating - 1]);
				star.setStyleName("link");
				this.add(star, new RowData(-1, -1, new Margins(0, 1, 0, 1)));
				
				c++;
			}
			for(int i=0; i < 5 - rating; i++) {
				final int rating = c;
				Image star2 = new Image(MyResources.INSTANCE.starOff());
				star2.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(enabled) {
							starClicked(rating);
            }
					}
				});
				star2.setTitle(AppController.LangConstants.Rating()[rating - 1]);
				star2.setStyleName("link");
				this.add(star2, new RowData(-1, -1, new Margins(0, 1, 0, 1)));  
				c++; 
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		this.layout();
		
	}
	
	/**
	 * Updates rating when star is clicked
	 * @param rating : 1-5
	 */
	void starClicked(int rating) {
		handler.ratingChanged(rating);

		updateValue(rating);
	}

  /**
   * @param rating2
   */
  public void setRating(int rating) {
    updateValue(rating);
  }
}
