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
