package com.delect.motiver.server.dao;

import java.util.List;

import com.delect.motiver.server.jdo.nutrition.FoodName;
import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.FoodModel;
import com.delect.motiver.shared.FoodNameModel;
import com.delect.motiver.shared.MealModel;
import com.delect.motiver.shared.TimeModel;

public interface NutritionManager {

  
  /**
   * Adds single name
   * @param pm
   * @param model
   * @param uid
   * @param locale
   * @return added name
   * @throws Exception 
   */
  public abstract FoodNameModel addFoodNameModel(FoodNameModel model, String uid, String locale) throws Exception;

  /**
   * Returns exercise name
   * @param pm
   * @param nameId
   * @throws Exception 
   */
  public abstract FoodNameModel getFoodNameModel(long nameId) throws Exception;

  /**
   * Returns all food name
   * @param pm
   * @param locale
   * @throws Exception
   * @return list 
   */
  public abstract List<FoodName> getFoodNames() throws Exception;

  /**
   * Returns single time
   * @param pm
   * @param id
   * @return
   */
  public abstract TimeModel getTimeModel(Long timeId, String userUid) throws Exception;

  /**
   * Returns single meal
   * @param pm
   * @param id
   * @return
   */
  public abstract MealModel getMealModel(Long mealId, String userUid) throws Exception;

  /**
   * Adds single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public abstract FoodModel addFoodModel(FoodModel model, String uid, String locale) throws Exception;

  /**
   * Removes single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public abstract boolean removeFoodModel(FoodModel model, String uid) throws Exception;

  /**
   * Removes single food
   * @param pm
   * @param food
   * @param uID
   * @return
   */
  public abstract FoodModel updateFoodModel(FoodModel model, String uid) throws Exception;
  
  /**
   * Removes single time
   * @param pm
   * @param time
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public abstract Boolean removeTimeModel(Long timeId, String uid) throws Exception;
  
  /**
   * Removes single meal
   * @param pm
   * @param meal
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public abstract Boolean removeMealModel(MealModel model, String uid) throws Exception;
  
  /**
   * Removes single meal
   * @param pm
   * @param meal
   * @param uid
   * @return true if remove was successfully
   * @throws ConnectionException 
   */
  public abstract MealModel addMealModel(MealModel model, String uid) throws Exception;

  /**
   * Returns how often user have use single food name
   * @param uid
   * @param id
   * @return
   */
  public abstract int getFoodNameCount(String uid, Long id);
  
}
