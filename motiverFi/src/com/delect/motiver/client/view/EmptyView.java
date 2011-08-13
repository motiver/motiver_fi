/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

/**
 * 
 * Empty view given text
 */
public class EmptyView extends EmptyPresenter.EmptyDisplay {

	private boolean isLoading;
	
	private Text label = new Text();
	private int panelHeight = 250;

	private TableLayout tl = new TableLayout(1);
	
	public EmptyView() {
		tl.setWidth("100%");
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
	}
	
	@Override
	public Widget asWidget() {
		this.setStyleName("panel-empty");
		
		tl.setHeight(panelHeight + "px");
		
		this.setLayout(tl);
        
		LayoutContainer panel = new LayoutContainer();
		panel.setLayout(new RowLayout());
		
		//loading
		if(isLoading) {			
			//show image
			Image image = new Image(Constants.URL_APP + "img/loading.gif");
			panel.add(image, new RowData(-1, -1, new Margins(0, 0, 5, 0)));
		}
		else {
			panel.add(label);
    }
		
		this.add(panel);
		
		return this;
	}

	@Override
	public void setHeight(int height) {
		panelHeight  = height;
	}

	@Override
	public void setLeftAlign() {
		tl.setCellHorizontalAlign(HorizontalAlignment.LEFT);
	}

	@Override
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	@Override
	public void setText(String text) {
		label.setText(text);
	}

}
