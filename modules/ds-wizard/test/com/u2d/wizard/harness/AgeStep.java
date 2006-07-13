package com.u2d.wizard.harness;

import com.u2d.wizard.details.BasicStep;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: eitan
 * Date: Aug 24, 2005
 * Time: 5:48:10 PM
 */
public class AgeStep extends BasicStep
{
   int _age = 0;

   public AgeStep() {}

   public JComponent getView()
   {
      System.out.println("what's you age?");
      _age = 19;
      System.out.println("i'll fill in for you.."+_age);
      return null;
   }

   public int getAge() { return _age; }

   public String title() { return "age step"; }
   public String description() { return "age step"; }

}
