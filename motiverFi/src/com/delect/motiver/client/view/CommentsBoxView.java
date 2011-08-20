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
import com.delect.motiver.client.presenter.CommentsBoxPresenter;
import com.delect.motiver.client.presenter.CommentsBoxPresenter.CommentBoxHandler;
import com.delect.motiver.client.view.widget.MyButton;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class CommentsBoxView extends CommentsBoxPresenter.CommentsBoxDisplay {
	
	private MyButton btnSend = new MyButton();

	private CheckBox cbPublish = new CheckBox();
	private CommentBoxHandler handler;
	private boolean isDisabled;

	//panels
	private LayoutContainer panelComments = new LayoutContainer();
	//widgets
	private TextArea textArea = new TextArea();
	private String title = "";
	
	public CommentsBoxView() {
		this.setLayout(new RowLayout());
		this.setStyleName("panel-comments-box");
	}
	
	@Override
	public Widget asWidget() {
		
		//if disabled -> don't render anything
		if(!isDisabled) {
			//title
			Text text = new Text(AppController.Lang.CommentsFor(title.toLowerCase()) + ":");
			text.setStyleName("label-title-medium");
			this.add(text, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
			
			//new comment form
			textArea.addListener(Events.Valid, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent arg0) {
					btnSend.setEnabled(true);
				}
			});
			textArea.addListener(Events.Invalid, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent arg0) {
					btnSend.setEnabled(false);
				}
			});
			textArea.setPreventScrollbars(true);
			textArea.setAutoValidate(true);
			textArea.setMaxLength(450);
			textArea.setMinLength(3);
			textArea.setMessageTarget("none");
			textArea.setEmptyText(AppController.Lang.WriteYourComment());  
      this.add(textArea, new RowData(1, -1, new Margins(0, 0, 10, 0)));
		    
      LayoutContainer panelButtons = new LayoutContainer();
      HBoxLayout layout = new HBoxLayout();
      layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
      panelButtons.setLayout(layout);
      panelButtons.setHeight(32);
		    
      //send button
      btnSend.setEnabled(false);
      btnSend.setText(AppController.Lang.Send());
      btnSend.setScale(ButtonScale.MEDIUM);
      btnSend.setColor(MyButton.Style.GREEN);
      btnSend.addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					String text = textArea.getValue();
					
					//clear text area
					textArea.clear();
					
					handler.newComment(text, cbPublish.getValue());
				}
      });
			panelButtons.add(btnSend, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
			
			//publish on fb
			panelButtons.add(cbPublish, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			panelButtons.add(new Text(AppController.Lang.PublishAlsoOnFacebook()), new HBoxLayoutData(new Margins(0, 5, 0, 0)));
		    
			this.add(panelButtons, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
		    
			//comments
			this.add(panelComments);
		}
		
		return this;
	}

	@Override
	public LayoutContainer getCommentsContainer() {
		return panelComments;
	}

	@Override
	public void setCommentTitle(String title) {
		this.title = title;
	}

	@Override
	public void setDisabled() {
		isDisabled = true;
	}

	@Override
	public void setHandler(CommentBoxHandler handler) {
		this.handler = handler;
	}

}
