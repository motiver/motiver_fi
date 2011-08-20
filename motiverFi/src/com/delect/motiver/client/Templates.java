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
package com.delect.motiver.client;

public class Templates {

  /**
  * Exercise name template for combobox
  * @return
  */
  public static native String getExerciseNameTemplate() /*-{ 
    return  [ 
      '<tpl for=".">', 
      '<div class="x-combo-list-item">', 
      //exercise found
      '<tpl if="id != -1">',
      '<table width=\'100%\'><tr>', 
      '<td>', 
      '{values.fn}',  
      '</td>',
      '<td align=right valign=middle>',
      //video
      '<tpl if="values.v!=\'\'"><div class=\'icon-list-video\'></div></tpl>',
      '<tpl if="values.v==\'\'"><div style=\'height:32px;\'></div></tpl>',
      ' </td>',
      '</tr></table>',
      '</tpl>',
      '<tpl if="id == -1">',
      '<table width=\'100%\'><tr>', 
      '<td>', 
      '{values.fn}',
      '</td>',
      '<td align=right valign=middle>',
      '<tpl if="v==\'\'"><div style=\'height:32px;\'></div></tpl>',
      ' </td>',
      '</tr></table>',
      '</tpl>',
    	    
      '</div>',
      '</tpl>' 
    ].join(""); 
  }-*/; 
    

  /**
  * Food name template for combobox
  * @return
  */
  public static native String getFoodNameTemplate() /*-{ 
    return  [ 
      '<tpl for=".">', 
      '<div class="x-combo-list-item">', 
    	    
      //food found
      '<tpl if="values.id != -1">',
      '<table width=\'100%\'><tr>', 
      '<td>', 
      '<h3>{values.n}</h3>', 
      '<p>Energy: <i>{values.e} kcal</i><br>', 
      'P: <i>{values.p} g</i>&nbsp;|&nbsp;', 
      'C: <i>{values.c} g</i>&nbsp;|&nbsp;', 
      'F: <i>{values.f} g</i></p>',
      '</td>',
      '<td align=right valign=middle>',
      '<div class=\'icon-list-{values.uid}\'></div>',
      '</td>',
      '</tr></table>',
      '</tpl>',
      '<tpl if="values.id == -1">',
      '<table width=\'100%\'><tr>', 
      '<td>', 
      '{values.n}',
      '</td>',
      '<td align=right valign=middle>',
      '<div style=\'height:32px;\'></div>',
      ' </td>',
      '</tr></table>',
      '</tpl>',
    	    
      '</div>',
      '</tpl>' 
    ].join(""); 
  }-*/; 
}
