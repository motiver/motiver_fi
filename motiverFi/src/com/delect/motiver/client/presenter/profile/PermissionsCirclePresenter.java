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
package com.delect.motiver.client.presenter.profile;

import java.util.ArrayList;
import java.util.List;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.UserRemovedEvent;
import com.delect.motiver.client.event.handler.UserRemovedEventHandler;
import com.delect.motiver.client.presenter.EmptyPresenter;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.presenter.UserPresenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.shared.UserModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;

public class PermissionsCirclePresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class PermissionsCircleDisplay extends Display {
	  public abstract LayoutContainer getBodyContainer();
	  public abstract void setTarget(int target);
	  public abstract void setHandler(PermissionsCircleHandler handler);
	  public abstract void setAllUsersEnabled(boolean enabled);
	}
	public interface PermissionsCircleHandler {
	  void newUser(UserModel user);
	  /**
	   * If all users are included in this circle
	   * @param enable
	   */
	  void setEnableAll(boolean enabled);
	}

	private PermissionsCircleDisplay display;
	
  private EmptyPresenter emptyPresenter;
  private List<UserPresenter> userPresenters = new ArrayList<UserPresenter>();
  int target;
  
	/**
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 * @param target
	 */
	public PermissionsCirclePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, PermissionsCircleDisplay display, int target) {
		super(rpcService, eventBus);
		this.display = display;
		
		this.target = target;		
	}
	
	@Override
	public Display getView() {
		return display;
	}
	
	@Override
	public void onBind() {
	  display.setTarget(target);
	  display.setHandler(new PermissionsCircleHandler() {
      @Override
      public void newUser(final UserModel user) {
        System.out.println("User added: "+user.getEmail());
        
        display.setContentEnabled(false);
        
        //add to db
        final Request req = rpcService.addUserToCircle(target, user.getUid(), new MyAsyncCallback<Boolean>() {
          @Override
          public void onSuccess(Boolean result) {
            
            display.setContentEnabled(true);
            
            //add new presenter
            if(result)
              addNewPresenter(user);
          }
        });
        addRequest(req);
      }
      @Override
      public void setEnableAll(boolean enabled) {
        //enable all
        if(enabled) {
          
          //add to db
          final Request req = rpcService.addUserToCircle(target, "-1", new MyAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
          });
          addRequest(req);
        }
        //disable all users
        else {
          //remove from server
          final Request req = rpcService.removeUserFromCircle(target, "-1", new MyAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
            }
          });
          addRequest(req);
        }
      }
	  });
	  
	  //EVENT: user removed
	  addEventHandler(UserRemovedEvent.TYPE, new UserRemovedEventHandler() {
      @Override
      public void onUserRemoved(UserRemovedEvent event) {

        //find from array
        for(int i=0; i<userPresenters.size(); i++) {
          final UserPresenter p = userPresenters.get(i);

          //found correct user from this circle
          if(p.equals(event.getSource())) {
            display.setContentEnabled(false);
            
            //remove from server
            final Request req = rpcService.removeUserFromCircle(target, event.getUser().getUid(), new MyAsyncCallback<Boolean>() {
              @Override
              public void onSuccess(Boolean result) {
                display.setContentEnabled(true);
              }
            });
            addRequest(req);
        
            //remove from array
            userPresenters.remove(i);
            
            //if no users
            if(userPresenters.size() == 0) {
              emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoUsersAdded(), EmptyPresenter.OPTION_SMALLER);
              emptyPresenter.run(display.getBodyContainer());
            }
            
            break;
          }
        }
      }
	  });
	}

	/**
   * Loads users based on query word
   */
  protected void loadUsers() {

    if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
    unbindPresenters();
    
    //show loading
    emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), EmptyPresenter.EMPTY_LOADING);
    emptyPresenter.run(display.getBodyContainer());
    
    final Request req = rpcService.getUsersFromCircle(target, new MyAsyncCallback<List<UserModel>>() {
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
      
      //if no users
      if(list.size() == 0) {
        emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay)GWT.create(EmptyView.class), AppController.Lang.NoUsersAdded(), EmptyPresenter.OPTION_SMALLER);
        emptyPresenter.run(display.getBodyContainer());
      }
      else {

        display.setAllUsersEnabled(false);
        for(UserModel m : list) {
          String id = m.getUid();

          //if enabled permissions for all users, uid is "-1"
          if(m.getUid().equals("-1")) {
            display.setAllUsersEnabled(true);
          }
          else {
            addNewPresenter(m);
          }
        }
          
      }
      
    } catch (Exception e) {
      Motiver.showException(e);
    }
  }

  /**
   * @param p
   */
  private void addNewPresenter(UserModel user) {
    
    if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
    
    final UserPresenter presenter = new UserPresenter(rpcService, eventBus, (UserDisplay)GWT.create(UserView.class), user, true);
    presenter.showDeleteIcon();
    userPresenters.add(presenter);
    presenter.run(display.getBodyContainer());
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
    loadUsers();
	}
  
  @Override
  public void onStop() {

    if(emptyPresenter != null) {
      emptyPresenter.stop();
    }
    //stop presenters
    unbindPresenters();
  }

}
