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
/**
 * 
 */
package com.delect.motiver.shared.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Antti
 *
 */
public class NoPermissionException extends ConnectionException implements IsSerializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2133135177551970816L;
  private int target;
  private String ourUid;
  private String targetUid;

  public NoPermissionException(int target, String ourUid, String targetUid) {
    this.target = target;
    this.ourUid = ourUid;
    this.targetUid = targetUid;
  }
  
  @Override
  public String getMessage() {
    StringBuilder builder = new StringBuilder();
    builder.append("No permission for target ");
    builder.append(target);
    builder.append(", ourUID: ");
    builder.append(ourUid);
    builder.append(", targetUid: ");
    builder.append(targetUid);
    
    return builder.toString();
  }
}
