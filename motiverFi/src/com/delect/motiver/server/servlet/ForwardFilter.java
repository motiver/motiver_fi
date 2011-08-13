/**
 * 
 */
package com.delect.motiver.server.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Antti
 *
 */
public class ForwardFilter implements Filter 
{ 
        public static final String SERVLET_PATH = ForwardFilter.class.getName() + ".servletPath"; 
        private String target; 

        public void destroy() 
        { 
        } 

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
        {           
          String path = ((HttpServletRequest)request).getServletPath();
          
          if(!path.startsWith("/_ah/") && !path.startsWith("/motiver/") && !path.startsWith("/app/"))
                request.setAttribute(SERVLET_PATH, ((HttpServletRequest)request).getServletPath()); 
                request.getRequestDispatcher(target).forward(request, response); 
        } 

        public void init(FilterConfig config) throws ServletException 
        { 
                target = config.getInitParameter("target"); 
        } 

} 
