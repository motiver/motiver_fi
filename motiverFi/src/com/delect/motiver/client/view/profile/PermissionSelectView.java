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
