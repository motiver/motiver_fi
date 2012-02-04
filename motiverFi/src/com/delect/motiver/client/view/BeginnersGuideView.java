package com.delect.motiver.client.view;

import com.delect.motiver.client.presenter.BeginnersGuidePresenter;
import com.delect.motiver.client.presenter.BeginnersGuidePresenter.BeginnersGuideHandler;
import com.delect.motiver.client.presenter.BeginnersGuidePresenter.Button;
import com.delect.motiver.client.presenter.BeginnersGuidePresenter.PointDirection;
import com.delect.motiver.client.res.MyResources;
import com.delect.motiver.client.view.widget.MyButton;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class BeginnersGuideView extends BeginnersGuidePresenter.BeginnersGuideDisplay {

  Text text = new Text();
  Popup popup = new Popup();
  private BeginnersGuideHandler handler;

  MyButton btnPrev = new MyButton();
  MyButton btnNext = new MyButton();
  
  public BeginnersGuideView() {

    TableLayout layout = new TableLayout(2);
    layout.setWidth("100%");
    layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
    layout.setCellHorizontalAlign(HorizontalAlignment.LEFT);
    layout.setCellPadding(5);
    this.setLayout(layout);
    
    this.add(text);
    
    //previous button
    btnPrev.setText("_prev_");
    btnPrev.addListener(Events.OnClick, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.onButtonClicked(Button.PREVIOUS);
      }
    });
    this.add(btnPrev);
    
    //next button
    btnNext.setText("_next_");
    btnNext.addListener(Events.OnClick, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        handler.onButtonClicked(Button.NEXT);
      }
    });
    this.add(btnNext);
    
    popup.setAutoHide(false);
    popup.setSize(16, 16);
  }
  @Override
  public Widget asWidget() {
    return this;
  }
  
  @Override
  public void onStop() {
    if(popup != null && popup.isVisible()) {
      popup.hide();
    }
  }

  @Override
  public void showText(String t) {
    text.setText(t);
  }
  @Override
  public void setHandler(BeginnersGuideHandler handler) {
    this.handler = handler;
  }
  @Override
  public void setButtonEnabled(Button button, boolean enabled) {
    switch(button) {
      case NEXT:
        btnNext.setEnabled(enabled);
        break;
      case PREVIOUS:
        btnPrev.setEnabled(enabled);
        break;
    }
  }
  @Override
  public void showArrow(String id, PointDirection direction) {
    if(popup != null && popup.isVisible()) {
      popup.hide();
    }
    popup.removeAll();
    
    if(id != null) {
      ImageResource image;
      switch(direction) {
        case LEFT:
          image = MyResources.INSTANCE.getArrowRedLeft();
          break;
        case RIGHT:
          image = MyResources.INSTANCE.getArrowRedRight();
          break;
        default:
          image = MyResources.INSTANCE.getArrowRedUp();
          break;
      }
      
      popup.add(new Image(image));
      Element el = DOM.getElementById(id);
      popup.show(el, "", new int[] {el.getClientWidth()/2, el.getClientHeight()+5} );
      popup.setZIndex(2000);
      popup.layout();
    }
  }

}
