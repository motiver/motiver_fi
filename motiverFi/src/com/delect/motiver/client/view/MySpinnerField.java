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

import com.delect.motiver.shared.util.CommonUtils;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;

public class MySpinnerField extends SpinnerField {

	public MySpinnerField() {		
		this.setAutoValidate(true);
		this.setValidationDelay(2000);
    CommonUtils.setWarningMessages(this);
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
	
	@Override public void setMaxValue(Number maxValue) {
	  super.setMaxValue(maxValue);
	  
    CommonUtils.setWarningMessages(this);
	}
  
  @Override public void setMinValue(Number minValue) {
    super.setMinValue(minValue);
    
    CommonUtils.setWarningMessages(this);
  }
	
}
