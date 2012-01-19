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
package com.delect.motiver.client.view.blog;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
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
    footer.addText("Motiver &#169; 2012&nbsp;&nbsp;|&nbsp;&nbsp;"+Motiver.VERSION);
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
