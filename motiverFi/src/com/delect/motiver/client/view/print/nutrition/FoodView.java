/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view.print.nutrition;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter;
import com.delect.motiver.client.presenter.nutrition.FoodPresenter.FoodHandler;
import com.delect.motiver.shared.FoodModel;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class FoodView extends FoodPresenter.FoodDisplay {

	private FoodModel food;
	
	/**
	 * Meal view
	 */
	public FoodView() {
		this.setStyleName("panel-food");
		
		HBoxLayout layout = new HBoxLayout();
    layout.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
    this.setLayout(layout);
    this.setAutoHeight(true);
    this.setAutoWidth(true);
		
	}
	
	@Override
	public Widget asWidget() {

		try {
			//name
			final String name = (food.getName() != null)? food.getName().getName() + ": " : " ";
			Text textName = new Text(name);
			textName.setWidth(200);
			this.add(textName, new HBoxLayoutData(new Margins(0, 20, 0, 10)));
			
			//amount
			this.add(new Text(food.getAmount() + " g"), new HBoxLayoutData(new Margins(0, 10, 0, 0)));
			
			//portions
			if(food.getName() != null) {
				String text = "";
				double por = food.getName().getPortion();
				if(por == 0) {
					por = 100;
        }

				//if equal number
				double left = (((food.getAmount() / por) * 4) % 2);
				
				if(left == 0 && food.getName().getPortion() > 0) {
					double val = food.getAmount() / por;
					//one piece
					if(val == 1) {
						text = AppController.Lang.PortionsOne();
          }
					else if(val == 0) {
						text = "";
          }
					else {
						text = AppController.Lang.Portions(NumberFormat.getFormat("0.#").format((food.getAmount() / por)));
          }

					Text labelPortions = new Text(text);
					labelPortions.setStyleName("label-portions");
					this.add(labelPortions);
				}
				
			}
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setHandler(FoodHandler handler) {}

	@Override
	public void setModel(FoodModel food) {
		this.food = food;
	}

	@Override
	public void setNameComboEnabled(boolean enabled) {}	
	
}
