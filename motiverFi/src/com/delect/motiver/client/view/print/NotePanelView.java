/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.print;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.NotePanelPresenter;
import com.delect.motiver.client.presenter.NotePanelPresenter.NotePanelHandler;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * Panel that wraps content and header
 * <br>Header: title (left), buttons/panel(right), help (right)
 */
public class NotePanelView extends NotePanelPresenter.NotePanelDisplay  {

	private NotePanelHandler handler;
	private LayoutContainer panelData = new LayoutContainer();
	
	private LayoutContainer panelHeader = new LayoutContainer();
	Text textTitle = new Text("");
		
	public NotePanelView() {
		
		try {
			
			this.addStyleName("panel-note");
			this.setLayout(new RowLayout());

      textTitle.setStyleName("label-title-big");
      panelHeader.add(textTitle);
      panelHeader.setStyleName("panel-note-header");
      panelData.add(panelHeader, new RowData(-1, -1, new Margins(0, 10, 0, 0)));
	        
			panelData.setLayout(new RowLayout());
			panelData.setStyleName("panel-note-data");
			this.add(panelData);
			
			//call handler when panel expands/collapses
			panelData.addListener(Events.Show, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.contentVisible();
				}
			});
			panelData.addListener(Events.Hide, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.contentHidden();
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	@Override
	public void addHeaderButton(String text, Listener<BaseEvent> listener) {}
	
	@Override
	public Widget asWidget() {

		panelData.setStyleAttribute("min-height", "250px");
		panelData.hide();
		
		return this;
	}

	/**
	 * Add something to content
	 * Parameters: component
	 */
	@Override
	public LayoutContainer getBodyContainer() {
		return panelData;
	}

	@Override
	public void setHandler(NotePanelHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setTitle(String title) {
		textTitle.setText(title);
	}

	@Override
	public void showContent() {
		if(!panelData.isVisible()) {
			panelData.show();
		}
	}
}
