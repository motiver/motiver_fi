/**
 * 
 */
package com.delect.motiver.client.service;

import java.util.Date;
import java.util.List;

import com.delect.motiver.shared.ConnectionException;
import com.delect.motiver.shared.GuideValueModel;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Antti
 *
 */
public interface NutritionService extends RemoteService {

  
  /**
   * Return guide values.
   *
   * @param uid the uid
   * @param index the index
   * @param date : if null -> all values are returned
   * @return values
   * @throws ConnectionException the connection exception
   */
  public List<GuideValueModel> getGuideValues(String uid, int index, Date date) throws ConnectionException;
  
}
