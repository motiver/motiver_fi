/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.LoadingPresenter;

import com.extjs.gxt.ui.client.widget.Text;

public class LoadingView extends LoadingPresenter.LoadingDisplay {


	private String message = "";

	public LoadingView() {
		this.setStyleName("panel-loading");
	}
	
	@Override
	public Widget asWidget() {
		
		this.add(new Text(message));
		
		return this;
	}

	@Override
	public void setMessage(String message) {
		this.message  = message;
	}
	
}
