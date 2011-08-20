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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class BlogServlet extends RemoteServiceServlet {

  private static final long serialVersionUID = 5384654650397L;

  public static final String SERVLET_PATH = ForwardFilter.class.getName() + ".servletPath"; 
  private String target; 
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    try {
//      request.setAttribute(SERVLET_PATH, ((HttpServletRequest)request).getServletPath()); 
      request.getRequestDispatcher(target).forward(request, response); 
  
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void init(ServletConfig config) throws ServletException 
  {
    target = config.getInitParameter("target"); 
  } 
}
