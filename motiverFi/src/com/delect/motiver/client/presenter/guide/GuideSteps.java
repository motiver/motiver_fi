package com.delect.motiver.client.presenter.guide;

import java.util.List;

import com.delect.motiver.client.presenter.guide.BeginnersGuidePresenter.BeginnersGuideDisplay;
import com.delect.motiver.client.presenter.guide.BeginnersGuidePresenter.PointDirection;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;

public abstract class GuideSteps {

  public abstract static class GuideStep {
    /**
     * Checks if everything is ready to do (=init) this step
     * @return true if ready
     */
    public boolean isReady() {
      return true;
    }
    /**
     * Called when step is active
     * @param eventBus
     * @return false if step should be skipped
     */
    public abstract boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display);
    /**
     * Called on constants basis to check if this step should be skipped
     * @return true if ready
     */
    public boolean skip() {
      return false;
    }
  }
  
  public enum Guides {
    MAIN,
    WORKOUT_CREATE
  }
  
  /**
   * Adds steps to list
   * @param steps
   */
  public static void addSteps(Guides target, List<GuideStep> steps) {
    
    //main
    if(target == Guides.MAIN) {
      //short description of main sections
      for(int i=1; i<=6; i++) {
        final int j = i;
        GuideStep step = new GuideStep() {
          @Override
          public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
            display.showArrow("header-link-"+j, PointDirection.UP);
            display.showText(texts[j-1]); 
            
            return true;
          }
        };
        steps.add(step);
      }
    }
    
    //creating workouts
    else if(target == Guides.WORKOUT_CREATE) {
      //go to workout page
      GuideStep step = new GuideStep() {
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showArrow("header-link-2", PointDirection.UP);
          display.showText("Nyt yritä luoda treeni. Mene treeniosioon");
          return true;
        }
        @Override
        public boolean skip() {
          return History.getToken().contains("user/training");
        }
      };
      steps.add(step);
      
      //calendar view
      GuideStep step2 = new GuideStep() {          
        @Override
        public boolean isReady() {
          return History.getToken().contains("user/training");
        }
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText("Kalenteri näkymän kuvaus");
          return true;
        }
      };
      steps.add(step2);
    }
  }
  

  
  private static String[] texts = new String[] {
      "Pääsivulla näet aktiviteetti historia",
      "Treeni-osiossa voi merkitä treenisi",
      "Ravinto-osiossa voit laskea kalorit",
      "Aerobinen kuvaus",
      "Tilastot kuvaus",
      "Profiili kuvaus",
  };
  
  private static String[] textsTraining = new String[] {
      "Nyt yritä luoda treeni. Mene treeniosioon",
      "Päivänäkymässä näet tämä päivän treenin",
      "",
      "",
      "",
      "",
  };
  
  private static String[] idsTraining = new String[] {
      "header-link-2",
      null,
      null,
      null
  };
}
