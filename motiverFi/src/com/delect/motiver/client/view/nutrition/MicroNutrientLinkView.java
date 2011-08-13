/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.MicroNutrientLinkPresenter;
import com.delect.motiver.shared.Functions;
import com.delect.motiver.shared.MicroNutrientModel;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class MicroNutrientLinkView extends MicroNutrientLinkPresenter.MicroNutrientLinkDisplay {

	private MicroNutrientModel model;
	
	public MicroNutrientLinkView() {
		this.setStyleName("panel-micronutrientlink");

		//layout
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
	}
	
	@Override
	public Widget asWidget() {
		
		try {

			//name
			String str = AppController.Lang.NoName();
			if(model.getNameId() < AppController.LangConstants.MicroNutrients().length) {
				str = AppController.LangConstants.MicroNutrients()[model.getNameId()];
      }
			Text name = new Text(str + ":");
			this.add(name, new HBoxLayoutData(new Margins(0, 5, 0, 0)));
			
			//value
			Text val = new Text(Functions.convertNutritionValueFromDB(model.getValue() / 1000));
			this.add(val, new HBoxLayoutData(new Margins(0, 5, 0, 0)));

		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setModel(MicroNutrientModel model) {
		this.model = model;
	}
	
}
