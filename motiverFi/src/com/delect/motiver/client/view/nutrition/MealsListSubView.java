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
import com.delect.motiver.client.presenter.nutrition.MealsListSubPresenter;
import com.delect.motiver.client.presenter.nutrition.MealsListSubPresenter.MealsListSubHandler;
import com.delect.motiver.client.view.MyContentPanel;
import com.delect.motiver.client.view.widget.MyButton;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class MealsListSubView extends MealsListSubPresenter.MealsListSubDisplay {

	private MealsListSubHandler handler;
	private MyContentPanel panelMostPopular = new MyContentPanel();

	private MyContentPanel panelMyMeals = new MyContentPanel();
	

	public MealsListSubView() {

		//VIEW 1
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
    this.setLayout(tl);
    TableData td = new TableData();
    td.setVerticalAlign(VerticalAlignment.TOP);
    td.setWidth("50%");
		
		//my meals
    panelMyMeals.addStyleName("panel-meals-my");
		panelMyMeals.setHeading(AppController.Lang.MyMeals());
		panelMyMeals.setLayout(new RowLayout());
		panelMyMeals.setStyleAttribute("margin-right", "5px");
		this.add(panelMyMeals, td);
		//most popular
		panelMostPopular.addStyleName("panel-meals-mostpopular");
		panelMostPopular.setHeading(AppController.Lang.MostPopular());
		this.add(panelMostPopular, td);
		
		//new meal button
		MyButton btnAddMeal = new MyButton(AppController.Lang.CreateTarget(AppController.Lang.Meal().toLowerCase()));
		btnAddMeal.setScale(ButtonScale.MEDIUM);
		btnAddMeal.setColor(MyButton.Style.GREEN);
		btnAddMeal.setStyleAttribute("margin-top", "10px");
		btnAddMeal.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				handler.createMeal("");
			}
		});
		this.add(btnAddMeal);
		
	}

	@Override
	public Widget asWidget() {
		
    //		showView(0);
		
		return this;
	}

	@Override
	public LayoutContainer getMostPopularContainer() {
		return panelMostPopular;
	}

	@Override
	public LayoutContainer getMyMealsContainer() {
		return panelMyMeals;
	}

	@Override
	public void setHandler(MealsListSubHandler handler) {
		this.handler = handler;
	}
	
}
