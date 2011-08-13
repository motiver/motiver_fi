/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.blog;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.blog.BlogIndexPresenter;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class BlogIndexView extends BlogIndexPresenter.BlogIndexDisplay {

	private LayoutContainer content = new LayoutContainer();
	private LayoutContainer footer = new LayoutContainer();
	private LayoutContainer header = new LayoutContainer();
  private LayoutContainer top = new LayoutContainer();
	
	public BlogIndexView() {
		
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
		this.layout();
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

}
