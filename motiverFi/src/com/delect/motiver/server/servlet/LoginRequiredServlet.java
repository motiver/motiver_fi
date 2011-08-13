/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author Antti
 *
 */
public class LoginRequiredServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 4306008508711734894L;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser(); // or req.getUserPrincipal()


      //everything ok -> send to previous url
      if (user != null) {
          resp.sendRedirect(req.getParameter("continue"));
      }
      //show login choices
      else {
        resp.setContentType("text/html");

        try {
          
          Set<String> attributes = new HashSet();
          String url_google = userService.createLoginURL(req.getParameter("continue"), null, "google.com/accounts/o8/id", attributes);
          String url_yahoo = userService.createLoginURL(req.getParameter("continue"), null, "yahoo.com", attributes);
          String url_myspace = userService.createLoginURL(req.getParameter("continue"), null, "myspace.com", attributes);
          String url_aol = userService.createLoginURL(req.getParameter("continue"), null, "aol.com", attributes);
          String url_myopenid = userService.createLoginURL(req.getParameter("continue"), null, "myopenid.com", attributes);
  
          //redirect urls to attributes
          req.setAttribute("url_google", url_google);
          req.setAttribute("url_yahoo", url_yahoo);
          req.setAttribute("url_myspace", url_myspace);
          req.setAttribute("url_aol", url_aol);
          req.setAttribute("url_myopenid", url_myopenid);
          
          RequestDispatcher rd = req.getRequestDispatcher("/auth.jsp");
          rd.forward(req, resp);
          
        } catch (ServletException e) {
          e.printStackTrace();
        }
    
      }
  }
}