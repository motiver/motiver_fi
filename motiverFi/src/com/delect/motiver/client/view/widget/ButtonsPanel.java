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
package com.delect.motiver.client.view.widget;

import com.delect.motiver.client.view.widget.MyButton.Style;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

/**
 * Vertical panel which shows 2+3 buttons (left + right side)
 * @author Antti
 */
public class ButtonsPanel extends LayoutContainer {

  public enum ButtonTarget {
    Cancel,         //left
    Back,           //left
    MoveToDate,     //right
    MoveToTarget,     //right
    Copy,           //right
    QuickSelection  //right
  }
  
  private boolean IsVisibleCancel = false;
  private boolean IsVisibleBack = false;
  private boolean IsVisibleMoveToDate = false;
  private boolean IsVisibleMoveToTarget = false;
  private boolean IsVisibleCopy = false;
  private boolean IsVisibleQuickSelection = false;
  
  public ButtonsPanel() {
    //set layout
    HBoxLayout layoutButtons = new HBoxLayout();
    layoutButtons.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layoutButtons);
    this.setHeight(28);
    
    //add spacer
    HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));  
    flex.setFlex(1);  
    this.add(new Text(), flex);
  }
  
  /**
   * Adds button
   * @param target button to be added
   * @param style
   * @param text
   * @return
   */
  public MyButton addButton(ButtonTarget target, Style style, String text) {

    boolean add = false;
    int position = 0;
    HBoxLayoutData data = new HBoxLayoutData(new Margins(0, 0, 0, 10));
    
    switch(target) {
      case Cancel:
        add = !IsVisibleCancel; //only add if not already added
        position = 0;
        data = new HBoxLayoutData(new Margins(0, 0, 0, 0));
        IsVisibleCancel = true;
        break;
      case Back:
        add = !IsVisibleBack; //only add if not already added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(position == 0) {
          data = new HBoxLayoutData(new Margins(0, 0, 0, 0));
        }
        IsVisibleBack = true;
        break;
      case MoveToDate:
        add = !IsVisibleMoveToDate; //only add if not already added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer

        IsVisibleMoveToDate = true;
        break;
      case MoveToTarget:
        add = !IsVisibleMoveToTarget; //only add if not already added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer
        if(IsVisibleMoveToDate) position++;

        IsVisibleMoveToTarget = true;
        break;
      case Copy:
        add = !IsVisibleCopy; //only add if not already added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer
        if(IsVisibleMoveToDate) position++;
        if(IsVisibleMoveToTarget) position++;

        IsVisibleCopy = true;
        break;
      case QuickSelection:
        add = !IsVisibleQuickSelection; //only add if not already added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer
        if(IsVisibleMoveToDate) position++;
        if(IsVisibleMoveToTarget) position++;
        if(IsVisibleCopy) position++;

        IsVisibleQuickSelection = true;
        break;
    }
    
    //add button
    MyButton btn = new MyButton();
    if(add) {
      btn.setScale(ButtonScale.MEDIUM);
      btn.setColor(style);
      btn.setText(text);
      insert(btn, position, data);
      layout();
    }
    //else return old button
    else {
      btn = (MyButton)this.getItem(position);
      btn.removeAllListeners();
    }
    
    return btn;
  }
  
  public void removeButton(ButtonTarget target) {
    int position = 0;
    boolean remove = false;
    
    switch(target) {
      case Cancel:
        remove = IsVisibleCancel; //only remove if added
        position = 0;
        IsVisibleCancel = false;
        break;
      case Back:
        remove = IsVisibleBack; //only remove if added
        position = (IsVisibleCancel)? 1 : 0;  //if cancel button is visible -> add to "second place"
        IsVisibleBack = false;
        break;
      case MoveToDate:
        remove = IsVisibleMoveToDate; //only remove if added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer

        IsVisibleMoveToDate = false;
        break;
      case MoveToTarget:
        remove = IsVisibleMoveToTarget; //only remove if added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer
        if(IsVisibleMoveToDate) position++;

        IsVisibleMoveToTarget = false;
        break;
      case Copy:
        remove = IsVisibleCopy; //only remove if added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer
        if(IsVisibleMoveToDate) position++;
        if(IsVisibleMoveToTarget) position++;

        IsVisibleCopy = false;
        break;
      case QuickSelection:
        remove = IsVisibleQuickSelection; //only remove if added
        //calculate how many buttons are before this
        if(IsVisibleCancel) position++;
        if(IsVisibleBack) position++;
        position++; //spacer
        if(IsVisibleMoveToDate) position++;
        if(IsVisibleMoveToTarget) position++;
        if(IsVisibleCopy) position++;

        IsVisibleQuickSelection = false;
        break;
    }
    
    if(remove) {
      remove(getItem(position));
    }
  }
}
