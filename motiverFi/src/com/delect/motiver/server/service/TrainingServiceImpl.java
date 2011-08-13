/**
 * 
 */
package com.delect.motiver.server.service;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.delect.motiver.client.service.TrainingService;
import com.delect.motiver.shared.WorkoutModel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Antti
 *
 */
public class TrainingServiceImpl extends RemoteServiceServlet implements TrainingService {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1930822872413338185L;

  /**
   * Logger for this class
   */
  private static final Logger logger = Logger.getLogger(TrainingServiceImpl.class.getName()); 
}
