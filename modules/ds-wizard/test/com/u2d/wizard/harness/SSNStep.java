package com.u2d.wizard.harness;

import com.u2d.wizard.details.BasicStep;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:49:24 PM
 */
public class SSNStep extends BasicStep
{
   private String _ssn;

   public JComponent getView()
   {
      System.out.println("what's your ssn?");
      _ssn = "234234234";
      System.out.println("i'll fill in for you it's "+_ssn);
      return null;
   }

   public String title() { return "ssn step"; }
   public String description() { return "ssn step"; }
}
