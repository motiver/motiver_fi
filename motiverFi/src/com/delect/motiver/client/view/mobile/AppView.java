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
package com.delect.motiver.client.view.mobile;

import com.google.gwt.user.client.ui.Widget;
import com.delect.motiver.client.AppController;
import com.delect.motiver.client.res.MyResources;
import com.extjs.gxt.ui.client.widget.MessageBox;


public class AppView extends AppController.AppDisplay {

  private MessageBox box;
  
  public AppView() {
    
    //inject css
    MyResources.INSTANCE.cssMobile().ensureInjected();
  }
	@Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  public void showLoadingDialog(boolean show) {
    //hide
    if(box != null && box.isVisible()) {
      box.close();
    }
    if(show) {
      box = MessageBox.wait(AppController.Lang.Loading(), AppController.Lang.PleaseWait(), AppController.Lang.Loading() + "...");
      box.show();
    }
  }
  
  @Override
  public void onStop() {
    //hide
    if(box != null && box.isVisible()) {
      box.close();
    }
  }
}
