package com.u2d.wizard.harness;

import com.u2d.wizard.details.BasicStep;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:48:05 PM
 */
public class NameStep extends BasicStep
{
   private String _name;

   public JComponent getView()
   {
      System.out.println("what's your name?");
      _name = "eitan";
      System.out.println("i'll fill in for you.."+_name);
      return null;
   }

   public String title() { return "name step"; }
   public String description() { return "name step"; }

}
