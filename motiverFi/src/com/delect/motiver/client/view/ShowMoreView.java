/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.ShowMorePresenter;
import com.delect.motiver.client.presenter.ShowMorePresenter.ShowMoreHandler;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * Shows button with text "show more"
 * @author Antti
 *
 */
public class ShowMoreView extends ShowMorePresenter.ShowMoreDisplay {

	private Text btnShowMore = new Text();
	private ShowMoreHandler handler;
	
	
	
	@Override
	public Widget asWidget() {
		this.setId("empty-panel");
		TableLayout tl = new TableLayout(1);
		tl.setWidth("100%");
		tl.setHeight("50px");
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
        
		this.setLayout(tl);
		btnShowMore.setText(AppController.Lang.ShowMore());
		btnShowMore.setStyleName("link");
		btnShowMore.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.showMore();
			}
		});
		this.add(btnShowMore);
		
		this.layout();
		return this;
	}

	@Override
	public void setHandler(ShowMoreHandler handler) {
		this.handler = handler;
	}

}
