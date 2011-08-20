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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.IndexPresenter;
import com.delect.motiver.client.res.MyResources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class IndexView extends IndexPresenter.IndexDisplay {

	private MessageBox box;
	private LayoutContainer content = new LayoutContainer();
	private LayoutContainer footer = new LayoutContainer();
	private LayoutContainer header = new LayoutContainer();
	private LayoutContainer panelPreview = new LayoutContainer();
  private LayoutContainer top = new LayoutContainer();
	
	private LayoutContainer panelSignIn = new LayoutContainer();
	
	public IndexView() {
		
		this.setLayout(new RowLayout());
    
    //error
    top.setId("top");
    header.add(top);
		
		//header
		header.setId("header");
		this.add(header);

		//logo
		LayoutContainer panelLogo = new LayoutContainer();
		panelLogo.setStyleAttribute("margin", "0 auto 0 auto");
		panelLogo.setStyleAttribute("text-align", "center");
		panelLogo.add(new Image(MyResources.INSTANCE.logoBig()), new RowData(-1, -1, new Margins(50, 0, 30, 0)));
		this.add(panelLogo);
		
		//content
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
    content.setLayout(layout);
    content.setId("content");
    content.setHeight(580);
		this.add(content);
		
    //description
		LayoutContainer panelDesc = new LayoutContainer();
		panelDesc.setLayout(new RowLayout());
		panelDesc.setAutoHeight(true);
    panelDesc.setWidth(550);
		panelDesc.add(new Text(AppController.Lang.MotiverDesc()), new RowData(-1, -1, new Margins(10, 0, 10, 0)));
		// data
		Text title1 = new Text(AppController.Lang.Data());
		title1.setStyleName("label-title-big");
		panelDesc.add(title1, new HBoxLayoutData(new Margins(30, 0, 15, 0)));
		panelDesc.add(new Text(AppController.Lang.MotiverDescData()), new RowData(-1, -1, new Margins(0, 0, 10, 0)));
		// friends
		Text title2 = new Text(AppController.Lang.Friends());
		title2.setStyleName("label-title-big");
		panelDesc.add(title2, new HBoxLayoutData(new Margins(30, 0, 15, 0)));
		panelDesc.add(new Text(AppController.Lang.MotiverDescFriends()), new RowData(-1, -1, new Margins(0, 0, 10, 0)));
		// price
		Text title3 = new Text(AppController.Lang.Account());
		title3.setStyleName("label-title-big");
		panelDesc.add(title3, new HBoxLayoutData(new Margins(30, 0, 15, 0)));
		panelDesc.add(new Text(AppController.Lang.MotiverDescAccount()), new RowData(-1, -1, new Margins(0, 0, 10, 0)));
    
    content.add(panelDesc, new HBoxLayoutData(new Margins(0,20,0,0)));

    //right panel
    VerticalPanel panelRight = new VerticalPanel();
    panelRight.setSpacing(10);
    panelPreview.setStyleAttribute("margin-top", "25px");
    panelRight.setHorizontalAlign(HorizontalAlignment.CENTER);
        
    //preview video
    panelPreview.setSize(326, 268);
    panelPreview.setStyleAttribute("margin-bottom", "50px");
    panelRight.add(panelPreview);
        
    //sign in
    panelRight.add(panelSignIn);
        
    content.add(panelRight, new HBoxLayoutData(new Margins(0, 75, 10, 20)));
		
		footer = new LayoutContainer();
		footer.setId("footer");
		footer.layout();
		this.add(footer);
	}
	
	@Override
  public Widget asWidget() {
		
		return this;
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
	public LayoutContainer getInfoContainer() {
		return this;
	}

	@Override
	public LayoutContainer getPreviewVideoContainer() {
		return panelPreview;
	}

	@Override
	public LayoutContainer getSignInContainer() {
		return panelSignIn;
	}


	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	@Override
	public void showProgressBox(boolean show) {
		//hide
		if(box != null && box.isVisible()) {
      box.close();
    }
		
		//show
		if(show) {
			box = MessageBox.wait(AppController.Lang.SigningIn(), AppController.Lang.PleaseWait(), AppController.Lang.Loading() + "...");
		}
	}

  @Override
  public LayoutContainer getMessageContainer() {
    return top;
  }

}
