/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.view;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;

public class MySpinnerField extends SpinnerField {

	public MySpinnerField() {		
		this.setAutoValidate(true);
		this.setValidationDelay(2000);
    this.addListener(Events.Invalid, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				try {
					if(getRawValue().contains(",")) {
						setValue( Double.parseDouble(getRawValue().replace(",", ".")) );
          }
				} catch (Exception e) {
				}
			}
		});
    this.addListener(Events.OnBlur, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				setFireChangeEventOnSetValue(false);
				setValue(getValue());
				setFireChangeEventOnSetValue(true);
			}
		});
	}
}
