/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
package com.delect.motiver.server;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.delect.motiver.server.jdo.ExerciseSearchIndex;
import com.delect.motiver.server.jdo.FoodSearchIndex;
import com.delect.motiver.shared.Constants;


public class CommonTasksImpl extends RemoteServiceServlet {

	private static final Logger log = Logger.getLogger(CommonTasksImpl.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -5683430613554407776L; 
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {

		//execute common tasks
		PersistenceManager pm =  PMF.get().getPersistenceManager();

    try {
      long count1;
      long count2;
				
      //clear old exercise search indexes
      Date date = new Date();
      date.setDate(date.getDate() - Constants.DAYS_SEARCH_INDEXES_EXPIRE);
      Query q1 = pm.newQuery(ExerciseSearchIndex.class);
      q1.setFilter("date < dateParam");
      q1.declareParameters("java.util.Date dateParam");
      q1.setRange(0, 50);
      List<ExerciseSearchIndex> arrQuery = (List<ExerciseSearchIndex>) q1.execute(date);
      count1 = arrQuery.size();
      pm.deletePersistentAll(arrQuery);

      //clear old food search indexes
      date = new Date();
      date.setDate(date.getDate() - Constants.DAYS_SEARCH_INDEXES_EXPIRE);
      q1 = pm.newQuery(FoodSearchIndex.class);
      q1.setFilter("date < dateParam");
      q1.declareParameters("java.util.Date dateParam");
      q1.setRange(0, 50);
      List<FoodSearchIndex> arrQuery2 = (List<FoodSearchIndex>) q1.execute(date);
      count2 = arrQuery2.size();
      pm.deletePersistentAll(arrQuery2);

      //send email with info
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("antti@motiver.fi", "Antti Havanko"));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress("antti@motiver.fi", "Antti Havanko"));
      msg.setSubject("Motiver.fi: Common tasks executed");
      msg.setText("Common tasks runned as cron jobs." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Exercise search indexes cleared: " + count1 + System.getProperty("line.separator") + "Food search indexes cleared: " + count2);
      Transport.send(msg);
				
    } catch (Exception e) {
      log.severe(e.getMessage());
    }
    finally {
      if(pm != null && !pm.isClosed()) {
        pm.close();
      }
    }
	}
	
}
