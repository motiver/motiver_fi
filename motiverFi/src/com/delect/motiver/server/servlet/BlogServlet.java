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
