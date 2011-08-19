/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.profile;

import java.util.ArrayList;
import java.util.List;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.profile.PermissionsCirclePresenter.PermissionsCircleDisplay;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.client.view.profile.PermissionsCircleView;
import com.delect.motiver.shared.Permission;
import com.delect.motiver.shared.UserModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

public class PermissionsSelectPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class PermissionsSelectDisplay extends Display {
    public abstract void setHandler(PermissionsSelectHandler profileHandler);
    public abstract LayoutContainer getUsersContainer();
    public abstract LayoutContainer getPermissionTrainingContainer();
    public abstract LayoutContainer getPermissionNutritionContainer();
    public abstract LayoutContainer getPermissionNutritionFoodsContainer();
    public abstract LayoutContainer getPermissionCardioContainer();
    public abstract LayoutContainer getPermissionMeasurementsContainer();
    public abstract LayoutContainer getPermissionCoachContainer();
	}
	public interface PermissionsSelectHandler {
	  void onUserSearch(String query);
	}

	private PermissionsSelectDisplay display;
	
  private EmptyPresenter emptyPresenter;
  private List<UserPresenter> userPresenters = new ArrayList<UserPresenter>();

  private PermissionsCirclePresenter circleTrainingPresenter;
  private PermissionsCirclePresenter circleNutritionPresenter;
  private PermissionsCirclePresenter circleNutritionFoodsPresenter;
  private PermissionsCirclePresenter circleCardioPresenter;
  private PermissionsCirclePresenter circleMeasurementsPresenter;
  private PermissionsCirclePresenter circleCoachPresenter;

	/**
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public PermissionsSelectPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, PermissionsSelectDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
		
		//permissions circles
		circleTrainingPresenter = new PermissionsCirclePresenter(rpcService, eventBus, (PermissionsCircleDisplay)GWT.create(PermissionsCircleView.class), Permission.READ_TRAINING);
    circleNutritionPresenter = new PermissionsCirclePresenter(rpcService, eventBus, (PermissionsCircleDisplay)GWT.create(PermissionsCircleView.class), Permission.READ_NUTRITION);
    circleNutritionFoodsPresenter = new PermissionsCirclePresenter(rpcService, eventBus, (PermissionsCircleDisplay)GWT.create(PermissionsCircleView.class), Permission.READ_NUTRITION_FOODS);
    circleCardioPresenter = new PermissionsCirclePresenter(rpcService, eventBus, (PermissionsCircleDisplay)GWT.create(PermissionsCircleView.class), Permission.READ_CARDIO);
    circleMeasurementsPresenter = new PermissionsCirclePresenter(rpcService, eventBus, (PermissionsCircleDisplay)GWT.create(PermissionsCircleView.class), Permission.READ_MEASUREMENTS);
    circleCoachPresenter = new PermissionsCirclePresenter(rpcService, eventBus, (PermissionsCircleDisplay)GWT.create(PermissionsCircleView.class), Permission.COACH);
		
	}
	
	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
		
		display.setHandler(new PermissionsSelectHandler() {
      @Override
      public void onUserSearch(String query) {
        loadUsers(query);
      }
		});
		
	}

	/**
   * Loads users based on query word
   */
  protected void loadUsers(String query) {

    if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
    unbindPresenters();
    
    //show loading
    emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING_SMALL);
    emptyPresenter.run(display.getUsersContainer());
    
    Motiver.setNextCallCacheable(true);
    final Request req = rpcService.searchUsers(0, query, new MyAsyncCallback<List<UserModel>>() {
      @Override
      public void onSuccess(List<UserModel> result) {
        showUsers(result);
      }
    });
    addRequest(req);
  }

  /**
   * Shows user presenters
   * @param result
   */
  protected void showUsers(List<UserModel> list) {

    try {

      if(emptyPresenter != null) {
        emptyPresenter.stop();
      }
      unbindPresenters();
      
      //if no measurements
      if(list.size() == 0) {
        emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoUsersFound(), EmptyPresenter.OPTION_SMALLER);
        emptyPresenter.run(display.getUsersContainer());
      }
      else {
        
        for(UserModel m : list) {
          final UserPresenter p = new UserPresenter(rpcService, eventBus, (UserDisplay)GWT.create(UserView.class), m, true);
          userPresenters.add(p);
          p.run(display.getUsersContainer());
        }
          
      }
      
    } catch (Exception e) {
      Motiver.showException(e);
    }
  }

  /**
   * Unbinds all the meal/time presenters
   */
  private void unbindPresenters() {
    
    if(userPresenters != null) {
      for(int i=0; i < userPresenters.size(); i++) {
        final Presenter presenter = userPresenters.get(i);
        if(presenter != null) {
          presenter.stop();
        }
      }
      userPresenters.clear();          
    }
  }

  @Override
	public void onRun() {
    
    //show permissions circles
    circleTrainingPresenter.run(display.getPermissionTrainingContainer());
    circleNutritionPresenter.run(display.getPermissionNutritionContainer());
    circleNutritionFoodsPresenter.run(display.getPermissionNutritionFoodsContainer());
    circleCardioPresenter.run(display.getPermissionCardioContainer());
    circleMeasurementsPresenter.run(display.getPermissionMeasurementsContainer());
    circleCoachPresenter.run(display.getPermissionCoachContainer());
	}

  
  @Override
  public void onStop() {

    if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
    if(circleTrainingPresenter != null) {
      circleTrainingPresenter.stop();
    }
    if(circleNutritionPresenter != null) {
      circleNutritionPresenter.stop();
    }
    if(circleNutritionFoodsPresenter != null) {
      circleNutritionFoodsPresenter.stop();
    }
    if(circleCardioPresenter != null) {
      circleCardioPresenter.stop();
    }
    if(circleMeasurementsPresenter != null) {
      circleMeasurementsPresenter.stop();
    }
    if(circleCoachPresenter != null) {
      circleCoachPresenter.stop();
    }
    unbindPresenters();
  }

}
