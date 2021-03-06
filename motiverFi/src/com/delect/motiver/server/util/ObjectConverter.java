package com.delect.motiver.server.util;

import java.util.ArrayList;
import java.util.List;

import com.delect.motiver.server.FoodInMeal;
import com.delect.motiver.server.FoodInMealTime;
import com.delect.motiver.server.Meal;
import com.delect.motiver.server.MealInTime;

public class ObjectConverter {

  public static MealInTime getMealInTime(Meal meal) {
    
    MealInTime model = new MealInTime();
    model.setId(meal.getId());
    model.setName(meal.getName());
    
    
    //foods
    if(model.getFoods() != null) {
      List<FoodInMealTime> foods = new ArrayList<FoodInMealTime>();
      for(FoodInMeal m : meal.getFoods()) {
        foods.add(getFoodInMealTime(m));
      }
      model.setFoods(foods);
    }
    
    return model;
  }

  public static FoodInMealTime getFoodInMealTime(FoodInMeal food) {
    
    FoodInMealTime model = new FoodInMealTime();
    model.setId(food.getId());
    model.setAmount(food.getAmount());
    model.setNameId(food.getNameId());
    
    return model;
    
  }
}
