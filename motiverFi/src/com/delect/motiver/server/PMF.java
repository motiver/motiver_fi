/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
  private static final PersistenceManagerFactory pmfInstance =
  JDOHelper.getPersistenceManagerFactory("transactions-optional");

  public static PersistenceManagerFactory get() {
    return pmfInstance;
  }

  private PMF() {}
}
