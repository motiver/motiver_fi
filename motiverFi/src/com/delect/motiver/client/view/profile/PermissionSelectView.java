/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.profile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter;
import com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectHandler;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class PermissionSelectView extends PermissionsSelectPresenter.PermissionsSelectDisplay {

	private PermissionsSelectHandler handler;
	
	public PermissionSelectView() {		
	}
	
	@Override
	public Widget asWidget() {

	  return this;
	}

	@Override
	public void setHandler(PermissionsSelectHandler handler) {
		this.handler = handler;
	}

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getUsersContainer()
   */
  @Override
  public LayoutContainer getUsersContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getPermissionTrainingContainer()
   */
  @Override
  public LayoutContainer getPermissionTrainingContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getPermissionNutritionContainer()
   */
  @Override
  public LayoutContainer getPermissionNutritionContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getPermissionNutritionFoodsContainer()
   */
  @Override
  public LayoutContainer getPermissionNutritionFoodsContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getPermissionCardioContainer()
   */
  @Override
  public LayoutContainer getPermissionCardioContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getPermissionMeasurementsContainer()
   */
  @Override
  public LayoutContainer getPermissionMeasurementsContainer() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.delect.motiver.client.presenter.profile.PermissionsSelectPresenter.PermissionsSelectDisplay#getPermissionCoachContainer()
   */
  @Override
  public LayoutContainer getPermissionCoachContainer() {
    // TODO Auto-generated method stub
    return null;
  }

}
