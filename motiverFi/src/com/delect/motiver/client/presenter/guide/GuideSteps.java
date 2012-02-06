package com.delect.motiver.client.presenter.guide;

import java.util.List;

import com.delect.motiver.client.event.ExerciseUpdatedEvent;
import com.delect.motiver.client.event.WorkoutCreatedEvent;
import com.delect.motiver.client.event.handler.ExerciseUpdatedEventHandler;
import com.delect.motiver.client.event.handler.WorkoutCreatedEventHandler;
import com.delect.motiver.client.presenter.guide.BeginnersGuidePresenter.BeginnersGuideDisplay;
import com.delect.motiver.client.presenter.guide.BeginnersGuidePresenter.PointDirection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.delect.motiver.client.lang.LangTutorial;

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

  private static LangTutorial Lang = GWT.create(LangTutorial.class);
  
  private static String[] textsMain = new String[] {
    Lang.Main1(),
    Lang.Main2(),
    Lang.Main3(),
    Lang.Main4(),
    Lang.Main5(),
    Lang.Main6()
  };
  
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
            Window.scrollTo(0, 0);
            display.showArrow("header-link-"+j, PointDirection.UP);
            display.showText(textsMain[j-1]); 
            
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
          display.showText(Lang.WorkoutCreate1());
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
          display.showText(Lang.WorkoutCreate2());
          display.showArrow("panel-note1", PointDirection.DOWN);
          return true;
        }
      };
      steps.add(step2);
      
      //user workouts
      GuideStep step3 = new GuideStep() {
        @Override
        public boolean isReady() {
          return History.getToken().contains("user/training");
        }
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate3());
          display.showArrow("panel-note2", PointDirection.DOWN);
          return true;
        }
        @Override
        public boolean skip() {
          return (DOM.getElementById("btn-add-workout") != null);
        }
      };
      steps.add(step3);
      
      //create new workout
      GuideStep step4 = new GuideStep() {
        @Override
        public boolean isReady() {
          return (DOM.getElementById("btn-add-workout") != null);
        }
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate4());
          display.showArrow("btn-add-workout", PointDirection.RIGHT);
          return true;
        }
        @Override
        public boolean skip() {
          return (DOM.getElementById("ni-btn-add") != null);
        }
      };
      steps.add(step4);
      
      //enter workout's name
      GuideStep step5 = new GuideStep() {
        boolean skip = false;
        HandlerRegistration ret = null;
        
        @Override
        public boolean isReady() {
          return (DOM.getElementById("ni-btn-add") != null);
        }
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate5());
          display.showArrow("ni-btn-add", PointDirection.UP);
          
          //listen for workout created event
          ret = eventBus.addHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {
            @Override
            public void onWorkoutCreated(WorkoutCreatedEvent event) {
              if(event.getWorkout() != null && event.getWorkout().getDate() == null) {
                skip = true;
                if(ret != null)
                  ret.removeHandler();
              }
            }     
          });
          return true;
        }
        @Override
        public boolean skip() {
          //if button clicked and workout element found
          return skip;
        }
      };
      steps.add(step5);
      
      //workout created
      GuideStep step6 = new GuideStep() {
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate6());
          return true;
        }
        @Override
        public boolean skip() {
          return false;
        }
      };
      steps.add(step6);
      
      //add exercise
      GuideStep step7 = new GuideStep() {
        boolean skip = false;
        private HandlerRegistration ret;
        
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate7());

          //listen for workout created event
          ret = eventBus.addHandler(ExerciseUpdatedEvent.TYPE, new ExerciseUpdatedEventHandler() {
            @Override
            public void onExerciseUpdated(ExerciseUpdatedEvent event) {
              if(event.getExercise() != null && event.getExercise().getName() != null) {
                skip = true;
                if(ret != null)
                  ret.removeHandler();
              }
            }     
          });
          return true;
        }
        @Override
        public boolean skip() {
          return skip;
        }
      };
      steps.add(step7);
      
      //edit exercise
      GuideStep step8 = new GuideStep() {        
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate8());
          return true;
        }
      };
      steps.add(step8);
      
      //move workout to date
      GuideStep step9 = new GuideStep() {        
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate9());
          display.showArrow("panel-note1-btn1", PointDirection.DOWN);
          return true;
        }
        @Override
        public boolean skip() {
          return (DOM.getElementById("btn-add-workout") != null);
        }
      };
      steps.add(step9);
      
      //select workout
      GuideStep step10 = new GuideStep() { 
        @Override
        public boolean isReady() {
          return (DOM.getElementById("btn-add-workout") != null);
        }       
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate10());
          return true;
        }
        @Override
        public boolean skip() {
          return (DOM.getElementById("btn-move-to-date") != null || DOM.getElementById("btn-quick-select") != null);
        }
      };
      steps.add(step10);
      
      //move workout
      GuideStep step11 = new GuideStep() {
        private boolean skip = false;
        private HandlerRegistration ret;
        @Override
        public boolean isReady() {
          return (DOM.getElementById("btn-move-to-date") != null || DOM.getElementById("btn-quick-select") != null);
        }
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate11());
          display.showArrow( (DOM.getElementById("btn-move-to-date") != null)? "btn-move-to-date" : "btn-quick-select", PointDirection.LEFT);
          
          //listen for workout created event
          ret = eventBus.addHandler(WorkoutCreatedEvent.TYPE, new WorkoutCreatedEventHandler() {

            @Override
            public void onWorkoutCreated(WorkoutCreatedEvent event) {
              if(event.getWorkout() != null && event.getWorkout().getDate() != null) {
                skip = true;
                if(ret != null)
                  ret.removeHandler();
              }
            }     
          });
          return true;
        }
        @Override
        public boolean skip() {
          return skip;
        }
      };
      steps.add(step11);
      
      //DONE!
      GuideStep step12 = new GuideStep() { 
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate12());
          return true;
        }
        @Override
        public boolean skip() {
          return (DOM.getElementById("btn-move-to-date") != null || DOM.getElementById("btn-quick-select") != null);
        }
      };
      steps.add(step12);
      
      //DONE!
      GuideStep step13 = new GuideStep() { 
        @Override
        public boolean init(SimpleEventBus eventBus, BeginnersGuideDisplay display) {
          display.showText(Lang.WorkoutCreate13());
          return true;
        }
        @Override
        public boolean skip() {
          return (DOM.getElementById("btn-move-to-date") != null || DOM.getElementById("btn-quick-select") != null);
        }
      };
      steps.add(step13);
    }
  }
}
