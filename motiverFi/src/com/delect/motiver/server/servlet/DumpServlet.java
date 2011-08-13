/**
 * 
 */
package com.delect.motiver.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Antti
 *
 */
public class DumpServlet extends RemoteServiceServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(404);
  }
}
