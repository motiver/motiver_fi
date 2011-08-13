/**
 * 
 */
package com.delect.motiver.shared;

/**
 * @author Antti
 *
 */
public interface Permission {
  
  /**
   * Permissions
   */
  public static final int READ_TRAINING = 0;
  public static final int READ_NUTRITION = 1;
  public static final int READ_NUTRITION_FOODS = 4;
  public static final int READ_CARDIO = 2;
  public static final int READ_MEASUREMENTS = 3;
  public static final int WRITE_TRAINING = 10;
  public static final int WRITE_NUTRITION = 11;
  public static final int WRITE_NUTRITION_FOODS = 14;
  public static final int WRITE_CARDIO = 12;
  public static final int WRITE_MEASUREMENTS = 13;
}
