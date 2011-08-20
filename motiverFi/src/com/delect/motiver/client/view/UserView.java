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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.presenter.UserPresenter.UserHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.UserModel;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

/**
 * Shows single user
 */
public class UserView extends UserPresenter.UserDisplay {

	private UserHandler handler;
	private boolean smallPicture;
	private UserModel user;
  private boolean showDelete = false;

	public UserView() {

		this.setStyleName("panel-user");

    this.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
    this.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.onClick();
			}
		});
		
		//drag source
		DragSource source = new DragSource(this) {  
      @Override  
      protected void onDragStart(DNDEvent event) { 
          super.onDragStart(event);
                            
        //show user's email when dragging
        HTML html = new HTML();
        html.setHTML(user.getEmail());
        event.setData(user);  
        event.getStatus().update(El.fly(html.getElement()).cloneNode(true));
      }
    };
    source.setGroup(Constants.DRAG_GROUP_USER);
	}
	
	@Override
	public Widget asWidget() {

	  this.setWidth(245);
	  
		//small picture: horizontal layout
		this.setHeight(60);
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);  
    this.setLayout(layout);
		
    //profile pic
    Image img = new Image(MyResources.INSTANCE.getEmptyProfilePic());
    this.add(img, new HBoxLayoutData(new Margins(0,5,0,5)));

    //name
    Html htmlName = new Html(user.getNickName());
    htmlName.setStyleName("label-user-name");
    htmlName.setHeight(20);
    htmlName.setWidth(140);
    this.add(htmlName, new HBoxLayoutData(new Margins(0,10,0,0)));
		
    //spacer
    HBoxLayoutData flex = new HBoxLayoutData(new Margins(0,10,0,0));
    flex.setFlex(1);
    this.add(new Text(), flex);
    
    //delete icon
    if(showDelete) {
      ImageButton btnRemove = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.ThisUser().toLowerCase()), MyResources.INSTANCE.iconRemove());
      btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onDelete();
        }
      });
      this.add(btnRemove, new HBoxLayoutData(new Margins(0,5,0,0)));
    }
    
		return this;
	}

	@Override
	public void setHandler(UserHandler h) {
		handler = h;
	}

	@Override
	public void setModel(UserModel user) {
		this.user = user;  
	}

	@Override
	public void setSmallPicture(boolean smallPicture) {
		this.smallPicture = smallPicture;
	}

  @Override
  public void showDeleteIcon() {
    showDelete  = true;
  }
	
}
