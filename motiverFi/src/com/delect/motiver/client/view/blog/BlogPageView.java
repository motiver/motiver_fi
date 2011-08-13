/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.blog;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.blog.BlogPagePresenter;

public class BlogPageView extends BlogPagePresenter.BlogPageDisplay {
	
	@Override
	public Widget asWidget() {
		return this;
	}

}
