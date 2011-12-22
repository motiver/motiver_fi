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
package com.delect.motiver.client.view.css3.nutrition;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.AppController;
import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.view.css3.FlexBox;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Text;


public class FoodView extends com.delect.motiver.client.view.nutrition.FoodView {

	private FlexBox flexBox = new FlexBox();

	
	/**
	 * Meal view
	 */
	public FoodView() {
		this.setStyleName("panel-food");
		
		flexBox.setWidth("100%");
    this.add(flexBox);
		
		//show hide header buttons based on mouse position
		this.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				addStyleName("panel-food-active");
				panelButtons.setVisible(true);
			}
		});
		this.addListener(Events.OnMouseOut, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				removeStyleName("panel-food-active");
				panelButtons.setVisible(false);
			}
		});
	}
	
	@Override
	public Widget asWidget() {

		try {
			
			//food selection combo
			if(food.getUid().equals(AppController.User.getUid())) {
				comboName = addFoodCombo();
				containerName.add(comboName);
				flexBox.add(containerName, -1, 10);
			}
			else {
				Text textName = new Text();
				if(food.getName() != null) {
					textName.setText( food.getName().getName() );
        }
				textName.addStyleName("field-readonly");
				textName.setStyleAttribute("text-align", "left");
				textName.setWidth(325);
				flexBox.add(textName, -1, 10);
			}

			//amount
      flexBox.add(getSpinAmount(), -1, 10);

			//portion
			labelPortions.setText("-");
			labelPortions.setStyleName("label-portions");
			flexBox.add(labelPortions, -1, -1);
			updatePortions(food.getAmount());

      flexBox.add(getPanelButtons(), -1, -1);
						
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		panelButtons.setVisible(false);
		
		return this;
	}
}
