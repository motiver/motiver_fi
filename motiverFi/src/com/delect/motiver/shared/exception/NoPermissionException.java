/**
 * 
 */
package com.delect.motiver.shared.exception;

/**
 * @author Antti
 *
 */
public class NoPermissionException extends Exception {

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
