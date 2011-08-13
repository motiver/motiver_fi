/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.res;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface MyResources extends ClientBundle {

  public static final MyResources INSTANCE =  GWT.create(MyResources.class);

  @Source("style.css")
  @CssResource.NotStrict
  CssResource css();
  
  @Source("style_mobile.css")
  @CssResource.NotStrict
  CssResource cssMobile();
  
  //done/undone
  @Source("checked.png")
  ImageResource done();
  @Source("icon_btn_calendar.png")
  ImageResource iconBtnCalendar();
  @Source("icon_btn_drag.png")
  ImageResource iconBtnDrag();
		
  @Source("icon_btn_rename.png")
  ImageResource iconBtnRename();
  @Source("icon_btn_video.png")
  ImageResource iconBtnVideo();

  @Source("icon_cardio.png")
  ImageResource iconCardio();
  @Source("icon_clock.png")
  ImageResource iconClock();
		
  @Source("icon_guide_values.png")
  ImageResource iconGuideValues();

  @Source("icon_last_weights.png")
  ImageResource iconLastWeights();
		
  @Source("icon_meal.png")
  ImageResource iconMeal();
  @Source("icon_measurement.png")
  ImageResource iconMeasurement();
  @Source("icon_nutrition_day.png")
  ImageResource iconNutritionDay();
  //buttons icons (16x16)
  @Source("icon_remove.png")
  ImageResource iconRemove();
  @Source("icon_run.png")
  ImageResource iconRun();
  //icons (28x28)
  @Source("icon_workout.png")
  ImageResource iconWorkout();
		
  //logo
  @Source("logo_big.png")
  ImageResource logoBig();
  @Source("logo_header.png")
  ImageResource logoHeader();
  @Source("logo_header_print.png")
  ImageResource logoHeaderPrint();
  @Source("checked2.png")
  ImageResource notDone();
  @Source("star2.png")
  ImageResource starOff();
  //rating star
  @Source("star.png")
  ImageResource starOn();
  @Source("video_preview.png")
  ImageResource videoPreview();

  @Source("face.png")
  ImageResource getEmptyProfilePic();

}
