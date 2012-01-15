package com.delect.motiver.shared.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AliasTakenException extends ConnectionException implements IsSerializable {

  /**
   * 
   */
  private static final long serialVersionUID = 8266148651203440121L;

  private String alias;

  public AliasTakenException() {
    super();
  }

  public AliasTakenException(String alias) {
    this();
    this.alias = alias;
  }

  @Override
  public String toString() {
    return "AliasTakenException: alias "+alias+" already taken";
  }
}
