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
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.nutrition.MealsListPresenter;
import com.delect.motiver.client.presenter.nutrition.MealsListPresenter.MealsListHandler;
import com.delect.motiver.client.view.widget.ButtonsPanel;
import com.delect.motiver.client.view.widget.ButtonsPanel.ButtonTarget;
import com.delect.motiver.client.view.widget.MyButton;
import com.delect.motiver.client.view.widget.MyButton.Style;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.TimeModel;
import com.delect.motiver.shared.util.CommonUtils;

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

public class MealsListView extends MealsListPresenter.MealsListDisplay {

	private MessageBox box = null;
	private MealsListHandler handler;
	private Text labelTitle = new Text();
	private String lastQuery = "";
  private ButtonsPanel panelButtons = new ButtonsPanel();
	private LayoutContainer panelData = new LayoutContainer();
	private TextField<String> tfSearch = new TextField<String>();

	public MealsListView() {
		
		this.setLayout(new RowLayout());
		this.setStyleName("panel-meals-list");
		
		//search panel
		LayoutContainer panelSearch = new LayoutContainer();
		panelSearch.setHeight(89);
		panelSearch.setStyleName("panel-search");
		panelSearch.setLayout(new RowLayout());
		//title
		labelTitle.setText(AppController.Lang.MyMeals());
		labelTitle.setStyleName("label-title-medium");
		panelSearch.add(labelTitle, new RowData(-1, -1, new Margins(10, 0, 15, 0)));
		
		//search & cancel
		LayoutContainer panelSearchSub = new LayoutContainer();
		panelSearchSub.setLayoutOnChange(true);
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    panelSearchSub.setLayout(layout);
    panelSearchSub.setHeight(28);
		tfSearch.setEmptyText(AppController.Lang.EnterKeywordToSearchForMeals());
		tfSearch.setMinLength(Constants.LIMIT_MIN_QUERY_WORD);
    CommonUtils.setWarningMessages(tfSearch);
		tfSearch.setAutoValidate(true);
		tfSearch.setValidationDelay(Constants.DELAY_SEARCH);
		tfSearch.addListener(Events.Valid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(handler != null && tfSearch.getValue() != null && !lastQuery.equals(tfSearch.getValue())) {
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
		
    //		showView(0);
		
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
	public void setCopyButtonVisible(boolean visible) {
    if(visible) {
      MyButton btn = panelButtons.addButton(ButtonTarget.Copy, Style.GREEN, AppController.Lang.CopyTo(AppController.Lang.MyMeals()));
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
	public void setHandler(MealsListHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setMoveToTimeButtonVisible(boolean visible, TimeModel time) {
    if(visible) {
      String text = (time != null)? AppController.Lang.MoveTo(CommonUtils.getTimeToString(time.getTime())) : "";
      MyButton btn = panelButtons.addButton(ButtonTarget.MoveToTarget, Style.GREEN, text);
      btn.addListener(Events.OnClick, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          handler.onMoveToTimeButtonClicked();
        }
      });
    }
    else {
      panelButtons.removeButton(ButtonTarget.MoveToTarget);
    }
	}

	@Override
	public void setQuickSelectionButtonVisible(boolean visible, TimeModel time) {
    if(visible) {
      String text = (time != null)? AppController.Lang.MoveSelectedTo(CommonUtils.getTimeToString(time.getTime())) : "";
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
