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
  public static final int WRITE_TRAINING = 4;
  public static final int WRITE_NUTRITION = 5;
  public static final int WRITE_NUTRITION_FOODS = 6;
  public static final int WRITE_CARDIO = 7;
  public static final int WRITE_MEASUREMENTS = 8;
  public static final int COACH = 9;
}
