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
package com.delect.motiver.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Request;
import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.presenter.EmptyPresenter.EmptyDisplay;
import com.delect.motiver.client.presenter.UserPresenter.UserDisplay;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.client.view.EmptyView;
import com.delect.motiver.client.view.UserView;
import com.delect.motiver.shared.UserModel;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/** Shows user's friends.
 */
public class FriendsListPresenter extends Presenter {

  /**
   * Abstract class for view to extend
   */
  public abstract static class FriendsListDisplay extends Display {
    /**
     * Returns container for friends.
     * @return LayoutContainer
     */
    public abstract LayoutContainer getFriendsContainer();
  }

  private final FriendsListDisplay display;

  private EmptyPresenter emptyPresenter;
  // child presenters
  private final List<Presenter> userPresenters = new ArrayList<Presenter>();

  /**
   * 
   * @param rpcService
   * @param eventBus
   * @param display
   */
  public FriendsListPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus,
  FriendsListDisplay display) {
    super(rpcService, eventBus);
    this.display = display;

  }

  @Override
  public Display getView() {
    return display;
  }

  @Override
  public void onBind() {
  }

  @Override
  public void onRun() {

    // add empty presenter
    emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay) GWT.create(EmptyView.class),
    EmptyPresenter.EMPTY_LOADING_SMALL);
    emptyPresenter.run(display.getFriendsContainer());

    Motiver.setNextCallCacheable(true);
    final Request req = rpcService.getFriends(new MyAsyncCallback<List<UserModel>>() {
      @Override
      public void onSuccess(List<UserModel> result) {
        showFriends(result);
      }
    });
    addRequest(req);
  }

  @Override
  public void onStop() {
    unbindPresenters();
  }

  /**
   * Shows friends (multiple UserPresenters)
   * 
   * @param list : UserModels
   */
  private void showFriends(List<UserModel> list) {

    try {
      unbindPresenters();

      // if no workouts
      if (list.size() == 0) {
        emptyPresenter = new EmptyPresenter(rpcService, eventBus, (EmptyDisplay) GWT.create(EmptyView.class), AppController.Lang.NoFriends(), EmptyPresenter.OPTION_SMALLER_LEFT_ALIGN);
        emptyPresenter.run(display.getFriendsContainer());
      } else {

        Collections.sort(list);

        for (final UserModel m : list) {
          // new presenter
          final UserPresenter wp = new UserPresenter(rpcService, eventBus, (UserDisplay) GWT.create(UserView.class), m, true);
          addNewPresenter(wp);

        }
      }

    } catch (Exception e) {
      Motiver.showException(e);
    }
  }

  /**
   * Unbinds all the presenters
   */
  private void unbindPresenters() {
    if (emptyPresenter != null) {
      emptyPresenter.stop();
    }
    if (userPresenters != null) {
      for (int i = 0; i < userPresenters.size(); i++) {
        final Presenter presenter = userPresenters.get(i);
        if (presenter != null) {
          presenter.stop();
        }
      }
      userPresenters.clear();
    }
  }

  /**
   * Adds new presenter to view
   * 
   * @param presenter
   */
  protected void addNewPresenter(Presenter presenter) {

    try {
      // remove emptypresenter if present
      if (emptyPresenter != null) {
        emptyPresenter.stop();
        emptyPresenter = null;
      }

      userPresenters.add(presenter);
      presenter.run(display.getFriendsContainer());
      
    } catch (final Exception e) {
      Motiver.showException(e);
    }
  }
}
