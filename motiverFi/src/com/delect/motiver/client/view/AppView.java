/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.delect.motiver.client.AppController;
import com.delect.motiver.client.res.MyResources;
import com.extjs.gxt.ui.client.widget.MessageBox;


public class AppView extends AppController.AppDisplay {

  private MessageBox box;
  
  public AppView() {
    
    //inject css
    MyResources.INSTANCE.css().ensureInjected();
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
