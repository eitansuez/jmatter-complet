package com.u2d.app;

import nextapp.echo.app.ApplicationInstance;
import nextapp.echo.app.Window;
import nextapp.echo.webcontainer.WebContainerServlet;
import javax.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.u2d.view.echo2.Echo2ViewMechanism;
import com.u2d.model.ComplexType;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Dec 28, 2006
 * Time: 1:07:53 PM
 */
public class EchoEntryPoint extends WebContainerServlet
{
    public ApplicationInstance newApplicationInstance()
    {
       return new EchoJMatterApp();
    }
   
   class EchoJMatterApp extends ApplicationInstance
   {
      public Window init()
      {
         ServletContext servletContext = getServletContext();

         Logger.getLogger("org.springframework").setLevel(Level.WARNING);
         ComplexType.reset();

         ApplicationContext context =
               WebApplicationContextUtils.getWebApplicationContext(servletContext);

         Application app = (Application) context.getBean("application");
         app.postInitialize();

         Echo2ViewMechanism vmech = (Echo2ViewMechanism) context.getBean("view-mechanism");
         vmech.launch();

         AppSession session = (AppSession) context.getBean("app-session");
         session.begin();

         return vmech.getAppFrame();
      }
   }
}
