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

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.UserIndexPresenter;
import com.delect.motiver.client.presenter.UserIndexPresenter.UserIndexHandler;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.shared.TicketModel;
import com.delect.motiver.shared.util.CommonUtils;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;

public class UserIndexView extends UserIndexPresenter.UserIndexDisplay {

	//widgets
	private MessageBox box;
	private MyButton btnSend = new MyButton();
	private LayoutContainer content = new LayoutContainer();
	private LayoutContainer footer = new LayoutContainer();
	
	private UserIndexHandler handler;
	private LayoutContainer header = new LayoutContainer();
	private Text linkPrintPage = new Text();
	private Text linkSendBug = new Text();
	private Popup popup;
	
	//panels
	private LayoutContainer top = new LayoutContainer();
    
	public UserIndexView() {
		
		this.setLayout(new RowLayout());
		
		//error
		top.setId("top");
		header.add(top);
		
		//header
		header.setId("header");
		this.add(header);

		//help container
		VerticalPanel panelHelp = new VerticalPanel();
		panelHelp.setSpacing(5);
		panelHelp.setHorizontalAlign(HorizontalAlignment.RIGHT);
		panelHelp.setStyleName("panel-help");
		//shortcut key text
		panelHelp.addText(AppController.Lang.Help() + ": Shift + H");
		//bug informer
		linkSendBug.setText(AppController.Lang.ReportProblem());
		linkSendBug.setStyleName("link");
		linkSendBug.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent arg0) {
				showBugWindow();
			}
		});
		panelHelp.add(linkSendBug);
		//print page link
		linkPrintPage.setText(AppController.Lang.PrintView());
		linkPrintPage.setVisible(false);
		linkPrintPage.setStyleName("link");
		linkPrintPage.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent arg0) {
				handler.printPage();
			}
		});
		panelHelp.add(linkPrintPage);
		//Google+ button
    Html html = new Html("<g:plusone size=\"tall\" href=\"http://www.motiver.fi\"></g:plusone>");
		html.setStyleAttribute("margin-top", "10px");
		panelHelp.add(html);
		this.add(panelHelp);
		
		//content
		content.setId("content");
		this.add(content);
		
		//footer
		footer.setId("footer");
		footer.add(new Html("Motiver &#169; 2012&nbsp;&nbsp;|&nbsp;&nbsp;"+Motiver.VERSION
		    +" | <a class='link' href='http://dev.motiver-app.appspot.com/'>"+AppController.Lang.ExpirementalVersion()+"</a>"));
		footer.layout();
		this.add(footer);
		
		setPlusLang(AppController.User.getLocale());
		
		//add Google+ script
		Document doc = Document.get();
    ScriptElement script = doc.createScriptElement();
    script.setSrc("https://apis.google.com/js/plusone.js");
    script.setType("text/javascript");
    script.setLang("javascript");
    doc.getBody().appendChild(script);
	}
	
	public static native void setPlusLang(String l) /*-{
    $wnd.___gcfg = {lang: l};
  }-*/;
	
	@Override
	public Widget asWidget() {
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

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
		if(popup != null && popup.isVisible()) {
      popup.hide();
    }
	}

	@Override
	public void setHandler(UserIndexHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setPrintLinkVisibility(boolean visible) {
		linkPrintPage.setVisible(visible);
	}

	@Override
	public void showLoading(boolean enabled) {
		if(box != null && box.isVisible()) {
      box.close();
    }
		
		if(enabled) {
			box = MessageBox.wait(AppController.Lang.PleaseWait(), AppController.Lang.Loading() + "...", "");
			box.setModal(true);
			box.show();
		}
	}

	/**
	 * Shows simple form where user can send bugs
	 */
	protected void showBugWindow() {
		if(popup != null && popup.isVisible()) {
      popup.hide();
    }
		
		popup = new Popup();
		popup.setAutoHide(true);
		popup.setBorders(true);  
		popup.setLayout(new RowLayout());
		popup.setStyleAttribute("background-color", "#fff");
		popup.setStyleAttribute("padding", "10px");

		Text textTitle = new Text(AppController.Lang.ReportProblemOnThisPage() + ":");
		textTitle.setStyleName("label-title-medium");
		popup.add(textTitle, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
		
		final TextArea textArea = new TextArea();
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
		textArea.setMinLength(10);
		textArea.setWidth(400);
		textArea.setHeight(200);
    CommonUtils.setWarningMessages(textArea);
		textArea.setEmptyText(AppController.Lang.Description());  
		popup.add(textArea, new RowData(-1, -1, new Margins(0, 0, 10, 0)));

    LayoutContainer panelButtons = new LayoutContainer();
    HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    panelButtons.setLayout(layout);
    panelButtons.setHeight(32);
	    
    //send MyButton
    btnSend.setEnabled(false);
    btnSend.setText(AppController.Lang.Send());
    btnSend.setScale(ButtonScale.MEDIUM);
    btnSend.setColor(MyButton.Style.GREEN);
    btnSend.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//send ticket
				TicketModel ticket = new TicketModel();
				ticket.setPriority(1);
				ticket.setDesc("User: "+AppController.User.getEmail()+": "+textArea.getValue());
				ticket.setTitle("Reported problem at #" + History.getToken());
				ticket.setUid(AppController.User.getUid());
				handler.newTicket(ticket);

        if(popup != null && popup.isVisible()) {
          popup.hide();
        }
			}
    });
    panelButtons.add(btnSend, new HBoxLayoutData(new Margins(0, 10, 0, 0)));
    //cancel
    Text linkCancel = new Text(AppController.Lang.Cancel());
    linkCancel.setStyleName("link");
    linkCancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent arg0) {
				if(popup != null && popup.isVisible()) {
          popup.hide();
        }
			}
    });
	    
		popup.add(panelButtons);
		
		popup.show(linkSendBug);
	}

}
