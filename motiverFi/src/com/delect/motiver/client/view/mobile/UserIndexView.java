/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.mobile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.UserIndexPresenter;
import com.delect.motiver.client.presenter.UserIndexPresenter.UserIndexHandler;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class UserIndexView extends UserIndexPresenter.UserIndexDisplay {

	//widgets
	private MessageBox box;
	private LayoutContainer content = new LayoutContainer();
	private LayoutContainer footer = new LayoutContainer();
	
	private LayoutContainer header = new LayoutContainer();
	private Popup popup;
	
	//panels
	private LayoutContainer top = new LayoutContainer();
    
	public UserIndexView() {
		
		this.setLayout(new RowLayout());
		
		//error
		top.setId("top");
		header.add(top);
		
		//header
		header.setId("header");
		this.add(header);
		//content
		content.setId("content");
		this.add(content);
		
		//footer
		footer.setId("footer");
		footer.addText("Motiver &#169; 2011&nbsp;&nbsp;|&nbsp;&nbsp;" + AppController.Lang.MotiverIsInBeta());
		footer.layout();
		this.add(footer);
	}
	
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public LayoutContainer getBodyContainer() {
		return content;
	}

	@Override
	public LayoutContainer getFooterContainer() {
		return footer;
	}
	
	@Override
	public LayoutContainer getHeaderContainer() {
		return header;
	}

	@Override
	public LayoutContainer getMessageContainer() {
		return top;
	}

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
		if(popup != null && popup.isVisible()) {
      popup.hide();
    }
	}

	@Override
	public void setHandler(UserIndexHandler handler) {
	}

	@Override
	public void setPrintLinkVisibility(boolean visible) {
	  
	}

	@Override
	public void showLoading(boolean enabled) {
		if(box != null && box.isVisible()) {
      box.close();
    }
		
		if(enabled) {
			box = MessageBox.wait(AppController.Lang.PleaseWait(), AppController.Lang.Loading() + "...", "");
			box.setModal(true);
			box.show();
		}
	}
}
