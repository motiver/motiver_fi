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
/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.delect.motiver.server.FoodName;
import com.delect.motiver.server.PMF;
import com.delect.motiver.shared.Constants;
import com.google.appengine.repackaged.org.json.JSONWriter;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class SearchServlet extends RemoteServiceServlet {

  private static final long serialVersionUID = 5384098937371620397L;

  @SuppressWarnings("unchecked")
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    final int limit = 20;
    
    try {
      String query = request.getParameter("q");
      if(query == null) {
        query = "";
      }
    
      //strip special characters
      query = query.replace("(", "");
      query = query.replace(")", "");
      query = query.replace(",", "");
      query = query.toLowerCase();
      String[] arr = query.split(" ");

      PersistenceManager pm =  PMF.get().getPersistenceManager();
      
      //search
      Query q = pm.newQuery(FoodName.class);
      q.setFilter("locale == localeParam");
      q.declareParameters("java.lang.String localeParam");
      List<FoodName> names = (List<FoodName>) q.execute("fi_FI");

      ArrayList<FoodName> arrNames = new ArrayList<FoodName>();
      
      //check matches
      for(FoodName n : names) {

        String name = n.getName();
        name = name.replace("(", "");
        name = name.replace(")", "");
        name = name.replace(",", "");
        
        int count = 0;
        for(String s : arr) {
          //if word long enough
          if(s.length() >= Constants.LIMIT_MIN_QUERY_WORD) {
            //exact match
            if(name.toLowerCase().equals( s )) {
              count += 3;
            }
            //partial match
            else if(name.toLowerCase().contains( s )) {
              count++;
            }
          }
        }
        
        if(count > 0) {
          //if motiver's food -> add count
          if(n.getTrusted() == 100) {
            count += 2;
          }
          //if verified
          else if(n.getTrusted() == 1) {
            count++;
          }
        }
        
        //if found
        if(count > 0) {
          n.setCount(0, count);
          arrNames.add(n);
        }
      }

      //sort array based on count
      Collections.sort(arrNames);

      //convert to json
      JSONWriter writer = new JSONWriter(response.getWriter());
      writer.object();
      writer.key("foods");
      writer.array();
      
      int found = 0;
      for(int i=0; i < arrNames.size(); i++) {
        FoodName n = arrNames.get(i);
        
        if(n.getCountUse() > 0) {
          
          writer.object();
          
          //name
          writer.key("n");
          writer.value(n.getName());
          //energy
          writer.key("e");
          writer.value(n.getEnergy());
          //protein
          writer.key("p");
          writer.value(n.getProtein());
          //carb
          writer.key("c");
          writer.value(n.getCarb());
          //fet
          writer.key("f");
          writer.value(n.getFet());
          //portion
          writer.key("po");
          writer.value(n.getPortion());
          
          writer.endObject(); 
          
          found++;
        }
        
        //limit query
        if(found >= limit) {
          break;
        }
      }
      writer.endArray();
      writer.endObject();
      
    } catch (Exception e) {
      try {
        response.getWriter().write(e.getLocalizedMessage());
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
  }
}
