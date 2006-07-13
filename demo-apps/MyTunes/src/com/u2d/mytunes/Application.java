package com.u2d.mytunes;

import com.u2d.xml.CodesList;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: May 2, 2006
 * Time: 1:49:40 PM
 */
public class Application extends com.u2d.app.Application
{
   public void initObjects()
   {
      super.initObjects();
      CodesList.populateItemsFor(Genre.class,  "genres.xml");
   }

   public static void main(String[] args)
   {
      new Application().launch();
   }
}
