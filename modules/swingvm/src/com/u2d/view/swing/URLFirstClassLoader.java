package com.u2d.view.swing;

import java.net.URLClassLoader;
import java.net.URL;

/**
 * This class exists to revise the behavior of the urlclassloader,
 * to search the url first for resources, and then falling back to the
 * parent class loader.
 *
 * Date: Feb 12, 2008
 * @author Eitan Suez
 */
public class URLFirstClassLoader extends URLClassLoader
{
   public URLFirstClassLoader(URL[] urls, ClassLoader parent) { super(urls, parent); }

   public URL getResource(String name)
   {
      URL url = findResource(name);
      if (url == null)
      {
         ClassLoader parent = getParent();
         url = parent.getResource(name);
      }
      return url;
   }
   
// shouldn't need this but saving it for now..
//   public Class<?> loadClass(String name) throws ClassNotFoundException
//   {
//      Class loadedClass = findLoadedClass(name);
//      if (loadedClass == null) {
//         try {
//             loadedClass = findClass(name);
//         } catch (ClassNotFoundException e) {
//             // Swallow exception - does not exist locally
//         }
//
//         if (loadedClass == null) {
//             loadedClass = super.loadClass(name);
//         }
//     }
//      return loadedClass;
//   }

}
