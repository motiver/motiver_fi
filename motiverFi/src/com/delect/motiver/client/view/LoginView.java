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
import com.delect.motiver.client.presenter.LoginPresenter;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

/**
 * 
 * Init login view
 *  - sign in form
 *  - sign up form
 */
public class LoginView extends LoginPresenter.LoginDisplay {

	private MessageBox box;
	
	public LoginView() {
		this.setLayout(new RowLayout());
	}
	
	@Override
	public Widget asWidget() {
		
	  Html html = new Html("<div id=\"fb-root\"></div><script src=\"http://connect.facebook.net/en_US/all.js#appId=198007390233150&amp;xfbml=1\"></script><fb:login-button show-faces=\"true\" width=\"200\" max-rows=\"1\" perms=\"user_about_me,user_groups,offline_access,publish_stream\"></fb:login-button>");
	  html.setWidth(200);
	  this.add(html);
		
		return this;
	}
	
	@Override
	public void onStop() {
		//hide
		if(box != null && box.isVisible()) {
      box.close();
    }
	}
	
	@Override
	public void showLoadingDialog(boolean enabled) {
		//hide
		if(box != null && box.isVisible()) {
      box.close();
    }
		if(enabled) {
			box = MessageBox.wait(AppController.Lang.SigningIn(), AppController.Lang.PleaseWait(), AppController.Lang.Loading() + "...");
			box.show();
		}
	}
}
