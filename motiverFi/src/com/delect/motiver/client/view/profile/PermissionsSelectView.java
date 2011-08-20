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
package com.delect.motiver.client.view.profile;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter;
import com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectHandler;
import com.delect.motiver.shared.Constants;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PermissionsSelectView extends PermissionsSelectPresenter.PermissionsSelectDisplay {

	private PermissionsSelectHandler handler;
	private String queryLast = "";
	
	private LayoutContainer panelPermissionTraining = new LayoutContainer();
  private LayoutContainer panelPermissionNutrition = new LayoutContainer();
  private LayoutContainer panelPermissionNutritionFoods = new LayoutContainer();
  private LayoutContainer panelPermissionCardio = new LayoutContainer();
  private LayoutContainer panelPermissionMeasurements = new LayoutContainer();
  private LayoutContainer panelPermissionCoach = new LayoutContainer();
	
	private LayoutContainer panelUsers = new LayoutContainer();
	
	public PermissionsSelectView() {	
	}
	
	@Override
	public Widget asWidget() {

	  //search widget
	  final TextField<String> tfSearch = new TextField<String>();
	  tfSearch.setAllowBlank(true);
	  tfSearch.setAutoValidate(true);
	  tfSearch.setMinLength(Constants.LIMIT_MIN_QUERY_WORD);
	  tfSearch.setMessageTarget("none");
	  tfSearch.setWidth("775px");
	  tfSearch.setEmptyText(AppController.Lang.SearchUsers());
	  tfSearch.addListener(Events.Valid, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        if(!queryLast.equals(tfSearch.getValue())) {
          handler.onUserSearch(tfSearch.getValue());
        }
      }
	  });
	  this.add(tfSearch, new RowData(-1, -1, new Margins(10)));
	  
	  //search results
	  panelUsers.setStyleAttribute("min-height", "100px");
	  TableLayout tl = new TableLayout(4);
	  tl.setCellPadding(5);
	  tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
	  panelUsers.setLayout(tl);
	  this.add(panelUsers, new RowData(-1, -1, new Margins(10)));
	  
	  
	  //permission boxes
	  HorizontalPanel p1 = new HorizontalPanel();
	  p1.setSpacing(10);
	  p1.add(panelPermissionTraining);
    p1.add(panelPermissionNutrition);
    p1.add(panelPermissionNutritionFoods);
    p1.setCellWidth(panelPermissionTraining, "33%");
    p1.setCellWidth(panelPermissionNutrition, "33%");
    p1.setCellWidth(panelPermissionNutritionFoods, "33%");
    this.add(p1);
    HorizontalPanel p2 = new HorizontalPanel();
    p2.setSpacing(10);
    p2.add(panelPermissionCardio);
    p2.add(panelPermissionMeasurements);
    p2.add(panelPermissionCoach);
    p2.setCellWidth(panelPermissionCardio, "33%");
    p2.setCellWidth(panelPermissionMeasurements, "33%");
    p2.setCellWidth(panelPermissionCoach, "33%");
    this.add(p2);
	  
	  return this;
	}

	@Override
	public void setHandler(PermissionsSelectHandler handler) {
		this.handler = handler;
	}

  @Override
  public LayoutContainer getUsersContainer() {
    return panelUsers;
  }

  @Override
  public LayoutContainer getPermissionTrainingContainer() {
    return panelPermissionTraining;
  }

  @Override
  public LayoutContainer getPermissionNutritionContainer() {
    return panelPermissionNutrition;
  }

  @Override
  public LayoutContainer getPermissionNutritionFoodsContainer() {
    return panelPermissionNutritionFoods;
  }

  @Override
  public LayoutContainer getPermissionCardioContainer() {
    return panelPermissionCardio;
  }

  @Override
  public LayoutContainer getPermissionMeasurementsContainer() {
    return panelPermissionMeasurements;
  }

  @Override
  public LayoutContainer getPermissionCoachContainer() {
    return panelPermissionCoach;
  }

}
