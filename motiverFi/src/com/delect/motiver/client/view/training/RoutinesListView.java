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
package com.delect.motiver.client.view.training;

import java.util.Date;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.training.RoutinesListPresenter;
import com.delect.motiver.client.presenter.training.RoutinesListPresenter.RoutinesListHandler;
import com.delect.motiver.client.view.widget.ButtonsPanel;
import com.delect.motiver.client.view.widget.ButtonsPanel.ButtonTarget;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.client.view.widget.MyButton.Style;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class RoutinesListView extends RoutinesListPresenter.RoutinesListDisplay {
	
	private MessageBox box = null;
	
	private RoutinesListHandler handler;
	private Text labelTitle = new Text();
	private String lastQuery = "";

  private ButtonsPanel panelButtons = new ButtonsPanel();
	private LayoutContainer panelData = new LayoutContainer();
	private TextField<String> tfSearch = new TextField<String>();
	
	public RoutinesListView() {
		
		this.setLayout(new RowLayout());
		this.setStyleName("panel-routines-list");
		
		//search panel
		LayoutContainer panelSearch = new LayoutContainer();
		panelSearch.setHeight(89);
		panelSearch.setStyleName("panel-search");
		panelSearch.setLayout(new RowLayout());
		//title
		labelTitle.setText(AppController.Lang.MyRoutines());
		labelTitle.setStyleName("label-title-medium");
		panelSearch.add(labelTitle, new RowData(-1, -1, new Margins(10, 0, 15, 0)));
		
		//search & cancel
		LayoutContainer panelSearchSub = new LayoutContainer();
		panelSearchSub.setLayoutOnChange(true);
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    panelSearchSub.setLayout(layout);
    panelSearchSub.setHeight(28);
		tfSearch.setEmptyText(AppController.Lang.EnterKeywordToSearchForRoutines());
		tfSearch.setMinLength(3);
		tfSearch.setMessageTarget("none");
		tfSearch.setAutoValidate(true);
		tfSearch.setValidationDelay(Constants.DELAY_SEARCH);
		tfSearch.addListener(Events.Valid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(handler != null && !lastQuery.equals(tfSearch.getValue())) {
					lastQuery = tfSearch.getValue();
					handler.search(tfSearch.getValue());
				}
			}
		});
		HBoxLayoutData data = new HBoxLayoutData(new Margins(0));
		data.setFlex(1);
		panelSearchSub.add(tfSearch, data);
		//search button
		MyButton btnSearch = new MyButton(AppController.Lang.Search());
		btnSearch.setScale(ButtonScale.MEDIUM);
		panelSearchSub.add(btnSearch, new HBoxLayoutData(new Margins(0, 0, 0, 10)));
		panelSearch.add(panelSearchSub);
		
		this.add(panelSearch, new RowData(1, -1, new Margins(0, 0, 10, 0)));
		
		//buttons (cancel, back, move/add)
		this.add(panelButtons, new RowData(-1, -1, new Margins(0, 0, 10, 0)));

		this.add(panelData, new RowData(1, -1, new Margins(0)));

	}
	
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public LayoutContainer getDataContainer() {
		return panelData;
	}

	@Override
	public void onStop() {
		if(box != null && box.isVisible()) {
      box.close();
    }
	}

	@Override
	public void setBackButtonVisible(boolean visible) {
	  if(visible) {
	    MyButton btn = panelButtons.addButton(ButtonTarget.Back, Style.DEFAULT, AppController.Lang.Back());
	    btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onBackButtonClicked();
        }
      });
    }
	  else {
	    panelButtons.removeButton(ButtonTarget.Back);
	  }
	}

	@Override
	public void setCancelButtonVisible(boolean visible) {
    if(visible) {
      MyButton btn = panelButtons.addButton(ButtonTarget.Cancel, Style.RED, AppController.Lang.CancelSelection());
      btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onCancelButtonClicked();
        }
      });
    }
    else {
      panelButtons.removeButton(ButtonTarget.Cancel);
    }
	}

	@Override
	public void setContentEnabled(boolean enabled) {
		setEnabled(enabled);
	}

	@Override
	public void setCopyButtonVisible(boolean visible) {
    if(visible) {
      MyButton btn = panelButtons.addButton(ButtonTarget.Copy, Style.GREEN, AppController.Lang.CopyTo(AppController.Lang.MyRoutines()));
      btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onCopyButtonClicked();
        }
      });
    }
    else {
      panelButtons.removeButton(ButtonTarget.Copy);
    }
	}


	@Override
	public void setHandler(RoutinesListHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMoveToDateButtonVisible(boolean visible, Date date) {
    if(visible) {
      String text = (date != null)? AppController.Lang.MoveTo(Functions.getDateString(date, true, false)) : "";
      MyButton btn = panelButtons.addButton(ButtonTarget.MoveToDate, Style.GREEN, text);
      btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onMoveToDateButtonClicked();
        }
      });
    }
    else {
      panelButtons.removeButton(ButtonTarget.MoveToDate);
    }
	}

	@Override
	public void setQuickSelectionButtonVisible(boolean visible, Date date) {
    if(visible) {
      String text = (date != null)? AppController.Lang.MoveTo(Functions.getDateString(date, true, false)) : "";
      MyButton btn = panelButtons.addButton(ButtonTarget.QuickSelection, Style.GREEN, text);
      btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onQuickSelectionButtonClicked();
        }
      });
    }
    else {
      panelButtons.removeButton(ButtonTarget.QuickSelection);
    }
	}

	/**
	 * Sets title
	 * @param btnText
	 * @param handler
	 */
	@Override
	public void setTitle(String text) {
		labelTitle.setText(text);
	}

}
