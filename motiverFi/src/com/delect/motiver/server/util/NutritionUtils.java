package com.delect.motiver.server.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.delect.motiver.server.jdo.nutrition.Food;
import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.server.jdo.nutrition.Meal;
import com.delect.motiver.server.jdo.nutrition.MealJDO;
import com.delect.motiver.server.jdo.nutrition.Time;
import com.delect.motiver.server.jdo.nutrition.TimeJDO;
import com.delect.motiver.server.manager.NutritionManager;
import com.delect.motiver.shared.NutritionDayModel;

public class NutritionUtils {

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(NutritionUtils.class.getName());


  /**
   * Calculates energy from times (searches meals and foods)
   * @param pm 
   * @param times
   * @return
   */
  public static NutritionDayModel calculateEnergyFromTimes(List<TimeJDO> times) {

    if(logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Calculating total energy: ("+times.size()+")");
    }

    double energy = 0;
    double protein = 0; 
    double carbs = 0;
    double fet = 0;

    try {
      
      //each time
      for(TimeJDO tClient : times) {
        
        //each meal
        for(MealJDO m : tClient.getMealsNew()) {
          
            if(m.getFoods() != null) {
              for(Food food : m.getFoods()) {

                final double amount = food.getAmount();
                final FoodName name = food.getName();
                if(name != null) {
                  energy += (name.getEnergy() / 100) * amount;
                  protein += (name.getProtein() / 100) * amount;
                  carbs += (name.getCarb() / 100) * amount;
                  fet += (name.getFet() / 100) * amount;
                }
              }
            }
          
        }

        if(tClient.getFoods() != null) {
          for(Food food : tClient.getFoods()) {

            final double amount = food.getAmount();
            final FoodName name = food.getName();
            if(name != null) {
              energy += (name.getEnergy() / 100) * amount;
              protein += (name.getProtein() / 100) * amount;
              carbs += (name.getCarb() / 100) * amount;
              fet += (name.getFet() / 100) * amount;
            }
          }
        }
      }
      
    } catch (Exception e1) {
      logger.log(Level.SEVERE, "calculateEnergyFromTimes", e1);
    }

    return new NutritionDayModel(energy, protein, carbs, fet);
  }
  
}
