package com.delect.motiver.client.presenter.guide;

import java.util.ArrayList;

import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.presenter.guide.GuideSteps.GuideStep;
import com.delect.motiver.client.presenter.guide.GuideSteps.Guides;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Timer;

public class BeginnersGuidePresenter extends Presenter {
  
  /**
  * Abstract class for view to extend
  */
  public abstract static class BeginnersGuideDisplay extends Display {
    public abstract void showTitle(String title);
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

  protected static final int LEVEL_WORKOUT_CREATE = 8;
  
  private BeginnersGuideDisplay display = null;
  private Timer t;
  
  private int level = 0;
  private int levelPrev = -1;

  private ArrayList<GuideStep> steps;

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
    
    steps = new ArrayList<GuideStep>();
    GuideSteps.addSteps(Guides.MAIN, steps);
    GuideSteps.addSteps(Guides.WORKOUT_CREATE, steps);
    GuideSteps.addSteps(Guides.NUTRITION, steps);

    threadRun();
    highlight();
    
    //start timer
    t = new Timer() {
      @Override
      public void run() {
        threadRun();
      }
    };
    t.scheduleRepeating(500);
  }

  protected void threadRun() {
    
    try {
      display.setButtonEnabled(Button.PREVIOUS, (level != 0));

      GuideStep step = steps.get(level);
      
      //check if next step is ready
      if(steps.size() > level+1) {
        boolean isReady = steps.get(level+1).isReady();
        display.setButtonEnabled(Button.NEXT, isReady);
        
        //check if current step should be skipped
        if(isReady && step.skip()) {
          level++;
          
          highlight();
          return;
        }
      }
      else
        display.setButtonEnabled(Button.NEXT, false);
      
      if(level != levelPrev) {
        
        display.showArrow(null, PointDirection.UP);
        step.init(eventBus, display);
        
        levelPrev = level;
      }
      
    } catch (Exception e) {
    }
  }

  @Override
  public void onStop() {
    if(t != null)
      t.cancel();
  }

}
