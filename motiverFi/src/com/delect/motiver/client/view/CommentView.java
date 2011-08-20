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
import com.delect.motiver.client.presenter.CommentPresenter;
import com.delect.motiver.client.presenter.CommentPresenter.CommentHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.widget.ImageButton;
import com.delect.motiver.shared.CommentModel;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.Functions.MessageBoxHandler;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class CommentView extends CommentPresenter.CommentDisplay {

	private CommentModel comment;
	private CommentHandler handler;
	
	private boolean isClickable;
	private Text labelText = new Text();

	//widgets
	private Html labelTitle = new Html();
	private LayoutContainer panelButtons = new LayoutContainer();
	//panels
	private LayoutContainer panelRecentActivity = new LayoutContainer();

	MessageBox box = null;
	ImageButton btnRemove = new ImageButton(AppController.Lang.RemoveTarget(AppController.Lang.Comment().toLowerCase()), MyResources.INSTANCE.iconRemove());
	
	public CommentView() {

		this.setAutoHeight(true);
		this.setStyleName("panel-comment");
    panelRecentActivity.setLayout(new RowLayout());
		
		//layout
		TableLayout tl = new TableLayout(2); 
		tl.setCellVerticalAlign(VerticalAlignment.TOP);
    tl.setWidth("100%");
    tl.setCellPadding(5);
    this.setLayout(tl);
        
    //show hide button based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				btnRemove.setVisible(true);
				panelButtons.layout(true);
			}
		});
		this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				btnRemove.setVisible(false);
				panelButtons.layout(true);
			}
		});
	}
	
	@Override
	public Widget asWidget() {
			
		//if clickable
		if(isClickable) {
      //click listener
      this.addListener(Events.OnMouseOver, CustomListener.panelMouseOver);
      this.addListener(Events.OnMouseOut, CustomListener.panelMouseOut);
			this.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					handler.onClick();
				}
			});
			this.setStyleAttribute("cursor", "pointer");
			this.setToolTip(AppController.Lang.ClickToView(AppController.Lang.Comment().toLowerCase()));
		}
		//if unread
		if(comment.isUnread()) {
			this.addStyleName("panel-comment-unread");
    }
		
    //profile pic
    Html html = new Html("<fb:profile-pic uid=\"" + comment.getUid() + "\" facebook-logo=true size=square linked=false></fb:profile-pic>");
    html.setHeight(50);
    html.setWidth(50);
    TableData tdPic = new TableData();
    tdPic.setWidth("60px");
    this.add(html, tdPic);        

    HBoxLayout tl = new HBoxLayout();
    tl.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
    panelButtons.setLayout(tl);
        
    //name & date
    String str = "<div class=\"label-title-small\" style=\"display:inline;\"><fb:name uid=\"" + comment.getUid() + "\" capitalize=\"true\" linked=\"false\"></fb:name></div>";
    labelTitle.setHeight(16);
    str += "<div class=\"label-date\" style=\"margin-left:10px;display:inline;\">" + Functions.getDateTimeString(comment.getDate(), true, true) + "</div>";
    labelTitle.setHtml(str);
    panelButtons.add(labelTitle, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
        
    //remove
    if(comment.getUid().equals(AppController.User.getUid())) {

			//spacer
			HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, 0, 5));
      flex.setFlex(1);  
      panelButtons.add(new Text(), flex);  
	        
      btnRemove.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          //ask for confirm
          box = Functions.getMessageBoxConfirm(AppController.Lang.RemoveConfirm(AppController.Lang.Comment().toLowerCase()), new MessageBoxHandler() {
            @Override
            public void okPressed(String text) {
              handler.commentRemoved();
            }
          });
          box.show();
        }
      });
      panelButtons.add(btnRemove);
      btnRemove.setVisible(false);
    }

    panelRecentActivity.add(panelButtons, new RowData(1, -1, new Margins(0, 0, 5, 0)));
        
    //text
    labelText.setText(comment.getText());
		panelRecentActivity.add(labelText, new RowData(1, -1, new Margins(0, 0, 5, 0)));

		this.add(panelRecentActivity);
        
    return this;
	}

	@Override
	public void setClickable(boolean isClickable) {
		this.isClickable = isClickable;
	}

	@Override
	public void setHandler(CommentHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setModel(CommentModel comment) {
		this.comment = comment;
	}
	
}
