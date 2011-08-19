/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.profile;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.presenter.profile.PermissionsCirclePresenter;
import com.delect.motiver.client.presenter.profile.PermissionsCirclePresenter.PermissionsCircleHandler;
import com.delect.motiver.shared.Constants;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.UserModel;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class PermissionsCircleView extends PermissionsCirclePresenter.PermissionsCircleDisplay {
		
  LayoutContainer panelBody = new LayoutContainer();
  CheckBox cbAllUsers = new CheckBox();
  
  private int target;
  private PermissionsCircleHandler handler;
  
	public PermissionsCircleView() {
	  this.setStyleName("panel-circle");
	  
	  //drag target for users
	  DropTarget dt = new DropTarget(this) {
      @Override  
      protected void onDragEnter(DNDEvent event) { 

        //if all users
        if(cbAllUsers.getValue()) {
          return;
        }
        
        UserModel user = event.getData();
        
        //show user's email when dragging
        HTML html = new HTML();
        html.setHTML(AppController.Lang.DropToGiveThesePermissionTo(user.getEmail()));
        event.setData(user);  
        event.getStatus().update(El.fly(html.getElement()).cloneNode(true));
      }
      @Override  
      protected void onDragLeave(DNDEvent event) { 
        super.onDragLeave(event);

        UserModel user = event.getData();
        
        //show user's email when dragging
        HTML html = new HTML();
        html.setHTML(user.getEmail());
        event.setData(user);  
        event.getStatus().update(El.fly(html.getElement()).cloneNode(true));
      }
      @Override  
      protected void onDragFail(DNDEvent event) { 
        super.onDragFail(event); 
        onDragLeave(event);
      }
      @Override  
      protected void onDragCancelled(DNDEvent event) { 
        super.onDragCancelled(event); 
        onDragLeave(event);
      }
      @Override  
      protected void onDragDrop(DNDEvent event) {  
        super.onDragDrop(event);  

        //if all users
        if(cbAllUsers.getValue()) {
          return;
        }
        
        UserModel user = event.getData();
        handler.newUser(user);
      } 
    };  
    dt.setGroup(Constants.DRAG_GROUP_USER);  
    dt.setOverStyle("panel-drag-ok");
    
	}
	
	@Override
	public Widget asWidget() {

	  //title
	  Text textTitle = new Text();
	  textTitle.setStyleName("label-title-medium");
	  this.add(textTitle, new RowData(-1, -1, new Margins(5)));
	  
	  //desc
    Text textDesc = new Text();
    textDesc.setStyleName("label-info");
    this.add(textDesc, new RowData(-1, -1, new Margins(5,5,10,5)));

    //strings
    switch(target) {
      case Permission.READ_TRAINING:
        textTitle.setText(AppController.Lang.Training());
        textDesc.setText(AppController.Lang.PermissionDescTraining());
        break;
      case Permission.READ_NUTRITION:
        textTitle.setText(AppController.Lang.Nutrition());
        textDesc.setText(AppController.Lang.PermissionDescNutrition());
        break;
      case Permission.READ_NUTRITION_FOODS:
        textTitle.setText(AppController.Lang.NutritionFoods());
        textDesc.setText(AppController.Lang.PermissionDescNutritionFoods());
        break;
      case Permission.READ_CARDIO:
        textTitle.setText(AppController.Lang.Cardio());
        textDesc.setText(AppController.Lang.PermissionDescCardio());
        break;
      case Permission.READ_MEASUREMENTS:
        textTitle.setText(AppController.Lang.Measurements());
        textDesc.setText(AppController.Lang.PermissionDescMeasurements());
        break;
      case Permission.COACH:
        textTitle.setText(AppController.Lang.Coach());
        textDesc.setText(AppController.Lang.PermissionDescCoach());
        break;
    }
    
    //enable all (not coach)
    if(target != 5) {
      cbAllUsers.setBoxLabel(AppController.Lang.IncludeAllUsers());
      cbAllUsers.addListener(Events.OnChange, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent arg0) {
          handler.setEnableAll(cbAllUsers.getValue());
          
          panelBody.setEnabled(!cbAllUsers.getValue());
        }
      });
      this.add(cbAllUsers, new RowData(-1, -1, new Margins(5,5,10,5)));
    }
    
	  //users
    this.add(panelBody);
    
	  return this;
	}

  @Override
  public LayoutContainer getBodyContainer() {
    return panelBody;
  }

  @Override
  public void setTarget(int target) {
    this.target = target;
  }

  @Override
  public void setHandler(PermissionsCircleHandler handler) {
    this.handler = handler;
  }
  
  @Override
  public void setAllUsersEnabled(boolean enabled) {
    cbAllUsers.setValue(enabled);
    
    panelBody.setEnabled(!cbAllUsers.getValue());
  }
}
