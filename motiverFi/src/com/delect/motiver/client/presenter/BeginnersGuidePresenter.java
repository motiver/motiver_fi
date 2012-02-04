package com.delect.motiver.client.presenter;

import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Timer;

public class BeginnersGuidePresenter extends Presenter {
  
  /**
  * Abstract class for view to extend
  */
  public abstract static class BeginnersGuideDisplay extends Display {
    public abstract void showText(String text);
    public abstract void setHandler(BeginnersGuideHandler handler);
    public abstract void setButtonEnabled(Button button, boolean enabled);
    public abstract void showArrow(String id, PointDirection direction);
  }
  
  public interface BeginnersGuideHandler {
    public void onButtonClicked(Button btn);
  }
  
  public enum Button {
    PREVIOUS,
    NEXT,
    CLOSE
  }
  
  public enum PointDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
  }
  
  private BeginnersGuideDisplay display = null;
  private Timer t;
  
  private int level = 1;
  private int levelPrev = 1;

  public BeginnersGuidePresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, BeginnersGuideDisplay display) {
    super(rpcService, eventBus);
    this.display = display;
  }

  @Override
  public Display getView() {
    return display;
  }

  @Override
  public void onBind() {
    display.showText("Testi teksti");
    display.setHandler(new BeginnersGuideHandler() {
      @Override
      public void onButtonClicked(Button btn) {
        switch(btn) {
          case NEXT:
            level++;
            break;
          case PREVIOUS:
            level--;
            break;
          case CLOSE:
            stop();
            break;
        }
        
        if(level < 1) {
          level = 1;
        }
      }
    });
  }

  @Override
  public void onRun() {
    
    //start timer
    t = new Timer() {
      @Override
      public void run() {
        if(level <= texts.length) {
          display.showText(texts[level-1]); 
        }
        
        display.setButtonEnabled(Button.PREVIOUS, (level != 1));
        
        if(level != levelPrev) {
          display.showArrow(null, PointDirection.UP);
          //introduce header links
          if(level >= 2 && level <= 7) {
            display.showArrow("header-link-"+(level-1), PointDirection.UP);
          }
          
          levelPrev = level;
        }
      }
    };
    t.scheduleRepeating(2000);
  }

  @Override
  public void onStop() {
    if(t != null)
      t.cancel();
  }
  
  private String[] texts = new String[] {
      "Tervetuloa",
      "Pääsivulla näet aktiviteetti historia",
      "Treeni-osiossa voi merkitä treenisi",
      "Ravinto-osiossa voit laskea kalorit",
      "Aerobinen kuvaus",
      "Tilastot kuvaus",
      "Profiili kuvaus",
      "Mene treenisivulle"
  };

}
