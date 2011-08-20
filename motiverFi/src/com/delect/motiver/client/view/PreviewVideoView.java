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
package com.delect.motiver.client.view;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.presenter.PreviewVideoPresenter;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.shared.Constants;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.RowData;

/**
 * 
 * Init login view
 *  - sign in form
 *  - sign up form
 */
public class PreviewVideoView extends PreviewVideoPresenter.PreviewVideoDisplay {
	
	private Html video;
	private Window window = null;
	
	public PreviewVideoView() {
		this.setBorders(true);
		this.setSize(326, 268);
		this.setStyleAttribute("cursor", "pointer");
		
		this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				showVideoPopup();
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		
		Image img = new Image();
		if(!GXT.isIE) {
			img.setResource(MyResources.INSTANCE.videoPreview());
    }
		//ie hack
		else {
			img.setUrl(Constants.URL_APP_STATIC + "img/video_preview.png");
    }
		img.setSize("320px", "262px");
		this.add(img, new RowData(-1, -1, new Margins(2)));
		return this;
	}
	
	@Override
	public void onStop() {
		if(window != null) {
			if(video != null) {
				video.removeFromParent();
      }
				
			window.removeAll();
			if(window.isVisible()) {
				window.hide();
      }
			window = null;
		}
	}

	private void showVideoPopup() {
		window = new Window();
		window.setHeading("Motiver.fi");
		window.setModal(true);
		window.setResizable(false);
		window.setDraggable(false);
		window.setAutoHide(true);
		window.setSize(800, 640);
		window.addListener(Events.BeforeHide, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				if(video != null) {
					video.removeFromParent();
        }
			}
		});
		
		video = new Html("<iframe width=\"100%\" height=\"605\" src=\"http://www.youtube.com/embed/mxuQVZor9pU?hd=1&controls=1&rel=0&autoplay=1&autohide=1&showinfo=0&showsearch=0\" frameborder=\"0\" allowfullscreen></iframe>");
		window.add(video);
		
		window.show();
		
	}
}
