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
package com.delect.motiver.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.delect.motiver.client.event.handler.CardioValueRemovedEventHandler;
import com.delect.motiver.shared.CardioValueModel;

public class CardioValueRemovedEvent extends GwtEvent<CardioValueRemovedEventHandler> {
	
  public static Type<CardioValueRemovedEventHandler> TYPE = new Type<CardioValueRemovedEventHandler>();
  private final CardioValueModel model;


  public CardioValueRemovedEvent(CardioValueModel model) {
    this.model = model;
  }
	  
  @Override
  public Type<CardioValueRemovedEventHandler> getAssociatedType() {
    return TYPE;
  }
	  
  public CardioValueModel getCardioValue() {
		return model;  
  }

  @Override
  protected void dispatch(CardioValueRemovedEventHandler handler) {
    handler.onCardioValueRemoved(this);
  }
}




