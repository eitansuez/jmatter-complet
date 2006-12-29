package com.u2d.app;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Window;
import nextapp.echo2.webcontainer.WebContainerServlet;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.u2d.view.echo2.Echo2ViewMechanism;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 28, 2006
 * Time: 1:07:53 PM
 */
public class Echo2EntryPoint extends WebContainerServlet
{
    public ApplicationInstance newApplicationInstance()
    {
       return new Echo2JMatterApp();
    }
   
   class Echo2JMatterApp extends ApplicationInstance
   {
      public Window init()
      {
         ServletContext servletContext = getServletContext();
         ApplicationContext context = 
               WebApplicationContextUtils.getWebApplicationContext(servletContext);
      
         AppSession session = (AppSession) context.getBean("app-session");
       
         session.launch();
         return ((Echo2ViewMechanism) session.getViewMechanism()).getAppFrame();
      }
   }
}
