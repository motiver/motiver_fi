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
package com.delect.motiver.client.view.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.MicroNutrientLinkPresenter;
import com.delect.motiver.shared.MicroNutrientModel;
import com.delect.motiver.shared.util.CommonUtils;

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
			Text val = new Text(CommonUtils.convertNutritionValueFromDB(model.getValue() / 1000));
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
