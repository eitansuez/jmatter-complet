package com.u2d.app;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.ApplicationContext;
import org.wings.session.SessionManager;
import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Oct 3, 2006
 * Time: 3:29:57 PM
 */
public class WingSEntryPoint
{
   public WingSEntryPoint()
   {
      ServletContext servletContext = SessionManager.getSession().getServletContext();
      ApplicationContext context = 
            WebApplicationContextUtils.getWebApplicationContext(servletContext);
      
      AppSession session = (AppSession) context.getBean("app-session");
      session.launch();
   }

}
