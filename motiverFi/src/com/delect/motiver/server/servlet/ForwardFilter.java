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
