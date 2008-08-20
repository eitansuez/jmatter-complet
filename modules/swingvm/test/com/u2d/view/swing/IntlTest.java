package com.u2d.view.swing;

import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Aug 20, 2008
 * Time: 3:17:44 PM
 */
public class IntlTest extends JPanel
{
   public IntlTest() throws IOException
   {
      intlTest3();
   }
   /*
   1. works
    */
   private void intlTest1()
   {
      String hebtext = "שלום";
      JLabel lbl = new JLabel(hebtext);
      add(lbl);
   }
   /*
   2. doesn't work.  the problem is ResourceBundle.getBundle requires the encoding of the file to
    be iso-8859-1.  i don't get why this is the default.  doesn't make any sense to me.
    */
   private void intlTest2()
   {
      Locale.setDefault(new Locale("he", "IL"));
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      PropertyResourceBundle bundle = (PropertyResourceBundle)
            ResourceBundle.getBundle("com/u2d/view/swing/locale", Locale.getDefault(), loader);
      String hebtext = bundle.getString("hello");
      JLabel lbl = new JLabel(hebtext);
      add(lbl);
   }

   /*
   this works.  but i have to replicate the logic for locating a resource.  this sucks.  is there a better way?
    */
   private void intlTest3() throws IOException
   {
      Locale.setDefault(new Locale("he", "IL"));
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      PropertyResourceBundle bundle = new PropertyResourceBundle(readerFor(loader, "com/u2d/view/swing/locale"));
      
      String hebtext = bundle.getString("hello");
      JLabel lbl = new JLabel(hebtext);
      add(lbl);
   }
   private Reader readerFor(ClassLoader loader, String name) throws IOException
   {
      Locale locale = Locale.getDefault();
      List<String> fallbackList = new ArrayList<String>();
      fallbackList.add(name + "_" + locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant() + ".properties");
      fallbackList.add(name + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties");
      fallbackList.add(name + "_" + locale.getLanguage() + ".properties");
      fallbackList.add(name + ".properties");

      for (String trypath : fallbackList)
      {
         InputStream inputstream = loader.getResourceAsStream(trypath);
         if (inputstream != null)
         {
            return new InputStreamReader(inputstream);
         }
      }
      throw new IOException("Cannot find resource for "+name);
   }


   public static void main(String[] args) throws IOException
   {
      JFrame f = new JFrame("Int'l Test");
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setContentPane(new IntlTest());
      f.setLocation(200,200);
      f.pack();
      f.setVisible(true);
   }
}
