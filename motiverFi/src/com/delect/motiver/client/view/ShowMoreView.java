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
