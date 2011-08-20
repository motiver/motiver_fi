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
package com.delect.motiver.client.view.profile;

import com.google.gwt.user.client.ui.Widget;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.presenter.profile.InBodyPresenter;
import com.delect.motiver.client.presenter.profile.InBodyPresenter.InBodyHandler;
import com.delect.motiver.client.view.MySpinnerField;

import com.extjs.gxt.ui.client.widget.layout.AbsoluteData;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;

public class InBodyView extends InBodyPresenter.InBodyDisplay {

	static MySpinnerField getSpinnerField(double value) {

		MySpinnerField sf = new MySpinnerField();
		sf.setWidth(70);
		sf.setValue(value);
		
		return sf;
	}
	
	public InBodyView() {
		
		setLayout(new AbsoluteLayout());
		this.setWidth(950);
		this.setHeight(1307);
	}

	@Override
	public Widget asWidget() {

		try {
			//set background
//			Image imgInBody = new Image(MyResources.INSTANCE.inBody());
//			this.add(imgInBody, new AbsoluteData(0, 0));
			
			//1.
			MySpinnerField nf1 = getSpinnerField(0);
			this.add(nf1, new AbsoluteData(132, 175));
			//2.
			MySpinnerField nf2 = getSpinnerField(0);
			this.add(nf2, new AbsoluteData(132, 205));
			//3.
			MySpinnerField nf3 = getSpinnerField(0);
			this.add(nf3, new AbsoluteData(132, 235));
			//4.
			MySpinnerField nf4 = getSpinnerField(0);
			this.add(nf4, new AbsoluteData(132, 265));
			//5.
			MySpinnerField nf5 = getSpinnerField(0);
			this.add(nf5, new AbsoluteData(132, 295));

			//6.
			MySpinnerField nf6 = getSpinnerField(0);
			this.add(nf6, new AbsoluteData(215, 195));
			//7.
			MySpinnerField nf7 = getSpinnerField(0);
			this.add(nf7, new AbsoluteData(295, 215));
			//8.
			MySpinnerField nf8 = getSpinnerField(0);
			this.add(nf8, new AbsoluteData(375, 230));
			//9.
			MySpinnerField nf9 = getSpinnerField(0);
			this.add(nf9, new AbsoluteData(455, 245));
			//10.
			MySpinnerField nf10 = getSpinnerField(0);
			this.add(nf10, new AbsoluteData(265, 280));
			
			//11.
			MySpinnerField nf11 = getSpinnerField(0);
			this.add(nf11, new AbsoluteData(460, 390));
			//12.
			MySpinnerField nf12 = getSpinnerField(0);
			this.add(nf12, new AbsoluteData(460, 425));
			//13.
			MySpinnerField nf13 = getSpinnerField(0);
			this.add(nf13, new AbsoluteData(460, 450));
			
			//14.
			MySpinnerField nf14 = getSpinnerField(0);
			this.add(nf14, new AbsoluteData(460, 547));
			//15.
			MySpinnerField nf15 = getSpinnerField(0);
			this.add(nf15, new AbsoluteData(460, 582));
			//16.
			MySpinnerField nf16 = getSpinnerField(0);
			this.add(nf16, new AbsoluteData(460, 615));
			
			//17.
			MySpinnerField nf17 = getSpinnerField(0);
			this.add(nf17, new AbsoluteData(350, 710));
			//18.
			MySpinnerField nf18 = getSpinnerField(0);
			this.add(nf18, new AbsoluteData(350, 766));
			//19.
			MySpinnerField nf19 = getSpinnerField(0);
			this.add(nf19, new AbsoluteData(350, 822));
			//20.
			MySpinnerField nf20 = getSpinnerField(0);
			this.add(nf20, new AbsoluteData(350, 878));
			//21.
			MySpinnerField nf21 = getSpinnerField(0);
			this.add(nf21, new AbsoluteData(350, 925));
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return this;
	}

	@Override
	public void setHandler(InBodyHandler handler) {}

}
