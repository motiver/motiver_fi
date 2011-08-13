/**
 * 
 */
package com.delect.motiver.client.service;

import java.util.Date;
import java.util.List;

import com.delect.motiver.shared.GuideValueModel;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Antti
 *
 */
public interface NutritionServiceAsync {

  /**
   * Return guide values.
   *
   * @param uid the uid
   * @param index the index
   * @param date : if null -> all values are returned
   * @param callback the callback
   * @return values
   */
  public Request getGuideValues(String uid, int index, Date date, AsyncCallback<List<GuideValueModel>> callback);
  
  
}
