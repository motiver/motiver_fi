/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.event.handler;

import com.google.gwt.event.shared.EventHandler;

import com.delect.motiver.client.event.CommentRemovedEvent;

public interface CommentRemovedEventHandler extends EventHandler {
  void onCommentRemoved(CommentRemovedEvent event);
}
