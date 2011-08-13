/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.StringConstants;
import com.delect.motiver.client.presenter.nutrition.GuideValuePresenter;
import com.delect.motiver.client.presenter.nutrition.GuideValuePresenter.GuideValueHandler;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.GuideValueModel;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;


public class GuideValueView extends GuideValuePresenter.GuideValueDisplay {

	private GuideValueHandler handler;
	private GuideValueModel value;
	
	
	public GuideValueView() {
		this.setStyleName("panel-guidevalue");
	}
	
	@Override
	public Widget asWidget() {

		this.setAutoHeight(true);
		
		LayoutContainer thisContent = new LayoutContainer();
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
    thisContent.setLayout(layout);
		thisContent.setAutoHeight(true);
		
		if(value.getId() == 0) {
			thisContent.add(new Label(AppController.Lang.Loading()));
			
			this.add(thisContent);
			return this;
		}
		
		try {		                         

			final DateTimeFormat fmt = DateTimeFormat.getFormat( StringConstants.DATEFORMATS[AppController.User.getDateFormat()] );
			LayoutContainer panelName = new LayoutContainer();
			panelName.setLayout(new RowLayout());
			//name
			Text name = new Text(value.getName());
			name.setWidth(200);
			name.setStyleName("label-title-medium");
			panelName.add(name, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
			
			//dates
			Text textDates = new Text();
			textDates.setText( fmt.format(value.getDateStart()) + " - " + fmt.format(value.getDateEnd()) );
			panelName.add(textDates, new RowData(-1, -1, new Margins(0, 0, 10, 0)));
			
			thisContent.add(panelName);
			
			//training
			LayoutContainer panelTraining = getContentPanel(true);
			thisContent.add(panelTraining, new HBoxLayoutData(new Margins(0, 0, 0, 30)));
			
			//rest
			LayoutContainer panelRest = getContentPanel(false);
			thisContent.add(panelRest, new HBoxLayoutData(new Margins(0, 0, 0, 30)));
			
			//spacer
			HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 0, 0, 5));
      flex.setFlex(1);  
      thisContent.add(new Text(), flex);  
	        
      //remove and check icon
      LayoutContainer lc = new LayoutContainer();
      VBoxLayout layoutLc = new VBoxLayout();
      layoutLc.setVBoxLayoutAlign(VBoxLayoutAlign.RIGHT);
      lc.setHeight(60);
      lc.setLayout(layoutLc);
	        
      //remove value link
			Anchor linkRemove = new Anchor();
			linkRemove.setText(AppController.Lang.RemoveTarget(""));
			linkRemove.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					MessageBox.confirm(AppController.Lang.Confirm(), AppController.Lang.RemoveConfirm(AppController.Lang.ThisValue().toLowerCase()), new Listener<MessageBoxEvent>() {   
						public void handleEvent(MessageBoxEvent ce) {
							Button btn = ce.getButtonClicked();
							if(Dialog.YES.equals(btn.getItemId())) {
								handler.valueRemoved();
							}
		        }   
					});  
				}
			});
			lc.add(linkRemove, new VBoxLayoutData(new Margins(0, 0, 10, 0)));
			
			//icon (if guide value is "on")
			Date now = new Date();
			long d1 = Functions.stripTime(now, true).getTime();
			long d2 = Functions.stripTime(now, false).getTime();
			if((value.getDateStart().getTime() <= d2 && value.getDateEnd().getTime() >= d1) || (value.getDateEnd().getTime() >= d1 && value.getDateStart().getTime() <= d2) ) {
				Image imgDone = new Image(MyResources.INSTANCE.done());
				lc.add(imgDone, new VBoxLayoutData(new Margins(0, 0, 0, 0)));
			}
			thisContent.add(lc, new HBoxLayoutData(new Margins(0, 0, 0, 0)));
			
			this.add(thisContent);
			thisContent.layout();
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setHandler(GuideValueHandler handler) {
		this.handler = handler;
	}
	@Override
	public void setModel(GuideValueModel value) {
		this.value = value;
	}

	/**
	 * Returns panel with values
	 * @param isTraining
	 * @return
	 */
	private LayoutContainer getContentPanel(boolean isTraining) {
		
		LayoutContainer lc = new LayoutContainer();
		lc.setLayout(new RowLayout());
		Text labelTitle = new Text();
		if(isTraining) {
			labelTitle.setText(AppController.Lang.TrainingDay());
    }
		else {
			labelTitle.setText(AppController.Lang.RestDay());
    }
		labelTitle.setStyleName("label-title-small");
		lc.add(labelTitle, new RowData(-1, -1, new Margins(0, 0, 5, 0)) );
		lc.add( new Text(AppController.Lang.Energy() + ": " + value.getEnergy(isTraining) + " kcal"), new RowData(-1, -1, new Margins(0, 0, 3, 0)) );
		String unit = (value.isPercent())? "%" : "g";
		lc.add( new Text(AppController.Lang.Protein() + ": " + value.getProtein(isTraining) + " " + unit), new RowData(-1, -1, new Margins(0, 0, 3, 0)) );
		lc.add( new Text(AppController.Lang.Carbohydrates() + ": " + value.getCarbs(isTraining) + " " + unit), new RowData(-1, -1, new Margins(0, 0, 3, 0)) );
		lc.add( new Text(AppController.Lang.Fet() + ": " + value.getFet(isTraining) + " " + unit), new RowData(-1, -1, new Margins(0)) );
		
		return lc;
	}
	
}
