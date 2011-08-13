/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
/*
 * Shows timeselect window below given textfield
 */
package com.delect.motiver.client.view;

import java.util.Date;

import com.google.gwt.user.client.Event;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.shared.Functions;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
/**
 * Shows timeselect textfield
 */
public class TimeSelectFieldView extends TextField<String> {

	public interface TimeSelectFieldHandler {
		void timeChanged(int time);
	}

	private TimeSelectFieldHandler handler;
	
	//widgets
	private Text[] labelHours = new Text[24];
	private Listener<ComponentEvent> listenerLabelMouseOut = new Listener<ComponentEvent>() {
		@Override
		public void handleEvent(ComponentEvent be) {
			if(be.getComponent() instanceof Text) {
				Text l = ((Text)be.getComponent());
				l.removeStyleName("label-time-select-sel");
			}
			setValue(Functions.getTimeToString(origValue));
		}								
	};

  private int Hours = 0;
	private int Minutes = 0;
	
	private int origValue = 0;
	private HorizontalPanel pMinutesBottom;
	private HorizontalPanel pMinutesTop;
	private Popup popup = null;
	
	//Parameters: textfield containing time
	@SuppressWarnings("deprecation")
	public TimeSelectFieldView(int time, TimeSelectFieldHandler handler) {

		this.handler = handler;
		
		this.setWidth(65);
		this.setReadOnly(true);
		this.setValue(Functions.getTimeToString(time));
		this.setMessageTarget("none");
		
		origValue = time;
		
		//parse orig value
		try {
			Date d = new Date(time * 1000);
			Hours = d.getHours();
			Minutes = d.getMinutes();
		} catch (Exception e) {
		}

		//open popup when clicked
		this.addListener(Events.OnClick, new Listener<ComponentEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(ComponentEvent be) {
				try {
					if(popup == null) {
						popup = new Popup();
						popup.setWidth("370px");
						popup.setHeight("105px");
						
						initPopup();
					}
					
					final TextField<String> tf = (TextField<String>)be.getSource();
					popup.show(tf.getElement(), "", new int[] {3, 22} );

				} catch (Exception e) {
		      Motiver.showException(e);
				}
			}
		});
		sinkEvents(Event.ONCLICK);
	}
	
	private void initPopup() {
		LayoutContainer panelContent = new LayoutContainer();

		//bottom minutes
		pMinutesTop = new HorizontalPanel();
		pMinutesTop.setHeight(20);
		pMinutesBottom = new HorizontalPanel();

		//add minutes to top
		panelContent.add(pMinutesTop);
		
		//add times (before 12)
		final LayoutContainer panel1 = panelHours(true, 0, 11);
		panelContent.add(panel1);
		//add times (after 12)
		final LayoutContainer panel2 = panelHours(false, 12, 23);
		panelContent.add(panel2);
		
		//add minutes to bottom
		panelContent.add(pMinutesBottom);
		panelContent.layout();
		popup.add(panelContent);

		//reset orig value when popup is hidden
		popup.addListener(Events.Hide, listenerLabelMouseOut);
	}

	protected void setHours(boolean setValue, int hours) {
		try {
			Hours = hours;

			int seconds = Hours * 3600 + Minutes * 60;
			setValue(Functions.getTimeToString(seconds));
			
			if(setValue) {
				origValue = seconds;

				popup.hide();
				popup = null;

				//fire time selected event
				if(handler != null) {
				  handler.timeChanged( seconds );
				}
			}
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/*
	 * Creates hours panel
	 * Parameters: which minutes are shown, hours min, max
	 */
	LayoutContainer panelHours(final boolean showTopMinutes, int start, int end) {
		final LayoutContainer panelHour = new LayoutContainer();
		panelHour.setLayout(new TableLayout(12));
		for(int i=start; i <= end; i++) {
			Text l = new Text(((i < 10)? "0" : "") + i);
			l.setStyleName("label-time-select");
			l.setData("h", i);
			labelHours[i] = l;

			l.addListener(Events.OnMouseOver, new Listener<ComponentEvent>() {
				@Override
				public void handleEvent(ComponentEvent be) {
					Text l = ((Text)be.getComponent());
					l.addStyleName("label-time-select-sel");
					int x = l.getAbsoluteLeft() - panelHour.getAbsoluteLeft();
					if(showTopMinutes) {
						pMinutesTop.removeAll();	
						pMinutesTop.add(panelMinutes());
						pMinutesTop.setStyleAttribute("margin-left", x + "px");
						pMinutesTop.layout();
						pMinutesBottom.removeAll();	
						pMinutesBottom.layout();
					}
					else {
						pMinutesBottom.removeAll();	
						pMinutesBottom.add(panelMinutes());
						pMinutesBottom.setStyleAttribute("margin-left", x + "px");
						pMinutesBottom.layout();
						pMinutesTop.removeAll();
						pMinutesTop.layout();
					}
					
					//remove style from other hours
					for(int k=0; k < labelHours.length; k++) {
						if(!labelHours[k].equals(l)) {
							labelHours[k].removeStyleName("label-time-select-sel");
            }
					}

					setHours(false, Integer.parseInt(be.getComponent().getData("h").toString()));
				}								
			});
			l.addListener(Events.OnMouseUp, new Listener<ComponentEvent>() {
				@Override
				public void handleEvent(ComponentEvent be) {
					setHours(true, Integer.parseInt(be.getComponent().getData("h").toString()));
				}								
			});
			l.sinkEvents(Event.MOUSEEVENTS);
			
			panelHour.add(l);
		}
		
		return panelHour;
	}

	
	/*
	 * Creates minutes panel
	 */
	LayoutContainer panelMinutes() {
		//top minutes
		final LayoutContainer panelMinute = new LayoutContainer();
		panelMinute.setLayout(new TableLayout(12));
		
		for(int i=0; i <= 55; i+=5) {
			Text l1 = new Text(((i < 10)? "0":"") + i);
			l1.setStyleName("label-time-select-min");
			l1.setData("m", i);
			l1.addListener(Events.OnMouseOver, new Listener<ComponentEvent>() {
				@Override
				public void handleEvent(ComponentEvent be) {
					Text l = ((Text)be.getComponent());
					l.addStyleName("label-time-select-sel");
					setMinutes(false, Integer.parseInt(be.getComponent().getData("m").toString()));
				}								
			});
			l1.addListener(Events.OnMouseOut, listenerLabelMouseOut);
			l1.addListener(Events.OnMouseUp, new Listener<ComponentEvent>() {
				@Override
				public void handleEvent(ComponentEvent be) {
					setMinutes(true, Integer.parseInt(be.getComponent().getData("m").toString()));
				}								
			});
			l1.sinkEvents(Event.MOUSEEVENTS);
			panelMinute.add(l1);
		}
		return panelMinute;
	}
	
	void setMinutes(boolean setValue, int minutes) {
		try {
			Minutes = minutes;
			
			int seconds = Hours * 3600 + Minutes * 60;
			setValue(Functions.getTimeToString(seconds));
			
			if(setValue) {
				origValue = seconds;

				popup.hide();
				popup = null;

				//fire time selected event
				if(handler != null) {
				  handler.timeChanged( seconds );
				}
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}
	
	/**
	 * Returns current time
	 * @return time in seconds
	 */
	public int getTime() {
	  return Hours * 3600 + Minutes * 60;
	}
}
